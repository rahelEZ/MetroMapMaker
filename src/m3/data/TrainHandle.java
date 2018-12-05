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
public class TrainHandle extends Text implements Draggable, CanvasItem{

    
    double startX;
    double startY;
    
    
    public static final int HANDLE_RADIUS = 5;
    public double x;
    public double y;
    public String line;
    public String name;
    
    
   public TrainHandle(int x, int y,String value) {
       this.setX(x);
       this.setY(y);
       this.line = value;
       this.setText(value);
//       setFill(Color.WHITE);
//        setStroke(Color.BLACK);
//        setStrokeWidth(2);
    }
   
   public static String trackStartHandleName(String trainLine){
       return trainLine + "_start"; 
   }
   public static String trackEndHandleName(String trainLine){
       return trainLine + "_end";
   }

    @Override
    public String itemType() {
        return "handle";
    }

    @Override
    public boolean isDraggable() {
        return true;
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
