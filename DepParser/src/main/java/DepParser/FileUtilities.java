package DepParser;

import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.TreeMap;

/**
 * Created by Marco Corona on 13/04/2017.
 */


public class FileUtilities {



    public static String getFileName(File file){
        String name = FilenameUtils.getBaseName(file.getName());
        return name;
    }




    public static void writeString(File file, String s) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(s);
        writer.close();
    }



    public static String makeText(File file) throws IOException {

        FileReader reader = new FileReader(file);
        BufferedReader buffreader = new BufferedReader(reader);
        String line ;
        String text="";
        while((line = buffreader.readLine())!=null){
            text +=line;
        }
        return text;
    }

}
