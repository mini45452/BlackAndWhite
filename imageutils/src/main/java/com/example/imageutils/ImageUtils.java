package com.example.imageutils;

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

    public List<Rect> detectFaces(Bitmap bitmap) {
        // Load the pre-trained face detection model
        String cascadePath = "train_model/haarcascade_frontalface_default.xml";
        CascadeClassifier cascadeClassifier = new CascadeClassifier(cascadePath);

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

    public Mat bitmapToMat(Bitmap bitmap) {
        Mat mat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, mat);
        return mat;
    }
}