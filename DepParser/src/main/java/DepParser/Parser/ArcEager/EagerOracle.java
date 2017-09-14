package DepParser.Parser.ArcEager;

import DepParser.Model.*;
import DepParser.Parser.Oracle;
import DepParser.Parser.Sentence;
import DepParser.Utils.Logging;
import DepParser.Utils.PrintUltis;
import DepParser.Utils.UDBankReader;

import java.io.IOException;
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
    public synchronized  ArcEager.Type[] getZeroCostAction(State state) throws IOException {
        //Logging logger = new Logging();

        ArcEager.Type [] appliable = ArcEager.getValidAction(state);
        ArrayList<ArcEager.Type> zeroCost = new ArrayList<ArcEager.Type>();
        for(ArcEager.Type action : appliable){
            if(getCostAction(action,state)==0){
                zeroCost.add(action);
            }
        }
        //logger.log(builder.toString(),Logging.DEBUG);
        return zeroCost.toArray(new ArcEager.Type[zeroCost.size()]);
    }

    /**
     *  Compute the total cost of an action when it's applied on a state
     *  It depends from the type of transition system adopted
     * @param action
     * @param state
     * @return
     * @throws IOException
     */

    @Override
    public synchronized int getCostAction(ArcSystem.operation action,State state) throws IOException {

        Logging logger = new Logging();
        GoldTree goldTree = this.goldTrees.get(state.getInput().id);
        Token top = state.getTopStack();
        Token first = state.getFirstBuffer();
        Dependency[] gold = goldTree.getDependencies();
        int cost = 0;
        StringBuilder builder = new StringBuilder();
        StringBuilder costs = new StringBuilder();


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

            for (Dependency dep : gold){
                if(dep.getDependent().equals(first)){
                    if(state.getStack().contains(dep.getHead())){
                        cost++;
                        builder.append("SHIFT : cost \n" );
                        builder.append("first buffer (dep) : " + first.getIndex() + "\n");
                        builder.append("on stack (head): " + dep.getHead().getIndex() + "\n");
                    }
                }
                else if(dep.getHead().equals(first)){
                    if(state.getStack().contains(dep.getDependent())){
                        cost++;
                        builder.append("SHIFT : cost \n" );
                        builder.append("first buffer (head): " + first.getIndex() + "\n");
                        builder.append(" on stack (dep) : " + dep.getDependent().getIndex() + "\n");
                    }
                }
            }

            // builder.append("Shift action cost " + cost + "\n");
        }


        if (ArcEager.Type.REDUCE == action) {
            for(Dependency dep: gold){
                if(dep.getHead().equals(top)){
                    if(state.getBuffer().equals(dep.getDependent())){
                        cost++;
                        builder.append("REDUCE : cost \n" );
                        builder.append("top stack (head) : " + top.getIndex() + "\n");
                        builder.append("on buffer (dep) : " + dep.getDependent().getIndex() + "\n");
                    }
                }
            }

           // builder.append("Reduce action cost " + cost + "\n");
        }


        if (ArcEager.Type.isLeftAction((ArcEager.Type)action)) {
            for (Dependency dep : gold) {

                if(dep.getDependent().equals(top)){
                    if(!dep.getHead().equals(first) && state.getBuffer().contains(dep.getHead())){
                        cost++;
                        builder.append("LEFT : cost"  + "\n");
                        builder.append("top stack (dep): " + top.getIndex() + "\n");
                        builder.append("on buffer (head): " + dep.getHead().getIndex() +"\n");
                    }
                }
                if(dep.getHead().equals(top)){
                    if(state.getBuffer().contains(dep.getDependent())){
                        cost++;
                        builder.append("LEFT : cost" + "\n" );
                        builder.append("top stack (head): " + top.getIndex() + "\n");
                        builder.append("on buffer (dep) : " + dep.getDependent().getIndex() + "\n");
                    }
                }
            }

           // builder.append("Left action cost " + cost + "\n");
        }

        if (ArcEager.Type.isRightAction((ArcEager.Type)action)) {
            for (Dependency dep : gold) {
                if(dep.getDependent().equals(first)){
                    if(!dep.getHead().equals(top) &&
                            (state.getStack().contains(dep.getHead()) ||
                                    state.getBuffer().contains(dep.getHead()))){
                        cost++;
                        builder.append("RIGHT : cost" +"\n" );
                        builder.append("first buffer (dep) : " + first.getIndex() + "\n");
                        builder.append("on buffer or on stack (head) : " + dep.getHead().getIndex() + "\n");
                    }
                }
                if(dep.getHead().equals(first)){
                    if(state.getStack().contains(dep.getDependent())){
                        cost++;
                        builder.append("RIGHT : cost"+ "\n" );
                        builder.append("first buffer (head) ): " + first.getIndex() + "\n");
                        builder.append("on stack (dep) : " + dep.getDependent().getIndex() + "\n");
                    }
                }
            }
            //builder.append("Right action cost " + cost + "\n");
        }

        //logger.log(builder.toString(),Logging.DEBUG);
        //logger.log(costs.toString(),Logging.DEBUG);

        return cost;
    }


    /**
     *  Check if an action is a zero-cost action if it's applied on a state
     * @param action
     * @param state
     * @return
     * @throws IOException
     */

    public boolean isZeroCost(ArcEager.Type action, State state) throws IOException {

        ArcEager.Type [] zeros = getZeroCostAction(state);
        for(ArcEager.Type zero : zeros){
            if(action==zero){
                return true;
            }
        }
        return false;
    }


    @Override
    public ArcSystem.operation [] getReachableGoldTreeActions(State state) throws IOException {

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
    public synchronized Transition [] findGoldSeqs(Sentence s, GoldTree goldTree) throws IOException {

        LinkedHashMap<Integer, Transition> history = new LinkedHashMap<Integer,Transition>();
        State state = new State(s);
        int step = 0;
        Logging logger = new Logging();

        StringBuilder logBuilder = new StringBuilder();
        //logBuilder.append("ORACLE GOLD SEQUENCE FIND " + s.id +"\n");
        //logger.log(logBuilder.toString(), Logging.DEBUG);
        //logBuilder = new StringBuilder();

        while (!state.isTerminal()) {


            /*
                logBuilder.append("Step number :" + state.getStep() + "\n\n");
                logBuilder.append("Zero cost actions : \n\n" );
                ArcEager.Type [] zeroCost = getZeroCostAction(s.id,state);
                logBuilder.append(PrintUltis.toString(ArcSystem.getAllActionName(zeroCost)));
                int [] costs = this.getAllCostAction(state, s);
                logBuilder.append(this.getCostsString(costs) + "\n");
            */

            ArcEager.Type oracle = getAction(state);
            int cost = getCostAction(oracle,state);
            logBuilder.append("Selected action : " + oracle.getName() + "- " + oracle.getRelation() + " on step " + step +"\n\n");
            if(cost>0){
                logBuilder = appendCostInfo(logBuilder,state);
            }


            State prev = state.cloneState();
            state = oracle.apply(state);

            boolean test = ArcEager.test(oracle,prev,state);
            if(!test){
                System.out.println("Test on " + oracle.getName() + " not passed");
            }


            //logBuilder.append("arcs found : " + state.countArcsNotNull() + "\n");
            //logger.log(logBuilder.toString(), Logging.DEBUG);

            Transition tr = new Transition(state,oracle);
            history.put(step,tr);
            step++;
            
            logBuilder = new StringBuilder();
        }

        // check seqs
        state = new State(s);

        Transition[] goldActions = history.values().toArray(new Transition[history.size()]);

        //logBuilder.append("Transitions gold action : " + goldActions.length);

        for(int i=0; i<goldActions.length; i++){
            ArcEager.Type oracle = (ArcEager.Type)goldActions[i].getAction();
            state = oracle.apply(state);
        }

        if(!Dependency.sameArcs(goldTree.getDependencies(),state.getArcs())){
            logBuilder.append("[ERROR] Oracle not works! \n");
            logBuilder.append("Gold tree \n" + PrintUltis.toString(goldTree.getDependencies()));
            logBuilder.append("Oracle tree \n" + PrintUltis.toString(state.getArcs()));
            StringBuilder outputLog= new StringBuilder();
            outputLog.append(logBuilder.toString());
            logger.log(outputLog.toString(),Logging.DEBUG);
            System.out.println("[ERROR] Oracle not works! \n");
        }
        else{
            System.out.println("Oracle works perfectly!");
            logger.log(logBuilder.toString(),Logging.DEBUG);
        }

        return goldActions;
    }


    @Override
    public synchronized int [] getAllCostAction(State state) throws IOException {

        int [] costs = new int [ArcEager.Type.values().length-1];
        for(ArcEager.Type action : ArcEager.Type.values()){
            if(action.getType()==-1) continue;
            costs[action.getType()]=getCostAction(action,state);
        }
        return costs;
    }



    protected synchronized ArcEager.Type getAction(State state) {

        Token topStack = state.getTopStack();
        Token firstBuffer = state.getFirstBuffer();
        Token[] tokens = state.getInput().tokens;


        if (ArcEager.Type.isLeftAppliable(state) && topStack.getHead() == firstBuffer.getIndex() ) {
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

        else if (ArcEager.Type.isRightAppliable(state) && firstBuffer.getHead() == topStack.getIndex()
                ) {
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
