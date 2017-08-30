package DepParser.Parser;

import DepParser.Model.Action;
import DepParser.Model.ProjectiveTree;
import DepParser.Model.State;

/**
 * Created by Marco Corona on 01/08/2017.
 */
public class TransitionBasedParser {


     private LinearClassifier classifier;


    public TransitionBasedParser(LinearClassifier classifier){
        this.classifier = classifier;
    }


    public ProjectiveTree parse(Sentence sentence){
        ProjectiveTree tree = new ProjectiveTree();
        State state = new State(sentence);
        while (!state.isTerminal()){
            Action.Type predicted = classifier.getBestAction(state);
            System.out.println("Action predicted : "  + predicted.getName());
            state = state.applyAction(predicted);
        }
        tree.setDependencies(state.getArcs());
        return tree;
    }
}
