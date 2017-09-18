package DepParser.Model;


/**
 * Created by Marco Corona on 23/08/2017.
 */
public class Model {


    private float [][] weights ;
    private float [][] averageWeights;

    public Model(int operators){

        weights= new float[Features.hashSize][operators];

        for(int i = 0; i<weights.length ; i++){
            for (int j = 0; j<weights[i].length; j++){
                    weights[i][j]= (float) 0;
            }
        }
        averageWeights = weights.clone();
    }

    public void setWeights(float[][] weights){
        this.weights = weights;
    }

    public float[][] getWeights() {
        return weights;
    }

    public float[][] getAverageWeights() {
        return averageWeights;
    }


    public double getScore(int action, int [] featuresIndexes){

        double score = 0.0;
        for(int index = 0; index<featuresIndexes.length; index++){
            score += weights[featuresIndexes[index]][action]*featuresIndexes[index];
        }
        return score;
    }

    public void updateWeights( int [] features  , int action, int value){
        for(int index = 0; index<features.length ; index++){
            weights[features[index]][action]+= value;
        }
    }


    public void updateMeanWeights(int [] features,int action, int value){
        for(int index = 0; index<features.length ; index++){
            averageWeights[features[index]][action]+= value;
        }
    }



    public float[][] getResultWeights(int count){
        float [][] result = new float[Features.hashSize][weights[0].length];

            for(int hash = 0; hash<weights.length; hash++){
                for(int action=0; action<weights[hash].length ; action++){
                    result[hash][action] = weights[hash][action] - ((averageWeights[hash][action])/(float)count);
                }
            }

        return result;
    }

}
