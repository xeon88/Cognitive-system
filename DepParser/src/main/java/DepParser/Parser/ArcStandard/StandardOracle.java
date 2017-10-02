package DepParser.Parser.ArcStandard;


import DepParser.Model.*;
import DepParser.Parser.Oracle;
import DepParser.Model.Sentence;
import DepParser.Utils.ConllReader;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Marco Corona on 30/08/2017.
 */
public class StandardOracle extends Oracle {

    public StandardOracle(){
        super();
    }

    public ArcStandard.Type[] getZeroCostAction(State state) {
        ArcStandard.Type [] appliable = (ArcStandard.Type[]) ArcStandard.getValidActions(state);
        ArrayList<ArcStandard.Type> zeroCost = new ArrayList<ArcStandard.Type>();
        for(ArcStandard.Type action : appliable){
            if(getCostAction(action,state)==0){
                zeroCost.add(action);
            }
        }
        return zeroCost.toArray(new ArcStandard.Type[zeroCost.size()]);
    }


    public int getCostAction(ArcSystem.transition action, State state) {
        return 0;
    }



    public ArcStandard.Type[] getReachableGoldTreeActions(State state) {
        ArrayList<ArcStandard.Type> goldReachable = new ArrayList<ArcStandard.Type>();
        for(ArcStandard.Type action : ArcStandard.Type.values()){
            if(this.getCostAction(action,state)==0){
                goldReachable.add(action);
            }
        }

        if(goldReachable.isEmpty()) return null;
        ArcStandard.Type [] actions = goldReachable.toArray(new ArcStandard.Type[goldReachable.size()]);
        return actions;
    }


    public Transition[] findGoldSeqs(Sentence s, GoldTree tree){
        LinkedHashMap<Integer, Transition> history = new LinkedHashMap<Integer,Transition>();
        State state = new State(s);
        int step = 0;

        while (!state.isTerminal()) {
            ArcStandard.Type oracle = getAction(state);
            state = oracle.apply(state);
            Transition tr = new Transition(state,oracle);
            step++;
            history.put(step,tr);
        }

        Transition[] goldActions = history.values().toArray(new Transition[history.size()]);
        return goldActions;
    }

    protected String getCostsString(int[] costs) {
        return null;
    }

    @Override
    public ArcStandard.Type getAction(State state) {

        /*
        if(state.getStack().isEmpty()){
            return ArcStandard.Type.SHIFT;
        }
        */


        Token topStack = state.getTopStack();
        Token firstBuffer = state.getFirstBuffer();
        Token[] tokens = state.getInput().tokens;
        Arc[] arcs = state.getArcs();


        if (ArcStandard.Type.isLeftAppliable(state) && topStack.getHead() == firstBuffer.getIndex()) {
            String relationTop = topStack.getValue(ConllReader.Conll.DEPREL.getName());

            if(relationTop.equals("nsubj")){
                return ArcStandard.Type.LEFT_NSUBJ;
            }
            if(relationTop.equals("dobj")){
                return ArcStandard.Type.LEFT_DOBJ;
            }
            if(relationTop.equals("noname")){
                return ArcStandard.Type.LEFT_OTHER;
            }

        }

        else if (ArcStandard.Type.isRightAppliable(state) && firstBuffer.getHead() == topStack.getIndex()
                && checkChilds(firstBuffer.getIndex(),state)){

            String relationFirst = firstBuffer.getValue(ConllReader.Conll.DEPREL.getName());

            if(relationFirst.equals("nsubj")){
                return ArcStandard.Type.RIGHT_NSUBJ;
            }
            if(relationFirst.equals("dobj")){
                return ArcStandard.Type.RIGHT_DOBJ;
            }
            if(relationFirst.equals("noname")){
                return ArcStandard.Type.RIGHT_OTHER;
            }
        }
        return ArcStandard.Type.SHIFT;
    }


    @Override
    public int [] getAllCostAction(State state){
        int [] costs = new int [ArcStandard.Type.values().length-1];
        for(ArcStandard.Type action : ArcStandard.Type.values()){
            if(action.getType()==-1) continue;
            costs[action.getType()]=getCostAction(action,state);
        }
        return costs;
    }


    public boolean checkChilds(int head, State state){
        Arc[] arcs = state.getArcs();
        Arc[] gold = goldTrees.get(state.getInput().id).getDependencies();
        for(Arc arc : gold ){
            if(arc.getHead().getIndex()==head){
                if(arcs[arc.getDependent().getIndex()-1]==null){
                    return false;
                }
            }
        }
        return true;
    }

}
