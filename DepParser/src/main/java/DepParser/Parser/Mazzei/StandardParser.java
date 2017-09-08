package DepParser.Parser.Mazzei;

import DepParser.Model.ArcStandard;
import DepParser.Model.ProjectiveTree;
import DepParser.Model.State;
import DepParser.Parser.Sentence;
import DepParser.Parser.TBParser;

/**
 * Created by Marco Corona on 06/09/2017.
 */
public class StandardParser extends TBParser{

    private StandardClassifier classifier;

    public StandardParser(StandardClassifier classifier) {
        super();
        this.classifier = classifier;
    }

    @Override
    public ProjectiveTree parse(Sentence s) {
        ProjectiveTree tree = new ProjectiveTree();
        State state = new State(s);
        while (!state.isTerminal()){
            ArcStandard.Type predicted = classifier.getBestAction(state);
            //System.out.println("Action predicted : "  + predicted.getName());
            state = predicted.apply(state);
        }
        tree.setDependencies(state.getArcs());
        return tree;

    }

}
