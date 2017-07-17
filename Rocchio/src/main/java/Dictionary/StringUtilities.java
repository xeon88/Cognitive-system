package Dictionary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Marco Corona on 26/06/2017.
 * Collection of function to work and parse strings
 */
public class StringUtilities {

    public static ArrayList<String> getSubStr(String s){
        ArrayList<String> tmp = new ArrayList<String>();
        String regex = "([A-zàèòùì]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(s);
        while (match.find()){
            tmp.add(match.group(0));
        }
        return tmp;
    }


    public static boolean checkWordIterative(String s){

        if(s==null || s.equals("")) return false;
        for(char c : s.toCharArray()){
            int ascii = (int)c;
            if(ascii<=64 || (ascii>=91 && ascii<=96) || ascii>122){
                return false;
            }
        }
        return true;
    }

    public static boolean checkWord(String s){

        String regex = "(\0|\\s|\\t|\\-|\\?|!|/|\\*|#|\\{|\\}|<|>|" +
                "\\$|:|\\.|;|\\(|\\)|=|%|[0-9]|:|«|»|\\+|&|')";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(s);
        boolean result = match.find();
        return !result;
    }

    public static String getNormalizedForm(String s){
        String lemma = s;
        if(lemma.length()>2){
            lemma = toProperCase(s);
        }
        return lemma;
    }


    public static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }


    public String getLemma(String entry){
        String[] splitting = entry.split("_");
        return splitting.length>1 ? splitting[0] : "";
    }


    public String getPos(String entry){
        String[] splitting = entry.split("_");
        return splitting.length>1 ? splitting[1] : "";
    }
}
