import DepParser.GoldTree;
import DepParser.ProjectiveTree;
import DepParser.Trainer;
import DepParser.UDBankReader;

import java.io.File;

/**
 * Created by Marco Corona on 10/08/2017.
 */
public class UDBankTest {

    public static void main(String [] args){

         System.out.println("Working Directory = " +
              System.getProperty("user.dir"));
        
        File udbank = new File("src/main/resources/dev-test.txt");
        UDBankReader reader = new UDBankReader(udbank);
        Trainer trainer = reader.getTrainer();
        trainer.train();
        for(GoldTree gold : trainer.getGoldTrees().values()){
            gold.printDeps();
            gold.printGoldSeqs();
        }
    }
}

