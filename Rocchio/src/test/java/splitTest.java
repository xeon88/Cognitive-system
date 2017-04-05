import Dictionary.WordDictionary;

import java.io.*;
import java.util.TreeSet;

/**
 * Created by Marco Corona on 05/04/2017.
 */
public class splitTest {

    public static void main(String [] args){

        File file = new File("Rocchio/src/main/resources/docs_100");
        WordDictionary dict = new WordDictionary(file);
        try {
            dict.loadWords();
            TreeSet<String> wordSet = dict.getWords();
            File output = new File ("Rocchio/src/main/resources/output.txt");
            FileWriter writer = new FileWriter(output);
            BufferedWriter buffwriter = new BufferedWriter(writer);
            for(String word : wordSet){
                buffwriter.write(word + "\n");
            }
            buffwriter.close();
        }
        catch (IOException e){

        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }

    }


}
