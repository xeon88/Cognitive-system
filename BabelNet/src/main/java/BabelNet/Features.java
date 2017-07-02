package BabelNet;


import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;


/**
 * Created by Marco Corona on 05/05/2017.
 */

public class Features implements Comparable<Features> {
    private String word;
    private int occurencies;
    private boolean lock;

    public Features(String s){
        this.word = s;
        this.occurencies = 0;
        lock = false;
    }

    public int getOccurencies() {
        return occurencies;
    }

    public String getWord() {
        return word;
    }


    public boolean getLock(){return lock;}

    public void updateOccurencies(){
        lock = true;
        occurencies++;
    }



    public int compareTo(Features o) {
        return this.word.compareTo(o.word);
    }


    public void resetLock(){
        this.lock = false;
    }

    public JsonObject toJsonObject(){

        JsonObject json = new JsonObject();
        json.put("word",word);
        json.put("global", occurencies);
        JsonArray labelledFrequencies = new JsonArray();
        return json;
    }
}

