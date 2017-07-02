package Dictionary;

import java.io.File;
import java.io.IOException;

/**
 * Created by Marco Corona on 05/04/2017.
 */


public class DocumentVectorBuilder {

    private File directory;
    private DocumentVectorManager manager;
    private WordDictionary wdict;
    private int docsNumber;


    public DocumentVectorBuilder(File directory, String lang) throws IOException {
        this.wdict = WordDictionary.getInstance(directory, lang);
        this.directory = directory;
        this.docsNumber = directory.listFiles().length;
        this.manager = new DocumentVectorManager();
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

    public void makeAllMeanDocumentVectors(double beta, double gamma) throws IOException {

        // make first all centroid object and update them structure with
        // the number of documents belonged to the same class

        for (String label : manager.getKeys()){
            String category = FileUtilities.getCategoryfromLabel(label);
            Centroid centroid = manager.getCentroid(category);
            centroid.incDocuments();
            manager.updateCentroid(category,centroid);
        }


        String [] labels = manager.getLabelsKey().toArray(new String [manager.getLabelsKey().size()]);

        // make the data vector corresponding to each class, computing the mean of
        // features

        for (String label : labels){
            Centroid centroid = manager.getCentroid(label);
            centroid.makeDataVector(wdict, docsNumber,beta,gamma);
            System.out.println("compute centroid : " + centroid.getLabel());
            manager.updateCentroid(label,centroid);
        }

        // compute the nearest and set nearest for all classes

        for(String label : labels){
            Centroid centroid = manager.getCentroid(label);
            centroid.setNearest(manager.NearestCentroid(centroid));
            manager.updateCentroid(label,centroid);
        }

        // compute again centroids through Rocchio method with nearest class info

        for (String label : labels){
            Centroid centroid = manager.getCentroid(label);
            centroid.makeDataVector(wdict,docsNumber,beta,gamma);
            manager.updateCentroid(label,centroid);
        }

    }



    public void makeAllDocumentVectors(){
        File [] texts = directory.listFiles();
        String label = "";

        for (int i = 0; i< texts.length; i++){
            label = FileUtilities.getFileName(texts[i]);
            DocumentVector vect = manager.getVector(label);
            vect.makeDataVector(wdict, docsNumber);
            manager.updateDocumentVector(label,vect);
        }


        System.out.println("number of features : " + wdict.getWordsMap().keySet().size());
    }



    public DocumentVector createTextVector(File in) throws IOException {
        String label = "";
        label = "test_"+FileUtilities.getFileName(in);
        String text = FileUtilities.getTextFromFile(in);
        DocumentVector vect = new DocumentVector(label);
        wdict.insertQueryTextFeatures(text,label);
        vect.makeDataVector(wdict, docsNumber);
        return vect;
    }

}
