/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package m3.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Rahel
 */
public class m3Path {
    public DraggableStation startStation;
    public DraggableStation endStation;
    
    
    public List<String> navigation = new ArrayList<>();
    
   public  int STATION_COST = 3;
   public  int TRANSFER_COST = 10;
    
    
    public List<TrainLine> tripLines = new ArrayList<>();
    public HashMap<String, TrainLine> tripLineNames= new HashMap<>();
    
    // THESE ARE ALL THE STATIONS VISITED WHEN FOLLOWING THE PATH
    
    
    public List<DraggableStation> tripStations = new ArrayList<>();
    public HashMap<String,DraggableStation> tripStationNames= new HashMap<>();


    public List<DraggableStation> transferStations = new ArrayList<>();

    private m3Path(DraggableStation startStation, DraggableStation endStation) {
  this.startStation = startStation;
        this.endStation = endStation;
    }
    
    
    
    
    
    
    public m3Path copy(){
        m3Path path = new m3Path(this.startStation,this.endStation);
       
        for (int i = 0; i < this.transferStations.size(); i++) {
            path.transferStations.add(this.transferStations.get(i));
        }
        
        for (int i =0 ; i < this.tripStations.size(); i++){
            path.tripStationNames.put(this.tripStations.get(i).name, this.tripStations.get(i));
            path.tripStations.add(this.tripStations.get(i));
        }
        
        for (int i =0 ; i < this.tripLines.size(); i++){
            path.tripLineNames.put(this.tripLines.get(i).lineName, this.tripLines.get(i));
            path.tripLines.add(this.tripLines.get(i));
        }
        
        return path;
    }
    
    public void addBoarding(TrainLine line, DraggableStation station){
        this.tripLines.add(line);
        this.tripLineNames.put(line.lineName, line);
        
        
        
        this.transferStations.add(station);
    }
    
    
    
    
    public boolean isCompleteTrip() {
        if (this.tripLines.isEmpty()) {
            return false;
        }

        // THEN, IS THE END STATION ON THE LAST LINE? IF IT IS NOT THEN THE TRIP IS INCOMPLETE
        if (!this.tripLines.get(this.tripLines.size()-1).hasStation(this.endStation.name)) {
            return false;
        }

        // NOW, ARE ALL THE BOARDING STATIONS ON ALL THE TRIP LINES, IF NOT IT'S INCORRECT
        for (int i = 0; i < this.transferStations.size(); i++) {
            System.err.println(i + "  -- " + this.transferStations.size());
            if (!this.tripLines.
                    get(i).
                    hasStation(
                    this.transferStations.get(i)
                            .name
            )) {
                return false;
            }
        }

        // IF WE MADE IT THIS FAR WE KNOW IT'S A COMPLETE TRIP'
        return true;
    }
    
    
    public List<DraggableStation> getTripStations() {
        // WE'LL RETURN AN ARRAY OF STATIONS AND WE'LL USE THE NAMES
        // FOR A QUICK LOOKUP
        this.tripStations.clear();// = new Array();
        this.tripStationNames.clear();// = new Array();

        // WE ONLY DO THIS IF WE HAVE A VALID TRIP
        if (this.isCompleteTrip()) {
            // IF WE MADE IT THIS FAR WE KNOW IT'S A GOOD TRIP
            int i = 0;
            while (i < this.transferStations.size() - 1) {
                List<DraggableStation> stationsToAdd = this.generateStationsForPathOnLine(
                    this.tripLines.get(i), this.transferStations.get(i), this.transferStations.get(i + 1));
                for (int j = 0; j < stationsToAdd.size(); j++) {
                    DraggableStation stationToAdd = stationsToAdd.get(j);
                    if (!this.tripStationNames.containsKey(stationToAdd.name)) {
                        this.tripStations.add(stationToAdd);
                        this.tripStationNames.put(stationToAdd.name, stationToAdd);
                    }
                }

                // ONTO THE NEXT LINE
                i++;
            }
            // AND NOW FOR THE LAST LINK IN THE CHAIN
            List<DraggableStation> stationsToAdd = this.generateStationsForPathOnLine(
                    this.tripLines.get(i), this.transferStations.get(i), this.endStation);
            for (int k = 0; k < stationsToAdd.size(); k++) {
                DraggableStation stationToAdd = stationsToAdd.get(k);
                this.tripStations.add(stationToAdd);
            }
        }

        // RETURN THE STATIONS
        return this.tripStations;
    }

    public boolean hasLine (String testLineName) {
        return this.tripLineNames.containsKey(testLineName);
    }

    
    public int calculateTimeOfTrip () {
        List stations = this.getTripStations();
        int stationsCost = (stations.size() - 1) * this.STATION_COST;
        int transferCost = (this.tripLines.size() - 1) * this.TRANSFER_COST;
        return stationsCost + transferCost;
    }
    
    public boolean hasLineWithStation (String testStationName) {
        // GO THROUGH ALL THE LINES AND SEE IF IT'S IN ANY OF THEM'
        for (int i = 0; i < this.tripLines.size(); i++) {
            if (this.tripLines.get(i).hasStation(testStationName)) {
                // YUP
                return true;
            }
        }
        // NOPE
        return false;
    }
    
    
    
       public List<DraggableStation> generateStationsForPathOnLine(TrainLine line, DraggableStation station1, DraggableStation station2) {
        List<DraggableStation> stationsOnPath = new ArrayList<>();
        int station1Index = line.getStationIndex(station1.name);
        int station2Index = line.getStationIndex(station2.name);
        
        // FOR CIRCULAR LINES WE CAN GO IN EITHER DIRECTION
        if (line.circular) {
            if (station1Index >= station2Index) {
                int forward = station1Index - station2Index;
                int reverse = station2Index + line.stations.size() - station1Index;
                if (forward < reverse) {
                    for (int i = station1Index; i >= station2Index; i--) {
                        DraggableStation stationToAdd = line.stations.get(i);
                        stationsOnPath.add(stationToAdd);
                    }
                }
                else {
                    for (int i = station1Index; i < line.stations.size(); i++) {
                        DraggableStation stationToAdd = line.stations.get(i);
                        stationsOnPath.add(stationToAdd);
                    }
                    for (int i = 0; i <= station2Index; i++) {
                        DraggableStation stationToAdd = line.stations.get(i);
                        stationsOnPath.add(stationToAdd);
                    }
                }
            }
            // STILL CIRCULAR, BUT station1 IS BEFORE station2 IN THE ARRAY
            else {
                int forward = station2Index - station1Index;
                int reverse = station1Index + line.stations.size() - station2Index;
                if (forward < reverse) {
                    for (int i = station1Index; i <= station2Index; i++) {
                        DraggableStation stationToAdd = line.stations.get(i);
                        stationsOnPath.add(stationToAdd);
                    }
                }
                else {
                    for (int i = station1Index; i >= 0; i--) {
                        DraggableStation stationToAdd = line.stations.get(i);
                        stationsOnPath.add(stationToAdd);
                    }
                    for (int i = line.stations.size() - 1; i >= station2Index; i--) {
                        DraggableStation stationToAdd = line.stations.get(i);
                        stationsOnPath.add(stationToAdd);
                    }
                }
            }
        }
        // NOT CIRCULAR
        else {
            if (station1Index >= station2Index) {
                for (int i = station1Index; i >= station2Index; i--) {
                    DraggableStation stationToAdd = line.stations.get(i);
                    stationsOnPath.add(stationToAdd);
                }
            }
            else {
                for (int i = station1Index; i <= station2Index; i++) {
                    DraggableStation stationToAdd = line.stations.get(i);
                    stationsOnPath.add(stationToAdd);
                }
            }
        }
        return stationsOnPath;
    }
 
       
       
    public static m3Path findMinimumTransferPath  (DraggableStation startStation, DraggableStation endStation) {
        List<TrainLine> linesToTest = new ArrayList<>();
        HashMap<String, String> visitedLineNames = new HashMap<>();

        // THIS WILL COUNT HOW MANY TRANSFERS
        int numTransfers = 0;

        // THESE WILL BE PATHS THAT WE WILL BUILD TO TEST
        List<m3Path> testPaths = new ArrayList<>();

        // START BY PUTTING ALL THE LINES IN THE START STATION
        // IN OUR linesToTest Array
        for (int i = 0; i < startStation.rails.size(); i++) {
            m3Path path = new m3Path(startStation, endStation);
            testPaths.add(path);
            path.addBoarding(startStation.rails.get(i), startStation);
        }

        boolean found = false;
        boolean morePathsPossible = true;
        List<m3Path> completedPaths = new ArrayList<>();
        while (!found && morePathsPossible) {
            List<m3Path> updatedPaths = new ArrayList<>();
            for (int i = 0; i < testPaths.size(); i++) {
                m3Path testPath = testPaths.get(i);

                // FIRST CHECK TO SEE IF THE DESTINATION IS ALREADY ON THE PATH
                if (testPath.hasLineWithStation(endStation.name)) {
                    completedPaths.add(testPath);
                    found = true;
                    morePathsPossible = false;
                }
                else if (morePathsPossible) {
                    // GET ALL THE LINES CONNECTED TO THE LAST LINE ON THE TEST PATH
                    // THAT HAS NOT YET BEEN VISITED
                    TrainLine lastLine = testPath.tripLines.get(testPath.tripLines.size() - 1);
                    for (TrainLine testLine : lastLine.transferTo) {
                        if (!testPath.hasLine(testLine.lineName)) {
                            m3Path newPath = testPath.copy();
                            DraggableStation intersectingStation = lastLine.findIntersectingStation(testLine);
                            System.out.println(intersectingStation + "check check");
                            newPath.addBoarding(testLine, intersectingStation);
                            updatedPaths.add(newPath);
                        }
                        // DEAD ENDS DON'T MAKE IT TO THE NEXT ROUND
                    }
                }
            }
            if (updatedPaths.size() > 0) {
                testPaths = updatedPaths;
                numTransfers++;
            }
            else {
                morePathsPossible = false;
            }
        }
        // WAS A PATH FOUND?
        if (found) {
            m3Path shortestPath = completedPaths.get(0);
            int shortestTime = shortestPath.calculateTimeOfTrip();
            for (int i = 1; i < completedPaths.size(); i++) {
                m3Path testPath = completedPaths.get(i);
                int timeOfTrip = testPath.calculateTimeOfTrip();
                if (timeOfTrip < shortestTime) {
                    shortestPath = testPath;
                    shortestTime = timeOfTrip;
                }
            }
            // WE NOW KNOW THE SHORTEST PATH, COMPLETE ITS DATA FOR EASY USE
           
            
            
           shortestPath.navigation = updatePathDescription(shortestPath, startStation, endStation);
            return shortestPath;
        }
        // NO PATH FOUND
        else {
            return null;
        }
    }
    
    
    
    public static List<String> updatePathDescription(m3Path path, DraggableStation startStationName, DraggableStation endStationName) {
        // ONLY DO THIS IF THERE ACTUALLY IS A PATH
        
        List<String> instructions = new ArrayList<>();
        if ((path != null)) {
            int i = 0;
            String lastLineName = "";
            for (; i < path.tripLines.size(); i++) {
                TrainLine line = path.tripLines.get(i);
                instructions.add("Board " + line.lineName + " at " + path.transferStations.get(i).name);
                lastLineName = line.lineName;
            }
            instructions.add("Disembark " + lastLineName + " at " + endStationName );
        }
        return instructions;
    }
    
    
}
