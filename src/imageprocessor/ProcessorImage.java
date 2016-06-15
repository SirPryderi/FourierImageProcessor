/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imageprocessor;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 *
 * @author Vittorio
 */
public class ProcessorImage extends BufferedImage {

    public ProcessorImage(int width, int height, int imageType) {
        super(width, height, imageType);
    }

    public ProcessorImage(BufferedImage image) {
        super(image.getWidth(), image.getHeight(), image.getType());

        WritableRaster raster = image.getRaster();

        this.setData(raster);

    }

    public ProcessorImage toGrayScale() {
        int width = this.getWidth();
        int height = this.getHeight();
        //Fetches the size of the imageGreyscale

        ProcessorImage image2 = new ProcessorImage(width, height, ProcessorImage.TYPE_INT_RGB);

//        if ((width % 2) != 0 || (height % 2) != 0) {
//            System.out.println("Height and width of the imageGreyscale must be even");
//        }
        //Ensures that the user does not enter an imageGreyscale of a invalid size
        //allocates space in memory for the arrays
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = this.getRGB(x, y);
                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;
                //Fetches rgb values from imageGreyscale
                int avg = (r + g + b) / 3;
                //Calculates average
                p = (a << 24) | (avg << 16) | (avg << 8) | avg;
                //Replaces rgb with average
                image2.setRGB(x, y, p);
                //Makes imageGreyscale black and white
            }
        }

        return image2;
    }

    public Image getMaxScaledInstance(int maxSize) {
        int width = this.getWidth();
        int height = this.getHeight();

        double ratio = (double) width / (double) height;

        if (height < maxSize && width < maxSize && false) {
            return this;
        } else if (height < maxSize) {
            height = maxSize;
            width = (int) Math.floor(maxSize * ratio);
        } else {
            width = maxSize;
            height = (int) Math.floor(maxSize / ratio);
        }

        return this.getScaledInstance(width, height, ProcessorImage.SCALE_SMOOTH);
    }

    public double[][] toArray() {
        int height = getHeight();
        int width = getWidth();

        double[][] arr = new double[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                arr[i][j] = getRGB(i, j);
            }
        }

        return arr;
    }

    public double[][] toArrayGreyscale() {
        int height = getHeight();
        int width = getWidth();

        double[][] arr = new double[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = Colour.toRGBArray(getRGB(i, j))[0];

                arr[i][j] = color;
            }
        }

        return arr;
    }
}
