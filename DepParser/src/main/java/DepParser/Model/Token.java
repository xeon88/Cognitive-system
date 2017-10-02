package DepParser.Model;

import DepParser.Utils.ConllReader;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marco Corona on 03/08/2017.
 *
 *
 *  ID: Word index, integer starting at 1 for each new sentence; may be a range for multiword tokens; may be a decimal number for empty nodes.
 *  FORM: Word form or punctuation symbol.
 *  LEMMA: Lemma or stem of word form.
 *  UPOSTAG: Universal part-of-speech tag.
 *  XPOSTAG: Language-specific part-of-speech tag; underscore if not available.
 *  FEATS: List of morphological features from the universal feature inventory or from a defined language-specific extension; underscore if not available.
 *  HEAD: Head of the current word, which is either a value of ID or zero (0).
 *  DEPREL: Universal dependency relation to the HEAD (root iff HEAD = 0) or a defined language-specific subtype of one.
 *  DEPS: Enhanced dependency graph in the form of a list of head-deprel pairs.
 *  MISC: Any other annotation.
 */


public class Token implements Cloneable{

    private int index;
    private int head;
    private Map<String,String> attributes;

    public Token(){
        attributes = new HashMap<String, String>();
    }

    public static Token makeRoot(){

        Token root = new Token();
        root.index = 0;
        root.head = 0;
        root.attributes = new HashMap<String, String>();
        root.attributes.put("form","<ROOT>");
        root.attributes.put("lemma","<ROOT>");
        root.attributes.put("upostag","<>");
        root.attributes.put("xpostag","<>");
        root.attributes.put("xpostag","<>");
        root.attributes.put("idhead","0");
        return root;
    }

    public static Token makeFake(){
        Token fake = new Token();
        fake.index = -1;
        fake.head = -1;
        fake.attributes = new HashMap<String, String>();
        fake.attributes.put("form","");
        fake.attributes.put("lemma","");
        fake.attributes.put("upostag","");
        fake.attributes.put("xpostag","");
        fake.attributes.put("xpostag","");
        fake.attributes.put("idhead","");
        return fake;

    }


    public int getIndex() {
        return index;
    }

    public int getHead(){
        return head;
    }

    public boolean isRoot(){
        return index==0 && attributes.get(ConllReader.Conll.FORM.getName()).equals("<ROOT>");

    }

    public boolean isFake(){
        return index==-1 && head==-1 && attributes.get(ConllReader.Conll.FORM.getName()).equals("") ;
    }

    public void setIndex(int i){
        this.index=i;
    }

    public void setHead(int h){
        this.head = h;
    }

    public void setAttributes(String attr, String value){
        this.attributes.put(attr,value);
    }

    public String getValue(String attr){
        return  attributes.containsKey(attr) ? attributes.get(attr) : null;
    }


    public boolean equals(Token t){
        if(t.head!=this.head) return false;
        if(t.index!=this.index) return false;
        if(t.attributes.size()!=t.attributes.size()) {
            return false;
        }
        else {
            for(String attr : attributes.keySet()){
                if(!t.attributes.keySet().contains(attr)) return false;
                String value = this.attributes.get(attr);
                if(!t.attributes.get(attr).equals(value)) return false;
            }
        }

        return true;
    }


    @Override
    public Token clone(){
        Token clone = new Token();
        clone.head = this.head;
        clone.index = this.index;
        clone.attributes = new HashMap<String, String>();
        for( String key : this.attributes.keySet()){
            clone.attributes.put(key,this.attributes.get(key));
        }

        return clone;
    }


    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder .append("index : " + index + "\n")
                .append("head : " + head + "\n");

        for(String key : attributes.keySet()){
            builder.append(key + "- " + attributes.get(key) + "\n");
        }

        return builder.toString();
    }
}
