import edu.mit.jwi.IDictionary;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import org.apache.jena.base.Sys;
import rita.RiString;
import rita.RiWordNet;
import wordnet.LeskAlgorithm;
import wordnet.SynsetRetrieve;
import wordnet.WordDictionary;
import wordnet.WordUsageTextsRetrieve;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Marco Corona on 12/05/2017.
 */
public class wordNetTest {

    public static void main(String [] args) {

        /*
        String wnSearchPath = "WordNet/src/main/resources/WordNet/dict";
        System.setProperty("wordnet.database.dir", wnSearchPath);
        */
        System.out.println("directory : " + System.getenv("WNSEARCHDIR") );
        String path = System.getenv("WNSEARCHDIR");
        System.setProperty("wordnet.database.dir", path);

        WordUsageTextsRetrieve retrieve = new WordUsageTextsRetrieve();
        LeskAlgorithm lesk = new LeskAlgorithm(path);
        SynsetRetrieve sr = lesk.getSr();

        ArrayList<Synset> synsets = new ArrayList<Synset>();

        String sentence = "The key problem was not one of quality but of quantity.";
        String word = "key";
        for (Synset s : sr.getSynsets(word, SynsetType.NOUN)){
            lesk.getDict().getSignatures().createSenseSignature(s);
            synsets.add(s);
        }

        try{
            lesk.getDict().loadWords(synsets);
        }
        catch (IOException ex){

        }

        Synset s = lesk.getBestSense(word,sentence,SynsetType.NOUN);
        System.out.println("");
        System.out.println("Best sense : " + s.getDefinition() );

    }

}
