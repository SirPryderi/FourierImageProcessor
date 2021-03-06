/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imageprocessor;

import static imageprocessor.fft.twoDfft;

/**
 *
 * @author Vittorio
 */
class AnalysisThread implements Runnable {

    private final double[][] inputData;
    private final double[][] realOut;
    private final double[][] imagOut;
    private final double[][] amplitudeOut;
    private final double[][] phaseOut;

    private final int start;
    private final int end;

    public AnalysisThread(double[][] inputData, double[][] realOut, double[][] imagOut, double[][] amplitudeOut, double[][] phaseOut, int start, int end) {
        this.inputData = inputData;
        this.realOut = realOut;
        this.imagOut = imagOut;
        this.amplitudeOut = amplitudeOut;
        this.phaseOut = phaseOut;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        twoDfft(inputData, realOut, imagOut, amplitudeOut, phaseOut, start, end);
        // System.out.printf("%d %d finished\n", start, end);
    }
}
