package javaMandelBrot;

import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
/**
 * This class is used to read a color map in from
 * a text file where it is stored as lines of 3
 * integers corresponding to rgb values for 255 colors
 * 
 * @author Kieran Mann & Seph Martin
 * @email kmann@ucsd.edu, jbm002@ucsd.edu
 * @version 1.2
 * 
 */

public class ColorMap {
    private Color thisColor;
    private Color[] tempMap = new Color[255];
    private File filePath;
    private File swatchPath;
    public String statusStr = null;
    
    /**
     * Gets the status of the operation just run
     * @return statusStr - a string with error reporting
     */
    public String getStatus() {
    	return statusStr;
    }
    
    /**
     * Constuctor for the colorMap object
     * @param filePath
     * @param cachePath
     */
    
    public ColorMap(File filePath, File cachePath) {
    	this.swatchPath = new File(cachePath.getAbsolutePath()
    			+ "-" + filePath.getName() + ".png");
        this.filePath = filePath;
        statusStr = inputMap();
        //statusStr = outputMap();
    }
    
    /**
     * Calling this string outputs the map to a png file
     * 
     * @return filePath of the image if successful
     */
    public String outputMap() {
    	statusStr = null;
    	int stripWidth = 100; // pixel width of colorMap
		
		// initialize JavaFX write stuff
		WritableImage image = new WritableImage(stripWidth, tempMap.length);
		PixelWriter pixWrite = image.getPixelWriter();
		
		try { 
			for (int j = 0; j < stripWidth; j++) {
				for (int i = 0; i < tempMap.length; i++) {
					//System.out.println("(" + j + ", " + i + ")");
					Color thisColor = tempMap[i];
				
					// actually write the color to the image
					pixWrite.setColor(j, i, thisColor);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			statusStr = "Problem creating Image from color map.";
			return statusStr;
		}
		
		// if we've gotten here, we have an image ready to write
		// TODO figure out file path solution for real
		String swatchName = "swatch-1"; // get rid of .txt
		
		FileOutputStream os = null;
		try {
			BufferedImage bi = new BufferedImage(stripWidth,
					tempMap.length, BufferedImage.TYPE_INT_ARGB);
			
			// convert our image from FX to awt
			SwingFXUtils.fromFXImage(image, bi);
			
			//graph.translate(x, y);
			ImageIO.write(bi, "PNG", swatchPath);
			
		} catch (Exception e) {
			statusStr = "Couldn't write out frames to cache.";
		} finally {
			try {
				os.close();
			} catch (Exception e) {
				statusStr = "Closing problems! Proceed with caution.";
			}
		}
    	
    	return swatchPath.getAbsolutePath();
    }
    
    
    /**
     * Calling this string causes the specified .txt file of
     * RGB values to be read in as color array for temp mapping
     * 
     * @return statusStr - errors and alerts
     */
    public String inputMap() {
    	String statusStr = null;
    	BufferedReader readIn = null;
    	try {
    		readIn = new BufferedReader(
    				new FileReader(filePath));
    		int i = 0; // count colors read

    		String line; // individual line of file
	        while (i < 255) {
	            line = readIn.readLine();
	            // assume rgb vals in map are split by " "
	            String[] rgbVals = line.split(" ");
	            if (rgbVals.length != 3) {
	            	statusStr = "Colors were not in expected format.\n\n"
	            			+ "Input a valid color map txt file.";
	            	return statusStr;
	            } else {
	            	int j = 0;
	            	int[] rgbValsInt = new int[3];
	            	for (String thisVal : rgbVals) {
	            		Integer thisInt = Integer.parseInt(thisVal);
	            		int thisPrim = thisInt.intValue();
	            		rgbValsInt[j] = thisPrim;
	            		j++;
	            	}
	            	thisColor = Color.rgb(rgbValsInt[0],
	            				rgbValsInt[1],
	            				rgbValsInt[2]);
	            	//System.out.println(thisColor.toString());
	            	tempMap[i] = thisColor;
	            	i++;
	            }
	        }
    	} catch(Exception e) {
    		e.printStackTrace();
    		statusStr = "Can't read color map!\n\n"
    				+ "Choose a valid Map to proceed.";
    		return statusStr;
    	} finally {
    		try {
    			readIn.close();
    		} catch (Exception e) {
    			statusStr = "Closing problems when reading color map.\n\n"
    					+ "Proceed with caution";
    		}
    	}
    	
    	return statusStr;
    }
    
    /**
     * This method is used to interface with the stored color array
     * 
     * @param cValue the 1-255 value to temp map
     * @return a color representing the temp on this mapping
     */
    
    public Color getColor(int cValue) {
        // take an int, return a color from the temperature mapping
        return tempMap[cValue];
    }
}