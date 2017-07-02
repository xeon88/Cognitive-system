import Dictionary.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Marco Corona on 16/06/2017.
 */
public class MorphItTest {


    public static void main (String [] args){

        String logPath = "Rocchio/src/main/resources/log.txt";
        System.setProperty("siscog.rocchio.logpath",logPath);
        DocumentAnnotator annotator = new DocumentAnnotator(DocumentAnnotator.LANGUAGE_EN);
        try {
            File file = new File("Rocchio/src/main/resources/docs_400/comp_windows_x_0005003.txt");
            String text = FileUtilities.getTextFromFile(file);
            ArrayList<Annotation> ann = annotator.makeAnnotatedSentences(text);

            for(int i= 0; i<ann.size() ; i++){
                System.out.println("annotation : " + ann.get(i).toString());
                ArrayList<String> lemmas = annotator.getAllWordsAnnotationByClass(ann.get(i), CoreAnnotations.LemmaAnnotation.class);
                for (String lemma : lemmas){
                    System.out.println("lemma :" + lemma);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
