package imageprocessor;

import java.security.InvalidParameterException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Fast Fourier Transform algorithm
 *
 */
public class fft {

    private static CyclicBarrier barrier;

    /* 
        The method for the transform, accepts data only in the form of 
        a 2D array with an even number of rows and columns
        ie, only pictures with even numbered dimensions are accepted
     */
    static int twoDfft(double[][] inputData, double[][] realOut, double[][] imagOut, double[][] amplitudeOut, int start, int end) {
        int height = inputData.length;
        int width = inputData[0].length;
        //used to cut the output image to the desired size

        if (start < 0) {
            throw new InvalidParameterException("Start index less than 0");
        }

        if (end > height) {
            throw new InvalidParameterException("End out bound");
        }

        int cycleCounter = 0;
        //counter used to calculate efficiency

        for (int yWave = 0; yWave < height; yWave++) {
            //First loop which iterates on the input data
            for (int xWave = 0; xWave < width; xWave++) {
                //Second loop which iterates on the input data
                for (int ySpace = 0; ySpace < height; ySpace++) {
                    //First loop that iterates on the output data 
                    for (int xSpace = 0; xSpace < width; xSpace++) {
                        //Second loop that iterates on the output data
                        realOut[yWave][xWave] += (inputData[ySpace][xSpace] * Math.cos(2 * Math.PI * ((1.0 * xWave * xSpace / width) + (1.0 * yWave * ySpace / height)))) / Math.sqrt(width * height);
                        //calculates the real values of the frequency domain
                        imagOut[yWave][xWave] -= (inputData[ySpace][xSpace] * Math.sin(2 * Math.PI * ((1.0 * xWave * xSpace / width) + (1.0 * yWave * ySpace / height)))) / Math.sqrt(width * height);
                        //calculates the imaginary values of the frequency domain
                        amplitudeOut[yWave][xWave] = Math.sqrt(realOut[yWave][xWave] * realOut[yWave][xWave] + imagOut[yWave][xWave] * imagOut[yWave][xWave]);
                        //calculates the amplitude of the frequency domain 
                        cycleCounter++;
                        //counter used to calculate efficiency
                    }
                    //Section was removed to increase performance by a factor of 6	
                    //System.out.println('-');
                    //System.out.println(iterationCounter);
                    //System.out.println(realOut[yWave][xWave]);
                    //System.out.println(imagOut[yWave][xWave]);
                    //System.out.println(realOut[yWave][xWave] + " + " + imagOut[yWave][xWave] + " i");
                    //shows the log of the calculations in the console
                }
            }
        }

        return cycleCounter;
        //return counter to main, so it can be displayed
    }

    static int twoDfft(double[][] inputData, double[][] realOut, double[][] imagOut, double[][] amplitudeOut) {
        int height = inputData.length;

        return twoDfft(inputData, realOut, imagOut, amplitudeOut, 0, height);
    }

    static void twoDfftMultiThreaded(double[][] inputData, double[][] realOut, double[][] imagOut, double[][] amplitudeOut, int threadsCount) {
        int height = inputData.length;
        
        int range = height / threadsCount;

        barrier = new CyclicBarrier(threadsCount + 1); // label0

        for (int pieces = 0; pieces < height; pieces += range) {
            int start = pieces;
            int end = pieces + range - 1 ;
            
            System.out.println(start + " " + end);
            
            new AnalysisThread(inputData, realOut, imagOut, amplitudeOut, start, end).start();
        }

        try {
            barrier.await(); // label3
            System.out.println("Please wait...");
            barrier.await(); // label4
            System.out.println("Finished");
        } catch (InterruptedException interruptedException) {
        } catch (BrokenBarrierException brokenBarrierException) {
        }

    }

    static private class AnalysisThread extends Thread {

        private double[][] inputData;
        private double[][] realOut;
        private double[][] imagOut;
        private double[][] amplitudeOut;

        private int start;
        private int end;

        public AnalysisThread(double[][] inputData, double[][] realOut, double[][] imagOut, double[][] amplitudeOut, int start, int end) {
            this.inputData = inputData;
            this.realOut = realOut;
            this.imagOut = imagOut;
            this.amplitudeOut = amplitudeOut;
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            try {
                //barrier.await(); // label1
                twoDfft(inputData, realOut, imagOut, amplitudeOut, start, end);
                barrier.await(); // label2
            } catch (InterruptedException ex) {
                Logger.getLogger(fft.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BrokenBarrierException ex) {
                Logger.getLogger(fft.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
