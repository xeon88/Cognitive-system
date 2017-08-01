package wordnet;


import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.dictionary.Dictionary;
import rita.wordnet.RiWordnet;


import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Marco Corona on 14/05/2017.
 * Provide all methods to access to Wordnet's resources, to retrieve synset
 * and sentences associated to each synset
 */
public class WordNetUtils {

    private RiWordnet wn;

    public WordNetUtils(){

        this.wn = new RiWordnet(null,System.getProperty("wordnet.database.dir"));
    }


    public RiWordnet getWn() {
        return wn;
    }

    public void getWordInfo(String word, String pos){


        System.out.println("Gloss");
        String [] glosses = wn.getAllGlosses(word,pos);
        if(glosses!=null) {
            for (String gloss : glosses) {
                System.out.println("[Gloss] "+ gloss);

            }
        }

        System.out.println("Examples all : ");
        String [] examplesAll = wn.getAllExamples(word,pos);
        if(examplesAll!=null) {
            for (String example : examplesAll) {
                System.out.println("[Usage] : " + example);
            }
        }


        System.out.println("Examples : ");
        String [] examples = wn.getExamples(word,pos);
        if(examples!=null) {
            for (String example : examples) {
                System.out.println("[Usage] : " + example);
            }
        }

    }

    public void getSynsetInfo(String word, POS pos) throws JWNLException {
        Dictionary dict = wn.getDictionary();
        IndexWord index = dict.getIndexWord(pos,word);
        Synset[] syns = index.getSenses();
        for(Synset syn : syns){
            System.out.println("Definition : " + syn.getGloss());
            System.out.println("WordForm : ");
            for (String wordForm : syn.getVerbFrames()){
                System.out.println(wordForm);
            }
        }
    }


    /**
     * Get all synset of word when it occupies a specific part-of-speech
     * @param word
     * @param pos
     * @return
     * @throws JWNLException
     */

    public Synset[] getSynsets(String word, POS pos) throws JWNLException {
        Dictionary dict = wn.getDictionary();
        IndexWord index = dict.getIndexWord(pos,word);
        if(index!=null){
            return index.getSenses();
        }
        else {
            return null;
        }
    }



    public ArrayList<String> getUsagesBySynset(Synset s){

        // retrive definition of synset
        Logging log = new Logging();

        ArrayList<String> usages = new ArrayList<String>();
        String definition = s.getGloss();
        String [] verbframes = s.getVerbFrames();

        try {
            log.log("Gloss : " + definition, "debug");
            usages.add(definition);
            for(String verbFrame : verbframes){
                usages.add(verbFrame);
            }
        }
         catch (IOException e) {
            e.printStackTrace();
        }
        return usages;
    }




    public ArrayList<String> getUsageTexts(String word,POS pos) throws JWNLException {
        ArrayList<String> texts = new ArrayList<String>();
        Dictionary dict = wn.getDictionary();
        IndexWord index = dict.getIndexWord(pos,word);
        Synset[] syns = index.getSenses();
        for(int i= 0; i<syns.length; i++){
            texts.addAll(getUsagesBySynset(syns[i]));
        }
        return texts;
    }
}
