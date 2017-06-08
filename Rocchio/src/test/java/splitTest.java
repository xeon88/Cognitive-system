import Dictionary.Centroid;
import Dictionary.DocumentVector;
import Dictionary.DocumentVectorBuilder;
import Dictionary.FileUtilities;

import java.io.*;

/**
 * Created by Marco Corona on 05/04/2017.
 */
public class splitTest {

    public static void main(String [] args){

        File file = new File("Rocchio/src/main/resources/docs_100");

        try {

            System.out.println("start to create vectors ....");
            DocumentVectorBuilder vectorMaker = new DocumentVectorBuilder(file,10);
            vectorMaker.makeAllDocumentVectors();
            vectorMaker.makeAllMeanDocumentVectors();
            //vectorMaker.getWdict().makeDictonaryJson();
            System.out.println("testing vectors ....");


            DocumentVector sportTest = vectorMaker.createTextVector(new File("Rocchio/src/main/resources/ubi.txt"));
            System.out.println("Test norm : " + sportTest.norm2());

            DocumentVector euclideanCategory = vectorMaker.getManager().MostNearCategory(sportTest);
            DocumentVector similarityCategory = vectorMaker.getManager().MostLikelihoodCategory(sportTest);

            System.out.println("test belong to : " + euclideanCategory.getLabel());
            System.out.println("test belong to : " + similarityCategory.getLabel());

        }
        catch (IOException e){

        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }

    }

}
