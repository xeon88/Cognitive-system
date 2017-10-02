/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DepParser.Parser.ArcEager;

import DepParser.Model.ArcEager;
import DepParser.Model.Tree;
import DepParser.Model.State;
import DepParser.Model.Sentence;
import DepParser.Parser.TBParser;

/**
 * @author theacidmc
 */


public class EagerParser extends TBParser{
    

    public EagerParser ( EagerClassifier classifier) {
        super(classifier);
    }


    @Override
    public Tree parse(Sentence s) {
        Tree tree = new Tree();
        State state = new State(s);
        while (!state.isTerminal()){
            ArcEager.Type predicted = (ArcEager.Type)classifier.getBestAction(state);
            state = predicted.apply(state);
        }
        tree.setDependencies(state.getArcs());
        return tree;
      
    }
    
    
}
