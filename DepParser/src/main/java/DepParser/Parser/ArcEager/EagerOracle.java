package DepParser.Parser.ArcEager;

import DepParser.Model.*;
import DepParser.Parser.Oracle;
import DepParser.Model.Sentence;
import DepParser.Utils.ConllReader;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Marco Corona on 30/08/2017.
 */
public class EagerOracle extends Oracle {

    
    public EagerOracle (){
        super();
    }

    @Override
    public synchronized  ArcEager.Type[] getZeroCostAction(State state) {
        ArcEager.Type [] appliable = ArcEager.getValidActions(state);
        ArrayList<ArcEager.Type> zeroCost = new ArrayList<ArcEager.Type>();
        for(ArcEager.Type action : appliable){
            if(getCostAction(action,state)==0){
                zeroCost.add(action);
            }
        }
        return zeroCost.toArray(new ArcEager.Type[zeroCost.size()]);
    }


    /**
     *  Compute the total cost of an action when it's applied on a state
     *  It depends from the type of transition system adopted
     * @param action
     * @param state
     * @return
     *
     */

    @Override
    public synchronized int getCostAction(ArcSystem.transition action, State state){

        GoldTree goldTree = this.goldTrees.get(state.getInput().id);
        Token top = state.getTopStack();
        Token first = state.getFirstBuffer();
        Arc[] gold = goldTree.getDependencies();
        int cost = 0;

        if(ArcEager.Type.SHIFT == action && !ArcEager.Type.SHIFT.isAppliable(state)){
            return Integer.MAX_VALUE;
        }

        if(ArcEager.Type.REDUCE == action && !ArcEager.Type.REDUCE.isAppliable(state)){
            return Integer.MAX_VALUE;
        }

        if(ArcEager.Type.isLeftAction((ArcEager.Type)action) && !ArcEager.Type.isLeftAppliable(state)){
            return Integer.MAX_VALUE;
        }

        if(ArcEager.Type.isRightAction((ArcEager.Type)action) && !ArcEager.Type.isRightAppliable(state)){
            return Integer.MAX_VALUE;
        }

        if (ArcEager.Type.SHIFT == action) {

            for (Arc dep : gold){
                if(dep.getDependent().equals(first)){
                    if(state.getStack().contains(dep.getHead())){
                        cost++;
                    }
                }
                else if(dep.getHead().equals(first)){
                    if(state.getStack().contains(dep.getDependent())){
                        cost++;
                    }
                }
            }
        }


        if (ArcEager.Type.REDUCE == action) {
            for(Arc dep: gold){
                if(dep.getHead().equals(top)){
                    if(state.getBuffer().equals(dep.getDependent())){
                        cost++;
                    }
                }
            }
        }

        if (ArcEager.Type.isLeftAction((ArcEager.Type)action)) {
            for (Arc dep : gold) {
                if(dep.getDependent().equals(top)){
                    if(!dep.getHead().equals(first) && state.getBuffer().contains(dep.getHead())){
                        cost++;
                    }
                }
                if(dep.getHead().equals(top)){
                    if(state.getBuffer().contains(dep.getDependent())){
                        cost++;
                    }
                }
            }
        }

        if (ArcEager.Type.isRightAction((ArcEager.Type)action)) {
            for (Arc dep : gold) {
                if(dep.getDependent().equals(first)){
                    if(!dep.getHead().equals(top) &&
                            (state.getStack().contains(dep.getHead()) ||
                                    state.getBuffer().contains(dep.getHead()))){
                        cost++;
                    }
                }
                if(dep.getHead().equals(first)){
                    if(state.getStack().contains(dep.getDependent())){
                        cost++;
                    }
                }
            }

        }

        return cost;
    }


    /**
     *  Check if an action is a zero-cost action if it's applied on a state
     * @param action
     * @param state
     * @return
     */

    public boolean isZeroCost(ArcEager.Type action, State state) {

        ArcEager.Type [] zeros = getZeroCostAction(state);
        for(ArcEager.Type zero : zeros){
            if(action==zero){
                return true;
            }
        }
        return false;
    }


    @Override
    public ArcSystem.transition[] getReachableGoldTreeActions(State state){
        ArrayList<ArcEager.Type> goldReachable = new ArrayList<ArcEager.Type>();
        for(ArcEager.Type action : ArcEager.Type.values()){
            if(this.getCostAction(action,state)==0){
                goldReachable.add(action);
            }
        }

        if(goldReachable.isEmpty()) return null;
        ArcEager.Type [] actions = goldReachable.toArray(new ArcEager.Type[goldReachable.size()]);
        return actions;
    }


    @Override
    public synchronized Transition [] findGoldSeqs(Sentence s, GoldTree goldTree) {
        LinkedHashMap<Integer, Transition> history = new LinkedHashMap<Integer,Transition>();
        State state = new State(s);
        int step = 0;
        while (!state.isTerminal()) {
            ArcEager.Type oracle = getAction(state);
            state = oracle.apply(state);
            Transition tr = new Transition(state,oracle);
            history.put(step,tr);
            step++;
        }
        Transition[] goldActions = history.values().toArray(new Transition[history.size()]);
        return goldActions;
    }


    @Override
    public synchronized int [] getAllCostAction(State state) {

        int [] costs = new int [ArcEager.Type.values().length-1];
        for(ArcEager.Type action : ArcEager.Type.values()){
            if(action.getType()==-1) continue;
            costs[action.getType()]=getCostAction(action,state);
        }
        return costs;
    }


    @Override
    public synchronized ArcEager.Type getAction(State state) {

        Token topStack = state.getTopStack();
        Token firstBuffer = state.getFirstBuffer();
        Token[] tokens = state.getInput().tokens;


        if (ArcEager.Type.isLeftAppliable(state) && topStack.getHead() == firstBuffer.getIndex() ) {
            if(topStack.getValue(ConllReader.Conll.DEPREL.getName()).equals("nsubj")){
                return ArcEager.Type.LEFT_NSUBJ;
            }
            if(topStack.getValue(ConllReader.Conll.DEPREL.getName()).equals("dobj")){
                return ArcEager.Type.LEFT_DOBJ;
            }
            if(topStack.getValue(ConllReader.Conll.DEPREL.getName()).equals("noname")){
                return ArcEager.Type.LEFT_OTHER;
            }

        }

        else if (ArcEager.Type.isRightAppliable(state) && firstBuffer.getHead() == topStack.getIndex()
                ) {
            if(firstBuffer.getValue(ConllReader.Conll.DEPREL.getName()).equals("nsubj")){
                return ArcEager.Type.RIGHT_NSUBJ;
            }
            if(firstBuffer.getValue(ConllReader.Conll.DEPREL.getName()).equals("dobj")){
                return ArcEager.Type.RIGHT_DOBJ;
            }
            if(firstBuffer.getValue(ConllReader.Conll.DEPREL.getName()).equals("noname")){
                return ArcEager.Type.RIGHT_OTHER;
            }
        }

        else {
            for (int i = 0; i < topStack.getIndex(); i++) {
                if ((tokens[i].getHead() == firstBuffer.getIndex() ||
                        firstBuffer.getHead() == tokens[i].getIndex()) && ArcEager.Type.REDUCE.isAppliable(state))
                {
                    return ArcEager.Type.REDUCE;
                }
            }
        }

        return ArcEager.Type.SHIFT;
    }



    public String getCostsString(int [] costs){

        StringBuilder builder = new StringBuilder();
        ArcEager.Type []  actions = ArcEager.Type.values();
        for(int i =0; i<costs.length; i++){
            builder.append("action : " + actions[i+1].getName() + " " + actions[i+1].getRelation());
            builder.append(" - " + costs[i] + "\n");
        }

        return builder.toString();
    }
}
