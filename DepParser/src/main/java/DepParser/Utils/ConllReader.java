package DepParser.Utils;

import DepParser.Model.Arc;
import DepParser.Model.GoldTree;
import DepParser.Model.Token;
import DepParser.Parser.ConllStorage;
import DepParser.Model.Sentence;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Marco Corona on 08/08/2017.
 */


public class ConllReader {

    private ConllStorage storage;
    private static final String [] relations = new String[]{"nsubj","dobj","noname"};
    private Sentence[] sentences ;

    public enum Conll {
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

        Conll(int i, String name){
            this.index = i;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }
        public String getName(){return  name;}

    }


    public ConllStorage getStorage() {
        return storage;
    }

    public ConllReader(File conll, ConllStorage storage ){
        this.storage = storage;
        try {
            ReadUDFile(conll);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Sentence[] getSentences() {
        return sentences;
    }



    public void ReadUDFile(File file) throws IOException {

        FileReader reader = new FileReader(file);
        BufferedReader buffreader = new BufferedReader(reader);
        String line ;
        ArrayList<Sentence> tmp = new ArrayList<Sentence>();
        int count = 0;
        ArrayList<Token>  tokens = new ArrayList<Token>();
        tokens.add(Token.makeRoot());

        while((line = buffreader.readLine())!=null){
            if(line.equals("")){
                GoldTree tree = makeGoldTree(tokens);
                Sentence s = makeSentence(count,tokens);
                tmp.add(s);
                storage.addGoldTree(s,tree);
                System.out.println("Loaded sentence : " + (count));
                count++;
                //if(count>=limit) break;

                // reset
                tokens = new ArrayList<Token>();
                tokens.add(Token.makeRoot());
            }
            else{
                Token token = createToken(line);
                tokens.add(token);
            }
        }

        sentences = tmp.toArray(new Sentence[tmp.size()]);
    }


    private Token createToken(String line){

        Token token = null;
        String[] row = line.split("\t");
        if(row.length<=1){
            return token;
        }

        token = new Token();
        for ( Conll udi : Conll.values()) {
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




    private GoldTree makeGoldTree(ArrayList<Token> tokens){
        ArrayList<Arc> deps = new ArrayList<Arc>();
        for(Token token : tokens){
            if(token.isRoot()) {
                continue;
            }
            else{
                deps.add(new Arc(
                        tokens.get(token.getHead()),
                        token,
                        token.getValue(Conll.DEPREL.getName()))
                );
            }
        }

        GoldTree tree = new GoldTree();
        Arc[] dependencies = deps.toArray(new Arc[deps.size()]);
        tree.setDependencies(dependencies);
        return tree;
    }


    private Sentence makeSentence(int id, ArrayList<Token> tokens){
        Sentence s = new Sentence(id);
        Token [] sentenceTokens = tokens.toArray(new Token[tokens.size()]);
        s.setTokens(sentenceTokens);
        s.setId(id);
        return  s;
    }

}
