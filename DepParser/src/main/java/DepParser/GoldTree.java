package DepParser;

import java.util.*;

/**
 * Created by Marco Corona on 17/08/2017.
 */
public class GoldTree extends ProjectiveTree {

    private LinkedHashMap<Integer,Transition> history;


    public GoldTree(){
        super();
    }


    public LinkedHashMap<Integer,Transition> getGoldSeqs() {
        return history;
    }


    public void setGoldSeqs(LinkedHashMap<Integer,Transition> history) {
        this.history = history;
    }


    public void printGoldSeqs(){

        
        for(Map.Entry<Integer,Transition> entry : history.entrySet()){
          System.out.println("Step ; " + entry.getKey().intValue());
          System.out.println("Action : " + entry.getValue().getAction().getName());
        }
        System.out.println(history.toString());
    }
}
