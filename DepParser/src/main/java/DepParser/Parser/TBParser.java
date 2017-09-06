package DepParser.Parser;

import DepParser.Model.ArcSystem;
import DepParser.Model.ProjectiveTree;
import DepParser.Model.State;

/**
 * Created by Marco Corona on 01/08/2017.
 */
public abstract class TBParser {

    public TBParser(){}

    public abstract ProjectiveTree parse(Sentence s);
}
