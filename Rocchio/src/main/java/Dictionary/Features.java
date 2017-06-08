package Dictionary;


import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;

import java.util.HashMap;

/**
 * Created by Marco Corona on 05/05/2017.
 */

public class Features implements Comparable<Features> {
    private String word;
    private int occurencies;
    private int presence;
    private HashMap<String, Integer> labelFreq ;


    public Features(String s){
        this.word = s;
        this.occurencies = 0;
        this.presence = 0;
        labelFreq = new HashMap<String, Integer>();
    }

    public int getOccurencies() {
        return occurencies;
    }

    public String getWord() {
        return word;
    }

    public int getPresence() {
        return presence;
    }

    public HashMap<String, Integer> getLabelFreq() {
        return labelFreq;
    }

    public void updateOccurencies(){
        occurencies++;
    }

    public void updateLabelFrequencies(String label){
        if(!labelFreq.keySet().contains(label)){
            presence++;
            labelFreq.put(label,1);
        }
        else{
            labelFreq.put(label,labelFreq.get(label)+1);
        }
    }



    public int getOccurenciesByLabel(String category){
        int occurencies = 0;
        for(String label : labelFreq.keySet()){
            if(label.contains(category)){
                occurencies+=labelFreq.get(label);
            }
        }
        return occurencies;
    }


    public int getOccurenciesOutLabel(String category){
        int occurencies = 0;
        for(String label : labelFreq.keySet()){
            if(!label.contains(category)){
                occurencies+=labelFreq.get(label);
            }
        }
        return occurencies;
    }

    public int compareTo(Features o) {
        return this.word.compareTo(o.word);
    }



    public JsonObject toJsonObject(){

        JsonObject json = new JsonObject();
        json.put("word",word);
        json.put("global", occurencies);
        json.put("presecence",presence);
        JsonArray labelledFrequencies = new JsonArray();
        for(String labels : labelFreq.keySet()){
            JsonObject infoLabel = new JsonObject();
            infoLabel.put(labels,labelFreq.get(labels));
            labelledFrequencies.add(infoLabel);
        }
        json.put("labelled_freq",labelledFrequencies);
        return json;
    }
}

