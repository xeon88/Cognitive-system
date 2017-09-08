import DepParser.Model.ProjectiveTree;
import DepParser.Parser.ArcEager.EagerParser;
import DepParser.Parser.ArcEager.EagerTrainer;
import DepParser.Parser.Mazzei.StandardParser;
import DepParser.Parser.Mazzei.StandardTrainer;
import DepParser.Parser.Sentence;
import DepParser.Parser.Tester;
import DepParser.Utils.Logging;
import DepParser.Utils.PrintUltis;
import DepParser.Utils.UDBankReader;

import java.io.File;
import java.io.IOException;

/**
 * Created by Marco Corona on 10/08/2017.
 */
public class Main {

    public static void main(String [] args){

        String logPath = "src/main/resources/log.txt";
        System.setProperty("siscog.parser.logpath",logPath);
        File file = new File(System.getProperty("siscog.parser.logpath"));
        if(file.exists()){
            file.delete();
        }


         System.out.println("Working Directory = " +
              System.getProperty("user.dir"));

        StringBuilder builder = new StringBuilder();
        Logging log = new Logging();

        File train = new File("src/main/resources/train-proj.txt");
        File test = new File("src/main/resources/test-proj.txt");

        UDBankReader trainReader = new UDBankReader(train);

        EagerTrainer trainer = trainReader.getTrainer();
        EagerParser parser = new EagerParser(trainer.getClassifier());

        UDBankReader testReader = new UDBankReader(test, parser);
        Tester tester = testReader.getTester();

        Sentence [] sentences = testReader.getSentences();

        double accuracy = 0;
        double mean = 0;
        int tests = 100;
        for(int i=0; i<tests ; i++){
            int random = (int)Math.floor(Math.random()*testReader.getSentences().length);
            ProjectiveTree found= parser.parse(sentences[random]);
            accuracy = tester.getAccuracy(random,found.getDependencies());
            builder.append("Accuracy of parser for sentence " +  random + " is " + accuracy + "\n" );
            mean += accuracy;

            builder.append("Seqs found \n");
            builder.append(PrintUltis.toString(found.getDependencies()) + "\n");
            builder.append("Gold deps \n");
            ProjectiveTree gold = tester.getGoldTrees().get(random);
            builder.append(PrintUltis.toString(found.getDependencies()) + "\n");

        }

        mean = mean/(double)tests;

        builder.append("Mean accuracy obtained is " + mean + "\n");
        try {
            log.log(builder.toString(),Logging.DEBUG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

