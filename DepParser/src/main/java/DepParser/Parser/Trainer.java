package DepParser.Parser;

import DepParser.Model.*;
import DepParser.Utils.PrintUltis;

import java.util.*;

/**
 * Created by Marco Corona on 09/08/2017.
 *
 */


public abstract  class Trainer implements ConllStorage{

    protected Oracle oracle;
    protected Classifier classifier;
    protected Model model;   
    protected int count;
    protected int epoch;

    public Trainer(int operators) {

        this.model = new Model(operators);
        count = 1;
        epoch = 50;
    }


    public Classifier getClassifier() {
        return classifier;
    }

    public Oracle getOracle() {
        return oracle;
    }

    public Model getModel() {
        return model;
    }

    public int getCount() {
        return count;
    }


    public void updates(int [] features, int oracle, int predicted, int count ){
        model.updateWeights(features, oracle, 1);
        model.updateWeights(features, predicted, -1);
        model.updateMeanWeights(features,oracle,count);
        model.updateMeanWeights(features,predicted,-count);
    }


    public void updates(int [] features, int oracle, int predicted, int count , int max){
        model.updateWeights(features, oracle, 1);
        model.updateWeights(features, predicted, max==0 ? 1 : -max);
        model.updateMeanWeights(features,oracle,count);
        model.updateMeanWeights(features,predicted,max==0 ? count : -max*count);
    }

    public void setResultWeights(){
        float [][][] result = model.getResultWeights(count);
        model.setWeights(result);
        classifier.setModel(model);
    }
}


