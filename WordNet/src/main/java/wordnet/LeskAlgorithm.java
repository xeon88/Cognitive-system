package wordnet;

import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;

import java.io.IOException;
import java.util.HashSet;


/**
 * Created by Marco Corona on 13/05/2017.
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


    public void getBestSense(String word, String sentence, POS pos) throws IOException, JWNLException {


        Logging logging = new Logging();


        String header = "Sentence : " + sentence + "\n"
                + "Word : " + word + "\n"
                + "Type : " +  getTypeName(pos) + "\n\n";

        logging.log(header, "info");

        Synset bestSense = null;
        double maxOverlap = Double.NEGATIVE_INFINITY;
        HashSet<String> context = getWordsContext(sentence);
        Synset[] senses = usageRetrieve.getSynsets(word,pos);

        logging.log("Context : ", "info");
        logging.log(toString(context), "info");

        for(int i = 0; i<senses.length; i++){

            HashSet<String> signature = getWordsSet(senses[i]);
            HashSet<String> intersection = new HashSet<String>(context);
            intersection.retainAll(signature);

            double overlap = computeOverlap(intersection);
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

        if(bestSense!=null){
            logging.log("Best sense : " + bestSense.getGloss(), "info");
            logging.log("Best overlap : " + maxOverlap + "\n\n", "info");
        }
        else{
            logging.log("[INFO] Failed! No sense found", "info");        }


    }


    private double computeOverlap(HashSet<String> intersection) {
        double overlap = 0;
        for(String word : intersection){
            Features f = dict.getWordsMap().get(word);
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



    public HashSet<String> getWordsContext(String text){

        StopWords sw = StopWords.getInstance();

        HashSet<String> wordsContext = new HashSet<String>();
        String [] split = text.split(dict.splitRegex);
        for(int i = 0; i<split.length ; i++){

            String normalized = dict.getSubStr(split[i]);
            if(normalized!=null && !sw.isStopWords(normalized)) {
                normalized = dict.getNormalizedForm(normalized);
            }
            wordsContext.add(normalized);
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
