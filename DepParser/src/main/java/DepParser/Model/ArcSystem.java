package DepParser.Model;

/**
 * Created by Marco Corona on 11/08/2017.
 */
public abstract class ArcSystem {


    public interface operation {

        public int getType();
        public String getName();
        public String getRelation();
        public boolean isAppliable(State state);
        public State apply(State state);
    }


    public static String []  getAllActionName( operation [] actions){

        String out [] = new String[actions.length];
        for (int i=0; i<actions.length ; i++ ){
          out[i]= "(" + actions[i].getType() + " - " + actions[i].getName() + " - " + actions[i].getRelation() + ")";
        }
        return out;
    }




}
