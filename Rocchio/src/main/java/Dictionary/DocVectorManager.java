package Dictionary;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Marco Corona on 06/04/2017.
 */
public class DocVectorManager {

    private HashMap<String, DocumentVector> vectors;
    private int size;

    public DocVectorManager(int size){
        vectors = new HashMap<String, DocumentVector>();
        this.size = size;
    }

    public Set<String> getKeys(){
        return vectors.keySet();
    }

    public void printLabels(){

        for(String s : vectors.keySet()){
            System.out.print("(" + s + ")");
        }
        System.out.print("\n");
    }

    public DocumentVector getVector(String label){
        if(!vectors.containsKey(label)){
            System.out.println("Label created : " + label);
            vectors.put(label,new DocumentVector(label, size));
        }
        return vectors.get(label);
    }


    public void update(String label, DocumentVector dv){
        vectors.remove(label);
        vectors.put(label,dv);
    }


}
