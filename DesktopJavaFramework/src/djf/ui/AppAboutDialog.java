/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package djf.ui;

import static djf.settings.AppPropertyType.APP_ICON;
import static djf.settings.AppPropertyType.APP_TITLE;
import static djf.settings.AppPropertyType.DEVELOPER_NAME;
import static djf.settings.AppPropertyType.PRODUCTION_YEAR;
import static djf.settings.AppStartupConstants.CLOSE_BUTTON_LABEL;
import static djf.settings.AppStartupConstants.FILE_PROTOCOL;
import static djf.settings.AppStartupConstants.PATH_IMAGES;
import java.awt.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;

/**
 *
 * @author Rahel Zewde
 */
public class AppAboutDialog extends Stage {
    // HERE'S THE SINGLETON OBJECT
    static AppAboutDialog singleton;
    
    // HERE ARE THE DIALOG COMPONENTS
    
    Scene messageScene;
    VBox messagePane;
    Label appName;
    ImageView logoView;
    Label appDeveloper;
    Label yearOfWork;
    
    Button closeButton;
    
    /**
     * Initializes this dialog so that it can be used repeatedly
     * for all kinds of messages. Note this is a singleton design
     * pattern so the constructor is private.
     * 
     * @param owner The owner stage of this modal dialoge.
     * 
     * @param closeButtonText Text to appear on the close button.
     */
    private AppAboutDialog() {}
    
    /**
     * A static accessor method for getting the singleton object.
     * 
     * @return The one singleton dialog of this object type.
     */
    public static AppAboutDialog getSingleton() {
	if (singleton == null)
	    singleton = new AppAboutDialog();
	return singleton;
    }
    
    /**
     * This function fully initializes the singleton dialog for use.
     * 
     * @param owner The window above which this dialog will be centered.
     */
    public void init(Stage owner) {
        // MAKE IT MODAL
        initModality(Modality.WINDOW_MODAL);
        initOwner(owner);
        

        
        // LABEL TO DISPLAY THE CUSTOM MESSAGE
        appName = new Label();  
        appName.fontProperty().set(Font.font(48));
        logoView = new ImageView();
        logoView.setFitHeight(120);
        logoView.setFitWidth(120);
        appDeveloper = new Label();
        yearOfWork = new Label();

        closeButton = new Button();
        closeButton.setOnAction(e->{ AppAboutDialog.this.close(); });

        
        
        // MAKE IT LOOK NICE
        messagePane = new VBox();
        messagePane.setPadding(new Insets(80, 60, 80, 60));
        messagePane.setSpacing(50);

        // AND PUT IT IN THE WINDOW
        
         
        

        
        // WE'LL PUT EVERYTHING HERE
        messagePane = new VBox();
        messagePane.setAlignment(Pos.CENTER);
        messagePane.getChildren().add(appName);
        messagePane.getChildren().add(logoView);
        messagePane.getChildren().add(appDeveloper);
        messagePane.getChildren().add(yearOfWork);
        
        
         messageScene = new Scene(messagePane);
        this.setScene(messageScene);
       
        
        
    }
 
    /**
     * This method loads a custom message into the label and
     * then pops open the dialog.
     * 
     * @param title The title to appear in the dialog window.
     * 
     * @param message Message to appear inside the dialog.
     */
    public void show(String title) {
        setTitle(title);
	// SET THE DIALOG TITLE BAR TITLE
       PropertiesManager props = PropertiesManager.getPropertiesManager();
      
        appName.setText(props.getProperty(APP_TITLE));
        appDeveloper.setText(props.getProperty(DEVELOPER_NAME));
        yearOfWork.setText(props.getProperty(PRODUCTION_YEAR));
        
        String imagePath = FILE_PROTOCOL + PATH_IMAGES + props.getProperty(APP_ICON);
        logoView.setImage(new Image(imagePath));
	
	// AND OPEN UP THIS DIALOG, MAKING SURE THE APPLICATION
	// WAITS FOR IT TO BE RESOLVED BEFORE LETTING THE USER
	// DO MORE WORK.
        showAndWait();
    }
}
