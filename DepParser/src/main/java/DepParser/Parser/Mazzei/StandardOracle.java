package DepParser.Parser.Mazzei;


import DepParser.Model.*;
import DepParser.Parser.Oracle;
import DepParser.Parser.Sentence;
import DepParser.Utils.Logging;
import DepParser.Utils.PrintUltis;
import DepParser.Utils.UDBankReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Created by Marco Corona on 30/08/2017.
 */
public class StandardOracle extends Oracle {

    public StandardOracle(){
        super();
    }

    public ArcStandard.Type[] getZeroCostAction(int sequenceId, State s) {
        ArcStandard.Type [] appliable = (ArcStandard.Type[]) ArcStandard.getValidAction(s);
        ArrayList<ArcStandard.Type> zeroCost = new ArrayList<ArcStandard.Type>();
        for(ArcStandard.Type action : appliable){
            if(getCostAction(action,sequenceId,s)==0){
                zeroCost.add(action);
            }
        }
        //logger.log(builder.toString(),Logging.DEBUG);
        return zeroCost.toArray(new ArcStandard.Type[zeroCost.size()]);
    }

    public int getCostAction(ArcSystem.operation action, int sequenceId, State state) {
        return 0;
    }



    public ArcStandard.Type[] getReachableGoldTreeActions(State state, int sequenceId) {
        ArrayList<ArcStandard.Type> goldReachable = new ArrayList<ArcStandard.Type>();
        for(ArcStandard.Type action : ArcStandard.Type.values()){
            if(this.getCostAction(action,sequenceId,state)==0){
                goldReachable.add(action);
            }
        }

        if(goldReachable.isEmpty()) return null;
        ArcStandard.Type [] actions = goldReachable.toArray(new ArcStandard.Type[goldReachable.size()]);
        return actions;
    }


    public Transition[] findGoldSeqs(Sentence s, GoldTree tree) throws IOException {
        LinkedHashMap<Integer, Transition> history = new LinkedHashMap<Integer,Transition>();
        State state = new State(s);
        int step = 0;
        Logging logger = new Logging();


        StringBuilder logBuilder = new StringBuilder();
        while (!state.isTerminal()) {

            logBuilder.append("Zero cost actions : \n" );
            ArcStandard.Type [] zeroCost = (ArcStandard.Type [])getZeroCostAction(s.id,state);
            logBuilder.append(PrintUltis.toString(ArcSystem.getAllActionName(zeroCost)));
            ArcStandard.Type oracle = (ArcStandard.Type) getAction(state);
            int cost = getCostAction(oracle,s.id,state);
            logBuilder.append("Selected action : " + oracle.getName() + "- " + oracle.getRelation() + " on step " + step +"\n\n");
            if(cost>0){
                logBuilder.append("topstack : " + state.getTopStack().getIndex() + "\n");
                logBuilder.append("firstbuffer : " + state.getFirstBuffer().getIndex() + "\n");
                logBuilder.append("arcs : " + PrintUltis.toString(state.getArcs(),state.getFirstBuffer().getIndex()) + "\n\n");
                logBuilder.append("STACK : \n" + PrintUltis.toString(state.getStack()) + "\n\n");
                logBuilder.append("BUFFER : \n" + PrintUltis.toString(state.getBuffer()) + "\n\n");
                logBuilder.append("Costs actions \n:" + getCostsString(this.getAllCostAction(state,s)));
            }

            state = oracle.apply(state);
            Transition tr = new Transition(state,oracle);
            step++;
            history.put(step,tr);
        }

        // check seqs
        state = new State(s);

        Transition[] goldActions = history.values().toArray(new Transition[history.size()]);
        for(int i=0; i<goldActions.length; i++){
            ArcStandard.Type oracle = (ArcStandard.Type)goldActions[i].getAction();
            state = oracle.apply(state);
        }

        if(!Dependency.sameArcs(tree.getDependencies(),state.getArcs())){
            logBuilder.append("Gold tree \n" + PrintUltis.toString(tree.getDependencies()));
            logBuilder.append("Oracle tree \n" + PrintUltis.toString(state.getArcs()));
            logBuilder.append("[ERROR] Oracle not works! \n");
            StringBuilder outputLog= new StringBuilder();
            outputLog.append(logBuilder.toString());
            logger.log(outputLog.toString(),Logging.DEBUG);
            System.out.println("[ERROR] Oracle not works! \n");
        }
        else{
            System.out.println("Oracle works perfectly!");
        }

        return goldActions;
    }

    protected String getCostsString(int[] costs) {
        return null;
    }

    @Override
    protected ArcStandard.Type getAction(State state) {

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
                && checkSubTree(firstBuffer.getIndex(),state)){

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



    private int [] getAllCostAction(State state, Sentence sentence){
        int [] costs = new int [ArcStandard.Type.values().length-1];
        for(ArcStandard.Type action : ArcStandard.Type.values()){
            if(action.getType()==-1) continue;
            costs[action.getType()]=getCostAction(action, sentence.id,state);
        }
        return costs;
    }


    public boolean checkSubTree(int head, State state){


        LinkedList<Integer> dependent = new LinkedList<Integer>();
        dependent.addFirst(head);
        boolean [] checked = new boolean[state.getArcs().length];
        Dependency[] arcs = state.getArcs();
        Dependency [] gold = goldTrees.get(state.getInput().id).getDependencies();
        while (!dependent.isEmpty()){
            int first = dependent.removeFirst();
            for(int i=0; i<checked.length;i++){
                if(!checked[i]){
                    if(gold[i].getHead().getIndex()==first){
                        if(arcs[i]==null){
                            return false;
                        }
                        else {
                            dependent.addFirst(gold[i].getDependent().getIndex());
                            checked[i]=true;
                        }
                    }
                }
            }
        }

        return true;
    }

}
