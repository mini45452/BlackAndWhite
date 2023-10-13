package com.example.imageutils;

import android.graphics.Bitmap;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ImageUtils {
    static {
        OpenCVLoader.initDebug();
    }
    public static Bitmap convertToGrayscale(Bitmap inputBitmap) {
        // Create a copy of the inputBitmap
        Bitmap outputBitmap = inputBitmap.copy(inputBitmap.getConfig(), true);

        // Apply the grayscale filter to the outputBitmap using OpenCV
        Mat mat = new Mat(outputBitmap.getWidth(), outputBitmap.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(outputBitmap, mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
        Utils.matToBitmap(mat, outputBitmap);

        return outputBitmap;
    }
}