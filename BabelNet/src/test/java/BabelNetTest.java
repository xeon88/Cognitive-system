import BabelNet.DocumentAnnotator;
import BabelNet.ExtendedLeskAlgorithm;
import BabelNet.WordUsageRetrieve;
import edu.smu.tspell.wordnet.Synset;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import it.uniroma1.lcl.babelnet.BabelNet;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Marco Corona on 30/03/2017.
 */
public class BabelNetTest {


 public static void main(String [] args) {


  System.out.println("directory : " + System.getenv("WNSEARCHDIR") );
  String path = System.getenv("WNSEARCHDIR");
  System.setProperty("wordnet.database.dir", path);
  String logPath = "BabelNet/src/main/resources/log.txt";
  System.setProperty("siscog.babelnet.logpath",logPath);


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

  BestSense("The classification of the genetic data took two years.","classification");
  BestSense("The journal Science published the classification this month.","classification");
  BestSense("His cottage is near a small wood.","wood");
  BestSense("The statue was made out of a block of wood.","wood");

 }




 public static void BestSense(String sentence, String word){

  System.out.println("*-------------TEST--------------*\n");
  System.out.println("Sentence : " + sentence + "\n");
  System.out.println("Word : " + word + "\n");


  try{

     DocumentAnnotator annotator = new DocumentAnnotator(sentence);
     String morpha = annotator.getWordAnnotationByClass(word,CoreAnnotations.LemmaAnnotation.class);
     String pos = annotator.getWordAnnotationByClass(word,CoreAnnotations.PartOfSpeechAnnotation.class);

     // select best sense from all
     Synset s = null;

     ExtendedLeskAlgorithm lesk = new ExtendedLeskAlgorithm();
     WordUsageRetrieve usageRetrieve = new WordUsageRetrieve();
     lesk.getDict().loadWords(usageRetrieve.getSynsets(morpha,annotator.getSynsetType(pos)));
     lesk.getBestSense(morpha,sentence, annotator.getSynsetType(pos));
   }
   catch (IOException ex){
     ex.printStackTrace();
   }

   System.out.println("Lesk algorithm done... + \n");
   System.out.println("=-------------TEST--------------=\n");

 }


}
