package DepParser.Parser.ArcStandard;

import DepParser.Model.*;
import DepParser.Parser.Classifier;

/**
 * Created by Marco Corona on 06/09/2017.
 */
public class StandardClassifier extends Classifier {

    public StandardClassifier(Model model) {
        super(model);
    }


    public ArcStandard.Type getBestAction(State state){

        double max = Double.NEGATIVE_INFINITY;
        ArcStandard.Type best = ArcStandard.Type.NOP;
        Features fts = new Features(state);
        int [] featuresVector = fts.extract();
        ArcStandard.Type [] valid = (ArcStandard.Type[]) ArcStandard.getValidActions(state);
        double[] scores = new double[ArcEager.Type.values().length-1];
        for(ArcStandard.Type action : valid){
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
        ArcStandard.Type [] valid = (ArcStandard.Type[]) ArcStandard.getValidActions(state);
        double[] scores = new double[ArcEager.Type.values().length-1];
        for(ArcStandard.Type action : valid){
            double score = model.getScore(action.getType(), featuresVector);
            scores[action.getType()]=score;
        }
        return scores;
    }
}
