package wordnet;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import rita.RiWordNet;
import java.io.File;

/**
 * Created by Marco Corona on 12/05/2017.
 */
public class SynsetRetrieve {

    private RiWordNet wordnet;
    private WordNetDatabase wordNetDb ;

    public SynsetRetrieve(String wnSearchPath){
        File wordDict = new File(wnSearchPath);
        wordNetDb = WordNetDatabase.getFileInstance();
    }

    public Synset[] getSynsets(String word, SynsetType type){
        Synset[] syns = wordNetDb.getSynsets(word,type);
        return syns;
    }



}
