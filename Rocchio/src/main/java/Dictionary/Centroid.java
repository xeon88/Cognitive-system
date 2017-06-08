package Dictionary;

/**
 * Created by Marco Corona on 09/05/2017.
 */
public class Centroid extends DocumentVector {

    private final double beta = 16.0 ;
    private final double gamma = 4.0;

    public Centroid(String label) {
        super(label);
    }

    public void makeArrayForCentroidVector(WordDictionary dict, int categoryDocs , int totalDocs){
        int i = 0;
        this.featureValues = new double[dict.getWordsMap().size()];
        for(String key : dict.getWordsMap().keySet()){
            Features f = dict.getWordsMap().get(key);
            double positveVal=(beta)*((double)f.getOccurenciesByLabel(label)/(double)categoryDocs);
            double negativeVal=(gamma)*((double)f.getOccurenciesOutLabel(label)/(double)(totalDocs-categoryDocs));
            featureValues[i]=positveVal-negativeVal;
            featureValues[i] = featureValues[i]*(Math.log(totalDocs) - Math.log(f.getPresence()));
            i++;
        }
    }
}
