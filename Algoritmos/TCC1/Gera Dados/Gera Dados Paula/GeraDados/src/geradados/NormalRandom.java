/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geradados;

import java.util.Random;

/**
 *
 * @author marianehadoop
 */
public class NormalRandom {
 
    double MEAN = 100.0f; 
    double VARIANCE = 5.0f;
    
//    for (int idx = 1; idx <= 10; ++idx){
//      log("Generated : " + gaussian.getGaussian(MEAN, VARIANCE));
//    }
//  }
    
  private Random fRandom = new Random();
  
  private double getNormal(double aMean, double aVariance){
      return aMean + fRandom.nextGaussian() * aVariance;
  }

}
