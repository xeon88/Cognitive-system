

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;


import edu.stanford.nlp.util.logging.RedwoodConfiguration;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.Synset;
import wordnet.DocumentAnnotator;
import wordnet.LeskAlgorithm;
import wordnet.StopWords;
import wordnet.WordNetUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TreeSet;

/**
 * Created by Marco Corona on 12/05/2017.
 */
public class wordNetTest {


    public static void main(String [] args) {


        File wordnet = new File("WordNet/src/main/resources/WordNet/dict");
        String path = wordnet.getAbsolutePath();
        System.out.println("directory : " + path);
        System.setProperty("wordnet.database.dir", path);
        String logPath = "WordNet/src/main/resources/log.txt";
        System.setProperty("siscog.wordnet.logpath",logPath);

        File file = new File(System.getProperty("siscog.wordnet.logpath"));
        if(file.exists()){
            file.delete();
        }


        // Sentences for testing

        BestSense("Arms bend at the elbow.","arms");
        /*
        BestSense("This table is made of ash wood.","ash");
        BestSense("Germany sells arms to Saudi Arabia.","arms");
        BestSense("Work out the solution in your head.","head");
        BestSense("The house was burnt to ashes while the owner returned.","ashes");
        BestSense("The key broke in the lock.","key");
        BestSense("The key problem was not one of quality but of quantity.","key");
        BestSense("Heat the solution to 75° Celsius.","heat");
        BestSense("The lunch with her boss took longer than she expected.","lunch");
        BestSense("She packed her lunch in her purse.","lunch");
        BestSense("I decided to get the car this week ","get");
        BestSense("The classification of the genetic data took two years.","classification");
        BestSense("The journal Science published the classification this month.","classification");
        BestSense("His cottage is near a small wood.","wood");
        BestSense("The statue was made out of a block of wood.","wood");
        */
    }




    public static void BestSense(String sentence , String search ){

        System.out.println("*-------------TEST--------------*\n");
        System.out.println("Sentence : " + sentence + "\n");
        System.out.println("Word : " + search + "\n");

        try{
            DocumentAnnotator annotator = new DocumentAnnotator();
            Annotation document = annotator.makeAnnotatedDocument(sentence);
            String morpha = annotator.getWordAnnotationByClass(document,search,CoreAnnotations.LemmaAnnotation.class);
            String pos = annotator.getWordAnnotationByClass(document,search,CoreAnnotations.PartOfSpeechAnnotation.class);

            // select best sense from all
            LeskAlgorithm lesk = new LeskAlgorithm();
            WordNetUtils wtr = new WordNetUtils();
            lesk.getDict().loadWords(wtr.getSynsets(morpha,annotator.getPOS(pos)));
            lesk.getBestSense(morpha,sentence,annotator.getPOS(pos));
        }
        catch (IOException ex){
            ex.printStackTrace();
        } catch (JWNLException e) {
            e.printStackTrace();
        }

        System.out.println("Lesk algorithm done... + \n");
        System.out.println("=-------------TEST--------------=\n");

    }


}
