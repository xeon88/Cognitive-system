package wordnet;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;


/**
 * Created by Marco Corona on 13/05/2017.
 * This class implements the lesk algorithm
 */
public class LeskAlgorithm {


    private WordDictionary dict ;
    private WordNetUtils usageRetrieve;

    public  LeskAlgorithm (){

        dict = WordDictionary.getInstance();
        usageRetrieve = new WordNetUtils();
    }


    public WordDictionary getDict() {
        return dict;
    }


    public void setDict(WordDictionary dict) {
        this.dict = dict;
    }


    /**
     * Compute the best sense of a given word into a sentence, knowing that in that
     * sentence it occupies a specific part-of-speech
     * @param word
     * @param sentence
     * @param pos
     * @throws IOException
     * @throws JWNLException
     */

    public void getBestSense(String word, String sentence, POS pos) throws IOException, JWNLException {


        Logging logging = new Logging();

        // Write log info

        String header = "Sentence : " + sentence + "\n"
                + "Word : " + word + "\n"
                + "Type : " +  getTypeName(pos) + "\n\n";

        logging.log(header, "info");

        Synset bestSense = null;

        double maxOverlap = Double.NEGATIVE_INFINITY;

        // fetch context words set

        HashSet<String> context = getWordsContext(sentence);
        Synset[] senses = usageRetrieve.getSynsets(word,pos);

        // if exsist some synset for that pos

        if(senses!=null){

            logging.log("Context : ", "info");
            logging.log(toString(context), "info");

            for(int i = 0; i<senses.length; i++){

                // fetch signature of synset and computes intersection with context
                HashSet<String> signature = getWordsSet(senses[i]);
                HashSet<String> intersection = new HashSet<String>(context);
                intersection.retainAll(signature);

                // compute overlap
                double overlap = computeOverlap(intersection);

                // optimization step
                if(overlap>maxOverlap) {
                    maxOverlap = overlap;
                    bestSense = senses[i];
                }


                String message = "Sense : " + senses[i].getGloss() + "\n"
                    + "Signature : \n" + toString(signature) + "\n"
                    + "Intersection : " + toString(intersection) + "\n"
                    + "Overlap score : " + overlap + "\n\n";

                logging.log(message, "info");
            }
        }

        if(bestSense!=null){
            logging.log("Best sense : " + bestSense.getGloss(), "info");
            logging.log("Best overlap : " + maxOverlap + "\n\n", "info");
        }
        else{
            logging.log("[INFO] Failed! No sense found", "info");        }

    }

    /**
     * Compute overlap through the use of idf function
     * @param intersection
     * @return
     */

    private double computeOverlap(HashSet<String> intersection) {
        double overlap = 0;
        for(String word : intersection){
            Feature f = dict.getWordsMap().get(word);
            overlap += (Math.log((double) dict.getNumberSentences())-Math.log((double) f.getOccurencies()));
        }
        return overlap;
    }


    private HashSet<String> getWordsSet(Synset sense) {

        HashSet<String> context = new HashSet<String>();
        SenseSignature signatures = dict.getSignatures();
        context.addAll(signatures.getSignature(sense));
        return context;
    }

    /**
     * Returns a string list contained all words of context, after lemmatization step
     * @param text
     * @return
     * @throws IOException
     */

    public HashSet<String> getWordsContext(String text) throws IOException {

        StopWords sw = StopWords.getInstance();
        HashSet<String> wordsContext = new HashSet<String>();
        DocumentAnnotator annotator = new DocumentAnnotator();
        Annotation annotation = annotator.makeAnnotatedDocument(text);
        ArrayList<String> words =annotator.getAllWordsAnnotationByClass(annotation, CoreAnnotations.LemmaAnnotation.class);
        for (String word : words){
            if(!sw.isStopWords(word)){
                word = StringUtilities.getNormalizedForm(word);
                wordsContext.add(word);
            }
        }
        return wordsContext;
    }


    public String toString(HashSet<String> set){

        String words = "[";
        for(String word : set){
            words += word + " ";
        }
        words +="]";
        return words;
    }


    public String getTypeName(POS pos){

        return pos.getLabel();
    }



}
