package DepParser;

import java.util.ArrayList;

/**
 * Created by Marco Corona on 03/08/2017.
 */
public class ProjectiveTree {

    private Dependency [] dependencies;

    public ProjectiveTree(){
    }

    public void setDependencies(Dependency [] deps){
        this.dependencies = deps;
    }

    public Dependency[] getDependencies() {
        return dependencies;
    }



    public void printDeps(){

        System.out.println("Deps List : \n\n");
        String deps = "";
        if(dependencies!=null){
            for(int i = 0; i<dependencies.length; i++){
                deps+= "(head," + dependencies[i].getHead().getIndex() +
                        " " + dependencies[i].getHead().getValue("form") + ")" +
                        "(dep," + dependencies[i].getDependent().getIndex() +
                        " " + dependencies[i].getDependent().getValue("form") + ")" + "\n";
            }
        }
        System.out.println(deps);
    }

}
