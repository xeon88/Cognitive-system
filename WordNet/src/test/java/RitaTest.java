import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.dictionary.Dictionary;
import wordnet.WordNetUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Marco Corona on 09/06/2017.
 */
public class RitaTest {


    public static void main(String [] args){


        File wordnet = new File("WordNet/src/main/resources/WordNet/dict");
        String path = wordnet.getAbsolutePath();
        System.out.println("directory : " + path);
        System.setProperty("wordnet.database.dir", path);
        String logPath = "WordNet/src/main/resources/log.txt";
        System.setProperty("siscog.wordnet.logpath",logPath);
        try {
            WordNetUtils wnu = new WordNetUtils();
            wnu.getWordInfo("arm", "n");
            Dictionary dict = wnu.getWn().getDictionary();
            IndexWord index = dict.getIndexWord(POS.NOUN,"arm");
            for(int i = 0 ; i<index.getSenseCount() ; i++){
                Synset sense = index.getSense(i+1);
                System.out.println("[GLOSS]" +sense.getGloss());
                Word[] words = sense.getWords();
                for(int j = 0; j<words.length ; j++){
                    System.out.println("[WORD]" + words[j].getLemma());
                }
            }

            //System.out.println(syn[0].getDefinition());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
