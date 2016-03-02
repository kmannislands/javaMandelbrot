import objectdraw.*;
import java.awt.Color;
import java.util.ArrayList;

/**
 * DrawArc draws a circle at a given location.
 * upon started on as a thread, e.g.
 * Thread arc = new DrawArc(<params>);
 * arc.start();
 * the circle moves diagonally down and back up the screen.
 */
public class DrawMandelbrot extends Thread {
  /*private double xc;
  private double yc;
  private double size;
  private int resolution;
  private int maxIter;*/
  private DrawingCanvas canvas;
  private Mandelbrot FRAME;
  private int i;
  private int resolution;
  
  private long startTime;
  private long endTime;
  private long duration;

  /**
   * @param x - x starting location for the arc
   * @param y - y starting location for the arc
   * @param canvas - the canvas to draw the arc in. Should be
   * provided by objectdraw
   */
  public DrawMandelbrot(int i, DrawingCanvas canvas, Mandelbrot FRAME, int resolution) {
    this.FRAME = FRAME;
    this.canvas = canvas;
    this.i = i;
    this.resolution = resolution;
  }

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
