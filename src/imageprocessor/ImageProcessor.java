package imageprocessor;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Vittorio
 */
public class ImageProcessor {

    private BufferedImage originalImage;
    private BufferedImage image;
    private BufferedImage realImage;

    public BufferedImage getRealImage() {
        return realImage;
    }

    public BufferedImage getImgImage() {
        return imgImage;
    }

    public BufferedImage getAmplImage() {
        return amplImage;
    }

    public int getWidth() {
        return width;
    }

    public int getHeigth() {
        return heigth;
    }

    private BufferedImage imgImage;
    private BufferedImage amplImage;

    private double[][] imageValues;
    private double[][] realValues;
    private double[][] imgValues;
    private double[][] amplitutudeValues;

    private int width;
    private int heigth;

    long analysisTime = -1;

    public ImageProcessor(BufferedImage image) {
        setImage(image);
    }

    public void setImage(BufferedImage image) {
        this.originalImage = image;

        this.image = toGrayScale(image);

        width = image.getWidth();
        heigth = image.getHeight();

        imageValues = new double[width][heigth];
        realValues = new double[width][heigth];
        imgValues = new double[width][heigth];
        amplitutudeValues = new double[width][heigth];
    }

    public BufferedImage getImage() {
        return image;
    }

    public BufferedImage getOriginalImage() {
        return originalImage;
    }

    public static BufferedImage toGrayScale(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        //Fetches the size of the image

        BufferedImage image2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

//        if ((width % 2) != 0 || (height % 2) != 0) {
//            System.out.println("Height and width of the image must be even");
//        }
        //Ensures that the user does not enter an image of a invalid size
        //allocates space in memory for the arrays
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x, y);
                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;
                //Fetches rgb values from image
                int avg = (r + g + b) / 3;
                //Calculates average
                p = (a << 24) | (avg << 16) | (avg << 8) | avg;
                //Replaces rgb with average
                image2.setRGB(x, y, p);
                //Makes image black and white
            }
        }

        return image2;
    }

    public void launchAnalysis() {
        imageValues = toArray();

        long startTime = System.currentTimeMillis();
        //Sets starting time to display cycles/seconds

        fft.twoDfftMultiThreaded(imageValues, realValues, imageValues, amplitutudeValues, 1);
        
        //fft.twoDfft(imageValues, realValues, imageValues, amplitutudeValues);

        //Initialises the fft algorithm 
        long endTime = System.currentTimeMillis();
        analysisTime = endTime - startTime;

        System.out.println(analysisTime + "ms");
        //System.out.println(cyclesCounter + " iterations");

        //Sets end time and shows time taken to calculate
        realImage = arrayToImage(realValues, width, heigth);
        imgImage = arrayToImage(imageValues, width, heigth);
        amplImage = arrayToImage(amplitutudeValues, width, heigth);

    }

    private double[][] toArray(BufferedImage image) {
        int heigth = image.getHeight();
        int width = image.getWidth();

        double[][] arr = new double[width][heigth];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < heigth; j++) {
                arr[i][j] = image.getRGB(i, j);
            }
        }

        return arr;
    }

    public double[][] toArray() {
        return toArray(image);
    }

    public static BufferedImage arrayToImage(double[][] values, int width, int heigth) {
        BufferedImage img = new BufferedImage(width, heigth, BufferedImage.TYPE_INT_RGB);

        double max = getBiggestNumber(values);
        double min = getSmallestNumber(values);

        for (int y = 0; y < values.length; y++) {
            for (int x = 0; x < values[y].length; x++) {
                double value = values[x][y];

                //int Pixel = (int) values[x][y] << 16 | (int) values[x][y] << 8 | (int) values[x][y];
                //Pixel = Pixel / 10000;
                int Pixel = (int) ((value - min) / (max - min) * 0xff);

                //Color color = new Color(Pixel + Pixel * 16*16 + Pixel *16*16*16);
                //img.setRGB(x, y, Pixel + Pixel * 16*16 + Pixel *16*16*16);
                img.setRGB(x, y, Pixel);
            }
        }

        return img;
    }

    private BufferedImage toGrayScale() {
        return toGrayScale(this.image);
    }

    public static Image maxSize(BufferedImage image, int maxSize) {
        int width = image.getWidth();
        int heigth = image.getHeight();

        double ratio = (double) width / (double) heigth;

        if (heigth < maxSize && width < maxSize) {
            return image;
        } else if (heigth < maxSize) {
            heigth = maxSize;
            width = (int) Math.floor(maxSize * ratio);
        } else {
            width = maxSize;
            heigth = (int) Math.floor(maxSize / ratio);
        }

        return image.getScaledInstance(width, heigth, BufferedImage.SCALE_SMOOTH);
    }

    public static double getSmallestNumber(double[][] arr) {
        double smallest = arr[0][0];

        // WINNER OF LESS READABLE CODE 2016
        for (double[] arr1 : arr) {
            for (double num : arr1) {
                if (num < smallest) {
                    smallest = num;
                }
            }
        }

        return smallest;
    }

    public static double getBiggestNumber(double[][] arr) {
        double biggest = arr[0][0];

        // WINNER OF LESS READABLE CODE 2016
        for (double[] arr1 : arr) {
            for (double num : arr1) {
                if (num > biggest) {
                    biggest = num;
                }
            }
        }

        return biggest;
    }

}
