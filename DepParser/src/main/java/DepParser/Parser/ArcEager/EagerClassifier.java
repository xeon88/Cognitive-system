/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DepParser.Parser.ArcEager;

import DepParser.Model.ArcEager;
import DepParser.Model.Features;
import DepParser.Model.Model;
import DepParser.Model.State;
import DepParser.Parser.Classifier;

/**
 *
 * @author theacidmc
 */
public class EagerClassifier extends Classifier{
    
    
    
    public EagerClassifier(Model model) {
        super(model);
    }

    
    public ArcEager.Type getBestAction(State state){

        double max = Double.NEGATIVE_INFINITY;
        ArcEager.Type best = ArcEager.Type.NOP;
        Features fts = new Features(state);
        int [] featuresVector = fts.extract();
        ArcEager.Type [] valid = ArcEager.getValidAction(state);
        double[] scores = new double[ArcEager.Type.values().length-1];
        for(ArcEager.Type action : valid){
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
        ArcEager.Type [] valid = ArcEager.getValidAction(state);
        double[] scores = new double[ArcEager.Type.values().length-1];
        for(ArcEager.Type action : valid){
            double score = model.getScore(action.getType(), featuresVector);
            scores[action.getType()]=score;
        }
        return scores;
    }

}
