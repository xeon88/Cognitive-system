package DepParser.Model;

import DepParser.Parser.Sentence;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Created by Marco Corona on 11/08/2017.
 */
public class State implements Comparable<State> {

    private Stack<Token> stack;
    private LinkedList<Token> buffer;
    private Token topStack;
    private Dependency[] arcs;
    private Token firstBuffer;
    private Sentence input;
    private int step;
    private boolean rooted;
    private int[] leftmost;
    private int[] rightmost;

    private static final String NSUBJ = "nsubj";
    private static final String DOBJ = "dobj";
    private static final String ROOT = "root";
    private static final String OTHER = "other";


    public State(Sentence s) {
        this.buffer = new LinkedList<Token>();
        this.rooted = false;
        this.stack = new Stack<Token>();
        this.topStack = null;
        this.firstBuffer = null;
        this.input = s;
        this.arcs = new Dependency[s.tokens.length - 1]; // indexed by dependent token id
        this.leftmost = new int [s.tokens.length-1];
        this.rightmost = new int [s.tokens.length-1];
        this.step = 0;
        Arrays.fill(leftmost,Integer.MAX_VALUE);
        Arrays.fill(rightmost,-1);
        initStructures(s.tokens);
    }

    public State(Stack<Token> stack, LinkedList<Token> buffer, Dependency[] arcs,
                 Token topStack, Token firstBuffer, Sentence s,
                 int nseq, boolean rooted, int [] leftmost, int [] rightmost
                ) {

        this.buffer = buffer;
        this.stack = stack;
        this.arcs = arcs;
        this.topStack = topStack;
        this.firstBuffer = firstBuffer;
        this.input = s;
        this.step = step;
        this.rooted = rooted;
        this.leftmost = leftmost;
        this.rightmost = rightmost;
    }


    public void initStructures(Token[] tokens) {
        stack.push(Token.makeRoot());
        for (int i = 1; i < tokens.length; i++) {
            buffer.add(tokens[i]);
        }
        topStack = !stack.isEmpty() ? stack.peek() : null;
        firstBuffer = !buffer.isEmpty() ? buffer.getFirst() : null;
    }


    public boolean isRooted() {
        return rooted;
    }

    public Stack<Token> getStack() {
        return stack;
    }

    public LinkedList<Token> getBuffer() {
        return buffer;
    }

    public Dependency[] getArcs() {
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

    public void setFirstBuffer(Token firstBuffer) {
        this.firstBuffer = firstBuffer;
    }


    public void setRooted(boolean rooted) {
        this.rooted = rooted;
    }

    public void setTopStack(Token topStack) {
        this.topStack = topStack;
    }

    public void setArc(int index, Dependency arc) {
        this.arcs[index] = arc;
    }

    public void setLeftmost(int[] leftmost) {
        this.leftmost = leftmost;
    }

    public void setRightmost(int[] rightmost) {
        this.rightmost = rightmost;
    }


    public void updateLeftMost(Dependency arc){
        if(arc.getHead().getIndex()==0) return;
        if( arc.getDependent().getIndex() < this.leftmost[arc.getHead().getIndex()-1]){
            this.leftmost[arc.getHead().getIndex()-1] = arc.getDependent().getIndex();
        }
    }

    public void updateRightMost(Dependency arc){
        if(arc.getHead().getIndex()==0) return;
        if (arc.getDependent().getIndex() > this.rightmost[arc.getHead().getIndex()-1]) {
            this.rightmost[arc.getHead().getIndex()-1] = arc.getDependent().getIndex();
        }
    }


    private boolean isLegitIndex(int index){
       boolean test = index>=0 && index<=input.tokens.length-1;
       return test;
    }

    public Token getLeftmostToken(int index) {
        if(index==0) return Token.makeFake();
        return isLegitIndex(leftmost[index-1]) ? input.tokens[leftmost[index-1]]: Token.makeFake();
    }


    public Token getRightmostToken(int index) {
        if(index==0) return Token.makeFake();
        return isLegitIndex(rightmost[index-1]) ? input.tokens[rightmost[index-1]]: Token.makeFake();
    }


    public int getStep() {
        return step;
    }

    public void incrementStep() {
        step++;
    }

    public boolean isTerminal() {
        return this.buffer.isEmpty();
    }


    public void printArcs() {

        for (Dependency dep : arcs) {
            if (dep != null) {
                System.out.println(dep.toString());
            }
        }
    }


    public Token getStackTokenOrFake(int i) {

        return i >= 0 && i < stack.size() ? stack.get(i) : Token.makeFake();
    }

    public Token getBufferTokenOrFake(int i) {
        return buffer.size() > i ? buffer.get(i) : Token.makeFake();
    }

    public Token getHeadOrFake(int i) {
        return arcs[i] != null ? arcs[i].getHead() : Token.makeFake();
    }


    public int compareTo(State t) {
        return this.step - t.step;

    }

    public State cloneState() {

        Stack<Token> stack = (Stack<Token>) this.stack.clone();
        LinkedList<Token> buffer = (LinkedList<Token>) this.buffer.clone();
        Dependency[] arcs = this.arcs.clone();
        Token first = this.firstBuffer!=null ? this.firstBuffer.clone() : null;
        Token top = this.topStack!=null ? this.topStack.clone() : null;
        int step = this.step;
        boolean rooted = this.isRooted();
        int [] leftmost = this.leftmost.clone();
        int [] rightmost = this.rightmost.clone();
        return new State(stack, buffer, arcs, top, first, input, step
                , rooted, leftmost, rightmost);
    }


    public int countArcsNotNull(){
        int count = 0;
        for(int i=0; i<arcs.length;i++){
            if(arcs[i]!=null){
                count++;
            }
        }
        return count;
    }


    /**

    public boolean isLeftAppliable(){
        return !stack.isEmpty() && !buffer.isEmpty() &&
                ( topStack.isRoot() ? false : arcs[topStack.getIndex()-1]==null);
    }

    public boolean isRightAppliable(){
        return !stack.isEmpty() && !buffer.isEmpty();
    }

    public boolean isReduceAppliable(){
        return !stack.isEmpty() &&
                (topStack.isRoot() ? rooted : arcs[topStack.getIndex()-1]!=null);
    }

    public boolean isShiftAppliable(){
        return !buffer.isEmpty();
    }

    */




    public boolean isHead(int head){
        for(int i=0;i<arcs.length;i++){
            if(arcs[i]!=null && arcs[i].getHead().getIndex()==head ){
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean equals(Object obj){

        if(obj==null){
            return false;
        }

        if(!State.class.isAssignableFrom(obj.getClass())){
            return false;
        }

        State t = (State) obj;

        if(this.step!=t.step){
            return false;
        }

        if(this.rooted!=t.rooted) return false;

        if(this.stack==null ? t.stack!=null : !this.stack.equals(t.stack)){
            return false;
        }

        if(this.buffer==null ? t.buffer!=null : !this.buffer.equals(t.buffer)){
            return false;
        }

        if(this.arcs==null ? t.arcs!=null : !this.arcs.equals(t.arcs)){
            return false;
        }

        if(this.leftmost==null ? t.leftmost!=null : !this.leftmost.equals(t.leftmost)){
            return false;
        }

        if(this.rightmost==null ? t.rightmost!=null : !this.rightmost.equals(t.rightmost)){
            return false;
        }

        return true;

       }


    @Override
    public int hashCode(){

        HashCodeBuilder builder = new HashCodeBuilder(31,17);
        builder.append(step)
                .append(stack)
                .append(buffer)
                .append(arcs)
                .build();

        return builder.toHashCode();

    }


}

