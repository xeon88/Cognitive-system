package Dictionary;

import it.uniroma1.lcl.babelnet.BabelNet;

import java.io.*;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by Marco Corona on 05/04/2017.
 */
public class WordDictionary {

    private BabelNet babelInstance;
    private TreeSet<String> words;
    private File directory;
    private final String splitRegex = "(\\s|\\t|\\n|\\r|," +
            "|\\.\\.\\.|\\.|\\(|\\)|\"|#|\\&|\\:|«|»|\\%|\\s\\-|\'|\\?)*";

    private final String prepositionsRegex= "(All|Dell|)";

    public WordDictionary(File dir){
        if(dir.isDirectory()) {
            this.directory = dir;
        }
        else{
            throw new IllegalArgumentException("Missing directory");
        }
        words = new TreeSet();
        babelInstance = BabelNet.getInstance();
    }

    public TreeSet<String> getWords(){
        return words;
    }



    public void loadWords() throws IOException {

        File [] files = directory.listFiles();
        ArrayList<String> fileWords;
        for(int i = 0 ; i<files.length; i++){
           fileWords = createArrayList(files[i]);
            words.addAll(fileWords);
        }

    }



    private String getNormalizedForm(String s) throws IOException {
        String lemma = s;
        if(lemma.length()>2){
           lemma = toProperCase(s);
        }
        return lemma;
    }



    private ArrayList<String> createArrayList(File file) throws IOException {

        ArrayList<String> words = new ArrayList<String>();
        FileReader reader = new FileReader(file);
        BufferedReader buffreader = new BufferedReader(reader);
        String line ;
        String text="";
        while((line = buffreader.readLine())!=null){
            text +=line;
        }
        String [] split = text.split(splitRegex);
        System.out.println("splits : " + split.length);
        for(int i = 0; i<split.length ; i++){
            words.add(getNormalizedForm(split[i]));
        }
        return words;
    }



    static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }
}

