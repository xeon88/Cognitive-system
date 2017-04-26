package Dictionary;

import java.util.HashMap;
import java.util.TreeSet;

/**
 * Created by Marco Corona on 06/04/2017.
 */
public class LabelManager {

    private HashMap<String, DocumentVector> vectors;

    public LabelManager (){
        vectors = new HashMap<String, DocumentVector>();
    }


    public DocumentVector getVector(String label, int size){

        if(!vectors.containsKey(label)){
            vectors.put(label,new DocumentVector(label, size));
        }
        return vectors.get(label);
    }


    public void updateVector(String label, DocumentVector dv){
        vectors.remove(label);
        vectors.put(label,dv);
    }

}
