package impressumscrawler.extraction_component.FieldExtraction;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import impressumscrawler.datatypes.*;
import impressumscrawler.ner.Pipeline;
import javafx.util.Pair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class FieldExtractor_handler {


    public static void main(String[] args) {
        boolean all_address = false;
        boolean single_address = false;
        boolean initial_address = false;

        boolean ust_all = false;
        boolean ust_single = false;

        boolean vertreter_all = false;
        boolean vertreter_single = false;

        boolean register_all = false;

        boolean verkammerte_berufe = false;

        boolean aufsichtsbehoerde_all = false;
        boolean aufsichtsbehoerde_single = false;

        boolean redaktion_all = false;

        boolean editor_info_all = true;

        boolean ner_test = false;


        // boolean testing = true;

        if(ner_test) {
            StanfordCoreNLP ner_tagger = Pipeline.getNLP();

            String text = "a) Berufsbezeichnung zuständige Kammer sowie Aufsichtsbehörde gem. § 5 Abs. 1 Nr. 3 und Nr. 5 a TMG: Rechtsanwalt Die Berufsbezeichnung wurde in der Bundesrepublik Deutschland (Bundesland: Nordrhein-Westfalen) verliehen. Alle Rechtsanwälte der Kanzlei ZIEGLER &amp; KOLLEGEN sind nach dem Recht der Bundesrepublik zugelassen und Mitglieder der Rechtsanwaltskammer Düsseldorf\n" +
                    " Freiligrathstraße 25\n" +
                    "40479 Düsseldorf";
            //String text = "Berufshaftpflichtversicherung Notare sind nach den Bestimmungen der Bundesnotarordnung verpflichtet\n" +
            //        " eine Berufshaftpflichtversicherung zu unterhalten. Alle Anwaltsnotare der Sozietät ZIEGLER &amp; KOLLEGEN bis auf RA Gregor Ziegler sind in ihrer Eigenschaft sowohl als Rechtsanwälte als auch als Notar derzeit versichert bei der Allianz Versicherungs-AG\n" +
            //        "10900 Berlin";

            CoreDocument coreDocument = new CoreDocument(text);
            ner_tagger.annotate(coreDocument);
            List<CoreLabel> coreLabels = coreDocument.tokens();

            for(CoreLabel coreLabel : coreLabels) {
                String ner = coreLabel.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                String original_text = coreLabel.originalText();
                System.out.println(original_text + " = " + ner);
                //if(ner.equals("PERSON")) System.out.println(coreLabel.originalText() + " = " + ner);
            }
        }

        List<String> filenames = filenames();

        ArrayList<Pair<String, Document>> documents = createListOfDocuments();


        ArrayList<MutablePair<String, ArrayList<AdressObject>>> extracted_addresses = new ArrayList<MutablePair<String, ArrayList<AdressObject>>>();
        Document doc = null;
        FieldExtractor fieldExtractor = new FieldExtractor(doc);

        if (initial_address) {
            try {
                File input = new File(System.getProperty("user.dir") + "/src/data/xml/XML_concrete/imprints_source/thyssenkrupp.com/thyssenkrupp.com.html");
                doc = Jsoup.parse(input, "UTF-8", "http://thyssenkrupp.com/");
                //doc = Jsoup.parse("<div>hallo welt<div><div>Hier gibt es nichts.</div><div></div></div> <div>Hier gibt es nichts2.</div> <p>This is a paragraph</p>59592</div>");
                fieldExtractor.setCurrent_document(doc);

            } catch (Exception e) {
                e.printStackTrace();
            }

            fieldExtractor.findPLZandCity();

        }
        if(all_address == true) {

            //List<String> filenames = filenames();
            System.out.println("-------------------------------------");
            System.out.println("-------------------------------------");
            System.out.println("-------------------------------------");
            System.out.println("-------------------------------------");
            for (String filename : filenames) {
                System.out.println("File: " + filename);
                try {
                    Document document = openDoc(filename);
                    MutablePair<String, ArrayList<AdressObject>> adresses = new MutablePair(filename, null);
                    //MutablePair<String, ArrayList<AdressObject>> rejected_adresses = new MutablePair(filename, null);
                    fieldExtractor.setCurrent_document(document);
                    //adresses.setRight(((MutablePair<ArrayList<AdressObject>, ArrayList<AdressObject>>) fieldExtractor.findPLZandCity()).getLeft());
                    //adresses.setRight(((MutablePair<ArrayList<AdressObject>, ArrayList<AdressObject>>) fieldExtractor.findPLZandCity()).getRight());
                    extracted_addresses.add(adresses);
                } catch (FileNotFoundException exception) {
                    System.out.println("File cannot be found...");
                } catch (IOException exception) {
                    System.out.println("IO Exception found...");
                } catch (StringIndexOutOfBoundsException e) {
                    System.out.println("String index out ofbounds exception found...");
                }
                System.out.println("-------------------------------------");
                System.out.println("-------------------------------------");
            }

            for (MutablePair<String, ArrayList<AdressObject>> a : extracted_addresses) {
                System.out.println("----------------");
                System.out.println("Filename: " + a.getLeft());
                if(a.getRight() == null) System.out.println("NO ADRESS FOUND");
                else
                for (AdressObject ao : a.getRight()) {
                    System.out.println("////");
                    //ao.report_adress();
                    //ao.report_adress_and_indezes_indicators();
                    ao.report_adress_with_indicator();
                }
            }

        }
        if(single_address == true) {
            String[] files = {"thyssenkrupp.com.xml", "aldi-sued.de.xml", "buegelservicelandau.de.rs.xml", "copyshop-duisburg.de.xml", "uni-due.de.xml", "holzblog.schule.xml", "igus.de.xml"};
            MutablePair<String, ArrayList<AdressObject>> results = (MutablePair<String, ArrayList<AdressObject>>) test_doc(files[1], fieldExtractor);
            System.out.println("-------------------------------------");
            System.out.println("-------------------------------------");
            System.out.println("-------------------------------------");
            System.out.println("-------------------------------------");
            System.out.println("Addresses of file: " + results.getLeft());
            for (AdressObject ao : results.getRight()) {
                System.out.println("is valid adress: " + ao.getIs_valid_address());
                ao.report_adress_and_indezes();
                System.out.println("-------------------------------------");

            }
        }

        if(ust_all) {
            System.out.println("-------------------------------------");
            System.out.println("-------------------------------------");

            for(Pair<String, Document> d : documents) {
                System.out.println("Finding all wst in: " + d.getKey());
                fieldExtractor.setCurrent_document(d.getValue());
                fieldExtractor.findUst();
            }
        }

        if (ust_single) {
            String[] files = {"thyssenkrupp.com.xml", "aldi-sued.de.xml", "buegelservicelandau.de.rs.xml", "copyshop-duisburg.de.xml", "uni-due.de.xml", "holzblog.schule.xml", "igus.de.xml"};
        }

        if(vertreter_all) {
            System.out.println("-------------------------------------");
            System.out.println("-------------------------------------");
            ArrayList<MutablePair<String, ArrayList<VertretungsberechtigterObject>>> results = new ArrayList<MutablePair<String, ArrayList<VertretungsberechtigterObject>>>();

            for(Pair<String, Document> d : documents) {
                MutablePair<String, ArrayList<VertretungsberechtigterObject>> doc_results = new MutablePair<String, ArrayList<VertretungsberechtigterObject>>();
                doc_results.setLeft(d.getKey());
                System.out.println("-------------------------------------");
                System.out.println("-------------------------------------");
                System.out.println("---------------- Finding all verterter und indikatoren in: " + d.getKey());

                fieldExtractor.setCurrent_document(d.getValue());
                doc_results.setRight(fieldExtractor.findVertretungsberechtigten());
                results.add(doc_results);
            }
            System.out.println("Results of the vertretungsberechtigten analyse: ");
            for(MutablePair<String , ArrayList<VertretungsberechtigterObject>> mp : results) {
                System.out.println("--------- filename: " + mp.getLeft());
                for (VertretungsberechtigterObject vo : mp.getRight()) {
                    System.out.println("Indikatoren: " + vo.getVertretungs_indikatoren());
                    System.out.println("Found names: " + vo.getFound_names());
                }
            }
            System.out.println(results);
        }

        if(vertreter_single) {
            System.out.println("-------------------------------------");
            System.out.println("-------------------------------------");
            String[] files = {"thyssenkrupp.com.xml", "aldi-sued.de.xml", "buegelservicelandau.de.rs.xml", "copyshop-duisburg.de.xml", "uni-due.de.xml", "holzblog.schule.xml", "igus.de.xml", "einsteigen.jetzt.xml"};
            try{
                Document document = openDoc(files[4]);
                fieldExtractor.setCurrent_document(document);
                fieldExtractor.findVertretungsberechtigten();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(vertreter_single) {
            System.out.println("-------------------------------------");
            System.out.println("-------------------------------------");
            String[] files = {"thyssenkrupp.com.xml", "aldi-sued.de.xml", "buegelservicelandau.de.rs.xml", "copyshop-duisburg.de.xml", "uni-due.de.xml", "holzblog.schule.xml", "igus.de.xml", "einsteigen.jetzt.xml"};
            try{
                Document document = openDoc(files[6]);
                fieldExtractor.setCurrent_document(document);
                fieldExtractor.findVertretungsberechtigten();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(register_all) {
            boolean report = true;
            ArrayList<MutablePair<String, ArrayList<RegisterObject>>> results = new ArrayList<>();
            for(Pair<String, Document> d : documents) {

                System.out.println("-------------------------------------");
                System.out.println("-------------------------------------");
                System.out.println("---------------- Finding all verterter und indikatoren in: " + d.getKey());

                fieldExtractor.setCurrent_document(d.getValue());
                results.add(new MutablePair<String, ArrayList<RegisterObject>>(d.getKey() ,fieldExtractor.findRegister_Registernummer()));

            }
            if(report) {
                for(MutablePair<String, ArrayList<RegisterObject>> mp : results) {
                    System.out.println("------------------ File: "+mp.getLeft());
                    for (RegisterObject r : mp.getRight()) {
                        r.reportRegisterCourtAndNumber();
                    }
                }
            }
        }


        if(verkammerte_berufe) {
            String[] verkammerte_berufe_webseiten = {"arkaden-apotheke", "kleintierpraxis-fuhrmann", "pkf-fasselt", "praxis-am-brunnen", "thiemer", "zieglerundkollegen"};
            //String[] verkammerte_berufe_webseiten = {"thiemer"};
            ArrayList<Pair<String, Document>> beruf_documents = createBerufDocumentList(verkammerte_berufe_webseiten);
            ArrayList<MutablePair<String, ArrayList<ChamberObject>>> results_chamber = new ArrayList<MutablePair<String, ArrayList<ChamberObject>>>();
            for (Pair<String, Document> b_pair : beruf_documents) {
                System.out.println("--------------- Analysing: " + b_pair.getKey());
                fieldExtractor.setCurrent_document(b_pair.getValue());
                ArrayList<ChamberObject> chambers = fieldExtractor.findVerkammerteBerufe();
                results_chamber.add(new MutablePair<>(b_pair.getKey(), chambers));
            }
            if(false) {
                for (Pair<String, Document> d_pair : documents) {
                    System.out.println("--------------- Analysing: " + d_pair.getKey());
                    fieldExtractor.setCurrent_document(d_pair.getValue());
                    ArrayList<ChamberObject> chambers = fieldExtractor.findVerkammerteBerufe();
                    results_chamber.add(new MutablePair<>(d_pair.getKey(), chambers));
                }

            }

            for(MutablePair<String, ArrayList<ChamberObject>> mp : results_chamber) {
                System.out.println("---------- file: " + mp.getKey());
                for (ChamberObject co : mp.getValue()) {
                    co.report_debug();
                }
            }

        }

        if(aufsichtsbehoerde_all) {
            String[] verkammerte_berufe_webseiten = {"arkaden-apotheke", "kleintierpraxis-fuhrmann", "pkf-fasselt", "praxis-am-brunnen", "thiemer", "zieglerundkollegen"};
            ArrayList<Pair<String, Document>> beruf_documents = createBerufDocumentList(verkammerte_berufe_webseiten);
            for(Pair<String, Document> d : documents) {
                System.out.println("---------- "+ d.getKey());
                fieldExtractor.setCurrent_document(d.getValue());
                fieldExtractor.findZustaendigeAufsichtsbehoerde();
            }
            for (Pair<String, Document> beruf_pair : beruf_documents) {
                System.out.println("---------- " + beruf_pair.getKey());
                fieldExtractor.setCurrent_document(beruf_pair.getValue());
                fieldExtractor.findZustaendigeAufsichtsbehoerde();
            }
        }

        if(redaktion_all) {
            String[] verkammerte_berufe_webseiten = {"arkaden-apotheke", "kleintierpraxis-fuhrmann", "pkf-fasselt", "praxis-am-brunnen", "thiemer", "zieglerundkollegen"};
            ArrayList<Pair<String, Document>> beruf_documents = createBerufDocumentList(verkammerte_berufe_webseiten);
            for(Pair<String, Document> d : documents) {
                System.out.println("---------- "+ d.getKey());
                fieldExtractor.setCurrent_document(d.getValue());
                fieldExtractor.findRedaktionellerAngabe();
            }
            for (Pair<String, Document> beruf_pair : beruf_documents) {
                System.out.println("---------- " + beruf_pair.getKey());
                fieldExtractor.setCurrent_document(beruf_pair.getValue());
                fieldExtractor.findRedaktionellerAngabe();
            }
        }

        if(editor_info_all) {
            String[] verkammerte_berufe_webseiten = {"arkaden-apotheke", "kleintierpraxis-fuhrmann", "pkf-fasselt", "praxis-am-brunnen", "thiemer", "zieglerundkollegen"};
            ArrayList<Pair<String, Document>> beruf_documents = createBerufDocumentList(verkammerte_berufe_webseiten);
            ArrayList<NumericContactObject> nco = new ArrayList<>();
            for(Pair<String, Document> d : documents) {
                System.out.println("---------- "+ d.getKey());
                fieldExtractor.setCurrent_document(d.getValue());
                //fieldExtractor.findEditorInfo();
                nco.addAll(fieldExtractor.find_numeric_contact());
            }
            for (Pair<String, Document> beruf_pair : beruf_documents) {
                System.out.println("---------- " + beruf_pair.getKey());
                fieldExtractor.setCurrent_document(beruf_pair.getValue());
                //fieldExtractor.findEditorInfo();
                nco.addAll(fieldExtractor.find_numeric_contact());
            }
            for (NumericContactObject nc : nco) {
                System.out.println(nc.report());
            }
        }






    }

    private static ArrayList<Pair<String, Document>> createBerufDocumentList(String[] names) {
        ArrayList<Pair<String, Document>> documents = new ArrayList<>();
        for (String name : names ) {
            System.out.println("----- file name: " + name);
           String filepath = getBaseFilePath("verkammerte_berufe/website/" + name + "/Impressum.htm").replace("/", "\\");
            //Q:\Sciebo+\Uni\Selbstständigkeit und Co\Programming\Crawle_impressum\src\data\verkammerte_berufe\website\arkaden-apotheke\Impressum.htm
            // System.out.println("Document path: " + filepath);
           try {
               Document document = b_openDoc(filepath);
               documents.add(new Pair<String, Document>(name, document));
               System.out.println("Success at reading file!");

           } catch (FileNotFoundException exception) {
               System.out.println("File cannot be found...");
           } catch (IOException exception) {
               System.out.println("IO Exception found...");
           } catch (StringIndexOutOfBoundsException e) {
               System.out.println("String index out ofbounds exception found...");
           }
        }
        return documents;
    }

    private static ArrayList<Pair<String, Document>> createListOfDocuments() {
        ArrayList<Pair<String, Document>> documents = new ArrayList<>();
        List<String> filenames = filenames();
        for(String filename : filenames) {
            try {
                Document document = openDoc(filename);
                documents.add(new Pair<>(filename, document));
            } catch (FileNotFoundException exception) {
                System.out.println("File cannot be found...");
            } catch (IOException exception) {
                System.out.println("IO Exception found...");
            } catch (StringIndexOutOfBoundsException e) {
                System.out.println("String index out ofbounds exception found...");
            }
        }
        return documents;

    }

    private static Object test_doc(String filename, FieldExtractor fieldExtractor) {
        MutablePair<String, ArrayList<AdressObject>> adresses = new MutablePair(filename, null);
        try {
            Document document = openDoc(filename);
            fieldExtractor.setCurrent_document(document);
            adresses.setRight((ArrayList<AdressObject>) fieldExtractor.findPLZandCity());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return adresses;
    }

    private static Document b_openDoc(String file_path) throws FileNotFoundException, IOException {
        Document doc = null;
        File html_file = new File(file_path);
        doc = Jsoup.parse(html_file, "UTF-8", "http://");

        return doc;
    }


    private static Document openDoc(String file_name) throws FileNotFoundException, IOException {
        Document doc = null;
        File html_file = new File(getHtmlFilePath(file_name));
        doc = Jsoup.parse(html_file, "UTF-8", "http://");

        return doc;
    }

    private static String getBaseFilePath(String path) throws StringIndexOutOfBoundsException {
        String data_path = System.getProperty("user.dir") + "/src/data/" + path;
        //System.out.println("Presumed path: " + data_path);
        return data_path;
    }

    private static String getHtmlFilePath(String filename) throws StringIndexOutOfBoundsException {
        String html_path = System.getProperty("user.dir") + "/src/data/xml/XML_concrete/imprints_source/";
        String file_name_no_extension = filename.substring(0, filename.lastIndexOf("."));
        String html_path_file = html_path + file_name_no_extension + "/" + file_name_no_extension + ".html";
        return html_path_file;
    }

    private static List<String> filenames() {
        File dirPath = new File(System.getProperty("user.dir")+"/src/data/xml/XML_concrete");
        String contents[] = dirPath.list();
        List<String> filenames = new ArrayList<String>();
        System.out.println(contents);
        for (String pathname : contents) {
            System.out.println(pathname);
            filenames.add(pathname);
        }
        return filenames;
    }
}
