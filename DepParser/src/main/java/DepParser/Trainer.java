package DepParser;

import java.util.*;

/**
 * Created by Marco Corona on 09/08/2017.
 */
public class Trainer {

    private Map<Integer, GoldTree> goldTrees;
    private Map<Integer, Sentence> sentences;


    public Trainer() {
        goldTrees = new HashMap<Integer, GoldTree>();
        sentences = new HashMap<Integer, Sentence>();
    }


    public void addGoldTree(int sentenceId, GoldTree gold, Sentence s) {
        goldTrees.put(sentenceId, gold);
        sentences.put(sentenceId, s);
    }


    public Map<Integer, GoldTree> getGoldTrees() {
        return goldTrees;
    }

    public Map<Integer, Sentence> getSentences() {
        return sentences;
    }


    public Oracle train() {

        Oracle trained = new Oracle();

        for (int i = 0; i < sentences.size(); i++) {

            GoldTree gt = goldTrees.get(i);
            TreeMap<State, Action.Type> history = buildGoldSeqs(sentences.get(i), gt);
            Action.Type[] goldActions = history.values().toArray(new Action.Type[history.size()]);

            // update gold tree with gold sequence

            gt.setGoldSeqs(history);
            goldTrees.put(i, gt);
            System.out.println("gold seqs n : " + i + " done ...");
        }
        return trained;
    }


    private TreeMap<State, Action.Type> buildGoldSeqs(Sentence s, GoldTree goldTree) {

        TreeMap<State, Action.Type> history = new TreeMap<State, Action.Type>(new Comparator<State>() {
            public int compare(State o1, State o2) {
                return o1.compareTo(o2);
            }
        });
        goldTree.printDeps();
        State state = new State(s);
        history.put(state, Action.Type.NOP);
        int step = 0;
        while (!state.isTerminal()) {
            Action.Type type = getAction(state);
            System.out.println("State number : " + state.getNseq() + " - Actions selected : " + type.getName());
            int prevHash = state.hashCode();
            state = state.applyAction(type);
            int newHash = state.hashCode();
            history.put(state, type);
            state.printArcs();
        }

        return history;
    }


    private Action.Type getAction(State state) {

        if (state.getStack().isEmpty()) {
            return Action.Type.SHIFT;
        }
        Token topStack = state.getTopStack();
        Token firstBuffer = state.getFirstBuffer();
        Token[] tokens = state.getInput().getTokens();

        System.out.println("top stack  id : " + topStack.getIndex());
        System.out.println("first buffer  id : " + firstBuffer.getIndex());


        if (topStack.getHead() == firstBuffer.getIndex()) {
            return Action.Type.LEFT;
        }
        else if (firstBuffer.getHead() == topStack.getIndex()) {
            return Action.Type.RIGHT;
        }
        else {
            for (int i = 0; i < topStack.getIndex(); i++) {
                if (tokens[i].getHead() == firstBuffer.getIndex() ||
                        firstBuffer.getHead() == tokens[i].getIndex())
                {
                    return Action.Type.REDUCE;
                }
            }
        }

        return Action.Type.SHIFT;
    }

}