package Dictionary;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Marco Corona on 07/06/2017.
 */
public class Logging {

    private File log;
    private HashMap<String,Boolean> config ;
    private HashMap<String,String> headers;

    public Logging(){
        log = new File(System.getProperty("siscog.rocchio.logpath"));
        config = new HashMap<String, Boolean>();

        // default
        config.put("info", true);
        config.put("debug",true);

        headers = new HashMap<String,String>();
        headers.put("info","[INFO] ");
        headers.put("debug","[DEBUG] ");
    }

    public Logging(HashMap<String,Boolean> config){
        log = new File(System.getProperty("siscog.rocchio.logpath"));
        this.config = config;
        headers = new HashMap<String,String>();
        headers.put("info","[INFO] ");
        headers.put("debug","[DEBUG] ");
    }

    private String formatMessage(String message, String type){
        String formatted= "";
        if(config.containsKey(type)){
            formatted =  config.get(type) + message + "\n";
        }
        return formatted;
    }

    public void log(String message, String type) throws IOException {

        if(config.get(type)){
            formatMessage(message,type);
            FileWriter writer = new FileWriter(log,true);
            writer.write(message + "\n");
            writer.close();
        }
    }


}
