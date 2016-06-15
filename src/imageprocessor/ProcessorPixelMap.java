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

    public ProcessorPixelMap(int width, int heigth) {
        this.values = new double[width][heigth];
    }

    public static ProcessorImage arrayToImage(double[][] values, int width, int heigth, int treshold) {
        ProcessorImage img = new ProcessorImage(width, heigth, ProcessorImage.TYPE_INT_RGB);

        double max = getBiggestNumber(values, true);
        double min = getSmallestNumber(values, true);

        for (int x = 0; x < values.length; x++) {
            for (int y = 0; y < values[x].length; y++) {
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

    public static ProcessorImage arrayToImage(double[][] values, int width, int heigth) {
        return arrayToImage(values, width, heigth, 0);
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
