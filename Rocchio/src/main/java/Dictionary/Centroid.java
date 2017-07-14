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
            double positiveVal = 0;
            int freqs = f.getFreqsByLabel(label);
            if (nearest == null) {
                positiveVal = ((double) freqs/ (double) documents);
                negativeVal = 0;
            }
            else {
                positiveVal = beta*((double) freqs/ (double) documents);
                int nearfreqs = f.getFreqsByLabel(nearest.getLabel());
                negativeVal = gamma* ((double)nearfreqs/(double)documents);
            }
            featureValues[i] = positiveVal - negativeVal;
            featureValues[i] = featureValues[i] * (Math.log(totalDocs) - Math.log(f.getPresences()));
            i++;
        }
    }
}
