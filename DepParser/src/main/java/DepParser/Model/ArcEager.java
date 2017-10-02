package DepParser.Model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Created by Marco Corona on 30/08/2017.
 */
public class ArcEager extends ArcSystem {
    
    public static final int SIZE = 8;

    public enum Type implements transition {

        NOP(-1, "no-op", "") {
            public boolean isAppliable(State state) {
                return false;
            }

            public State apply(State state) {
                return state.clone();
            }
        },
        SHIFT(0, "Shift", "") {
            public boolean isAppliable(State state) {
                return true;
            }

            public State apply(State state) {
                State next = state.clone();
                Stack<Token> stack = next.getStack();
                LinkedList<Token> buffer = next.getBuffer();
                Token first = buffer.getFirst();
                buffer.removeFirst();
                stack.push(first);
                next.setTopStack(!stack.isEmpty() ? stack.peek() : null);
                next.setFirstBuffer(!buffer.isEmpty() ? buffer.getFirst() : null);
                next.incrementStep();
                return next;
            }
        },

        LEFT_DOBJ(1, "Left", "dobj") {
            public boolean isAppliable(State state) {
                return isLeftAppliable(state);
            }

            public State apply(State state) {
                return applyLeft(state, this.relation);
            }
        },
        LEFT_NSUBJ(2, "Left", "nsubj") {
            public boolean isAppliable(State state) {
                return isLeftAppliable(state);
            }

            public State apply(State state) {
                return applyLeft(state, this.relation);
            }
        },
        LEFT_OTHER(3, "Left", "noname") {
            public boolean isAppliable(State state) {
                return isLeftAppliable(state);
            }

            public State apply(State state) {
                return applyLeft(state, this.relation);
            }
        },
        RIGHT_NSUBJ(4, "Right", "nsubj") {
            public boolean isAppliable(State state) {
                return isRightAppliable(state);
            }

            public State apply(State state) {
                return applyRight(state, this.relation);
            }
        },
        RIGHT_DOBJ(5, "Right", "dobj") {
            public boolean isAppliable(State state) {
                return isRightAppliable(state);
            }

            public State apply(State state) {
                return applyRight(state, this.relation);
            }
        },
        RIGHT_OTHER(6, "Right", "noname") {
            public boolean isAppliable(State state) {
                return isRightAppliable(state);
            }

            public State apply(State state) {
               return applyRight(state,this.relation);
            }
        },
        REDUCE(7, "Reduce", "") {
            public boolean isAppliable(State state) {
                if (!state.getStack().isEmpty()) {
                    Arc[] arcs = state.getArcs();
                    Token head = state.getTopStack();
                    if ((head.getIndex()!=0 && arcs[head.getIndex() - 1] != null)) {
                        return true;
                    }
                }
                return false;
            }

            public State apply(State state) {
                State next = state.clone();
                Stack<Token> stack = next.getStack();
                LinkedList<Token> buffer = next.getBuffer();
                stack.pop();
                next.setTopStack(!stack.isEmpty() ? stack.peek() : null);
                next.setFirstBuffer(!buffer.isEmpty() ? buffer.getFirst() : null);
                next.incrementStep();
                return next;
            }
        };

        protected int type;
        protected String name;
        protected String relation;

        private Type(int type, String name, String relation) {
            this.type = type;
            this.name = name;
            this.relation = relation;
        }

        public int getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getRelation() {
            return relation;
        }


        protected State applyLeft(State state, String relation) {

            State next = state.clone();
            Stack<Token> stack = next.getStack();
            LinkedList<Token> buffer = next.getBuffer();
            Token head = buffer.getFirst();
            Token dependent = stack.pop();
            Arc dependency = new Arc(head, dependent, relation);
            next.updateLeftMost(dependency);
            next.setArc(dependent.getIndex() - 1, dependency);
            next.setTopStack(!stack.isEmpty() ? stack.peek() : null);
            next.setFirstBuffer(!buffer.isEmpty() ? buffer.getFirst() : null);
            next.incrementStep();
            return next;
        }


        protected State applyRight(State state, String relation) {
            State next = state.clone();
            Stack<Token> stack = next.getStack();
            LinkedList<Token> buffer = next.getBuffer();
            Token dependent = buffer.getFirst();
            Token head = stack.peek();
            buffer.removeFirst();
            stack.push(dependent);
            Arc dependency = new Arc(head, dependent, relation);
            next.updateRightMost(dependency);
            next.setArc(dependent.getIndex() - 1, dependency);
            next.setTopStack(!stack.isEmpty() ? stack.peek() : null);
            next.setFirstBuffer(!buffer.isEmpty() ? buffer.getFirst() : null);
            next.incrementStep();
            return next;
        }


        public static boolean isLeftAppliable(State state) {
            if (!state.getStack().isEmpty()) {
                Arc[] arcs = state.getArcs();
                Token head = state.getTopStack();
                if (head.getIndex() != 0 &&
                        arcs[head.getIndex()-1] == null) {
                    return true;
                }
            }
            return false;
        }

        public static boolean isRightAppliable(State state){
            return !state.getStack().isEmpty() ;
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


    public static ArcEager.Type [] getValidActions(State state) {
        ArrayList<ArcEager.Type> appliable = new ArrayList<ArcEager.Type>();
        for(ArcEager.Type action : ArcEager.Type.values()){
            if(action.isAppliable(state)) appliable.add(action);
        }

        return appliable.toArray(new ArcEager.Type[appliable.size()]);
    }



    public static boolean testShift(State prev, State next){

        if(!next.getTopStack().equals(prev.getFirstBuffer())) return false;
        if(next.getBuffer().size()!=prev.getBuffer().size()-1) return false;
        if(next.getStack().size()!=prev.getStack().size()+1) return false;
        if(next.getStep()<=prev.getStep()) return false;
        return true;
    }

    public static boolean testLeft(State prev, State next){
        boolean test = true;
        if(next.getTopStack().equals(prev.getTopStack())) {
            System.out.println("new top stack equals of old top stack");
            test &=false;
        }

        Arc arc = next.getArcs()[prev.getTopStack().getIndex()-1];
        if(arc==null) {
            System.out.println("dependency not added");
            test  &=false;
        }
        if(!arc.getHead().equals(prev.getFirstBuffer())) {
            System.out.println("head is not corrected");
            test &=false;
        }
        if(next.getStep()<=prev.getStep()) {
            System.out.println("step not incremented");
            test &=false;
        }
        return test;
    }


    public static boolean testRight(State prev, State next){
        boolean test = true;
        if(!next.getTopStack().equals(prev.getFirstBuffer())){
            System.out.println("new top stack not equals of old first buffer");
            test &= false;
        }
        if(next.getArcs()[prev.getFirstBuffer().getIndex()-1]==null) {
            System.out.println("dependency not added");
            test &=false;
        }
        if(!next.getArcs()[prev.getFirstBuffer().getIndex()-1].getHead().
                equals(prev.getTopStack())) {
            System.out.println("head dependecy is not corrected");
            test &=false;
        }
        if(next.getStep()<=prev.getStep()) {
            System.out.println("step not incremented ");
            test &= false;
        }
        return test;
    }


    public static boolean testReduce(State prev, State next){
        boolean test = true;
        if(next.getTopStack().equals(prev.getTopStack())){
            System.out.println("new to stack is equal to old top stack");
            test &= false;
        }
        if(next.getStep()<=prev.getStep()) {
            System.out.println("step not incremented");
            test &=false;
        }
        return true;
    }


    public static boolean test(Type action, State prev, State next){
        boolean test = false;

        if(action==Type.SHIFT) test =testShift(prev,next);
        if(action==Type.REDUCE) test =testReduce(prev,next);
        if(ArcEager.Type.isLeftAction(action)) test = testLeft(prev,next);
        if(ArcEager.Type.isRightAction(action)) test =testRight(prev,next);
        return test;
    }

}
