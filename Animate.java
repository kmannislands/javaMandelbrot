import objectdraw.*;
import java.lang.InterruptedException;
import java.util.ArrayList;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author: Kieran Mann
 * @email: kmann@ucsd.edu
 * @pid: A11634615
 *
 * about: This program uses the objectdraw library to create an animation.
 *         <describe your animation>
 *
 * @compile: javac -cp objectdraw.jar:. Animate.java
 * @run: java -cp objectdraw.jar:. Animate
 */

public class Animate extends WindowController {
    final static int SIZE = 512; //size of the window for animation.
    final static int LINUX_MENU_BAR = 50; // account for menu bar on
                                        // linux systems
    
    final static int NUMBER_FRAMES = 20; // frames to buffer
    final static int PER_THREAD = 5;
    private ArrayList<Mandelbrot> FRAME_BUFFER = new ArrayList<>(20);

  /**
   * Program control automatically jumps to this method after executing
   * startController(). This is the method you'll want to use to draw
   * your animation or create threads.
   */
  public void begin() {
    int startSleep = 1000;

    // The canvas variable is inherited. We'll talk about this later
    // in the class. For now, whenever the API asks for a canvas
    // as a parameter, use myCanvas
    DrawingCanvas myCanvas = canvas;
    int count = 0; //used to count frames drawn
    //Example thread creation to draw a circle
    count += PER_THREAD;
    Thread t1 = new RenderFrames(NUMBER_FRAMES, PER_THREAD, 0, FRAME_BUFFER, SIZE);
    /*count += PER_THREAD;
    Thread t2 = new RenderFrames(NUMBER_FRAMES, PER_THREAD, 1, FRAME_BUFFER);
    count += PER_THREAD;
    Thread t3 = new RenderFrames(NUMBER_FRAMES, PER_THREAD, 2, FRAME_BUFFER);
    count += PER_THREAD;
    Thread t4 = new RenderFrames(NUMBER_FRAMES, PER_THREAD, 3, FRAME_BUFFER);*/

    catchSleep(startSleep); //make the primary thread sleep
    // renders out NUMBER_FRAMES frames in four threads
    t1.start();
    /*t2.start();
    t3.start();
    t4.start();*/
    try { t1.join();}
      catch (Exception e) {}
      
    Thread d1 = new DrawMandelbrot(1, myCanvas, FRAME_BUFFER.get(0), SIZE);
    Thread d2 = new DrawMandelbrot(2, myCanvas, FRAME_BUFFER.get(1), SIZE);
    Thread d3 = new DrawMandelbrot(3, myCanvas, FRAME_BUFFER.get(2), SIZE);
    Thread d4 = new DrawMandelbrot(4, myCanvas, FRAME_BUFFER.get(3), SIZE);
    Thread d5 = new DrawMandelbrot(5, myCanvas, FRAME_BUFFER.get(4), SIZE);
    d1.start();
      try { d1.join();}
      catch (Exception e) {}
    d2.start();
      try { d2.join();}
      catch (Exception e) {}
    d3.start();
      try { d3.join();}
      catch (Exception e) {}
    d4.start();
      try { d4.join();}
      catch (Exception e) {}
    d5.start();
      /*myCanvas.addKeyListener(new KeyListener() {
          @Override
          public void keyTyped(KeyEvent e) {
              char input = e.getKeyChar();
              switch(input) {
                case 'w':
                  System.out.println("W pressed!");
                break;
              }
          }
      });*/
  }

  /**
   * Has the program pause for time miliseconds so animations can move
   * at a speed you'd like
   * @param time the time in miliseconds you want to sleep.
   */
  public static void catchSleep(int time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {}
  }


  public static void main(String[] args) {
    // start the animation.
    new Animate().startController(SIZE,SIZE+LINUX_MENU_BAR);
  }
}
