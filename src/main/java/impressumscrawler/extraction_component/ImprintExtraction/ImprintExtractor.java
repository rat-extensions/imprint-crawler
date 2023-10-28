package impressumscrawler.extraction_component.ImprintExtraction;

import data.names.Names;
import impressumscrawler.datatypes.*;
import impressumscrawler.extraction_component.FieldExtraction.FieldExtractor;
import impressumscrawler.extraction_component.preprocessing.Preprocessor;
import impressumscrawler.search_component.ImprintFinder;
import javafx.util.Pair;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Stream;

public class ImprintExtractor {

    private Document current_document;

    private Elements elements;

    private FieldExtractor fieldExtractor = null;

    /**
     * Checks if the adressed of an adress is a person
     * @param ao    AdressObject that is looked at
     * @return      returns true if the adressed contains a name
     */
    public boolean adressed_is_person(AdressObject ao) {
        Names names = Names.getInstance();
        try {
            String[] adressed_splitted = ao.getAdressed().split(" ");
            for(String s : adressed_splitted) {
                if (names.containsName(s)) return true;
            }
        } catch (NullPointerException exception) {
            return false;
        }
        return false;
    }

    /**
     * Returns Elements of given Array that are inside of given String
     * @param array An array with Indicators
     * @param s     A String that needs to be checked against indicators
     * @return      A list of indicators that are present in the String
     */
    private ArrayList<String> check_return_matches(String[] array, String s) {
        Stream<String> stream = Arrays.stream(array);
        ArrayList<String> matches = new ArrayList<String>();

        stream.forEach(str -> {
            //System.out.println("Checking...");
            //System.out.println(s);
            if(s.toLowerCase().contains(str.toLowerCase())) {
                matches.add(str);
            }
        });
        return matches;
    }

    /**
     * gathers the Rechtsformen inside a given String.
     * @param s
     * @return A list of Firm Indicators, that are present in the given String
     */
    private ArrayList<String> findRechtsform(String s) {
        String[] rechtsform_indicators = {"Unternehmensgruppe", " ag"," aktiengesellschaft", " aör", " co", " co.", " co.kg", " commerce", " e."," eigenbetrieb",
                " einzelunternehmen", " eg", " ev", " e.k.", " e.v.", " ewiv", " gag", " gbr", " german-reit", " gesellschaft",
                " ggmbh", " gmbh", " g-reit", " haftungsbeschränkt", " (haftungsbeschränkt)", " handelsgesellschaft",
                " invag", " kg", " kgaa", " kör", " limited", " ltd.", " mbb", " mbh", " ohg", " partenreederei",
                " partg", " plc", " regiebetrieb", " reit-ag", " se", " sce", " shop", " stiftung", " sup",
                " ug", " verwaltungs-gmbh", " vvag", " v.", " webshop"};
        Elements elements = current_document.getAllElements();

        ArrayList<String> found_firm_indicators = check_return_matches(rechtsform_indicators, s);

        return found_firm_indicators;
    }

    public ImprintObject analyzeDoc(Document document) {
        this.current_document = document;
        fieldExtractor.setCurrent_document(document);
        return analyzeDoc();
    }

    /**
     * Analysis current document and stores results inside of an ImprintObject
     * @return ImprintObject with all the corresponding info from the imprint.
     */
    public ImprintObject analyzeDoc() {

        ImprintObject imprint = new ImprintObject();

        Document doc = current_document;
        elements = current_document.getAllElements();

        ArrayList<AdressObject> adressObjects = fieldExtractor.findPLZandCity();
        ArrayList<VertretungsberechtigterObject> vertretungsberechtigterObjects = fieldExtractor.findVertretungsberechtigten();
        ArrayList<String> mails = fieldExtractor.find_email_contact();
        ArrayList<String> contactforms = fieldExtractor.findKontaktformular();
        ArrayList<NumericContactObject> numericContactObjects = fieldExtractor.find_numeric_contact();
        AufsichtsObject aufsichtsObject = fieldExtractor.findZustaendigeAufsichtsbehoerde();
        ArrayList<RedaktionsObject> redaktionsObjects = fieldExtractor.findRedaktionellerAngabe();
        ArrayList<EditorInfoObject> editorInfoObjects = fieldExtractor.findEditorInfo();
        ArrayList<Ust_IdentificationNumber> ust_identificationNumbers = fieldExtractor.findUst();
        ArrayList<ChamberObject> chamberObjects = fieldExtractor.findVerkammerteBerufe();
        BerufsordnungsObject berufsordnungsObject = fieldExtractor.findBerufsordnung(); // Muss nur bei verkammertem Beruf berücksichtigt werden
        ArrayList<RegisterObject> registerObjects = fieldExtractor.findRegister_Registernummer();
        ArrayList<RechtsformObject> rechtsformObjects = fieldExtractor.findRechtsform();

        boolean firm_found = false;

        //check for firm in an address
        AdressObject adressObject_with_firm_indication = null;
        for(AdressObject ao : adressObjects) {
            if(ao.isFirm_indication()) {
                System.out.println("FIRM INDICATION FOUND");
                adressObject_with_firm_indication = ao;
                firm_found = true;
                imprint.setAddress_contains_firm(true);
                imprint.setRechtsform(adressObject_with_firm_indication.getFirm_indicator());
                //imprint.setRechtsform("Has nothing");
                // imprint.setRechtsform(ao.getFirm_indicator());
            }
        }

        //check if name is present in address
        boolean name_found = false;
        for(AdressObject ao : adressObjects) {
            if(adressed_is_person(ao)) {
                imprint.setAdress(ao.getAdressed() + "\n" + ao.getStreet() + "\n" + ao.getPlz_location() + "\n");
                imprint.setAddress_contains_name(true);
            }
        }

        //set address and vertreter
        if (firm_found) {
            imprint.setAdress("UNBEKANNTE SITUATION");
            ArrayList<MutablePair<VertretungsberechtigterObject, AdressObject>> adress_vertreter_pair = new ArrayList<>();
            ArrayList<AdressObject> adressObjects_temp = (ArrayList<AdressObject>) adressObjects.clone();

            for (VertretungsberechtigterObject vo : vertretungsberechtigterObjects) {
                AdressObject closest_adress = null;
                int closest_distance = elements.size();
                for (AdressObject ao : adressObjects_temp) {
                    System.out.println("--- Addressreport: ");
                    //if (ao.isFirm_indication()) {
                        ao.report_adress();
                        int distance = vo.getName_element_index() - ao.getPlz_element_index();
                        System.out.println("Distanz: " + distance);
                        //distance < closest_distance --> ao is closer, distance >= 0 --> ao is listed before vo
                        if(distance < closest_distance && distance >= 0 ) {
                            closest_adress = ao;
                        }
                   // } else {
                    //    continue;
                   // }
                }
                adress_vertreter_pair.add(new MutablePair<VertretungsberechtigterObject, AdressObject>(vo, closest_adress));
                adressObjects_temp.remove(closest_adress);
                System.out.println("Closest address object: " + closest_adress);
            }
            ArrayList<MutablePair<VertretungsberechtigterObject, AdressObject>> adress_vertreter_pair_temp = (ArrayList<MutablePair<VertretungsberechtigterObject, AdressObject>>) adress_vertreter_pair.clone();
            boolean main_address_set = false;
            System.out.println("----------- Pairs found: ");
            for (MutablePair<VertretungsberechtigterObject, AdressObject> mp : adress_vertreter_pair_temp) {
                try {
                    System.out.println("Vertreter: " + mp.getKey().getFound_names());
                    System.out.println("Adress: ");
                    mp.getValue().report_adress();
                    if(!main_address_set) {
                        imprint.setVertreter(mp.getKey().getVertretungs_indikatoren().toString().replace("[", "").replace("]", "")+ ": " +mp.getKey().getFound_names().toString().replace("[", "").replace("]", ""));
                        imprint.setHas_vertreter(true);
                        imprint.setAdress(mp.getValue().getAdressed() + "\n" + mp.getValue().getStreet() +"\n"+ mp.getValue().getPlz_location() + "\n");
                        imprint.setMain_address(mp.getValue());
                        main_address_set = true;
                        int addressed_element_index = elements.indexOf(mp.getValue().getAdressed_element());
                        ArrayList<String> rechtsform_ao = findRechtsform(mp.getValue().getAdressed());
                        imprint.setRechtsform(rechtsform_ao.toString());
                    }
                } catch (NullPointerException e) {
                    //NO ADDRESS FOUND IN THE PAIR
                    adress_vertreter_pair.remove(mp);
                    continue;
                }
            }

        } else if(!name_found) {
            boolean just_one_valid_adress = false;
            for (AdressObject ao : adressObjects) {
                if (just_one_valid_adress && ao.getIs_valid_address()) {
                    just_one_valid_adress = false;
                    break;
                } else if(!just_one_valid_adress && ao.getIs_valid_address()) {
                    just_one_valid_adress = true;
                }
            }

            if(adressObjects.size() == 1 || just_one_valid_adress) {
                AdressObject ao = adressObjects.get(0);
                adressObjects.get(0).report_adress();
                imprint.setAdress(ao.getAdressed() + "\n" + ao.getStreet() + "\n" + ao.getPlz_location() + "\n");
            } else if(adressObjects.size() > 1) {
                System.out.println("ADDRESS DOES NOT GET HANDLED RIGHT NOW -- NO FIRM NO NAME MULTIPLE ADDRESSES");
                System.out.println("Adressobjects size: " + adressObjects.size());
                imprint.setMultiple_addresses_no_main_address(true);
                imprint.setAdress("MEHRERE GLEICHWERTIGE ADDRESSEN");
            } else {
                imprint.setAdress("NO VALID ADDRESSES FOUND");
            }

            if(vertretungsberechtigterObjects.size() > 0) {
                imprint.setHas_vertreter(true);
                if (vertretungsberechtigterObjects.size() > 1) {
                    String vertreter = "Es wurden mehrmals Vertreter erkannt: \n";
                    for (VertretungsberechtigterObject vo : vertretungsberechtigterObjects) {
                        vertreter += "Possible Vertreter:"+ "Rolle: " + vo.getVertretungs_indikatoren().toString().replace("[", "").replace("]", "") +" Namen: " + vo.getFound_names().toString().replace("[", "").replace("]", "") + "\n";
                    }
                    imprint.setVertreter(vertreter);
                } else {
                    String rollen_indikatoren = vertretungsberechtigterObjects.get(0).getVertretungs_indikatoren().toString().replace("[", "").replace("]", "");
                    String vertreter_namen = vertretungsberechtigterObjects.get(0).getFound_names().toString().replace("[", "").replace("]", "");
                    imprint.setVertreter("Rolle: " + rollen_indikatoren +" Namen: " + vertreter_namen + "");
                }

            }
        }

        //set has_mail and mail
        System.out.println("----- Mails");
        if(mails.size() > 0) {
            imprint.setHas_email_address(true);
        }
        imprint.setMails(mails);
        for (String mail : mails){
            System.out.println(mail);
        }

        //set has form and contactform
        System.out.println("----- Kontaktformular");
        if(contactforms.size() > 0 ) {
            imprint.setHas_contact_form(true);
        }
        imprint.setContactforms(contactforms);

        for (String form_link : contactforms) {
            System.out.println(form_link);
        }

        //set aufsichtsbehörde
        System.out.println("------ Aufsichtsbehörde");
        imprint.setContains_aufsichtsbehoerde_indicator(aufsichtsObject.isHas_indicator());
        for(Element e : aufsichtsObject.getElements_with_structural_indicator()) {
            System.out.println("behoerde: " + aufsichtsObject.getAufsichtebehoerde());
            int closest_distance = elements.size();
            AdressObject closest_adressObject = null;
            System.out.println(e);
            System.out.println(elements.indexOf(e));
            int aufsichts_index = elements.indexOf(e);
            for (AdressObject ao : adressObjects) {
                int adressObject_index = elements.indexOf(ao.getAdressed_element());
                int distance = adressObject_index - aufsichts_index;
                if(distance < closest_distance && distance >= 0) {
                    closest_distance = distance;
                    closest_adressObject = ao;
                }
            }
            try {
                aufsichtsObject.setAufsichtebehoerde(closest_adressObject.getAdressed() + "\n" + closest_adressObject.getStreet() + "\n" + closest_adressObject.getPlz_location());
                closest_adressObject.report_adress();
                imprint.setAufsichtsbehoerde(aufsichtsObject.report());

            } catch (NullPointerException exception) {
                //No valid adress close to indicator
                System.out.println("Can not find corresponding adress");
            }

        }

        //set chamber indicator and chambers
        System.out.println("------ Gefundene Kammern");
        boolean chamber_job_indicator_found = false;
        boolean has_chambers = false;
        ArrayList<String> berufsbezeichnungen = new ArrayList<String>();
        for(ChamberObject co : chamberObjects) {

            if(co.getBerufsbezeichnung().size() > 0) {
               chamber_job_indicator_found = true;
               berufsbezeichnungen.addAll(co.getBerufsbezeichnung());
            }
            if(co.getChamber_indicators().size() > 0) {
                has_chambers = true;
            }
        }
        imprint.setHas_chambererd_job_indicator(chamber_job_indicator_found);
        imprint.setHas_chamber_indicator(has_chambers);
        imprint.setBerufsbezeichnung(berufsbezeichnungen.toArray(new String[0]));
        ArrayList<String> chambers = new ArrayList<>();
        for (ChamberObject co : chamberObjects) {
            chambers.addAll(co.getValid_chambers());
        }
        imprint.setChambers(chambers.toArray(new String[0]));

        System.out.println("Anzahl an gefundenen Kammern: " + chamberObjects.size());
        System.out.println("Loop over Chamber objects: ");
        for (ChamberObject co : chamberObjects) {
            System.out.println(co.getChamber_indicators());
        }

        //set ust numbers
        System.out.println("----- Ust_numbers:");
        System.out.println("ust numbers size: " + ust_identificationNumbers.size());
        boolean has_ust_numbers = ust_identificationNumbers.size() > 0;
        imprint.setHas_ust_numbers(has_ust_numbers);
        if(has_ust_numbers) {
            imprint.setUst_number(ust_identificationNumbers.get(0).getUst_number());
        }

        //set editorial indicator
        System.out.println("----- Redaktionsinfo:");
        System.out.println("redaktionsobjects size: " + redaktionsObjects.size());
        boolean has_redaktions_indikator = redaktionsObjects.size() > 0;
        imprint.setHas_editorial_indicator(has_redaktions_indikator);

        //set editor info
        System.out.println("----- Editorinfo:");
        System.out.println("Editorinfoobjects size: " + editorInfoObjects.size());
        String editors = "";
        for (EditorInfoObject eio : editorInfoObjects) {
            editors += "Rolle: " + eio.getRole() + "\nName: " + eio.getName() + "\n";
            //System.out.println("Rolle: " + eio.getRole());
            //System.out.println("Name: " + eio.getName());
        }
        imprint.setEditors(editors);
        System.out.println(editors);

        //Set berufsordnung
        //Only need to e present if a chamber is mentioned
        System.out.println("----- Berufsordnung");
        for (Element e : berufsordnungsObject.getElements_with_nennung()) {
            System.out.println("Nennung: " + e.text());
        }
        String berufsrechtliche_regelung = "Regelungen: \n";
        for (Element e : berufsordnungsObject.getElements_with_regelung()) {
            berufsrechtliche_regelung += e.text() + "\n" + "---------" + "\n";
            System.out.println("Regelung: " + e.text());
        }
        imprint.setBerufsrechliche_regelung(berufsrechtliche_regelung);


        // Setting register infos
        String register_info = "";
        boolean has_register_info = registerObjects.size() > 0;
        imprint.setHas_register_info_indicator(has_register_info);
        if(has_register_info) {
            RegisterObject ro = registerObjects.get(0);
            register_info = ro.getRegister_court() + " " + ro.getRegister_number();
        }
        imprint.setRegister_info(register_info);


        imprint.setAdressObjects(adressObjects);
        imprint.setVertretungsberechtigterObjects(vertretungsberechtigterObjects);
        imprint.setMails(mails);
        imprint.setContactforms(contactforms);
        imprint.setNumericContactObjects(numericContactObjects);
        imprint.setAufsichtsObject(aufsichtsObject);
        imprint.setRedaktionsObjects(redaktionsObjects);
        imprint.setEditorInfoObjects(editorInfoObjects);
        imprint.setUst_identificationNumbers(ust_identificationNumbers);
        imprint.setChamberObjects(chamberObjects);
        imprint.setBerufsordnungsObject(berufsordnungsObject);
        imprint.setRegisterObjects(registerObjects);
        imprint.setRechtsformObjects(rechtsformObjects);


        return imprint;
    }

    public ImprintExtractor(Document doc) {
        this.current_document = doc;
        this.elements = current_document.getAllElements();
        Document preprocessed_document = Preprocessor.preprocess_document(doc);
        this.fieldExtractor = new FieldExtractor(preprocessed_document);

    }

    public ImprintExtractor() {
        this.fieldExtractor = new FieldExtractor(null);
    }

    public void setDocument(Document doc) {
        this.current_document = doc;
        elements = current_document.getAllElements();
        Document preprocessed_document = Preprocessor.preprocess_document(doc);
        this.fieldExtractor.setCurrent_document(preprocessed_document);
    }




}
