package DepParser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Created by Marco Corona on 01/08/2017.
 */
public class TransitionBasedParser {



    private Oracle oracle;
    private ArrayList<Dependency> dependencies ;


    public TransitionBasedParser(Oracle oracle){
        this.oracle = oracle;
        this.dependencies = new ArrayList<Dependency>();
    }



    private boolean terminal() {
        return true;
    }




    private ArrayList<Token> makeVerticesList(String sentence) {

        ArrayList<Token> vertices = new ArrayList<Token>();
        return vertices;

    }






    public ProjectiveTree parse(String sentence){
        ProjectiveTree tree = new ProjectiveTree();
        return tree;
    }
}
