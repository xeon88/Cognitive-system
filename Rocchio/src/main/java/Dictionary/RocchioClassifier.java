package Dictionary;

import java.io.File;
import java.io.IOException;

/**
 * Created by Marco Corona on 19/06/2017.
 */
public class RocchioClassifier {

    private DocumentVectorBuilder builder;

    public RocchioClassifier(File trainingSet,double beta, double gamma, String lang) throws IOException {
        this.builder = new DocumentVectorBuilder(trainingSet,lang);
        builder.makeAllDocumentVectors();
        builder.makeAllMeanDocumentVectors(beta,gamma);
    }

    public RocchioClassifier(File trainingSet, String lang) throws IOException {
        this.builder = new DocumentVectorBuilder(trainingSet, lang);
        builder.makeAllDocumentVectors();
        builder.makeAllMeanDocumentVectors(1,0);
    }



    public DocumentVectorBuilder getBuilder() {
        return builder;
    }



    public boolean classify(File input) throws IOException {

        DocumentVector docTest = builder.createTextVector(input);

        String realCategory = FileUtilities.getCategoryfromLabel(docTest.getLabel());
        DocumentVector similarityCategory = builder.getManager().MostLikelihoodCategory(docTest);

        System.out.println("[Real category] : " + realCategory);
        System.out.println("[Similarity] test belong to : " + similarityCategory.getLabel() + "\n\n");
        return realCategory.equals(similarityCategory.getLabel());
    }



}
