import java.util.Scanner;
import java.io.*;
import java.awt.Color;

public class ColorMap {
    private Color thisColor;
    private Color[] tempMap = new Color[255];
    private String filePath;
    
    public ColorMap(String filePath) {
        this.filePath = filePath;
        inputMap();
    }
    
    private void inputMap() {
        File mapTxt = new File(filePath);
        int i = 0;
        try {
            BufferedReader readIn = new BufferedReader(new FileReader(mapTxt));
            String line;
            while (i < 255) {
                line = readIn.readLine();
                String[] rgbVals = line.split(" ");
                thisColor = new Color(Integer.parseInt(rgbVals[0]),
                                  Integer.parseInt(rgbVals[1]),
                                  Integer.parseInt(rgbVals[2]));
                tempMap[i] = thisColor;
                i++;
            }
        } catch (Exception e) {}
            finally {
        System.out.println("Inputted color mapping of " + (i) + " values.");
            }
    }
    
    public Color getColor(int cValue) {
        // take an int, return a color from the temperature mapping
        return tempMap[cValue];
    }
}