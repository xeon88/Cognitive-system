package DepParser.Model;

import DepParser.Utils.ConllReader;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by Marco Corona on 21/08/2017.
 */
public class Features {

    public static final int hashSize = (int)Math.pow(2,15);
    public static final int size = 27;

    protected Token s0;
    protected Token s1;
    protected Token b0;
    protected Token b1;
    protected Token b2;
    protected Token s0h;
    protected Token s0l;
    protected Token s0r;
    protected Token b0l;

    public Features(State s){

        this.s0 = s.getStackTokenOrFake(s.getStack().size()-1);
        this.s1 = s.getStackTokenOrFake(s.getStack().size()-2);
        this.b0 = s.getBufferTokenOrFake(0);
        this.b1 = s.getBufferTokenOrFake(1);
        this.b2 = s.getBufferTokenOrFake(2);
        this.s0h = Token.makeFake();
        this.s0l = Token.makeFake();
        this.s0r = Token.makeFake();
        this.b0l = Token.makeFake();
        if(!s0.isFake()){
            this.s0h = s.getHeadOrFake(s0.getIndex()-1);
            this.s0l = s.getLeftmostToken(s0.getIndex());
            this.s0r = s.getRightmostToken(s0.getIndex());
        }
        if(!b0.isFake()){
            this.b0l = s.getLeftmostToken(b0.getIndex());
        }
    }


    public int[] extract(){

        try {
        int [] features = new int[]{
                Feature.create(Feature.S0_POS.label ,  //#0
                                s0.getValue(ConllReader.Conll.UPOSTAG.getName())),

                Feature.create(Feature.S1_POS.label ,  //#1
                                s1.getValue(ConllReader.Conll.UPOSTAG.getName())),

                Feature.create(Feature.B0_POS.label , //#2
                                b0.getValue(ConllReader.Conll.UPOSTAG.getName())),

                Feature.create(Feature.B1_POS.label , //#3
                                b1.getValue(ConllReader.Conll.UPOSTAG.getName())),

                Feature.create(Feature.B2_POS.label , //#4
                                b2.getValue(ConllReader.Conll.UPOSTAG.getName())),

                Feature.create(Feature.S0_FORM.label , //#5
                                s0.getValue(ConllReader.Conll.FORM.getName())),

                Feature.create(Feature.S1_FORM.label , //#6
                                s1.getValue(ConllReader.Conll.FORM.getName())),

                Feature.create(Feature.B0_FORM.label , //#7
                                b0.getValue(ConllReader.Conll.FORM.getName())),

                Feature.create(Feature.B1_FORM.label , //#8
                                b1.getValue(ConllReader.Conll.FORM.getName())),

                Feature.create(Feature.S0_POS_FORM_B0_POS_FORM.label, //#9
                        s0.getValue(ConllReader.Conll.UPOSTAG.getName()),
                        s0.getValue(ConllReader.Conll.FORM.getName()),
                        b0.getValue(ConllReader.Conll.UPOSTAG.getName()),
                        b0.getValue(ConllReader.Conll.FORM.getName())
                        ),

                Feature.create(Feature.S0_POS_FORM.label, //#10
                                s0.getValue(ConllReader.Conll.UPOSTAG.getName()),
                                s0.getValue(ConllReader.Conll.FORM.getName())),

                Feature.create(Feature.S1_POS_FORM.label, //#11
                                s1.getValue(ConllReader.Conll.UPOSTAG.getName()),
                                s1.getValue(ConllReader.Conll.FORM.getName())),

                Feature.create(Feature.B0_POS_FORM.label, //#12
                                b0.getValue(ConllReader.Conll.UPOSTAG.getName()),
                                b0.getValue(ConllReader.Conll.FORM.getName())),

                Feature.create(Feature.B1_POS_FORM.label, //#13
                                b1.getValue(ConllReader.Conll.UPOSTAG.getName()),
                                b1.getValue(ConllReader.Conll.FORM.getName())),

                Feature.create(Feature.B2_POS_FORM.label, //#14
                                b2.getValue(ConllReader.Conll.UPOSTAG.getName()),
                                b2.getValue(ConllReader.Conll.FORM.getName())),

                Feature.create(Feature.S0_POS_B0_POS.label , //#15
                                s0.getValue(ConllReader.Conll.UPOSTAG.getName()),
                                b0.getValue(ConllReader.Conll.UPOSTAG.getName())),

                Feature.create(Feature.S0_FORM_B0_FORM.label , //#16
                        s0.getValue(ConllReader.Conll.FORM.getName()),
                        b0.getValue(ConllReader.Conll.FORM.getName())),

                Feature.create(Feature.S0_POS_B0_FORM.label , //#17
                                s0.getValue(ConllReader.Conll.UPOSTAG.getName()),
                                b0.getValue(ConllReader.Conll.FORM.getName())),

                Feature.create(Feature.S0_FORM_B0_POS.label , //#18
                                s0.getValue(ConllReader.Conll.UPOSTAG.getName()),
                                b0.getValue(ConllReader.Conll.FORM.getName())),
                Feature.create(Feature.B0_POS_B1_POS.label , //#19
                        b0.getValue(ConllReader.Conll.UPOSTAG.getName()),
                        b1.getValue(ConllReader.Conll.UPOSTAG.getName())),

                Feature.create(Feature.S0_POS_FORM_B0_POS.label , //#20
                                s0.getValue(ConllReader.Conll.UPOSTAG.getName()),
                                s0.getValue(ConllReader.Conll.FORM.getName()),
                                b0.getValue(ConllReader.Conll.UPOSTAG.getName())
                                ),
                Feature.create(Feature.S0_POS_B0_POS_FORM.label , //#21
                                s0.getValue(ConllReader.Conll.UPOSTAG.getName()),
                                b0.getValue(ConllReader.Conll.UPOSTAG.getName()),
                                b0.getValue(ConllReader.Conll.FORM.getName())),

                Feature.create(Feature.S0H_POS_S0_POS_B0_POS.label, //#22
                        s0h.getValue(ConllReader.Conll.UPOSTAG.getName()),
                        s0.getValue(ConllReader.Conll.UPOSTAG.getName()),
                        b0.getValue(ConllReader.Conll.UPOSTAG.getName())
                ),
                Feature.create(Feature.S0H_POS_FORM_S0_POS_FORM_B0_POS.label, //#23
                        s0h.getValue(ConllReader.Conll.UPOSTAG.getName()),
                        s0h.getValue(ConllReader.Conll.FORM.getName()),
                        s0.getValue(ConllReader.Conll.UPOSTAG.getName()),
                        s0.getValue(ConllReader.Conll.FORM.getName()),
                        b0.getValue(ConllReader.Conll.UPOSTAG.getName())
                        ),
                Feature.create(Feature.S0_POS_S0L_POS_B0_POS.label, //#24
                        s0.getValue(ConllReader.Conll.UPOSTAG.getName()),
                        s0l.getValue(ConllReader.Conll.UPOSTAG.getName()),
                        b0.getValue(ConllReader.Conll.UPOSTAG.getName())
                        ),
                Feature.create(Feature.S0_POS_S0R_POS_B0_POS.label,//#25
                        s0.getValue(ConllReader.Conll.UPOSTAG.getName()),
                        s0r.getValue(ConllReader.Conll.UPOSTAG.getName()),
                        b0.getValue(ConllReader.Conll.UPOSTAG.getName())
                        ),
                Feature.create(Feature.S0_POS_B0_POS_B0L_POS.label, //#26
                        s0.getValue(ConllReader.Conll.UPOSTAG.getName()),
                        b0.getValue(ConllReader.Conll.UPOSTAG.getName()),
                        b0l.getValue(ConllReader.Conll.UPOSTAG.getName())
                        )
        };
            return features;
        }
        catch (Exception ex){
            System.out.println("Error to parse state");
            System.out.println("Token s0 :" + s0.toString());
            System.out.println("Token s0h :" + s0h.toString());
            System.out.println("Token b0 :" + b0.toString());
            System.exit(1);
        }
        return null;
    }


    public enum Feature {

        S0_POS(0,"s0p"),
        S1_POS(1,"s1p"),
        B0_POS(2,"b0p"),
        B1_POS(3,"b1p"),
        B2_POS(4,"b2p"),
        S0_FORM(5,"s0f"),
        S1_FORM(6,"s1f"),
        B0_FORM(7,"b0f"),
        B1_FORM(8,"b1f"),
        S0_POS_FORM(9,"s0pf"),
        S1_POS_FORM(10,"s0pf"),
        B0_POS_FORM(11,"b0pf"),
        B1_POS_FORM(12,"b1pf"),
        B2_POS_FORM(13,"b2pf"),
        S0_POS_B0_POS(14,"s0p.b0p"),
        S0_FORM_B0_FORM(15,"s0f.b0f"),
        S0_POS_B0_FORM(16,"s0p.b0f"),
        S0_FORM_B0_POS(17,"s0p.b0f"),
        B0_POS_B1_POS(18,"bop:b1p"),
        S0_POS_FORM_B0_POS(19,"s0pf.b0p"),
        S0_POS_B0_POS_FORM(20,"s0p.b0pf"),
        S0_POS_FORM_B0_POS_FORM(21,"s0pf.b0pf"),
        S0H_POS_S0_POS_B0_POS(22,"s0hp.s0p.b0p"),
        S0H_POS_FORM_S0_POS_FORM_B0_POS(23,"s0hpf.s0pf.b0p"),
        S0_POS_S0L_POS_B0_POS(24,"s0p:s0lp:b0p"),
        S0_POS_S0R_POS_B0_POS(25,"s0p:s0rp:b0p"),
        S0_POS_B0_POS_B0L_POS(26,"s0p:b0p:b0lp");


        private Feature(int pos, String label){
            this.pos = pos;
            this.label = label;
        }

        private int pos;
        private String label ;


        public static int create(String ... keys){
            HashCodeBuilder builder = new HashCodeBuilder(31,17);
            for (String key: keys ) {
                builder.append(key);
            }

            int hash = builder.toHashCode();
            hash = Math.abs(hash%hashSize);
            return hash;
        }

    }

}
