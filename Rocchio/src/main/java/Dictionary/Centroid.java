package Dictionary;

/**
 * Created by Marco Corona on 09/05/2017.
 */
public class Centroid extends DocumentVector {

    public Centroid(String label) {
        super(label);
    }

    public void makeArrayForCentroidVector(WordDictionary dict, int size , int totalDocs){
        int i = 0;
        this.featureValues = new double[dict.getWordsMap().size()];
        for(String key : dict.getWordsMap().keySet()){
            Features f = dict.getWordsMap().get(key);
            featureValues[i] = (double)f.getOccurenciesForLabel(label)*(Math.log(totalDocs) - Math.log(f.getPresence()));
            featureValues[i] = featureValues[i]/(double)size;
            i++;
        }
    }
}
