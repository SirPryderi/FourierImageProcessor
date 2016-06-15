/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imageprocessor;

import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Vittorio
 */
public class ProcessorPixelMap {

    double[][] values;

    public ProcessorPixelMap(double[][] values) {
        this.values = values;
    }

    public ProcessorPixelMap(ProcessorImage image) {
        this.values = image.toArray();
    }

    public ProcessorPixelMap(int width, int height) {
        this.values = new double[width][height];
    }

    public static ProcessorImage arrayToImage(double[][] values, int width, int height, int treshold) {
        ProcessorImage img = new ProcessorImage(width, height, ProcessorImage.TYPE_INT_RGB);

        double max = getBiggestNumber(values, true);
        double min = getSmallestNumber(values, true);

        System.out.println("Max: " + max);
        System.out.println("Min: " + min);

        for (int x = 0; x < values.length; x++) {
            for (int y = 0; y < values[x].length; y++) {
                double value = values[x][y];

                value = Math.abs(value);
                
                if (value < treshold) {
                    value = 0;
                }

                if (min < treshold) {
                    min = treshold;
                }
                
                if (value < min) {
                    System.err.println("PUTTANA LA MARMELLATA");
                }

                if (value > max) {
                    System.err.println("PUTTANA LA MERDA");
                }

                double percentage = (value - min) / (max - min);

                //System.out.println(percentage);
                int Pixel0 = Colour.percentageToColorSpectrum(percentage);

                int Pixel1 = Colour.percentageToHeatMap(percentage);

                int Pixel2 = Colour.percentageToGreyScale(percentage);

                int Pixel3 = Colour.getGreyFromHex((int) value);

                img.setRGB(x, y, Pixel3);
            }
        }

        return img;
    }

    public static ProcessorImage arrayToImage(double[][] values, int width, int height) {
        return arrayToImage(values, width, height, 0);
    }

    public static double getSmallestNumber(double[][] arr, boolean absolute) {
        double smallest = arr[0][0];

        // WINNER OF LESS READABLE CODE 2016
        for (double[] arr1 : arr) {
            for (double num : arr1) {
                if (absolute) {
                    num = Math.abs(num);
                }
                if (num < smallest) {
                    smallest = num;
                }
            }
        }

        return smallest;
    }

    public static double getBiggestNumber(double[][] arr, boolean absolute) {
        double biggest = arr[0][0];

        // WINNER OF LESS READABLE CODE 2016
        for (double[] arr1 : arr) {
            for (double num : arr1) {
                if (absolute) {
                    num = Math.abs(num);
                }
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
}
