package DepParser;

/**
 * Created by Marco Corona on 11/08/2017.
 */
public class Action {

    public enum Type {

        NOP(-1,"no-op"),
        SHIFT(0,"Shift"),
        LEFT(1,"Left"),
        RIGHT(2,"Right"),
        REDUCE(3,"Reduce");

        private int type;
        private String name;

        private Type(int type,
                     String name){
            this.type = type;
            this.name = name;
        }

        public int getType() {
            return type;
        }
        public String getName(){return name;}
    }


}
