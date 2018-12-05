/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package m3.data;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

/**
 *
 * @author Rahel Zewde
 */
public class DraggableImage extends DraggableRectangle implements CanvasItem {

    ImagePattern imagePattern;

    public DraggableImage() {
        super();
    }

    public String filePath;

    public boolean loadImages(String filePath) {

        this.filePath = filePath;

        try {
            Image image = new Image(filePath);
            imagePattern = new ImagePattern(image);
            setFill(imagePattern);
            widthProperty().set(image.getWidth());
            heightProperty().set(image.getHeight());
            System.out.println(filePath);
            return true;
        } catch (Exception ex) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Image Loading Error");
            alert.setContentText("Please check if the selected file is an image file");
            alert.showAndWait();
            ex.printStackTrace();
            return false;
        }

    }

    @Override
    public m3State getStartingState() {
        return m3State.STARTING_IMAGE;
    }

    @Override
    public String getShapeType() {
        return IMAGE;
    }

    boolean loadImage(String toExternalForm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String itemType() {
        return "image";
    }

    @Override
    public boolean isDraggable() {
        return true;
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
