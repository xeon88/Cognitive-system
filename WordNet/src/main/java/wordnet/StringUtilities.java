package wordnet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Marco Corona on 08/07/2017.
 */


public class StringUtilities {

    public static String getNormalizedForm(String s) {
        String lemma = s;
        if(lemma.length()>2){
            lemma = toProperCase(s);
        }
        return lemma;
    }

    public static String getSubStr(String s){
        String regex = "([A-Za-zàèòùì]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(s);
        if(match.find()){
            return match.group(0);
        }
        return null;
    }



     static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }



}
