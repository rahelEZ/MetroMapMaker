package m3.data;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
/**
 *
 * @author rahel
 */
public class DraggableStation extends Circle implements Draggable,CanvasItem{
    double startCenterX;
    double startCenterY;
    
    
    public static final int STATION_RAIDUS = 8;
    
    public String name;
    public Integer x_cor;
    public Integer y_cor;
    public Color color = Color.WHITE;
    public int radius = STATION_RAIDUS;
    public List<TrainLine> rails = new ArrayList<>();
    public StationLabel stationLabel;
    
    public DraggableStation(String name) {
	setCenterX(0.0);
	setCenterY(0.0);
	setRadiusX(STATION_RAIDUS);
	setRadiusY(STATION_RAIDUS);
	startCenterX = 0.0;
	startCenterY = 0.0;
        this.name = name;
        stationLabel = new StationLabel(this, 0, 0);
    }
    @Override
    public m3State getStartingState() {
	return m3State.STARTING_STATION;
    }
     @Override
    public void start(int x, int y) {
	startCenterX = x;
	startCenterY = y;
    }
    
    @Override
    public void drag(int x, int y) {
	double diffX = x - startCenterX;
	double diffY = y - startCenterY;
	double newX = getCenterX() + diffX;
	double newY = getCenterY() + diffY;
	setCenterX(newX);
	setCenterY(newY);
	startCenterX = x;
	startCenterY = y;
        stationLabel.moveTo(newX,newY);
        
    }
    
    @Override
    public void size(int x, int y) {
	double width = x - startCenterX;
	double height = y - startCenterY;
	double centerX = startCenterX + (width / 2);
	double centerY = startCenterY + (height / 2);
	setCenterX(centerX);
	setCenterY(centerY);
	setRadiusX(width / 2);
	setRadiusY(height / 2);	
    }
        
    @Override
    public double getX() {
	return getCenterX() - getRadiusX();
    }

    @Override
    public double getY() {
	return getCenterY() - getRadiusY();
    }

    @Override
    public double getWidth() {
	return getRadiusX() * 2;
    }

    @Override
    public double getHeight() {
	return getRadiusY() * 2;
    }
        
    
    public void setLocation(int x, int y){
        this.setLocationAndSize(x - (STATION_RAIDUS/2 ) * 1, y - (STATION_RAIDUS/2 ) *1, STATION_RAIDUS, STATION_RAIDUS);
    }
    @Override
    public void setLocationAndSize(double initX, double initY, double initWidth, double initHeight) {
	setCenterX(initX + (initWidth/2));
	setCenterY(initY + (initHeight/2));
	setRadiusX(this.radius);
	setRadiusY(this.radius);
        setFill(this.color);
        setStroke(Color.BLACK);
        setStrokeWidth(2);
        this.stationLabel.moveTo(initX, initY);
    }
    
    @Override
    public String getShapeType() {
	return STATION;
    }

    public void addRail(TrainLine line){
        if (this.rails.indexOf(line)==-1){
            this.rails.add(line);
        }
    }
   
    private void setRadiusX(double d) {
        this.radiusProperty().set(d);
    }

    private void setRadiusY(double d) {
    this.radiusProperty().set(d);
    }

    private double getRadiusX() {
        return this.radiusProperty().get();
    }

    private double getRadiusY() {
        
        return this.radiusProperty().get();
    }

    @Override
    public String itemType() {
        return "station";
    }
    
    @Override
    public boolean isDraggable() {
        return true;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public void setColor(Color stationColor) {
        this.color = stationColor;
        this.setFill(color);
    }
    
    

    public void sizeStation(Integer radius) {
        this.radius = radius;
        this.setRadius(radius);
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
        results[0] = this.getCenterX();
        results[1] = this.getCenterY();
        return results;
    }

    void rename(String newName) {
        this.name = newName;
        if (this.stationLabel!=null){
            this.stationLabel.setText(name);
        }
        
        
    }
    
    
    
}
