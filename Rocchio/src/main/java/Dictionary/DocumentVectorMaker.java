package Dictionary;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Marco Corona on 05/04/2017.
 */


public class DocumentsClustering {

    private File directory;
    private DocVectorManager manager;
    private WordDictionary wdict;

    public DocumentsClustering(String pathDirectory) throws IOException {
        this.directory = new File(pathDirectory);
        this.wdict = new WordDictionary(directory);
        wdict.loadWords();
        this.manager = new DocVectorManager(wdict.getWords().size());
    }

    /**
     * count number of occurencies for same word in a text
     * @param word to search
     * @param text input
     * @return a occurencies counter
     */

    private int getNumOccur(String word, String text){

        Pattern pattern = Pattern.compile(word);
        Matcher match = pattern.matcher(text);
        int count = 0;
        while (match.find()){
            count++;
        }
        return count;
    }

    /**
     * Udpate counters for all words for all possible type of text.
     * This method needs text and document referred to the same categories
     * @param words
     * @param dvec
     * @param text
     */

    private DocumentVector updateDocumentVector
                (TreeSet<String> words, DocumentVector dvec,String text){

        for (String s : words){
            dvec.incrementsOcc(s,getNumOccur(s,text));
        }
        return dvec;
    }


    /**
     * Update all document vectors
     */

    public void makeAllDocumentVectors(){
        try {
            File [] texts = directory.listFiles();
            String label = "";
            for (int i = 0; i< texts.length; i++){

                String text = fileUtilities.getTextFile(texts[i]);
                label = fileUtilities.getLabelFromFileName(texts[i]);
                DocumentVector vect = manager.getVector(label);
                vect = updateDocumentVector(wdict.getWords(),vect,text);
                manager.update(label,vect);

            }
        }
        catch (IOException e){

        }

    }

}
