package m3.gui;

import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.shape.Shape;
import djf.AppTemplate;
import djf.ui.Log;
import m3.data.CanvasItem;
import m3.data.Draggable;
import m3.data.StationLabel;
import m3.data.m3Data;

import m3.data.m3State;
import static m3.data.m3State.*;
/**
 * This class responds to interactions with the rendering surface.
 *
 * @author Rahel Zewde
 * @version 2.0
 */
public class CanvasController {

    AppTemplate app;

    public CanvasController(AppTemplate initApp) {
        app = initApp;
    }
    int[] startingCordinate= null;
    /**
     * Respond to mouse presses on the rendering surface, which we call canvas,
     * but is actually a Pane.
     */
    
    public void processCanvasMousePress(int x, int y) {
        startingCordinate= new int[2];
        m3Data dataManager = (m3Data) app.getDataComponent();
        if (dataManager.isInState(SELECTING_SHAPE)) {
            // SELECT THE TOP SHAPE
            Shape shape = dataManager.selectTopShape(x, y);
            Scene scene = app.getGUI().getPrimaryScene();

            // AND START DRAGGING IT
            if ((shape != null && ((CanvasItem) shape).isDraggable()) || (shape instanceof StationLabel && dataManager.lblMoveActive) ) {
                
                scene.setCursor(Cursor.MOVE);
                dataManager.setState(DRAGGING_SHAPE);
                startingCordinate[0] = x;
                startingCordinate[1] = y;  
                app.getGUI().updateToolbarControls(false);
            } else {
                scene.setCursor(Cursor.DEFAULT);
                dataManager.setState(DRAGGING_NOTHING);
                app.getWorkspaceComponent().reloadWorkspace(dataManager);
            }
//            if (dataManager.isInState(m3State.STARTING_IMAGE)) {
//            dataManager.startNewImage(x, y);
//            }
//            else if (dataManager.isInState(m3State.STARTING_LABEL)){
//            dataManager.startNewLabel();
//            }
            
        }
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        workspace.reloadWorkspace(dataManager);
    }

    /**
     * Respond to mouse dragging on the rendering surface, which we call canvas,
     * but is actually a Pane.
     */
    public void processCanvasMouseDragged(int x, int y) {
        
        m3Data dataManager = (m3Data) app.getDataComponent();
        
                
        if (dataManager.isInState(DRAGGING_SHAPE)) {
            dataManager.redrawLines();
            Draggable selectedDraggableShape = (Draggable) dataManager.getSelectedShape();
            selectedDraggableShape.drag(x, y);
            app.getGUI().updateToolbarControls(false);
        }
    }

    /**
     * Respond to mouse button release on the rendering surface, which we call canvas,
     * but is actually a Pane.
     */
    public void processCanvasMouseRelease(int x, int y) {
        
        m3Data dataManager = (m3Data) app.getDataComponent();
        
        
         if (dataManager.isInState(m3State.DRAGGING_SHAPE)) {
            dataManager.setState(SELECTING_SHAPE);
            Scene scene = app.getGUI().getPrimaryScene();
            scene.setCursor(Cursor.DEFAULT);
            app.getGUI().updateToolbarControls(false);
            Draggable selectedShape = (Draggable) dataManager.getSelectedShape();
            int startedX = startingCordinate[0];
            int startedY = startingCordinate[1];
            app.getGUI().justAddTransaction(new jtps.jTPS_Transaction() {
                @Override
                public void doTransaction() {
                
                     selectedShape.setLocationAndSize(selectedShape.getX(), selectedShape.getY(), 0, 0); 
                                          dataManager.redrawLines();
                }

                @Override
                public void undoTransaction() {
               
                     selectedShape.setLocationAndSize(startedX, startedY, 0, 0);
                     dataManager.redrawLines();
                }
            });
            if (dataManager.getSelectedShape() instanceof StationLabel){
                StationLabel stationlbl = (StationLabel) dataManager.getSelectedShape();
                stationlbl.recalculateOffSet();
            }
            
        } else if (dataManager.isInState(m3State.DRAGGING_NOTHING)) {
            dataManager.setState(SELECTING_SHAPE);
        }
    }
}
