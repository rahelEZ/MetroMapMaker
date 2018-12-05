package djf.ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import static djf.settings.AppStartupConstants.CLOSE_BUTTON_LABEL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * This class serves to present custom text messages to the user when
 * events occur. Note that it always provides the same controls, a label
 * with a message, and a single Ok button. 
 * 
 * @author Richard McKenna
 * @version 1.0
 */
public class AppNavigationView extends Stage {
    // HERE'S THE SINGLETON OBJECT
    static AppNavigationView singleton = null;
   
    
    
    BorderPane menuContainer;
    ListView<String> stationList;
    BorderPane window;
    Button close;
    
    Scene stationListScene;
    
    List<String> trainStationOrder;
    
    Callback callback;
    
    /**
     * Initializes this dialog so that it can be used repeatedly
     * for all kinds of messages. Note this is a singleton design
     * pattern so the constructor is private.
     * 
     * @param owner The owner stage of this modal dialoge.
     * 
     * @param closeButtonText Text to appear on the close button.
     */
    private AppNavigationView() {}
    
    /**
     * A static accessor method for getting the singleton object.
     * 
     * @return The one singleton dialog of this object type.
     */
    public static AppNavigationView getSingleton() {
	if (singleton == null)
	    singleton = new AppNavigationView();
	return singleton;
    }
    
    
    int selectedIndex = -1;
    
    /**
     * This function fully initializes the singleton dialog for use.
     * 
     * @param owner The window above which this dialog will be centered.
     */
    public void init(Stage owner) {
        // MAKE IT MODAL
        initModality(Modality.WINDOW_MODAL);
        initOwner(owner);
        
        
        this.setX(0);
        this.setY(0);
        
        
        close= new Button("Close");
      
       
        
        menuContainer = new BorderPane();
        menuContainer.setRight(close);
        
        close.setOnAction(e->{
            callback.onClose();
            this.close();
        });
        
        stationList = new ListView<>();
        
         window = new BorderPane();
         
         window.setCenter(stationList);
         window.setBottom(menuContainer);
         
         
        
        
        

       window.setPrefHeight(400.0);
       
       window.setPrefWidth(500.0);

        // AND PUT IT IN THE WINDOW
        stationListScene = new Scene(window);
        
          close.prefWidthProperty().bind(stationList.prefWidthProperty());
        this.setScene(stationListScene);
    }
   boolean act = true;
   
    public void show(List<String> order, Callback onclose) {
	// SET THE DIALOG TITLE BAR TITLE
	setTitle("Navigation Instruction");
        
        stationList.getItems().clear();
	for (int i = 0; i < order.size(); i++){
            this.stationList.getItems().add(order.get(i));
            
        }
        this.callback = onclose;
        
        
        showAndWait();
    }
    
    public  interface Callback{
        public void onClose();
    }

    @Override
    public void close() {
        super.close(); //To change body of generated methods, choose Tools | Templates.
    
        this.callback.onClose();
    }
    
    
}