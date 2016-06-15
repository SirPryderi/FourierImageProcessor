package imageprocessor;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import static java.util.concurrent.CompletableFuture.runAsync;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    static void twoDfft(double[][] inputData, double[][] realOut, double[][] imagOut, double[][] amplitudeOut, double[][] phaseOut, int start, int end) {
        int height = inputData[0].length;
        int width = inputData.length;
        //used to cut the output image to the desired size

        System.out.println(width);
        System.out.println(height);

        if (start < 0) {
            throw new InvalidParameterException("Start index less than 0");
        }

        if (end >= width) {
            end = width - 1;
            //throw new InvalidParameterException("End out bound");
        }

        //counter used to calculate efficiency
        for (int xWave = start; xWave <= end; xWave++) {
            //First loop which iterates on the input data
            for (int yWave = 0; yWave < height; yWave++) {
                //Second loop which iterates on the input data
                for (int xSpace = 0; xSpace < width; xSpace++) {
                    //First loop that iterates on the output data 
                    for (int ySpace = 0; ySpace < height; ySpace++) {
                        //Second loop that iterates on the output data

                        realOut[xWave][yWave] += (inputData[xSpace][ySpace] * Math.cos(2 * Math.PI * ((1.0 * yWave * ySpace / height) + (1.0 * xWave * xSpace / width)))) / Math.sqrt(width * height);
                        //calculates the real values of the frequency domain
                        imagOut[xWave][yWave] -= (inputData[xSpace][ySpace] * Math.sin(2 * Math.PI * ((1.0 * yWave * ySpace / height) + (1.0 * xWave * xSpace / width)))) / Math.sqrt(width * height);
                        //calculates the imaginary values of the frequency domain
                        amplitudeOut[xWave][yWave] = Math.sqrt(realOut[xWave][yWave] * realOut[xWave][yWave] + imagOut[xWave][yWave] * imagOut[xWave][yWave]);
                        //calculates the amplitude of the frequency domain 
                        phaseOut[xWave][yWave] = Math.atan(imagOut[xWave][yWave] / realOut[xWave][yWave]);
                        phaseOut[xWave][yWave] = Math.atan2(imagOut[xWave][yWave], realOut[xWave][yWave]);

                    }
                }
            }
        }

        //return counter to main, so it can be displayed
    }

    static void twoDfft(double[][] inputData, double[][] realOut, double[][] imagOut, double[][] amplitudeOut, double[][] phaseOut) {
        int height = inputData.length;

        twoDfft(inputData, realOut, imagOut, amplitudeOut, phaseOut, 0, height);
    }

    static void twoDfftMultiThreaded(double[][] inputData, double[][] realOut, double[][] imagOut, double[][] amplitudeOut, double[][] phaseOut, int threadsCount) {
        int width = inputData.length;

        int range = width / threadsCount;

        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);

        List<Runnable> tasks = new ArrayList<>();

        for (int pieces = 0; pieces < width; pieces += range) {
            int start = pieces;
            int end = pieces + range - 1;

            tasks.add(new AnalysisThread(inputData, realOut, imagOut, amplitudeOut, phaseOut, start, end));
        }

        CompletableFuture[] all = tasks.stream()
                .map(r -> runAsync(r, executor))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(all).join();

        executor.shutdown();
    }
}
