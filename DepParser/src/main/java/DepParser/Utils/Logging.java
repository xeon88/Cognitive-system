package DepParser.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Marco Corona on 07/06/2017.
 * Log services with different levels of verbosity
 */
public class Logging {

    private File log;
    private HashMap<String,Boolean> config ;
    private HashMap<String,String> headers;
    public final static String DEBUG = "debug";
    public final static String INFO = "info";
    public final static String ERROR = "error";

    public Logging(){
        log = new File(System.getProperty("siscog.parser.logpath"));
        config = new HashMap<String, Boolean>();

        // default
        config.put("info", true);
        config.put("debug",true);
        config.put("error",true);

        headers = new HashMap<String,String>();
        headers.put("info","[INFO] ");
        headers.put("debug","[DEBUG] ");
        headers.put("error","[ERROR] ");
    }

    public Logging(HashMap<String,Boolean> config){
        log = new File(System.getProperty("siscog.rocchio.logpath"));
        this.config = config;
        headers = new HashMap<String,String>();
        headers.put("info","[INFO] ");
        headers.put("debug","[DEBUG] ");
        headers.put("error","[ERROR] ");
    }

    private String formatMessage(String message, String type){
        String formatted= "";
        if(config.containsKey(type)){
            formatted =  config.get(type) + message + "\n";
        }
        return formatted;
    }



    public void log(String message, String type) throws IOException {

        if(message.equals("")) return;

        if(config.containsKey(type) && config.get(type)){
            formatMessage(message,type);
            FileWriter writer = new FileWriter(log,true);
            writer.write(message + "\n");
            writer.close();
        }
    }

}
