package org.opencv.samples.colorblobdetect;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class Detector {

    private Mat mGrayMat = new Mat();
    int [] detected;
    private Boolean mDetectionIsRunning = true;


    public void process(Mat rgbaImage) {

        if(!mDetectionIsRunning)
        {
            return;
        }

        int signalLength = (int)rgbaImage.size().height;
        Mat grayVect = new Mat(1, signalLength, 4);

        //transform to grayscale
        Imgproc.cvtColor(rgbaImage, mGrayMat, Imgproc.COLOR_RGB2GRAY);
        Mat transposedGrayMat = mGrayMat.t();

        //sum to get 1D signal
        Core.reduce(transposedGrayMat, grayVect, 0, Core.REDUCE_SUM, 4);

        double samplingLevel = 0.1;  //0.1

        Mat s0 = grayVect.submat(0, 1, 0, signalLength*4/13);
        Mat s1 = grayVect.submat(0, 1, signalLength*5/13, signalLength*8/13);
        Mat s2 = grayVect.submat(0, 1, signalLength*8/13, signalLength);

        Mat[] ROIs = {s0, s1, s2};
        int[] ROIOffsets = {0, signalLength*5/13, signalLength*8/13};
        float[] Xs = new float[ROIs.length];
        float[] Ys = new float[ROIs.length];
        Mat A = new Mat(ROIs.length, ROIs.length, 5);
        Mat B = new Mat(ROIs.length, 1, 5);
        Mat C = new Mat(ROIs.length, 1, 5);

        //define linear equations matrices
        for(int i = 0; i < ROIs.length; i++) {
            //get points to determine quadratic equation
            Core.MinMaxLocResult res = Core.minMaxLoc(ROIs[i]);
            Xs[i] = (float)res.maxLoc.x + ROIOffsets[i]; //get index
            Ys[i] = (float)res.maxVal; //get value
            A.put(i, 0, Xs[i] * Xs[i]);
            A.put(i, 1, Xs[i]);
            A.put(i, 2, 1);
            B.put(i, 0, Ys[i]);
        }
        //get quadratic equation
        Core.solve(A, B, C);

        detected = new int[signalLength];
        double C0 = C.get(0,0)[0];
        double C1 = C.get(1,0)[0];
        double C2 = C.get(2,0)[0];

        //detect and store in detected vector
        for(int i = 0; i < signalLength; i++)
        {
            double tmp = ((C0*i*i) + (C1*i) + C2) * samplingLevel;
            if(grayVect.get(0, i)[0] > tmp){
                detected[i] = 1;
            }
            else
            {
                detected[i] = -1;
            }
        }
    }
}