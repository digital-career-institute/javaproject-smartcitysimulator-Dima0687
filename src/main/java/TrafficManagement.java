package main.java;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/*
 * TrafficManagement.java: 
 * Simulates smart traffic management, including traffic lights,
 * congestion detection, and rerouting of vehicles. 
 * You can use a CSV file (traffic_data.csv) to represent traffic data.
 */

public class TrafficManagement implements Runnable {
  private List< Vehicle > vehicles;
  private Random random = new Random();
  private static String csvFile = "src" + File.separator + "resources" + File.separator + "traffic_data.csv";

  private double airQuality, noiseLevel, temperature, amountOfRainInML;

  public TrafficManagement() {
    this.vehicles = new ArrayList<>();
    readTrafficDataFromCSV();
  }
  
  public void run() {
    // Read traffic data from CSV
    // Implement traffic management logic using collections
    // Handle exceptions for I/O operations
    try {
      while ( !Thread.currentThread().isInterrupted() ) {
  
          synchronized ( this ) {
            joinOrRemoveVehicle();
            
            vehicles.forEach( vehicle -> {
              boolean isAccelarating = random.nextBoolean();
              if ( isAccelarating ) {
                vehicle.accelerate( random.nextDouble( 3, 25 ) );
              } else {
                vehicle.brake( random.nextDouble( 50 ) );
              }
            } );
          }
  
          TimeUnit.SECONDS.sleep( 3 );
      }
      writeTrafficDataToCSV();
    } catch ( InterruptedException e ) {
      System.out.println( Thread.currentThread().getName() + " interrupted!" );

      Thread.currentThread().interrupt();
    }

  }

  public double getAirQuality() {
    return airQuality;
  }

  public void setAirQuality( double airQuality ) {
    this.airQuality = airQuality;
  }

  public double getNoiseLevel() {
    return noiseLevel;
  }

  public void setNoiseLevel( double noiseLevel ) {
    this.noiseLevel = noiseLevel;
  }

  public double getTemperature() {
    return temperature;
  }

  public void setTemperature( double temperature ) {
    this.temperature = temperature;
  }

  public double getAmountOfRainInML() {
    return amountOfRainInML;
  }

  public void setAmountOfRainInML( double amountOfRainInML ) {
    this.amountOfRainInML = amountOfRainInML;
  }

  public Map< String, Integer > calculateAmountOfEachVehicleOnStreet() {
    Map< String, Integer > vehiclesOnStreet = new LinkedHashMap<>();
    vehiclesOnStreet.put( "VEHICLES ON STREET", vehicles.size() );

    vehicles.forEach( vehicle -> {
      String type = vehicle.getVehicleType().toUpperCase( Locale.GERMANY );
      Integer previousAmount = vehiclesOnStreet.get( type );
      Integer amount = previousAmount == null ? 1 : previousAmount + 1;

      vehiclesOnStreet.put( type, amount );
    } );

    return vehiclesOnStreet;
  }

  public void printAmountOfEachVehicleOnStreet() {
    Map< String, Integer > vehiclesOnStreet = calculateAmountOfEachVehicleOnStreet();

    vehiclesOnStreet.forEach( ( s, i ) -> System.out.printf( "\u001b[36;1;4m%s: %d\u001b[0m%n", s, i ) );
  }

  private void joinOrRemoveVehicle() {
    synchronized ( this ) {

      int degree18 = 18;
      String bicycle = Vehicle.VehicleTypes.BICYCLE.toString().toLowerCase( Locale.GERMANY );
      String car = Vehicle.VehicleTypes.CAR.toString().toLowerCase( Locale.GERMANY );
      String bus = Vehicle.VehicleTypes.BUS.toString().toLowerCase( Locale.GERMANY );
      String truck = Vehicle.VehicleTypes.TRUCK.toString().toLowerCase( Locale.GERMANY );

      String[] motorizedVehiclePool = { car, bus, truck };

      boolean isWarm = temperature > degree18;
      boolean isRainy = amountOfRainInML > 18; // ml^2

      if ( isWarm && !isRainy ) {
        // if temperature is great bicycle are joining the streets
        int amountOfBicycleToJoin = random.nextInt( 1, 6 );

        // will motorized vehicles join too?
        boolean isMotorizedVehicleJoining = random.nextBoolean();

        if ( isMotorizedVehicleJoining ) {
          int amountOfVehiclesToJoin = amountOfBicycleToJoin / 2;
          amountOfVehiclesToJoin = amountOfVehiclesToJoin == 0 ? 1 : amountOfVehiclesToJoin;

          System.out.printf( "\u001b[32;1;40m%d motorized vehicles will join.\u001b[0m%n", amountOfVehiclesToJoin );
          for ( int i = 0; i < amountOfVehiclesToJoin; i++ ) {
            String type = motorizedVehiclePool[ random.nextInt( motorizedVehiclePool.length ) ];

            vehicles.add( new Vehicle( type ) );
            System.out.printf( "\u001b[32;1;40mA %s joined.\u001b[0m%n", type );
          }

        }

        for ( int i = 0; i < amountOfBicycleToJoin; i++ ) {
          String registrationNumber = Vehicle.generateRegistrationNumber();
          double initialSpeed = Vehicle.getRandomCurrentSpeed();
          vehicles.add( new Vehicle( bicycle, registrationNumber, initialSpeed ) );
        }

        System.out.printf( "\u001b[32;1;40mThe weather is great. %d bicycles joined.\u001b[0m%n",
            amountOfBicycleToJoin );
      } else if ( !isWarm || isRainy ) {

        Map< String, Integer > vehiclesLeft = new HashMap<>();
        
        StringBuilder outputBuilder = new StringBuilder( "\u001b[31;1;40mThe weather is bad. " );
        
        vehicles.removeIf( vehicle -> {
          String type = vehicle.getVehicleType();
          boolean isBicycle = type.equalsIgnoreCase( bicycle );
          
          if( isBicycle ) {
            vehiclesLeft.putIfAbsent( bicycle, 1 );
          }
          
          return isBicycle;
        });
        
        boolean isBicycleInList = vehiclesLeft.containsKey( bicycle );
        
        if( isBicycleInList ) {
          outputBuilder.append("All bicycles left. ");
        }


        boolean isNoisy = noiseLevel > 65;
        boolean isMediumAirQuality = airQuality <= 60 && airQuality >= 35;
        boolean isBadAirQuality = !isMediumAirQuality;
        int amountOfBus[] = { 0 } ;
        
        // if it is to loud or to much smog every vehicle except the bus will have to leave
        if ( isBadAirQuality || isNoisy ) {

          vehicles.removeIf( vehicle -> {
            String vehicleType = transformFirstCharToUpperCase( vehicle.getVehicleType() );
            boolean isBus = vehicleType.equalsIgnoreCase( bus );

            Integer previousAmount = vehiclesLeft.get( vehicleType );
            Integer amount = previousAmount == null ? 1 : previousAmount + 1;

            if( !isBus ) {
              vehiclesLeft.put( vehicleType, amount );              
            } else {
              amountOfBus[0] = amountOfBus[0] + 1 ;
            }

            return !isBus;
          } );
        } else if ( isMediumAirQuality ) { // if the smog isn't so high. A car and a bus will join. We don't care about noisy.

          vehicles.removeIf( vehicle -> {
            String vehicleType = transformFirstCharToUpperCase( vehicle.getVehicleType() );
            boolean isCar = vehicleType.equalsIgnoreCase( car );
            boolean isBus = vehicleType.equalsIgnoreCase( bus );

            Integer previousAmount = vehiclesLeft.get( vehicleType );
            Integer amount = previousAmount == null ? 1 : previousAmount + 1;

            if( !isCar && !isBus ) {
              vehiclesLeft.put( vehicleType, amount );              
            } else if ( isBus ) {
              amountOfBus[0] = amountOfBus[0] + 1 ;
            }

            return ( !isCar && !isBus );
          } );

        } else { // is good air quality

          int amountOfMotorizedVehiclesToJoin = random.nextInt( 11 );
          System.out.printf( "\u001b[32;1;40m%dx motorized vehicle(s) will join.\u001b[0m%n",
              amountOfMotorizedVehiclesToJoin );
          for ( int i = 0; i < amountOfMotorizedVehiclesToJoin; i++ ) {
            String type = motorizedVehiclePool[ random.nextInt( motorizedVehiclePool.length ) ];

            vehicles.add( new Vehicle( type ) );
            type = transformFirstCharToUpperCase( type );
            System.out.println( type + " has joined." );
          }
          
          vehicles.forEach( vehicle -> {
            boolean isBus = vehicle.getVehicleType().equalsIgnoreCase( bus );
            
            if( isBus ) {
              amountOfBus[0] = amountOfBus[0] + 1 ;
            }
          });
        }
        
        String outputString;
        
        if( !vehiclesLeft.isEmpty() ) {
          if( !isBicycleInList ) {
            outputBuilder.append( "And also " );
            vehiclesLeft.forEach( ( s, i ) -> outputBuilder.append( String.format( "%s: %d, ", s, i ) ) );
            
            outputString = outputBuilder.toString().substring( 0, outputBuilder.length() - 2 ); // deleting the last commata            
          } else {
            outputString = outputBuilder.toString();             
          }
        } else {
          outputString = outputBuilder.toString();          
        }
        
        System.out.println( outputString + "\u001b[0m" );
        
        int maxAllowedAmountOfBus = 5;
        boolean isToMuchBusOnStreet = amountOfBus[0] > maxAllowedAmountOfBus;
        
        if( isToMuchBusOnStreet ) {
          for( int i = 0; i < maxAllowedAmountOfBus; i++ ) {
            Vehicle vehicle = vehicles.get( i );
            String type = vehicle.getVehicleType();
            String registrationNumber = vehicle.getRegistrationNumber();
            
            if(bus.equalsIgnoreCase( type )) {
              vehicles.remove( vehicle );
              System.out.println( "\u001b[31;1;40m" + type + " " + registrationNumber +" left due to excessive load.\u001b[0m" );
            }
          }
        }
      }

    }

  }

  private String transformFirstCharToUpperCase( String string ) {
    return Character.toUpperCase( string.charAt( 0 ) ) + string.substring( 1 ).toLowerCase( Locale.GERMANY );
  }

  private void readTrafficDataFromCSV() {
    Path csvPath = Path.of( csvFile );
    String line;
    String cvsSplitBy = ",";

    if ( !Files.exists( csvPath ) ) {
      System.out.println( "Couldn't read the File." );
      return;
    }

    try ( BufferedReader br = new BufferedReader( new FileReader( csvFile ) ) ) {
      while ( ( line = br.readLine() ) != null ) {
        String[] data = line.split( cvsSplitBy );
        String vehicleType = data[ 0 ];
        String registrationNumber = data[ 1 ];
        double currentSpeed = Double.parseDouble( data[ 2 ] );
        Vehicle vehicle = new Vehicle( vehicleType, registrationNumber, currentSpeed );
        vehicles.add( vehicle );
      }
    } catch ( IOException e ) {
      e.printStackTrace();
    }
  }

  private void writeTrafficDataToCSV() {
    Path csvPath = Path.of( csvFile );

    if ( !Files.exists( csvPath ) ) {
      System.out.println( "Couldn't write to the File." );
      return;
    }

    try ( BufferedWriter writer = new BufferedWriter( new FileWriter( csvFile ) ) ) {
      vehicles.forEach( vehicle -> {
        try {
          writer.write( String.format( "%s,%s,%s", vehicle.getVehicleType(), vehicle.getRegistrationNumber(),
              vehicle.getCurrentSpeed() ) );
          writer.newLine();
        } catch ( IOException e ) {
          throw new RuntimeException( e );
        }
      } );
      System.out.println( "Data written successfully to " + csvFile );
    } catch ( IOException e ) {
      System.out.println( "FileWriter has some problems to write into the file." );
    }
  }
}