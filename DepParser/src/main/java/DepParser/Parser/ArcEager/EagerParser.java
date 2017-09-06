/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DepParser.Parser.ArcEager;

import DepParser.Model.ArcEager;
import DepParser.Model.ProjectiveTree;
import DepParser.Model.State;
import DepParser.Parser.Sentence;
import DepParser.Parser.TBParser;

/**
 *
 * @author theacidmc
 */
public class EagerParser extends TBParser{
    
    
    private EagerClassifier classifier;
    
    public EagerParser ( EagerClassifier classifier) {
        super();
        this.classifier = classifier;
    }

    @Override
    public ProjectiveTree parse(Sentence s) {
        ProjectiveTree tree = new ProjectiveTree();
        State state = new State(s);
        while (!state.isTerminal()){
            ArcEager.Type predicted = classifier.getBestAction(state);
            System.out.println("Action predicted : "  + predicted.getName());
            state = predicted.apply(state);
        }
        tree.setDependencies(state.getArcs());
        return tree;
      
    }
    
    
}
