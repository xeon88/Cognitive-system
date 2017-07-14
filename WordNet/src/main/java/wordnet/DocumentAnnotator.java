package wordnet;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.MorphaAnnotator;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;
import net.didion.jwnl.data.POS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Created by Marco Corona on 07/06/2017.
 */
public class DocumentAnnotator {

    private StanfordCoreNLP pipeline;
    private HashMap<String,POS> map;



    public DocumentAnnotator(){
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        this.pipeline= pipeline;
        RedwoodConfiguration.current().clear().apply();
        makeMap();
    }




    public Annotation makeAnnotatedDocument(String sentence){
        Annotation document = new Annotation(sentence);
        pipeline.annotate(document);

        RedwoodConfiguration configuration = RedwoodConfiguration.current();
        configuration.empty().capture(System.err).apply();
        MorphaAnnotator morphaAnnotator = new MorphaAnnotator();
        morphaAnnotator.annotate(document);

        POSTaggerAnnotator posTagger = new POSTaggerAnnotator();
        posTagger.annotate(document);
        configuration.clear().apply();
        return document;
    }



    public ArrayList<String> getAllWordsAnnotationByClass(Annotation document, Class c) throws IOException {

        Logging logger = new Logging();

        ArrayList<String> annotation = new ArrayList<String>();
        List<CoreMap> texts = document.get(CoreAnnotations.SentencesAnnotation.class);
        String message = "Class annotation : " + c.getName() + "\n";

        for(CoreMap text : texts) {
            message+= "Text to read : " + text.toString() +  "\n";
            for (CoreLabel token : text.get(CoreAnnotations.TokensAnnotation.class)) {
                String elem = token.get(CoreAnnotations.TextAnnotation.class);
                String ann = (String)token.get(c);
                annotation.add(ann);
                message += "Token to annotate : " + elem + "\n"
                        + "Annotation : " + annotation + "\n";
            }
        }

        logger.log(message, "debug");
        return annotation;
    }


    public String getWordAnnotationByClass(Annotation document,String word,Class c) throws IOException {

        Logging logger = new Logging();

        String annotation = null;
        List<CoreMap> texts = document.get(CoreAnnotations.SentencesAnnotation.class);
        String message = "Class annotation : " + c.getName() + "\n";

        for(CoreMap text : texts) {
            message+= "Text to read : " + text.toString() +  "\n";
            for (CoreLabel token : text.get(CoreAnnotations.TokensAnnotation.class)) {
                String elem = token.get(CoreAnnotations.TextAnnotation.class);
                annotation = (String)token.get(c);
                if(elem.equalsIgnoreCase(word)){
                    message += "Token to annotate : " + elem + "\n"
                            + "Annotation : " + annotation + "\n";
                    break;
                }
            }
        }

        message += "Word to annotate : " + word + "\n"
                + "Annotation : " + annotation + "\n";

        logger.log(message, "debug");
        return annotation;
    }




    public POS getPOS(String annotation){
        return map.get(annotation);
    }


    private void makeMap(){

        map= new HashMap<String, POS>();
        map.put("NN", POS.NOUN);
        map.put("NNS", POS.NOUN);
        map.put("NNP", POS.NOUN);
        map.put("NNPS", POS.NOUN);
        map.put("VB",POS.VERB);
        map.put("VBD",POS.VERB);
        map.put("VBG",POS.VERB);
        map.put("VBN",POS.VERB);
        map.put("VBP",POS.VERB);
        map.put("VBZ",POS.VERB);
        map.put("RBR",POS.ADVERB);
        map.put("RB",POS.ADVERB);
        map.put("RBS",POS.ADVERB);
        map.put("JJR",POS.ADJECTIVE);
        map.put("JJ",POS.ADJECTIVE);
        map.put("JJS",POS.ADJECTIVE);
    }


}
