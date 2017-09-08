package DepParser.Parser.Mazzei;

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


    public synchronized void train(GoldTree gold, Sentence s) throws IOException {

        Logging log = new Logging();

        // update oracle with gold tree

        StringBuilder logBuilder = new StringBuilder();
        oracle.addGoldTree(s, gold);

        State state = new State(s);

        //logBuilder.append("TRAIN ALGORITHM PROCEDURE " + s.id + "\n\n");
        //System.out.println("Action : \n" + PrintUltis.toString(Action.getAllActionName(Action.Type.values())));
        //log.log(logBuilder.toString(), Logging.DEBUG);


        for( int i = 0; i<oracle.getLength(s.id); i++) {

            /*
            // cloning state;
            logBuilder.append("Step number :" + state.getStep() + " - test : " + test +"\n\n");
            int [] costs = oracle.getAllCostAction(state, s);
            logBuilder.append(oracle.getCostsString(costs) + "\n\n");
            */

            ArcStandard.Type[] appliable = (ArcStandard.Type[]) ArcStandard.getValidAction(state);
            int[] features = new Features(state).extract();
            ArcStandard.Type predictedAction = classifier.getBestAction(state);
            ArcStandard.Type oracleAction = (ArcStandard.Type)oracle.getOracleAction(s.id,i);

            int cost = 0;
            if((cost = oracle.getCostAction(oracleAction,s.id,state))>0){
                logBuilder.append("Appliable actions : " + ArcStandard.getAllActionName(appliable) + "\n");
                logBuilder.append("predicted action : " + predictedAction.getName() + "-" + predictedAction.getRelation() + "\n");
                logBuilder.append("oracle action : " + oracleAction.getName() + "-" + oracleAction.getRelation() + "\n");
                logBuilder.append("cost of oracle action :" + cost + "\n");
                System.out.println("[ERROR!] Action not valid : " +  cost);
            }

            state = oracleAction.apply(state);

            /*
            logBuilder.append("Compare actions : \n");
            logBuilder.append("Predicted action : " + predictedAction.getName() + "\n");
            logBuilder.append("Oracle action : " + oracleAction.getName() + "\n");
            */
            //System.out.println("Predicted action : " + predictedAction.getName() + "\n");
            //System.out.println("Oracle action : " + oracleAction.getName() + "\n");


            // update weights if predicted and oracle action doesn't match

            if (predictedAction != oracleAction) {
                updates(features,oracleAction.getType(),predictedAction.getType(),count);
                classifier.setModel(model);
            }


            count++;
            log.log(logBuilder.toString(), Logging.DEBUG);
            logBuilder = new StringBuilder();
        }


        System.out.println("gold seqs n : " + s.id + " done ...");

    }
}
