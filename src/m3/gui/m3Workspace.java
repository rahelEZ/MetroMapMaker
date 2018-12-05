package m3.gui;

import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import static m3.m3LanguageProperty.*;
import djf.ui.AppYesNoCancelDialogSingleton;
import djf.ui.AppMessageDialogSingleton;
import djf.ui.AppGUI;
import djf.AppTemplate;
import djf.components.AppDataComponent;
import djf.components.AppWorkspaceComponent;
import djf.controller.AppFileController;
import djf.ui.AppAboutDialog;
import djf.ui.AppNavigationView;
import djf.ui.AppStationReorderDialog;
import java.io.File;
import java.net.MalformedURLException;
//import static m3.css.*;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import jtps.PostTransaction;
import jtps.jTPS;
import jtps.jTPS_Transaction;
import static m3.css.m3Style.CLASS_EDIT_TOOLBAR;
import static m3.css.m3Style.CLASS_EDIT_TOOLBAR_ROW;
import static m3.css.m3Style.CLASS_RENDER_CANVAS;
import m3.data.CanvasItem;
import m3.data.DraggableImage;
import m3.data.DraggableStation;
import m3.data.TrainLine;
import m3.data.m3Data;
import m3.data.DraggableLabel;
import m3.data.PathGraphics;
import m3.data.StationLabel;
import m3.data.m3Path;
import properties_manager.PropertiesManager;

/**
 * This class responds to interactions with the rendering surface.
 *
 * @author Rahel Zewde
 * @version 1.0
 */

public class m3Workspace extends AppWorkspaceComponent {
    // HERE'S THE APP

    AppTemplate app;

    // IT KNOWS THE GUI IT IS PLACED INSIDE
    AppGUI gui;

    Pane grid;

    //TOP BAR
    VBox section1;
    BorderPane section1A;
    HBox section1B;
    Label metroLineLabel;
    ComboBox lineCombo;
    ColorPicker lineColorPicker;
    Button addStation;
    Label addStationLabel;
    Button removeStation;
    Label removeStationLabel;
    Button listStations;
    Button editLine;
    Slider thicknessControl;
    Button deleteTrack;
    Button createTrack;
    //METROSTATIONS TOOLBAR
    VBox section2;
    BorderPane section2A;
    HBox section2B;
    Label metroStationLabel;
    ComboBox stationCombo;
    Button createStation;
    Button deleteStation;
    ColorPicker stationColorPicker;
    Button snap;
    ToggleButton moveLabel;
    Label moveLabelLabel;
    Button rotateLabel;
    Button editStation;
    Slider radiusControl;
    //ROUTE TOOLBAR
    BorderPane section3;
    VBox section3A;
    VBox section3B;
    ComboBox stationFrom;
    ComboBox stationTo;
    Button findRoute;
    //DECOR TOOLBAR
    VBox section4;
    BorderPane section4A;
    HBox section4B;
    Label decorLabel;
    ColorPicker decorColorPicker;
    Button setImagebg;
    Label setImagebgLabel;
    Button addImage;
    Label addImageLabel;
    Button addLabel;
    Label addLabelLabel;
    Button removeElement;
    Label removeElementLabel;
    //FONT TOOLBAR
    VBox section5;
    HBox section5A;
    HBox section5B;
    Label fontLabel;
    ColorPicker fontColorPicker;
    ToggleButton bold;
    ToggleButton italics;
    ComboBox<Integer> fontSize;
    ComboBox<String> fontType;
    //NAVIGATION TOOL BAR
    VBox section6;
    HBox section6A;
    HBox section6B;
    Label navigationLabel;
    CheckBox showGrid;
    Label showGridLabel;
    Button zoomIn;
    Button zoomOut;
    Button mapZoomIn;
    Button mapZoomOut;
    //WHOLE EDIT TOOL BAR
    VBox leftEditToolBar;
    ScrollPane leftEditBarWrapper;
    //HERE ARE OUR CONTROLLERS
    m3EditController m3EditController;
    AppFileController appFileController;
    CanvasController canvasController;
    // HERE ARE OUR DIALOGS
    AppMessageDialogSingleton messageDialog;
    AppYesNoCancelDialogSingleton yesNoCancelDialog;
    public String backgroundLabel = "";
    // FOR DISPLAYING DEBUG STUFF
    Text debugText;
    Pane canvas;
    ScrollPane canvasWrap;
    StackPane workSpaceWrapper;

    Group scrollContent;

    StackPane canvasPane;

    /**
     * Constructor for initializing the workspace, note that this constructor
     * will fully setup the workspace user interface for use.
     *
     * @param initApp The application this workspace is part of.
     *
     * @throws IOException Thrown should there be an error loading application
     * data for setting up the user interface.
     */
    public m3Workspace(AppTemplate initApp) {
        // KEEP THIS FOR LATER
        app = initApp;

        // KEEP THE GUI FOR LATER
        gui = app.getGUI();

        // LAYOUT THE APP
        initLayout();

        // HOOK UP THE CONTROLLERS
        initControllers();

        // AND INIT THE STYLE FOR THE WORKSPACE
        initStyle();

        gui.actor = new jTPS(new PostTransaction() {
            @Override
            public void run(boolean redo, boolean undo) {
                gui.undoButton.setDisable(!undo);
                gui.redoButton.setDisable((!redo));
                gui.saveAsButton.setDisable(false);
                gui.saveButton.setDisable(false);
            }
        });

    }

    /**
     * Note that this is for displaying text during development.
     */
    public void setDebugText(String text) {
        debugText.setText(text);
    }

    //HELPER SETUP METHOD
    private void initLayout() {
        //FILE TOOL BAR 

        System.err.println("called");

        //LEFT SIDE OF THE WORKSPACE
        leftEditToolBar = new VBox();
        leftEditBarWrapper = new ScrollPane(leftEditToolBar);
        leftEditBarWrapper.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        //METRO LINES TOOL BAR

        section1 = new VBox();
        section1A = new BorderPane();
        section1B = new HBox();

        metroLineLabel = new Label(props.getProperty(METRO_LINE_LABEL.toString()));

        lineCombo = new ComboBox<>();
        lineColorPicker = new ColorPicker(Color.valueOf("blue"));

        section1A.setLeft(metroLineLabel);
        section1A.setCenter(lineCombo);
        section1A.setRight(lineColorPicker);

        createTrack = gui.initChildButton(section1B, CREATE_STATION_ICON.toString(), CREATE.toString(), false);
        deleteTrack = gui.initChildButton(section1B, DELETE_STATION_ICON.toString(), DELETE.toString(), false);
        addStation = gui.initChildButton(section1B, ADD_STATION_ICON.toString(), ADD_STATION_LABEL.toString(), false);
        removeStation = gui.initChildButton(section1B, REMOVE_STATION_ICON.toString(), REMOVE_STATION_LABEL.toString(), false);
        listStations = gui.initChildButton(section1B, LIST_STATIONS_ICON.toString(), LIST_STATIONS_TOOLTIP.toString(), false);
        editLine = gui.initChildButton(section1, EDIT.toString(), EDIT_TEXT.toString(), false);

        thicknessControl = new Slider(1, 15, TrainLine.DEFAULT_TICKNESS);
        thicknessControl.setMajorTickUnit(1);
        thicknessControl.setShowTickMarks(true);

        thicknessControl.setShowTickLabels(true);
        thicknessControl.setSnapToTicks(true);
        thicknessControl.setMinHeight(Slider.USE_PREF_SIZE);

        section1.getChildren().add(section1A);
        section1.getChildren().add(section1B);
        section1.getChildren().add(thicknessControl);

        //METRO STATIONS TOOL BAR
        section2 = new VBox();
        section2A = new BorderPane();
        section2B = new HBox();

        metroStationLabel = new Label(props.getProperty(METRO_STATION_LABEL.toString()));
        stationCombo = new ComboBox<>();
        stationColorPicker = new ColorPicker(Color.valueOf("yellow"));

        section2A.setLeft(metroStationLabel);
        section2A.setCenter(stationCombo);
        section2A.setRight(stationColorPicker);

        createStation = gui.initChildButton(section2B, CREATE_STATION_ICON.toString(), CREATE.toString(), false);
        deleteStation = gui.initChildButton(section2B, DELETE_STATION_ICON.toString(), DELETE.toString(), false);

        moveLabel = gui.initToogleButton(section2B, MOVE.toString(), MOVE_LABEL_LABEL.toString(), false);

        rotateLabel = gui.initChildButton(section2B, ROTATE.toString(), ROTATE_LABEL_TOOLTIP.toString(), false);
        editStation = gui.initChildButton(section2B, EDIT.toString(), EDIT_TEXT.toString(), false);
        
        
        radiusControl = new Slider();
        radiusControl = new Slider(5, 15, DraggableStation.STATION_RAIDUS);
        radiusControl.setMajorTickUnit(1);
        radiusControl.setShowTickMarks(true);

        radiusControl.setShowTickLabels(true);
        radiusControl.setSnapToTicks(true);
        radiusControl.setMinHeight(Slider.USE_PREF_SIZE);

        section2.getChildren().add(section2A);
        section2.getChildren().add(section2B);
        section2.getChildren().add(radiusControl);

        //ROUTE TOOLBAR
        section3 = new BorderPane();
        section3A = new VBox();
        section3B = new VBox();

        stationFrom = new ComboBox();
        section3A.getChildren().add(stationFrom);
        stationTo = new ComboBox();
        section3A.getChildren().add(stationTo);
        findRoute = gui.initChildButton(section3B, FIND_ROUTE_ICON.toString(), FIND_ROUTE_ICON.toString(), false);
        section3.setCenter(section3A);
        section3.setRight(section3B);

        //DECOR TOOLBAR
        section4 = new VBox();
        section4A = new BorderPane();
        section4B = new HBox();

        decorLabel = new Label(props.getProperty(DECOR_LABEL.toString()));
        decorColorPicker = new ColorPicker(Color.valueOf("green"));

        section4A.setLeft(decorLabel);
        section4A.setRight(decorColorPicker);

        setImagebg = gui.initChildButton(section4B, BACKGROUND_IMAGE.toString(), SET_IMAGE_BACKGROUND_LABEL.toString(), false);
        addImage = gui.initChildButton(section4B, ADD_IMAGE.toString(), ADD_IMAGE_LABEL.toString(), false);
        addLabel = gui.initChildButton(section4B, ADD_TEXT.toString(), ADD_LABEL_LABEL.toString(), false);
        removeElement = gui.initChildButton(section4B, DELETE_STATION_ICON.toString(), REMOVE_ELEMENT_LABEL.toString(), true);

        section4.getChildren().add(section4A);
        section4.getChildren().add(section4B);

        //FONT TOOL BAR
        section5 = new VBox();
        section5A = new HBox();
        section5B = new HBox();

        fontColorPicker = new ColorPicker(Color.valueOf("Red"));

        fontSize = new ComboBox();
        fontType = new ComboBox();
        section5B.getChildren().add(fontColorPicker);
        bold = gui.initToogleButton(section5B, BOLD.toString(), TOGGLE_BOLD.toString(), true);
        italics = gui.initToogleButton(section5B, ITALIC.toString(), TOGGLE_ITALIC.toString(), true);

        fontType.prefHeightProperty().bind(bold.heightProperty());
        fontColorPicker.prefHeightProperty().bind(bold.heightProperty());
        fontSize.prefHeightProperty().bind(bold.heightProperty());

        for (int i = 1; i < 51; i++) {
            fontSize.getItems().add((i * 2) + 1);
        }
        String[] fonts = "Times new Roman,Verdana,Arial,Courier,Helvetica".split(",");
        for (String font : fonts) {
            fontType.getItems().add(font);
        }

        section5B.getChildren().add(fontSize);
        section5B.getChildren().add(fontType);

        section5.getChildren().add(section5A);
        section5.getChildren().add(section5B);

        //NAVIGATION BAR
        section6 = new VBox();
        section6A = new HBox();
        section6B = new HBox();

        navigationLabel = new Label(props.getProperty(NAVIGATION_LABEL.toString()));
        showGridLabel = new Label(props.getProperty(SHOW_GRID_LABEL.toString()));
        showGrid = new CheckBox();

        section6A.getChildren().add(navigationLabel);
        section6A.getChildren().add(showGridLabel);
        section6A.getChildren().add(showGrid);

        zoomIn = gui.initChildButton(section6B, ZOOM_IN_ICON.toString(), ZOOM_IN_TOOLTIP.toString(), false);
        zoomOut = gui.initChildButton(section6B, ZOOM_OUT_ICON.toString(), ZOOM_OUT_TOOLTIP.toString(), false);
        mapZoomIn = gui.initChildButton(section6B, MAP_ZOOM_IN_ICON.toString(), MAP_ZOOM_IN_TOOLTIP.toString(), false);
        mapZoomOut = gui.initChildButton(section6B, MAP_ZOOM_OUT_ICON.toString(), MAP_ZOOM_OUT_TOOLTIP.toString(), false);
        snap = gui.initChildButton(section6B, SNAP.toString(), SNAP_LABEL.toString(), false);

        section6.getChildren().add(section6A);
        section6.getChildren().add(section6B);

        leftEditToolBar.getChildren().add(section1);
        leftEditToolBar.getChildren().add(section2);
        leftEditToolBar.getChildren().add(section3);
        leftEditToolBar.getChildren().add(section4);
        leftEditToolBar.getChildren().add(section5);
        leftEditToolBar.getChildren().add(section6);

        // WE'LL RENDER OUR STUFF HERE IN THE CANVAS
        canvas = new Pane();
        debugText = new Text();
        canvas.getChildren().add(debugText);

        grid = new Pane();
        grid.setVisible(false);

        // AND MAKE SURE THE DATA MANAGER IS IN SYNCH WITH THE PANE
        m3Data data = (m3Data) app.getDataComponent();
        data.setShapes(canvas.getChildren());

        // AND NOW SETUP THE WORKSPACE
        workspace = new BorderPane();

        final StackPane zoomPane = new StackPane();
        zoomPane.getChildren().add(canvas);

        final Group zoomContent = new Group(zoomPane);

        // Create a pane for holding the content, when the content is smaller than the view port,
        // it will stay the view port size, make sure the content is centered
        canvasPane = new StackPane();
        canvasPane.getChildren().add(zoomContent);

        scrollContent = new Group(canvasPane);
        // Scroll pane for scrolling
        canvasWrap = new ScrollPane();
        canvasWrap.setContent(scrollContent);

        zoomPane.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {

                zoom(canvas, event);
//              if (event.getDeltaY() == 0) {
//                return;
//              }
//
//              double scaleFactor =
//                (event.getDeltaY() > 0)
//                  ? SCALE_DELTA
//                  : 1/SCALE_DELTA;
//
//              canvas.setScaleX(canvas.getScaleX() * scaleFactor);
//              canvas.setScaleY(canvas.getScaleY() * scaleFactor);
//              
//              
//              int newX = (int)(event.getX()*(0.9f - 1f) + 0.9f*event.getX());
//    int newY = (int)(event.getY()*(0.9f - 1f) + 0.9f*event.getY());
//    stackPane.getV.setViewPosition(new Point(newX, newY));
//
//    this.imagePanel.revalidate();
//    this.imagePanel.repaint();
//              
            }
        });

        zoomPane.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable, Bounds oldBounds, Bounds bounds) {
                zoomPane.setClip(new Rectangle(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight()));
            }
        });

        canvasWrap.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable,
                    Bounds oldValue, Bounds newValue) {
                zoomPane.setMinSize(newValue.getWidth(), newValue.getHeight());
            }
        });

        workSpaceWrapper = new StackPane();
        workSpaceWrapper.getChildren().add(canvasWrap);
        workSpaceWrapper.getChildren().add(grid);

        ((BorderPane) workspace).setLeft(leftEditBarWrapper);
        ((BorderPane) workspace).setCenter(workSpaceWrapper);

        app.stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            createGrid(grid);
        });
        app.stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            createGrid(grid);

        });

    }

    public double workSpaceWidth = 0;
    public double workSpaceHeight = 0;

    public static void zoom(Node node, double factor, double x, double y) {
        double oldScale = node.getScaleX();
        double scale = oldScale * factor;
        if (scale < 0.05) {
            scale = 0.05;
        }
        if (scale > 50) {
            scale = 50;
        }
        node.setScaleX(scale);
        node.setScaleY(scale);

        double f = (scale / oldScale) - 1;
        Bounds bounds = node.localToScene(node.getBoundsInLocal());
        double dx = (x - (bounds.getWidth() / 2 + bounds.getMinX()));
        double dy = (y - (bounds.getHeight() / 2 + bounds.getMinY()));

        node.setTranslateX(node.getTranslateX() - f * dx);
        node.setTranslateY(node.getTranslateY() - f * dy);

    }

    public static void focusAndCenter(Node node, double[] objectSize, double x, double y, double[] workspaceSize) {

//        System.err.println(workspaceSize[0] + "-" + workspaceSize[1]);
//        double scale = (objectSize[1]/workspaceSize[1]);
//        
//        if (objectSize[0]/workspaceSize[0] < objectSize[1]/workspaceSize[1]){
//            scale = objectSize[1]/workspaceSize[1];
//        }
//        
//        System.out.println(objectSize[0] + "dimensions" +  objectSize[1] + "scale" + scale);
//        
//          double oldScale = node.getScaleX();
//        
//        
//       
//    if (scale < 0.05) scale = 0.05;
//    if (scale > 50)  scale = 50;
//    node.setScaleX(scale);
//    node.setScaleY(scale);
        double f = 0;// (scale / oldScale)-1;
        Bounds bounds = node.localToScene(node.getBoundsInLocal());
        double dx = (x - (bounds.getWidth() / 2 + bounds.getMinX()));
        double dy = (y - (bounds.getHeight() / 2 + bounds.getMinY()));

        node.setTranslateX(node.getTranslateX() - f * dx);
        node.setTranslateY(node.getTranslateY() - f * dy);

    }

    public static void zoom(Node node, ScrollEvent event) {
        zoom(node, Math.pow(1.01, event.getDeltaY()), event.getSceneX(), event.getSceneY());
    }

    public static void zoom(Node node, ZoomEvent event) {
        zoom(node, event.getZoomFactor(), event.getSceneX(), event.getSceneY());
    }

    final double SCALE_DELTA = 1.1;

    private void initControllers() {
        // MAKE THE EDIT CONTROLLER
        m3EditController = new m3EditController(app);

        gui.aboutButton.setOnAction(e -> {
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            AppAboutDialog.getSingleton().show(props.getProperty(ABOUT_TOOLTIP));
        });

        // MAKE THE CANVAS CONTROLLER	
        canvasController = new CanvasController(app);
        canvas.setOnMousePressed(e -> {
            if (e.getClickCount() == 2 ) {
                

            } else {

                canvasController.processCanvasMousePress((int) e.getX(), (int) e.getY());
            }
        });
        canvas.setOnMouseReleased(e -> {
            canvasController.processCanvasMouseRelease((int) e.getX(), (int) e.getY());
        });
        canvas.setOnMouseDragged(e -> {
            canvasController.processCanvasMouseDragged((int) e.getX(), (int) e.getY());
        });

        m3Data dataMan = (m3Data) app.getDataComponent();

        createStation.setOnAction(e -> {

            int stationValidation = 0;

            boolean cancel = false;
            String stationName = "New Station";

            while (stationValidation >= 0 && !cancel) {
                TextInputDialog dialog = new TextInputDialog(stationName);
                dialog.setTitle("Add a station");
                dialog.setHeaderText("Add a station");
                dialog.setContentText(stationValidation > 0 ? "Invalid station Name. Please choose a different one" : "Station Name");
                dialog.initOwner(app.stage);
                // Traditional way to get the response value.
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    stationName = result.get();
                    if (stationName.trim().length() > 2 && dataMan.stationMap.get(stationName) == null) {
                        DraggableStation newStation = new DraggableStation(stationName);
                        newStation.setLocation(15, 15);

                        gui.addAndDoTransaction(new jTPS_Transaction() {
                            @Override
                            public void doTransaction() {

                                dataMan.addStation(newStation);
                            }

                            @Override
                            public void undoTransaction() {
                                dataMan.deleteStation(newStation);
                            }
                        });
                        stationValidation = -1;
                    } else {
                        stationValidation++;
                    }
                } else {
                    cancel = true;
                }

            }

        });
        createTrack.setOnAction(e -> {

            int trainValidation = 0;

            boolean isCircle = false;
            boolean cancel = false;
            String stationName = "New Line";

            while (trainValidation >= 0 && !cancel) {
                TextInputDialog dialog = new TextInputDialog(stationName);
                dialog.setHeaderText("Add a Line");
                dialog.setTitle(("Add a line"));
                dialog.setContentText(trainValidation > 0 ? "Invalid line Name. Please choose a different one" : "Line Name");
                dialog.initOwner(app.stage);

                CheckBox circularStation = new CheckBox("Is Circular");
                circularStation.setSelected(isCircle);
                dialog.setGraphic(circularStation);
                // Traditional way to get the response value.
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    isCircle = circularStation.isSelected();
                    stationName = result.get();
                    if (result.get().trim().length() > 2 && dataMan.trackMap.get(stationName) == null) {
                        TrainLine newLine = new TrainLine();
                        newLine.lineName = result.get();
                        newLine.circular = circularStation.isSelected();
                        newLine.init(dataMan);

                        gui.addAndDoTransaction(new jTPS_Transaction() {
                            @Override
                            public void doTransaction() {

                                dataMan.addLine(newLine);
                            }

                            @Override
                            public void undoTransaction() {
                                dataMan.deleteLine(newLine);
                            }
                        });

                        trainValidation = -1;
                    } else {
                        trainValidation++;
                    }

                } else {
                    cancel = true;
                }
            }

        });

        lineColorPicker.setOnAction(e -> {

            String selectedItemType = ((CanvasItem) dataMan.getSelectedShape()).itemType();
            if (selectedItemType.equals("track")) {
                TrainLine selectedTrack = (TrainLine) dataMan.getSelectedShape();
                Color oldColor = selectedTrack.color;
                gui.addAndDoTransaction(new jTPS_Transaction() {
                    @Override
                    public void doTransaction() {

                        selectedTrack.color = lineColorPicker.getValue();
                        selectedTrack.setStroke(lineColorPicker.getValue());
                    }

                    @Override
                    public void undoTransaction() {

                        selectedTrack.color = oldColor;

                        selectedTrack.setStroke(oldColor);
                    }
                });
            }
        });

        stationColorPicker.setOnAction(e -> {

            String selectedItemType = ((CanvasItem) dataMan.getSelectedShape()).itemType();
            if (selectedItemType.equals("station")) {
                DraggableStation station = (DraggableStation) dataMan.getSelectedShape();
                Color oldColor = station.color;
                gui.addAndDoTransaction(new jTPS_Transaction() {
                    @Override
                    public void doTransaction() {

                        station.setColor(stationColorPicker.getValue());
                    }

                    @Override
                    public void undoTransaction() {

                        station.setColor(oldColor);
                    }
                });

            }
        });

        decorColorPicker.setOnAction(e -> {

            if (decorColorPicker.getValue() != null) {

                Color oldColor = dataMan.backgroundColor;
                gui.addAndDoTransaction(new jTPS_Transaction() {
                    @Override
                    public void doTransaction() {

                        dataMan.setBackGroundColor(decorColorPicker.getValue());
                    }

                    @Override
                    public void undoTransaction() {
                        dataMan.setBackGroundColor(oldColor);
                    }
                });
            }
        });
        fontColorPicker.setOnAction(e -> {
            if (fontColorPicker.getValue() != null && dataMan.getSelectedShape() instanceof DraggableLabel) {
                DraggableLabel selectedLabel = (DraggableLabel) dataMan.getSelectedShape();
                Color oldColor = selectedLabel.fontColor;
                gui.addAndDoTransaction(new jTPS_Transaction() {
                    @Override
                    public void doTransaction() {

                        selectedLabel.setColor(fontColorPicker.getValue());
                    }

                    @Override
                    public void undoTransaction() {

                        selectedLabel.setColor(oldColor);
                    }
                });
            }
        });

        setImagebg.setOnAction(e -> {
            PropertiesManager props = PropertiesManager.getPropertiesManager();// AND NOW ASK THE USER FOR THE FILE TO OPEN
            FileChooser fc = new FileChooser();
            fc.setTitle(props.getProperty("Select an Image"));
            File selectedFile = fc.showOpenDialog(app.getGUI().getWindow());
            Image backgroundImage = dataMan.backgroundImage;
            Color backgroundColor = dataMan.backgroundColor;
            gui.addAndDoTransaction(new jTPS_Transaction() {
                @Override
                public void doTransaction() {
                    try {

                        dataMan.setBackGroundImage(selectedFile.toURI().toURL().toExternalForm());
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(m3Workspace.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                @Override
                public void undoTransaction() {
                    dataMan.backgroundColor = backgroundColor;
                    dataMan.backgroundImage = backgroundImage;
                    updateCanvas(dataMan);
                }
            });

        });

        showGrid.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                createGrid(grid);
                grid.setVisible(newValue);
            }
        });

        removeStation.setOnAction(e -> {
            if (lineCombo.getValue() != null && !lineCombo.getValue().equals("") && stationCombo.getValue() != null && !stationCombo.getValue().equals("")) {
                final TrainLine trainLine = dataMan.trackMap.get(lineCombo.getValue());
                final DraggableStation draggableStation = dataMan.stationMap.get(stationCombo.getValue());
                if (trainLine != null && draggableStation != null) {
                    dataMan.removeStation(trainLine, draggableStation);

                    int[] index = {-1};
                    gui.addAndDoTransaction(new jTPS_Transaction() {
                        @Override
                        public void doTransaction() {
                            index[0] = trainLine.stations.indexOf(draggableStation);
                            dataMan.removeStation(trainLine, draggableStation);

                        }

                        @Override
                        public void undoTransaction() {
                            dataMan.addStation(trainLine, draggableStation);
                        }
                    });
                    removeStation.setDisable(true);
                }
            }
        });

        addStation.setOnAction(e -> {
            if (lineCombo.getValue() != null && !lineCombo.getValue().equals("") && stationCombo.getValue() != null && !stationCombo.getValue().equals("")) {
                TrainLine trainLine = dataMan.trackMap.get(lineCombo.getValue());
                DraggableStation draggableStation = dataMan.stationMap.get(stationCombo.getValue());
                if (trainLine != null && draggableStation != null) {

                    gui.addAndDoTransaction(new jTPS_Transaction() {
                        @Override
                        public void doTransaction() {

                            dataMan.addStation(trainLine, draggableStation);
                        }

                        @Override
                        public void undoTransaction() {
                            dataMan.removeStation(trainLine, draggableStation);
                        }
                    });
                }
            }
        });

        deleteStation.setOnAction(e -> {
            if (((CanvasItem) dataMan.getSelectedShape()).itemType().equals("station")) {
                DraggableStation station = (DraggableStation) dataMan.getSelectedShape();
                gui.addAndDoTransaction(new jTPS_Transaction() {
                    @Override
                    public void doTransaction() {

                        dataMan.deleteStation(station);
                    }

                    @Override
                    public void undoTransaction() {

                        dataMan.addStation(station);
                        for (TrainLine rail : station.rails) {
                            dataMan.addStation(rail, station);
                        }
                    }
                });

            }
        });

        deleteTrack.setOnAction(e -> {
            if (((CanvasItem) dataMan.getSelectedShape()).itemType().equals("track")) {
                TrainLine line = (TrainLine) dataMan.getSelectedShape();
                gui.addAndDoTransaction(new jTPS_Transaction() {
                    @Override
                    public void doTransaction() {

                        dataMan.deleteLine(line);
                    }

                    @Override
                    public void undoTransaction() {
                        dataMan.addLine(line);
                    }
                });
                deleteTrack.setDisable(true);
            }
        });

        stationCombo.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (oldValue != newValue) {
                    dataMan.unselectShape();
                    if (newValue != null && !newValue.equals("")) {

                        dataMan.setSelectedShape(dataMan.stationMap.get(newValue));
                        dataMan.highlightShape(dataMan.getSelectedShape());
                    }

                }
            }
        });

        addImage.setOnAction(e -> {
            PropertiesManager props = PropertiesManager.getPropertiesManager();// AND NOW ASK THE USER FOR THE FILE TO OPEN
            FileChooser fc = new FileChooser();
            fc.setTitle(props.getProperty("Select an Image"));
            File selectedFile = fc.showOpenDialog(app.getGUI().getWindow());
            try {

                DraggableImage image = new DraggableImage();
                image.loadImages(selectedFile.toURI().toURL().toExternalForm());
                image.setLocation(15, 15);

                gui.addAndDoTransaction(new jTPS_Transaction() {
                    @Override
                    public void doTransaction() {

                        dataMan.addImage(image);
                    }

                    @Override
                    public void undoTransaction() {

                        dataMan.deleteImage(image);
                    }
                });

            } catch (MalformedURLException ex) {
                Logger.getLogger(m3Workspace.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        editLine.setVisible(false);
        editStation.setVisible(false);
        editLine.setManaged(false);
        editStation.setManaged(false);
        
        
        editStation.setOnAction(e->{
            
            
            if (dataMan.getSelectedShape()==null || !(dataMan.getSelectedShape() instanceof DraggableStation)) return;
            
            
            DraggableStation station = (DraggableStation) dataMan.getSelectedShape();
            int stationValidation = 0;

            boolean cancel = false;
            String stationName = station.name + "";

            while (stationValidation >= 0 && !cancel) {
                TextInputDialog dialog = new TextInputDialog(stationName);
                dialog.setTitle("Rename station");
                dialog.setHeaderText("Rename Station");
                dialog.setContentText(stationValidation > 0 ? "Invalid station Name. Please choose a different one" : "Station Name");
                dialog.initOwner(app.stage);
                // Traditional way to get the response value.
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    stationName = result.get();
                    if (stationName.trim().length() > 2 && (dataMan.stationMap.get(stationName) == null)) {
                           if (!stationName.equals(station.name)){
                               
                               final String oldString = station.name + "";
                               gui.addAndDoTransaction(new jTPS_Transaction() {
                            @Override
                            public void doTransaction() {
                                dataMan.renameStation(station, result.get());
                            }

                            @Override
                            public void undoTransaction() {
                                dataMan.renameStation(station, oldString);
                            }
                        });
                           } 
                        
                        
                        stationValidation = -1;
                    } else {
                        stationValidation++;
                    }
                } else {
                    cancel = true;
                }

            }

            
            
        });

        addLabel.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog("Label");
            dialog.setTitle("Label");
            dialog.setHeaderText("Input text");
            dialog.setContentText("enter a text:");
            dialog.initOwner(app.stage);

            // Traditional way to get the response value.
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {

                DraggableLabel draggableLabel = new DraggableLabel();
                draggableLabel.setText(result.get());
                draggableLabel.setLocation(15, 15);

                gui.addAndDoTransaction(new jTPS_Transaction() {
                    @Override
                    public void doTransaction() {

                        dataMan.addLabel(draggableLabel);
                    }

                    @Override
                    public void undoTransaction() {
                        dataMan.deleteLabel(draggableLabel);
                    }
                });

            }
        });

        rotateLabel.setOnAction(e -> {
            if (dataMan.getSelectedShape() instanceof StationLabel) {
                StationLabel stationlabel = (StationLabel) dataMan.getSelectedShape();
                int currentAngle = stationlabel.angle;

                gui.addAndDoTransaction(new jTPS_Transaction() {
                    @Override
                    public void doTransaction() {

                        stationlabel.rotate();
                    }

                    @Override
                    public void undoTransaction() {
                        stationlabel.setAngle(currentAngle);
                    }
                });

            }
        });
        moveLabel.setOnAction(e -> {
            dataMan.lblMoveActive = !dataMan.lblMoveActive;
        });

        lineCombo.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (oldValue != newValue) {
                    dataMan.unselectShape();
                    if (newValue != null && !newValue.equals("")) {

                        dataMan.setSelectedShape(dataMan.trackMap.get(newValue));
                        dataMan.highlightShape(dataMan.getSelectedShape());
                        reloadWorkspace(dataMan);
                    }
                }
            }
        });

        fontType.valueProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (dataMan.getSelectedShape() instanceof DraggableLabel && changeFont) {
                DraggableLabel selectedLabel = (DraggableLabel) dataMan.getSelectedShape();
                String currentFam = selectedLabel.fontfalimy + "";
                gui.addAndDoTransaction(new jTPS_Transaction() {
                    @Override
                    public void doTransaction() {

                        selectedLabel.setFontFamily(newValue);
                    }

                    @Override
                    public void undoTransaction() {
                        selectedLabel.setFontFamily(currentFam);

                    }
                });
                selectedLabel.setFontFamily(newValue);

            }
        });
        fontSize.valueProperty().addListener((ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) -> {
            if (dataMan.getSelectedShape() instanceof DraggableLabel && changeFont) {
                DraggableLabel selectedLabel = (DraggableLabel) dataMan.getSelectedShape();
                int currentSize = selectedLabel.size;
                gui.addAndDoTransaction(new jTPS_Transaction() {
                    @Override
                    public void doTransaction() {

                        selectedLabel.setSize(newValue);
                    }

                    @Override
                    public void undoTransaction() {

                        selectedLabel.setSize(currentSize
                        );
                    }
                });

            }
        });
        bold.setOnAction(e -> {
            if (dataMan.getSelectedShape() instanceof DraggableLabel && changeFont) {

                DraggableLabel selectedLabel = (DraggableLabel) dataMan.getSelectedShape();
                boolean currentlyBold = selectedLabel.bold;
                gui.actor.addTransaction(new jTPS_Transaction() {
                    @Override
                    public void doTransaction() {

                        selectedLabel.setBold(!selectedLabel.bold);
                    }

                    @Override
                    public void undoTransaction() {

                        selectedLabel.setBold(currentlyBold);
                    }
                });

            }
        });
        italics.setOnAction(e -> {
            if (dataMan.getSelectedShape() instanceof DraggableLabel && changeFont) {
                DraggableLabel selectedLabel = (DraggableLabel) dataMan.getSelectedShape();
                boolean italic = false;
                gui.addAndDoTransaction(new jTPS_Transaction() {
                    @Override
                    public void doTransaction() {
                        selectedLabel.setItalic(!italic);
                    }

                    @Override
                    public void undoTransaction() {
                        selectedLabel.setItalic(italic);
                    }
                });
                selectedLabel.setItalic(!selectedLabel.italic);

            }
        });

        thicknessControl.valueProperty().addListener((ObservableValue<? extends Number> observableValue, Number previous, Number now) -> {

            if (previous.intValue() != now.intValue() && dataMan.getSelectedShape() instanceof TrainLine) {
                TrainLine selectedLine = (TrainLine) dataMan.getSelectedShape();
                gui.addAndDoTransaction(new jTPS_Transaction() {
                    @Override
                    public void doTransaction() {

                        selectedLine.setThickness(now.intValue());
                    }

                    @Override
                    public void undoTransaction() {

                        selectedLine.setThickness(previous.intValue());
                    }
                });

            }

            thicknessControl.setValue(now.intValue());
            // This only fires when we're done
            // or when the slider is dragged to its max/min.

        });

        radiusControl.valueProperty().addListener((ObservableValue<? extends Number> observableValue, Number previous, Number now) -> {

            if (previous.intValue() != now.intValue() && dataMan.getSelectedShape() instanceof DraggableStation) {
                DraggableStation selectedStation = (DraggableStation) dataMan.getSelectedShape();
                gui.addAndDoTransaction(new jTPS_Transaction() {
                    @Override
                    public void doTransaction() {

                        selectedStation.sizeStation(now.intValue());
                    }

                    @Override
                    public void undoTransaction() {

                        selectedStation.sizeStation(previous.intValue());
                    }
                });

            }

            radiusControl.setValue(now.intValue());
            // This only fires when we're done
            // or when the slider is dragged to its max/min.

        });

        listStations.setOnAction(e -> {
            if (dataMan.getSelectedShape() instanceof TrainLine) {
                TrainLine trainLine = (TrainLine) dataMan.getSelectedShape();
                AppStationReorderDialog.getSingleton().show(trainLine.stationsNames, new AppStationReorderDialog.Callback() {
                    @Override
                    public void onClose() {
                        dataMan.redrawLines();
                    }
                });
            }

        });

        removeElement.setOnAction(e -> {
            if ((dataMan.getSelectedShape() instanceof DraggableLabel) ) {
                DraggableLabel labelTODlt = (DraggableLabel) dataMan.getSelectedShape();

                gui.addAndDoTransaction(new jTPS_Transaction() {
                    @Override
                    public void doTransaction() {

                        dataMan.deleteLabel(labelTODlt);
                    }

                    @Override
                    public void undoTransaction() {
                        dataMan.addLabel(labelTODlt);
                    }
                });
                removeElement.setDisable(true);
            } else if (dataMan.getSelectedShape() instanceof DraggableImage) {

                DraggableImage imageToDlt = (DraggableImage) dataMan.getSelectedShape();

                gui.addAndDoTransaction(new jTPS_Transaction() {
                    @Override
                    public void doTransaction() {
                        dataMan.deleteImage(imageToDlt);
                    }

                    @Override
                    public void undoTransaction() {
                        dataMan.addImage(imageToDlt);
                    }
                });

                removeElement.setDisable(true);
            }
        });

        findRoute.setOnAction(e -> {

            String startFromStr = (String) this.stationFrom.getValue();
            String stationToStr = (String) this.stationTo.getValue();
            if (!startFromStr.equals("") && !stationToStr.equals("")) {

                dataMan.prepareTransfers();
                m3Path path = m3Path.findMinimumTransferPath(dataMan.stationMap.get(startFromStr), dataMan.stationMap.get(stationToStr));
                if (path != null) {

                    PathGraphics graphics = new PathGraphics();
                    for (DraggableStation stationsOnTrip : path.tripStations) {
                        graphics.getPoints().add(stationsOnTrip.getCenterX());
                        graphics.getPoints().add(stationsOnTrip.getCenterY());
                    }

                    if (graphics.getPoints().size() == 2) {
                        graphics.getPoints().add(graphics.getPoints().get(0) + 1);

                        graphics.getPoints().add(graphics.getPoints().get(1) + 1);
                    }

                    dataMan.plotPath(graphics);

                    AppNavigationView.getSingleton().show(path.navigation, () -> {
                        dataMan.removePath();
                    });

                } else {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Route not found");
                    alert.setHeaderText(null);
                    alert.setContentText("There seems no possible route to your destination using just the metro. May be try Uber");

                    alert.showAndWait();
                }

            }
        });

        
        zoomOut.setOnAction(e -> {
            canvas.setPrefWidth(canvas.getWidth() * (1/1.1));

            canvas.setPrefHeight(canvas.getHeight() * (1/1.1));
        });
        
        zoomIn.setOnAction(e -> {
            canvas.setPrefWidth(canvas.getWidth() * 1.1);

            canvas.setPrefHeight(canvas.getHeight() * 1.1);
        });

        mapZoomIn.setOnAction(e -> {
            canvas.setScaleX(canvas.getScaleX() * 1.5);
            canvas.setScaleY(canvas.getScaleY() * 1.5);
        });
        mapZoomOut.setOnAction(e -> {
            canvas.setScaleX(canvas.getScaleX() * 1 / 1.5);
            canvas.setScaleY(canvas.getScaleY() * 1 / 1.5);
        });

        snap.setVisible(false);
        snap.setManaged(false);
        snap.setOnAction(e -> {

            if (dataMan.getSelectedShape() == null || !(dataMan.getSelectedShape() instanceof CanvasItem)) {
                return;
            }

            CanvasItem selectedItem = (CanvasItem) dataMan.getSelectedShape();

            double[] dims = {workSpaceWrapper.getWidth(), workSpaceWrapper.getHeight()};

            m3Workspace.focusAndCenter(canvas, selectedItem.getRange(), selectedItem.getXy()[0], selectedItem.getXy()[1], dims);

        });

    }

    boolean changeFont = true;

    public Pane getCanvas() {
        return canvas;
    }

    private StackPane createCell() {

        StackPane cell = new StackPane();

        Rectangle rectangle = new Rectangle();
        rectangle.widthProperty().bind(cell.widthProperty());
        rectangle.heightProperty().bind(cell.heightProperty());
        rectangle.setStroke(Color.GRAY);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStrokeWidth(1);
        cell.getChildren().add(rectangle);

        cell.getStyleClass().add("cell");
        return cell;
    }

    public void createGrid(Pane grid) {

        if (grid == null) {
            return;
        }

        workSpaceWidth = grid.getPrefWidth();
        workSpaceHeight = grid.getPrefHeight();

        grid.setMouseTransparent(true);
        grid.getChildren().clear();
        double height = workSpaceWrapper.getHeight();
        double width = workSpaceWrapper.getWidth();

        double frequency = width > height ? height / 6 : width / 6;

        Line xAxis = new Line();
        xAxis.setStroke(Color.BLACK);
        xAxis.setStrokeWidth(1.0);
        xAxis.setStartX(width / 2);
        xAxis.setStartY(0);
        xAxis.getStrokeDashArray().addAll(15d, 10d);
        xAxis.setEndX(width / 2);
        xAxis.setEndY(height);

        Line yAxis = new Line();
        yAxis.setStroke(Color.BLACK);
        yAxis.setStrokeWidth(1.0);
        yAxis.setStartX(0);
        yAxis.setStartY(height / 2);
        yAxis.setEndX(width);
        yAxis.getStrokeDashArray().addAll(15d, 10d);
        yAxis.setEndY(height / 2);
        grid.getChildren().addAll(xAxis, yAxis);

        double coveredWidth = 0;
        while (coveredWidth + frequency < width / 2) {
            Line vertialLine1 = new Line(width / 2 + frequency + coveredWidth, 0, width / 2 + frequency + coveredWidth, height);

            Line vertialLine2 = new Line(width / 2 - frequency - coveredWidth, 0, width / 2 - frequency - coveredWidth, height);
            vertialLine1.setStrokeWidth(0.5);
            vertialLine2.setStrokeWidth(0.5);
            vertialLine1.getStrokeDashArray().addAll(2d, 2d);
            vertialLine2.getStrokeDashArray().addAll(2d, 2d);

            grid.getChildren().add(vertialLine1);
            grid.getChildren().add(vertialLine2);
            coveredWidth += frequency;
        }

        double coveredHeight = 0;
        while (coveredHeight + frequency < width / 2) {
            Line vertialLine1 = new Line(0, height / 2 + frequency + coveredHeight, width, height / 2 + frequency + coveredHeight);

            Line vertialLine2 = new Line(0, height / 2 - frequency - coveredHeight, width, height / 2 - frequency - coveredHeight);
            vertialLine1.setStrokeWidth(0.5);
            vertialLine2.setStrokeWidth(0.5);
            vertialLine1.getStrokeDashArray().addAll(2d, 2d);
            vertialLine2.getStrokeDashArray().addAll(2d, 2d);

            grid.getChildren().add(vertialLine1);
            grid.getChildren().add(vertialLine2);
            coveredHeight += frequency;
        }

    }

    public void initStyle() {
        // NOTE THAT EACH CLASS SHOULD CORRESPOND TO
        // A STYLE CLASS SPECIFIED IN THIS APPLICATION'S
        // CSS FILE
        canvas.getStyleClass().add(CLASS_RENDER_CANVAS);

        leftEditToolBar.getStyleClass().add(CLASS_EDIT_TOOLBAR);
        section1.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
        section2.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);

        section3.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
        section3.prefWidthProperty().bind(leftEditToolBar.widthProperty());

        stationFrom.prefWidthProperty().bind(section3A.widthProperty());
        stationTo.prefWidthProperty().bind(section3A.widthProperty());

        section4.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
        section5.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
        section6.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);

        createTrack.prefHeightProperty().bind(section1B.heightProperty());
        deleteTrack.prefHeightProperty().bind(section1B.heightProperty());
        addStation.prefHeightProperty().bind(section1B.heightProperty());
        removeStation.prefHeightProperty().bind(section1B.heightProperty());
        listStations.prefHeightProperty().bind(section1B.heightProperty());

        createTrack.prefWidth(section1B.getPrefWidth() / 5);
        deleteTrack.prefWidth(section1B.getPrefWidth() / 5);
        addStation.prefWidth(section1B.getPrefWidth() / 5);
        removeStation.prefWidth(section1B.getPrefWidth() / 5);

        listStations.prefWidth(section1B.getPrefWidth() / 5);

        createTrack.prefHeightProperty().bind(section2B.heightProperty());
        deleteTrack.prefHeightProperty().bind(section2B.heightProperty());
        addStation.prefHeightProperty().bind(section2B.heightProperty());
        removeStation.prefHeightProperty().bind(section2B.heightProperty());
        listStations.prefHeightProperty().bind(section1B.heightProperty());

        createTrack.prefWidth(section2B.getPrefWidth() / 5);
        deleteTrack.prefWidth(section2B.getPrefWidth() / 5);
        addStation.prefWidth(section2B.getPrefWidth() / 5);
        removeStation.prefWidth(section2B.getPrefWidth() / 5);

        listStations.prefWidth(section2B.getPrefWidth() / 5);

        lineColorPicker.getStyleClass().add("button");
        lineColorPicker.setStyle("-fx-color-label-visible: false ;");

        stationColorPicker.getStyleClass().add("button");
        stationColorPicker.setStyle("-fx-color-label-visible: false ;");

        decorColorPicker.getStyleClass().add("button");
        decorColorPicker.setStyle("-fx-color-label-visible: false ;");

        fontColorPicker.getStyleClass().add("button");
        fontColorPicker.setStyle("-fx-color-label-visible: false ;");

    }

    public void loadSelectedShapeSettings(Shape shape) {

    }

    public void resetWorkspace() {
        // WE ARE NOT USING THIS, THOUGH YOU MAY IF YOU LIKE
    }

    @Override
    public void selectedItemProps(AppDataComponent dataComponent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateLists(AppDataComponent dataComponent) {

        m3Data dataManager = (m3Data) dataComponent;
        lineCombo.getItems().clear();

        stationCombo.getItems().clear();

        lineCombo.getItems().addAll(dataManager.trackMap.keySet());

        stationFrom.getItems().clear();
        stationTo.getItems().clear();
        stationFrom.getItems().addAll(dataManager.stationMap.keySet());
        stationTo.getItems().addAll(dataManager.stationMap.keySet());

        stationCombo.getItems().addAll(dataManager.stationMap.keySet());
    }

    boolean pathFinderReady = false;

    @Override
    public void reloadWorkspace(AppDataComponent dataComponent) {
        m3Data dataManager = (m3Data) dataComponent;

        snap.setDisable(true);
        addStation.setDisable(true);
        removeStation.setDisable(true);

        deleteStation.setDisable(true);
        deleteTrack.setDisable(true);

        thicknessControl.setDisable(true);

        lineColorPicker.setDisable(true);
        stationColorPicker.setDisable(true);
        decorColorPicker.setDisable(false);

        listStations.setDisable(true);

        moveLabel.setDisable(true);
        rotateLabel.setDisable(true);
        moveLabel.setSelected(false);
        radiusControl.setDisable(true);

        fontColorPicker.setDisable(true);
        fontSize.setDisable(true);
        fontType.setDisable(true);
        bold.setDisable(true);
        italics.setDisable(true);

        removeElement.setDisable(true);
        if (dataManager.getSelectedShape() == null) {
            return;
        }
        String selectedItemType = ((CanvasItem) dataManager.getSelectedShape()).itemType();
        if (selectedItemType.equals("track")) {
            TrainLine selectedTrack = (TrainLine) dataManager.getSelectedShape();
            lineCombo.setValue(selectedTrack.lineName);
            lineColorPicker.setValue(selectedTrack.color);
            lineColorPicker.setDisable(false);
            deleteTrack.setDisable(false);
            thicknessControl.setDisable(false);
            thicknessControl.setValue(selectedTrack.getThickness());
            listStations.setDisable(false);;
            if (stationCombo.getValue() != null && !stationCombo.getValue().equals("")) {
                addStation.setDisable(false);
                removeStation.setDisable(false);
            }
        } else if (selectedItemType.equals("station")) {
            DraggableStation selectedStaion = (DraggableStation) dataManager.getSelectedShape();
            stationCombo.setValue(selectedStaion.name);
            deleteStation.setDisable(false);

            stationColorPicker.setDisable(false);
            radiusControl.setValue(selectedStaion.radius);
            radiusControl.setDisable(false);

            stationColorPicker.setValue(selectedStaion.color);

            if (lineCombo.getValue() != null && !lineCombo.getValue().equals("")) {
                addStation.setDisable(false);
                removeStation.setDisable(false);
            }
        } else if (selectedItemType.equals("stationLabel")) {
            moveLabel.setDisable(false);
            moveLabel.setSelected(dataManager.lblMoveActive);
            rotateLabel.setDisable(false);
        } else if (selectedItemType.equals("label")) {

            DraggableLabel lbl = (DraggableLabel) dataManager.getSelectedShape();
            removeElement.setDisable(false);
            bold.setDisable(false);
            italics.setDisable(false);
            fontColorPicker.setDisable(false);
            fontType.setDisable(false);
            fontSize.setDisable(false);
            bold.setSelected(lbl.bold);
            italics.setSelected(lbl.italic);
            fontColorPicker.setValue(lbl.fontColor);
            fontSize.setValue(lbl.size);
            fontType.setValue(lbl.fontfalimy);

        } else if (selectedItemType.equals("image")) {
            removeElement.setDisable(false);
        }

        if (dataManager.getSelectedShape() instanceof CanvasItem) {
            snap.setDisable(false);
        }

    }

    public void colorPicker(ColorPicker p, TrainLine newLine) {
        p.setOnAction(e -> {
            Color c = p.getValue();
            System.out.println(p.getValue());
            newLine.color = c;
        });

    }

    public void setSliderThickness(double thicknessValue) {
        thicknessControl.setValue(thicknessValue);
    }

    public Slider getlineThicknessSlider() {
        return thicknessControl;
    }

    @Override
    public void updateCanvas(AppDataComponent dataComp) {
        m3Data dataman = (m3Data) dataComp;
        dataman = (m3Data) dataman;
        if (dataman.backgroundImage == null) {
            System.err.println("some hwere");
            canvasPane.setBackground(new Background(new BackgroundFill(dataman.backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));

            canvas.setBackground(null);
            if (dataman.backgroundColor != decorColorPicker.getValue()) {
                decorColorPicker.setValue(dataman.backgroundColor);
            }
        } else {
            BackgroundImage myBI = new BackgroundImage(dataman.backgroundImage,
                    BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER,
                    BackgroundSize.DEFAULT);
            canvas.setBackground(new Background(myBI));
        }
    }

}
