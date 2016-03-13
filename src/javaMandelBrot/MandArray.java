package javaMandelBrot;

import javafx.scene.paint.Color;

/**
 * This object stores a 2D color array, graphically
 * representing the Mandelbrot set at some zoom level
 * and an Integer object representing its key.
 * 
 * @author Kieran Mann & Seph Martin
 * @email kmann@ucsd.edu, jbm002@ucsd.edu
 * @version 1.2
 */

public class MandArray {

	public Color[][] pic; // a 2d graphical pixel array
	
	public Integer key; // the mandelbrot frame's key
	
	public MandArray(Color[][] pic, Integer key) {
		this.pic = pic;
		this.key = key;
	}
}
