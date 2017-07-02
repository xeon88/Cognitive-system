package wordnet;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import net.didion.jwnl.data.Synset;
import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.io.JsonWriter;

import java.io.*;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Marco Corona on 05/04/2017.
 * SingleTon class containing all features information
 */


public class WordDictionary {

    private TreeMap<String,Features> wordsMap;
    public final String splitRegex = "(\\t|\\s|\\(|\\)|\\+|’|')+";
    private SenseSignature signatures;
    private static WordDictionary instance;
    private int numberSentences;
    private DocumentAnnotator annotator;



    public static WordDictionary getInstance(){
        if(instance==null){
            instance = new WordDictionary();
        }
        return instance;
    }

    private WordDictionary(){
            this.numberSentences = 0;
            this.wordsMap = new TreeMap<String, Features>();
            this.signatures = new SenseSignature();
            this.annotator = new DocumentAnnotator();
    }


    public TreeMap<String,Features> getWordsMap(){
        return wordsMap;
    }

    public void setWordsMap(TreeMap<String,Features> map){
        this.wordsMap = map;
    }

    public int getNumberSentences() {
        return numberSentences;
    }


    public SenseSignature getSignatures(){
        return signatures;
    }


    public void loadWords(Synset[] senses) throws IOException {

        Logging logging = new Logging();
        WordNetUtils dbUsages = new WordNetUtils();
        int i = 0;
        for(Synset sense : senses){
            ArrayList<String> usages = dbUsages.getUsagesBySynset(sense);
            signatures.createSenseSignature(sense);
            logging.log("Sense nummber :" + i, "debug");
            logging.log("Ssages number : " + usages.size(), "debug");
            logging.log("Usages example :", "debug");

            String usageMessage = "";
            for(String usage : usages){
                usageMessage += usage + "\n";
                resetFeatureLocks();
                insertTextWords(usage,sense);
            }

            logging.log(usageMessage, "debug");
            numberSentences++;
            i++;
        }
    }


    private void resetFeatureLocks(){

        for(String key : wordsMap.keySet()){
            Features f = wordsMap.get(key);
            f.resetLock();
            wordsMap.put(key,f);
        }
    }


    public String getNormalizedForm(String s) {
        String lemma = s;
        if(lemma.length()>2){
            lemma = toProperCase(s);
        }
        return lemma;
    }



    public void insertTextWords(String text, Synset sense) throws IOException {

        StopWords sw = StopWords.getInstance();

        String [] split = text.split(splitRegex);
        for(int i = 0; i<split.length ; i++){

            String s = getSubStr(split[i]);
            Annotation annotation = annotator.makeAnnotatedDocument(s);
            s = annotator.getWordAnnotationByClass(annotation,s,CoreAnnotations.LemmaAnnotation.class);

            if(s!=null && !sw.isStopWords(s)){
                String normalized = getNormalizedForm(s);
                Features f = new Features(normalized);

                if(wordsMap.containsKey(normalized)) {
                    f = wordsMap.get(normalized);
                }

                if(!f.getLock()){
                    f.updateOccurencies();
                    signatures.addWordToSignature(sense,normalized);
                }

                wordsMap.put(normalized,f);
            }

            }
        }



    public String getSubStr(String s){
        String regex = "(\\.\\.\\.|\\.|#|&|;|" +
                ":|«|»|%|\\?|@|\\”)*([A-Za-zàèòùì]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(s);
        if(match.find()){
            return match.group(2);
        }
        return null;
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

