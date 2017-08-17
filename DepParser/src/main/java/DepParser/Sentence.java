package DepParser;

/**
 * Created by Marco Corona on 17/08/2017.
 */
public class Sentence {

    private int id;
    private Token [] tokens;

    public Sentence(int sentenceId){
        this.id = sentenceId;
    }

    public int getId() {
        return id;
    }


    public void setTokens(Token[] tokens) {
        this.tokens = tokens;
    }


    public void setId(int id) {
        this.id = id;
    }

    public Token[] getTokens() {
        return tokens;
    }
}
