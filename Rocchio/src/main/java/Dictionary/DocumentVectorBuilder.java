package Dictionary;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Marco Corona on 05/04/2017.
 */


public class DocumentVectorBuilder {

    private File directory;
    private DocumentVectorManager manager;
    private WordDictionary wdict;
    private int docsPerCategory;
    private int docsNum;


    public DocumentVectorBuilder(File directory, int size) throws IOException {
        this.wdict = WordDictionary.getInstance(directory);
        this.directory = directory;
        wdict.loadWords();
        this.docsPerCategory = size;
        this.docsNum = directory.listFiles().length;
        this.manager = new DocumentVectorManager(wdict.getWordsMap().size());
    }

    public DocumentVectorManager getManager() {
        return manager;
    }

    public File getDirectory() {
        return directory;
    }

    public WordDictionary getWdict() {
        return wdict;
    }


    /**
     * Update all document vectors
     */

    public void makeAllMeanDocumentVectors(){

        for (String label : manager.getKeys()){
            String category = FileUtilities.getCategoryfromLabel(label);
            Centroid centroid = manager.getCentroid(category);
            if(centroid.featureValues.length==1){
                centroid.makeArrayForCentroidVector(wdict, docsPerCategory,docsNum);
                manager.updateCentroid(category,centroid);
            }
        }

    }



    public void makeAllDocumentVectors(){
        File [] texts = directory.listFiles();
        String label = "";

        // clean most common words

       // wdict.cleanMostFreqWords(texts.length);

        // build data vectory for all texts

        for (int i = 0; i< texts.length; i++){
            label = FileUtilities.getFileName(texts[i]);
            DocumentVector vect = manager.getVector(label);
            vect.makeArrayForDataVector(wdict,docsNum);
            manager.updateDocumentVector(label,vect);
        }

        System.out.println("number of features : " + wdict.getWordsMap().size());
    }



    public DocumentVector createTextVector(File in) throws IOException {
        String label = "";
        label = FileUtilities.getFileName(in);
        String text = FileUtilities.getTextFile(in);
        DocumentVector vect = manager.getVector(label);
        wdict.insertFreqForNewLabel(text,label);
        vect.makeArrayForDataVector(wdict,docsNum);
        return vect;
    }

}
