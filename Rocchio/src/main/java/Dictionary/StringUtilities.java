package Dictionary;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Marco Corona on 26/06/2017.
 */
public class StringUtilities {

    public static String getSubStr(String s){
        String regex = "(\\.\\.\\.|\\.|#|&|;|" +
                ":|«|»|%|\\?|@|\\”)*([A-Za-zàèòùì]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(s);
        if(match.find()){
            return match.group(2);
        }
        return null;
    }



    public static boolean checkWord(String s){

        String regex = "(\\-|\\?|!|/|\\*|#|\\{|\\}|<|>|" +
                "\\$|:|\\.|;|\\(|\\)|=|%|[0-9]|:|«|»" +
                "&)";
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
