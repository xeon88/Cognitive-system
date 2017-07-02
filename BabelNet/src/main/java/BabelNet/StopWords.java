package BabelNet;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeSet;

/**
 * Created by Marco Corona on 12/04/2017.
 */
public class StopWords {

    public final static String pathStopWords = "WordNet/src/main/resources/stopwords";

    public static TreeSet load(){
        TreeSet stopwords;
        try {
            stopwords = new TreeSet();
            File dir = new File(pathStopWords);
            File[] files = dir.listFiles();

            for(int i=0 ; i<files.length ; i++){

                String fileName = files[i].getParent() + File.separator +files[i].getName();
                BufferedReader reader = new BufferedReader(new FileReader(fileName));
                String buffer = "";
                while ((buffer=reader.readLine())!=null){
                    String stop = StringEscapeUtils.unescapeHtml4(buffer);
                    stopwords.add(stop);
                }
            }

        }
        catch (IOException io){
            return null;
        }

        return stopwords;
    }
}
