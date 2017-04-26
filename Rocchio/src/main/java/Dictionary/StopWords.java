package Dictionary;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Marco Corona on 12/04/2017.
 */
public class StopWords {

    public final static String pathStopWords = "Rocchio/src/main/resources/stopwords_it.txt";

    public static String[] load(){
        String [] stopwords;
        try {
            stopwords = null;
            ArrayList<String> tmp = new ArrayList<String>();
            BufferedReader reader = new BufferedReader(new FileReader(pathStopWords));
            String buffer = "";
            while ((buffer=reader.readLine())!=null){
                String stop = StringEscapeUtils.unescapeHtml(buffer);
                //stop = StringEscapeUtils.escapeXml(stop);
                tmp.add(stop);
                //System.out.println("buffer : " + buffer);
                //System.out.println("stopword : " + stop);
            }
            stopwords =tmp.toArray(new String[tmp.size()]);
        }
        catch (IOException io){
            return null;
        }

        return stopwords;
    }
}
