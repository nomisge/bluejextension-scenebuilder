package de.nomisge.bluejextensions.scenebuilder;

import java.io.File;
import java.util.Optional;

import bluej.extensions2.*;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.stage.StageStyle;

class MenuBuilder extends MenuGenerator {
	private BPackage curPackage;
	private BClass curClass;
	private BObject curObject;
	private EventHandler<ActionEvent> newFxmlFile = newFxmlFile();
	private BlueJ bluej;

	public void setBlueJ(BlueJ bluej) {
		this.bluej = bluej;
	}

	@Override
	public MenuItem getToolsMenuItem(BPackage aPackage) {
		MenuItem mi = new MenuItem();
		mi.setText(bluej.getLabel("xtsn.scenebuilder.newfile"));
		mi.setId("Tools menu:");
		mi.setOnAction(newFxmlFile);
		return mi;
	}

	@Override
	public MenuItem getPackageMenuItem(BPackage bPackage) {
		MenuItem mi = new MenuItem();
		mi.setText(bluej.getLabel("xtsn.scenebuilder.newfile"));
		mi.setId("Package menu:");
		mi.setOnAction(newFxmlFile);
		//TODO: Fix or report bug on frozen textinput in dialog
		return mi;
	}

	// These methods will be called when
	// each of the different menus are about to be invoked.
	@Override
	public void notifyPostToolsMenu(BPackage bp, MenuItem mi) {
		System.out.println("Post on Tools menu");
		curPackage = bp;
		curClass = null;
		curObject = null;
	}

	@Override
	public void notifyPostPackageMenu(BPackage bp, MenuItem mi) {
		System.out.println("Post on Package menu");
		curPackage = bp;
		curClass = null;
		curObject = null;
	}

	public EventHandler<ActionEvent> newFxmlFile() {

		return actionEvent -> {
			try {
				String file = "";
				boolean doExec = true;

				if (curObject != null)
					curClass = curObject.getBClass();
				if (curClass != null)
					curPackage = curClass.getPackage();

				TextInputDialog dialog = new TextInputDialog("gui");
				dialog.setTitle(bluej.getLabel("xtsn.scenebuilder.newfile"));
				dialog.setHeaderText(bluej.getLabel("xtsn.scenebuilder.newfileheader"));
				dialog.setContentText(bluej.getLabel("xtsn.scenebuilder.newfilename"));
				dialog.initStyle(StageStyle.UTILITY);
				dialog.setGraphic(null);
				Optional<String> result = dialog.showAndWait();
				if (result.isPresent()) {
					file = result.get() + ".fxml";
				} else {
					doExec = false;
				}

				if (doExec) { // only open if dialog was not canceled
					File fxmlFile = new File(curPackage.getProject().getDir() + File.separator + file);
					if (fxmlFile.createNewFile()) {
						curPackage.reload();
//						Runtime.getRuntime()
//								.exec(bluej.getExtensionPropertyString(SBPreferences.PROPERTY_PATH_SCENEBUILDER,
//										SBPreferences.PATH_SCENEBUILDER_DEFAULT) + " " + fxmlFile.toString());
					}
					else {
						//TODO file already exists. Show some message about it.
					}
				}
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		};

	}
}