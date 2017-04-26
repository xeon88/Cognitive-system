package Dictionary;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Marco Corona on 05/04/2017.
 */


public class DocumentVectorMaker {

    private File directory;
    private DocVectorManager manager;
    private WordDictionary wdict;
    private int size;

    public DocumentVectorMaker(File directory, int size) throws IOException {
        this.wdict = new WordDictionary(directory);
        this.directory = directory;
        wdict.loadWords();
        this.size = size;
        this.manager = new DocVectorManager(wdict.getWords().size());
    }

    public DocVectorManager getManager() {
        return manager;
    }

    public File getDirectory() {
        return directory;
    }

    public WordDictionary getWdict() {
        return wdict;
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
            //System.out.println("word :" + s);
            dvec.incrementsOcc(s,getNumOccur(s,text));
        }
        return dvec;
    }


    /**
     * Update all document vectors
     */

    public void makeAllMeanDocumentVectors(){
        try {
            File [] texts = directory.listFiles();
            String label = "";
            for (int i = 0; i< texts.length; i++){
                String text = FileUtilities.getTextFile(texts[i]);
                label = FileUtilities.getLabelFromFileName(texts[i]);
                DocumentVector vect = manager.getVector(label);
                vect = updateDocumentVector(wdict.getWords(),vect,text);
                manager.update(label,vect);;
            }

            Set<String> keys = manager.getKeys();
            String [] array = keys.toArray(new String[keys.size()]);
            for (int i = 0 ; i<array.length ; i++){
                DocumentVector vector = manager.getVector(array[i]);
                vector.setOccurences(vector.makeArrayForCentroidVector(size));
                manager.update(label,vector);;
            }

        }
        catch (IOException e){

        }

    }



    public void makeAllDocumentVectors(){
        try {
            File [] texts = directory.listFiles();
            String label = "";
            for (int i = 0; i< texts.length; i++){

                label = FileUtilities.getFileName(texts[i]);
                String text = FileUtilities.getTextFile(texts[i]);
                DocumentVector vect = manager.getVector(label);
                vect = updateDocumentVector(wdict.getWords(),vect,text);
                vect.makeArrayForDataVector();
                manager.update(label,vect);

            }

            System.out.println("number of features : " + wdict.getWords().size());

        }
        catch (IOException e){

        }

    }

}
