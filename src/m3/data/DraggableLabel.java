/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package m3.data;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 * @author Rahel Zewde
 */
public class DraggableLabel extends Text implements Draggable,CanvasItem {

    public enum STYLE{
        NORMAL,BOLD,ITALIC
    }
    
    public STYLE style = DraggableLabel.STYLE.NORMAL;
    
    double startX;
    double startY;
    
    public boolean bold = false;
    public boolean italic = false;
    public int size  = 14;
    public String fontfalimy = "Courier";
    public Color fontColor = Color.BLACK;
    
    public boolean justCreated = false;
    
    public DraggableLabel(){
        setX(0.0);
        setY(0.0);
        
        
        setOpacity(1.0);
	startX = 0.0;
	startY = 0.0;
        
    }
    
    
    public void setColor(Color color){
        this.fontColor = color;
        applyFont();
    }
    
    public void setBold(boolean bold){
       this.bold = bold;;
       applyFont();;
    }
    
    public void setItalic(boolean italic){
        this.italic = italic;
        applyFont();
    }
    public void setFontFamily(String family){
        this.fontfalimy =  family;
        applyFont();
    }
    public void setSize(int size){
        this.size = size;
        applyFont();
    }
    
    public void applyFont(){
        this.setStyle("-fx-font-family:\"" + fontfalimy + "\";" +
                      "-fx-font-size:" + size + "px;" + 
                      "-fx-font-weight:" + (bold ? "bolder;" : "normal;") +
                      "-fx-font-style:" + (italic ? "oblique;" : "normal;" )       
        );
        this.setFill(fontColor);
    }
    
    
    @Override
    public void start(int x, int y) {
	startX = x;
	startY = y;
        if (justCreated){
            setX(x);
            setY(y);
            justCreated = false;
        }
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
	return LABEL;
    }

    @Override
    public String itemType() {
        return "label";
    }
    
    @Override
    public boolean isDraggable() {
        return true;
    }
    
    @Override
    public m3State getStartingState() {
        return m3State.STARTING_LABEL;
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
