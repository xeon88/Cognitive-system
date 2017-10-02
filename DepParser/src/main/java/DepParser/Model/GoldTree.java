package DepParser.Model;

/**
 * Created by Marco Corona on 17/08/2017.
 */
public class GoldTree extends Tree {

    private Transition [] history;


    public GoldTree(){
        super();
    }


    public Transition[] getGoldSeqs() {
        return history;
    }


    public void setGoldSeqs(Transition [] history) {
        this.history = history;
    }


    public void printGoldSeqs(){

        for(int i = 0; i<history.length ; i++){
          System.out.println("Step ; " + i);
          System.out.println("Action : " + history[i].getAction().getName());
        }
        System.out.println(history.toString());
    }
}
