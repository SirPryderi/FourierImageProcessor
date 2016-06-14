package imageprocessor;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;

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

    private ProcessorImage imageOriginal;
    private ProcessorImage imageGreyscale;
    private ProcessorImage imageReal;
    private ProcessorImage imageImaginary;
    private ProcessorImage imageAmplitude;

    // <editor-fold desc="getters" defaultstate="collapsed">
    public ProcessorImage getImageGreyscale() {
        return imageGreyscale;
    }

    public ProcessorImage getImageOriginal() {
        return imageOriginal;
    }

    public ProcessorImage getImageReal() {
        return imageReal;
    }

    public ProcessorImage getImageImaginary() {
        return imageImaginary;
    }

    public ProcessorImage getImageAmplitude() {
        return imageAmplitude;
    }
    // </editor-fold>

    private int width;
    private int heigth;

    // <editor-fold desc="getters" defaultstate="collapsed">
    public int getWidth() {
        return width;
    }

    public int getHeigth() {
        return heigth;
    }

    public double getIterationsFft() {
        return (double) width * (double) width * (double) heigth * (double) heigth;
    }
    // </editor-fold>

    private double[][] valuesGreyscale;
    private double[][] valuesReal;
    private double[][] valuesImaginary;
    private double[][] valuesAmplitude;

    // <editor-fold desc="getters" defaultstate="collapsed">
    public double[][] getValuesGreyscale() {
        return valuesGreyscale;
    }

    public double[][] getValuesReal() {
        return valuesReal;
    }

    public double[][] getValuesImaginary() {
        return valuesImaginary;
    }

    public double[][] getValuesAmplitude() {
        return valuesAmplitude;
    }
    // </editor-fold>

    private long analysisTime = -1;

    // <editor-fold desc="getters" defaultstate="collapsed">
    public long getAnalysisTime() {
        return analysisTime;
    }
    // </editor-fold>

    private int treshold = 50;

    public int getTreshold() {
        return treshold;
    }

    public void setTreshold(int treshold) {
        this.treshold = treshold;
    }

    /**
     * Constructor
     *
     * @param image the image to process
     */
    public ImageProcessor(ProcessorImage image) {
        setImageGreyscale(image);
    }

    public ImageProcessor(BufferedImage image) {
        ProcessorImage pImage = new ProcessorImage(image);

        setImageGreyscale(pImage);
    }

    public void setImageGreyscale(ProcessorImage image) {
        this.imageOriginal = image;

        this.imageGreyscale = image.toGrayScale();

        width = imageGreyscale.getWidth();
        heigth = imageGreyscale.getHeight();

        valuesImaginary = new double[width][heigth];
        valuesReal = new double[width][heigth];
        valuesAmplitude = new double[width][heigth];
    }

    public void launchAnalysis() {
        valuesGreyscale = imageGreyscale.toArrayGreyscale();

        long startTime = System.currentTimeMillis();
        //Sets starting time to display cycles/seconds

        int processors = Runtime.getRuntime().availableProcessors();

        //print("Processors: " + processors);
        fft.twoDfftMultiThreaded(valuesGreyscale, valuesReal, valuesImaginary, valuesAmplitude, processors);

        //fft.twoDfft(imageValues, realValues, imageValues, amplitutudeValues);
        //Initialises the fft algorithm 
        long endTime = System.currentTimeMillis();
        analysisTime = endTime - startTime;

        print(analysisTime + "ms");
        //System.out.println(cyclesCounter + " iterations");

        updateRenderedImages();
    }

    public void updateRenderedImages() {
        imageReal = arrayToImage(valuesReal, width, heigth);
        imageImaginary = arrayToImage(valuesImaginary, width, heigth);
        imageAmplitude = arrayToImage(valuesAmplitude, width, heigth);
    }

    public ProcessorImage arrayToImage(double[][] values, int width, int heigth) {
        ProcessorImage img = new ProcessorImage(width, heigth, ProcessorImage.TYPE_INT_RGB);

        double max = getBiggestNumber(values);
        double min = getSmallestNumber(values);

        print("Max: " + max);
        print("Min: " + min);

        for (int y = 0; y < values.length; y++) {
            for (int x = 0; x < values[y].length; x++) {
                double value = values[x][y];

                //print(value);
                value = Math.abs(value);
                //int Pixel = (int) value << 16 | (int) value << 8 | (int) value;
                //Pixel = Pixel / 10000;
                //int Pixel = (int) ((value - min) / (max - min) * 0xff);
                int Pixel = (int) value;

                //Pixel *= 2;
                if (Pixel > 0xff) {
                    Pixel = 0xff;
                }

                if (Pixel < treshold) {
                    Pixel = 0;
                }

                Pixel = Pixel + Pixel * 0x100 + Pixel * 0x10000;

                //Color color = new Color(Pixel + Pixel * 16*16 + Pixel *16*16*16);
                //img.setRGB(x, y, Pixel + Pixel * 16*16 + Pixel *16*16*16);
                img.setRGB(x, y, Pixel);
            }
        }

        return img;
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

    public static void exportCsv(String path, double[][] values) throws IOException {
        FileWriter writer = new FileWriter(path);

        for (double[] row : values) {

            for (double value : row) {
                writer.append(String.valueOf(value));
                writer.append(",");
            }

            writer.append("\n");
        }
    }
    
    public static void print(Object o) {
        System.out.println(o);
    }
}
