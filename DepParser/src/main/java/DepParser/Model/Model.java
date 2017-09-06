package DepParser.Model;


/**
 * Created by Marco Corona on 23/08/2017.
 */
public class Model {


    private float [][][] weights ;
    private float [][][] averageWeights;

    public Model(int operators){

        weights= new float[Features.size][Features.hashSize][operators];

        for(int i = 0; i<weights.length ; i++){
            for (int j = 0; j<weights[i].length; j++){
                for(int k=0; k<weights[i][j].length; k++){
                    weights[i][j][k]= (float) 0;
                }
            }
        }

        averageWeights = weights.clone();
    }

    public float[][][] getWeights() {
        return weights;
    }

    public float[][][] getAverageWeights() {
        return averageWeights;
    }


    public double getScore(int action, int [] featuresIndexes){

        double score = 0.0;
        for(int index = 0; index<averageWeights.length; index++){
            score += averageWeights[index][featuresIndexes[index]][action]*featuresIndexes[index];
        }
        return score;
    }


    public void updateWeights( int [] features  , int action, int value){
        for(int index = 0; index<features.length ; index++){
            weights[index][features[index]][action]+= value;
        }
    }


    public void updateMeanWeights(int n){
        for(int feature = 0; feature<averageWeights.length ; feature++){
            for(int hash = 0; hash<averageWeights[feature].length; hash++){
                for(int action=0; action<averageWeights[feature][hash].length ; action++){
                    averageWeights[feature][hash][action] =
                        ( averageWeights[feature][hash][action]*(float)n-1 + weights[feature][hash][action])/(float)n;
                }
            }
        }

    }

}
