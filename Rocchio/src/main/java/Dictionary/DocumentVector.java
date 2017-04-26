package Dictionary;

import java.util.TreeMap;


/**
 * Created by Marco Corona on 06/04/2017.
 */


public class DocumentVector {

    private  int [] occurences;
    private String label;
    private TreeMap<String,Term> map;


    private class Term implements Comparable<Term>{

        protected int occurencies;
        protected String name;

        public int compareTo(Term term) {
            return name.compareTo(term.name);
        }


        public int getOccuriences(){
            return occurencies;
        }
        public String getName(){
            return name;

        }

        public void setName(String name){
            this.name = name;
        }

        public void incrementsOcc(int occs){
            this.occurencies+=occs;
        }

        protected Term(String name, int initOcc){
            this.name = name;
            this.occurencies = initOcc;
        }
    }


    public TreeMap<String, Term> getMap() {
        return map;
    }


    public void setOccurences(int[] occurences) {
        this.occurences = occurences;
    }

    public void setMap(TreeMap<String, Term> map) {
        this.map = map;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    public DocumentVector(String label, int size){
        this.label = label;
        map = new TreeMap<String,Term>();
        this.occurences = new int[size];
    }

    public String getLabel() {
        return label;
    }

    public int[] getOccurences() {
        return occurences;
    }

    public TreeMap<String,Term> getInfo() {
        return map;
    }

    public void incrementsOcc(String name, int occ){
        if(map.containsKey(name)) {
            map.get(name).incrementsOcc(occ);
        }
        else{
            map.put(name,new Term(name, occ));
        }
    }


    public void makeArrayForDataVector(){
        int i = 0;
        for (Term t : map.values()){
            occurences[i]=t.occurencies;
            i++;
        }
    }


    public int[] makeArrayForCentroidVector(int size){
        int [] mean = new int [occurences.length];
        int i = 0;
        for (Term t : map.values()){
            occurences[i]=t.occurencies;
            i++;
        }

        for (i = 0; i<occurences.length ; i++){
            mean[i]=occurences[i]/size;
        }
        return mean;
    }



    public double normInf(){
        double norm = Double.MIN_VALUE;
        for(int i = 0 ; i<occurences.length ; i++){
            if(norm<occurences[i]){
                norm = (double) occurences[i];
            }
        }
        return norm;
    }


    public double norm1(){
        double norm = 0;
        for(int i = 0 ; i<occurences.length ; i++){
            norm+= occurences[i];
        }
        return norm;
    }



    public double norm2(){
        double norm = 0;
        for(int i = 0 ; i<occurences.length ; i++){
            norm+= Math.pow(occurences[i],2);
        }
        return Math.sqrt(norm);
    }



    public double cosDist (DocumentVector t1){
        int [] vector = t1.getOccurences();
        double cosineDistance = 0;
        double dotProduct = 0;
        for (int i = 0; i<occurences.length ; i++){
            double product = (double) vector[i]*(double)occurences[i];
            dotProduct += product;
        }
        double NormsProduct = norm2()*t1.norm2();
        cosineDistance = dotProduct/NormsProduct;
        return cosineDistance;
    }

    public void printOccVectors(int num){
        int end =  occurences.length > num ? num : occurences.length;
        for(int i = 0 ; i<end; i++){
            System.out.print("[" + occurences[i] + "]");
        }
    }
}
