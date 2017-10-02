package DepParser.Parser;

import DepParser.Model.Sentence;
import DepParser.Model.Tree;

/**
 * Created by Marco Corona on 01/08/2017.
 */


public abstract class TBParser {

    protected Classifier classifier;

    public TBParser(Classifier classifier){
        this.classifier = classifier;
    }

    public abstract Tree parse(Sentence s);

}
