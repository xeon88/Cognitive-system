package DepParser;

import java.util.*;

/**
 * Created by Marco Corona on 17/08/2017.
 */
public class GoldTree extends ProjectiveTree {

    private TreeMap<State,Action.Type> history;


    public GoldTree(){
        super();
    }


    public TreeMap<State,Action.Type> getGoldSeqs() {
        return history;
    }


    public void setGoldSeqs(TreeMap<State, Action.Type> history) {
        this.history = history;
    }


    public void printGoldSeqs(){

        System.out.println(history.toString());
    }
}
