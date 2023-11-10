package main.java;

import java.util.concurrent.TimeUnit;

/*
 * SmartCitySimulation.java: 
 * The main class of the simulation that starts and manages 
 * the different aspects of the smart city simulation.
 * 
 * THIS PROGRAM CAN BE IMPOVED A LOT!!!!!  =) 
 */

public class SmartCitySimulation {
  public static void main( String[] args ) {
    String heading = "SMART CITY SIMULATOR";
    System.out.printf( "\u001b[37;1;44m%30s%s%n%30s%2$s\u001b[0m%n", heading, " ".repeat( 30 - heading.length() ), "=".repeat( heading.length() ) );
    
    TrafficManagement trafficManagement = new TrafficManagement();
    SmartLighting smartLighting = new SmartLighting();
    EnvironmentalMonitoring environmentalMonitoring = new EnvironmentalMonitoring( trafficManagement, smartLighting );

    // Start simulation components using multithreading
    Thread trafficThread = new Thread( trafficManagement );
    Thread lightingThread = new Thread( smartLighting );
    Thread monitoringThread = new Thread( environmentalMonitoring );

    trafficThread.start();
    lightingThread.start();
    monitoringThread.start();

    // Add exception handling for each component
    try {
      trafficThread.join();
      lightingThread.join();
      monitoringThread.join();

      //TimeUnit.MINUTES.sleep( 2 );
      TimeUnit.SECONDS.sleep( 5 );
      trafficThread.interrupt();
      lightingThread.interrupt();
      monitoringThread.interrupt();
    } catch ( InterruptedException e ) {
      e.printStackTrace();
    }
  }
}
