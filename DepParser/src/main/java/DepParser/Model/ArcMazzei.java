package DepParser.Model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Created by Marco Corona on 30/08/2017.
 */
public class ArcMazzei extends ArcSystem {

    public static final int SIZE = 7;
    
    
    public enum Type implements operation{

        NOP(-1,"no-op","") {
            public boolean isAppliable(State state) {
                return false;
            }

            public State apply(State state) {
                return state.cloneState() ;
            }
        },
        SHIFT(0,"Shift","") {
            public boolean isAppliable(State state) {
                return !state.getBuffer().isEmpty();
            }

            public State apply(State state) {
                Token first = state.getBuffer().getFirst();
                state.getBuffer().removeFirst();
                state.getStack().push(first);
                Stack<Token> stack = state.getStack();
                LinkedList<Token> buffer = state.getBuffer();
                state.setTopStack(!stack.isEmpty() ? stack.peek() : null);
                state.setFirstBuffer(!buffer.isEmpty()? buffer.getFirst() : null);
                state.incrementStep();
                return state.cloneState();
            }
        },

        LEFT_DOBJ(1,"Left","dobj") {
            public boolean isAppliable(State state) {
                return !state.getStack().isEmpty() &&
                        !state.getBuffer().isEmpty();
            }

            public State apply(State state) {
                return applyLeft(state,this.relation);
            }
        },
        LEFT_NSUBJ(2,"Left","nsubj") {
            public boolean isAppliable(State state) {
                return !state.getStack().isEmpty() &&
                        !state.getBuffer().isEmpty();
            }

            public State apply(State state) {
                return applyLeft(state,this.relation);
            }
        },
        LEFT_OTHER(3,"Left","noname") {
            public boolean isAppliable(State state) {
                return !state.getStack().isEmpty() &&
                        !state.getBuffer().isEmpty();
            }

            public State apply(State state) {
                return applyLeft(state,this.relation);
            }
        },
        //LEFT_ROOT(1,"Left","root"),
        //RIGHT_ROOT(5,"Right","root"),
        RIGHT_NSUBJ(4,"Right","nsubj") {
            public boolean isAppliable(State state) {
                return !state.getStack().isEmpty() &&
                        !state.getBuffer().isEmpty();

            }


            public State apply(State state) {
                return applyRight(state,this.relation);
            }
        },
        RIGHT_DOBJ(5,"Right","dobj") {
            public boolean isAppliable(State state) {
                return false;
            }

            public State apply(State state) {
                return applyRight(state,this.relation);
            }
        },
        RIGHT_OTHER(6,"Right","noname") {
            public boolean isAppliable(State state) {
                return false;
            }

            public State apply(State state){
               return applyRight(state,this.relation);
            }
        };
        //REDUCE(7,"Reduce","")

        protected int type;
        protected String name;
        protected String relation;

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


        public State applyLeft(State state, String relation){
            Stack<Token> stack = state.getStack();
            LinkedList<Token> buffer = state.getBuffer();
            Token head = buffer.getFirst();
            Token dependent = stack.pop();
            Dependency dependency = new Dependency(head,dependent,relation);
            if(head.isRoot()) state.setRooted(true);
            state.setArc(dependent.getIndex()-1,dependency);
            state.setTopStack(!stack.isEmpty() ? stack.peek() : null);
            state.setFirstBuffer(!buffer.isEmpty()? buffer.getFirst() : null);
            state.incrementStep();
            return state.cloneState();
        }


        public State applyRight(State state, String relation){
            Stack<Token> stack = state.getStack();
            LinkedList<Token> buffer = state.getBuffer();
            Token dependent = buffer.getFirst();
            Token head = stack.pop();
            buffer.removeFirst(); // rimuove dalla lista
            buffer.addFirst(head);
            Dependency dependency = new Dependency(head,dependent,relation);
            if(head.isRoot()) state.setRooted(true);
            state.setArc(dependent.getIndex()-1,dependency);
            state.setTopStack(!stack.isEmpty() ? stack.peek() : null);
            state.setFirstBuffer(!buffer.isEmpty()? buffer.getFirst() : null);
            state.incrementStep();
            return state.cloneState();
        }


        public static boolean isLeftAppliable(State state){
            return !state.getStack().isEmpty() && !state.getStack().isEmpty();
        }

        public static boolean isRightAppliable(State state){
            return !state.getStack().isEmpty() && !state.getStack().isEmpty();
        }

        public static boolean isLeftAction(Type action){
            if(action.getType()>=1 && action.getType()<=3){
                return true;
            }
            return false;
        }



        public static boolean isRightAction(Type action){
            if(action.getType()>=4 && action.getType()<=6){
                return true;
            }
            return false;
        }


    }


    public operation[] getValidAction(State state) {
        ArrayList<Type> appliable = new ArrayList<Type>();
        for(Type action : ArcMazzei.Type.values()){
            if(action.isAppliable(state)) appliable.add(action);
        }

        return appliable.toArray(new Type[appliable.size()]);
    }
}
