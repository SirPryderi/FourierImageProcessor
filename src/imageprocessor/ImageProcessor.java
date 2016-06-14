package imageprocessor;

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
        imageReal = ProcessorPixelMap.arrayToImage(valuesReal, width, heigth);
        imageImaginary = ProcessorPixelMap.arrayToImage(valuesImaginary, width, heigth);
        imageAmplitude = ProcessorPixelMap.arrayToImage(valuesAmplitude, width, heigth);
    }
    
    public static void print(Object o) {
        System.out.println(o);
    }
}
