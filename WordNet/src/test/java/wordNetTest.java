import edu.mit.jwi.IDictionary;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.MorphaAnnotator;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import edu.stanford.nlp.util.CoreMap;
import wordnet.DocumentAnnotator;
import wordnet.LeskAlgorithm;
import wordnet.SynsetRetrieve;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Created by Marco Corona on 12/05/2017.
 */
public class wordNetTest {

    private static SynsetRetrieve sr ;

    public static void main(String [] args) {


        System.out.println("directory : " + System.getenv("WNSEARCHDIR") );
        String path = System.getenv("WNSEARCHDIR");
        System.setProperty("wordnet.database.dir", path);
        String logPath = "WordNet/src/main/resources/log.txt";
        System.setProperty("siscog.wordnet.logpath",logPath);
        sr = new SynsetRetrieve(path);
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        String typeList = "Type List : \n"
                +"Adjective : "+ SynsetType.ADJECTIVE.getCode() + "\n"
                + "Adjective satellite : " + SynsetType.ADJECTIVE_SATELLITE.getCode() + "\n"
                + "Adverb : " + SynsetType.ADVERB.getCode() + "\n"
                + "Noun : " + SynsetType.NOUN.getCode() + "\n"
                + "Verb : " + SynsetType.VERB.getCode() + "\n";

        System.out.println(typeList);

        // Sentences for testing
        /*

        BestSense("Arms bend at the elbow.","arms",pipeline);
        BestSense("Germany sells arms to Saudi Arabia.","arms",pipeline);
        BestSense("The key broke in the lock.","key",pipeline);
        BestSense("The key problem was not one of quality but of quantity.","key",pipeline);
        BestSense("Work out the solution in your head.","head",pipeline);
        BestSense("Heat the solution to 75° Celsius.","heat",pipeline);
        BestSense("The house was burnt to ashes while the owner returned.","ashes",pipeline);
        BestSense("This table is made of ash wood.","ash",pipeline);
        BestSense("The lunch with her boss took longer than she expected.","lunch",pipeline);
        BestSense("She packed her lunch in her purse.","lunch",pipeline);
        */

        BestSense("The classification of the genetic data took two years.","classification",pipeline);
        BestSense("The journal Science published the classification this month.","classification",pipeline);
        BestSense("His cottage is near a small wood.","wood",pipeline);
        BestSense("The statue was made out of a block of wood.","wood",pipeline);

    }




    public static void BestSense(String sentence , String word , StanfordCoreNLP pipe){

        System.out.println("*-------------TEST--------------*\n");
        System.out.println("Sentence : " + sentence + "\n");
        System.out.println("Word : " + word + "\n");


        try{

            DocumentAnnotator annotator = new DocumentAnnotator(pipe,sentence);
            String morpha = annotator.getWordAnnotationByClass(word,CoreAnnotations.LemmaAnnotation.class);
            String pos = annotator.getWordAnnotationByClass(word,CoreAnnotations.PartOfSpeechAnnotation.class);

            // select best sense from all
            Synset s = null;

            LeskAlgorithm lesk = new LeskAlgorithm(sr);
            lesk.getDict().loadWords(lesk.getSr().getSynsets(morpha,annotator.getSynsetType(pos)));
            lesk.getBestSense(morpha,sentence, annotator.getSynsetType(pos));
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        System.out.println("Lesk algorithm done... + \n");
        System.out.println("=-------------TEST--------------=\n");

    }


}
