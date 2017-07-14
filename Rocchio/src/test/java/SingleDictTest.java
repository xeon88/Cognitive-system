import Dictionary.DocumentAnnotator;
import Dictionary.RocchioClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.SentenceAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Created by Marco Corona on 05/07/2017.
 */
public class SingleDictTest {

    public  static void main( String [] args){


        String logPath = "Rocchio/src/main/resources/log.txt";
        System.setProperty("siscog.rocchio.logpath",logPath);
        String pathMorphIt = "Rocchio/src/main/resources/morph-it/morph-it/current_version/morph-it_048.txt";
        System.setProperty("siscog.rocchio.morphit",pathMorphIt);
        File file = new File(System.getProperty("siscog.rocchio.logpath"));
        if(file.exists()){
            file.delete();
        }

        /*

        System.out.println("start to create vectors ....");

        double beta =16;
        double gamma = 4;


        String choose ="docs_400" ;
        File trainingSet = new File("Rocchio/src/main/resources/" + choose + "/comp_graphics_0001001.txt");
        String language = DocumentAnnotator.LANGUAGE_EN;
        try {
            RocchioClassifier classifier = new RocchioClassifier(trainingSet,beta,gamma,language,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse");
        RedwoodConfiguration configuration = RedwoodConfiguration.current();
        configuration.empty().capture(System.out).apply();
        StanfordCoreNLP stanPipeline = new StanfordCoreNLP(props);
        System.out.println("standfordNlp initialized \n");

        Sentence test = new Sentence("I could give much the same testimonial about my experiences as a scout\n" +
                "back in the 1960s");

        configuration.restore(System.out).apply();
        List<String> lemmas = test.lemmas();
        String message = "";
        for(String lemma : lemmas){
            message += lemma + " ";
        }
        System.out.println(message);
    }
}
