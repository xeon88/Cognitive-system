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



    public EagerTrainer(){
        super(ArcEager.SIZE);
        this.oracle = new EagerOracle();
        this.classifier = new EagerClassifier(this.model);
    }




    public synchronized void train(GoldTree gold, Sentence s){
        oracle.addGoldTree(s, gold);
        State state = new State(s);
        while (!state.isTerminal()) {
            ArcEager.Type predictedAction = (ArcEager.Type)classifier.getBestAction(state);
            ArcEager.Type oracleAction = (ArcEager.Type) oracle.getAction(state);
            int[] features = new Features(state).extract();
            state = oracleAction.apply(state);
            if(oracleAction!=predictedAction){
                updates(features,oracleAction.getType(),predictedAction.getType(),count);
                classifier.setModel(model);
            }
            count++;
        }

    }


}
