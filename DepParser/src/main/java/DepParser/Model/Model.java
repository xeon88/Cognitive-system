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

    public void setWeights(float[][][] weights){
        this.weights = weights;
    }

    public float[][][] getWeights() {
        return weights;
    }

    public float[][][] getAverageWeights() {
        return averageWeights;
    }


    public double getScore(int action, int [] featuresIndexes){

        double score = 0.0;
        for(int index = 0; index<weights.length; index++){
            score += weights[index][featuresIndexes[index]][action]*featuresIndexes[index];
        }
        return score;
    }

    public void updateWeights( int [] features  , int action, int value){
        for(int index = 0; index<features.length ; index++){
            weights[index][features[index]][action]+= value;
        }
    }


    public void updateMeanWeights(int [] features,int action, int value){
        for(int index = 0; index<features.length ; index++){
            averageWeights[index][features[index]][action]+= value;
        }
    }



    public float[][][] getResultWeights(int count){
        float [][][] result = new float[Features.size][Features.hashSize][weights[0][0].length];
        for(int feature = 0; feature<weights.length ; feature++){
            for(int hash = 0; hash<weights[feature].length; hash++){
                for(int action=0; action<weights[feature][hash].length ; action++){
                    result[feature][hash][action] = weights[feature][hash][action] - ((averageWeights[feature][hash][action])/(float)count);
                }
            }
        }

        return result;
    }

}
