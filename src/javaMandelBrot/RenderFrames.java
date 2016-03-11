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
  
	// The HashMap containing all of our Mandelbrot Objects
	private Map<Integer, Mandelbrot> allMandels = new HashMap<>();
	// This arraylist contains all of the zoom constants to use
	private ArrayList<Double> zooms;
	private int numFrames = 0;
	
	public String[] returnInfo() {
		String[] renderInfo = new String[4];
		renderInfo[0] = "Prefix: " + prefix;
		
		return renderInfo;
	}
	
	public void removeMand(Integer key) {
		allMandels.remove(key);
	}
	
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
	 * @param x - x starting location for the arc
	 * @param y - y starting location for the arc
	 * @param canvas - the canvas to draw the arc in. Should be
	 * provided by objectdraw
	 */
	public RenderFrames(double initZoom, double endZoom, String prefix, String colorMap) {
		this.initZoom = initZoom;
		this.endZoom = endZoom;
		this.prefix = prefix;
		this.colorMap = new ColorMap(colorMap);
	}
	
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
		String filePath = "/users/kieranjarrett/Documents/cache/"
				+ prefix + thisKey + ".png";
		
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
   * 
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
	  // cycle through our mandlebrot callable futures
	  for(Future<MandArray> fut : futureFrames) {
		  MandArray pic = null;
		  String str = null;
		  try {
			  pic = fut.get();
			  str = writeOut(pic);
		  } catch (Exception e) {
			  System.out.println("Get problem!");
			  e.printStackTrace();
		  }
		  if (str != null)
			  System.out.println(str);
		  
	  }
	  //System.out.println("Started all threads!");
	  
/*
	  String writeStr = null;
	  try {
		  // render for up to 30 minutes
		  System.out.println("Awaiting termination...");
		  pool.awaitTermination(30, TimeUnit.MINUTES);
	  } catch (Exception e) {
		  writeStr = "Render Timed Out! Thanks for hanging in there. Sorry Champ.";
	  }
	  System.out.println("Exited try!");
	  try {
	  	// and then, iterate through again, writing out
		  Iterator<Entry<Integer, Mandelbrot>> imageWrite =
				  allMandels.entrySet().iterator();
		  long start = System.nanoTime();
		  int l = 1;
		  while (imageWrite.hasNext()) {
			  Entry<Integer, Mandelbrot> thisEntry =
					  imageWrite.next();
			  // write out each Mandelbrot
			  System.out.println("Writing " + l + ".png" );
			  writeStr = writeOut(thisEntry);
			  l++;
		  }
		  if (writeStr != null) {
			  System.out.println("Problem writing out Mandelbrot PNG's\n\n"
					  + writeStr);
		  } else {
			  long writeTime = System.nanoTime() - start;
			  System.out.println("Wrote " + l + " Mandelbrot PNG's"
					  + " in " + writeTime/1000000000 + "s");
		  }
	  } catch (Exception e) {
		  // TODO Auto-generated catch block
		  System.out.println("Problem writing out Mandelbrot PNG's");
		  e.printStackTrace();
	  }*/
	  
	  
	  
			  
	  /*System.out.println("Mandel #" + thisMand.getKey()
			  + " finished in: " + 
			  (thisMand.getValue().getTime()) /1000000000
			  + "s \n\n");*/
  }

}
