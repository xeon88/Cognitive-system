import Dictionary.FileUtilities;

import java.io.File;
import java.io.IOException;

/**
 * Created by Marco Corona on 05/07/2017.
 */
public class TextCreation {

    public  static void main(String [] args){

        try {
            File convert = new File("Rocchio/src/main/resources/to_convert");
            FileUtilities.CreateNewDirectory(convert);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
