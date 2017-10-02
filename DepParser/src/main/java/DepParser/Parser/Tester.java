package DepParser.Parser;

import DepParser.Model.Arc;
import DepParser.Model.GoldTree;
import DepParser.Model.Sentence;

import java.util.HashMap;

/**
 * Created by Marco Corona on 28/08/2017.
 */
public class Tester implements ConllStorage{

    private HashMap<Integer,GoldTree> goldTrees;
    private TBParser parser;

    public Tester(TBParser parser){
        this.goldTrees = new HashMap<Integer,GoldTree>();
        this.parser = parser;
    }


    public HashMap<Integer, GoldTree> getGoldTrees() {
        return goldTrees;
    }

    public TBParser getParser() {
        return parser;
    }


    public void addGoldTree(Sentence s, GoldTree tree){
        goldTrees.put(s.id,tree);
    }

    

    public double getLAS(int id, Arc[] found){
        
        Arc[] gold = goldTrees.get(id).getDependencies();
        int diff = Arc.getDifference(found, gold);
        if(diff==-1){
            throw new IllegalArgumentException("Arcs found not comparable with gold tree");
        }

        double acc = (double)(gold.length-diff)/(double)(gold.length);
        return acc;
    }


    public double getUAS(int id, Arc[] found){
        Arc[] gold = goldTrees.get(id).getDependencies();
        int diff = Arc.getUnlabelledDifference(found, gold);
        if(diff==-1){
            throw new IllegalArgumentException("Arcs found not comparable with gold tree");
        }

        double acc = (double)(gold.length-diff)/(double)(gold.length);
        return acc;
    }
    
}
