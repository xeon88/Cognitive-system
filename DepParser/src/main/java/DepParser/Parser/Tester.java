package DepParser.Parser;

import DepParser.Model.Dependency;
import DepParser.Model.ProjectiveTree;

import java.util.HashMap;

/**
 * Created by Marco Corona on 28/08/2017.
 */
public class Tester {

    private HashMap<Integer,ProjectiveTree> goldTrees;
    private TransitionBasedParser parser;

    public Tester(TransitionBasedParser parser){
        this.goldTrees = new HashMap<Integer,ProjectiveTree>();
        this.parser = parser;
    }


    public HashMap<Integer, ProjectiveTree> getGoldTrees() {
        return goldTrees;
    }

    public TransitionBasedParser getParser() {
        return parser;
    }


    public void addGoldTree(int id, ProjectiveTree tree){
        goldTrees.put(id,tree);
    }


    public double getAccuracy(int id, Dependency[] found){
        
        Dependency [] gold = goldTrees.get(id).getDependencies();
        int diff = Dependency.getDifference(found, gold);
        if(diff==-1){
            throw new IllegalArgumentException("Arcs found not comparable with gold tree");
        }

        double acc = (double)(gold.length-diff)/(double)(gold.length);
        return acc;
    }
    
}
