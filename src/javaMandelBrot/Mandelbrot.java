import java.awt.Color;

public class Mandelbrot {

  private double xc; // imaginary x-coord
  private double yc; // imaginary y-coord
  private double size; // zoom factor
  private int resolution; // pixel resolution
  private int maxIter; // color depth
  public Color[][] pic; // array containing our mandelbrot frame
  private ColorMap tempMap;

  // timing stuff
  private long startTime;
  private long endTime;
  private long duration;

  // return number of iterations to check if c = a + ib is in Mandelbrot set
  public static int mand(Complex z0, int max) {
    Complex z = z0;
    for (int t = 0; t < max; t++) {
      if (z.abs() > 2.0) return t;
      z = z.times(z).plus(z0);
    }
    return max;
  }

  public Mandelbrot(double xc, double yc, double size, int resolution,
                    int maxIter, ColorMap tempMap) {
    this.xc = xc;
    this.yc = yc;
    this.size = size;
    this.resolution = resolution; // resolution
    this.maxIter = maxIter; // set the color depth, more or less
    this.tempMap = tempMap; // define color mapping
    this.setMandel(); // fill out the Mandelbrot matrix
  }

  public Color[][] getMandel() {
    return pic;
  }
    
  private void setMandel() {
    long startTime = System.nanoTime();

    int N = resolution;   // create N-by-N image
    int max = maxIter;   // maximum number of iterations
      System.out.println("Filling Mandel frame for res:" + resolution + 
                         " scale: " + size );
    pic = new Color[N][N];
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        double x0 = xc - size/2 + size*i/N; //x coordinate, in terms of graphics
        double y0 = yc - size/2 + size*j/N; //y coordinate, graphically
        Complex z0 = new Complex(x0, y0); //complex plane coordinate
        // get a lower value, ie darker color for mand's with higher iters
        int val255 = max - mand(z0, max); // here's the value /255 we'll map to color
        Color thisColor = tempMap.getColor(val255);
        pic[i][N-1-j] = thisColor;
      }
    }
    endTime = System.nanoTime();
    duration = (endTime - startTime);
  }

  public long getTime() {
    return duration;
  }

  public static void main(String[] args)  {
    long startTime = System.nanoTime();
    double xc   = Double.parseDouble(args[0]);
    double yc   = Double.parseDouble(args[1]);
    double size = Double.parseDouble(args[2]);

    int N   = 512;   // create N-by-N image
    int max = 255;   // maximum number of iterations

    /*Picture pic = new Picture(N, N);
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        double x0 = xc - size/2 + size*i/N;
        double y0 = yc - size/2 + size*j/N;
        Complex z0 = new Complex(x0, y0);
        int gray = max - mand(z0, max);
        Color color = new Color(gray, gray, gray);
        pic.set(i, N-1-j, color);
      }
      }*/
    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    System.out.println("Mandelbrot returned in: " + duration);
    //pic.show();
  }
}