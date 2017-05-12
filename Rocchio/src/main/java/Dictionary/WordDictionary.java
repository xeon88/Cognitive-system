package Dictionary;

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.io.JsonWriter;

import java.io.*;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Marco Corona on 05/04/2017.
 * SingleTon class containing all features information
 */
public class WordDictionary {

    private TreeMap<String,Features> wordsMap;
    private File directory;
    private TreeSet stopwords;
    private final String splitRegex = "(\\t|\\s|\\(|\\)|\\+|’|')+";
    private static WordDictionary instance;



    public static WordDictionary getInstance(File dir){
        if(instance==null){
            instance = new WordDictionary(dir);
        }
        return instance;
    }

    private WordDictionary(File dir){
        if(dir.isDirectory()) {
            this.directory = dir;
            this.stopwords  = StopWords.load();
            this.wordsMap = new TreeMap<String, Features>();
        }
        else{
            throw new IllegalArgumentException("Missing directory");
        }
    }


    public TreeMap<String,Features> getWordsMap(){
        return wordsMap;
    }

    public void setWordsMap(TreeMap<String,Features> map){
        this.wordsMap = map;
    }

    public void cleanMostFreqWords(int docsNum){

        for(String key : wordsMap.keySet()){
            Features obj = wordsMap.get(key);
            if(((double)obj.getPresence()/(double)docsNum)>0.5){
                wordsMap.remove(key);
            }
        }
    }

    public void loadWords() throws IOException {

        File [] files = directory.listFiles();
        ArrayList<String> fileWords;
        for(int i = 0 ; i<files.length; i++){
            String label = FileUtilities.getFileName(files[i]);
            String text = FileUtilities.makeText(files[i]);
            insertTextWords(text,label);
        }
        int size = this.wordsMap.size();
    }



    private String getNormalizedForm(String s) throws IOException {
        String lemma = s;
        if(lemma.length()>2){
           lemma = toProperCase(s);
        }
        return lemma;
    }


    public void insertTextWords(String text, String label) throws IOException {
        String [] split = text.split(splitRegex);
        for(int i = 0; i<split.length ; i++){
            String s = getSubStr(split[i]);
            if(s!=null && !isStopWords(s)){
                String normalized = getNormalizedForm(s);
                Features f = new Features(normalized);
                if(wordsMap.containsKey(normalized)){
                    f = wordsMap.get(normalized);
                }
                f.updateLabelFrequencies(label);
                f.updateOccurencies();
                wordsMap.put(normalized,f);
            }
        }
    }


    public void insertFreqForNewLabel(String text, String label) throws IOException {
        String [] split = text.split(splitRegex);
        for(int i = 0; i<split.length ; i++){
            String s = getSubStr(split[i]);
            if(s!=null && !isStopWords(s)){
                String normalized = getNormalizedForm(s);
                Features f = null;
                if(wordsMap.containsKey(normalized)){
                   f = wordsMap.get(normalized);
                   f.updateLabelFrequencies(label);
                   wordsMap.put(normalized,f);
                }
            }
        }
    }


    private String getSubStr(String s){
        String regex = "(\\.\\.\\.|\\.|#|&|;|" +
                ":|«|»|%|\\?|@|\\”)*([A-Za-zàèòùì]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(s);
        if(match.find()){
            return match.group(2);
        }
        return null;
    }

    private boolean isStopWords(String s){
        return stopwords.contains(s.toLowerCase());
    }


    static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }


    public void makeDictonaryJson() throws IOException {

        JsonArray jsonFeatures = new JsonArray();
        for(Features f : wordsMap.values()){
            jsonFeatures.add(f.toJsonObject());
        }

        FileOutputStream fos = new FileOutputStream("Rocchio/src/main/resources/dict.json");
        JsonWriter writer = new JsonWriter(new IndentedWriter(fos));
        writer.startOutput();
        writer.visit(jsonFeatures);
        writer.finishOutput();
        fos.close();
    }
}

