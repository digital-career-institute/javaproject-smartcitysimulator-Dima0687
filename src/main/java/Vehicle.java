package main.java;

import java.util.Locale;
import java.util.Random;

public class Vehicle {
  private static Random random = new Random();
  private String vehicleType;
  private String registrationNumber;
  private double currentSpeed;

  public static enum VehicleTypes {
    CAR, BUS, BICYCLE, TRUCK
  }

  public Vehicle( String vehicleType ) {
    this( vehicleType, generateRegistrationNumber(), getRandomCurrentSpeed() );
  }

  public Vehicle( String vehicleType, String registrationNumber, double currentSpeed ) {
    this.vehicleType = vehicleType;
    this.registrationNumber = registrationNumber;
    this.currentSpeed = currentSpeed;
  }

  public void accelerate( double speedIncrease ) {
    int maxSpeed = 50;
    if ( currentSpeed == maxSpeed ) {
      System.out.printf(
          "%n\u001b[36;1;40m%s\u001b[0m \u001b[31;1;40m%s\u001b[0m is driving constantly \u001b[32;1;40m%.2f\u001b[0m km/h%n",
          transformToFirstCapital( this.vehicleType ), this.registrationNumber, this.currentSpeed );
      return;
    }

    this.currentSpeed += speedIncrease;

    if ( currentSpeed > maxSpeed ) { // city speed limit
      currentSpeed = maxSpeed;
    }
    System.out.printf(
        "%n\u001b[36;1;40m%s\u001b[0m \u001b[31;1;40m%s\u001b[0m accelerate up to \u001b[32;1;40m%.2f\u001b[0m km/h%n",
        transformToFirstCapital( this.vehicleType ), this.registrationNumber, this.currentSpeed );
  }

  public void brake( double speedDecrease ) {

    if ( currentSpeed <= 0 ) {
      System.out.printf( "%n\u001b[36;1;40m%s\u001b[0m \u001b[31;1;40m%s\u001b[0m is standing still%n",
          transformToFirstCapital( this.vehicleType ), this.registrationNumber, this.currentSpeed );
      return;
    }

    this.currentSpeed -= speedDecrease;

    // Ensure the speed doesn't go below 0
    if ( this.currentSpeed < 0 ) {
      this.currentSpeed = 0;
    }
    System.out.printf(
        "%n\u001b[36;1;40m%s\u001b[0m \u001b[31;1;40m%s\u001b[0m brakes down to \u001b[32;1;40m%.2f\u001b[0m km/h%n",
        transformToFirstCapital( this.vehicleType ), this.registrationNumber, this.currentSpeed );
  }

  private String transformToFirstCapital( String string ) {
    return Character.toUpperCase( string.charAt( 0 ) ) + string.substring( 1 ).toLowerCase( Locale.GERMANY );
  }

  public static String generateRegistrationNumber() {
    StringBuilder builder = new StringBuilder();

    while ( builder.length() < 3 ) {
      int charAsNumber = random.nextInt( 65, 91 ); // 65 - 90 (A - Z)
      builder.append( ( char ) charAsNumber );
    }

    int number = random.nextInt( 100, 999 );

    builder.append( number ); // appending number e.g TZX345

    return builder.toString();
  }

  public static double getRandomCurrentSpeed() {
    return random.nextDouble( 51 );
  }

  public double getCurrentSpeed() {
    return currentSpeed;
  }

  public String getVehicleType() {
    return vehicleType;
  }

  public String getRegistrationNumber() {
    return registrationNumber;
  }

  @Override
  public String toString() {
    return "Vehicle { type: " + vehicleType + "; registrationNumber: " + registrationNumber + "currentSpeed: "
        + currentSpeed + " }";
  }
}
