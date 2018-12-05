package m3.gui;

import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javax.imageio.ImageIO;
import djf.AppTemplate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import jtps.jTPS_Transaction;
import m3.data.CanvasItem;
import m3.data.TrainHandle;
import m3.data.TrainLine;
import m3.data.m3Data;
import m3.data.m3State;
//import javafx.jjson.JsonObject;


/**
 * This class responds to interactions with the rendering surface.
 *
 * @author Rahel Zewde
 * @version 1.0
 */

public class m3EditController {
     AppTemplate app;
     m3Data dataManager;

 public m3EditController(AppTemplate initApp) {
        app = initApp;
        dataManager= (m3Data)app.getDataComponent();
//        AppDataComponent datacomp =  app.getDataComponent();
//        datacomp.resetData();
}
 
     public void processSelectImageToDraw() {
              // CHANGE THE CURSOR
              Scene scene = app.getGUI().getPrimaryScene();
              scene.setCursor(Cursor.CROSSHAIR);
              // CHANGE THE STATE
              dataManager.setState(m3State.STARTING_IMAGE);
              // ENABLE/DISABLE THE PROPER BUTTONS
              m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
              workspace.reloadWorkspace(dataManager);
    }
    
    public void processSnapshot(){
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
            Pane canvas = workspace.getCanvas();
            WritableImage image = canvas.snapshot(new SnapshotParameters(), null);
            File file = new File("metromap.png");
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    
    public void processLineEdit() {
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        m3Data dataMan= (m3Data) app.getDataComponent();
        Shape selectedShape= dataManager.getSelectedShape();
        if (selectedShape == null) {
           return;
        }
        TrainLine selectedTrack = (TrainLine) dataManager.getSelectedShape();
        TextInputDialog dialog = new TextInputDialog("Edit Selected Line");
        dialog.setTitle("Edit Line");
        dialog.setHeaderText("Change Line Color");
        dialog.setContentText("Change Line name");
        ColorPicker p= new ColorPicker();
        dialog.setGraphic(p);
        Optional<String> result = dialog.showAndWait();
        Color oldColor= selectedTrack.color;
        Color newColor= p.getValue();
        String oldName= selectedTrack.lineName;
        String selectedItemType = ((CanvasItem) dataManager.getSelectedShape()).itemType();
        
         jTPS_Transaction transaction= new jTPS_Transaction(){
         @Override
         public void doTransaction(){
         if (selectedItemType.equals("track")){
            selectedTrack.color= newColor;
            selectedTrack.setStroke(newColor);
            if(result.isPresent()){
                selectedTrack.lineName=result.get();
                dataMan.renameLine(selectedTrack,result.get());
                selectedTrack.setHandleText(result.get());
               // changeHandleTextfromMap( oldName,result.get(), dataManager.handleMap);
            }
           }
        }
         @Override
         public void undoTransaction() {
            System.err.println("trying" + oldColor); 
            System.err.println("trying" + oldName); 
            //selectedTrack.lineName= oldName;
            selectedShape.setStroke(oldColor);
            dataMan.renameLine(selectedTrack, oldName);
            selectedTrack.setHandleText(oldName);
            //changeHandleTextfromMap( selectedTrack.lineName,oldName, dataManager.handleMap);
//            selectedTrack.getStartHandle(dataManager.handleMap).setTextAndLine(oldName);
//            selectedTrack.getEndHandle(dataManager.handleMap).setTextAndLine(oldName);
            dataManager.setSelectedShape(selectedShape);
            app.getGUI().updateToolbarControls(false);
         }

//            private void changeHandleTextfromMap(String oldName, String newName, HashMap<String, TrainHandle> handleMap) {
//                Iterator it = handleMap.entrySet().iterator();
//                while (it.hasNext()) {
//                    HashMap.Entry pair = (HashMap.Entry)it.next();
//                    if(pair.get())
//                    System.out.println(pair.getKey() + " = " + pair.getValue());
//                   
//    }
//            }
         };
         app.getGUI().addAndDoTransaction(transaction);
    }
    
    

}
 
 
