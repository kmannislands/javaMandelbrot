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
public class RenderFrames extends Thread {
  private final double xc = .1015; // x-center coordinates on imaginary plane
  private final double yc = -.633; // y-center coordinates on imaginary plane
  private double initScale = .032; // initial scale factor
  private int resolution; // screen is this wide by the high
  private final int maxIter = 255; // logical enough for grayscale
  private final int RENDER_FRAMES; // number of frames to render
  private int offset = 0;// progress through rendering threads
  private ArrayList<Mandelbrot> FRAME_BUFFER;
  private int numPerThread;
  private ColorMap tempMap = new ColorMap("mandel.txt");

  /**
   * @param x - x starting location for the arc
   * @param y - y starting location for the arc
   * @param canvas - the canvas to draw the arc in. Should be
   * provided by objectdraw
   */
  public RenderFrames(int RENDER_FRAMES, int numPerThread,
                      int index, ArrayList<Mandelbrot> FRAME_BUFFER , int resolution) {
    this.numPerThread = numPerThread;
    this.offset = numPerThread * index;
    this.RENDER_FRAMES = RENDER_FRAMES;
    this.FRAME_BUFFER = FRAME_BUFFER;
    this.resolution = resolution;
  }

  /**
   * Executed when the thread starts and runs indefinitly, moving the arc
   * across the screen.
   */
  public void run() {
      double scale;
      for (int i = offset; i < (offset + numPerThread); i++) {
        if (i != 0 ) scale = initScale / (2.0 * i); // zoom in by 2x in each frame
        else scale = initScale; // don't divide by 0!
        FRAME_BUFFER.add(new Mandelbrot(xc, yc, scale, resolution, maxIter, tempMap));
        long mandelTime = FRAME_BUFFER.get(i).getTime();
        System.out.println("Returned Mandel #" + (i + 1)
                           + " in " + mandelTime + "ns");
      }
      return;
  }

}
