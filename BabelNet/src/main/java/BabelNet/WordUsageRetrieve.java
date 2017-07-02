package BabelNet;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelSense;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.jlt.util.Language;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco Corona on 14/05/2017.
 */
public class WordUsageRetrieve {

    private WordNetDatabase db;
    private BabelNet bnet ;

    public WordUsageRetrieve(){
        this.db = WordNetDatabase.getFileInstance();
        this.bnet = BabelNet.getInstance();
    }


    public Synset[] getSynsets(String word, SynsetType type){
        Synset[] syns = db.getSynsets(word,type);
        return syns;
    }

    public ArrayList<String> getUsageTexts(String search){
        ArrayList<String> texts = new ArrayList<String>();
        Synset[] syns = db.getSynsets(search);
        for(int i= 0; i<syns.length; i++){
            texts.addAll(getUsagesBySynset(syns[i]));
        }
        return texts;
    }



    public ArrayList<String> getUsagesBySynset(Synset s){

        // retrive definition of synset

        ArrayList<String> usages = new ArrayList<String>();

        String definition = s.getDefinition();
        usages.add(definition);

        // retrieve examples of usage
        String [] usage = s.getUsageExamples();
        for (int j=0; j<usage.length; j++){
            usages.add(usage[j]);
        }

        return usages;
    }

}
