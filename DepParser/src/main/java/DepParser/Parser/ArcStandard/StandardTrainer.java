package DepParser.Parser.ArcStandard;

import DepParser.Model.*;
import DepParser.Parser.Sentence;
import DepParser.Parser.Trainer;
import DepParser.Utils.Logging;

import java.io.IOException;

/**
 * Created by Marco Corona on 30/08/2017.
 */
public class StandardTrainer extends Trainer{

    private StandardOracle oracle;
    private StandardClassifier classifier;

    public StandardTrainer(int operators){
        super(operators);
        this.oracle = new StandardOracle();
        this.classifier = new StandardClassifier(this.model);
    }

    public StandardClassifier getClassifier() {
        return classifier;
    }


    public synchronized void train(GoldTree gold, Sentence s){

        oracle.addGoldTree(s, gold);
        State state = new State(s);
        for( int i = 0; i<oracle.getLength(s.id); i++) {
            ArcStandard.Type predictedAction = classifier.getBestAction(state);
            ArcStandard.Type oracleAction = (ArcStandard.Type)oracle.getOracleAction(s.id,i);
            state = oracleAction.apply(state);
            if (predictedAction != oracleAction) {
                int[] features = new Features(state).extract();
                updates(features,oracleAction.getType(),predictedAction.getType(),count);
                classifier.setModel(model);
            }
            count++;
        }
    }
}
