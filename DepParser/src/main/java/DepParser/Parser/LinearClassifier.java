package DepParser.Parser;


import DepParser.Model.Action;
import DepParser.Model.Features;
import DepParser.Model.Model;
import DepParser.Model.State;


/**
 * Created by Marco Corona on 23/08/2017.
 */
public class LinearClassifier {

    private Model model ;

    public LinearClassifier(Model model){
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }


    public Action.Type getBestAction(State state){

        double max = Double.NEGATIVE_INFINITY;
        Action.Type best = Action.Type.NOP;
        Features fts = new Features(state);
        int [] featuresVector = fts.extract();
        Action.Type [] valid = state.getValidAction();
        double[] scores = new double[Action.Type.values().length-1];
        for(Action.Type action : valid){
            double score = model.getScore(action.getType(), featuresVector);
            scores[action.getType()]=score;
            if(score>max){
                best = action;
                max = score;
            }
        }

        return best;
    }



    public double[] getAllScores(State state){

        Features fts = new Features(state);
        int [] featuresVector = fts.extract();
        Action.Type [] valid = state.getValidAction();
        double[] scores = new double[Action.Type.values().length-1];
        for(Action.Type action : valid){
            double score = model.getScore(action.getType(), featuresVector);
            scores[action.getType()]=score;
        }
        return scores;
    }

}
