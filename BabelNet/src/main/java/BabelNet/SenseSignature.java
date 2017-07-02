package BabelNet;

import edu.smu.tspell.wordnet.Synset;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Created by Marco Corona on 17/05/2017.
 */
public class SenseSignature {

    public HashMap<Synset,TreeSet<String>> mapSignature;

    public SenseSignature(){
        mapSignature = new HashMap<Synset, TreeSet<String>>();
    }

    public HashMap<Synset, TreeSet<String>> getMapSignature() {
        return mapSignature;
    }

    public void createSenseSignature(Synset s){
        mapSignature.put(s,new TreeSet<String>());
    }

    public TreeSet<String> getSignature(Synset s){
        if(mapSignature.containsKey(s)){
            return mapSignature.get(s);
        }
        return null;
    }

    public void addWordToSignature(Synset s , String word){
        TreeSet<String> set = mapSignature.get(s);
        set.add(word);
        mapSignature.put(s,set);
    }


}
