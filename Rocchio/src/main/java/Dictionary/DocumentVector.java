package Dictionary;

import java.util.HashMap;


/**
 * Created by Marco Corona on 06/04/2017.
 */


public class DocumentVector {

    protected   double[] featureValues;
    protected int size;
    protected String label;


    public DocumentVector(String label){
        this.label = label;
        this.size = 1 ;
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




    public void makeArrayForDataVector(WordDictionary dict , int totalDocs){
        int i = 0;

        this.featureValues = new double[dict.getWordsMap().size()];
        for(String key : dict.getWordsMap().keySet()){
            int freqs = 0;
            Features f = dict.getWordsMap().get(key);
            if(f.getLabelFreq().keySet().contains(label)){
                freqs = f.getLabelFreq().get(label);
            }
            featureValues[i] = freqs*(Math.log(totalDocs) - Math.log(f.getPresence()));
            i++;
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
            double product = vector[i]* featureValues[i];
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
        return variance;
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
