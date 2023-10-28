package impressumscrawler.util;

import impressumscrawler.datatypes.ImprintObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Util {
    /**
     * Takes a string and writes it as a ".log" file somewhere into the "/src/" folder.
     * @param path      Path inside of the project where the string shall be written to.
     * @param content   The String that shall be written into a logfile
     */
    public static void writeStringToPath(String path, String content) {
        String project_path = System.getProperty("user.dir") + "/src/";
        String file_path = project_path + path;

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file_path+".log"));
            writer.append(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Similar to writeStringTopath, but takes an ImprintObject as parameter.
     * @param path          Path somewhere in the project.
     * @param imprintObject ImprintObject out of which the information needs to be extracted
     */
    public static void writeImprintObjectToPath(String path, ImprintObject imprintObject) {
        String project_path = System.getProperty("user.dir") + "/src/";
        String file_path = project_path + path;

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file_path+".log"));
            String object_report = imprintObject.report();
            writer.append(object_report);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes a file and reads out the contents line by line, for each line an element is
     * added to an arraylist
     * @param file  File the line by line Array is written into
     * @return An ArrayList with the rows of a File as Elements
     */
    public static ArrayList<String> getArrayListFromFile(File file) {
        Scanner scanner = null;
        ArrayList<String> links = new ArrayList<>();
        try {
            scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                links.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
        return links;
    }

    /**
     * Wrapper for getArrayListFromFile. Takes a path to a file, which is turned into the file
     * @param path Path to a file which should be transformed into an ArrayList
     * @return An ArrayList with the rows of a File as Elements
     */
    public static ArrayList<String> getArrayListFromFile(String path) {
        String project_path = System.getProperty("user.dir") + "/src/";
        String filePath = project_path + path;
        File file = new File(filePath);
        return getArrayListFromFile(file);
    }

    /**
     * Reads out the config Files, as a String Array
     * @param fileName name of the config file that needs to be extracted
     * @return A String Array that is
     */
    public static String[] getConfigStringArrayFromFile(String fileName) {
        String project_path = System.getProperty("user.dir") + "/src/config/";
        String filePath = project_path + fileName;
        File file = new File(filePath);
        ArrayList<String> as_list = getArrayListFromFile(file);
        String[] as_array = as_list.toArray(new String[0]);
        return as_array;
    }


}
