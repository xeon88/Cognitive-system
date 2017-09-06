package DepParser.Parser.Mazzei;


import DepParser.Model.*;
import DepParser.Parser.Oracle;
import DepParser.Parser.Sentence;
import DepParser.Utils.Logging;
import DepParser.Utils.PrintUltis;
import DepParser.Utils.UDBankReader;

import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * Created by Marco Corona on 30/08/2017.
 */
public class MazzeiOracle extends Oracle {

    public MazzeiOracle(){
        super();
    }

    public ArcSystem.operation[] getZeroCostAction(int sequenceId, State s) {
        return new ArcSystem.operation[0];
    }

    public int getCostAction(ArcSystem.operation action, int sequenceId, State state) {
        return 0;
    }

    public ArcSystem.operation[] getReachableGoldTreeActions(State state, int sequenceId) {
        return new ArcSystem.operation[0];
    }



    public Transition[] findGoldSeqs(Sentence s, GoldTree tree) throws IOException {
        LinkedHashMap<Integer, Transition> history = new LinkedHashMap<Integer,Transition>();
        State state = new State(s);
        int step = 0;
        Logging logger = new Logging();


        StringBuilder logBuilder = new StringBuilder();
        while (!state.isTerminal()) {

            logBuilder.append("Zero cost actions : \n" );
            ArcMazzei.Type [] zeroCost = (ArcMazzei.Type [])getZeroCostAction(s.id,state);
            logBuilder.append(PrintUltis.toString(ArcSystem.getAllActionName(zeroCost)));

            ArcEager.Type oracle = getAction(state);
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
            ArcEager.Type oracle = (ArcEager.Type)goldActions[i].getAction();
            state = oracle.apply(state);
        }

        if(!Dependency.sameArcs(tree.getDependencies(),state.getArcs())){
            logBuilder.append("Gold tree \n" + PrintUltis.toString(tree.getDependencies()));
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

    protected String getCostsString(int[] costs) {
        return null;
    }

    @Override
    protected ArcEager.Type getAction(State state) {

        if(state.getStack().isEmpty()){
            return ArcEager.Type.SHIFT;
        }

        Token topStack = state.getTopStack();
        Token firstBuffer = state.getFirstBuffer();
        Token[] tokens = state.getInput().tokens;
        Dependency[] arcs = state.getArcs();


        if (topStack.getHead() == firstBuffer.getIndex() && ArcMazzei.Type.isLeftAppliable(state)) {
            /*
            if(topStack.getValue(UDBankReader.UDIndex.DEPREL.getName()).equals("root")){
                return Action.Type.LEFT_ROOT;
            }
            */
            if(topStack.getValue(UDBankReader.UDIndex.DEPREL.getName()).equals("nsubj")){
                return ArcEager.Type.LEFT_NSUBJ;
            }
            if(topStack.getValue(UDBankReader.UDIndex.DEPREL.getName()).equals("dobj")){
                return ArcEager.Type.LEFT_DOBJ;
            }
            if(topStack.getValue(UDBankReader.UDIndex.DEPREL.getName()).equals("noname")){
                return ArcEager.Type.LEFT_OTHER;
            }

        }

        else if (firstBuffer.getHead() == topStack.getIndex()
                && state.isHead(firstBuffer.getIndex())
                && ArcMazzei.Type.isRightAppliable(state)) {
            /*
            if(firstBuffer.getValue(UDBankReader.UDIndex.DEPREL.getName()).equals("root")){
                return Action.Type.RIGHT_ROOT;
            }
            */
            if(firstBuffer.getValue(UDBankReader.UDIndex.DEPREL.getName()).equals("nsubj")){
                return ArcEager.Type.RIGHT_NSUBJ;
            }
            if(firstBuffer.getValue(UDBankReader.UDIndex.DEPREL.getName()).equals("dobj")){
                return ArcEager.Type.RIGHT_DOBJ;
            }
            if(firstBuffer.getValue(UDBankReader.UDIndex.DEPREL.getName()).equals("noname")){
                return ArcEager.Type.RIGHT_OTHER;
            }
        }

        /*
        else {
            for (int i = 0; i < topStack.getIndex(); i++) {
                if ((tokens[i].getHead() == firstBuffer.getIndex() ||
                        firstBuffer.getHead() == tokens[i].getIndex()) && state.isReduceAppliable())
                {
                    return ArcEager.Type.REDUCE;
                }
            }
        }
        */
        return ArcEager.Type.SHIFT;
    }



    private int [] getAllCostAction(State state, Sentence sentence){
        int [] costs = new int [ArcEager.Type.values().length-1];
        for(ArcMazzei.Type action : ArcMazzei.Type.values()){
            if(action.getType()==-1) continue;
            costs[action.getType()]=getCostAction(action, sentence.id,state);
        }
        return costs;
    }


    // && state.isHead(firstBuffer.getIndex())

}
