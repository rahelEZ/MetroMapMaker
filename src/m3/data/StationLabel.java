/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package m3.data;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

/**
 *
 * @author Rahel
 */
public class StationLabel extends Text implements Draggable, CanvasItem{

    
    public static final int DEFAULT_OFFSET_X = -10;
    public static final int DEFAULT_OFFSET_Y = -20;
    
    
    public int offsetX = StationLabel.DEFAULT_OFFSET_X;
    public int offsetY = StationLabel.DEFAULT_OFFSET_Y;
    
    double startX;
    double startY;
    
    
    public static final int HANDLE_RADIUS = 5;
    
   public DraggableStation station;
    
    public String line;
    public String name;
    
    
   public StationLabel(DraggableStation station, int x, int y) {
       this.setX(x + offsetX);
       this.setY(y + offsetY);
       this.station = station;
       this.setText(station.name);
       
       
       
       
       
       
    }
   
   public void recalculateOffSet(){
       offsetX  = (int) (this.getX() - station.getX() );
       offsetY = (int) ( this.getY() - station.getY());
   }
   
   
   
   public int angle = 0;
   
   public void rotate(){
       this.angle-= 90;
       this.angle%=360;
       this.setRotate(angle);
   }
   

    @Override
    public String itemType() {
        return "stationLabel";
    }

    @Override
    public boolean isDraggable() {
        return false;
    }

    @Override
    public m3State getStartingState() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void start(int x, int y) {
        startX = x;
	startY = y;
    }

      @Override
    public void drag(int x, int y) {
	double diffX = x -  startX;
	double diffY = y - startY;
	double newX = getX() + diffX;
	double newY = getY() + diffY;
	xProperty().set(newX);
	yProperty().set(newY);
        
	startX = x;
	startY = y;
        
    }
    public void setAngle(int angle){
        this.angle = angle;
        this.setRotate(angle);
    }
    public void newOffset(int offsetX,int OffsetY){
        int oldX = (int) (this.getX() - this.offsetX);
        int oldY = (int) (this.getY() - this.offsetY);
        this.offsetX = offsetX;
        this.offsetY = OffsetY;
        this.moveTo(oldX, oldY);
    }
    public void moveTo(int x, int y){
        this.setX(x + offsetX);
        this.setY(y + offsetY);
    }
    @Override
    public void size(int x, int y) {
	double width = x - getX();
	double height = y - getY();
    }

    @Override
    public double getWidth() {
        return 0;
    }

    @Override
    public double getHeight() {
        return 0;
    }
    
    
    @Override
    public void setLocationAndSize(double initX, double initY, double initWidth, double initHeight) {
	xProperty().set(initX);
	yProperty().set(initY);
    }
    
    public void setLocation(double initX, double initY) {
	xProperty().set(initX);
	yProperty().set(initY);
    }
    @Override
    public String getShapeType() {
	return HANDLE;
    }
    
    
    public double getCenterX(){
        return this.getX() + (this.getWidth() / 2);
    }
    public double getCenterY(){
        
        return this.getY() + (this.getHeight()/ 2);
    }

    void moveTo(double initX, double initY) {
        this.moveTo((int) initX, (int) initY);
    }
    
      
     @Override
    public double[] getRange() {
        double[] results = new double[2];
        results[0] = this.getWidth() + 100;
        results[1] = this.getHeight() + 100;
        return results;
    }

    @Override
    public double[] getXy() {
        double[] results = new double[2];
        results[0] = this.getX() + (this.getWidth() / 2);
        results[1] = this.getY() +  (this.getHeight() / 2);
        return results;
    }
    
}
