package BabelNet;

import edu.smu.tspell.wordnet.SynsetType;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.MorphaAnnotator;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Created by Marco Corona on 07/06/2017.
 */
public class DocumentAnnotator {

    private StanfordCoreNLP pipeline;
    private HashMap<String,SynsetType> map;
    private Annotation document;



    public DocumentAnnotator(String sentence){
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        pipeline = new StanfordCoreNLP(props);
        makeMap();
        makeAnnotatedDocument(sentence);
    }




    public void makeAnnotatedDocument(String sentence){

        document = new Annotation(sentence);
        pipeline.annotate(document);

        MorphaAnnotator morphaAnnotator = new MorphaAnnotator();
        morphaAnnotator.annotate(document);

        POSTaggerAnnotator posTagger = new POSTaggerAnnotator();
        posTagger.annotate(document);
    }



    public String getWordAnnotationByClass(String word,Class c) throws IOException {

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




    public SynsetType getSynsetType(String annotation){
        return map.get(annotation);
    }


    private void makeMap(){

        map= new HashMap<String, SynsetType>();
        map.put("NN", SynsetType.NOUN);
        map.put("NNS", SynsetType.NOUN);
        map.put("NNP", SynsetType.NOUN);
        map.put("NNPS", SynsetType.NOUN);
        map.put("VB",SynsetType.VERB);
        map.put("VBD",SynsetType.VERB);
        map.put("VBG",SynsetType.VERB);
        map.put("VBN",SynsetType.VERB);
        map.put("VBP",SynsetType.VERB);
        map.put("VBZ",SynsetType.VERB);
        map.put("RBR",SynsetType.ADVERB);
        map.put("RB",SynsetType.ADVERB);
        map.put("RBS",SynsetType.ADVERB);
        map.put("JJR",SynsetType.ADJECTIVE);
        map.put("JJ",SynsetType.ADJECTIVE);
        map.put("JJS",SynsetType.ADJECTIVE);
    }


}
