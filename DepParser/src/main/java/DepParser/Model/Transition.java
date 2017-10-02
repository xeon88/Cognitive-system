/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DepParser.Model;

/**
 *
 * @author Marco Corona
 *
 */


public class Transition {
    
    private State result;
    private ArcSystem.transition action;
    
    public Transition(State result, ArcSystem.transition action){
    
        this.result = result;
        this.action = action;
    }
    
    
    public State getState(){
        return result;
    }
    
    public ArcSystem.transition getAction(){
        return action;
    }
    
    
}
