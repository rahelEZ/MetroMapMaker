package m3.data;

import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import djf.components.AppDataComponent;
import djf.AppTemplate;
import static djf.settings.AppPropertyType.LOAD_ERROR_MESSAGE;
import static djf.settings.AppPropertyType.LOAD_ERROR_TITLE;
import static djf.settings.AppStartupConstants.PATH_WORK;
import djf.ui.AppMessageDialogSingleton;
import djf.ui.Log;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import static m3.data.m3State.*;
import m3.gui.m3Workspace;
import properties_manager.PropertiesManager;

/**
 * This class serves as the data management component for this application.
 *
 * @author Rahel Zewde
 * @author ?
 * @version 1.0
 */
public class m3Data implements AppDataComponent {

    public static Color DEFAULT_BACKGROUND_COLOR = Color.LIGHTGRAY;


    AppTemplate app;
    
    
    
    
    m3State state;
    
    
        Shape newShape;
        
      public  boolean lblMoveActive = false;

    
    public HashMap<String, DraggableStation> stationMap;
    public HashMap<String, TrainLine> trackMap;
    public HashMap<String, TrainHandle> handleMap;
    
    
    
    
    
    public ObservableList<DraggableStation> stationList;
    public ObservableList<DraggableLabel> labelList;
    public ObservableList<DraggableImage> imageList;
    
    public ObservableList<TrainHandle> handlesList; 
    
    
    
    
    public String title = "";
    
    public ObservableList<TrainLine> trackList;
    
    ObservableList<Node> shapes;
    Effect highlightedEffect;
    public Image backgroundImage;
     public String getTitle(){
         return title;
     }
     
     public void setTitle(String title){
         this.title= title;
     }
      
    public Shape getNewShape() {
	return newShape;
    }

    public Shape getSelectedShape() {
	return selectedShape;
    }

    
    public void unselectShape(){
        if (selectedShape != null){
           unhighlightShape(selectedShape);
        }
    }
    public void setSelectedShape(Shape initSelectedShape) {
	selectedShape = initSelectedShape;
        lblMoveActive = !lblMoveActive ? false : (selectedShape  instanceof StationLabel );
    }
    
    
    public void renameStation(DraggableStation station, String newName){
        for (TrainLine line: station.rails){
            int index = 0;
            for(String stationName :  line.stationsNames){
                if (stationName.equals(station.name)){
                    
                }
                index++;
            }
        }
        deleteStation(station);
        station.rename(newName);
        addStation(station);
        redrawLines();
    }
    
    public void renameLine(TrainLine line, String newName){
        
        deleteLine(line);
        line.lineName = newName;
        
        if (line.startHandle!=null && line.endHandle!=null){
            TrainHandle startHandle = handleMap.get(TrainHandle.trackStartHandleName(line.lineName));
            TrainHandle endHandle = handleMap.get(TrainHandle.trackEndHandleName(line.lineName));
            addHandle(startHandle);
            addHandle(endHandle);
        }
        line.prepareStations(stationMap);
        
        redrawLines();
        checkMaps(line,line.endHandle);
    }
    
    public void checkMaps(TrainLine train, TrainHandle handle){
        
        for(Map.Entry<String, TrainLine> entry : trackMap.entrySet()) {
            String lineName = entry.getKey();
            TrainLine line = entry.getValue();
            if(lineName.equals(train.lineName)){
                trackMap.remove(line);
            }
           
       }
        trackMap.put(train.lineName, train);
         for(Map.Entry<String, TrainHandle> entry : handleMap.entrySet()) {
            String lineName = entry.getKey();
            TrainHandle thisHandle = entry.getValue();
            if(lineName.equals(handle.name)){
                handleMap.remove(thisHandle);
            }
       }
       handleMap.put(handle.name, handle);
        
    }
   
    public m3Data(AppTemplate initApp) {
	// KEEP THE APP FOR LATER
	app = initApp;
        stationMap = new HashMap<String, DraggableStation>();
        trackMap = new HashMap<String, TrainLine>();
        handleMap = new HashMap<String, TrainHandle>();
        
        
        
        stationList = FXCollections.observableArrayList();
        trackList = FXCollections.observableArrayList();
        labelList= FXCollections.observableArrayList();
        imageList= FXCollections.observableArrayList();
        handlesList = FXCollections.observableArrayList();
	selectedShape = null;
        newShape = null;
	
        
        DropShadow dropShadowEffect = new DropShadow();
	dropShadowEffect.setOffsetX(0.0f);
	dropShadowEffect.setOffsetY(0.0f);
	dropShadowEffect.setSpread(1.0);
	dropShadowEffect.setColor(Color.YELLOW);
	dropShadowEffect.setBlurType(BlurType.GAUSSIAN);
	dropShadowEffect.setRadius(15);
	highlightedEffect = dropShadowEffect;
    }
    
    @Override
    public void resetData() {
        
	setState(SELECTING_SHAPE);
        selectedShape = null;
        newShape = null;
      lblMoveActive = false;

        stationMap.clear(); trackMap.clear(); handleMap.clear(); stationList.clear();labelList.clear();imageList.clear();handlesList.clear(); 



        app.getWorkspaceComponent().updateLists(this);

        shapes.clear();
     title = "";trackList.clear();
     
     this.bgPath = "";
     this.backgroundColor = DEFAULT_BACKGROUND_COLOR;
     this.backgroundImage = null;
     this.pathArt = null;
     
     
        app.getWorkspaceComponent().updateCanvas(this);
        
    }
    
    
    
    public void reloadWorkspace(){
        app.getWorkspaceComponent().reloadWorkspace(this);
    }
    
    public void redrawLines(){
        
        for (int i = 0; i < trackList.size(); i++) {
           TrainLine line = (TrainLine) trackList.get(i);
            line.drawPath(stationMap); 
           
           Log.d("redrating");
             
        }   
    }
    public void setShapes(ObservableList<Node> shapes){
       this.shapes = shapes; 
        stationList = FXCollections.observableArrayList();
        trackList = FXCollections.observableArrayList();
    }
    
    
    public void addStation(DraggableStation station){
        stationMap.put(station.name, station);
        this.stationList.add(station);
        this.shapes.add(station);
        
        this.shapes.add(station.stationLabel);
        app.getWorkspaceComponent().updateLists(this);
        
    }
    public void deleteStation(DraggableStation station){
        stationMap.remove(station.name);
        this.stationList.remove(station);
        this.shapes.remove(station);
        this.shapes.remove(station.stationLabel);
        reloadWorkspace();
        for (int i = 0; i < this.trackList.size(); i++) {
            TrainLine tempTrack = this.trackList.get(i);
            
            for (int j = 0; j < tempTrack.stationsNames.size(); j++) {
                if (tempTrack.stationsNames.get(j).equals(station.name)){
                    tempTrack.stationsNames.remove(j);
                    j--;
                }
            }
            tempTrack.prepareStations(stationMap);
        }
        redrawLines();
        app.getWorkspaceComponent().updateLists(this);
    }
    
    
    
    public void prepareTransfers(){
        
        
        for (TrainLine clearable: this.trackList){
            clearable.transferTo.clear();
            clearable.transferToName.clear();
            
            
            for (String stationName :  clearable.stationsNames){
                clearable.stations.add(stationMap.get(stationName));
                
            }
            
        }
        
          for (TrainLine line: this.trackList) {
            for (DraggableStation station: line.stations){// j = 0; j < line.stations.length; j++) {
                for (TrainLine insideLine : station.rails){
                    line.addTransfer(insideLine);
                }
            }
            line.prepareStations(stationMap);
        }
    }
    public void addHandle(TrainHandle handle){
        this.shapes.add(handle);
        this.handlesList.add(handle);
        this.handleMap.put(handle.name, handle);
    }
    public void addLine(TrainLine line){
        this.shapes.add(0,line);
        this.trackList.add(line);
        this.trackMap.put(line.lineName, line);
        
        line.prepareStations(stationMap);
        line.drawPath(stationMap);
        app.getWorkspaceComponent().updateLists(this);
    }
    
   
    public void deleteLine(TrainLine line){
        
        TrainHandle startHandle = handleMap.get(TrainHandle.trackStartHandleName(line.lineName));
        TrainHandle endHandle = handleMap.get(TrainHandle.trackEndHandleName(line.lineName));
        
        for (DraggableStation station: line.stations){
            station.rails.remove(line);
        }
        
        if (startHandle!=null){
            shapes.remove(startHandle);
            this.handleMap.remove(startHandle.name);
            this.handlesList.remove(startHandle);
        }
        if (endHandle!=null){
            shapes.remove(endHandle);
            this.handleMap.remove(endHandle.name);
            this.handlesList.remove(endHandle);
         
        }
        
        this.shapes.remove(line);
        this.trackList.remove(line);
        this.trackMap.remove(line.lineName);
        app.getWorkspaceComponent().updateLists(this);
    }
    
    
    
    public void removeStation(TrainLine trainLine, DraggableStation draggableStation){
        Log.d("trying delete");
        for (int j = 0; j < trainLine.stationsNames.size(); j++) {
                if (trainLine.stationsNames.get(j).equals(draggableStation.name)){
                    trainLine.stationsNames.remove(j);
                    j--;
                }
            }
        
        redrawLines();
        app.getWorkspaceComponent().updateLists(this);
    }
    
    public void selectSizedShape() {
        if (selectedShape != null) {
            unhighlightShape(selectedShape);
        }
        setSelectedShape(newShape);
        highlightShape(selectedShape);

        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
        workspace.loadSelectedShapeSettings(newShape);
        newShape = null;
        if (state == SIZING_SHAPE) {
            Draggable shape = ((Draggable) selectedShape);state = shape.getStartingState();

            if (shape.getShapeType().equals(Draggable.IMAGE)) {

                DraggableImage thisImage = (DraggableImage) shape;

                PropertiesManager props = PropertiesManager.getPropertiesManager();

                // AND NOW ASK THE USER FOR THE FILE TO OPEN
                FileChooser fc = new FileChooser();
                fc.setInitialDirectory(new File(PATH_WORK));
                fc.setTitle(props.getProperty("Select an Image"));
                File selectedFile = fc.showOpenDialog(app.getGUI().getWindow());

                try {
                    // ONLY OPEN A NEW FILE IF THE USER SAYS OK
                    if (selectedFile != null) {
                        if (!thisImage.loadImage(selectedFile.toURI().toURL().toExternalForm())){
                            setSelectedShape(selectedShape);
                        }
                    }
                        else {
                        setSelectedShape(selectedShape);
                    }
                } catch (Exception ex) {

                    AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
                    dialog.show(props.getProperty(LOAD_ERROR_TITLE), props.getProperty(LOAD_ERROR_MESSAGE));
                }

            } else if (shape.getShapeType().equals(Draggable.LABEL)) {
                DraggableLabel thisText = (DraggableLabel) shape;

                PropertiesManager props = PropertiesManager.getPropertiesManager();

                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle(props.getProperty("Label"));
                dialog.setHeaderText(props.getProperty("Enter a label"));
                dialog.initOwner(app.getGUI().getWindow());


                // Traditional way to get the response value.
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent() && !result.get().trim().equals("")) {
                    thisText.setText(result.get().trim());
                    thisText.setFont(Font.font("Verdana"));
                } else {
                    setSelectedShape(selectedShape);
                    removeSelectedShape();
                }

            }
            final Shape shapeToAdd = selectedShape;

            addShape(shapeToAdd);
            app.getGUI().updateToolbarControls(false);
            app.getGUI().updateToolbarControls(false);

        }
    }
    public void addShape(Shape shapeToAdd) {
        shapes.add(shapeToAdd);
    }

    public void addStation(TrainLine trainLine, DraggableStation draggableStation){
        boolean alreadyExists = false;
            for (int j = 0; j < trainLine.stationsNames.size(); j++) {
                if (trainLine.stationsNames.get(j).equals(draggableStation.name)){
                    alreadyExists = true;
                    break;
                }
            }
            if (!alreadyExists){
                trainLine.stationsNames.add(draggableStation.name);
            }
            
            redrawLines();
    }
    
    
    public void startNewImage(int x, int y) {
        DraggableImage newImage = new DraggableImage();
        newImage.justCreated = true;
        newImage.start(x, y);
        newShape = newImage;
        initNewShape();
    }
    
    

    
    
    public void addImage(DraggableImage image) {
        this.imageList.add(image);
        this.shapes.add(image);
    }

    public void addLabel(DraggableLabel label) {
        this.labelList.add(label);
        this.shapes.add(label);
    }
//    public void startNewLabel(int x, int y) {
//        DraggableLabel draggableLabel = new DraggableLabel();
//        draggableLabel.justCreated = true;
//        draggableLabel.start(x, y);
//        newShape = draggableLabel;
//        initNewShape();
//
//    }
    public void initNewShape() {
        // DESELECT THE SELECTED SHAPE IF THERE IS ONE
        if (selectedShape != null) {
            unhighlightShape(selectedShape);
            setSelectedShape(null);
        }

        // USE THE CURRENT SETTINGS FOR THIS NEW SHAPE
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
//
//        if (!((Draggable) newShape).getShapeType().equals(Draggable.LABEL)) {
//            newShape.setFill();
//            newShape.setStroke(workspace.getOutlineColorPicker().getValue());
//            newShape.setStrokeWidth(workspace.getOutlineThicknessSlider().getValue());
//
//        }

        // ADD THE SHAPE TO THE CANVAS
        shapes.add(newShape);
        state = m3State.SIZING_SHAPE;

    }
    
    Shape selectedShape;

    
    public Shape selectTopShape(int x, int y) {
	Shape shape = getTopShape(x, y);
	if (shape == selectedShape)
	    return shape;
	
	if (selectedShape != null) {
	    unhighlightShape(selectedShape);
	}
	if (shape != null) {
            System.err.println("selected soemthing");
	    highlightShape(shape);
            m3Workspace workspace = (m3Workspace)app.getWorkspaceComponent();
	    workspace.loadSelectedShapeSettings(shape);
	}
	selectedShape = shape;
	if (shape != null && shape instanceof Draggable) {
	    ((Draggable)shape).start(x, y);
	}
	return shape;
    }
    
      public void unhighlightShape(Shape shape) {
          if (shape==null) return;
	selectedShape.setEffect(null);
    }
    
    public void highlightShape(Shape shape) {
	shape.setEffect(highlightedEffect);
    }
    
    
    public Shape getTopShape(int x, int y) {
	for (int i = shapes.size() - 1; i >= 0; i--) {
	    Shape shape = (Shape)shapes.get(i);
	    if (shape.contains(x, y)) {
		return shape;
	    }
	}
	return null;
    }

    public m3State getState() {
	return state;
    }

    public void setState(m3State initState) {
	state = initState;
    }

    public boolean isInState(m3State testState) {
	return state == testState;
    }

    public void removeSelectedShape() {
        if (selectedShape != null) {
            shapes.remove(selectedShape);
            setSelectedShape(null);
        }
    }

    public Color backgroundColor = m3Data.DEFAULT_BACKGROUND_COLOR;
    public void setBackGroundColor(Color value) {
        backgroundColor = value;
        backgroundImage = null;
        System.err.println(value.toString());
        app.getWorkspaceComponent().updateCanvas(this);
    }

    public void deleteLabel(DraggableLabel draggableLabel) {
        this.labelList.remove(draggableLabel);
        this.shapes.remove(draggableLabel);
        reloadWorkspace();
    }

    public void deleteImage(DraggableImage draggableImage) {
        this.imageList.remove(draggableImage);
        this.shapes.remove(draggableImage);
        reloadWorkspace();
    }
    
    
    PathGraphics pathArt;
    public void plotPath(PathGraphics pathArt) {
            
        if (this.pathArt!=null){
            
            this.shapes.remove(this.pathArt);
        }
            this.pathArt = pathArt;
        
        
        this.shapes.add(this.pathArt);
    }

    
    
    
    
    public String bgPath;
    public void setBackGroundImage(String toExternalForm) {
        if (toExternalForm == null || toExternalForm.equals("")) return;
        Image background = new Image(toExternalForm);
        this.backgroundImage = background;
        bgPath = toExternalForm;
        
        app.getWorkspaceComponent().updateCanvas(this);
    }

    public void removePath() {
        if (this.pathArt!=null){
            
            this.shapes.remove(this.pathArt);
        }
        this.pathArt = null;
    }

    @Override
    public void resetData(String get) {
        this.resetData();;
        this.title = get;
    }


    
    

    
}

