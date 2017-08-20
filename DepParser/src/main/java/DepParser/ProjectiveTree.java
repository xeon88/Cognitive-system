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
                deps += dependencies[i].toString();
            }
        }
        System.out.println(deps);
    }

}
