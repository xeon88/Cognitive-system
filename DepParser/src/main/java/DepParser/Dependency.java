package DepParser;

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

    @Override
    public String toString(){
        return  "(head," + this.head.getIndex() +
                " " + this.head.getValue("form") + ")" +
                "(dep," + this.dependent.getIndex() +
                " " + this.dependent.getValue("form") +
                ")";
    }

}
