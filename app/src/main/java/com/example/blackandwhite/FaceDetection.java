package com.example.blackandwhite;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.opencv.core.*;
import org.opencv.android.Utils;
import org.opencv.imgproc.Imgproc;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FaceDetection {
    private Net mtcnn;

    public FaceDetection(String modelPath) {
        mtcnn = Dnn.readNetFromTorch(modelPath);
    }

    public Rect[] detectFaces(Bitmap image) {
        // Convert bitmap to Mat
        Mat matImage = new Mat();
        Utils.bitmapToMat(image, matImage);

        // Preprocess image
        Imgproc.cvtColor(matImage, matImage, Imgproc.COLOR_RGBA2RGB);
        Imgproc.resize(matImage, matImage, new Size(300, 300));

        // Convert Mat to blob
        Mat blob = Dnn.blobFromImage(matImage, 1.0, new Size(300, 300), new Scalar(104.0, 177.0, 123.0), false, false);

        // Set the blob as input to the network
        mtcnn.setInput(blob);

        // Forward pass through the network
        List<Mat> outputBlobs = new ArrayList<>();
        mtcnn.forward(outputBlobs, Arrays.asList("boxes", "scores", "landmarks"));

        // Retrieve bounding box coordinates
        Mat boxes = outputBlobs.get(0);
        Mat scores = outputBlobs.get(1);
        Mat landmarks = outputBlobs.get(2);

        // Convert bounding box coordinates to Rect objects
        float[] boxesData = new float[(int) (boxes.total() * boxes.channels())];
        boxes.get(0, 0, boxesData);

        Rect[] faces = new Rect[boxes.rows()];
        for (int i = 0; i < boxes.rows(); i++) {
            int x1 = (int) (boxesData[i * 4 + 1] * image.getWidth());
            int y1 = (int) (boxesData[i * 4 + 0] * image.getHeight());
            int x2 = (int) (boxesData[i * 4 + 3] * image.getWidth());
            int y2 = (int) (boxesData[i * 4 + 2] * image.getHeight());
            faces[i] = new Rect(x1, y1, x2 - x1, y2 - y1);
        }

        return faces;
    }

    public Bitmap drawBox(List<Rect> rects, Bitmap image) {
        // Create a mutable copy of the input bitmap
        Bitmap mutableImage = image.copy(Bitmap.Config.ARGB_8888, true);

        // Create a canvas from the mutable image
        Canvas canvas = new Canvas(mutableImage);

        // Set up the paint for drawing the bounding boxes
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);

        // Draw the bounding boxes on the canvas
        for (Rect rect : rects) {
            canvas.drawRect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, paint);
        }

        // Return the modified bitmap with the bounding boxes
        return mutableImage;
    }
}
