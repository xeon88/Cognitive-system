package DepParser;

import java.util.ArrayList;

/**
 * Created by Marco Corona on 09/08/2017.
 */
public class Oracle {

    private State state;


    public Oracle(){

    }

    public Oracle(State state){
        this.state  = state;
    }

    /**
     * Check if an action is appliable in state s according to
     * gold tree
     * @param type
     * @return

    private boolean isGoldApplicable(int type){

        ArrayList<Dependency> deps = state.getArcs();
        Token top = state.getTopStack();
        Token first = state.getFirstBuffer();
        Dependency [] gold = this.gold.getDependencies();

        if(Action.Type.SHIFT.getType()==type){
            return !state.getBuffer().isEmpty();
        }

        if(Action.Type.LEFT.getType()==type){
            for(Dependency dep : gold){

                if(!deps.contains(dep)){

                    if(dep.getDependent().equals(top)){
                        return false;
                    }
                    if(dep.getHead().equals(top) && state.getBuffer().contains(dep.getDependent())){
                        return false;
                    }
                }
            }
        }

        if(Action.Type.RIGHT.getType()==type){
            for(Dependency dep : gold){
                if(!deps.contains(dep)){
                    if(dep.getDependent().equals(first)){
                        // if exisist a head on the stack
                        if(state.getStack().contains(dep.getHead())
                                || state.getStack().contains(dep.getDependent())){
                            return false;
                        }
                        // if exsist a head on the buffer
                        if(state.getBuffer().contains(dep.getHead())){
                            return false;
                        }
                    }
                }
            }
        }

        if(Action.Type.REDUCE.getType()==type){
            return !state.getStack().empty();
        }

        return false;
    }


    /**
     *
     * @return

    public int[] getActions(){

        ArrayList<Integer> appliable = new ArrayList<Integer>();
        for(Action.Type typeEnum : Action.Type.values()){
            if(isGoldApplicable(typeEnum.getType())){
                appliable.add(typeEnum.getType());
            }
        }

        if(appliable.isEmpty()) return null;
        Integer [] actions = appliable.toArray(new Integer[appliable.size()]);
        int [] codes = new int[actions.length];
        for(int i = 0; i<codes.length;i++){
            codes[i]=actions[i].intValue();
        }

        return codes;
    }
     */

    public ArrayList<Integer> buildGoldTree(){
        ArrayList<Integer> moves = new ArrayList<Integer>();


        return moves;
    }

}
