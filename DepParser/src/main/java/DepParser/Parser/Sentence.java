package DepParser.Parser;

import DepParser.Model.Token;

/**
 * Created by Marco Corona on 17/08/2017.
 */
public class Sentence {

    public int id;
    public Token[] tokens;

    public Sentence(int sentenceId){
        this.id = sentenceId;
    }


    public void setTokens(Token[] tokens) {
        this.tokens = tokens;
    }


    public void setId(int id) {
        this.id = id;
    }

}
