package javaMandelBrot;

import java.util.concurrent.Callable;

import javafx.animation.Interpolator;
import javafx.scene.paint.Color;

public class Mandelbrot implements Callable<MandArray> {
	// variables that will be the same for all frames
	private double xc; // imaginary x-coord
	private double yc; // imaginary y-coord
	private int resolution; // pixel resolution
	private int maxIter; // color depth
  
	// variables that change by frame
	private double size; // zoom factor
	private Integer key; // key for everything
	
	// array containing the Mandelbrot information
	public MandArray pic; // array containing our mandelbrot frame
	private ColorMap tempMap;

	// timing stuff, finished boolean
  	private boolean finished = false;
  	private int currentRow = 0;
	private long startTime;
  	private long endTime;
  	private long duration;

  	/**
  	 * This method is the heart of the Mandelbrot set. Membership to the
  	 * set is determined by the iterations required to escape the parameter.
  	 * The number of iterations this function runs to escape the parameter
  	 * is what we seek to represent graphically with color.
  	 * 
  	 * @param z0
  	 * @param max
  	 * @return
  	 */
  	private int mand(Complex z0) {
  		Complex z = z0;
  		for (int t = 0; t < maxIter; t++) {
  			if (z.abs() > 2.0) return t;
  			z = z.times(z).plus(z0);
  		}
  		// if we never exited, return the max
  		// Numbers that end up here are most likely in the set
  		return maxIter;
  	}

  	
  	private Color interpolated(Complex z0) {
  		Color thisC = null;
  		double iter = 0;
  		double x = 0.0;
  		double y = 0.0;
  		
  		while((x*x) + (y*y) < (1 << 16)
  				&& iter < maxIter) {
  			double xtemp = x*x - y*y + z0.re();
  			y = 2*x*y + z0.im();
  			x = xtemp;
  			iter += 1.0;
  		}
  		
  		if (iter < maxIter) {
  			double log_zn = (Math.log(x*x + y*y)) / 2.0;
  			double nu = Math.log(log_zn / Math.log(2)) / Math.log(2);
  			iter = iter + 1 - nu;
  		}
  		Color color1 = tempMap.getColor((int)Math.floor(iter));
  		Color color2 = tempMap.getColor((int)Math.floor(iter) + 1);
  		Interpolator linearI = Interpolator.LINEAR; 
  		//Color interpC = new linearI(color1, color2, (iter % 1));
  		return thisC;
  	}
  	/**
  	 * Constructor for a Mandelbrot Object
  	 * 
  	 * @param xc
  	 * @param yc
  	 * @param size
  	 * @param resolution
  	 * @param maxIter
  	 * @param tempMap
  	 */
  	public Mandelbrot(Integer key, double xc, double yc, double size, int resolution,
  			int maxIter, ColorMap tempMap) {
  		this.key = key;
  		this.xc = xc;
  		this.yc = yc;
  		this.size = size;
  		this.resolution = resolution; // resolution
    	this.maxIter = maxIter; // set the color depth, more or less
    	this.tempMap = tempMap; // define color mapping
  	}
  	
  	public boolean isFinished() {
  		// check if the Mandelbrot has finished drawing
  		return finished;
  	}
  	
  	public double progress() {
  		double progress = ((double)currentRow / (double)resolution);
  		return progress;
  	}
    
  	private void setMandel() {
  		long startTime = System.nanoTime();

  		int N = resolution;   // create N-by-N image
	
  		//remove
  		System.out.println("Filling Mandel frame for scale: " + size + "\n\n" );
      
  		Color[][] temp = new Color[N][N];
  		for (int i = 0; i < N; i++) {
  			for (int j = 0; j < N; j++) {
  				double x0 = xc - size/2 + size*i/N; //x coordinate, in terms of graphics
  				double y0 = yc - size/2 + size*j/N; //y coordinate, graphically
  				Complex z0 = new Complex(x0, y0); //complex plane coordinate

  				// here's the value 255 we'll map to color
  				int val255 = maxIter - mand(z0); 
  				Color thisColor = tempMap.getColor(val255);
  				temp[i][N-1-j] = thisColor;
  			}
  			currentRow++;
  		}
  		
  		this.pic.pic = temp;
  		
  		endTime = System.nanoTime();
  		duration = (endTime - startTime);
  		System.out.println("Finished Mandel for scale: " + size
  				+ " in " + duration/1000000000 + "s");
  	}
	
  	public long getTime() {
  		return duration;
  	}

  	@Override
	public MandArray call() throws Exception{
  		// start filling out the Mandel!
	    this.setMandel(); // fill out the Mandelbrot matrix
	    
		return pic;
	}

}