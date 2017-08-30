package DepParser.Utils;

import DepParser.Model.Dependency;
import DepParser.Model.Token;

import java.util.Collection;

/**
 * Created by Marco Corona on 24/08/2017.
 */
public class PrintUltis {


    public static String toString(double [] vect){

        String out = "";
        for(int i = 0;i<vect.length ; i++){
            out += "[" + vect[i] + "]";
        }
        out+="\n";
        return out;
    }


    public static String toString(float [] vect){

        String out = "";
        for(int i = 0;i<vect.length ; i++){
            out += "[" + vect[i] + "]";
        }
        out+="\n";
        return out;
    }


    public static String toString(String [] vect){

        String out = "";
        for(int i = 0;i<vect.length ; i++){
            out += "[" + vect[i] + "]";
        }
        out+="\n";
        return out;
    }


    public static String toString(float[][] matrix){

        StringBuilder builder = new StringBuilder();
        for(int i = 0; i<matrix.length ; i++){
            for(int j = 0; j<matrix[i].length ; j++){
                builder.append("[" + matrix[i][j] + "]");
            }
            builder.append("\n");
        }

        return builder.toString();
    }


    public static String toString(double[][] matrix){

        StringBuilder builder = new StringBuilder();
        for(int i = 0; i<matrix.length ; i++){
            for(int j = 0; j<matrix[i].length ; j++){
                builder.append("[" + matrix[i][j] + "] ");
            }
            builder.append("\n");
        }

        return builder.toString();
    }




    public static String toString(Dependency [] arcs){

        StringBuilder builder = new StringBuilder();
        for(int i = 0; i<arcs.length ; i++){
            builder.append("[" + arcs[i] + "]\n");
        }

        return builder.toString();
    }

    public static String toString(Dependency [] arcs,int lastpos){

        StringBuilder builder = new StringBuilder();
        for(int i = 0; i<lastpos ; i++){
            builder.append("[" + arcs[i] + "]\n");
        }

        return builder.toString();
    }


    public static <C extends Collection<Token>> String toString(C collection){
        StringBuilder builder = new StringBuilder();
        for (Token token : collection) {
            builder.append(token + "\n");
        }

        return builder.toString();
    }



}
