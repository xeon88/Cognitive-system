package Dictionary;

import it.uniroma1.lcl.babelnet.BabelNet;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Marco Corona on 05/04/2017.
 */
public class WordDictionary {

    private BabelNet babelInstance;
    private TreeSet<String> words;
    private File directory;
    private String[] stopwords;
    private final String splitRegex = "(\\t|\\s|\\(|\\)|\\+|’|')+";

            /*"" +
            "|(," +
            "|\\.\\.\\.|\\.|\\(|\\)|\"|#|" +
            "\\&|\\;|\\:|«|»|\\%|’|'|\\?|@|\\”" +
            "|(([0-9]|/|\\-|\\+)+(\\S)*))+";
            */


    public WordDictionary(File dir){
        if(dir.isDirectory()) {
            this.directory = dir;
            this.stopwords  = StopWords.load();
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
        for(int i = 0; i<split.length ; i++){
            String s = getSubStr(split[i]);
            if(s!=null && !isStopWords(s)){
                String normalized = getNormalizedForm(s);
                words.add(normalized);
                //System.out.println("added : " + normalized);
            }
        }
        return words;
    }


    private String getSubStr(String s){
        String regex = "(\\.\\.\\.|\\.|#|&|;|" +
                ":|«|»|%|\\?|@|\\”)*([A-Za-zàèòùì]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(s);
        if(match.find()){
            //System.out.println("group 0 : " + match.group(0));
            //System.out.println("group 1 : " + match.group(1));
            //System.out.println("group 2 : " + match.group(2));
            return match.group(2);
        }
        return null;
    }

    private boolean isStopWords(String s){
        boolean check = false;
        for(int  i = 0; i<stopwords.length; i++){
            if(s.equalsIgnoreCase(stopwords[i])){
                check = true;
                break;
            }
        }
        return check;
    }


    static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }
}

