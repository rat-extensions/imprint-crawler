package impressumscrawler.extraction_component.FieldExtraction;

import data.kammern.Kammern;
import data.names.Names;
import impressumscrawler.datatypes.*;
import impressumscrawler.extraction_component.preprocessing.Preprocessor;
import impressumscrawler.util.Util;
import javafx.util.Pair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FieldExtractor {
    /**
     *  This class takes a jsoup.document and parses for the different possible fields.
     */

    private Document current_document;

    public FieldExtractor(Document doc) {
        current_document = doc;
    }

    public void setCurrent_document(Document current_document) {
        this.current_document = current_document;
    }

    public Document getCurrent_document() {
        return current_document;
    }

    /*
        Fields to extract:
        - geschä
     */

    public Elements preprocess() {
        Preprocessor preprocessor = new Preprocessor(current_document);
        Elements elements = preprocessor.text_blocks();
        return elements;
    }

    /**
     * Parses the document for a link to an electronic contactformular
     * @return an ArrayList of Strings. Each String resembling a link to a contact formular
     */
    public ArrayList<String> findKontaktformular() {
        ArrayList<String> kontakt_list = new ArrayList<>();
        Elements elements = current_document.getAllElements();
        Elements kontaktformular_links = elements.select("a[href]:containsOwn(kontaktformular)");
        ArrayList<ContactObject> contactObjects = new ArrayList<>();
        for (Element e : kontaktformular_links) {
            ContactObject contactObject = new ContactObject();
            // System.out.println(e.attr("href"));
            String kontakt_link = e.attr("href");
            contactObject.setElement(e);
            contactObject.setElements(elements);
            contactObject.setContact_info(kontakt_link);
            contactObjects.add(contactObject);
            kontakt_list.add(kontakt_link);
        }
        return kontakt_list;
    }

    /**
     * Turns the Document into a String and Matches a pattern for an E-Mail adress
     * @return an ArrayList of Strings. Each String resembles an E-mail
     */
    public ArrayList<String> find_email_contact() {
        Pattern email_pattern = Pattern.compile("[a-z0-9+._-]+@[a-z0-9._-]+\\.[a-z0-9_-]+", Pattern.CASE_INSENSITIVE);
        String doc_as_string = current_document.toString();
        Matcher matcher = email_pattern.matcher(doc_as_string);
        HashSet<String> mails = new HashSet<String>();
        while (matcher.find()) {
            mails.add(matcher.group());
        }
        ArrayList<String> mail_list = new ArrayList<String>(mails);
        return new ArrayList<String>(mails);
    }

    /**
     * Parses the document for Indicators and matches a pattern for numeric contacts
     * @return A list of NumericContactObjects
     */
    public ArrayList<NumericContactObject> find_numeric_contact() {
        ArrayList<NumericContactObject> numericContactObjects = new ArrayList<NumericContactObject>();
        ArrayList<Pair<String, String>> matches = new ArrayList<Pair<String, String>>();
        ArrayList<Pair<String, String>> contact_info = new ArrayList<Pair<String, String>>();
        String[] contact_patterns;
        try{
            contact_patterns = Util.getConfigStringArrayFromFile("numeric_number_indicators");
        } catch (Exception e) {
            contact_patterns = new String[]{"Fax", "Tel.", "Telefon", "Telefax"};
        }

        Elements elements = current_document.getAllElements();
        Elements el_containing_contact = new Elements();
        ArrayList<Pair<String, Elements>> contact_indicator_element_list = new ArrayList<Pair<String, Elements>>();
        for(String contact_indicator : contact_patterns) {
            Elements elements_containing_indicators = current_document.getElementsContainingOwnText(contact_indicator);
            contact_indicator_element_list.add(new Pair<String, Elements>(contact_indicator, elements_containing_indicators));
        }

        for (Pair<String, Elements> p : contact_indicator_element_list) {
            Pattern numeric_pattern = Pattern.compile("([()/0-9\\-\\+]+\\s?){3,}", Pattern.CASE_INSENSITIVE); // [()/0-9\-\+]+
            String contact_type = p.getKey();
            Elements contact_elements = p.getValue();
            for (Element e : contact_elements) {
                String e_as_string = e.toString();
                int starting_point = e_as_string.indexOf(contact_type) + contact_type.length();
                Matcher matcher = numeric_pattern.matcher(e_as_string.substring(starting_point).replace("&nbsp;", " "));
                if(matcher.find()) {
                    int end_point = matcher.start();

                    String contact_value = matcher.group();
                    Pair contact_pair = new Pair(contact_type, contact_value);
                    matches.add(contact_pair);
                    NumericContactObject nco = new NumericContactObject();
                    nco.setElements(elements);
                    nco.setElement(e);
                    nco.setContact_info(contact_value);
                    nco.setContact_type(contact_type);
                    numericContactObjects.add(nco);
                }
            }
        }
        return numericContactObjects;
    }

    /**
     * Searches through the document and sees if Names contains that name. If yes the Name and Element are stored
     * @return An ArrayList of MutablePairs, on the left side containing the name, on the right side containing the Element it was found in
     */
    public ArrayList<MutablePair<String, Element>> findAllNames() {
        Elements elements = current_document.getAllElements();
        Names names = Names.getInstance();
        ArrayList<MutablePair<String, Element>> names_in_doc_list = new ArrayList<MutablePair<String, Element>>();
        for(Element e : elements) {
            String[] e_splitted = e.ownText().replace(",", "").replace(".", "").split(" ");
            for (String token : e_splitted) {
                if(names.containsName(token)) {
                    names_in_doc_list.add(new MutablePair<String, Element>(token, e));
                }
            }
        }
        return names_in_doc_list;
    }

    /**
     * Uses Indikator to find editors.
     * Searches for elements with indicaotrs, then searches for all names in the document.
     * Closest Name to an indicator gets matched and is set inside of an EditorInfoObject
     * @return ArrayList of EditorInfoObjects. Each containing A role and a corresponding Name.
     */
    public ArrayList<EditorInfoObject> findEditorInfo() {
        String[] rollen;
        try{
            rollen = Util.getConfigStringArrayFromFile("editor_indicators");
        } catch (Exception e) {
            rollen = new String[]{"Redakteur", "Leiter", "Webredaktion",
                    "Redaktionelle Mitarbeit", "Redaktionsassistenz",
                    "Verantwortliche Redakteurin", "Leitender SEO Redakteur",
                    "Artdirektion", "Inhaltlich Verantwortliche gemäß § 18 Abs. 2 MStV",
                    "Redaktion", "Verantwortlich für den Inhalt nach § 55 Abs. 2 RStV"};
        }

        Elements elements = current_document.getAllElements();

        ArrayList<MutablePair<String, Element>> names_in_doc_list = findAllNames();

        ArrayList<EditorInfoObject> editorInfoObjects = new ArrayList<EditorInfoObject>();

        for (String s : rollen) {

            Elements with_role_indicator = elements.select("*:containsOwn("+s+")");


            for (Element e : with_role_indicator) {
                Element name_with_min_distance = null;
                int distance = 10000000;
                int e_index = elements.indexOf(e);
                Element closest_element = null;
                for (MutablePair<String, Element> mp : names_in_doc_list) {
                    int name_index = elements.indexOf(mp.getRight());
                    int e_to_name_distance = Math.abs(e_index - name_index);
                    if(e_to_name_distance < distance) {
                        distance = e_to_name_distance;
                        System.out.println("Distance: " + distance);
                        if(distance < 50) { //kleinere Distanzen haben auch nicht gut funktioniert
                            closest_element = mp.getRight();
                        }
                    }

                }
                //if own text to long: it is no real name.
                try {
                    System.out.println("Rolle: " + s);
                    System.out.println("element: " + closest_element.ownText());
                    if(closest_element.ownText().split(" ").length < 10) {
                        EditorInfoObject eo = new EditorInfoObject();
                        eo.setElement(e);
                        eo.setElements(elements);
                        eo.setRole(s);
                        eo.setName(closest_element.ownText());
                        editorInfoObjects.add(eo);
                    }
                } catch (NullPointerException exception) {
                    System.out.println("editor name is to far away.");
                    //Es konnte kein Element gefunden werden, dass näher als 50 Stellen entfernt ist.
                }
            }
        }
        return editorInfoObjects;
    }

    /**
     * Uses Indicators to find the element in the document where info on the content is to be expected
     *
     * @return Returns an ArrayList of Redactionsobjects. Each containing corresponding Indikator and Element.
     */
    public ArrayList<RedaktionsObject> findRedaktionellerAngabe() {
        String[] redaktions_indikatoren;
        try{
            redaktions_indikatoren = Util.getConfigStringArrayFromFile("redaktions_indicators");
        } catch (Exception e) {
            redaktions_indikatoren = new String[]{"Redaktion", "§ 10 Absatz 3 MDStV", "inhaltlich verantwortlich", "§ 18 Abs. 2 MStV", "§ 10 Absatz 3 MDStV", "redaktion"};
        }

        Elements elements = current_document.getAllElements();

        ArrayList<RedaktionsObject> redaktionsObjects = new ArrayList<RedaktionsObject>();
        for (String s : redaktions_indikatoren) {
            Elements elements_with_indicator = elements.select("*:containsOwn("+s+")");
            RedaktionsObject ro = new RedaktionsObject();
            ro.setElements(elements);
            ro.setIndikator(s);

            ro.setElements_with_indikator(elements_with_indicator);
            if(elements_with_indicator.size() > 0) {
                redaktionsObjects.add(ro);
            }
        }
        return redaktionsObjects;
    }

    /**
     * Uses Indikators to find Berufsordnungen and uses Indicators to prevent accepting objects that are
     * part of other juridictive info blocks.
     * @return BerufsordnungsObject that collects all Berufsordnungen that could be found inside of the document
     */
    public BerufsordnungsObject findBerufsordnung() {
        //Setzen der Config Indikatoren
        String[] regelung_nennung_indicator;
        try{
            regelung_nennung_indicator = Util.getConfigStringArrayFromFile("berufsrechtliche_regelung_indicators");
        } catch (Exception e) {
            regelung_nennung_indicator = new String[]{"berufsrechtliche regelungen"};
        }

        String[] regelung_indicator;
        try{
            regelung_indicator = Util.getConfigStringArrayFromFile("berufsordnung_indicators");
        } catch (Exception e) {
            regelung_indicator = new String[]{"satzung", "verordnung", "ordnung", "gesetz", "berufsregeln"};
        }

        String[] datenschutz_indicator;
        try{
            datenschutz_indicator = Util.getConfigStringArrayFromFile("datenschutz_indicators");
        } catch (Exception e) {
            datenschutz_indicator = new String[]{"Sperrung der Nutzung", "datenschutz", "dsvgo",
                                                "online-beilegung", "streitbeilegung", "verbraucherstreitbeilegungsgesetz",
                    "§§ 8 bis 10 tmg", "berufshaftpflichtversicherung"};
        }
        //
        ArrayList<String> regelungen = new ArrayList<>();

        Elements elements = current_document.getAllElements();

        Elements elements_with_nennung = new Elements();
        Elements elements_with_regelung = new Elements();

        for (Element e : elements) {
            String e_ownText = e.ownText();
            if (check(regelung_nennung_indicator, e_ownText) && !check(datenschutz_indicator, e_ownText)) {
                elements_with_nennung.add(e);
            }
            if(check(regelung_indicator, e_ownText) && !check(datenschutz_indicator, e_ownText)) {
                elements_with_regelung.add(e);
                regelungen.add(e_ownText);
            }
        }

        BerufsordnungsObject bo = new BerufsordnungsObject();
        bo.setElements(elements);
        bo.setElements_with_nennung(elements_with_nennung);
        bo.setElements_with_regelung(elements_with_regelung);
        bo.setRegelung_links(regelungen);

      return bo;
    };

    /*
    public Object findAudiovisuellMitgliedstaat() {

        return null;
    }

    public Object findInfoAbwicklung() {

        return null;
    }
    */
    /**
     * Searches with the help of indicators, for gesetzliche Berufsbezeichnungen, that have to have Kammern,
     * <br>
     * Over the course of the analysis, Chamber Objects are created and put into an ArrayList.
     * @return An ArrayList of ChamberObjects
     */
    public ArrayList<ChamberObject> findVerkammerteBerufe() {
        String[] verkammerte_berufe_indikator;
        try{
            verkammerte_berufe_indikator = Util.getConfigStringArrayFromFile("verkammerte_berufe_indicators");
        } catch (Exception e) {
            verkammerte_berufe_indikator = new String[]{"arzt", "zahnarzt", "tierarzt", "apotheker", "psychotherapeut", "notar", "rechtsanwalt", "patentanwalt", "steuerberater"
                    , "wirtschaftsprüfer", "architekt", "beratender ingnieur", "ärztin", "zahnärztin", "tierärztin", "apothekerin", "psychotherapeutin",
                    "notarin", "rechtsanwältin", "patentanwältin", "steuerberaterin", "wirtschaftsprüferin", "architektin", "beratende ingenieurin"};
        }

        Pattern kammer_pattern = Pattern.compile("[\\s]?[\\wäöü]*kammer", Pattern.CASE_INSENSITIVE);
        Elements elements = current_document.getAllElements();
        HashSet<String> found_berufe = new HashSet<>();
        for(Element e : elements) {
            found_berufe.addAll(check_return_matches(verkammerte_berufe_indikator,e.ownText()));
        }

        ArrayList<Integer> indezes_with_kammer = new ArrayList<Integer>();
        ArrayList<String> valid_chambers = new ArrayList<String>();
        ArrayList<ChamberObject> chamberObjects = new ArrayList<ChamberObject>();
        for(Element e : elements) {
            ArrayList<String> beruf_indikatoren =  check_return_matches(verkammerte_berufe_indikator, e.ownText());
            ArrayList<String> matches = new ArrayList<String>();

            ArrayList<String> valid_chambers_in_element = new ArrayList<String>();

            ChamberObject chamberObject = new ChamberObject();
            chamberObject.setBerufsbezeichnung(new ArrayList<String>(found_berufe));
            chamberObject.setElements(elements);
            chamberObject.setElement(e);

            Matcher kammer_matcher = kammer_pattern.matcher(e.ownText());
            while (kammer_matcher.find()) {
                String match = kammer_matcher.group().trim();
                System.out.println("Found match: " + kammer_matcher.group());
                System.out.println("Element text: " + e.ownText());
                matches.add(match);
            }

            chamberObject.setChamber_indicatros(matches);

            ArrayList<String> element_splitted = new ArrayList<String>(Arrays.asList(e.ownText().replaceAll("[,.:]]", "").split(" ")));
            ArrayList<Integer> indezes_of_matches = new ArrayList<Integer>();
            for (String match : matches) {
                int index_of_match = element_splitted.indexOf(match);
                indezes_of_matches.add(index_of_match);
            }

            Kammern kammern = Kammern.getInstance();

            for(int match_index : indezes_of_matches) {
                String forward = "";
                String backwards = "";
                String both = "";

                try {
                    String controlled_match = element_splitted.get(match_index);
                    forward += controlled_match;
                    backwards += controlled_match;
                    both += controlled_match;
                } catch (ArrayIndexOutOfBoundsException exception) {
                    break;
                }

                int forward_limit = 10;
                int backwards_limit = 4;
                int both_limit = 5;
                for(int iteration = 1; iteration <= forward_limit; iteration++) {
                    int next_word_index = match_index + iteration;
                    String next_word = "";
                    try {
                        next_word = element_splitted.get(next_word_index);
                    } catch (IndexOutOfBoundsException exception) {
                       // continue
                    }
                    forward += " " + next_word;
                    boolean valid_kammer_found = kammern.containsKammer(forward);
                    if(valid_kammer_found) {

                        valid_chambers.add(forward);
                        valid_chambers_in_element.add(forward);
                        chamberObject.setValid_chamber_found(true);
                    }
                }
                for(int iteration = 1; iteration < backwards_limit; iteration++) {
                    int backwards_word_index = match_index - iteration;
                    String before_word = "";
                    try {
                        before_word = element_splitted.get(backwards_word_index) + " ";
                    } catch ( IndexOutOfBoundsException exception) {
                       // System.out.println("Forward is nothing to be found");
                    }
                    backwards = before_word + backwards;
                    boolean valid_kammer_found = kammern.containsKammer(backwards);
                    if(valid_kammer_found) {

                        valid_chambers.add(backwards);
                        valid_chambers_in_element.add(backwards);
                        chamberObject.setValid_chamber_found(true);
                        //break;
                    }
                }
                for(int iteration = 1; iteration < both_limit; iteration++) {
                    int forward_word_index = match_index + iteration;
                    int backwards_word_index = match_index - iteration;
                    String next_word = "";
                    String before_word = "";
                    try {
                        next_word = " " + element_splitted.get(forward_word_index);
                    }  catch ( IndexOutOfBoundsException exception) {
                       // System.out.println("Forward is nothing to be found");
                    }
                    try {
                        before_word = element_splitted.get(backwards_word_index) + " ";
                    } catch ( IndexOutOfBoundsException exception) {
                       // System.out.println("Forward is nothing to be found");
                    }
                    both = before_word + both + next_word;
                    boolean valid_kammer_found = kammern.containsKammer(backwards);
                    if(valid_kammer_found) {

                        valid_chambers.add(both);
                        valid_chambers_in_element.add(both);
                        chamberObject.setValid_chamber_found(true);
                        //break;
                    }
                }

            }

                chamberObject.setValid_chambers(valid_chambers_in_element);
                if(valid_chambers_in_element.size() > 0) {
                    chamberObjects.add(chamberObject);
                }


        }

        return chamberObjects;
    }

    /**
     * Parses the text with specific Patterns to find court and registernumber indicators
     * @return an ArrayList of RegisterObjects containg a registernumber and a court.
     */
    public ArrayList<RegisterObject> findRegister_Registernummer() {

        //Handelsregister (HRB oder HRA gefolgt von 5 ziffern), Vereinsregister (VR), Genossenschaftsregister (GnR), Partnerschaftsregister (PR) //TODO laut Evaluation kann auch ein B am Ende einer Nummer stehen. einfügen
        Pattern register_pattern = Pattern.compile("[a-z]*register[:]?[\\s]+", Pattern.CASE_INSENSITIVE); //[\s>]
        Pattern register_court_pattern = Pattern.compile("[\\s>]*[a-z]*gericht\\s[\\w]*[\\s]?", Pattern.CASE_INSENSITIVE);
        Pattern register_number_pattern = Pattern.compile("\\s[hra\\sbvgnp]{2,4}\\s?[\\d]{3,5}", Pattern.CASE_INSENSITIVE); //matches registernumbers, HRB would be specific for Handelsregister Abteilung B
        Elements elements = current_document.getAllElements();
        ArrayList<MutableTriple<String, Integer, Integer>> possible_register = new ArrayList<MutableTriple<String, Integer, Integer>>();
        ArrayList<MutableTriple<String, Integer, Integer>> possible_court = new ArrayList<MutableTriple<String, Integer, Integer>>();
        ArrayList<MutableTriple<String, Integer, Integer>> possible_numbers = new ArrayList<MutableTriple<String, Integer, Integer>>();

        ArrayList<RegisterObject> registerObjects = new ArrayList<RegisterObject>();

        for (Element e : elements) {
            ArrayList<Element> register_element = new ArrayList<Element>();
            String element_own_text = e.ownText();
            Matcher matcher_register = register_pattern.matcher(element_own_text);
            Matcher matcher_court = register_court_pattern.matcher(element_own_text);
            Matcher matcher_numbers = register_number_pattern.matcher(element_own_text);

            MutableTriple<String, Integer, Integer> group_register_index_pair = null;
            MutableTriple<String, Integer, Integer> group_court_index_pair = null;
            MutableTriple<String, Integer, Integer> group_number_index_pair = null;

            while(matcher_register.find()) {
                group_register_index_pair = new MutableTriple(matcher_register.group(), matcher_register.end(), elements.indexOf(e));
                possible_register.add(group_register_index_pair);
            }
            while(matcher_court.find()) {
                group_court_index_pair = new MutableTriple(matcher_court.group(), matcher_court.end(), elements.indexOf(e));
                possible_court.add(group_court_index_pair);
            }
            while(matcher_numbers.find()) {
                RegisterObject ro = new RegisterObject();
                group_number_index_pair = new MutableTriple<>(matcher_numbers.group(), matcher_numbers.start(), elements.indexOf(e));
                possible_numbers.add(group_number_index_pair);
                ArrayList<String> element_own_text_as_list = new ArrayList<String>(Arrays.asList(element_own_text.substring(0, matcher_numbers.start()).split(" ")));

                ro.setRegister_number(matcher_numbers.group());
                ro.setRegister_number_element(e);
                ro.setRegister_number_element_index(elements.indexOf(e));

                if(group_court_index_pair != null) {

                    if(group_court_index_pair.getRight().equals(group_number_index_pair.getRight())) {
                        //We found an Amtsgericht
                        ro.setRegister_court(group_court_index_pair.getLeft());
                        ro.setRegister_court_element(elements.get(group_court_index_pair.getRight()));
                        ro.setRegister_court_element_index(group_court_index_pair.getRight());
                    }
                } else {
                    //We could not find an Amtsgericht, so we guess
                    ro.setRegister_court("Amtsgericht " + element_own_text_as_list.get(element_own_text_as_list.size()-1));
                    ro.setRegister_court_element(e);
                    ro.setRegister_court_element_index(elements.indexOf(e));
                }
                registerObjects.add(ro);
            }

        }
        System.out.println("Register indikators: " + possible_register);
        System.out.println("Court indikators: " + possible_court);
        System.out.println("Court numbers: " + possible_numbers);
        return registerObjects;
    }

    /**
     * Uses Indicators to search for Elements containing the Aufsichtsbehörde
     * @return an AufsichObject containing all Elements that hold that specific Indicator
     */
    public AufsichtsObject findZustaendigeAufsichtsbehoerde() {
        String[] aufsichtsbehoerde_indicator = {"Aufsichtsbehörde"};

        Elements elements = current_document.getAllElements();
        Elements elements_with_structural_indicator = new Elements();

        elements_with_structural_indicator = elements.select("*:containsOwn(Aufsichtsbehörde)");

        boolean structural_indicator_present = elements_with_structural_indicator.size() > 0;

        String behoerde = "";

        System.out.println(elements_with_structural_indicator);
        if(elements_with_structural_indicator.size() > 0) {
            for (Element element : elements_with_structural_indicator) {
                try{
                    behoerde = element.nextElementSibling().text();
                } catch (NullPointerException e) {

                }
            }
        }
        AufsichtsObject ao = new AufsichtsObject();

        ao.setElements_with_structural_indicator(elements_with_structural_indicator);
        ao.setHas_indicator(structural_indicator_present);
        ao.setElements(elements);

        return ao;
    }
    /*
        Deprecated.
     */
    public Object findWst() { //No wirtschaftsidentifiaktionsnummer found, like ever.
        String[] wst_indicator = {"wirtschafts-identifikationsnummer", "w-idnr."};
        Pattern pattern_ust = Pattern.compile("DE\\s?\\d{9}", Pattern.CASE_INSENSITIVE);
        Elements elements = current_document.getAllElements();
        Elements elements_with_indicator = new Elements();
        Elements elements_with_number = new Elements();
        ArrayList<String> possible_wst_numbers = new ArrayList<String>();

        for(Element e : elements) {
            String e_as_string = e.toString();
            if (check(wst_indicator, e_as_string)) {
                elements_with_indicator.add(e);
            }
            Matcher matcher = pattern_ust.matcher(e_as_string);
            while(matcher.find()) {
                possible_wst_numbers.add(matcher.group());
                elements_with_number.add(e);
            }
        }
        System.out.println("Elements_with_indicator: " + elements_with_indicator.size());
        System.out.println("Elements_with_number: " + elements_with_number.size());
        System.out.println("Numbers: " + possible_wst_numbers);
        return null;
    }

    /**
     * Compiles a Pattern for the specific UST Number and parses the document with it.
     * @return an ArrayList of Ust_IdentificationNumbers each containing the number and element it was found in
     */
    public ArrayList<Ust_IdentificationNumber> findUst() {
        String[] ust_indicator = {"ust-id", "umsatzsteueridentifikationsnummer", "umsatzsteuer-id", "ust-idnr." };
        Pattern pattern_ust = Pattern.compile("[\\s>][a-z][a-z](\\s?\\d){9}", Pattern.CASE_INSENSITIVE);
        Elements elements = current_document.getAllElements();
        Elements elements_with_indicator = new Elements();
        Elements elements_with_number = new Elements();
        ArrayList<String> possible_wst_numbers = new ArrayList<String>();
        ArrayList<Ust_IdentificationNumber> ust_object_list = new ArrayList<>();

        String last_ust_number = "";
        for(Element e : elements) {
            String e_as_string = e.toString();
            if (check(ust_indicator, e_as_string)) {
                elements_with_indicator.add(e);
            }
            Matcher matcher = pattern_ust.matcher(e_as_string);

            while(matcher.find()) {
                boolean indicator_present = check(ust_indicator, e_as_string);
                String ust_number = matcher.group();
                ust_number = ust_number.replace(">", "");
                if(!last_ust_number.equals(ust_number)) {
                    int e_index = elements.indexOf(e);
                    possible_wst_numbers.add(ust_number);
                    elements_with_number.add(e);
                    Ust_IdentificationNumber ust_object = new Ust_IdentificationNumber(indicator_present, e, e_index, ust_number);
                    ust_object_list.add(ust_object);
                }
                last_ust_number = ust_number;
            }
        }
        return ust_object_list;
    }

    /**
     * Parses an Element for names and algorithmically tries to guess the surname.
     *
     * @param e Element that should be parsed
     *
     * @return ArrayList of found Names and Surnames
     */
    private ArrayList<String> extractNames(Element e) {
        Names names = Names.getInstance();
        ArrayList<Integer> indezes = new ArrayList<Integer>();

        String[] e_splitted = e.toString().replaceAll("[,.:<>]", " ").split("\\s");
        for(int i = 0; i< e_splitted.length; i++) {
            if(names.containsName(e_splitted[i])) {
                indezes.add(i);
            }
        }

        ArrayList<String> extracted_names = new ArrayList<String>();
        for(int i = 0; i < indezes.size(); i++) {
            String name = e_splitted[indezes.get(i)];
            int name_index = indezes.get(i);
            int next_name_index = e_splitted.length;
            try {
                next_name_index = indezes.get(i+1);
            } catch (IndexOutOfBoundsException error) {
                next_name_index = indezes.get(i) + 1;
            }

            int diff = next_name_index - name_index;
            String surname = e_splitted[indezes.get(i)+1];

            extracted_names.add(name + " " +surname);
        }
        return extracted_names;
    }

    /**
     * Finds Vertretungsberechtigte with the help of indicators.
     *
     * @return returns a list of VertretungsberechtigterObjects, mit Rolle und Namen.
     */
    public ArrayList<VertretungsberechtigterObject> findVertretungsberechtigten() {
        ArrayList<VertretungsberechtigterObject> vo_list = new ArrayList<>();
        Names names = Names.getInstance();

        String[] vertreter = {"inhaber", "inhaberin", "inh.", "geschäftsführender", "geschäftsführung" ,"gesellschafter", "geschäftsführer", "präsident", "intendant", "managing directors", "vorstand",
                 "vertretung", "vertreten durch", "vertreten", "partner"}; // "leiterin", "leiter",

        String[] vertreter_indicator = {"vertreten", }; //
        int vertreter_found = 0;

        Elements elements = current_document.getAllElements();
        for (Element e : elements) {
            MutablePair<ArrayList<String>, ArrayList<String>> role_name_pair = new MutablePair<ArrayList<String>, ArrayList<String>>(new ArrayList<>(), new ArrayList<>());
            String e_as_string = e.ownText();
            VertretungsberechtigterObject vo = new VertretungsberechtigterObject();

            if(check(vertreter, e_as_string)) {
                ArrayList<String> matches_vertreter = check_return_matches(vertreter, e_as_string);

                role_name_pair.setLeft(matches_vertreter);
                ArrayList<Integer> indezes = new ArrayList<Integer>();
                vertreter_found++;

                ArrayList<String> found_names = extractNames(e);

                if (found_names.size() != 0) {
                    role_name_pair.getRight().addAll(found_names);
                    //Constructor?
                    vo.setFound_names(removeDuplicates(found_names));
                    vo.setElements(elements);
                    vo.setVertretungs_indikatoren(matches_vertreter);
                    vo.setRole_element(e);
                    vo.setRole_element_index(elements.indexOf(e));
                    vo.setName_element(e);
                    vo.setName_element_index(elements.indexOf(e));
                    vo_list.add(vo);
                    break;
                }
                else {
                    Element parent_of_element = e.parent();
                    ArrayList<String> names_in_parent = new ArrayList<>();
                    for (String s : matches_vertreter) {
                        String parent_as_string = parent_of_element.toString();
                        int index_of_match = parent_of_element.toString().toLowerCase().indexOf(s);

                        String parent_substring = parent_as_string.substring(index_of_match + s.length()).replaceAll("[<>]", " ");
                        names_in_parent = extractNames(new Element("<div>").appendText(parent_substring));

                    }
                    //Constructor?
                    vo.setElements(elements);
                    vo.setVertretungs_indikatoren(matches_vertreter);
                    vo.setRole_element(e);
                    vo.setRole_element_index(elements.indexOf(e));
                    vo.setFound_names(removeDuplicates(names_in_parent));
                    vo.setName_element(parent_of_element);
                    vo.setName_element_index(elements.indexOf(parent_of_element));
                    vo_list.add(vo);

                    System.out.println(e.parent());
                }
            }

        }
        return vo_list;
    }

    /**
     * Loops over the Elements of document and creates for every element, that contains a Rechtsform indicator, a new RechtsformObject.
     * <p>
     *     In the end a list of Rechtsformobjects is returned.
     * </p>
     * @return A list of RechtsformObjects that contain the found Rechtsformindicators.
     */
    public ArrayList<RechtsformObject> findRechtsform() {
        ArrayList<RechtsformObject> rechtsformObjects = new ArrayList<RechtsformObject>();
        String[] rechtsform_indicators = {"Körperschaft des öffentlichen Rechts","Gesellschaft bürgerlichen Rechts", "Unternehmensgruppe", " ag"," aktiengesellschaft", " aör",  " co.kg", " commerce", " e."," eigenbetrieb",
                " einzelunternehmen", " eg", " ev", " e.k.", " e.v.", " ewiv", " gag", " gbr", " german-reit", " gesellschaft",
                " ggmbh", " gmbh", " g-reit", " haftungsbeschränkt", " (haftungsbeschränkt)", " handelsgesellschaft",
                " invag", " kg", " kgaa", " kör", " limited", " ltd.", " mbb", " mbh", " ohg", " partenreederei",
                " partg", " plc", " regiebetrieb", " reit-ag", " sce", " shop", " stiftung", " sup",
                " ug", " verwaltungs-gmbh", " vvag",  " webshop"}; // " v.", " se", " co", " co.", Haben zu häufig zu ungewollten Matches geführt
        Elements elements = current_document.getAllElements();

        for(Element e : elements) {
            ArrayList<String> found_rechtsform_indicators = check_return_matches(rechtsform_indicators, e.ownText());
            if(found_rechtsform_indicators.size() > 0 ) {
                RechtsformObject ro = new RechtsformObject();
                ro.setFound_rechtsform_indicators(found_rechtsform_indicators);
                ro.setElements(elements);
                ro.setElement(e);
                rechtsformObjects.add(ro);
            }
        }
        return rechtsformObjects;
    }

    /**
     * Initially searches for german Post-Codes and the corresponding City. Then guesses where Street and adressed Person is located
     * @return an ArrayList of AdressObjects. Each containing an Address and info about the circumstances.
     */
    public ArrayList<AdressObject> findPLZandCity() {

        ArrayList<AdressObject> found_adresses = new ArrayList<AdressObject>();

        ArrayList<Object> matches = new ArrayList<Object>();
        //Compile PLZ+City Pattern
        Pattern plz_location_pattern = Pattern.compile("\\d{5}\\s[a-züäö\\s]+", Pattern.CASE_INSENSITIVE);
        Elements elements = current_document.getAllElements();
        Elements plz_elements = new Elements();
        HashSet<String> found_numbers = new HashSet<String>(); //TODO: Better naming -- found_postcodes?
        for (Element e : elements) {
            Matcher matcher = plz_location_pattern.matcher(e.toString());
            if(matcher.find()) found_numbers.add(matcher.group());
        }

        ArrayList<Pair<String, Elements>> plz_element_pairs = new ArrayList<Pair<String, Elements>>();

        for (String match : found_numbers) {
            plz_element_pairs.add(new Pair<String, Elements>(match, current_document.getElementsContainingOwnText(match)));
        }

        Pattern tag_matcher = Pattern.compile("[,>][^,<>]+[,<]");

        for (Pair<String, Elements> p : plz_element_pairs) {

            for(Element e : p.getValue()) {

                ArrayList<MutablePair<String, String>> adress_matches = new ArrayList<MutablePair<String, String>>();
                adress_matches.add(new MutablePair<String, String>("PLZ + Ort:", null));
                adress_matches.add(new MutablePair<String, String>("Straße:", null));
                adress_matches.add(new MutablePair<String, String>("Adressat:", null));
                Iterator<MutablePair<String, String>> adress_matches_it = adress_matches.iterator();

                adress_matches_it.next().setRight(p.getKey());

                AdressObject adressObject = new AdressObject();

                adressObject.setElements(p.getValue());
                adressObject.setPlz_element(e);
                adressObject.setPlz_location(p.getKey());

                String element_as_string = e.toString();
                int end_index = element_as_string.indexOf(p.getKey());

                if(end_index == -1) continue; //war mal ein break; command

                String element_as_string_shortened = element_as_string.substring(0, end_index);

                Matcher matcher = tag_matcher.matcher(element_as_string_shortened.replace(",", "<>"));
                int match_count = 0;

                ArrayList<String> pattern_matches = new ArrayList<String>();

                while(matcher.find()) {
                    String match_processed = matcher.group().substring(1, matcher.group().length() - 1); //entfernen der Symbole
                    if(!matcher.group().isEmpty() && !check(new String[]{"verantwortlich", "§",}, matcher.group())) {
                        match_count++;
                        pattern_matches.add(match_processed);
                    }
                }
                Collections.reverse(pattern_matches);

                for(String m : pattern_matches) {
                    try {
                        adress_matches_it.next().setRight(m);
                    } catch (NoSuchElementException exception) { //When we find more Matches then there are elements in the iterator the adress is splitted into 2 lines.
                        adress_matches.set(2, new MutablePair<String, String>(adress_matches.get(2).getLeft(), m + adress_matches.get(2).getRight()));
                    }
                }

                // If match_count is 2 everything was found

                if (match_count >= 2) {
                    adressObject.setStreet_element(e);
                    adressObject.setAdressed_element(e);

                } else if (match_count == 1) {
                    adressObject.setStreet_element(e);
                    //We got a street but no name
                    String name = (String) foundStreetNoName(e, adressObject);
                    adress_matches_it.next().setRight(name);
                } else if (match_count == 0) {
                    //We got no street and no name
                    MutablePair<String,String> street_name_pair = (MutablePair<String, String>) noStreetNoName(e, adressObject);
                    String street = street_name_pair.getLeft();
                    String adressed = street_name_pair.getRight();
                    adress_matches_it.next().setRight(street);
                    adress_matches_it.next().setRight(adressed);
                }
                matches.add(adress_matches);

                /////////////////////// Set properties of AdressObject
                String adressed = adress_matches.get(2).getValue();

                adressObject.setAdressed(adressed);

                Pair<Boolean, String> result_firm_name = check_firm_indicator(adressed);
                Boolean firm_indicator_found = result_firm_name.getKey();
                String firm_indicator = result_firm_name.getValue();
                adressObject.setFirm_indication(firm_indicator_found);
                adressObject.setFirm_indicator(firm_indicator);
                String street = adress_matches.get(1).getValue();
                adressObject.setStreet(street);
                Pair<Boolean, String> result_street = check_street_indicator(street);
                Boolean street_indicator_found = result_street.getKey();
                String street_indicator = result_street.getValue();

                adressObject.setStreet_indication(street_indicator_found);
                adressObject.setStreet_indicator(street_indicator);

                //////////////////////

                adressObject.setPlz_element_index(elements.indexOf(e));
                adressObject.setName_element_index(elements.indexOf(adressObject.getAdressed_element()));
                adressObject.setStreet_element_index(elements.indexOf(adressObject.getStreet_element()));
                System.out.println(adressObject.report());
                try {
                    adressObject.setIs_valid_address(!adressObject.getAdressed().equals(null) &&check_is_no_plz(adressObject.getPlz_location()) && check_is_no_plz(adressObject.getStreet()) && !check_is_postal_postfach(adressObject.getStreet()) && check_street_has_numeric(adressObject.getStreet()));
                    adressObject.setIs_postal_address(check_is_postal_postfach(adressObject.getStreet()));
                } catch (NullPointerException exception) {
                    adressObject.setIs_valid_address(false);
                    adressObject.setIs_postal_address(false);
                }

                found_adresses.add(adressObject);
            }

        }
        return found_adresses;
    }

    /**
     * Checks if String s contains an Indicator for a street
     * @param s
     * @return a Pair of a boolean and a string. boolean if indicator was found and a String with the indicator
     */
    private Pair<Boolean, String> check_firm_indicator(String s) {
        String[] firm_indicators = {"Unternehmensgruppe", "AG", " ag"," aktiengesellschaft", " aör", " co", " co.", " co.kg", " commerce", " e."," eigenbetrieb",
                " einzelunternehmen", " eg", " ev", " e.k.", " e.v.", " ewiv", " gag", " gbr", " german-reit", " gesellschaft",
                " ggmbh", " gmbh", " g-reit", " haftungsbeschränkt", " (haftungsbeschränkt)", " handelsgesellschaft",
                " invag", " kg", " kgaa", " kör", " limited", " ltd.", " mbb", " mbh", " ohg", " partenreederei",
                " partg", " plc", " regiebetrieb", " reit-ag", " se", " sce", " shop", " stiftung", " sup",
                " ug", " verwaltungs-gmbh", " vvag", " v.", " webshop"};

        Boolean bool = false;
        String found = null;

        try {
            bool = Arrays.stream(firm_indicators).anyMatch(s.toLowerCase()::contains);
            found = Arrays.stream(firm_indicators).filter(s.toLowerCase()::contains).findAny().get();

        } catch (NullPointerException e) {

        } catch (NoSuchElementException e) {

        }


        Pair<Boolean, String> result = new Pair<Boolean, String>(bool, found);

        return result;
    }

    /**
     * Checks if a found adress is a postal adress
     * @param s
     * @return boolean if adress is a postaladress
     */
    private boolean check_is_postalddress(String s) {
        String[] postaladdress_indicators = {"postal address", "postadresse"};
        return check(postaladdress_indicators, s);
    }

    /**
     * Checks if a found adress is a postfach (german postal adress)
     * @param s
     * @return boolean if adress is a postaladress
     */
    private boolean check_is_postal_postfach(String s) {
        return (check_is_postfach(s) || check_is_postalddress(s));
    }

    /**
     * Checks if an element of an array is contained inside of a given String. \n
     * if yes, function returns true. converts s to lower String.
     * @param array Array of indicators
     * @param s     String which is checked whether indicators are present
     * @return reutrns true if the string contains an element of the array
     */
    private boolean check(String[] array, String s) {
        Boolean bool = false;
        try {
            bool = Arrays.stream(array).anyMatch(s.toLowerCase()::contains);
        } catch (NoSuchElementException e) {
            System.out.println("NO SUCH ELEMENT");
        } catch (NullPointerException exception) {
            System.out.println("NO Valid String");
        }
        return bool;
    }

    /**
     * Returns an ArrayList of given Indicators that are contained in String s.
     * <p>
     * it converts the given String s to and the array element to lower case.
     *
     * @param array an Array of Indicators, to be checked
     * @param s     String that is parsed
     * @return      Arraylist of Strings that contains the indicators that are contained inside of the String s
     */
    private ArrayList<String> check_return_matches(String[] array, String s) {
        Stream<String> stream = Arrays.stream(array);
        ArrayList<String> matches = new ArrayList<String>();

        stream.forEach(str -> {
            if(s.toLowerCase().contains(str.toLowerCase())) {
                matches.add(str);
            }
        });
        return matches;
    }

    /**
     * removes duplicates inside an ArrayList of Strings
     * @param al
     * @return the provided ArrayList of Strings without any duplicates
     */
    private ArrayList<String> removeDuplicates(ArrayList<String> al) {
        ArrayList<String> n_al = new ArrayList<>();
        for (String s : al) {
            if (!n_al.contains(s)) {
                n_al.add(s);
            }
        }
        return n_al;
    }

    /**
     * Checks if PLZ really is a PLZ
     * @param s
     * @return boolean if PLZ is valid
     */
    private boolean check_is_no_plz(String s) {
        boolean has_to_many_words;
        int plz_word_count = 0;
        String trim = s.trim();

        plz_word_count = ((s.isEmpty()) ? 0 : trim.split("\\s").length);
        has_to_many_words = ((plz_word_count > 5) ? false : true);

        return has_to_many_words;
    }

    /**
     * Checks if a number is part of a given String
     * @param s
     * @return boolean true if a number is present
     */
    private boolean check_street_has_numeric(String s) {
        s.matches(".*\\d.*");

        return s.matches(".*\\d.*");
    }

    //deprecated
    private boolean check_plz_has_to_many_words(String s) {
        int plz_word_count = 0;
        String trim = s.trim();

        plz_word_count = ((s.isEmpty()) ? 0 : trim.split("\\s").length);
        boolean has_to_many_words = ((plz_word_count > 5) ? false : true);
        return has_to_many_words;
    }

    /**
     * Checks if postfach is present in String
     * @param s
     * @return bool if postfach is contained in String
     */
    private boolean check_is_postfach(String s) {
        String[] postfach_indicator = {"postfach"};
        return check(postfach_indicator, s);
    }

    /**
     * Checks if a String contains Street indicator
     * @param s
     * @return returns a Pair with boolean, true if Indicaotr is present, and a String which is the indicator
     */
    private Pair<Boolean, String> check_street_indicator(String s) {
        String[] street_indicators_post = {"allee", "arcaden", "arkaden", "brücke", "boulevard",
                "chaussee", "chausee", "chause", "chausse", "gasse",
                "weg", "straße", "strasse", "str.", "str", // "st" maybe isnt a good indicator get matched to often
                "passage", "platz", "ring", "tor", "nord", " süd",
                " west", " ost", "ufer", "damm", "anlage", "tor",
                "brunnen", "pfad", "postfach", "landstrasse",
                "landstraße", "landstr", "landstr.", "furt", "dorfstrasse",
                "dorfstraße", "dorfstr", "dorfstr.", "hof"};


        Boolean bool = false;
        String found = null;
        try {
            bool = Arrays.stream(street_indicators_post).anyMatch(s.toLowerCase()::contains);
            found = Arrays.stream(street_indicators_post).filter(s.toLowerCase()::contains).findAny().get();
        } catch (NoSuchElementException e) {
          //  System.out.println("NO SUCH ELEMENT CAN BE FOUND");
        } catch (NullPointerException e) {
            System.out.println("NullpointerException - in check street indicator");
        }

        Pair<Boolean, String> result = new Pair<Boolean, String>(bool, found);

        return result;

    }

    /**
     * Recursivley traverses so far in front of an element till there is nothing or another valid element
     * @param element
     * @return Returns the element itself or a previous sibling element that is no 'br' element
     */
    private Element previous_sibling_non_br(Element element) {
        try {
            if (element.previousElementSibling().tagName().equals("br")) {
                return previous_sibling_non_br(element.previousElementSibling());
            } else {
                return element.previousElementSibling();
            }
        } catch (NullPointerException exception) {
            return element;
        }
    }

    /**
     * If findPLZandLocation can only find a PLZ element
     * @param e
     * @param ao
     * @return Returns a mutable Pair containing the results
     */
    private MutablePair<String, String> noStreetNoName(Element e, AdressObject ao) {
        Elements e_siblings = e.siblingElements();

        MutablePair<String, String> result_pair = new MutablePair<String, String>(null, null);
        Element previous_sibling = previous_sibling_non_br(e); // e.previousElementSibling();
        Element pre_previous_sibling = previous_sibling_non_br(previous_sibling); //e.previousElementSibling().previousElementSibling();
        ao.setStreet_element(previous_sibling);
        ao.setAdressed_element(pre_previous_sibling);

        Pattern tag_matcher = Pattern.compile(">[^<>]+<");

        //Street name
        try {
            ArrayList<String> matches = new ArrayList<String>();
            String previous_sibling_as_string = previous_sibling.toString();
            Matcher matcher = tag_matcher.matcher(previous_sibling_as_string);

            String last_match = null;
            while(matcher.find()) {
                String match = matcher.group().substring(1, matcher.group().length()-1);
                matches.add(match);
                last_match = match;
                result_pair.setLeft(last_match);


            }
        } catch (NullPointerException error) {
           //NO_PREVIOUS_SIBLING
            return null;
        }
        //Adress Name
        try {
            ArrayList<String> matches = new ArrayList<String>();

            String pre_previous_sibling_as_string = pre_previous_sibling.toString();
            boolean element_contains_geschaeftsfuehrer_indikator = check(new String[]{"geschäftsführer"}, pre_previous_sibling_as_string);
            if(element_contains_geschaeftsfuehrer_indikator) {
                pre_previous_sibling_as_string = pre_previous_sibling.previousElementSibling().toString();
            }

            Matcher matcher = tag_matcher.matcher(pre_previous_sibling_as_string);
            String last_match = null;
            while(matcher.find()) {
                String match = matcher.group().substring(1, matcher.group().length()-1);
                matches.add(match);
                last_match = match;
                result_pair.setRight(last_match);
            }
        } catch (NullPointerException error) {
            return result_pair;
        }

        return result_pair;
    }

    /**
     * In Case findPLZandLocation found a plz and a street but no Name.
     * @param e
     * @param ao
     * @return a String that is to be found to be an adress.
     */
    private String foundStreetNoName(Element e, AdressObject ao) {
        ArrayList<String> matches = new ArrayList<String>();
        Element previous_sibling = e.previousElementSibling(); //previous_sibling_non_br(e) -- könnte möglicherweise die performance verbessern
        ao.setAdressed_element(previous_sibling);
        Pattern tag_matcher = Pattern.compile(">[^<>]+<");

        try {
            String previous_sibling_as_string = previous_sibling.toString();
            boolean element_contains_geschaeftsfuehrer_indikator = check(new String[]{"geschäftsführer"}, previous_sibling_as_string);
            if(element_contains_geschaeftsfuehrer_indikator) {
                previous_sibling_as_string = previous_sibling.previousElementSibling().toString();
            }
            Matcher matcher = tag_matcher.matcher(previous_sibling_as_string);
            int matcher_count = 0;
            String last_match = null;
            while(matcher.find()) {
                String match = matcher.group().substring(1, matcher.group().length()-1);
           //     System.out.println("Match of previous sibling: " + match);
                matches.add(match);
                last_match = match;
                matcher_count++;
                return last_match;
            }
        } catch (NullPointerException error) {
       //     System.out.println("NO_PREVIOUS_SIBLING");
            return null;
        }

        System.out.println(matches);
        return null;
    }

}
