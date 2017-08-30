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
public class Oracle {

    private HashMap<Integer,GoldTree> goldTrees;

    public Oracle(){
        goldTrees = new HashMap<Integer, GoldTree>();
    }


    public HashMap<Integer, GoldTree> getGoldTrees() {
        return goldTrees;
    }


    public Action.Type getOracleAction(int sentence, int step){

        Transition[] seqs = goldTrees.get(sentence).getGoldSeqs();
        return seqs[step].getAction();
    }


    public Action.Type [] getZeroCostAction(int sequenceId, State s){
        Action.Type [] appliable = s.getValidAction();
        ArrayList<Action.Type> zeroCost = new ArrayList<Action.Type>();
        for(Action.Type action : appliable){
            if(getCostAction(action,sequenceId,s)==0){
                zeroCost.add(action);
            }
        }

        return zeroCost.toArray(new Action.Type[zeroCost.size()]);
    }


    /**
     * Compute the loss cost of an action when it was applied in State state
     * @return cost value
    */
    public int getCostAction(Action.Type action, int sequenceId , State state) {

        GoldTree goldTree = this.goldTrees.get(sequenceId);
        Token top = state.getTopStack();
        Token first = state.getFirstBuffer();
        Dependency[] gold = goldTree.getDependencies();
        int cost = 0;
        StringBuilder builder = new StringBuilder();
        StringBuilder costs = new StringBuilder();


        if(Action.Type.SHIFT == action && !state.isShiftAppliable()){
            return Integer.MAX_VALUE;
        }

        /*
        if(Action.Type.REDUCE == action && !state.isReduceAppliable()){
            return Integer.MAX_VALUE;
        }

       */

        if(isLeftAction(action) && !state.isLeftAppliable()){
            return Integer.MAX_VALUE;
        }

        if(isRightAction(action) && !state.isRightAppliable()){
            return Integer.MAX_VALUE;
        }

        if (Action.Type.SHIFT == action) {

            for (Dependency dep : gold){
                if(dep.getDependent().equals(first)){
                    if(state.getStack().contains(dep.getHead())){
                        cost++;
                        //builder.append("SHIFT : cost" );
                        //builder.append("first : " + first.getIndex());
                        //builder.append("head : " + dep.getHead().getIndex());
                    }
                }
                else if(dep.getHead().equals(first)){
                    if(state.getStack().contains(dep.getDependent())){
                        cost++;
                        //builder.append("SHIFT : cost" );
                        //builder.append("first : " + first.getIndex());
                        //builder.append("dep : " + dep.getDependent().getIndex());
                    }
                }
            }

            costs.append("Shift action cost " + cost);
        }

        /*
        if (Action.Type.REDUCE == action) {
            for(Dependency dep: gold){
                if(dep.getHead().equals(top)){
                    if(state.getBuffer().equals(dep.getDependent())){
                        cost++;
                        builder.append("REDUCE : cost" );
                        builder.append("top : " + top.getIndex());
                        builder.append("dep : " + dep.getDependent().getIndex());
                    }
                }
            }

            costs.append("Reduce action cost " + cost);
        }

        */
        if (isLeftAction(action)) {
            for (Dependency dep : gold) {

                if(dep.getDependent().equals(top)){
                    if(!dep.getHead().equals(first) && state.getBuffer().contains(dep.getHead())){
                        cost++;
                        //builder.append("LEFT : cost" );
                        //builder.append("top : " + top.getIndex());
                        //builder.append("head : " + dep.getHead().getIndex());
                    }
                }
                if(dep.getHead().equals(top)){
                    if(state.getBuffer().contains(dep.getDependent())){
                        cost++;
                        //builder.append("LEFT : cost" );
                        //builder.append("top : " + top.getIndex());
                        //builder.append("dep : " + dep.getDependent().getIndex());
                    }
                }
            }

            costs.append("Left action cost " + cost);
        }

        if (isRightAction(action)) {
            for (Dependency dep : gold) {
                if(dep.getDependent().equals(first)){
                    if(!dep.getHead().equals(top) &&
                            (state.getStack().contains(dep.getHead()) ||
                             state.getBuffer().contains(dep.getHead()))){
                        cost++;
                        //builder.append("RIGHT : cost" );
                        //builder.append("first : " + first.getIndex());
                        //builder.append("head : " + dep.getHead().getIndex());
                    }
                }
                if(dep.getHead().equals(first)){
                    if(state.getStack().contains(dep.getDependent())){
                    cost++;
                        //builder.append("RIGHT : cost" );
                        //builder.append("first : " + first.getIndex());
                        //builder.append("dep : " + dep.getDependent().getIndex());
                    }
                }
            }
            costs.append("Right action cost " + cost);
        }

        //System.out.println(costs.toString());
        return cost;
    }



    /**
     *
     * @return
    */
    public Action.Type [] getReachableGoldTreeActions(State state, int sequenceId){

        ArrayList<Action.Type> goldReachable = new ArrayList<Action.Type>();
        for(Action.Type action : Action.Type.values()){
            if(this.getCostAction(action,sequenceId,state)==0){
                goldReachable.add(action);
            }
        }

        if(goldReachable.isEmpty()) return null;
        Action.Type [] actions = goldReachable.toArray(new Action.Type[goldReachable.size()]);
        return actions;
    }



    public void addGoldTree(Sentence s, GoldTree gold) throws IOException {
        goldTrees.put(s.id,gold);
        boolean projective = gold.isProjectiveTree();
        System.out.println("Gold tree number" + s.id  +" is : " + (projective ? "projective \n": "not projective \n"));
        Transition [] goldseqs = findGoldSeqs(s,gold);
        gold.setGoldSeqs(goldseqs);
        goldTrees.put(s.id,gold);
    }




    public Transition [] findGoldSeqs(Sentence s, GoldTree goldTree) throws IOException {

        LinkedHashMap<Integer, Transition> history = new LinkedHashMap<Integer,Transition>();
        //goldTree.printDeps();
        State state = new State(s);
        int step = 0;
        Logging logger = new Logging();

        StringBuilder logBuilder = new StringBuilder();
        while (!state.isTerminal()) {

            logBuilder.append("Zero cost actions : \n" );
            Action.Type [] zeroCost = getZeroCostAction(s.id,state);
            logBuilder.append(PrintUltis.toString(Action.getAllActionName(zeroCost)));

            Action.Type type = getAction(state);
            int cost = getCostAction(type,s.id,state);
            logBuilder.append("Selected action : " + type.getName() + "- " + type.getRelation() + " on step " + step +"\n\n");
            if(cost>0){
                logBuilder.append("topstack : " + state.getTopStack().getIndex() + "\n");
                logBuilder.append("firstbuffer : " + state.getFirstBuffer().getIndex() + "\n");
                logBuilder.append("arcs : " + PrintUltis.toString(state.getArcs(),state.getFirstBuffer().getIndex()) + "\n\n");
                logBuilder.append("STACK : \n" + PrintUltis.toString(state.getStack()) + "\n\n");
                logBuilder.append("BUFFER : \n" + PrintUltis.toString(state.getBuffer()) + "\n\n");
                logBuilder.append("Costs actions \n:" + getCostsString(this.getAllCostAction(state,s)));
            }

            state = state.applyAction(type);
            Transition tr = new Transition(state,type);
            step++;
            history.put(step,tr);
        }

        // check seqs
        state = new State(s);

        Transition[] goldActions = history.values().toArray(new Transition[history.size()]);
        for(int i=0; i<goldActions.length; i++){
            Action.Type oracle = goldActions[i].getAction();
            state = state.applyAction(oracle);
        }

        if(!Dependency.sameArcs(goldTree.getDependencies(),state.getArcs())){
            logBuilder.append("Gold tree \n" + PrintUltis.toString(goldTree.getDependencies()));
            logBuilder.append("Oracle tree \n" + PrintUltis.toString(state.getArcs()));
            logBuilder.append("[ERROR] Oracle not works! \n");
            StringBuilder outputLog= new StringBuilder();
            outputLog.append(logBuilder.toString());
            logger.log(outputLog.toString(),Logging.DEBUG);
        }
        else{
            System.out.println("Oracle works perfectly!");
        }

        //state.printArcs();
        return goldActions;
    }


    private int [] getAllCostAction(State state, Sentence sentence){

        int [] costs = new int [Action.Type.values().length-1];
        for(Action.Type action : Action.Type.values()){
            if(action.getType()==-1) continue;
            costs[action.getType()]=getCostAction(action, sentence.id,state);
        }

        return costs;
    }



    private Action.Type getAction(State state) {

        if(state.getStack().isEmpty()){
            return Action.Type.SHIFT;
        }

        Token topStack = state.getTopStack();
        Token firstBuffer = state.getFirstBuffer();
        Token[] tokens = state.getInput().tokens;
        Dependency[] arcs = state.getArcs();


        if (topStack.getHead() == firstBuffer.getIndex() && state.isLeftAppliable()) {
            /*
            if(topStack.getValue(UDBankReader.UDIndex.DEPREL.getName()).equals("root")){
                return Action.Type.LEFT_ROOT;
            }
            */
            if(topStack.getValue(UDBankReader.UDIndex.DEPREL.getName()).equals("nsubj")){
                return Action.Type.LEFT_NSUBJ;
            }
            if(topStack.getValue(UDBankReader.UDIndex.DEPREL.getName()).equals("dobj")){
                return Action.Type.LEFT_DOBJ;
            }
            if(topStack.getValue(UDBankReader.UDIndex.DEPREL.getName()).equals("noname")){
                return Action.Type.LEFT_OTHER;
            }

        }

        else if (firstBuffer.getHead() == topStack.getIndex()
                && state.isHead(firstBuffer.getIndex())
                && state.isRightAppliable()) {
            /*
            if(firstBuffer.getValue(UDBankReader.UDIndex.DEPREL.getName()).equals("root")){
                return Action.Type.RIGHT_ROOT;
            }
            */
            if(firstBuffer.getValue(UDBankReader.UDIndex.DEPREL.getName()).equals("nsubj")){
                return Action.Type.RIGHT_NSUBJ;
            }
            if(firstBuffer.getValue(UDBankReader.UDIndex.DEPREL.getName()).equals("dobj")){
                return Action.Type.RIGHT_DOBJ;
            }
            if(firstBuffer.getValue(UDBankReader.UDIndex.DEPREL.getName()).equals("noname")){
                return Action.Type.RIGHT_OTHER;
            }
        }
        /*
        else {
            for (int i = 0; i < topStack.getIndex(); i++) {
                if ((tokens[i].getHead() == firstBuffer.getIndex() ||
                        firstBuffer.getHead() == tokens[i].getIndex()) && state.isReduceAppliable())
                {
                    return Action.Type.REDUCE;
                }
            }
        }
        */
        return Action.Type.SHIFT;
    }



    private boolean isLeftAction(Action.Type action){
        if(action.getType()>=1 && action.getType()<=3){
            return true;
        }
        return false;
    }


    private boolean isRightAction(Action.Type action){
        if(action.getType()>=4 && action.getType()<=6){
            return true;
        }
        return false;
    }


    private String getCostsString(int [] costs){

        StringBuilder builder = new StringBuilder();
        Action.Type []  actions =Action.Type.values();
        for(int i =0; i<costs.length; i++){
            builder.append("action : " + actions[i+1].getName() + " " + actions[i+1].getRelation());
            builder.append(" - " + costs[i] + "\n");
        }

        return builder.toString();
    }


}
