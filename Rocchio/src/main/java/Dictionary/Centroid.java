package Dictionary;



/**
 * Created by Marco Corona on 09/05/2017.
 * Class for representation of a centroid such a particolar case of document vector.
 * This class compute the data vector such a mean of document vectors found in training set
 */


public class Centroid extends DocumentVector {

    private Centroid nearest = null;
    private int documents = 0;

    public Centroid(String label) {
        super(label);
    }

    public Centroid getNearest() {
        return nearest;
    }

    public void setNearest(Centroid centroid) {
        this.nearest = centroid;
    }

    public void incDocuments(){
        documents++;
    }


    public void makeDataVector(WordDictionary dict, int totalDocs
            , double beta, double gamma) {

        this.featureValues = new double[dict.getWordsMap().size()];

        // apply rocchio method together to td-idf formula

        String[] keys = dict.getWordKeys();
        for (int i = 0; i < keys.length; i++) {
            Feature f = dict.getWordsMap().get(keys[i]);
            double negativeVal = 0;
            double positveVal = 0;
            if (nearest == null) {
                positveVal = ((double) f.getOccurenciesByLabel(label) / (double) documents);
                negativeVal = 0;
            } else {
                positveVal = beta*((double) f.getOccurenciesByLabel(label) / (double) documents);
                negativeVal = gamma* ((double) f.getOccurenciesByLabel(nearest.getLabel()) / (double) (documents));
            }
            featureValues[i] = positveVal - negativeVal;
            featureValues[i] = featureValues[i] * (Math.log(totalDocs) - Math.log(f.getPresence()));
            i++;
        }
    }
}
