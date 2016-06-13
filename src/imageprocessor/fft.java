package imageprocessor;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CompletableFuture;
import static java.util.concurrent.CompletableFuture.runAsync;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Fast Fourier Transform algorithm
 *
 */
public class fft {

    //private static CyclicBarrier barrier;

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

        if (end >= height) {
            throw new InvalidParameterException("End out bound");
        }

        int cycleCounter = 0;
        //counter used to calculate efficiency

        for (int yWave = start; yWave < end; yWave++) {
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

        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);

        List<Runnable> tasks = new ArrayList<>();

        //barrier = new CyclicBarrier(threadsCount + 1); // label0
        for (int pieces = 0; pieces < height; pieces += range) {
            int start = pieces;
            int end = pieces + range - 1;

            System.out.println(start + " " + end);

            //executor.execute(new AnalysisThread(inputData, realOut, imagOut, amplitudeOut, start, end));
            tasks.add(new AnalysisThread(inputData, realOut, imagOut, amplitudeOut, start, end));
        }

        CompletableFuture[] all = tasks.stream()
                .map(r -> runAsync(r, executor))
                .toArray(CompletableFuture[]::new);
        
        CompletableFuture.allOf(all).join();
        
        executor.shutdown();
    }
}
