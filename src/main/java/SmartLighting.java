package main.java;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/*
 * SmartLighting.java: Simulates smart lighting in the city, 
 * where street lights adjust their brightness based 
 * on the time of day and sensor data.
 */

public class SmartLighting implements Runnable {

  private double rainInML;
  private Random random = new Random();

  public void run() {
    while ( !Thread.currentThread().isInterrupted() ) {
      // Implement smart lighting logic

      // Adjust street light brightness based on time and sensor data

      // Get current time
      int hour = random.nextInt( 24 );
      
      adjustStreetLightBrightness( hour, rainInML );

      try {
        TimeUnit.SECONDS.sleep( 5 );
      } catch ( InterruptedException e ) {
        System.out.println( e );
        Thread.currentThread().interrupt();
      }
    }
  }
  
  public void setRainInML(double rainInML) {
    this.rainInML = rainInML;
  }

  private void adjustStreetLightBrightness( int hour, double rainInML ) {
    if ( hour >= 18 || hour <= 6 ) {
      System.out.printf( "%n\u001b[32;1;40mLights are on. The time is now %d o'clock.\u001b[0m%n", hour );
    } else if ( rainInML >= 25 ) { // ranInML indicates a strong rain, where it can be dark outside and the light
                                   // go on
      System.out.println(
          "\n\u001b[32;1;40mLights are on because it is raining so hard that it has become dark.\u001b[0m\n" );
    } else {
      System.out.println( "\n\u001b[31;1;40mLights are off.\u001b[0m\n" );
    }
  }
}
