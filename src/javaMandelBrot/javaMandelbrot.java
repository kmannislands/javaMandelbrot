package javaMandelBrot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * This is the main class that should contain the GUI
 * implementation, JavaFX application, etc.
 * 
 * 
 */

public class javaMandelbrot extends Application{
	private File cacheDir;
	
	public javaMandelbrot() {
		boolean cache = false;
		// set the base directory
		File baseDir = new File(System.getProperty("user.dir"));
		File[] dirs = baseDir.listFiles();
		for (File thisFile : dirs) {
			boolean isDir = thisFile.isDirectory();
			String[] thisPath = 
					thisFile.toString().split("/");
			String thisName = thisPath[thisPath.length - 1];
			if (thisName.equals("cache")) {
				cache = true;
			} else {
				System.out.println(thisName);
				continue;
			}
		}
		if (!cache) {
			//if no
			try {
				File newCache = new File(baseDir.getAbsolutePath() + "/cache");
				Files.createDirectory(newCache.toPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			cacheDir = new File (baseDir.getAbsolutePath() + "/cache");
			
			//Steph make the stage here
			this.start(primaryStage); // start the App
		}
		
	}

	@Override
	public void start(Stage primaryStage) {
	// Seph, this is where GUI stuff should go
		try {
			
		} catch (Exception e) {
			
		} finally {
			// great success
			Alert sucess = new Alert(AlertType.INFORMATION,
					"Java Manderbort, Great Success!");
		}
		
		// TODO cretateAlert() method
		stage.show();
	}
	
	public static void main(String[] args) {
		javaMandelbrot thisM = new javaMandelbrot();
	}
	
}
