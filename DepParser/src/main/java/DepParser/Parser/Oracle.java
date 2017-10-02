package DepParser.Parser;

import DepParser.Model.*;
import DepParser.Utils.PrintUltis;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Marco Corona on 09/08/2017.
 */


public abstract class Oracle {

    protected HashMap<Integer, GoldTree> goldTrees;

    public Oracle() {
        goldTrees = new HashMap<Integer, GoldTree>();
    }

    public HashMap<Integer, GoldTree> getGoldTrees() {
        return goldTrees;
    }


    public ArcSystem.transition getOracleAction(int sentence, int step) {
        Transition[] seqs = goldTrees.get(sentence).getGoldSeqs();
        return seqs[step].getAction();
    }


    public int getLength(int i) {
        return goldTrees.get(i).getGoldSeqs().length;
    }

    public void addGoldTree(Sentence s, GoldTree gold) {
        goldTrees.put(s.id, gold);
        Transition[] goldseqs = findGoldSeqs(s, gold);
        gold.setGoldSeqs(goldseqs);
        goldTrees.put(s.id, gold);
    }


    public StringBuilder appendCostInfo(StringBuilder logBuilder, State state) throws IOException {
        logBuilder.append("topstack : " + state.getTopStack().getIndex() + "\n");
        logBuilder.append("firstbuffer : " + state.getFirstBuffer().getIndex() + "\n");
        logBuilder.append("arcs : " + PrintUltis.toString(state.getArcs(), state.getFirstBuffer().getIndex()) + "\n\n");
        logBuilder.append("STACK : \n" + PrintUltis.toString(state.getStack()) + "\n\n");
        logBuilder.append("BUFFER : \n" + PrintUltis.toString(state.getBuffer()) + "\n\n");
        logBuilder.append("Costs actions \n:" + getCostsString(this.getAllCostAction(state)));
        return logBuilder;
    }


    public abstract Transition[] findGoldSeqs(Sentence s, GoldTree tree);

    protected abstract String getCostsString(int[] costs);

    public abstract int[] getAllCostAction(State state);

    public abstract ArcSystem.transition[] getReachableGoldTreeActions(State state);

    public abstract ArcSystem.transition getAction(State state);

    public abstract ArcSystem.transition[] getZeroCostAction(State s);

    public abstract int getCostAction(ArcSystem.transition action, State state);

}
