package Dictionary;


import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;

import java.util.HashMap;

/**
 * Created by Marco Corona on 05/05/2017.
 * Represent a feature and its info
 */

public class Feature implements Comparable<Feature> {
    private String word; // word associated to the feature
    private int occurencies; // number of total occurenices of specific feature
    private int presences;  // number of text in which feature is present
    private HashMap<String, Integer> textFreqs; // map indexed through text name of
                                                // occurencies in a specif text


    public Feature(String s){
        this.word = s;
        this.occurencies = 0;
        this.presences = 0;
        textFreqs = new HashMap<String, Integer>();
    }

    public int getOccurencies() {
        return occurencies;
    }

    public String getWord() {
        return word;
    }

    public int getPresences() {
        return presences;
    }

    public HashMap<String, Integer> getTextFreqs() {
        return textFreqs;
    }

    public void updateOccurencies(){
        occurencies++;
    }

    public void updateLabelFrequencies(String label){
        if(!textFreqs.keySet().contains(label)){
            presences++;
            textFreqs.put(label,1);
        }
        else{
            textFreqs.put(label, textFreqs.get(label)+1);
        }
    }

    public int getFreqsByLabel(String label){
        int freqs = 0;
        if(textFreqs.containsKey(label)){
            freqs = textFreqs.get(label);
        }
        return freqs;
    }

    /**
     * Compute the total number of occurencies of a feature for all document
     * belonged to the same category
     * @param category
     * @return
     */

    public int getOccurenciesByLabel(String category){
        int occurencies = 0;
        for(String label : textFreqs.keySet()){
            if(label.contains(category)){
                occurencies+= textFreqs.get(label);
            }
        }
        return occurencies;
    }


    public int compareTo(Feature o) {
        return this.word.compareTo(o.word);
    }


    /**
     * Build an json object about feature's informations
     * @return
     */

    public JsonObject toJsonObject(){

        JsonObject json = new JsonObject();
        json.put("word",word);
        json.put("global", occurencies);
        json.put("presecence", presences);
        JsonArray labelledFrequencies = new JsonArray();
        for(String labels : textFreqs.keySet()){
            JsonObject infoLabel = new JsonObject();
            infoLabel.put(labels, textFreqs.get(labels));
            labelledFrequencies.add(infoLabel);
        }
        json.put("labelled_freq",labelledFrequencies);
        return json;
    }
}
