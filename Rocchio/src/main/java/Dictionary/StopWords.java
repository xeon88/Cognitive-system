package Dictionary;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.*;
import java.util.TreeSet;

/**
 * Created by Marco Corona on 12/04/2017.
 * Collect and manage all founded stopwords
 */
public class StopWords {

    public final static String pathStopWords = "Rocchio/src/main/resources/stopwords/stopwords_en.txt";
    //public final static String pathStopWordsBackUp = "WordNet/src/main/resources/stopwords/stopwords_en_new.txt";
    private static StopWords stopWords;
    private TreeSet<String> words;



    public static StopWords getInstance(){
        if(stopWords==null){
            stopWords = new StopWords();
        }
        return stopWords;
    }


    private StopWords(){
        this.words = load();
        System.out.println("stopwords loaded ... " + words.size());
    }


    private TreeSet load(){
        TreeSet stopwords = null;
        try {
            stopwords = new TreeSet();
            BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                        new FileInputStream(pathStopWords), "ISO-8859-1")
            );
            String buffer = "";
            while ((buffer=reader.readLine())!=null){
                String stop = StringEscapeUtils.unescapeHtml4(buffer);
                stopwords.add(stop);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopwords;
    }


    public boolean isStopWords(String s){
        return words.contains(s.toLowerCase());
    }

    public void addStopWords(String stop){
        words.add(stop.toLowerCase());
    }

    public int size(){
        return words.size();
    }

    public void export(){
        String dump = "";
        File stopwords = new File(pathStopWords);
        for(String stop : words){
            dump += stop + "\n";
        }
        try {
            FileUtilities.writeString(stopwords,dump);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
