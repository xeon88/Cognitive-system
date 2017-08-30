package DepParser.Model;

/**
 * Created by Marco Corona on 03/08/2017.
 */
public class ProjectiveTree {

    private Dependency[] dependencies;

    public ProjectiveTree(){
    }

    public void setDependencies(Dependency [] deps){
        this.dependencies = deps;
    }

    public Dependency[] getDependencies() {
        return dependencies;
    }



    public void printDeps(){

        System.out.println("Deps List : \n");
        String deps = "";
        if(dependencies!=null){
            for(int i = 0; i<dependencies.length; i++){
                if(dependencies[i]!=null){
                    deps += dependencies[i].toString() + "\n";
                }
            }
        }
        System.out.println(deps);
    }


    public boolean isProjectiveTree(){
        boolean proj = true;
        for(int i = 0; i<dependencies.length;i++){
            proj = proj && isArcProjective(i);
        }
        return proj;
    }



    public boolean isArcProjective(int arc){
        Token head = dependencies[arc].getHead();
        Token dependent = dependencies[arc].getDependent();
        boolean proj = true;
        int start = 0;
        int end = 0;
        if(dependencies[arc].isLeftDep()){
            start = dependent.getIndex();
            end = head.getIndex();
        }
        if(dependencies[arc].isRightDep()){
            start = head.getIndex();
            end = dependent.getIndex();
        }

        for(int i=start+1; i<end; i++){
            int idxHead = dependencies[i-1].getHead().getIndex();
            boolean flag = false;
            if( dependencies[arc].getHead().getIndex()==0 ||
                dependencies[arc].getHead().getIndex()==idxHead) {
                flag = true;
            }
            else{
                while (idxHead!=0){
                    idxHead = dependencies[idxHead-1].getHead().getIndex();
                    if(idxHead==dependencies[arc].getHead().getIndex()){
                        flag = true;
                        //System.out.println("path found from" + idxHead + " to" + i);
                        break;
                    }
                }
            }

            proj = proj && flag;
        }
        return proj;
    }

    @Override
    public boolean equals(Object obj){

        if(obj==null){
            return false;
        }

        if(!ProjectiveTree.class.isAssignableFrom(obj.getClass())){
            return false;
        }

        ProjectiveTree t = (ProjectiveTree) obj;

        if(this.dependencies.length!=t.dependencies.length) return false;

        for(int i=0; i< this.dependencies.length ; i++){
            if(!this.dependencies[i].equals(t.dependencies[i])){
                return false;
            }
        }

        return true;
    }

}
