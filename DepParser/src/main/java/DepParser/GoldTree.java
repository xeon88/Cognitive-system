package DepParser;

/**
 * Created by Marco Corona on 17/08/2017.
 */
public class GoldTree extends ProjectiveTree {

    private Action.Type [] goldSeqs;

    public GoldTree(){
        super();
    }

    public Action.Type[] getGoldSeqs() {
        return goldSeqs;
    }

    public void setGoldSeqs(Action.Type[] goldSeqs) {
        this.goldSeqs = goldSeqs;
    }


}
