package impressumscrawler.extraction_component.ImprintExtraction;


import impressumscrawler.datatypes.BerufsordnungsObject;
import impressumscrawler.datatypes.ImprintObject;
import impressumscrawler.datatypes.RechtsformObject;
import impressumscrawler.datatypes.RegisterObject;
import impressumscrawler.extraction_component.FieldExtraction.FieldExtractor;
import javafx.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ImprintExtractor_handler {

    public static void main(String[] args) {

        boolean all = true;
        boolean single = false;

        ArrayList<ImprintObject> imprintObjects = new ArrayList<ImprintObject>();

        ImprintExtractor imprintExtractor = new ImprintExtractor();

        ArrayList<Pair<String, Document>> doc_list = createListOfDocuments();
        ArrayList<BerufsordnungsObject> ros = new ArrayList<>();

        String[] verkammerte_berufe_webseiten = {"arkaden-apotheke", "kleintierpraxis-fuhrmann", "pkf-fasselt", "praxis-am-brunnen", "thiemer", "zieglerundkollegen"};
        //String[] verkammerte_berufe_webseiten = {"thiemer"};
        //String[] verkammerte_berufe_webseiten = {"pkf-fasselt"};
        //String[] verkammerte_berufe_webseiten = {"kleintierpraxis-fuhrmann"};
        ArrayList<Pair<String, Document>> beruf_documents = createBerufDocumentList(verkammerte_berufe_webseiten);

        if(all) {
            /*
            for (Pair<String, Document> p : doc_list) {
                System.out.println("------------------------ " + p.getKey());
                imprintExtractor.setDocument(p.getValue());
                ImprintObject imprintObject = imprintExtractor.analyzeDoc();
                imprintObject.setDocTitle(p.getKey());
                imprintObjects.add(imprintObject);
                //imprintExtractor.setDocument(p.getValue());
                //imprintObjects.add(imprintObject);
            } */


            FieldExtractor fieldExtractor = new FieldExtractor(new Document(""));

            for (Pair<String, Document> p : beruf_documents) {
                System.out.println("------------------------ " + p.getKey());
                System.out.println(p.getKey());
                imprintExtractor.setDocument(p.getValue());
                ImprintObject imprintObject = imprintExtractor.analyzeDoc();
                imprintObject.setDocTitle(p.getKey());
                imprintObjects.add(imprintObject);
                //imprintObjects.add(imprintExtractor.analyzeDoc());
                //fieldExtractor.setCurrent_document(p.getValue());
                //ros.add(fieldExtractor.findBerufsordnung());
            }


            for (Pair<String, Document> p : beruf_documents) {
                fieldExtractor.setCurrent_document(p.getValue());
                fieldExtractor.findPLZandCity();
            }

        }

        if(single) {
            int index = 0;
            imprintExtractor.setDocument(doc_list.get(index).getValue());
            imprintObjects.add(imprintExtractor.analyzeDoc());
        }

        for (ImprintObject io : imprintObjects) {
            System.out.println(io.report());
        }
        for(ImprintObject io : imprintObjects) {
            System.out.println("----------------");

            System.out.println("Doc title: " + io.getDocTitle());
           try {
               System.out.println(io.getAdress());
           } catch (NullPointerException exception) {
               System.out.println("---- NO MAIN ADDRESS SET ----");
           }
        }
        //System.out.println(beruf_documents);
/*
        System.out.println("--- Berufungsobject: ");
        for (BerufsordnungsObject ro : ros) {
            System.out.println(ro.report());
            System.out.println(ro.getElements_with_regelung());
        }

 */
        /*
        for (Pair<String, Document> p : beruf_documents) {
            System.out.println(p.getKey());
        }*/
        System.out.println("Ende");

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

    private static String getBaseFilePath(String path) throws StringIndexOutOfBoundsException {
        String data_path = System.getProperty("user.dir") + "/src/data/" + path;
        //System.out.println("Presumed path: " + data_path);
        return data_path;
    }

    private static Document b_openDoc(String file_path) throws FileNotFoundException, IOException {
        Document doc = null;
        File html_file = new File(file_path);
        doc = Jsoup.parse(html_file, "UTF-8", "http://");

        return doc;
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

    private static Document openDoc(String file_name) throws FileNotFoundException, IOException {
        Document doc = null;
        File html_file = new File(getHtmlFilePath(file_name));
        doc = Jsoup.parse(html_file, "UTF-8", "http://");

        return doc;
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
