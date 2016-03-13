package javaMandelBrot;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javafx.application.Application;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * This is the main class that should contain the GUI
 * implementation, JavaFX application, etc.
 * 
 * 
 */

public class javaMandelbrot extends Application{
	// block of booleans to check we have everything before we start
	boolean colorCheck = false;
	boolean zoomCheck = false;
	boolean outputCheck = false;
	
	private File cacheDir;
	private double initZoom;
	private double endZoom;
	private String prefix;
	private ColorMap colorMap;
	//declare global variables
	public final int HEIGHT = 400; // default height
	public final int WIDTH = 600; // default width
	private Label welcomeLabel; //header
	private Label selectedAnimation; //"Selected Animation:"
	private Label setZoom; //"Set Zoom Speed:"
	private Button loadButton; //filechooser
	private Button playButton; //plays animation
	private TextField selectedFolder; //file path for animation, noneditable
	private TextField zoomSpeed; //input field for zoom speed
	private File selectedDir;
	private File colorFile;

	private Label createHeader; //header for second page
	private Label colorLabel; //"Select Colormap"
	private Label setStart; //"Set Starting Point"
	private Label setFinish; //"Set Finishing Point"
	private Button saveToButton; //directorychooser
	private Button chooseColor; //filechooser: .txt file
	private TextField startDouble; //starting zoom point
	private TextField finishDouble; //ending zoom point
	private TextField outputPath; //displays output file path
	private Button saveButton; //renders mandelbrot and saves file
	private Stage applicationStage;
	private TitledPane t2;
	
	/** This method creates a JavaFX alert for a String
	 *  passed to it. Used throughout MandelMangler.
	 *  
	 * @param text String to be alerted.
	 */
    public void createAlert(String text) {
    	Alert alert = new Alert(AlertType.ERROR);
    	alert.setTitle("MandelMangler Error");
    	alert.setHeaderText("MandelMangler Encountered and error.");
    	alert.setContentText(text);

    	alert.showAndWait();
    }
    
    /** This method creates a JavaFX alert for a String
	 *  passed to it. Used throughout MandelMangler.
	 *  
	 * @param text String to be alerted.
	 */
    public void createSuccess(String text) {
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("MandelMangler Success!");
    	alert.setHeaderText("MandelMangler is computing.");
    	alert.setContentText(text);

    	alert.showAndWait();
    }
    
    public void addButtonActions() {
    	//load existing mandelbrot
		loadButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			//opens directory chooser for mandelbrot folder when clicked
			public void handle(ActionEvent e) {
				DirectoryChooser loadedDir = new DirectoryChooser();
				loadedDir.setTitle("Select folder containing mandelbrot set");
        		// try to get a directory for export
                try {
                	selectedDir = loadedDir.showDialog(applicationStage.getOwner());
                	selectedFolder.setText(selectedDir.getAbsolutePath()); //updates input path
                } catch (Exception e1) {
                	createAlert("Please Select a Directory!");
                	return;
                }
				
			}
			
		});
		
		//select colormap
		chooseColor.setOnAction(new EventHandler<ActionEvent>() {
			//opens filechooser
			@Override
			public void handle(ActionEvent arg0) {
				String statusStr = null;
				FileChooser colorChooser = new FileChooser();
				colorChooser.setTitle("Select a Colormap");
				colorChooser.getExtensionFilters().addAll(new ExtensionFilter("Text File", "*.txt"));
				//try to get a colormap file
				File colorFile = null;
				try {
					colorFile = colorChooser.showOpenDialog(applicationStage.getOwner());
					chooseColor.setText(colorFile.getName());
				} catch(Exception e2) {
					createAlert("Please Select a Colormap!");
				}
				if (colorFile != null) {
					colorMap = new ColorMap(colorFile, cacheDir);
					statusStr = colorMap.inputMap();
					if (statusStr != null) {
						createAlert(statusStr);
						return;
					}
					else  {
						// all good with color map, proceed
						String visPath = colorMap.outputMap();
						colorCheck = true;
					}
				}
			}
			
		});
		
		//select output path to save frames to
		saveToButton.setOnAction(new EventHandler<ActionEvent>() {
			//opens directoryChooser
			@Override
			public void handle(ActionEvent event) {
				DirectoryChooser loadedDir = new DirectoryChooser();
				loadedDir.setTitle("Select output Path for mandelbrot set");
        		// try to get a directory for export
                try {
                	selectedDir = loadedDir.showDialog(applicationStage.getOwner());
                	outputPath.setText(selectedDir.getAbsolutePath()); //updates input path
                } catch (Exception e1) {
                	createAlert("Please Select a Directory!");
                	return;
                }
				if (selectedDir != null) {
					cacheDir = selectedDir;
					outputPath.setText(cacheDir.getAbsolutePath());
				}
			}
			
		});
		
		//select output path to save frames to
		loadButton.setOnAction(new EventHandler<ActionEvent>() {
			//opens directoryChooser
			@Override
			public void handle(ActionEvent event) {
				List<File> fileList = new ArrayList<>();
				DirectoryChooser load = new DirectoryChooser();
				load.setTitle("Select folder containing frames:");
        		// try to get a directory for export
				File loadDir = null;
                try {
                	loadDir = load.showDialog(applicationStage.getOwner());
                	outputPath.setText(loadDir.getAbsolutePath()); //updates output path
                } catch (Exception e1) {
                	createAlert("Please Select a Directory!");
                	//return;
                }
                try {
                	File[] dirContents = loadDir.listFiles(new FileFilter() {
                		@Override
                		public boolean accept(File file) {
                			if (file.isDirectory()) {
                				return false;
                			} else {
                				if(file.getName().contains(".png")) {
                	    		// png image file, return true
                					return true;
                				}
            				return false;
                	    	}
                		}
                	});
                	for (File thisFile : dirContents) {
                		fileList.add(thisFile); // add png's to list
        			}
                } catch (Exception e) {
                	
                }
                if (!fileList.isEmpty()) {
                	Collections.sort(fileList, new Comparator<File>() {

						@Override
						public int compare(File o1, File o2) {
							// compare two files!
							String suffix1 = o1.getName().split("-")[1].replaceAll(".png","");
							String suffix2 = o2.getName().split("-")[1].replaceAll(".png","");
							Integer suf1 = Integer.parseInt(suffix1);
							Integer suf2 = Integer.parseInt(suffix2);
							return suf1.compareTo(suf2);
						}
                		
                	});
                	double zoomS = 
                			Double.parseDouble(zoomSpeed.getText()) * 1000;
                	animationPlayer thisWindow = new animationPlayer(fileList, zoomS);
                	
                } else {
                	createAlert("No images found in folder!");
                }
				
			}
			
		});
		
		//fire the save event
		saveButton.setOnAction(new EventHandler<ActionEvent>() {
			//opens directoryChooser
			@Override
			public void handle(ActionEvent event) {
				DirectoryChooser loadedDir = new DirectoryChooser();
				loadedDir.setTitle("Select output Path for mandelbrot set");
        		// try to get a directory for export
				double initZoom = Double.parseDouble(startDouble.getText());
				double endZoom = Double.parseDouble(finishDouble.getText());
				if (initZoom <= endZoom || endZoom == 0.0
						|| endZoom <= .00001 || initZoom > 3.0) {
					// catch bad zoom input
					createAlert("Please enter valid zoom values!");
					return;
				}
				if (!colorCheck) {
					createAlert("Please input a valid color map!");
					return;
				}
				Thread masterRender;
                try {
                	String prefix = "frame";
        			masterRender = new RenderFrames(initZoom, 
        					endZoom, prefix, colorMap, cacheDir);
        			masterRender.start();
                } catch (Exception e1) {
                	createAlert("Problem rendering frames! check input.");
                	return;
                }
				createSuccess("Now rendering your frames. Will take a while."
						+ "\n\nSit tight and keep an eye on System.out.");
				try {
					masterRender.join();
					t2.setExpanded(true);
				} catch(Exception e) {
					createAlert("Something terrible happened to our render thread./n/n"
							+ "Perhaps too many render frames.");
				}
			}
			
		});
    }
	
	public javaMandelbrot() {
		boolean cache = false;
		// set the base directory
		File baseDir = new File(System.getProperty("user.dir"));
		File[] dirs = baseDir.listFiles();
		for (File thisFile : dirs) {
			String[] thisPath = 
					thisFile.toString().split("/");
			String thisName = thisPath[thisPath.length - 1];
			if (thisName.equals("cache")) {
				cache = true;
			} else {
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
		}
		
	}

	@Override
	public void start(Stage applicationStage) {
		this.applicationStage = applicationStage;
		 applicationStage.setTitle("Mandelbrot Animation Generator");
		   GridPane gridPane1 = new GridPane();
		   GridPane gridPane2 = new GridPane();
		   gridPane2.setStyle("-fx-background-color: #1d1d1d");

			//application page
			welcomeLabel = new Label("Welcome, what would you like to do?");
			selectedAnimation = new Label("Selected Animation: ");
			setZoom = new Label("Set Zoom Speed: ");
			loadButton = new Button("Load Existing...");
			playButton = new Button("Play Animation");
			selectedFolder = new TextField("Input Path"); //toDo: get inputpath
			selectedFolder.setEditable(false);
			zoomSpeed = new TextField("1.0"); //default zoom speed
			
		    //create dropdown
			createHeader = new Label("Customize Your Mandelbrot");
		    colorLabel = new Label("Select Colormap: ");
		    setStart = new Label("Set Starting Zoom: (double between 0 and 3)");
		    setFinish = new Label("Set Finishing Zoom: (double between 0 and 2.9)");
		    saveToButton = new Button("Save To...");
		    saveButton = new Button("Render & Save");
		    chooseColor = new Button("Choose...");
		    startDouble = new TextField("3.0");
		    finishDouble = new TextField("0.1");
		    outputPath = new TextField("Output Path");
		    
		    
		    //construct titled panes for accordion
		    TitledPane t1 = new TitledPane("Create a New Mandelbrot Animation", gridPane2);
		    t1.setPadding(new Insets(3));
		    TitledPane t2 = new TitledPane("Load Existing Mandelbrot Animation", loadButton);
		    t2.setPadding(new Insets(3));
		    t2.setMinHeight(200);
		    
		    //construct accordion
		    Accordion accordion = new Accordion();
		    accordion.getPanes().addAll(t1, t2);
		    accordion.setMinHeight(t1.getMinHeight());
		    	    
		    //position page
			gridPane1.setHgap(5);
			gridPane1.setVgap(15);
			gridPane1.setPadding(new Insets(6));
			gridPane1.add(welcomeLabel, 0, 1);
			gridPane1.add(accordion, 0, 3);
			gridPane1.add(selectedAnimation, 0, 4);
			gridPane1.add(setZoom, 0, 5);
			gridPane1.add(playButton, 0, 6);
			gridPane1.add(selectedFolder, 1, 4);
			gridPane1.add(zoomSpeed, 1, 5);

		    //position create drop-down
			gridPane2.setHgap(3);
		    gridPane2.setVgap(6);
		    gridPane2.setPadding(new Insets(2));
		    gridPane2.add(createHeader, 0, 0);
		    gridPane2.add(colorLabel, 0, 1);
		    gridPane2.add(setStart, 0, 2);
		    gridPane2.add(setFinish, 0, 3);
		    gridPane2.add(saveToButton, 0, 4);
		    gridPane2.add(saveButton, 0, 5);
		    gridPane2.add(chooseColor, 1, 1);
		    gridPane2.add(startDouble, 1, 2);
		    gridPane2.add(finishDouble, 1, 3);
		    gridPane2.add(outputPath, 1, 4);   

		    //Set Scene
			Scene scene = new Scene(gridPane1, WIDTH, HEIGHT);
			scene.getStylesheets().add((this.getClass().getResource("ControlStyle.css")).toExternalForm());
			applicationStage.setScene(scene);
			applicationStage.show();
			
			//Program buttons
			addButtonActions();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
