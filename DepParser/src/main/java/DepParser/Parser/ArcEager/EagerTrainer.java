package DepParser.Parser.ArcEager;

import DepParser.Parser.ArcEager.EagerOracle;
import DepParser.Model.*;
import DepParser.Parser.Classifier;
import DepParser.Parser.Sentence;
import DepParser.Parser.Trainer;
import DepParser.Utils.Logging;
import DepParser.Utils.PrintUltis;

import java.io.IOException;

/**
 * Created by Marco Corona on 30/08/2017.
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


    public synchronized void train(GoldTree gold, Sentence s) throws IOException {

        Logging log = new Logging();

        // update oracle with gold tree

        StringBuilder logBuilder = new StringBuilder();
        oracle.addGoldTree(s, gold);

        State state = new State(s);
        int test = 0;

        logBuilder.append("TRAIN ALGORITHM PROCEDURE " + s.id +"\n");
        //System.out.println("Action : \n" + PrintUltis.toString(Action.getAllActionName(Action.Type.values())));
        log.log(logBuilder.toString(), Logging.DEBUG);

        logBuilder = new StringBuilder();

        for( int i = 0; i<oracle.getLength(s.id); i++) {

            /*
            // cloning state;
            logBuilder.append("Step number :" + state.getStep() + " - test : " + test +"\n\n");
            int [] costs = oracle.getAllCostAction(state, s);
            logBuilder.append(oracle.getCostsString(costs) + "\n\n");
            */

            ArcEager.Type[] appliable = ArcEager.getValidAction(state);
            int[] features = new Features(state).extract();
            ArcEager.Type predictedAction = classifier.getBestAction(state);
            ArcEager.Type oracleAction = (ArcEager.Type)oracle.getOracleAction(s.id,i);

            int cost = 0;
            if((cost = oracle.getCostAction(oracleAction,s.id,state))>0){
                logBuilder.append("Appliable actions : " + ArcEager.getAllActionName(appliable) + "\n");
                logBuilder.append("predicted action : " + predictedAction.getName() + "-" + predictedAction.getRelation() + "\n");
                logBuilder.append("oracle action : " + oracleAction.getName() + "-" + oracleAction.getRelation() + "\n");
                logBuilder.append("cost of oracle action :" + cost + "\n");
                System.out.println("[ERROR!] Action not valid : " +  cost);
            }

            state = oracleAction.apply(state);

            logBuilder.append("Predicted action : " + predictedAction.getName() + "\n\n");
            logBuilder.append("Oracle action : " + oracleAction.getName() + "\n\n");

            //System.out.println("Predicted action : " + predictedAction.getName() + "\n");
            //System.out.println("Oracle action : " + oracleAction.getName() + "\n");


            // update weights if predicted and oracle action doesn't match

            if (predictedAction != oracleAction) {
                model.updateWeights(features, oracleAction.getType(), 1);
                model.updateWeights(features, predictedAction.getType(), -1);
                classifier.setModel(model); // update classifier with new weights
                //logBuilder.append(matrixToString(deltaMatrix));
            }

            if (count > 1) {
                model.updateMeanWeights(count);
            }

            count++;
            log.log(logBuilder.toString(), Logging.DEBUG);
            logBuilder = new StringBuilder();
        }


        System.out.println("gold seqs n : " + s.id + " done ...");

    }
}
