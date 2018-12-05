/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package m3.data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
/**
 *
 * @author Rahel Zewde
 */
public class TrainLine extends Polyline implements CanvasItem{
    double startX;
    double startY;
    
    
    public String lineName;
    public String lineColor;
    public double lineThickness;
    public Color color = Color.BLACK;
    public List<String> stationsNames = new ArrayList<>();
    public List<DraggableStation> stations = new ArrayList<>();
    public boolean circular = false;
    
    public TrainHandle[] handles;
    
    
    public double start_x = 0;
    public double start_y = 0;
    
    public double end_x = 0;
    public double end_y = 0;
    
    public boolean justCreated=false;
    public int thickness = DEFAULT_TICKNESS;

    public int getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
        this.setStrokeWidth(thickness);
    }
    
    
    
    public static final int DEFAULT_TICKNESS = 4;
    
    public TrainLine(){
        
        setOpacity(1.0);
        strokeWidthProperty().set(DEFAULT_TICKNESS);        
	startX = 0.0;
	startY = 0.0;
        
        
    }
    
    public void prepareStations(HashMap<String, DraggableStation> allStations){
        this.stations.clear();
        for (int i = 0; i < stationsNames.size(); i++) {
            DraggableStation station = allStations.get(stationsNames.get(i));
            this.stations.add(station);
            station.addRail(this);
        }
        
        
      
    }
    TrainHandle startHandle;
    TrainHandle endHandle;
    
    public void init(m3Data dataMan){
        
        this.getPoints().clear();
        if (!this.circular){
             startHandle = new TrainHandle(15, 15,lineName);
            startHandle.name = TrainHandle.trackStartHandleName(this.lineName);
            
             endHandle = new TrainHandle(15, 100,lineName);
            endHandle.name = TrainHandle.trackEndHandleName(this.lineName);
            
            this.getPoints().add(startHandle.getX());
            this.getPoints().add(startHandle.getY());

            this.getPoints().add(endHandle.getX());
            this.getPoints().add(endHandle.getY());
            
            
            
            dataMan.addHandle(startHandle);
            dataMan.addHandle(endHandle);
            
        }
    }
    
    
    public void loadhandles( HashMap<String, TrainHandle> allHandles){
        startHandle = allHandles.get(TrainHandle.trackStartHandleName(this.lineName));
        endHandle = allHandles.get(TrainHandle.trackEndHandleName(this.lineName));
        
    }
    
    public void drawPath(HashMap<String, DraggableStation> allStations){
        this.getPoints().clear();
        
        if (!this.circular){
            if (startHandle!=null){
                
            this.getPoints().add(startHandle.getCenterX());
            this.getPoints().add(startHandle.getCenterY());
               
            }
        }
        
        
        
        
        for (int i = 0; i < stationsNames.size(); i++) {
            DraggableStation station = allStations.get(stationsNames.get(i));
            this.getPoints().add(station.getCenterX()* 1.0);
            this.getPoints().add(station.getCenterY()* 1.0);
        }
        
        if (this.circular){
            
            int fakePoints = 3 - stationsNames.size();
            
            fakePoints = fakePoints > 0 ? fakePoints : 0;
            
            for (int i = 0; i < fakePoints; i++){
                this.getPoints().add((i * 100.0) + 100);
                this.getPoints().add((i * 100.0) + 100);
            }
            
            
            
            this.getPoints().add(this.getPoints().get(0));
            this.getPoints().add(this.getPoints().get(1));
        }
        else if (this.circular){
            this.getPoints().addAll(200.0,200.0,100.0,100.0);
        }
        
        if (!this.circular){
            if (endHandle!=null){
             
                this.getPoints().add(endHandle.getCenterX());
                this.getPoints().add(endHandle.getCenterY());
            }
            else{
            }
        }
        
        
    }

    @Override
    public String itemType() {
        return "track";
    }
    
    @Override
    public boolean isDraggable() {
        return false;
    }

    @Override
    public String toString() {
        return lineName;
    }

    boolean hasStation(String name) {
        boolean found = false;
        for(String station: this.stationsNames){
            if (station.equals(name)){
                found= true;
                break;
            }
        }
        
        return found;
    }

    int getStationIndex(String name) {
        int index = -1;
        try{
            for (int i = 0; i < this.stationsNames.size(); i++) {
            
             if (stationsNames.get(i).equals(name)){
                index = i;
                break;
            }
        }
        }
        catch(Exception ex){
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
        
        
        return index;
        
    }
    List<TrainLine> transferTo = new ArrayList<>();
    HashMap<String,TrainLine> transferToName = new HashMap<>();
    public void addTransfer(TrainLine line){
        
        
        if (!transferToName.containsKey(line.lineName) && !line.lineName.equals(this.lineName)){
            
        
        transferTo.add(line);
        transferToName.put(line.lineName, line);
        }
        
    }
    
    
    
    public DraggableStation findIntersectingStation (TrainLine intersectingLine) {
        // GO TRHOUGH ALL THE STATIONS IN THIS LINE
        for (int i = 0; i < this.stations.size(); i++) {
            DraggableStation station1 = this.stations.get(i);
            
            // FOUND IT
            if (intersectingLine.hasStation(station1.name)) {
                return station1;
            }
        }
        // THEY DON'T SHARE A STATION'
        return null;
    }
    
    
    
     int earliestX = -1;
        int latestX = -1;
        int earliestY = -1;
        int latestY = -1;
 @Override
    public double[] getRange() {
        double[] results = new double[2];
        
         earliestX = -1;
         latestX = -1;
         earliestY = -1;
         latestY = -1;
        
        
        for(int i = 0; i <this.getPoints().size(); i++){
            double thisVal = this.getPoints().get(i);
            if (i%2==0){
                if (earliestX==-1 || thisVal < earliestX)  earliestX = (int) thisVal;
                if (latestX==1 || thisVal > latestX) latestX = (int) thisVal;
           }
            else{
                if (earliestY==-1 || thisVal < earliestY)  earliestY = (int) thisVal;
                if (latestY==1 || thisVal > latestY) latestY = (int) thisVal;
           }
            
            
            
        }
        
        results[0] = latestX- earliestX;
        results[1] = latestY - earliestY;
        return results;
    }

    @Override
    public double[] getXy() {
        double[] results = new double[2];
        
        double[] dimensions = this.getRange();
        results[0] = this.earliestX + (dimensions[0] /2);
        results[1] = this.earliestY +(dimensions[1]/2);
        return results;
    }

    public void setHandleText(String newName) {
        startHandle.name= newName;
        endHandle.name=newName;
    }
     
        

}
