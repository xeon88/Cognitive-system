package Dictionary;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.TreeMap;

/**
 * Created by Marco Corona on 13/04/2017.
 * Collection of function to work with text document
 */
public class FileUtilities {

    public static String getTextFromFile( File file) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file), "ISO-8859-1")
        );
        String line ;
        String text="";

        while((line = reader.readLine())!=null){
            text += line + "\n";
        }

        return text;
    }


    public static void CreateNewDirectory(File directory) throws IOException {

        if(directory.isDirectory()){
            String path = directory.getAbsolutePath();
            File[] subdir = directory.listFiles();
            for(File sub : subdir){
                if(sub.isDirectory()){
                    File [] files = sub.listFiles();
                    for(File source : files){
                        String text = getTextFromFile(source);
                        String subName = sub.getName();
                        String fileName = source.getName();
                        String fullPath = path + File.separator + subName + "_" + fileName;
                        String newPath = StringUtils.replace(fullPath,".","_");
                        newPath += ".txt";
                        System.out.println("Path created :" + newPath);
                        File destination = new File(newPath);
                        writeString(destination,text);
                    }
                }
            }


        }


    }

    public static String getFileName(File file){

        if(file==null) return null;
        String name = FilenameUtils.getBaseName(file.getName());
        return name;
    }




    public static String getCategoryfromLabel(String label){
        String category = "";

        String [] split = label.split("_");
        if(split[0].equals("test")){
            String [] tmp = new String [split.length-1];
            for(int i = 0; i<split.length-1;i++){
                tmp[i]=split[i+1];
            }
            split=tmp;
        }
        if(split.length==2){
            category = split[0]; // first part of name
        }
        if(split.length>2){
            for(int j = 0 ; j<split.length-2; j++){
                category+= split[j] + "_";
            }
            category+=split[split.length-2];
        }
        return category;
    }

    public static void makeWordFile(TreeMap<String, Feature> words) throws IOException {
        String path = "Rocchio/src/main/resources/output.txt";
        File file = new File(path);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for ( Feature s: words.values()) {
            writer.write(s.getWord() + "\n");
        }
        writer.close();
    }


    public static void writeString(File file, String s) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String [] split = s.split("\n");
        for(int i =0 ; i<split.length; i++){
            writer.write(split[i]);
            writer.newLine();
        }
        writer.close();
    }



}
