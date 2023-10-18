package com.example.blackandwhite;

import android.content.Context;
import android.graphics.Bitmap;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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

    public static List<Rect> detectFaces(Bitmap bitmap, String cascadeFilePath) {
        // Load the XML file into a CascadeClassifier object
        CascadeClassifier cascadeClassifier = new CascadeClassifier(cascadeFilePath);

        // Convert the bitmap to a Mat
        Mat image = bitmapToMat(bitmap);

        // Convert the image to grayscale
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_RGBA2GRAY);

        // Detect faces in the image
        MatOfRect faces = new MatOfRect();
        cascadeClassifier.detectMultiScale(grayImage, faces);

        // Convert the MatOfRect to a list of Rect
        List<Rect> faceRects = faces.toList();

        return faceRects;
    }

    public static Bitmap drawFacesWithBoundingBoxes(Bitmap bitmap, List<Rect> faceRectangles) {
        Bitmap resultBitmap = bitmap.copy(bitmap.getConfig(), true);
        Canvas canvas = new Canvas(resultBitmap);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5); // Set the bounding box line width

        for (Rect rect : faceRectangles) {
            canvas.drawRect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, paint);
        }

        return resultBitmap;
    }

    private static Mat bitmapToMat(Bitmap bitmap) {
        Mat mat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, mat);
        return mat;
    }
}