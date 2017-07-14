package Dictionary;


/**
 * Created by Marco Corona on 06/04/2017.
 */


public class DocumentVector {

    protected   double[] featureValues;
    protected String label;


    public DocumentVector(String label){
        this.label = label;
        this.featureValues = new double[1];
    }

    public String getLabel() {
         return label;
    }
    public double[] getFeatureValues() {
        return featureValues;
    }

    public void setFeatureValues(double [] values){

        this.featureValues = values;
    }




    public void makeDataVector(WordDictionary dict , int totalDocs){


        this.featureValues = new double[dict.getWordsMap().size()];
        String [] keys = dict.getWordKeys();
        for(int i=0; i<keys.length; i++ ){
            int freqs = 0;
            Feature f = dict.getWordsMap().get(keys[i]);
            if(f.getTextFreqs().keySet().contains(label)){
                freqs = f.getTextFreqs().get(label);
            }
            featureValues[i] = freqs*(Math.log(totalDocs) - Math.log(f.getPresences()));
        }
    }



    public double normInf(){
        double norm = Double.MIN_VALUE;
        for(int i = 0; i< featureValues.length ; i++){
            if(norm< featureValues[i]){
                norm = featureValues[i];
            }
        }
        return norm;
    }


    public double norm1(){
        double norm = 0;
        for(int i = 0; i< featureValues.length ; i++){
            norm+= featureValues[i];
        }
        return norm;
    }



    public double norm2(){
        double norm = 0;
        for(int i = 0; i< featureValues.length ; i++){
            norm+= Math.pow(featureValues[i],2);
        }
        return Math.sqrt(norm);
    }



    public double similarity(DocumentVector t1){
        double [] vector = t1.getFeatureValues();
        double cosineDistance = 0;
        double dotProduct = 0;
        for (int i = 0; i< featureValues.length ; i++){
            double product = vector[i]*featureValues[i];
            dotProduct += product;
        }
        double NormsProduct = norm2()*t1.norm2();
        cosineDistance = dotProduct/NormsProduct;
        return cosineDistance;
    }


    public double euclidianDistance(DocumentVector t1){
        double [] vector = t1.getFeatureValues();
        double euclidean = 0;
        double variance = 0;
        for(int i=0; i<featureValues.length ; i++){
            variance+=Math.pow(featureValues[i]-vector[i],2);
        }
        euclidean = Math.sqrt(variance);
        return euclidean;
    }

    @Override
    public String toString(){
        String vector = "";
        vector += "label="+label+"\n";
        vector += "vec=[";
        for(int i=0; i<featureValues.length-1; i++){
            vector+=featureValues[i]+",";
        }
        vector+=featureValues[featureValues.length-1]+"]\n";
        return vector;
    }
}
