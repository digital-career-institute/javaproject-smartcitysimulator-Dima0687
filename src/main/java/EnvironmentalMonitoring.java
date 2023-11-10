package main.java;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
 * EnvironmentalMonitoring.java: Simulates environmental monitoring, 
 * including air quality, noise levels, and temperature.
 */

public class EnvironmentalMonitoring implements Runnable {

  private final List< Sensor > sensors;
  private final TrafficManagement trafficManagement;
  private final SmartLighting smartLighting;

  public EnvironmentalMonitoring( TrafficManagement trafficManagement, SmartLighting smartLighting ) {
    sensors = new ArrayList<>( List.of( new Sensor( "Air Quality" ), new Sensor( "Noise Level" ),
        new Sensor( "Temperature" ), new Sensor( "Rain" ) ) );

    this.trafficManagement = trafficManagement;
    this.smartLighting = smartLighting;
  }

  public void run() {
    // Implement environmental monitoring logic
    // Monitor air quality, noise levels, temperature, etc.

    while ( !Thread.currentThread().isInterrupted() ) {
      measureEnvironmentalDataAndForwardItToRelevantAuthorities();
      
      printSensorReadings();

      try {
        TimeUnit.SECONDS.sleep( 10 );
      } catch ( InterruptedException e ) {
        e.printStackTrace();

        Thread.currentThread().interrupt();
      }
    }
  }

  private void measureEnvironmentalDataAndForwardItToRelevantAuthorities() {
    sensors.forEach( sensor -> {
      sensor.measure();
      forwardToRelevantAuthority(sensor);
    } );
  }

  private void forwardToRelevantAuthority( Sensor sensor ) {
    String sensorType = sensor.getSensorType();
    
    switch ( sensorType ) {
    case "Air Quality": {
      double airQuality = sensor.getCurrentValue();
      trafficManagement.setAirQuality( airQuality );
      break;
    }
    case "Noise Level": {
      double noiseLevel = sensor.getCurrentValue();
      trafficManagement.setNoiseLevel( noiseLevel );
      break;
    }
    case "Temperature": {
      double temperature = sensor.getCurrentValue();
      trafficManagement.setTemperature( temperature );
      break;
    }
    case "Rain": {
      double rainInML = sensor.getCurrentValue();
      smartLighting.setRainInML( rainInML );
      trafficManagement.setAmountOfRainInML( rainInML );
      break;
    }
    }
  }

  private void printSensorReadings() {
    synchronized ( trafficManagement ) {
      System.out.println();
      sensors.forEach( sensor -> System.out.printf( "\u001b[36;4m%s:\u001b[0m \u001b[31m%.2f\u001b[0m%n",
          sensor.getSensorType(), sensor.getCurrentValue() ) );
      trafficManagement.printAmountOfEachVehicleOnStreet();
    }
  }

}
