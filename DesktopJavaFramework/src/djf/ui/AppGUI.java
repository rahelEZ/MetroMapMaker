package djf.ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;
import djf.controller.AppFileController;
import djf.AppTemplate;
import static djf.settings.AppPropertyType.*;
import static djf.settings.AppStartupConstants.APP_PROPERTIES_FILE_NAME;
import static djf.settings.AppStartupConstants.FILE_PROTOCOL;
import static djf.settings.AppStartupConstants.PATH_IMAGES;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import jtps.PostTransaction;
import jtps.jTPS_Transaction;
import jtps.jTPS;
import jtps.*;



/**
/**
 * This class provides the basic user interface for this application,
 * including all the file controls, but not including the workspace,
 * which would be customly provided for each app.
 * 
 * @author Richard McKenna + Rahel Zewde
 * @version 1.0
 */
public class AppGUI {
    // THIS HANDLES INTERACTIONS WITH FILE-RELATED CONTROLS
    protected AppFileController fileController;
    

    // THIS IS THE APPLICATION WINDOW
    protected Stage primaryStage;

    // THIS IS THE STAGE'S SCENE GRAPH
    protected Scene primaryScene;

    // THIS PANE ORGANIZES THE BIG PICTURE CONTAINERS FOR THE
    // APPLICATION AppGUI. NOTE THAT THE WORKSPACE WILL GO
    // IN THE CENTER REGION OF THE appPane
    protected BorderPane appPane;
    
    // THIS IS THE TOP PANE WHERE WE CAN PUT TOOLBAR
    protected FlowPane topToolbarPane;
    
    // THIS IS THE FILE TOOLBAR AND ITS CONTROLS
    protected FlowPane fileToolbar;
    
    protected FlowPane editToolBar;
    protected FlowPane settingsToolBar;

    // FILE TOOLBAR BUTTONS
    protected Button newButton;
    protected Button loadButton;
    public Button saveButton;
    public Button saveAsButton;
    protected Button exportButton;
    // ADDED TOOLBAR BUTTONS
    public Button undoButton;
    public Button redoButton;
    
    public Button aboutButton;
    
    // THIS DIALOG IS USED FOR GIVING FEEDBACK TO THE USER
    protected AppYesNoCancelDialogSingleton yesNoCancelDialog;
    
    // THIS TITLE WILL GO IN THE TITLE BAR
    protected String appTitle;
    
    /**
     * This constructor initializes the file toolbar for use.
     * 
     * @param initPrimaryStage The window for this application.
     * 
     * @param initAppTitle The title of this application, which
     * will appear in the window bar.
     * 
     * @param app The app within this gui is used.
     */
    
    
    public jtps.jTPS actor;
    public AppTemplate app;
    public AppGUI(  Stage initPrimaryStage, 
		    String initAppTitle, 
		    AppTemplate app){
        
        
	// SAVE THESE FOR LATER
	primaryStage = initPrimaryStage;
	appTitle = initAppTitle;
        this.app = app;
	      
        // INIT THE TOOLBAR
        initTopToolbar(this.app);
        		
        // AND FINALLY START UP THE WINDOW (WITHOUT THE WORKSPACE)
        initWindow();
        
        // INIT THE STYLESHEET AND THE STYLE FOR THE FILE TOOLBAR
        initStylesheet(app);
        initToolbarStyles();  
        
    }
    public void addAndDoTransaction(jTPS_Transaction transaction){
        actor.addTransaction(transaction,true);
    }
    public void justAddTransaction(jTPS_Transaction transaction){
        actor.addTransaction(transaction,false);
    }
    public void transactionUndo(){
        actor.undoTransaction();
    }
    public void transactionRedo(){
        actor.doTransaction(true);
    }
   
    
    /**
     * Accessor method for getting the file toolbar controller.
     */
    public AppFileController getFileController() { return fileController; }
    
    /**
     * Accessor method for getting the application pane, within which all
     * user interface controls are ultimately placed.
     * 
     * @return This application GUI's app pane.
     */
    public BorderPane getAppPane() { return appPane; }
    
    /**
     * Accessor method for getting the toolbar pane in the top, within which
     * other toolbars are placed.
     * 
     * @return This application GUI's app pane.
     */    
    public FlowPane getTopToolbarPane() {
        return topToolbarPane;
    }
    
    /**
     * Accessor method for getting the file toolbar pane, within which all
     * file controls are ultimately placed.
     * 
     * @return This application GUI's app pane.
     */    
    public FlowPane getFileToolbar() {
        return fileToolbar;
    }
    
    /**
     * Accessor method for getting this application's primary stage's,
     * scene.
     * 
     * @return This application's window's scene.
     */
    public Scene getPrimaryScene() { return primaryScene; }
    
    /**
     * Accessor method for getting this application's window,
     * which is the primary stage within which the full GUI will be placed.
     * 
     * @return This application's primary stage (i.e. window).
     */    
    public Stage getWindow() { return primaryStage; }

    /**
     * This method is used to activate/deactivate toolbar buttons when
     * they can and cannot be used so as to provide foolproof design.
     * 
     * @param saved Describes whether the loaded Page has been saved or not.
     */
    public void updateToolbarControls(boolean saved) {
        // THIS TOGGLES WITH WHETHER THE CURRENT COURSE
        // HAS BEEN SAVED OR NOT
        saveButton.setDisable(saved);

        // ALL THE OTHER BUTTONS ARE ALWAYS ENABLED
        // ONCE EDITING THAT FIRST COURSE BEGINS
	newButton.setDisable(false);
        loadButton.setDisable(false);
        exportButton.setDisable(false);
        
        if (!saved){
            fileController.markAsEdited();
        }
        
        // NOTE THAT THE NEW, LOAD, AND EXIT BUTTONS
        // ARE NEVER DISABLED SO WE NEVER HAVE TO TOUCH THEM
    }

    /****************************************************************************/
    /* BELOW ARE ALL THE PRIVATE HELPER METHODS WE USE FOR INITIALIZING OUR AppGUI */
    /****************************************************************************/
    
    /**
     * This function initializes all the buttons in the toolbar at the top of
     * the application window. These are related to file management.
     */
    private void initTopToolbar(AppTemplate app) {
        fileToolbar = new FlowPane();
        editToolBar = new FlowPane();
        settingsToolBar = new FlowPane();
        

        // HERE ARE OUR FILE TOOLBAR BUTTONS, NOTE THAT SOME WILL
        // START AS ENABLED (false), WHILE OTHERS DISABLED (true)
        newButton = initChildButtonOnlyLabel(fileToolbar, NEW_LABEL.toString(),	false);
        loadButton = initChildButtonOnlyLabel(fileToolbar, LOAD_LABEL.toString(),	false);
        saveButton = initChildButtonOnlyLabel(fileToolbar, SAVE_LABEL.toString(),	true);
        saveAsButton = initChildButtonOnlyLabel(fileToolbar, SAVE_AS_LABEL.toString(),	true);
        exportButton = initChildButtonOnlyLabel(fileToolbar, EXPORT_LABEL.toString(),	true);
        
        //ADDED BUTTONS
        undoButton = initChildButtonOnlyLabel(editToolBar,	UNDO_LABEL.toString(),	  false);
        redoButton = initChildButtonOnlyLabel(editToolBar,	REDO_LABEL.toString(),	  false);
        
        aboutButton = initChildButtonOnlyLabel(settingsToolBar,	    ABOUT_LABEL.toString(),	false);

	// AND NOW SETUP THEIR EVENT HANDLERS
        fileController = new AppFileController(app);
        newButton.setOnAction(e -> {
            fileController.handleNewRequest();
        });
        
        loadButton.setOnAction(e -> {
            fileController.handleLoadRequest();
        });
        
        saveButton.setOnAction(e -> {
            fileController.handleSaveRequest(false);
        });
        saveAsButton.setOnAction(e->{
            fileController.handleSaveRequest(true);
        });
        exportButton.setOnAction(e ->{
            fileController.handleExportRequest();
        });
        undoButton.setOnAction(e -> {
            transactionUndo();
        });
        
        redoButton.setOnAction(e -> {
            transactionRedo();
        });
        
        
        
        // NOW PUT THE FILE TOOLBAR IN THE TOP TOOLBAR, WHICH COULD
        // ALSO STORE OTHER TOOLBARS
        topToolbarPane = new FlowPane();
        topToolbarPane.getChildren().add(fileToolbar);
        topToolbarPane.getChildren().add(editToolBar);
        
        topToolbarPane.getChildren().add(settingsToolBar);
        
    }

    
    public void resetToolBars(){
        undoButton.setDisable(true);
        redoButton.setDisable(true);
        
    }
    // INITIALIZE THE WINDOW (i.e. STAGE) PUTTING ALL THE CONTROLS
    // THERE EXCEPT THE WORKSPACE, WHICH WILL BE ADDED THE FIRST
    // TIME A NEW Page IS CREATED OR LOADED
    private void initWindow() {
	PropertiesManager props = PropertiesManager.getPropertiesManager();
        
        // SET THE WINDOW TITLE
        primaryStage.setTitle(appTitle);
        

        // START FULL-SCREEN OR NOT, ACCORDING TO PREFERENCES
        primaryStage.setMaximized("true".equals(props.getProperty(START_MAXIMIZED)));

        // ADD THE TOOLBAR ONLY, NOTE THAT THE WORKSPACE
        // HAS BEEN CONSTRUCTED, BUT WON'T BE ADDED UNTIL
        // THE USER STARTS EDITING A COURSE
        appPane = new BorderPane();
        appPane.setTop(topToolbarPane);
        primaryScene = new Scene(appPane);

        // SET THE APP PANE PREFERRED SIZE ACCORDING TO THE PREFERENCES
        double prefWidth = Double.parseDouble(props.getProperty(PREF_WIDTH));
        double prefHeight = Double.parseDouble(props.getProperty(PREF_HEIGHT));
        appPane.setPrefWidth(prefWidth);
        appPane.setPrefHeight(prefHeight);

        // SET THE APP ICON
        String appIcon = FILE_PROTOCOL + PATH_IMAGES + props.getProperty(APP_ICON);
        primaryStage.getIcons().add(new Image(appIcon));

        // NOW TIE THE SCENE TO THE WINDOW
        primaryStage.setScene(primaryScene);
    }
    
    /**
     * This is a public helper method for initializing a simple button with
     * an icon and tooltip and placing it into a toolbar.
     * 
     * @param toolbar Toolbar pane into which to place this button.
     * 
     * @param icon Icon image file name for the button.
     * 
     * @param tooltip Tooltip to appear when the user mouses over the button.
     * 
     * @param disabled true if the button is to start off disabled, false otherwise.
     * 
     * @return A constructed, fully initialized button placed into its appropriate
     * pane container.
     */
    
    public Button initChildButton(Pane toolbar, String icon, String tooltip, boolean disabled) {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
	
	// LOAD THE ICON FROM THE PROVIDED FILE
        String imagePath = FILE_PROTOCOL + PATH_IMAGES + props.getProperty(icon);
        Image buttonImage = new Image(imagePath);
	
	// NOW MAKE THE BUTTON
        Button button = new Button();
        button.setDisable(disabled);
        button.setGraphic(new ImageView(buttonImage));
        Tooltip buttonTooltip = new Tooltip(props.getProperty(tooltip));
        button.setTooltip(buttonTooltip);
	
	// PUT THE BUTTON IN THE TOOLBAR
        toolbar.getChildren().add(button);
	
	// AND RETURN THE COMPLETED BUTTON
        return button;
    }
    
    public ToggleButton initToogleButton(Pane toolbar, String icon, String tooltip, boolean disabled) {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
	
	// LOAD THE ICON FROM THE PROVIDED FILE
        String imagePath = FILE_PROTOCOL + PATH_IMAGES + props.getProperty(icon);
        Image buttonImage = new Image(imagePath);
	
	// NOW MAKE THE BUTTON
        ToggleButton button = new ToggleButton();
        button.setDisable(disabled);
        button.setGraphic(new ImageView(buttonImage));
        Tooltip buttonTooltip = new Tooltip(props.getProperty(tooltip));
        button.setTooltip(buttonTooltip);
	
	// PUT THE BUTTON IN THE TOOLBAR
        toolbar.getChildren().add(button);
	
	// AND RETURN THE COMPLETED BUTTON
        return button;
    }
    
    public Button initChildButtonOnlyLabel(Pane toolPane, String title, boolean  disabled){
        PropertiesManager props = PropertiesManager.getPropertiesManager();
	
	
	// NOW MAKE THE BUTTON
        Button button = new Button();
        button.setText(props.getProperty(title));
        button.setDisable(disabled);
        Tooltip buttonTooltip = new Tooltip(props.getProperty(title));
        button.setTooltip(buttonTooltip);
	
	// PUT THE BUTTON IN THE TOOLBAR
        toolPane.getChildren().add(button);
	
	// AND RETURN THE COMPLETED BUTTON
        return button; 
    }
    
   /**
     *  Note that this is the default style class for the top file toolbar
     * and that style characteristics for this type of component should be 
     * put inside app_properties.xml.
     */
    public static final String CLASS_BORDERED_PANE = "bordered_pane";

   /**
     *  Note that this is the default style class for the file buttons in
     * the top file toolbar and that style characteristics for this type
     * of component should be put inside app_properties.xml.
     */
    public static final String CLASS_FILE_BUTTON = "file_button";
    
    /**
     * This function sets up the stylesheet to be used for specifying all
     * style for this application. Note that it does not attach CSS style
     * classes to controls, that must be done separately.
     */
    void initStylesheet(AppTemplate app) {
	// SELECT THE STYLESHEET
	PropertiesManager props = PropertiesManager.getPropertiesManager();
	String stylesheet = props.getProperty(APP_PATH_CSS);
	stylesheet += props.getProperty(APP_CSS);
        Class appClass = app.getClass();
	URL stylesheetURL = appClass.getResource(stylesheet);
	String stylesheetPath = stylesheetURL.toExternalForm();
	primaryScene.getStylesheets().add(stylesheetPath);	
    }
    
    /**
     * This function specifies the CSS style classes for the controls managed
     * by this framework.
     */
    private void initToolbarStyles() {
	topToolbarPane.getStyleClass().add(CLASS_BORDERED_PANE);
        fileToolbar.getStyleClass().add(CLASS_BORDERED_PANE);
        fileToolbar.setPrefSize(330, fileToolbar.getHeight());
        editToolBar.getStyleClass().add(CLASS_BORDERED_PANE);
        editToolBar.setPrefSize(155, editToolBar.getHeight());
        settingsToolBar.getStyleClass().add(CLASS_BORDERED_PANE);
        settingsToolBar.setPrefSize(90, settingsToolBar.getHeight());
        newButton.getStyleClass().add(CLASS_FILE_BUTTON);
	loadButton.getStyleClass().add(CLASS_FILE_BUTTON);
	saveButton.getStyleClass().add(CLASS_FILE_BUTTON);
        exportButton.getStyleClass().add(CLASS_FILE_BUTTON);
        
        
    }
    public void activateWorkSpace(){
	app.getWorkspaceComponent().activateWorkspace(app.getGUI().getAppPane());
    }
    public void sayWelcome(){
        List<String> recentProjects = fileController.getRecentFiles();
         
        
       
        
        
        
 
         AppWelcomeDialog dialog = AppWelcomeDialog.getSingleton(app.getGUI());
        dialog.present(recentProjects);
        
    }

    public Button initChildLabelOnly(HBox section2B, String toString, String toString0, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
