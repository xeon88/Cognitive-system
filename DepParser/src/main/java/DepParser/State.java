package DepParser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Created by Marco Corona on 11/08/2017.
 */
public class State {

    private Stack<Token> stack;
    private LinkedList<Token> buffer;
    private static final String NSUBJ = "nsubj";
    private static final String DOBJ = "dobj";
    private ArrayList<Dependency> arcs;
    private static State state;
    private Token topStack;
    private Token firstBuffer;
    private Sentence input;
    private int nseq;


    public State (Sentence s){
        this.buffer = new LinkedList<Token>();
        this.stack = new Stack<Token>();
        this.arcs = new ArrayList<Dependency>();
        this.topStack = null;
        this.firstBuffer = null;
        this.input = s;
        this.nseq = 0;
        initStructures(s.getTokens());
    }


    public void initStructures(Token [] tokens){
        stack.push(Token.makeRoot());
        for(int i = 0; i<tokens.length ; i++){
            buffer.add(tokens[i]);
        }
        updateOutliers();
    }


    private void updateOutliers(){
        topStack = stack.peek();
        firstBuffer = buffer.getFirst();
    }



    public Stack<Token> getStack() {
        return stack;
    }

    public LinkedList<Token> getBuffer() {
        return buffer;
    }

    public ArrayList<Dependency> getArcs() {
        return arcs;
    }

    public Sentence getInput() {
        return input;
    }

    public Token getTopStack() {
        return topStack;
    }

    public Token getFirstBuffer() {
        return firstBuffer;
    }


    public void shift(){
        Token vertex = this.buffer.poll();
        this.stack.push(vertex);
        updateOutliers();
        nseq++;
    }


    private void left(String type){
        Token head = this.buffer.poll();
        Token dependent = this.stack.pop();
        Dependency dependency = new Dependency(head,dependent,type);
        arcs.add(dependency);
        updateOutliers();
        nseq++;
    }


    private void right(String type){
        Token head = this.stack.pop();
        Token dependent = this.buffer.poll();
        this.buffer.addFirst(head);
        Dependency dependency = new Dependency(head,dependent,type);
        arcs.add(dependency);
        updateOutliers();
        nseq++;
    }

    private void reduce(){
        this.stack.pop();
        updateOutliers();
        nseq++;
    }


    private String chooseDependencyType(){
        return NSUBJ;
    }


    public void right(){
        String type = chooseDependencyType();
        right(type);
    }

    public void left(){
        String type = chooseDependencyType();
        left(type);
    }




    public State applyAction(Action.Type type){

            if(Action.Type.SHIFT==type){
                shift();
                return this;
            }
            else if(Action.Type.REDUCE==type){
                reduce();
                return this;
            }
            else if(Action.Type.LEFT==type){
                left();
                return this;
            }
            else{
                right();
                return this;
            }
    }


    public boolean isTerminal(){
        if(this.stack.empty() && this.buffer.isEmpty()){
            return true;
        }
        return false;
    }


}
