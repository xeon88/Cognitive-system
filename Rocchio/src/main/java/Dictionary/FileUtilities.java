package Dictionary;

import com.google.common.io.Files;
import org.apache.commons.io.FilenameUtils;

import java.io.*;

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



}
