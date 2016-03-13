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
	
	public animationPlayer(List<File> frames, double SPEED) {
		this.SPEED = SPEED;
		this.frames = frames;
		pane = new StackPane();
		Scene scene = new Scene(pane, 512, 512);
		Stage primaryStage = new Stage();
		
		primaryStage.setScene(scene);
		
		//ImageView lastImg = null;
		double thisTime = 0.0;
		
		frameIter = frames.iterator();
		addFrames(frameIter.next());
		primaryStage.show();
	}
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
		KeyFrame nextF = new KeyFrame(new Duration(SPEED*2.0), 
				new EventHandler<ActionEvent>() {
					public void handle(ActionEvent event) {
						if (frameIter.hasNext())
							// get rid of this frame once it's done
							try {
								pane.getChildren().remove(stackIndex);
							} catch (Exception e) {
								System.out.println("Problem #2");
							}
					}
		}, nextX, nextY);
		// eventhandler to remove this frame here
		
		Timeline timeline = new Timeline();
		
		timeline.getKeyFrames().addAll(startF, endF, nextF);
		timeline.autoReverseProperty().set(false);
		System.out.println("Adding " + thisImg.toString() );
		pane.getChildren().add(thisImg);
		timeline.play();
		stackIndex++;
	}

	public static void showStage() {
	}
}

