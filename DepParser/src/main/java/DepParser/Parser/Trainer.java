package DepParser.Parser;

import DepParser.Model.*;

import java.util.*;

/**
 * Created by Marco Corona on 09/08/2017.
 */
public abstract  class Trainer{

    protected Model model;   
    protected int count;

    public Trainer(int operators) {

        this.model = new Model(operators);
        count = 1;
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
}


