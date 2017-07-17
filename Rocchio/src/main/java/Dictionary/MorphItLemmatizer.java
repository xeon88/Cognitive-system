package Dictionary;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Marco Corona on 26/06/2017.
 */
public class MorphItLemmatizer {

    private static MorphItLemmatizer instance;
    private TreeMap<String,Lemma> lemmas ;


    public static MorphItLemmatizer getInstance(){
        if(instance==null){
            instance = new MorphItLemmatizer();
        }
        return instance;
    }



    private MorphItLemmatizer(){
        String pathMorphIt = System.getProperty("siscog.rocchio.morphit");
        File morphit = new File(pathMorphIt);
        loadLemmas(morphit);
    }


    private class Lemma implements Comparable<Lemma>{

        String lemma;
        String pos;

        Lemma(String lemma, String pos){
            this.lemma = lemma;
            this.pos = pos;
        }

        public String getLemma(){return lemma;}
        public String getPos(){return pos;}

        public int compareTo(Lemma o) {
            int compare = 0;
            if(lemma.compareTo(o.getLemma())!=0){
                compare = lemma.compareTo(o.getLemma());
            }
            else{
               compare = pos.compareTo(o.getPos());
            }
            return compare;
        }
    }


    private void loadLemmas(File morphIt) {

        StopWords sw = StopWords.getInstance();
        try {
            this.lemmas = new TreeMap();
            BufferedReader reader = new BufferedReader(new FileReader(morphIt));
            String buffer = "";
            while ((buffer=reader.readLine())!=null){
                buffer = StringEscapeUtils.unescapeHtml4(buffer);
                String[] split = buffer.split("\\t");
                String word = split [0];
                ArrayList<String> filteredWords = StringUtilities.getSubStr(word);
                for(String filteredWord : filteredWords){
                    if(filteredWord!=null && split.length>1){
                        String lemma = split[1];
                        String pos = split[2];
                        word = StringUtilities.getNormalizedForm(word);
                        lemma = StringUtilities.getNormalizedForm(lemma);
                        Lemma l = new Lemma(lemma,pos);
                        lemmas.put(word,l);
                    }
                }
            }
        }
        catch (IOException io){
        }
    }



    public String getLemma(String word){
        if(lemmas.containsKey(word)){
            return lemmas.get(word).getLemma();
        }
        return word;
    }

}
