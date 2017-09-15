package DepParser.Parser.ArcEager;

import DepParser.Model.*;
import DepParser.Parser.Sentence;
import DepParser.Parser.Trainer;

import java.io.IOException;

/**
 * Created by Marco Corona on 30/08/2017.
 *
 */


public class EagerTrainer extends Trainer{

    private EagerOracle oracle;
    private EagerClassifier classifier;

    public EagerTrainer(int operators){
        super(operators);
        this.oracle = new EagerOracle();
        this.classifier = new EagerClassifier(this.model);
    }

    public EagerClassifier getClassifier() {
        return classifier;
    }


    public synchronized void train(GoldTree gold, Sentence s){

        oracle.addGoldTree(s, gold);
        State state = new State(s);
        while (!state.isTerminal()) {
            ArcEager.Type predictedAction = classifier.getBestAction(state);
            ArcEager.Type oracleAction = oracle.getAction(state);
            state = oracleAction.apply(state);
            if(oracleAction!=predictedAction){
                int[] features = new Features(state).extract();
                updates(features,oracleAction.getType(),predictedAction.getType(),count);
                classifier.setModel(model);
            }
            count++;
        }

    }


}
