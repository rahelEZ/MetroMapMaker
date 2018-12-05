/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package djf.ui;

import djf.AppTemplate;
import djf.controller.AppFileController;
import static djf.settings.AppStartupConstants.FILE_PROTOCOL;
import static djf.settings.AppStartupConstants.PATH_IMAGES;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;

/**
 *
 * @author Rahel Zewde
 */
public class AppWelcomeDialog extends Stage {

    // HERE'S THE SINGLETON OBJECT
    static AppWelcomeDialog singleton;
    protected AppGUI gui;
    public AppTemplate app;
    // HERE ARE THE DIALOG COMPONENTS

    AppFileController fileController;

    BorderPane messagePane;

    Scene messageScene;
    Button closeButton;
    Label recentWorksLabel;
    ImageView m3;
    VBox leftside;
    VBox rightside;
    Hyperlink startNew;

    List<Hyperlink> recentWorkButtons = new ArrayList();

    /**
     * Initializes this dialog so that it can be used repeatedly for all kinds
     * of messages. Note this is a singleton design pattern so the constructor
     * is private.
     *
     * @param owner The owner stage of this modal dialoge.
     *
     * @param closeButtonText Text to appear on the close button.
     */
    protected AppWelcomeDialog(AppGUI gui) {
        this.gui = gui;
    }

    /**
     * A static accessor method for getting the singleton object.
     *
     * @return The one singleton dialog of this object type.
     */
    public static AppWelcomeDialog getSingleton(AppGUI gui) {
        if (singleton == null) {
            singleton = new AppWelcomeDialog(gui);
        }
        singleton.gui = gui;
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
        recentWorksLabel = new Label("Recent Works");
        startNew = new Hyperlink("Create New Metro Map");
        closeButton = new Button();
        closeButton.setOnAction(e -> {
            AppWelcomeDialog.this.close();
        });
        startNew.setOnAction(e -> {
            startNewMap();
        });

        startNew.setStyle("-fx-background-color: #ddd; "
                + "-fx-border-width: 50px;"
                + "-fx-border-color: #ddd;");
        // MAKE IT LOOK NICE
        messagePane = new BorderPane();
        messagePane.setPadding(new Insets(30, 10, 30, 10));

        m3 = new ImageView(new Image("file:./images/logoDisplay.PNG"));

        leftside = new VBox();
        rightside = new VBox();

        rightside.setAlignment(Pos.CENTER);

        leftside.setStyle("-fx-background-color: #ccc; "
                + "-fx-border-color: #ccc;"
                + "-fx-border-width: 50px;"
                + "-fx-height: 300px;"
        );

        rightside.setStyle("-fx-background-color: #ddd;"
                + "-fx-border-color: #ddd;"
                + "-fx-border-width: 90px;"
                + "-fx-height: 300px;");

        leftside.getChildren().add(recentWorksLabel);

        rightside.getChildren().add(m3);

        VBox startNewWrapper = new VBox(startNew);
        startNewWrapper.setAlignment(Pos.CENTER);
        rightside.getChildren().add(startNewWrapper);

        ((BorderPane) messagePane).setLeft(leftside);
        ((BorderPane) messagePane).setCenter(rightside);

        messagePane.setStyle("-fx-background-color:#ddd;");

        // AND PUT IT IN THE WINDOW
        messageScene = new Scene(messagePane, owner.getWidth() * 0.5, owner.getHeight() * 0.5);

        this.setScene(messageScene);
    }

    public void startNewMap() {
        gui.getFileController().handleNewRequest();
        this.close();
    }

    public void present(List<String> projectNames) {

        int projectsToShow = projectNames.size() > 5 ? 5 : projectNames.size();

        for (int i = 0; i < projectsToShow; i++) {
            String projectFileName = projectNames.get(i);
            Hyperlink projectLink = new Hyperlink(getProjectNameFromFile(projectFileName));
            projectLink.setOnAction(e -> {
                gui.getFileController().startWorkingOnFile(projectFileName);
                this.close();
            });
            leftside.getChildren().add(projectLink);
        }

        if (projectsToShow == 0) {

            recentWorksLabel.setText("No recent projects.\n\n\nYour recent projects\nwill show up here next time");
        }

        String recentWorksFile = FILE_PROTOCOL + PATH_IMAGES + "recentWorks";

        // SET THE DIALOG TITLE BAR TITLE
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        this.setTitle("Welcome to Metro Map Maker");
        this.showAndWait();
    }

    private static String getProjectNameFromFile(String fileName) {

        String[] sections = fileName.split("/");
        if (sections.length < 1) {
            return fileName;
        }
        return (sections[sections.length - 1].substring(0, 1).toUpperCase() + sections[sections.length - 1].substring(1)).substring(0, (sections[sections.length - 1].substring(0, 1).toUpperCase() + sections[sections.length - 1].substring(1)).length() - 5);
    }
}
