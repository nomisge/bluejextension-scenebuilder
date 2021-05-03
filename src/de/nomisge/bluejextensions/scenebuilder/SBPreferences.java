package de.nomisge.bluejextensions.scenebuilder;

import bluej.extensions2.BlueJ;
import bluej.extensions2.PreferenceGenerator;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

class SBPreferences implements PreferenceGenerator
{
    private Pane myPane;
    private TextField pathScenebuilder;
    private BlueJ bluej;
    public static final String PROPERTY_PATH_SCENEBUILDER = "Path-Scenebuilder";
    public static final String PATH_SCENEBUILDER_DEFAULT = "";

    // Construct the panel, and initialise it from any stored values
    public SBPreferences(BlueJ bluej)
    {
        this.bluej = bluej;
        myPane = new Pane();
        HBox hboxContainer = new HBox();
        hboxContainer.getChildren().add(new Label(bluej.getLabel("prefmgr.extensions.pathScenebuilder")));
        pathScenebuilder = new TextField();
        pathScenebuilder.setPrefWidth(250);
        hboxContainer.getChildren().add(pathScenebuilder);
        myPane.getChildren().add(hboxContainer);
        // Load the default value
        loadValues();
    }

    public Pane getWindow()
    {
        return myPane;
    }

    public void saveValues()
    {
        // Save the preference value in the BlueJ properties file
        bluej.setExtensionPropertyString(PROPERTY_PATH_SCENEBUILDER, pathScenebuilder.getText());
    }

    public void loadValues()
    {
        // Load the property value from the BlueJ properties file,
        // default to an empty string
        pathScenebuilder.setText(bluej.getExtensionPropertyString(PROPERTY_PATH_SCENEBUILDER, PATH_SCENEBUILDER_DEFAULT));
    }
}