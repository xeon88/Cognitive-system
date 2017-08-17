package DepParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marco Corona on 09/08/2017.
 */
public class Trainer {

    private Map<Integer,GoldTree> goldTrees;
    private Map<Integer,Sentence> sentences;


    public Trainer(){
        goldTrees = new HashMap<Integer, GoldTree>();
        sentences = new HashMap<Integer, Sentence>();
    }


    public void addGoldTree(int sentenceId, GoldTree gold , Sentence s){
        goldTrees.put(sentenceId,gold);
        sentences.put(sentenceId,s);
    }


    public Map<Integer, GoldTree> getGoldTrees() {
        return goldTrees;
    }

    public Map<Integer, Sentence> getSentences() {
        return sentences;
    }


    public Oracle train(){

        Oracle trained = new Oracle();

        for(int i = 0; i<sentences.size(); i++){

            GoldTree gt = goldTrees.get(i);
            HashMap<State,Action.Type> history = buildGoldSeqs(sentences.get(i),gt);
            Action.Type [] goldActions = history.values().toArray(new Action.Type[history.size()]);

            // update gold tree with gold sequence

            gt.setGoldSeqs(goldActions);
            goldTrees.put(i,gt);
        }
        return trained;
    }





    private HashMap<State,Action.Type> buildGoldSeqs(Sentence s, ProjectiveTree goldTree){

        HashMap<State,Action.Type> history = new HashMap<State, Action.Type>();
        State state = new State(s);
        history.put(state, Action.Type.NOP);
        while (!state.isTerminal()){
            Action.Type type = getAction(state);
            state = state.applyAction(type);
            history.put(state,type);
        }
        return history;
     }




    private Action.Type getAction(State state){
            Token topStack = state.getTopStack();
            Token firstBuffer = state.getFirstBuffer();
            Token [] tokens = state.getInput().getTokens();
            if (topStack.getHead() == firstBuffer.getIndex()) {
                return Action.Type.LEFT;
            } else if (firstBuffer.getHead() == topStack.getIndex()) {
                return Action.Type.RIGHT;
            } else {
                for (int i = 0; i < state.getStack().peek().getIndex(); i++) {
                    if (tokens[i].getHead() == firstBuffer.getIndex() ||
                            firstBuffer.getHead() == tokens[i].getIndex()) {
                        return Action.Type.REDUCE;
                    }
                }
            }
            return Action.Type.SHIFT;
    }
}
