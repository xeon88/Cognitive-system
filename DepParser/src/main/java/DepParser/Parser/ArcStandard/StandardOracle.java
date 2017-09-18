package DepParser.Parser.ArcStandard;


import DepParser.Model.*;
import DepParser.Parser.Oracle;
import DepParser.Parser.Sentence;
import DepParser.Utils.UDBankReader;

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
        ArcStandard.Type [] appliable = (ArcStandard.Type[]) ArcStandard.getValidAction(state);
        ArrayList<ArcStandard.Type> zeroCost = new ArrayList<ArcStandard.Type>();
        for(ArcStandard.Type action : appliable){
            if(getCostAction(action,state)==0){
                zeroCost.add(action);
            }
        }
        return zeroCost.toArray(new ArcStandard.Type[zeroCost.size()]);
    }


    public int getCostAction(ArcSystem.operation action, State state) {
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

        if(state.getStack().isEmpty()){
            return ArcStandard.Type.SHIFT;
        }

        Token topStack = state.getTopStack();
        Token firstBuffer = state.getFirstBuffer();
        Token[] tokens = state.getInput().tokens;
        Dependency[] arcs = state.getArcs();


        if (topStack.getHead() == firstBuffer.getIndex() && ArcStandard.Type.isLeftAppliable(state)) {
            if(topStack.getValue(UDBankReader.UDIndex.DEPREL.getName()).equals("nsubj")){
                return ArcStandard.Type.LEFT_NSUBJ;
            }
            if(topStack.getValue(UDBankReader.UDIndex.DEPREL.getName()).equals("dobj")){
                return ArcStandard.Type.LEFT_DOBJ;
            }
            if(topStack.getValue(UDBankReader.UDIndex.DEPREL.getName()).equals("noname")){
                return ArcStandard.Type.LEFT_OTHER;
            }

        }

        else if (firstBuffer.getHead() == topStack.getIndex()
                && ArcStandard.Type.isRightAppliable(state)
                && checkChilds(firstBuffer.getIndex(),state)){

            if(firstBuffer.getValue(UDBankReader.UDIndex.DEPREL.getName()).equals("nsubj")){
                return ArcStandard.Type.RIGHT_NSUBJ;
            }
            if(firstBuffer.getValue(UDBankReader.UDIndex.DEPREL.getName()).equals("dobj")){
                return ArcStandard.Type.RIGHT_DOBJ;
            }
            if(firstBuffer.getValue(UDBankReader.UDIndex.DEPREL.getName()).equals("noname")){
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
        Dependency[] arcs = state.getArcs();
        Dependency [] gold = goldTrees.get(state.getInput().id).getDependencies();
        for(Dependency arc : gold ){
            if(arc.getHead().getIndex()==head){
                if(arcs[arc.getDependent().getIndex()-1]==null){
                    return false;
                }
            }
        }
        return true;
    }

}
