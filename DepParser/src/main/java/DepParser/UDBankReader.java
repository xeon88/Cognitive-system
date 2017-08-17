package DepParser;

import java.awt.image.Kernel;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by Marco Corona on 08/08/2017.
 */
public class UDBankReader {

    private Trainer trainer;

    public enum UDIndex{

        ID(0,"id"),
        FORM(1,"form"),
        LEMMA(2,"lemma"),
        UPOSTAG(3,"upostag"),
        XPOSTAG(4,"xpostag"),
        FEATS(5,"feats"),
        HEAD(6,"idhead"),
        DEPREL(7,"deprel"),
        DEPS(8,"deprel");

        private int index;
        private String name;

        UDIndex(int i, String name){
            this.index = i;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }
        public String getName(){return  name;}

    }


    public UDBankReader(File udbank) {

        trainer = new Trainer();
        try {
            LoadProjTrees(udbank);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Trainer getTrainer(){
        return trainer;
    }


    private void LoadProjTrees( File file) throws IOException {
        FileReader reader = new FileReader(file);
        BufferedReader buffreader = new BufferedReader(reader);
        String line ;


        int count = 0;
        ArrayList<Token>  tokens = new ArrayList<Token>();
        ArrayList<Dependency> deps = new ArrayList<Dependency>();
        tokens.add(Token.makeRoot());

        while((line = buffreader.readLine())!=null){
            if(line.equals("")){
                for(Token token : tokens){
                    if(token.isRoot()) {
                        continue;
                    }
                    if(token.getIndex()==1){
                        deps.add(new Dependency(tokens.get(0),token,"root"));
                    }
                    else{
                        deps.add(new Dependency(
                                tokens.get(token.getHead()),
                                token,
                                token.getValue(UDIndex.HEAD.getName()))
                            );
                    }
                }

                ProjectiveTree tree = new ProjectiveTree();
                Dependency [] dependencies = deps.toArray(new Dependency[deps.size()]);
                tree.setDependencies(dependencies);
                trainer.addGoldTree(count,tree);
                tokens = new ArrayList<Token>();
                tokens.add(Token.makeRoot());
                deps = new ArrayList<Dependency>();

                System.out.println("Loaded sentence : " + (count+1));

                count++;
            }
            else{

                Token token = createToken(line);
                tokens.add(token);
            }
        }

    }





    private Token createToken(String line){

        Token token = null;
        String[] row = line.split("\t");
        if(row.length<=1){
            return token;
        }

        token = new Token();
        for ( UDIndex udi : UDIndex.values()) {
            if(udi.getName().equals("id")){
                int id = Integer.parseInt(row[udi.getIndex()]);
                token.setIndex(id);
            }
            else if(udi.getName().equals("idhead")){
                int id = Integer.parseInt(row[udi.getIndex()]);
                token.setHead(id);
            }
            else{
                token.setAttributes(udi.getName(), row[udi.getIndex()]);
            }
        }
        return token;
    }


}
