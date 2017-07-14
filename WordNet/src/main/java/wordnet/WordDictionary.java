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


/**
 * Created by Marco Corona on 05/04/2017.
 * SingleTon class containing all features information
 */


public class WordDictionary {

    private TreeMap<String, Feature> wordsMap;
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
            this.wordsMap = new TreeMap<String, Feature>();
            this.signatures = new SenseSignature();
            this.annotator = new DocumentAnnotator();
    }


    public TreeMap<String, Feature> getWordsMap(){
        return wordsMap;
    }

    public void setWordsMap(TreeMap<String, Feature> map){
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
        if(senses==null) return;
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
                ArrayList<String> words = getLemmas(usage);
                insertTextWords(words,sense);
            }

            logging.log(usageMessage, "debug");
            numberSentences++;
            i++;
        }
    }


    private ArrayList<String> getLemmas(String text) throws IOException {

        Annotation annotation = annotator.makeAnnotatedDocument(text);
        ArrayList<String> words = annotator.getAllWordsAnnotationByClass(annotation,CoreAnnotations.LemmaAnnotation.class);
        return words;
    }


    private void resetFeatureLocks(){

        for(String key : wordsMap.keySet()){
            Feature f = wordsMap.get(key);
            f.resetLock();
            wordsMap.put(key,f);
        }
    }





    public void insertTextWords(ArrayList<String> words, Synset sense) throws IOException {

        StopWords sw = StopWords.getInstance();
        for(String word : words){
            word = StringUtilities.getSubStr(word);
            if(word!=null && !sw.isStopWords(word)){
                String normalized = StringUtilities.getNormalizedForm(word);
                Feature f = new Feature(normalized);
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




    public void makeDictonaryJson() throws IOException {

        JsonArray jsonFeatures = new JsonArray();
        for(Feature f : wordsMap.values()){
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

