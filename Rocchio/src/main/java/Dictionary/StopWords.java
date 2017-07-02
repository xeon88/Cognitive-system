package Dictionary;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.*;
import java.util.TreeSet;

/**
 * Created by Marco Corona on 12/04/2017.
 */
public class StopWords {

    public final static String pathStopWords = "WordNet/src/main/resources/stopwords";
    private static StopWords stopWords;
    private TreeSet words;



    public static StopWords getInstance(){
        if(stopWords==null){
            stopWords = new StopWords();
        }
        return stopWords;
    }


    private StopWords(){
        this.words = load();
    }


    private TreeSet load(){
        TreeSet stopwords;
        try {
            stopwords = new TreeSet();
            File dir = new File(pathStopWords);
            File[] files = dir.listFiles();

            for(int i=0 ; i<files.length ; i++){

                String fileName = files[i].getParent() + File.separator +files[i].getName();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                        new FileInputStream(fileName), "ISO-8859-1")
                        );
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


    public boolean isStopWords(String s){
        return words.contains(s.toLowerCase());
    }

}
