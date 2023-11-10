package main.java;

import java.util.Random;

public class Sensor {
  private String sensorType;
  private double currentValue;
  private Random rand = new Random();

  public Sensor( String sensorType ) {
    this.sensorType = sensorType;
    this.currentValue = 0.0;
  }

  public void measure() {
    // Simulate measuring data by generating random values
    if ( "Air Quality".equals( sensorType ) ) {
      currentValue = rand.nextDouble( 100.1 ); // Random value between 0 and 100
    } else if ( "Noise Level".equals( sensorType ) ) {
      currentValue = rand.nextDouble( 120.1 ); // Random value between 0 and 120 (in decibels)
    } else if ( "Temperature".equals( sensorType ) ) {
      currentValue = rand.nextDouble( 40.1 ) - 10; // Random value between -10 and 30 degrees Celsius
    } else if ( "Rain".equals( sensorType ) ) {
      currentValue = rand.nextDouble( 51 ); // Random value between 0 and 50
    }

  }

  public double getCurrentValue() {
    return currentValue;
  }

  public String getSensorType() {
    return sensorType;
  }

  @Override
  public String toString() {
    return "Sensor { type: " + sensorType + "; value: " + currentValue + "; }";
  }
}