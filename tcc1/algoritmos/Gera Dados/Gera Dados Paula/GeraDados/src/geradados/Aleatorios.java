/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geradados;

import java.util.*;

/**
 *
 * @author marianehadoop
 */
public class Aleatorios {
    Random rand = new Random();
    
    public void uniforme(){
        rand.nextInt();
    }
    
 private double getGaussian(double aMean, double aVariance){
     
     return aMean + rand.nextGaussian() * aVariance;
  }

 
}

