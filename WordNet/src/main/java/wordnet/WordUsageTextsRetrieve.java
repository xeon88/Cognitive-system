package wordnet;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;

import java.util.ArrayList;

/**
 * Created by Marco Corona on 14/05/2017.
 */
public class WordUsageTextsRetrieve {

    private WordNetDatabase db;

    public WordUsageTextsRetrieve(){
        this.db = WordNetDatabase.getFileInstance();
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
