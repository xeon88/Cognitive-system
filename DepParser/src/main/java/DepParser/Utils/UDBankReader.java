package DepParser.Utils;

import DepParser.Model.ArcEager;
import DepParser.Model.Dependency;
import DepParser.Model.GoldTree;
import DepParser.Model.Token;
import DepParser.Parser.ArcEager.EagerTrainer;
import DepParser.Parser.Sentence;
import DepParser.Parser.Tester;
import DepParser.Parser.TBParser;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Marco Corona on 08/08/2017.
 */
public class UDBankReader {

    private EagerTrainer trainer;
    private Tester tester;
    private static final String [] relations = new String[]{"nsubj","dobj","noname"};
    private Sentence[] sentences ;

    public enum UDIndex{

        ID(0,"id"),
        FORM(1,"form"),
        LEMMA(2,"lemma"),
        UPOSTAG(3,"upostag"),
        XPOSTAG(4,"xpostag"),
        FEATS(5,"feats"),
        HEAD(6,"idhead"),
        DEPREL(7,"deprel"),
        DEPS(8,"deps");

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


    public UDBankReader(File train) {

        trainer = new EagerTrainer(ArcEager.SIZE);
        try {
            ReadUDFile(train,true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public UDBankReader(File test, TBParser parser){

        this.tester = new Tester(parser);
        try {
            ReadUDFile(test,false);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public EagerTrainer getTrainer(){
        return trainer;
    }

    public Tester getTester(){return tester;}

    public Sentence[] getSentences() {
        return sentences;
    }


    public void ReadUDFile(File file, boolean training) throws IOException {
        FileReader reader = new FileReader(file);
        BufferedReader buffreader = new BufferedReader(reader);
        String line ;
        

        ArrayList<Sentence> tmp = new ArrayList<Sentence>();
        int count = 0;
        int limit = 500;
        ArrayList<Token>  tokens = new ArrayList<Token>();
        ArrayList<Dependency> deps = new ArrayList<Dependency>();
        tokens.add(Token.makeRoot());

        while((line = buffreader.readLine())!=null){
            if(line.equals("")){
                for(Token token : tokens){
                    if(token.isRoot()) {
                        continue;
                    }
                    else{
                        deps.add(new Dependency(
                                tokens.get(token.getHead()),
                                token,
                                token.getValue(UDIndex.DEPREL.getName()))
                            );
                    }
                }

                GoldTree tree = new GoldTree();
                Dependency [] dependencies = deps.toArray(new Dependency[deps.size()]);
                tree.setDependencies(dependencies);
                Sentence s = new Sentence(count);
                Token [] sentenceTokens = tokens.toArray(new Token[tokens.size()]);
                s.setTokens(sentenceTokens);
                s.setId(count);
                tmp.add(s);
                
                if(training){
                    trainer.train(tree,s);
                }
                else{
                    tester.addGoldTree(s.id, tree);
                }
                
                
              
                System.out.println("Loaded sentence : " + (count));
                count++;
                if(count>=limit) break;
                // reset
                tokens = new ArrayList<Token>();
                tokens.add(Token.makeRoot());
                deps = new ArrayList<Dependency>();
            }
            else{

                Token token = createToken(line);
                tokens.add(token);
            }
        }

        sentences = tmp.toArray(new Sentence[tmp.size()]);

        if(training){
            float [][][] result = trainer.getModel().getResultWeights(trainer.getCount());
            trainer.getModel().setWeights(result);
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
            else if(udi.getName().equals("deprel")){

                String relation = "noname";
                for(int i = 0; i<relations.length; i++){
                    if(relations[i].equals(row[udi.getIndex()])){
                        relation = row[udi.getIndex()];
                    }
                }

                token.setAttributes(udi.getName(),relation);
            }
            else {
                token.setAttributes(udi.getName(), row[udi.getIndex()]);
            }
        }
        return token;
    }


}
