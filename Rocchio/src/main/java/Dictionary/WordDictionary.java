package Dictionary;


import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.io.JsonWriter;
import org.apache.jena.base.Sys;

import java.io.*;

import java.util.*;


/**
 * Created by Marco Corona on 05/04/2017.
 * SingleTon class containing all features information
 */


public class WordDictionary {

    private TreeMap<String,Feature> wordsMap;
    private File [] documents;
    private final String splitRegex = "(\\t|\\s|\\n|\\(|\\)|\\+|’|')+";
    private static WordDictionary instance;
    private DocumentAnnotator annotator;
    private String [] wordKeys;
    private String language;
    private int samples;
    private String [] categories;

    public static WordDictionary getInstance(File dir, String lang, int size){
        if(instance==null){
            instance = new WordDictionary(dir, lang, size);
        }
        return instance;
    }



    private WordDictionary(File dir, String lang, int size){
        try {
            this.annotator = new DocumentAnnotator(lang);
            this.wordsMap = new TreeMap<String, Feature>();
            this.language = lang;
            this.wordKeys = null;
            this.documents = null;
            this.samples = size;
            this.categories = null;
            loadWords(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getWordKeys(){return wordKeys ;}

    public TreeMap<String,Feature> getWordsMap(){
        return wordsMap;
    }

    public void setWordsMap(TreeMap<String,Feature> map){
        this.wordsMap = map;
    }

    public File[] getDocuments(){
        return this.documents;
    }

    public String [] getCategories(){
        return categories;
    }


    public void clear(){
        this.wordsMap = null;
        this.wordKeys = null;
        this.documents = null;
        this.annotator = null;
        this.categories = null;
        this.samples = 0;
        instance = null;
    }

    /**
     * Load all words into dictionary contained in all training set texts
     * @throws IOException
     */

    public void loadWords(File directory) throws IOException {


        if(directory.isDirectory()){
            categories = loadFromDirectory(directory);
        }
        else {
            categories = loadFromFile(directory);
        }


        createKeysArray();
        cleanMostFreqWords(categories);
        createKeysArray();
        System.out.println("number of features : " + wordKeys.length);
        /*
        String message = "[ADDED WORDS ] :  \n";
        for (String key : wordKeys){
            message += " :   " + key + " \n";
        }
        log.log(message,"debug");

        makeDictonaryJson();
        */
    }



    private void loadSingleText(File file) throws IOException {
        String label = FileUtilities.getFileName(file);
        String text = FileUtilities.getTextFromFile(file);
        String category = FileUtilities.getCategoryfromLabel(label);
        insertTrainingTextWords(text,category);
    }


    private File[] selectSubSetCategories(int size, File [] documents){

        if(documents.length<size){
            size = documents.length;
        }

        File [] selection = new File [size];
        int i = 0;
        int j = 0;
        boolean [] locks = new boolean[documents.length];


        while (i<size){
            if(!locks[j%documents.length]){
                double rand = Math.random();
                if(rand>0.6){
                    selection[i]=documents[j%documents.length];
                    locks[j%documents.length]=false;
                    i++;
                }
                j++;
            }
        }

        return selection;
    }


    /**
     * Load a sampled subset of documents contained within a directory
     * A random selection of document is performed before filling the
     * dictionary
     * @return
     * @throws IOException
     */

    private String [] loadFromDirectory(File directory) throws IOException {

        File [] files = directory.listFiles();
        TreeSet<String> categories = new TreeSet<String>();
        ArrayList<File> tmp = new ArrayList<File>();
        String prevCategory= FileUtilities.getCategoryfromLabel(FileUtilities.getFileName(files[0]));
        ArrayList<File> selection = new ArrayList<File>();

        System.out.println("Documents selection..");

        for (int i = 0;i<files.length ; i++) {
            String filename = FileUtilities.getFileName(files[i]);
            String category = FileUtilities.getCategoryfromLabel(filename);
            categories.add(category);
            if(category.equals(prevCategory)){
                tmp.add(files[i]);
            }
            else {
                File [] documents = tmp.toArray(new File[tmp.size()]);
                tmp = new ArrayList<File>();
                File [] subset = selectSubSetCategories(samples,documents);
                for (int j=0; j<subset.length ; j++){
                    selection.add(subset[j]);
                }
            }
            prevCategory = category;
        }

        String [] categoriesKeys = categories.toArray(new String [categories.size()]);
        File [] selectionArray = selection.toArray(new File [selection.size()]);
        int j = 1;

        System.out.println("Loading texts...");
        for(File file : selectionArray){
            if(j%(samples*2)==0){
                System.out.println("loaded : " + j + "/" + selectionArray.length + " documents");
            }
            loadSingleText(file);
            j++;
        }

        this.documents = selectionArray;
        System.gc();
        System.out.println("Loaded all texts done");
        return categoriesKeys;
    }


    private String [] loadFromFile(File file) throws IOException {
        String category = FileUtilities.getCategoryfromLabel(FileUtilities.getFileName(file));
        loadSingleText(file);
        String [] categories = new String[]{category};
        return categories;
    }



    /**
     * Filter words through an entropy measure. It models the probability for each categories
     * like the ratio between number of occurrencies of a word within texts belonged to the same category
     * on total number of occurencies of that word in all texts.
     * @param categories set of categories found in training set
     */


    private void cleanMostFreqWords(String [] categories) throws IOException {

        double normalizationConstant = Math.log((double)categories.length) ;
        StopWords sw = StopWords.getInstance();
        Logging log = new Logging();
        double threshold = 0.6;

        String message = "[REMOVED] : ";

        for (String key : wordKeys){
            Feature f = wordsMap.get(key);
            double occ = f.getOccurencies();
            double partial = 0;
            for( String category : categories){
                double prob = ((double)f.getOccurenciesByLabel(category)/(double)occ);
                partial -= prob!=0 ? prob*Math.log(prob):0;
            }

            double normalizedEntropy = partial/normalizationConstant;

            if(normalizedEntropy>=threshold || f.getOccurencies()<=1){
                wordsMap.remove(key);
                double stopWordsThreshold = 0.95 - ((double) samples/1000)*0.1;
                if(normalizedEntropy>=stopWordsThreshold){
                    sw.addStopWords(f.getWord());
                }
                message+= " " + key + " - " + normalizedEntropy + "\n";
            }

        }
        System.out.println("stopwords loaded ... " + sw.size());
        //sw.export();
        message+="\n";
       //log.log(message,"debug");
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
        String [] tokens = annotator.makeAnnotatedSentences(text);
        insertSentencesFeatures(tokens,label,false);
    }



    /**
     * Insert all features regarding a labelled query text (to classify)
     * @param text
     * @param label
     * @throws IOException
     */


    public void insertQueryTextFeatures(String text, String label) throws IOException {
        String [] tokens = annotator.makeAnnotatedSentences(text);
        insertSentencesFeatures(tokens,label,true);
    }


    /**
     * Insert or update all features into wordsMap derived from an annotated sentence
     * belonged to a text labelled
     * @param tokens list of token to lemmatize
     * @param label label of text
     * @param queryText needs an update of occurencies (not in case of query text)
     * @throws IOException
     */

    private void insertSentencesFeatures(String [] tokens, String label, boolean queryText) throws IOException {

        Logging logger = new Logging();
        StopWords sw = StopWords.getInstance();
        for(String token : tokens){
            if(token!=null && StringUtilities.checkWord(token) && !sw.isStopWords(token)){
                token = StringUtilities.getNormalizedForm(token);

                Feature f = new Feature(token);
                if(wordsMap.containsKey(token)){
                    f = wordsMap.get(token);
                }
                f.updateLabelFrequencies(label);
                // update occuriences only if features belonged to
                // a training text
                if(!queryText){
                    f.updateOccurencies();
                    wordsMap.put(token,f);
                }
                else { // query text case
                    if(wordsMap.containsKey(token)){
                        wordsMap.put(token,f);
                    }
                }
            }
        }

    }

    /**
     * Select the most (size) frequent features in documents belonged to a given
     * category
     * @param category
     * @param size
     * @return
     */

    public ArrayList<Feature> getBestFeatureForCategory(String category, int size){

        ArrayList<Feature> best = new ArrayList<Feature>();

        for(String key : wordKeys) {

            Feature f = wordsMap.get(key);
            if(f.getOccurenciesByLabel(category)==0){
                continue;
            }

            int i = 0;
            for (Feature feature : best) {
                if (f.getOccurenciesByLabel(category) > feature.getOccurenciesByLabel(category)) {
                    break;
                }
                i++;
            }

            ArrayList<Feature> tmp = new ArrayList<Feature>();

            for(int j = 0; j<i ; j++){
                tmp.add(best.get(j));
            }

            if(i<size){
                tmp.add(f);
                if(tmp.size()<=best.size()){
                    for (int j=i; j<best.size();j++){
                        tmp.add(best.get(j));
                    }
                }
            }
            best=tmp;
        }

        return best;
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
