package javaMandelBrot;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * This class is used to create and open a pane of 
 * our pre-cached Mandelbrot zoom animation.
 * 
 * @author Kieran Mann & Seph Martin
 * @email kmann@ucsd.edu, jbm002@ucsd.edu
 * @version 1.2
 */

public class animationPlayer {
	private final int HEIGHT = 512;
	private final int WIDTH = 512;
	private List<File> frames = new ArrayList<>();
	private final double SPEED;
	private StackPane pane;
	private Iterator<File> frameIter;
	private int stackIndex = 0;
	
	/**
	 * This method accepts a File and creates a javafx 
	 * ImageView object.
	 * 
	 * @param thisFrame
	 * @return logoV 
	 */
	private ImageView nextFrame(File thisFrame) {
    	ImageView logoV = null;
    	try {
    		File f = thisFrame;
        	FileInputStream logoIn = 
        			new FileInputStream(f);
        	Image logo = new Image(logoIn, HEIGHT,
        			WIDTH, true, true);
        	logoV = new ImageView();
        	logoV.setImage(logo);
        } catch (Exception e) {
        	System.out.println("Problem loading frame!");
        }
    	return logoV;
    }
	
	/**
	 * This class constructs an animation player object.
	 * 
	 * @param frames - ArrayList of frame files
	 * @param SPEED - double value representing seconds/200%
	 */
	
	public animationPlayer(List<File> frames, double SPEED) {
		this.SPEED = SPEED;
		this.frames = frames;
		pane = new StackPane();
		frameIter = frames.iterator();
		
	}
	
	/**
	 * This function uses recursion to iterate over
	 * the png files provided and animate
	 * 
	 * @param thisFile a sorted ArrayList of files
	 */
	private void addFrames(File thisFile) {
		ImageView thisImg = null;
		thisImg = nextFrame(thisFile);
		
		// starting keyframe
		KeyValue startX = new KeyValue(thisImg.scaleXProperty(), 0.5);
		KeyValue startY = new KeyValue(thisImg.scaleYProperty(), 0.5);
		KeyFrame startF = new KeyFrame(Duration.ZERO, startX, startY);
	
		// midpoint: zoom 1.0
		KeyValue endX = new KeyValue(thisImg.scaleXProperty(), 1.0);
		KeyValue endY = new KeyValue(thisImg.scaleYProperty(), 1.0);
		KeyFrame endF = new KeyFrame(new Duration(SPEED),
				new EventHandler<ActionEvent>() {
			
					public void handle(ActionEvent event) {
						if (frameIter.hasNext()) {
							// plug in the next file recursively
							addFrames(frameIter.next());
						}
					}
		}, endX, endY);
		// eventhandler to add the next frame here
		
		// continue to zoom 
		KeyValue nextX = new KeyValue(thisImg.scaleXProperty(), 2.0);
		KeyValue nextY = new KeyValue(thisImg.scaleYProperty(), 2.0);
		KeyFrame nextF = new KeyFrame(new Duration(SPEED*2.0), nextX, nextY);
		// eventhandler to remove this frame here
		
		Timeline timeline = new Timeline();
		
		// add keyframes to timeline
		timeline.getKeyFrames().addAll(startF, endF, nextF);
		timeline.autoReverseProperty().set(false);
		
		//System.out.println("Adding " + thisImg.toString() );
		
		// add the image to the stack pane
		pane.getChildren().add(thisImg);
		
		// start the animation on this frame
		timeline.play();
		stackIndex++;
	}
	
	/**
	 * This method starts the animation and shows the stage
	 * 
	 * @throws Exception
	 */
	
	public void showStage() throws Exception {
			Scene scene = new Scene(pane, 512, 512);
			Stage primaryStage = new Stage();
		
			primaryStage.setScene(scene);
		
			addFrames(frameIter.next());
			primaryStage.show(); 
	}
}

