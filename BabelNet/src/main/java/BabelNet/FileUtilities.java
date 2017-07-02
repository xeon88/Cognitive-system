package BabelNet;

import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.TreeMap;

/**
 * Created by Marco Corona on 13/04/2017.
 */
public class FileUtilities {

    public static String getTextFile( File file) throws IOException {
        FileReader reader = new FileReader(file);
        BufferedReader buffreader = new BufferedReader(reader);
        String line ;
        String text="";

        while((line = buffreader.readLine())!=null){
            text += line + "\n";
        }

        return text;
    }


    public static String getFileName(File file){
        String name = FilenameUtils.getBaseName(file.getName());
        return name;
    }

    public static String getLabelFromFileName(File file){
        String label = "";
        String name = FilenameUtils.getBaseName(file.getName());
        String [] split = name.split("_");
        if(split.length==2){
            label = split[0]; // first part of name
        }
        if(split.length>2){
            for(int j = 0 ; j<split.length-1; j++){
                label+= split[j] + " ";
            }
        }
        return label;
    }


    public static String getCategoryfromLabel(String label){
        String category = "";

        String [] split = label.split("_");
        if(split.length==2){
            category = split[0]; // first part of name
        }
        if(split.length>2){
            for(int j = 0 ; j<split.length-1; j++){
                category+= split[j] + " ";
            }
        }
        return category;
    }

    public static void makeWordFile(TreeMap<String, Features> words) throws IOException {
        String path = "Rocchio/src/main/resources/output.txt";
        File file = new File(path);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for ( Features s: words.values()) {
            writer.write(s.getWord() + "\n");
        }
        writer.close();
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
