package de.nomisge.bluejextensions.scenebuilder;

import bluej.extensions2.*;
import javafx.concurrent.Task;
import javafx.scene.control.TextInputDialog;
import javafx.stage.StageStyle;

//import java.awt.Desktop;
//import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * This is the starting point of a BlueJ Extension
 */
public class ScenebuilderExtension extends Extension {
	/*
	 * When this method is called, the extension may start its work.
	 */
	public void startup(BlueJ bluej) {
		MenuBuilder myMenu = new MenuBuilder();
		myMenu.setBlueJ(bluej);
		bluej.setMenuGenerator(myMenu);
		SBPreferences myPrefs = new SBPreferences(bluej);
		bluej.setPreferenceGenerator(myPrefs);

		// This is the launcher object that will be used for fxml extensions
		ExternalFileLauncher.OpenExternalFileHandler scenebuilderOpener = new ExternalFileLauncher.OpenExternalFileHandler() {

			Path pathScenebuilder = Paths.get(bluej.getExtensionPropertyString(SBPreferences.PROPERTY_PATH_SCENEBUILDER,
					SBPreferences.PATH_SCENEBUILDER_DEFAULT));;

			@Override
			public void openFile(String fileName) throws Exception {
				// This method will be called by BlueJ when an attempt to open the file is made.
				// (provided no other extension has overwritten the launcher for the specified
				// file type.)

				// check if path is valid first.
				Path tmpPath = pathScenebuilder;
				while (!Files.exists(pathScenebuilder)) {
					TextInputDialog dialog = new TextInputDialog(bluej.getExtensionPropertyString(
							SBPreferences.PROPERTY_PATH_SCENEBUILDER, SBPreferences.PATH_SCENEBUILDER_DEFAULT));
					dialog.setTitle(bluej.getLabel("prefmgr.extensions.pathScenebuilder"));
					dialog.setHeaderText(bluej.getLabel("xtsn.scenebuilder.error.execnotfound"));
					dialog.setContentText(bluej.getLabel("xtsn.scenebuilder.path"));
					dialog.initStyle(StageStyle.UTILITY);
					dialog.setGraphic(null);
					dialog.getEditor().requestFocus();
					Optional<String> result = dialog.showAndWait();
					result.ifPresent(name -> {
						pathScenebuilder = Paths.get(name);
						bluej.setExtensionPropertyString(SBPreferences.PROPERTY_PATH_SCENEBUILDER,
								pathScenebuilder.toString());
					});
					// dialog was canceled
					if (result.isEmpty()) {
						pathScenebuilder = tmpPath; // in case several attempts were made before cancellation, reset to
													// what it was before
						return;
					}
				}

				AtomicBoolean hasErrorOccurred = new AtomicBoolean(false);
				Task<Void> task = new Task<>() {
					@Override
					public Void call() {
						// File externalFile = new File(fileName);
						try {
							Runtime.getRuntime().exec(pathScenebuilder.toString() + " " + fileName);
							// Desktop.getDesktop().open(externalFile);
						} catch (IOException e) {
							hasErrorOccurred.set(true);
							this.cancel();
						}
						return null;
					}
				};

				// Launch scenebuilder outside the main BlueJ java FX thread.
				Thread thread = new Thread(task);
				thread.start();

				// Wait sufficiently long to get the application opening and if not returning
				// just continue
				thread.join(10000);
				if (hasErrorOccurred.get()) {
					throw new Exception(bluej.getLabel("xtsn.scenebuilder.error.open") + " " + fileName);
				}
			}
		};

		// Prepare a list of launchers for fxml files.
		List<ExternalFileLauncher> list = new ArrayList<>();
		ExternalFileLauncher fxmlLauncher = new ExternalFileLauncher("*.fxml", scenebuilderOpener);
		list.add(fxmlLauncher);

		// Set the launchers for registration to BlueJ
		bluej.addExternalFileLaunchers(list);
	}

	/*
	 * This method must decide if this Extension is compatible with the current
	 * release of the BlueJ Extensions API
	 */
	public boolean isCompatible() {
		return (getExtensionsAPIVersionMajor() >= 3);
	}

	/*
	 * Returns the version number of this extension
	 */
	public String getVersion() {
		return ("0.0.1");
	}

	/*
	 * Returns the user-visible name of this extension
	 */
	public String getName() {
		return ("JavaFX Scenebuilder");
	}

	public void terminate() {
		System.out.println("Scenebuilder extension terminates");
	}

	public String getDescription() {
		return ("An extensions that interacts with javafx-scenebuilder");
	}

	/*
	 * Returns a URL where you can find info on this extension. The real problem is
	 * making sure that the link will still be alive in three years...
	 */
	public URL getURL() {
		try {
			return new URL("http://www.bluej.org/doc/writingextensions.html");
		} catch (Exception e) {
			// The link is either dead or otherwise unreachable
			System.out.println("Scenebuilder extension: getURL: Exception=" + e.getMessage());
			return null;
		}
	}
}