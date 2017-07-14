package Dictionary;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Marco Corona on 06/04/2017.
 * This class manages all document vectors created other than
 * provides all methods to execute the search of best category
 */
public class DocumentVectorManager {

    private HashMap<String,Centroid> centroids;
    private HashMap<String,DocumentVector> vectors;
    private String [] categories ;

    public DocumentVectorManager(){
        vectors = new HashMap<String, DocumentVector>();
        centroids = new HashMap<String, Centroid>();
        this.categories = null;
    }

    public Set<String> getKeys(){
        return vectors.keySet();
    }

    public Set<String> getLabelsKey(){return centroids.keySet();}

    public HashMap<String,Centroid> getCentroids(){
        return centroids;
    }

    public HashMap<String,DocumentVector> getVectors(){
        return vectors;
    }

    public void printVectors(){

        for(String s : vectors.keySet()){
            System.out.print("(" + s + ")");
        }
        System.out.print("\n");
    }


    public DocumentVector getVector(String label){
        if(!vectors.containsKey(label)){
            //System.out.println("Label created : " + label);
            vectors.put(label,new DocumentVector(label));
        }
        return vectors.get(label);
    }

    public void clear(){
        this.centroids = null;
        this.categories = null;
        this.vectors = null;
    }

    public void clearVectors(){
        this.vectors = null;
    }


    public Centroid getCentroid(String label){
        if(!centroids.containsKey(label)){
           //System.out.println("Label created : " + label);
            centroids.put(label,new Centroid(label));
        }
        return centroids.get(label);
    }

    public void updateCentroid(String label, Centroid c){
        centroids.remove(label);
        centroids.put(label,c);
    }

    public void updateDocumentVector(String label, DocumentVector dv){
        vectors.remove(label);
        vectors.put(label,dv);
    }


    public void exportAllVectorsToFile(){
        String path = "Rocchio/src/main/resources/vectors/";
        try {
            for (DocumentVector doc : vectors.values()) {
                File out = new File(path + doc.getLabel() + ".txt");
                FileUtilities.writeString(out, doc.toString());
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }


    /**
     * Compute the best category associated to an input vector through a measure of
     * similarity
     * @param in
     * @return
     * @throws IOException
     */


    public DocumentVector MostLikelihoodCategory(DocumentVector in) throws IOException {

        if(categories == null){
            categories = centroids.keySet().toArray(new String[centroids.size()]);
            Arrays.sort(categories);
        }
        double max = Double.NEGATIVE_INFINITY;
        DocumentVector bestCentroid = null;

        Logging log = new Logging();

        String message = "Label : " + in.getLabel() + "\n"
                + "[ " ;


        for(int i=0;i<categories.length; i++){
            Centroid centroid = centroids.get(categories[i]);
            double sim = in.similarity(centroid);
            message += " [" + centroid.getLabel() + " : " + sim + "] ";
            if(sim>max){
                max = sim;
                bestCentroid = centroid;
            }
        }

        message +="] \n";
        log.log(message,"info");


        return bestCentroid;
    }

    /**
     * Compute the best category associated to an input vector through a distance measure
     * , in particular an euclidean distance.
     * @param in
     * @return
     */

    public DocumentVector MostNearCategory(DocumentVector in){

        if(categories == null){
            categories = centroids.keySet().toArray(new String[centroids.size()]);
            Arrays.sort(categories);
        }
        double min = Double.POSITIVE_INFINITY;
        DocumentVector bestCentroid = null;

        for(int i=0;i<categories.length; i++){
            Centroid centroid = centroids.get(categories[i]);
            double sim = in.euclidianDistance(centroid);
            if(sim<min){
                min = sim;
                bestCentroid = centroid;
            }
        }
        return bestCentroid;
    }


    /**
     * Compute most similar category centroid of an other to determine the most similar category
     *
     * @param input
     * @return
     * @throws IOException
     */

    public Centroid NearestCentroid(Centroid input) throws IOException {

        if(categories == null){
            categories = centroids.keySet().toArray(new String[centroids.size()]);
            Arrays.sort(categories);
        }

        double max = Double.NEGATIVE_INFINITY;
        Centroid bestCentroid = null;
        Logging log = new Logging();

        String message = "Label : " + input.getLabel() + "\n"
                + "[ " ;

        for(int i=0;i<categories.length; i++){
            Centroid centroid = centroids.get(categories[i]);
            if(!centroid.getLabel().equals(input.getLabel())){
                double sim = input.similarity(centroid);
                message += " [" + centroid.getLabel() + " : " + sim + "] ";
                if(sim>max){
                    max = sim;
                    bestCentroid = centroid;
                }
            }
        }

        message +="] \n";
        log.log(message,"info");

        return bestCentroid;
    }
}
