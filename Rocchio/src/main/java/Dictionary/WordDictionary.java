package Dictionary;


import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.io.JsonWriter;

import java.io.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * Created by Marco Corona on 05/04/2017.
 * SingleTon class containing all features information
 */


public class WordDictionary {

    private TreeMap<String,Feature> wordsMap;
    private File directory;
    private final String splitRegex = "(\\t|\\s|\\n|\\(|\\)|\\+|’|')+";
    private static WordDictionary instance;
    private DocumentAnnotator annotator;
    private String [] wordKeys;
    private String language;

    public static WordDictionary getInstance(File dir, String lang){
        if(instance==null){
            instance = new WordDictionary(dir, lang);
        }
        return instance;
    }


    private WordDictionary(File dir, String lang){

        if(dir.isDirectory()) {
            try {
                this.annotator = new DocumentAnnotator(lang);
                this.directory = dir;
                this.wordsMap = new TreeMap<String, Feature>();
                this.language = lang;
                this.wordKeys = null;
                loadWords();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            throw new IllegalArgumentException("Missing directory");
        }
    }

    public String[] getWordKeys(){return  wordKeys ;}

    public TreeMap<String,Feature> getWordsMap(){
        return wordsMap;
    }

    public void setWordsMap(TreeMap<String,Feature> map){
        this.wordsMap = map;
    }




    /**
     * Load all words into dictionary contained in all training set texts
     * @throws IOException
     */

    public void loadWords() throws IOException {

        File [] files = directory.listFiles();
        TreeSet<String> categories = new TreeSet<String>();
        ArrayList<String> fileWords;
        for(int i = 0 ; i<files.length; i++){
            String label = FileUtilities.getFileName(files[i]);
            String category = FileUtilities.getCategoryfromLabel(label);
            String text = FileUtilities.getTextFromFile(files[i]);
            insertTrainingTextWords(text,label);
            categories.add(category);
            System.out.println("Text file : " + label + " added");
        }

        createKeysArray();
        cleanMostFreqWords(categories);
        createKeysArray();
        makeDictonaryJson();
    }





    /**
     * Filter words through an entropy measure. It models the probability for each categories
     * like the ratio between number of occurrencies of a word within texts belonged to the same category
     * on total number of occurencies of that word in all texts.
     * @param categories set of categories found in training set
     */


    private void cleanMostFreqWords(TreeSet<String> categories) {

        double normalizationConstant = - ((double)categories.size())
                *(1/(double)categories.size())*Math.log(1/(double)categories.size());

        double threshold = 0.4;

        for (String key : wordKeys){
            Feature f = wordsMap.get(key);
            double occ = f.getOccurencies();
            double partial = 0;
            for( String category : categories){
                double prob = ((double)f.getOccurenciesByLabel(category)/(double)occ);
                partial -= prob!=0 ? prob*Math.log(prob):0;
            }

            double normalizedEntropy = partial/normalizationConstant;

            if(normalizedEntropy>=threshold){
                wordsMap.remove(key);
            }

        }

    }

    /**
     *  Create a fixed list of words contained in wordsmap in order to avoid
     *  conflicts when it necessary to update wordsMap data structure
     */

    public void createKeysArray(){

        if(wordKeys==null || wordKeys.length!=wordsMap.size()){
            wordKeys = wordsMap.keySet().toArray(new String[wordsMap.size()]);
            Arrays.sort(wordKeys);
        }
    }




    /**
     * Insert and update features in dictionary found in a text belonged to a training set
     * which is previously labelled. Each feature derived from a process of lemmatization
     * for a particular kind of language.
     * @param text to analyze
     * @param label associated with the text
     * @throws IOException
     */



    public void insertTrainingTextWords(String text, String label) throws IOException {

        ArrayList<Annotation> documents = annotator.makeAnnotatedSentences(text);
        for(Annotation document : documents){
            ArrayList<String> tokens = annotator.getAllWordsAnnotationByClass(document,CoreAnnotations.LemmaAnnotation.class);
            insertSentencesFeatures(tokens,document,label,false);
        }
    }


    /**
     * Insert all features regarding a labelled query text (to classify)
     * @param text
     * @param label
     * @throws IOException
     */


    public void insertQueryTextFeatures(String text, String label) throws IOException {


        ArrayList<Annotation> documents = annotator.makeAnnotatedSentences(text);
        for(Annotation document : documents){
            ArrayList<String> tokens = annotator.getAllWordsAnnotationByClass(document,CoreAnnotations.LemmaAnnotation.class);
            insertSentencesFeatures(tokens,document,label,true);
        }

    }


    /**
     * Insert or update all features into wordsMap derived from an annotated sentence
     * belonged to a text labelled
     * @param tokens list of token to lemmatize
     * @param sentence annotated sentences
     * @param label label of text
     * @param queryText needs an update of occurencies (not in case of query text)
     * @throws IOException
     */

    private void insertSentencesFeatures(ArrayList<String> tokens,Annotation sentence,
                                         String label, boolean queryText) throws IOException {

        Logging logger = new Logging();
        StopWords sw = StopWords.getInstance();
        MorphItLemmatizer lemmatizer = MorphItLemmatizer.getInstance();
        for(String token : tokens){
            token = StringUtilities.getSubStr(token);
            if(token!=null && !sw.isStopWords(token)){
                String lemma = "";
                if(language.equals(DocumentAnnotator.LANGUAGE_EN)){
                    lemma = annotator.getWordAnnotationByClass(sentence,token,CoreAnnotations.LemmaAnnotation.class);
                    lemma = StringUtilities.getNormalizedForm(lemma);
                }
                if(language.equals(DocumentAnnotator.LANGUAGE_IT)){
                    String normalized=StringUtilities.getNormalizedForm(token);
                    lemma = lemmatizer.getLemma(normalized);
                }
                Feature f = new Feature(lemma);
                if(wordsMap.containsKey(lemma)){
                    f = wordsMap.get(lemma);
                }
                else {
                    String message = "Added : " + lemma ;
                    logger.log(message,"debug");
                }
                f.updateLabelFrequencies(label);

                // update occuriences only if features belonged to
                // a training text
                if(!queryText){
                    f.updateOccurencies();
                    wordsMap.put(lemma,f);
                }
                else { // query text case
                    if(wordsMap.containsKey(lemma)){
                        wordsMap.put(lemma,f);
                    }
                }
            }
        }

    }


    /**
     * Export all dictionary in a json file
     * @throws IOException
     */

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
