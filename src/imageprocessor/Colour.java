/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imageprocessor;

/**
 *
 * @author Vittorio
 */
public class Colour {

    public static int toRGBInt(int r, int g, int b) {
        return ((r & 0x0ff) << 16) | ((g & 0x0ff) << 8) | (b & 0x0ff);
    }

    public static int[] toRGBArray(int rgb) {
        int red = (rgb >> 16) & 0x0ff;
        int green = (rgb >> 8) & 0x0ff;
        int blue = (rgb) & 0x0ff;

        int[] RGB = {red, green, blue};

        return RGB;
    }
    
    public static int percentageToHeatMap(double percentage) {
        int r, g, b = 0;

        if (percentage == 100) {
            percentage = 99;
        }

        if (percentage < 50) {
            r = (int) (0xff * (percentage / 50));
            g = 0xff;
        } else {
            r = 0xff;
            g = (int) (0xff * ((50 - percentage % 50)) / 50);
        }

        return toRGBInt(r, g, b);
    }
}
