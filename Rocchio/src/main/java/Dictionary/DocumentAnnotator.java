package Dictionary;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.MorphaAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
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
            this.language = lang;
            initStandfordNlp();
            makeMap();
    }


    private void initStandfordNlp(){
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse");
        RedwoodConfiguration configuration = RedwoodConfiguration.current();
        configuration.empty().capture(System.out).apply();
        StanfordCoreNLP stanPipeline = new StanfordCoreNLP(props);
        this.stanfordPipeline= stanPipeline;
        configuration.restore(System.out).apply();
        System.out.println("standfordNlp initialized \n");
    }



    private Annotation initAnnotatedDocument(String sentence){

        Annotation annotatedSentence = new Annotation(sentence);
        stanfordPipeline.annotate(annotatedSentence);
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
     * Substitute all word in a sentence with a new words composed only
     * by legit characters
     * @param sentence
     * @return
     */

    private String clearNoise(String sentence){

        String [] split = sentence.split(regexSentenceSplit);
        String clear = "";
        for(int i = 0; i<split.length ; i++ ){
            String sub = StringUtilities.getSubStr(split[i]);
            if(sub!=null){
                clear += sub + " " ;
            }
        }

        clear += "\n";

        return clear;
    }


    /**
     *  Make a list of annotated sentences from a text, splitting text itself and filter
     *  useless senteces and nextly calling standford nlp utilities
     * @param text
     * @return
     */


    public Annotation[]  makeAnnotatedSentences(String text){

        ArrayList<Annotation> annotatedSentences = new ArrayList<Annotation>();
        String[] textSplit = text.split(regexTextSplit);
        MorphaAnnotator morphaAnnotator = new MorphaAnnotator();
        for(String sentence : textSplit){
            String [] words = sentence.split(regexSentenceSplit);
            if(words.length>3 && numberOfValidWord(words)>0.9){
                sentence = clearNoise(sentence);
                Annotation document = initAnnotatedDocument(sentence);
                RedwoodConfiguration configuration = RedwoodConfiguration.current();
                configuration.empty().capture(System.out).apply();
                morphaAnnotator.annotate(document);
                configuration.restore(System.out).apply();
                annotatedSentences.add(document);
            }
        }
        Annotation [] annotations = annotatedSentences.toArray(new Annotation[annotatedSentences.size()]);
        return annotations;
    }





    public String [] getAllWordsAnnotationByClass(Annotation document, Class c) throws IOException {


        ArrayList<String> annotations = new ArrayList<String>();
        List<CoreMap> coreMaps = document.get(CoreAnnotations.SentencesAnnotation.class);

        CoreMap [] texts = coreMaps.toArray(new CoreMap[coreMaps.size()]);
        String message = "Method : getAllWordsAnnotationByClass \n" +
                "Class annotation : " + c.getName() + "\n";
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
        String [] annotationStrings = annotations.toArray(new String[annotations.size()]);
        return annotationStrings;
    }


    public String getWordAnnotationByClass(Annotation document,String word,Class c) throws IOException {

        Logging logger = new Logging();

        String annotation = null;
        List<CoreMap> coreMaps = document.get(CoreAnnotations.SentencesAnnotation.class);
        String message = "Method : getAllWordsAnnotationByClass \n" +
                "Class annotation : " + c.getName() + "\n"
                + "Word to annotate : " + word + "\n";

        CoreMap [] texts = coreMaps.toArray(new CoreMap[coreMaps.size()]);

        for(CoreMap text : texts) {
            message+= "Text to read : " + text.toString() +  "\n";
            List<CoreLabel> tokens = text.get(CoreAnnotations.TokensAnnotation.class);
            for (CoreLabel token : tokens) {

                message+= "CoreMap : " + token.originalText() + "\n";
                message+= " index : " + token.index() + "\n";
                message+= " word : " + token.word() + "\n";
                message+= " tag : " + token.tag() + "\n";
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
