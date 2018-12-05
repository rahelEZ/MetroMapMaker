/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package m3.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;

/**
 *
 * @author Rahel
 */
public class PathGraphics extends Polyline implements CanvasItem{
    m3Data data;
    
    PathGraphics pathArt;
    
        public PathGraphics(){
            
            this.setStroke(Color.rgb(255, 255, 0, 0.7));
            this.setStrokeWidth(20);
        }
        

        @Override
        public String itemType() {
            return "route";
        }

        @Override
        public boolean isDraggable() {
            return false;
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
     
        
}