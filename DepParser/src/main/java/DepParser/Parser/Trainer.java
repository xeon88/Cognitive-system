package DepParser.Parser;

import DepParser.Utils.Logging;
import DepParser.Model.*;
import DepParser.Utils.PrintUltis;

import java.io.IOException;
import java.util.*;

/**
 * Created by Marco Corona on 09/08/2017.
 */
public class Trainer {

    private Model model;
    private Oracle oracle;
    private LinearClassifier classifier;

    private int count;


    public Trainer() {

        this.model = new Model();
        this.oracle = new Oracle();
        this.classifier = new LinearClassifier(model);
        count = 1;
    }


    public Model getModel() {
        return model;
    }

    public Oracle getOracle() {
        return oracle;
    }

    public LinearClassifier getClassifier() {
        return classifier;
    }


    public void train(GoldTree gold, Sentence s) throws IOException {

        Logging log = new Logging();

        // update oracle with gold tree

        StringBuilder logBuilder = new StringBuilder();
        oracle.addGoldTree(s, gold);

        State state = new State(s);

        //System.out.println("Action : \n" + PrintUltis.toString(Action.getAllActionName(Action.Type.values())));

        while (!state.isTerminal()) {

            // cloning state;

            logBuilder.append("Step number :" + state.getNseq() + "\n");
            Action.Type[] appliable = state.getValidAction();
            Action.Type predictedAction = classifier.getBestAction(state);
            Action.Type oracleAction = oracle.getOracleAction(s.id, state.getNseq());
            int[] features = new Features(state).extract();

            int cost = 0;
            if((cost = oracle.getCostAction(oracleAction,s.id,state))>0){
                System.out.println("[ERROR!]Action not valid : " +  cost);
            };

            state = state.applyAction(oracleAction);

            //logBuilder.append("Predicted action : " + predictedAction.getName() + "\n");
            //logBuilder.append("Oracle action : " + oracleAction.getName() + "\n");

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
          //  log.log(logBuilder.toString(), Logging.DEBUG);
        }


        System.out.println("gold seqs n : " + s.id + " done ...");

    }


    private State cloneState(Sentence s, State state) {

        Stack<Token> stack = (Stack<Token>) state.getStack().clone();
        LinkedList<Token> buffer = (LinkedList<Token>) state.getBuffer().clone();
        Dependency[] arcs = state.getArcs().clone();
        Token first = state.getFirstBuffer().clone();
        Token top = state.getTopStack().clone();
        int nseq = state.getNseq();
        boolean rooted = state.isRooted();
        return new State(stack, buffer, arcs, top, first, s, nseq, rooted);
    }

}


