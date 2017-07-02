import Dictionary.*;

import javax.print.Doc;
import java.io.*;

/**
 * Created by Marco Corona on 05/04/2017.
 */
public class RocchioMethodTest {

    public static void main(String [] args){
        try {

            String logPath = "Rocchio/src/main/resources/log.txt";
            System.setProperty("siscog.rocchio.logpath",logPath);
            String pathMorphIt = "Rocchio/src/main/resources/morph-it/morph-it/current_version/morph-it_048.txt";
            System.setProperty("siscog.rocchio.morphit",pathMorphIt);
            File file = new File(System.getProperty("siscog.rocchio.logpath"));
            if(file.exists()){
                file.delete();
            }

            System.out.println("start to create vectors ....");

            double beta =16;
            double gamma = 4;

            String choose ="docs_400" ;
            File trainingSet = new File("Rocchio/src/main/resources/" + choose);
            String language = DocumentAnnotator.LANGUAGE_EN;
            RocchioClassifier classifier = new RocchioClassifier(trainingSet,language);


            System.out.println("Similarity ....." );


            System.out.println("testing vectors ....");

            File testSet = new File("Rocchio/src/main/resources/testset_" + choose);
            File [] documents = testSet.listFiles();

            int countSuccess = 0;
            int testSetSize = documents.length;

            for(File document : documents){
                boolean test = classifier.classify(document);
                if(test){ countSuccess++;}
            }

            for(Centroid centroid : classifier.getBuilder().getManager().getCentroids().values()){
                System.out.println("Nearest of centroid : " + centroid.getLabel() + " is " +
                centroid.getNearest().getLabel());
            }

            double accuracy = ((double)countSuccess)/((double) testSetSize);
            String result = "Result : \n"

                            + "Training set size : " + trainingSet.listFiles().length + "\n"
                            + "Test set size : " + documents.length + "\n"
                            + "Successes : " + countSuccess + "\n"
                            + "Accuracy : " + accuracy + "\n"
                            + "Beta : " + beta + "\n"
                            + "Gamma : " + gamma + "\n";


            System.out.println(result);


        }
        catch (IOException e){

        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }

    }




}
