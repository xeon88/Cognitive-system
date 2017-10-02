package DepParser.Parser.ArcStandard;

import DepParser.Model.*;
import DepParser.Parser.ConllStorage;
import DepParser.Model.Sentence;
import DepParser.Parser.Trainer;

/**
 * Created by Marco Corona on 30/08/2017.
 */
public class StandardTrainer extends Trainer{

    public StandardTrainer(){
        super(ArcStandard.SIZE);
        this.oracle = new StandardOracle();
        this.classifier = new StandardClassifier(this.model);
    }


    public synchronized void addGoldTree(Sentence s,GoldTree tree){
        oracle.addGoldTree(s, tree);
        for(int i=0; i<epoch;i++){
            State state = new State(s);
            while(!state.isTerminal()){
                ArcStandard.Type predictedAction = (ArcStandard.Type) classifier.getBestAction(state);
                ArcStandard.Type oracleAction = (ArcStandard.Type)oracle.getAction(state);
                int[] features = new Features(state).extract();
                state = oracleAction.apply(state);
                if (predictedAction != oracleAction) {
                    updates(features,oracleAction.getType(),predictedAction.getType(),count);
                    classifier.setModel(model);
                }
                count++;
            }
        }
    }
}
