package DepParser.Model;

/**
 * Created by Marco Corona on 11/08/2017.
 */
public class Action {




    public static final int size = 7;

    public enum Type {

        NOP(-1,"no-op",""),
        SHIFT(0,"Shift",""),
        //LEFT_ROOT(1,"Left","root"),
        LEFT_DOBJ(1,"Left","dobj"),
        LEFT_NSUBJ(2,"Left","nsubj"),
        LEFT_OTHER(3,"Left","noname"),
        //RIGHT_ROOT(5,"Right","root"),
        RIGHT_NSUBJ(4,"Right","nsubj"),
        RIGHT_DOBJ(5,"Right","dobj"),
        RIGHT_OTHER(6,"Right","noname");
        //REDUCE(7,"Reduce","")

        private int type;
        private String name;
        private String relation;

        private Type(int type, String name, String relation){
            this.type = type;
            this.name = name;
            this.relation = relation;
        }

        public int getType() {
            return type;
        }
        public String getName(){return name;}
        public String getRelation(){return relation;}



    }


    public static String [] getAllActionName( Action.Type [] actions){

        String out [] = new String[actions.length];
        for (int i=0; i<actions.length ; i++ ){
          out[i]= "(" + actions[i].getType() + " - " + actions[i].getName() + " - " + actions[i].getRelation() + ")";
        }
        return out;
    }


}
