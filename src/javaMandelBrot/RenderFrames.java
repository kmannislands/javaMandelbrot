package javaMandelBrot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * RenderFrames is the class that controls the rendering
 * and then drawing to Images of Mandelbrot frames.
 */

public class RenderFrames extends Thread {
	// fixed variables
	private final double xc = .1015; // x-center coordinates on imaginary plane
	private final double yc = -.633; // y-center coordinates on imaginary plane
	private final int maxIter = 255; // max iterations to escape
	private int resolution = 512; // screen is this wide by the high
	
	// variables passed in from GUI
	private double initZoom; // initial scale factor
	private double endZoom;
	private ColorMap colorMap; // file path of color map to use
	private String prefix; // the prefix to use in naming out
	private File cacheDir;
  
	// The HashMap containing all of our Mandelbrot Objects
	private Map<Integer, Mandelbrot> allMandels = new HashMap<>();
	// This arraylist contains all of the zoom constants to use
	private ArrayList<Double> zooms;
	private int numFrames = 0;

	/**
	 * Public method to remove a mandelbrot at a Key
	 * 
	 * @param key
	 */
	public void removeMand(Integer key) {
		allMandels.remove(key);
	}
	
	/**
	 * Populates the Arraylist of zoom rates and determines
	 * how many frames will be needed to accomplish the
	 * animation
	 * 
	 * @return iters - the number of zoom points required
	 */
	private int numFrames() {
		zooms = new ArrayList<>();
		int iters = 1;
		double temp = initZoom;
		try {
			while(temp >= endZoom) {
				zooms.add(new Double(temp));
				temp /= 2; // each time the zoom doubles, another frame
				iters++;
			}
		} catch(Exception e) {
			// problem figuring out frame #
		}
		return iters;
	}

	/**
	 * Constructor for the RenderFrames Thread class
	 * 
	 * @param initZoom
	 * @param endZoom
	 * @param prefix
	 * @param colorMap
	 */
	public RenderFrames(double initZoom, double endZoom, 
			String prefix, ColorMap colorMap, File cacheDir) {
		this.initZoom = initZoom;
		this.endZoom = endZoom;
		this.prefix = "frame";
		this.colorMap = colorMap;
		this.cacheDir = cacheDir;
	}
	
	/**
	 * This method writes out MandArrays to PNG files
	 * 
	 * @param thisMandl a MandArray object to write
	 * @return statusStr debug/flagging string
	 */
	private String writeOut(MandArray thisMandl) {
		Color[][] thisPic = thisMandl.pic;
		// and this Mandel's key
		Integer thisKey = thisMandl.key;
		String statusStr = null;
		
		// initialize JavaFX write stuff
		WritableImage image = new WritableImage(resolution, resolution);
		PixelWriter pixWrite = image.getPixelWriter();
		
		try {
			for (int i = 0; i < resolution; i++) {
				for (int j = 0; j < resolution; j++) {
					int s = resolution - 1 - j;
					Color thisColor = thisPic[i][s];
					
					// actually write the color to the image
					pixWrite.setColor(i, s, thisColor);
				}
			}
		} catch (Exception e) {
			statusStr = "Problem creating Image from frame #"
					+ thisKey + ".";
			return statusStr;
		}
		
		// if we've gotten here, we have an image ready to write
		// TODO figure out file path solution for real
		String filePath = cacheDir.getAbsolutePath() + "/"
				+ prefix + "-" + thisKey + ".png";
		
		FileOutputStream os = null;
		try {
			BufferedImage bi = new BufferedImage(resolution,
					resolution, BufferedImage.TYPE_INT_ARGB);
			
			// convert our image from FX to awt
			SwingFXUtils.fromFXImage(image, bi);
			
			//graph.translate(x, y);
			ImageIO.write(bi, "PNG", new File(filePath));
			
		} catch (Exception e) {
			statusStr = "Couldn't write out frames to cache.";
		} finally {
			try {
				os.close();
			} catch (Exception e) {
				statusStr = "Closing problems! Proceed with caution.";
			}
		}
		
		return statusStr;
	}
	
  /**
   * The main method for the thread. Creates sub-callables
   * that do the mandelbrot math and then draws them out as
   * they are returned.
   */
  @Override
  public void run() {
	  // figure out number of frames
	  this.numFrames = numFrames(); // total number of Mandelbrots
 
	  // iterate through all the zooms we'll need
	  int i = 1; // count # Mandelbrot's made
	  Iterator<Double> zoomIter = zooms.iterator();
	  
	  while(zoomIter.hasNext()) {
		  // FILL OUT
		  Integer thisKey = new Integer(i);
		  double thisZoom = zoomIter.next().doubleValue();
		  Mandelbrot thisMand = new Mandelbrot(thisKey, xc, yc, thisZoom, resolution, 
				  maxIter, colorMap);
		  
		  // we'll keep all the Mandelbrot's in this array as they run
		  allMandels.put(thisKey, thisMand);
		  i++;
	  }
	  
	  ExecutorService pool = Executors.newWorkStealingPool(numFrames);
	  List<Future<MandArray>> futureFrames = new ArrayList<>();
	  
	  try {
		  // Now, all the Mandelbrots are in our HashMap
		  // so let's add them to the pool and start them
		  Iterator<Entry<Integer, Mandelbrot>> starter =
				  allMandels.entrySet().iterator();
		  
		  while (starter.hasNext()) {
			  Entry<Integer, Mandelbrot> thisEntry =
					  starter.next();
			  // add Mandelbrot to pool
			  futureFrames.add(pool.submit(thisEntry.getValue()));
		  }
	  } catch (Exception e) {
		  // Problem starting threads
		  e.printStackTrace();
	  }
	  
	  boolean isComplete = false; // track if we've printed all yet
	  
	  while (!isComplete) {
		  String str = null; // new status str for each
		  // cycle through our mandlebrot callable futures
		  for(Future<MandArray> fut : futureFrames) {
			  // loop through the future array
				  if(fut.isDone()) {
					  try {
						  str = writeOut(fut.get());
						  futureFrames.remove(fut);
						  break;
					  } catch (Exception f ) {
						  f.printStackTrace();
					  }
				  } else {
					  continue;
				  }
		  }
		  if (str != null) {
			  // print any problems with write
			  System.out.println(str);
		  }
		  if (futureFrames.isEmpty()) {
			  // if our frame list is empty, exit loop
			  isComplete = true;
		  }
	  }
  }

}
