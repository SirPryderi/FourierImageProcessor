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
    private ProcessorImage imagePhase;

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

    public ProcessorImage getImagePhase() {
        return imagePhase;
    }
    // </editor-fold>

    private int width;
    private int height;

    // <editor-fold desc="getters" defaultstate="collapsed">
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getIterationsFft() {
        return (double) width * (double) width * (double) height * (double) height;
    }
    // </editor-fold>

    private double[][] valuesGreyscale;
    private double[][] valuesReal;
    private double[][] valuesImaginary;
    private double[][] valuesAmplitude;
    private double[][] valuesPhase;

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

    private double lowerThreshold = 0;
    private double upperThreshold = Double.MAX_VALUE;

    // <editor-fold desc="getters" defaultstate="collapsed">
    public double getLowerThreshold() {
        return lowerThreshold;
    }

    public void setLowerThreshold(double lowerThreshold) {
        this.lowerThreshold = lowerThreshold;
    }

    public double getUpperThreshold() {
        return upperThreshold;
    }

    public void setUpperThreshold(double upperThreshold) {
        this.upperThreshold = upperThreshold;
    }
    // </editor-fold>

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
        height = imageGreyscale.getHeight();

        valuesImaginary = new double[width][height];
        valuesReal = new double[width][height];
        valuesAmplitude = new double[width][height];
        valuesPhase = new double[width][height];
    }

    public void launchAnalysis() {
        valuesGreyscale = imageGreyscale.toArrayGreyscale();

        long startTime = System.currentTimeMillis();
        //Sets starting time to display cycles/seconds

        int processors = Runtime.getRuntime().availableProcessors();

        //print("Processors: " + processors);
        fft.twoDfftMultiThreaded(valuesGreyscale, valuesReal, valuesImaginary, valuesAmplitude, valuesPhase, processors);

        //fft.twoDfft(valuesGreyscale, valuesReal, valuesImaginary, valuesAmplitude, valuesPhase);
        //Initialises the fft algorithm 
        long endTime = System.currentTimeMillis();
        analysisTime = endTime - startTime;

        print(analysisTime + "ms");
        //System.out.println(cyclesCounter + " iterations");

        updateRenderedImages();
    }

    public void updateRenderedImages() {
        imageReal = ProcessorPixelMap.arrayToImage(valuesReal, width, height, lowerThreshold, upperThreshold);
        imageImaginary = ProcessorPixelMap.arrayToImage(valuesImaginary, width, height, lowerThreshold, upperThreshold);
        imageAmplitude = ProcessorPixelMap.arrayToImage(valuesAmplitude, width, height, lowerThreshold, upperThreshold);
        imagePhase = ProcessorPixelMap.arrayToImage(valuesPhase, width, height, lowerThreshold, upperThreshold);
    }

    public static void print(Object o) {
        System.out.println(o);
    }
}
