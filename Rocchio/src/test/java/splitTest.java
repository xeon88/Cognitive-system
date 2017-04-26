import Dictionary.DocumentVector;
import Dictionary.DocumentVectorMaker;
import Dictionary.WordDictionary;

import java.io.*;
import java.util.TreeSet;

/**
 * Created by Marco Corona on 05/04/2017.
 */
public class splitTest {

    public static void main(String [] args){

        File file = new File("Rocchio/src/main/resources/docs_100");

        try {
            System.out.println("start to create vectors ....");
            DocumentVectorMaker vectors = new DocumentVectorMaker(file,10);
            vectors.makeAllDocumentVectors();

            makeWordFile(vectors.getWdict().getWords());

            System.out.println("start to create means ....");
            DocumentVectorMaker means = new DocumentVectorMaker(file, 10);
            means.makeAllMeanDocumentVectors();


            System.out.println("testing vectors ....");

            DocumentVector vec1 = vectors.getManager().getVector("ambiente_01");
            DocumentVector vec2 = vectors.getManager().getVector("ambiente_02");
            double similarity = vec1.cosDist(vec2);
            System.out.println("vec1 norm : " + vec1.norm2());
            System.out.println("vec2 norm : " + vec2.norm2());
            System.out.println("similarity between (" + vec1.getLabel() + "," + vec2.getLabel() + ") : " + similarity );

            DocumentVector envirormentMean = means.getManager().getVector("ambiente");
            DocumentVector sportMean = means.getManager().getVector("sport");
            DocumentVector politicsMean = means.getManager().getVector("politica");
            similarity = vec1.cosDist(envirormentMean);
            System.out.println("similarity between (" + vec1.getLabel() + "," + envirormentMean.getLabel() + ") : " + similarity );
            similarity = vec1.cosDist(sportMean);
            System.out.println("similarity between (" + vec1.getLabel() + "," + sportMean.getLabel() + ") : " + similarity );
            similarity = vec1.cosDist(politicsMean);
            System.out.println("similarity between (" + vec1.getLabel() + "," + politicsMean.getLabel() + ") : " + similarity );
        }
        catch (IOException e){

        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }

    }


    public static void makeWordFile(TreeSet<String> words) throws IOException {
        String path = "Rocchio/src/main/resources/output.txt";
        File file = new File(path);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for ( String s: words) {
            writer.write(s + "\n");
        }

        writer.close();
    }
}
