package DepParser.Parser.ArcStandard;

import DepParser.Model.ArcStandard;
import DepParser.Model.Tree;
import DepParser.Model.State;
import DepParser.Model.Sentence;
import DepParser.Parser.TBParser;

/**
 * Created by Marco Corona on 06/09/2017.
 */
public class StandardParser extends TBParser{

    public StandardParser( StandardClassifier classifier) {
        super(classifier);
    }

    @Override
    public Tree parse(Sentence s) {
        Tree tree = new Tree();
        State state = new State(s);
        while (!state.isTerminal()){
            ArcStandard.Type predicted = (ArcStandard.Type) classifier.getBestAction(state);
            state = predicted.apply(state);
        }
        tree.setDependencies(state.getArcs());
        return tree;

    }

}
