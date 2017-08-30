package DepParser.Model;

import DepParser.Parser.Sentence;
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
    private Token topStack;
    private Dependency[] arcs;
    private Token firstBuffer;
    private Sentence input;
    private int nseq;
    private boolean rooted;


    private static final String NSUBJ = "nsubj";
    private static final String DOBJ = "dobj";
    private static final String ROOT = "root";
    private static final String OTHER = "other";


    public State (Sentence s){
        this.buffer = new LinkedList<Token>();
        this.rooted = false;
        this.stack = new Stack<Token>();
        this.topStack = null;
        this.firstBuffer = null;
        this.input = s;
        this.arcs  = new Dependency[s.tokens.length-1]; // indexed by dependent token id
        this.nseq = 0;
        initStructures(s.tokens);
    }

    public State(Stack<Token> stack, LinkedList<Token> buffer, Dependency [] arcs,
                 Token topStack, Token firstBuffer, Sentence s, int nseq, boolean rooted)  {

        this.buffer = buffer;
        this.stack = stack;
        this.arcs = arcs;
        this.topStack = topStack;
        this.firstBuffer = firstBuffer;
        this.input = s;
        this.nseq = nseq;
        this.rooted = rooted;
    }


    public void initStructures(Token [] tokens){
        stack.push(Token.makeRoot());
        for(int i = 1; i<tokens.length ; i++){
            buffer.add(tokens[i]);
        }
        topStack = !stack.isEmpty() ? stack.peek() : null;
        firstBuffer = !buffer.isEmpty()? buffer.getFirst() : null;
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

    public int getNseq(){return nseq;}

    public void shift(){
        Token vertex = this.buffer.getFirst();
        this.buffer.removeFirst();
        this.stack.push(vertex);
        topStack = !stack.isEmpty() ? stack.peek() : null;
        firstBuffer = !buffer.isEmpty()? buffer.getFirst() : null;
        nseq++;
    }


    private void left(String type){
        Token head = this.buffer.getFirst();
        Token dependent = this.stack.pop();
        Dependency dependency = new Dependency(head,dependent,type);
        arcs[dependent.getIndex()-1]=dependency;
        if(head.isRoot()) rooted = true;
        topStack = !stack.isEmpty() ? stack.peek() : null;
        firstBuffer = !buffer.isEmpty()? buffer.getFirst() : null;
        nseq++;
    }


    private void right(String type){
        Token head = this.stack.pop(); // prende il top dello stack lo salva e poi poppa lo stack
        Token dependent = this.buffer.getFirst(); // prende il primo della lista
        this.buffer.removeFirst(); // rimuove dalla lista
        this.buffer.addFirst(head); // mette all'inizio della lista la parola poppata
        Dependency dependency = new Dependency(head,dependent,type);
        arcs[dependent.getIndex()-1]=dependency;
        if(head.isRoot()) rooted = true;
        topStack = !stack.isEmpty() ? stack.peek() : null;
        firstBuffer = !buffer.isEmpty()? buffer.getFirst() : null;
        nseq++;
    }


    /*
    private void reduce(){
        this.stack.pop();
        topStack = !stack.isEmpty() ? stack.peek() : null;
        firstBuffer = !buffer.isEmpty()? buffer.getFirst() : null;
        nseq++;
    }
     */


    public State applyAction(Action.Type typeAction){

            if(Action.Type.SHIFT==typeAction){
                shift();
                return this;
            }
            /*
            else if(Action.Type.REDUCE==typeAction){
                reduce();
                return this;
            }
            */
            else if(isLeftAction(typeAction)){
                left(typeAction.getRelation());
                return this;
            }
            else if(isRightAction(typeAction)){
                right(typeAction.getRelation());
                return this;
            }
            else{
                return this;
            }
    }


    public boolean isTerminal(){
        if(this.buffer.isEmpty()){
            return true;
        }
        return false;
    }


    public void printArcs(){

        for(Dependency dep : arcs){
            if(dep!=null){
                System.out.println(dep.toString());
            }
        }
    }


    public Token getStackTokenOrFake(int i){

        return i>=0 && i<stack.size() ? stack.get(i) : Token.makeFake();
    }

    public Token getBufferTokenOrFake(int i){
        return buffer.size()>i ? buffer.get(i) : Token.makeFake();
    }

    public Token getHeadOrFake(int i){
        return arcs[i]!=null ? arcs[i].getHead() : Token.makeFake();
    }

    
    public int compareTo(State t){
        return this.nseq-t.nseq;
    }


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

    public Action.Type [] getValidAction(){

        ArrayList<Action.Type> valid = new ArrayList<Action.Type>();
        if(isShiftAppliable()) valid.add(Action.Type.SHIFT);
       // if(isReduceAppliable()) valid.add(Action.Type.REDUCE);

        if(isLeftAppliable()){
            /*
            if(!rooted){
                valid.add(Action.Type.LEFT_ROOT);
            }
            */
            valid.add(Action.Type.LEFT_DOBJ);
            valid.add(Action.Type.LEFT_NSUBJ);
            valid.add(Action.Type.LEFT_OTHER);
        }

        if(isRightAppliable()){
            /*
            if(!rooted){
                valid.add(Action.Type.RIGHT_ROOT);
            }
            */
            valid.add(Action.Type.RIGHT_DOBJ);
            valid.add(Action.Type.RIGHT_NSUBJ);
            valid.add(Action.Type.RIGHT_OTHER);
        }

        return valid.toArray(new Action.Type[valid.size()]);
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

        if(this.nseq!=t.nseq){
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

        return true;

       }


    @Override
    public int hashCode(){

        HashCodeBuilder builder = new HashCodeBuilder(31,17);
        builder.append(nseq)
                .append(stack)
                .append(buffer)
                .append(arcs)
                .build();

        return builder.toHashCode();

    }



}
