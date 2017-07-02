package Dictionary;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.MorphaAnnotator;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.logging.Redwood;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;
import eu.fbk.dh.tint.runner.TintPipeline;
import net.didion.jwnl.data.POS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;



/**
 * Created by Marco Corona on 07/06/2017.
 * This class provides all functions to execute text splitting, parsing and lemmatizazion
 * It implementes all methods through the use of libraries standford NLP , Tint.
 */


public class DocumentAnnotator {

    private StanfordCoreNLP stanfordPipeline;
    private TintPipeline tintPipeline;
    private HashMap<String,POS> map;
    public static final String LANGUAGE_IT = "IT";
    public static final String LANGUAGE_EN = "EN";
    private String language;
    private String regexTextSplit = "((\\.|\\?|!|;|,)|" +
            "((\\[|\\(|\\{))|" +
            "((\\[|\\{|\\)))|" +
            "(\\t\\t)+(\\s)*)";


    private String regexSentenceSplit = "(\\s|\\t|\\n)+";

    public DocumentAnnotator(String lang) {
        try {
            this.language = lang;
            if(lang.equals(LANGUAGE_IT)){
                initTint();
            }
            else if(lang.equals(LANGUAGE_EN)){
                initStandfordNlp();
            }
            else{
                throw  new IllegalArgumentException("Language not supported");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        makeMap();
    }


    private void initStandfordNlp(){
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse");
        RedwoodConfiguration configuration = RedwoodConfiguration.current();
        configuration.empty().capture(System.out).apply();
        StanfordCoreNLP stanPipeline = new StanfordCoreNLP(props);
        this.stanfordPipeline= stanPipeline;
        System.out.println("standfordNlp initialized \n");
    }

    private void initTint() throws IOException {
        TintPipeline tintPipeline = new TintPipeline();
        tintPipeline.loadDefaultProperties();
        tintPipeline.load();
        this.tintPipeline= tintPipeline;
    }




    private Annotation initAnnotatedDocument(String sentence){

        Annotation annotatedSentence = new Annotation(sentence);
        if(language.equals(LANGUAGE_IT)){
            annotatedSentence = tintPipeline.runRaw(sentence);
        }

        if(language.equals(LANGUAGE_EN)){
            stanfordPipeline.annotate(annotatedSentence);
        }
        return annotatedSentence;
    }


    private double numberOfValidWord(String [] sentenceSplit){

        double ratio = 0;
        int count = 0;
        for(String word : sentenceSplit){
            if(StringUtilities.checkWord(word)){
                count++;
            }
        }
        ratio = ((double)count/(double) sentenceSplit.length);
        return ratio;
    }


    /**
     *  Make a list of annotated sentences from a text, splitting text itself
     *  and nextly calling standford nlp utilities
     * @param text
     * @return
     */


    public ArrayList<Annotation> makeAnnotatedSentences(String text){

        ArrayList<Annotation> annotatedSentences = new ArrayList<Annotation>();
        String[] textSplit = text.split(regexTextSplit);

        for(String sentence : textSplit){
            String [] words = sentence.split(regexSentenceSplit);
            if(words.length>3 && numberOfValidWord(words)>0.9){
                Annotation document = initAnnotatedDocument(sentence);
                RedwoodConfiguration configuration = RedwoodConfiguration.current();
                configuration.empty().capture(System.out).apply();
                MorphaAnnotator morphaAnnotator = new MorphaAnnotator();
                morphaAnnotator.annotate(document);
                configuration.clear().apply();
                annotatedSentences.add(document);
                // System.out.println("sentence annotated " + sentence);
            }
        }
        return annotatedSentences;
    }





    public ArrayList<String> getAllWordsAnnotationByClass(Annotation document, Class c) throws IOException {

        Logging logger = new Logging();
        ArrayList<String> annotations = new ArrayList<String>();
        List<CoreMap> texts = document.get(CoreAnnotations.SentencesAnnotation.class);
        String message = "Class annotation : " + c.getName() + "\n";
        for(CoreMap text : texts) {
            message+= "Text to read : " + text.toString() +  "\n";
            for (CoreLabel token : text.get(CoreAnnotations.TokensAnnotation.class)) {
                String elem = token.get(CoreAnnotations.TextAnnotation.class);
                String ann = (String)token.get(c);
                annotations.add(ann);
                message += "Token to annotate : " + elem + "\n"
                        + "Annotation : " + annotations + "\n";
            }
        }
        logger.log(message, "debug");
        return annotations;
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
