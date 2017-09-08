package DepParser.Parser;

import DepParser.Model.*;
import DepParser.Utils.Logging;
import DepParser.Utils.PrintUltis;
import DepParser.Utils.UDBankReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Marco Corona on 09/08/2017.
 */
public abstract class Oracle {

    protected HashMap<Integer,GoldTree> goldTrees;

    public Oracle(){
        goldTrees = new HashMap<Integer, GoldTree>();
    }


    public HashMap<Integer, GoldTree> getGoldTrees() {
        return goldTrees;
    }


    public ArcSystem.operation getOracleAction(int sentence, int step){

        Transition[] seqs = goldTrees.get(sentence).getGoldSeqs();
        return seqs[step].getAction();
    }


    public int getLength(int i){
        return goldTrees.get(i).getGoldSeqs().length;
    }

    public abstract ArcSystem.operation [] getZeroCostAction(int sequenceId, State s) throws IOException;

    /**
     * Compute the loss cost of an action when it was applied in State state
     * @return cost value
    */

    public abstract int getCostAction(ArcSystem.operation action, int sequenceId , State state) throws IOException;
    /**
     *
     * @return
    */


    public abstract ArcSystem.operation [] getReachableGoldTreeActions(State state, int sequenceId) throws IOException;



    public void addGoldTree(Sentence s, GoldTree gold) throws IOException {
        goldTrees.put(s.id,gold);
     //   boolean projective = gold.isProjectiveTree();
     //   System.out.println("Gold tree number" + s.id  +" is : " + (projective ? "projective \n": "not projective \n"));
        Transition [] goldseqs = findGoldSeqs(s,gold);
        gold.setGoldSeqs(goldseqs);
        goldTrees.put(s.id,gold);
    }


    public abstract Transition[] findGoldSeqs (Sentence s , GoldTree tree) throws IOException;

    protected abstract String getCostsString(int [] costs);

    protected abstract ArcSystem.operation getAction(State state);

}
