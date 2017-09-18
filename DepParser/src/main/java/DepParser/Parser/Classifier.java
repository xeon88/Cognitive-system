package DepParser.Parser;


import DepParser.Model.ArcSystem;
import DepParser.Model.Features;
import DepParser.Model.Model;
import DepParser.Model.State;


/**
 * Created by Marco Corona on 23/08/2017.
 */
public abstract class Classifier {

    protected Model model ;

    public Classifier(Model model){
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public abstract ArcSystem.operation getBestAction(State state);

}
