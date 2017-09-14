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
        int step = 0;

        /*
        logBuilder.append("TRAIN ALGORITHM PROCEDURE " + s.id + "\n\n");
        //System.out.println("Action : \n" + PrintUltis.toString(Action.getAllActionName(Action.Type.values())));
        log.log(logBuilder.toString(), Logging.DEBUG);
        */
        while (!state.isTerminal()) {


            /*
            logBuilder.append("Step number :" + state.getStep() + " - test : " + test +"\n\n");
            int [] costs = oracle.getAllCostAction(state, s);
            logBuilder.append(oracle.getCostsString(costs) + "\n\n");
            */

            ArcEager.Type[] appliable = ArcEager.getValidAction(state);
            int[] features = new Features(state).extract();
            ArcEager.Type predictedAction = classifier.getBestAction(state);
            ArcEager.Type oracleAction = oracle.getAction(state);

            int cost = 0;
            if((cost = oracle.getCostAction(oracleAction,state))>0){
                logBuilder.append("transition step : " + step + "\n");
                logBuilder.append("Appliable actions : " + PrintUltis.toString(ArcEager.getAllActionName(appliable)) + "\n");
                logBuilder.append("predicted action : " + predictedAction.getName() + "-" + predictedAction.getRelation() + "\n");
                logBuilder.append("oracle action : " + oracleAction.getName() + "-" + oracleAction.getRelation() + "\n");
                logBuilder.append("cost of oracle action :" + cost + "\n");
                System.out.println("[ERROR!] Action not valid : " +  cost + " on step " + count );
            }


            //logBuilder.append("Compare actions : \n");
            //logBuilder.append("Predicted action : " + predictedAction.getName() + "\n");
            //logBuilder.append("Oracle action : " + oracleAction.getName() + "\n");


            // update weights if predicted and oracle action doesn't match

            state = oracleAction.apply(state);

            if(oracleAction!=predictedAction){
                updates(features,oracleAction.getType(),predictedAction.getType(),count);
                classifier.setModel(model);
            }
            /*
            if (!oracle.isZeroCost(predictedAction,state)) {
                updates(features,oracleAction.getType(),predictedAction.getType(),count);
                classifier.setModel(model);
            }
            else{
                state = predictedAction.apply(state);
            }
            */

            step++;
            count++;
            log.log(logBuilder.toString(), Logging.DEBUG);
            logBuilder = new StringBuilder();
        }


        System.out.println("gold seqs n : " + s.id + " done ...");

    }
}
