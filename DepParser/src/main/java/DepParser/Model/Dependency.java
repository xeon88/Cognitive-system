package DepParser.Model;

import DepParser.Model.Token;

/**
 * Created by Marco Corona on 03/08/2017.
 Class to represent a dependecy between nodes
 */
public class Dependency {

    private Token head ;
    private Token dependent;
    private String relationType;


    public Dependency(Token head, Token dependent, String relationType){
        this.head = head;
        this.dependent = dependent;
        this.relationType = relationType;
    }

    public Token getDependent() {
        return dependent;
    }

    public String getRelationType() {
        return relationType;
    }

    public Token getHead() {
        return head;
    }

    public boolean isLeftDep(){
        return head.getIndex()> dependent.getIndex();
    }

    public boolean isRightDep(){
        return head.getIndex() < dependent.getIndex();
    }

    @Override
    public String toString(){
        return  "(head : " + this.head.getIndex() +
                " " + this.head.getValue("form")  +
                " - dep : " + this.dependent.getIndex() +
                " " + this.dependent.getValue("form") +
                " - " + this.relationType + ")";
    }


    @Override
    public boolean equals(Object obj){

        if(obj==null){
            return false;
        }

        if(!Dependency.class.isAssignableFrom(obj.getClass())){
            return false;
        }

        Dependency t = (Dependency) obj;

        return  this.head.equals(t.head) &&
                this.dependent.equals(t.dependent) &&
                this.relationType.equals(t.relationType);
    }


    public static boolean sameArcs(Dependency [] arcs1 , Dependency [] arcs2){
        if(arcs1.length!=arcs2.length){ return false;}
        for(int i=0; i<arcs1.length; i++){
            if(!arcs1[i].equals(arcs2[i])) return false;
        }

        return true;
    }

    
    public static int getDifference(Dependency [] arcs1, Dependency [] arcs2){
        
        if(arcs1.length!=arcs2.length) return -1;
        int diff=0;
        for(int i=0; i<arcs1.length; i++){
            if(arcs1[i]!=null && arcs2[i]!=null){
                if(!arcs1[i].equals(arcs2[i])) diff++;
            }
            else{
                if(arcs1[i]!=null ^ arcs2[i]!=null) diff++;
            }
        }
    
        return diff;
    }

    
}
