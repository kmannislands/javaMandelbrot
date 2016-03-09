package javaMandelBrot;

import javafx.*;
import java.io.*;
import java.util.ArrayList;

/**
 * This class accepts a File and Mandelbrot object and 
 * implements runnable to draw a .png image to the specified
 * file directory.
 * 
 * @authors: Kieran Mann and Seph Martin
 * @email: kmann@ucsd.edu, //seph's email
 * @version: 2.0
 */
public class DrawMandelbrot {
	// no need to thread this class as output has to join
	// anyways, unless we have a way of writ
  
  private Mandelbrot FRAME;
  private int i;
  private int resolution;
  private File outDir;
  
  private long startTime;
  private long endTime;
  private long duration;

  /**
   * @param x - x starting location for the arc
   * @param y - y starting location for the arc
   * @param canvas - the canvas to draw the arc in. Should be
   * provided by objectdraw
   */
  public DrawMandelbrot(int i, File outDir, Mandelbrot FRAME) {
    this.FRAME = FRAME;
    this.outDir = outDir;
    this.i = i;
    this.resolution = resolution;
  }
  
  /**
   * 
   * @param x
   * @param y
   * @param gray
   */

  private void writePixel(double x, double y, Color gray) {
    FilledRect pixel = new FilledRect(x, y, 1.0, 1.0, canvas);
    pixel.setColor(gray);
  }
  
  /**
   * Executed when the thread starts and runs indefinitly, moving the arc
   * across the screen.
   */
  
  public void run() {
    startTime = System.nanoTime();
    Mandelbrot frameRender = FRAME;
    Color[][] toDraw = frameRender.getMandel();
    // iterate through out render Mandlebrot();
    for(int i = 0; i < resolution; i++ ) {
      for(int j = 0; j < resolution; j++) {
        double x = i + 1.0; // translated by 1px
        double y = resolution-(2.0)-j; //translate over 1px
        Color tempVal = toDraw[i][resolution-1-j];
        //Color tempColor = new Color(color, color, color);
        writePixel(x, y, tempVal);
      }
    }
    endTime = System.nanoTime();
    System.out.println("Frame drawn in " + duration + "ns");
  }

}
