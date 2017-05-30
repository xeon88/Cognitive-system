package Dictionary;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Marco Corona on 06/04/2017.
 */
public class DocumentVectorManager {

    private HashMap<String,Centroid> centroids;
    private HashMap<String,DocumentVector> vectors;
    private int size;

    public DocumentVectorManager(int size){
        vectors = new HashMap<String, DocumentVector>();
        centroids = new HashMap<String, Centroid>();
        this.size = size;
    }

    public Set<String> getKeys(){
        return vectors.keySet();
    }

    public Set<String> getLabelsKey(){return centroids.keySet();}

    public void printVectors(){

        for(String s : vectors.keySet()){
            System.out.print("(" + s + ")");
        }
        System.out.print("\n");
    }


    public DocumentVector getVector(String label){
        if(!vectors.containsKey(label)){
            System.out.println("Label created : " + label);
            vectors.put(label,new DocumentVector(label));
        }
        return vectors.get(label);
    }


    public Centroid getCentroid(String label){
        if(!centroids.containsKey(label)){
            System.out.println("Label created : " + label);
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


    public DocumentVector MostLikelihoodCategory(DocumentVector in){

        double max = Double.NEGATIVE_INFINITY;
        DocumentVector bestCentroid = null;

        for(Centroid centroid : centroids.values()){
            double sim = in.similarity(centroid);
            if(sim>max){
                max = sim;
                bestCentroid = centroid;
            }
        }
        return bestCentroid;
    }


    public DocumentVector MostNearCategory(DocumentVector in){

        double min = Double.POSITIVE_INFINITY;
        DocumentVector bestCentroid = null;

        for(Centroid centroid : centroids.values()){
            double sim = in.euclidianDistance(centroid);
            if(sim<min){
                min = sim;
                bestCentroid = centroid;
            }
        }
        return bestCentroid;
    }
}
