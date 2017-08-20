package DepParser;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Created by Marco Corona on 11/08/2017.
 */
public class State implements Comparable<State>{

    private Stack<Token> stack;
    private LinkedList<Token> buffer;
    private static final String NSUBJ = "nsubj";
    private static final String DOBJ = "dobj";
    private ArrayList<Dependency> arcs;
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
        for(int i = 1; i<tokens.length ; i++){
            buffer.add(tokens[i]);
        }
        updateOutliers();
    }


    private void updateOutliers(){

        topStack = !stack.isEmpty() ? stack.peek() : null;
        firstBuffer = !buffer.isEmpty()? buffer.getFirst() : null;
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

    public int getNseq(){return nseq;}

    public void shift(){
        Token vertex = this.buffer.getFirst();
        this.buffer.removeFirst();
        this.stack.push(vertex);
        updateOutliers();
        nseq++;
    }


    private void left(String type){
        Token head = this.buffer.getFirst();
        Token dependent = this.stack.pop();
        Dependency dependency = new Dependency(head,dependent,type);
        arcs.add(dependency);
        updateOutliers();
        nseq++;
    }


    private void right(String type){
        Token head = this.stack.peek();
        Token dependent = this.buffer.getFirst();
        this.buffer.removeFirst();
        this.stack.push(dependent);
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
            else if(Action.Type.NOP==type){
                return this;
            }
            else{
                right();
                return this;
            }
    }


    public boolean isTerminal(){
        System.out.println("\n\n Stack dim : " + stack.size());
        System.out.println("Buffer dim : " + buffer.size());
        if(this.buffer.isEmpty()){
            return true;
        }
        return false;
    }


    public void printArcs(){

        for(Dependency dep : arcs){
            System.out.println(dep.toString());
        }
    }


    @Override
    public int hashCode(){

        HashCodeBuilder builder = new HashCodeBuilder(31,17);
        builder.append(nseq)
            .append(stack)
            .append(buffer)
            .build();

        return builder.toHashCode();

    }

    public int compareTo(State t){
        return this.nseq-t.nseq;
    }
}
