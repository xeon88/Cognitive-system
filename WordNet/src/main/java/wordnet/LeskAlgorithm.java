package wordnet;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;

import java.io.IOException;
import java.util.HashSet;


/**
 * Created by Marco Corona on 13/05/2017.
 */
public class LeskAlgorithm {

    private SynsetRetrieve sr;
    private WordDictionary dict ;

    public  LeskAlgorithm (String wnSearchPath){
        sr = new SynsetRetrieve(wnSearchPath);
        dict = WordDictionary.getInstance();
    }

    public SynsetRetrieve getSr() {
        return sr;
    }

    public WordDictionary getDict() {
        return dict;
    }



    public void setDict(WordDictionary dict) {
        this.dict = dict;
    }

    public void setSr(SynsetRetrieve sr) {
        this.sr = sr;
    }

    public Synset getBestSense(String word, String sentence, SynsetType type){

        Synset bestSense = null;
        double maxOverlap = Double.NEGATIVE_INFINITY;
        HashSet<String> context = getWordsContext(sentence);
        Synset[] senses = sr.getSynsets(word,type);
        System.out.println("Context");
        printWords(context);
        System.out.println("--------------------------\n");

        for(int i = 0; i<senses.length; i++){

            HashSet<String> signature = getWordsSet(senses[i]);
            System.out.println("Sense : "+ senses[i].getDefinition());
            printWords(signature);
            HashSet<String> intersection = new HashSet<String>(context);
            intersection.retainAll(signature);
            double overlap = computeOverlap(intersection);
            System.out.println("overlap : "  + overlap );
            if(overlap>maxOverlap) {
                maxOverlap = overlap;
                bestSense = senses[i];
            }
            System.out.println("--------------------------\n");
        }

        return bestSense;
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

        HashSet<String> wordsContext = new HashSet<String>();
        String [] split = text.split(dict.splitRegex);
        for(int i = 0; i<split.length ; i++){

            String normalized = dict.getSubStr(split[i]);
            if(normalized!=null && !dict.isStopWords(normalized)) {
                normalized = dict.getNormalizedForm(normalized);
            }
            wordsContext.add(normalized);
        }

        return wordsContext;
    }


    public void printWords(HashSet<String> set){

        System.out.print("[");
        for(String word : set){
            System.out.print(word + " ");
        }
        System.out.print("]\n");
    }

}
