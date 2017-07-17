import Dictionary.*;

import javax.print.Doc;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by Marco Corona on 05/04/2017.
 * Class to test Rocchio based classifier
 */
public class RocchioMethodTest {

    public static void main(String [] args){
        try {

            // global configuration

            String logPath = "Rocchio/src/main/resources/log.txt";
            System.setProperty("siscog.rocchio.logpath",logPath);
            String pathMorphIt = "Rocchio/src/main/resources/morph-it/morph-it/current_version/morph-it_048.txt";
            System.setProperty("siscog.rocchio.morphit",pathMorphIt);
            File file = new File(System.getProperty("siscog.rocchio.logpath"));
            if(file.exists()){
                file.delete();
            }

            // fetching directories with documents

            String choose ="docs_400" ;
            File trainingSet = new File("Rocchio/src/main/resources/" + choose);
            File testSet = new File("Rocchio/src/main/resources/testset_" + choose);
            File [] documents = testSet.listFiles();

            // parameters of exectution

            double beta =16; // slope of positive class
            double gamma =2; // slope of negative class
            int [] samplesAmounts = new int[]{500};  // sampling amount to testing classifier
            int trials = 1; // number of execution per amount sampling type


            double meanAccuracy;
            Logging log = new Logging();
            String language = DocumentAnnotator.LANGUAGE_EN;
            String message = "[TEST] \n";

            for(int i = 0; i<samplesAmounts.length; i++){

                System.out.println("start to create vectors ....");
                meanAccuracy = 0;
                for(int j=0;j<trials;j++){
                    System.out.println("Test with " + samplesAmounts[i] + " samples number  " + (j+1));
                    RocchioClassifier classifier = new RocchioClassifier(trainingSet,beta,gamma,language, samplesAmounts[i]);
                    double acc = test(documents,classifier);
                    System.out.println("Accuracy : " + acc);
                    meanAccuracy += acc;
                    classifier.getBuilder().clear();
                }
                meanAccuracy = meanAccuracy/(double)trials;
                System.out.println("Mean accuracy with " + samplesAmounts[i] + " documents is " + meanAccuracy);
                message += "Samples : " + samplesAmounts[i] + "  mean accuracy : " + meanAccuracy + "\n";
            }
            log.log(message,"info");
        }
        catch (IOException e){

        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }

    }




    public static double test(File[] testSet,RocchioClassifier classifier) throws IOException {



        System.out.println("Similarity ....." );
        System.out.println("testing vectors ....");


        int countSuccess = 0;
        int testSetSize = testSet.length;

        for(File document : testSet){
            boolean test = classifier.classify(document);
            if(test){ countSuccess++;}
        }


        Logging log = new Logging();


        String message = "";

        /*
        for(Centroid centroid : classifier.getBuilder().getManager().getCentroids().values()){

            System.out.println("Nearest of centroid : " + centroid.getLabel() + " is " +
                    centroid.getNearest().getLabel());
            ArrayList<Feature> best = classifier.getBuilder().getWdict().getBeastFeatureForCategory(centroid.getLabel(),10);

            for(Feature feature : best){
                message += "Centroid : " + centroid.getLabel() + "\n\n";
                message += "Feature word : " + feature.getWord() + "\n";
                message += "Feature category occurencies : " + feature.getOccurenciesByLabel(centroid.getLabel()) + "\n";
                message +="Feature total occurencies : " + feature.getOccurencies() + "\n\n";

            }

        }
        */

        log.log(message,"info");
        double accuracy = ((double)countSuccess)/((double) testSetSize);
        /*
        String result = "Result : \n"

                + "Training set size : " + samples*classifier.getBuilder().getManager().getCentroids().size() + "\n"
                + "Test set size : " + documents.length + "\n"
                + "Successes : " + countSuccess + "\n"
                + "Accuracy : " + accuracy + "\n"
                + "Beta : " + beta + "\n"
                + "Gamma : " + gamma + "\n";

        System.out.println(result);
        */
        return accuracy;
    }


}
