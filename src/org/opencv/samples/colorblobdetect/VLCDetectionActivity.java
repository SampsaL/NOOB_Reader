package org.opencv.samples.colorblobdetect;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class VLCDetectionActivity extends Activity implements OnTouchListener, CvCameraViewListener2 {
    private static final String  TAG              = "OCVSample::Activity";
    private boolean              mIsColorSelected = false;
    private Mat                  mRgba;
    private VLCDetector          mDetector;
    private CameraBridgeViewBase mOpenCvCameraView;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                Log.i(TAG, "OpenCV loaded successfully");
                mOpenCvCameraView.enableView();
                mOpenCvCameraView.setOnTouchListener(VLCDetectionActivity.this);
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    public VLCDetectionActivity() {}
    int count = 0;
    int countS = 128;
    int toggle = 0;
    int toggle2 = 0;
    int toggle3 = 0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.detection_surface_view);
        //Handler handler = new Handler();
        mOpenCvCameraView = findViewById(R.id.detection_activity_surface_view);
        //mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        //mOpenCvCameraView.enableFpsMeter();
        mOpenCvCameraView.setMaxFrameSize(800,600); //800x600 for nokia 5.1
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.e("INIT", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);//(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.e("INIT", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mDetector = new VLCDetector();
        Toast.makeText(getApplicationContext(), "Tap screen to detect message", Toast.LENGTH_LONG).show();
        //mIsColorSelected = true;
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (!mIsColorSelected){
            Toast.makeText(getApplicationContext(), "Detection running", Toast.LENGTH_SHORT).show();
            mIsColorSelected = true;
        }
        else {
            Toast.makeText(getApplicationContext(), "Detection paused", Toast.LENGTH_SHORT).show();
            mIsColorSelected = false;
        }
        return false;
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        TextView txt = findViewById(R.id.textView2);
        ProgressBar progress = findViewById(R.id.progressBar);
        progress.setScaleY(8f);
        progress.setVisibility(View.VISIBLE);
        StringBuilder sb = new StringBuilder();
        StringBuilder bb = new StringBuilder();
        StringBuilder data = new StringBuilder();
        StringBuilder xrt = new StringBuilder();
        StringBuilder tooManyBuilders = new StringBuilder();

        if (count == countS - 1 && toggle3 == 1) {
            mIsColorSelected = false;
            try {
                startOOB(null);
                progress.setVisibility(View.INVISIBLE);
                progress.setProgress(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mIsColorSelected) {
            mDetector.process(mRgba);
            int max = mDetector.detected.length;
            /*byte[] BPix = new byte[4];
            BPix[0] = (byte) 55;
            BPix[1] = (byte) 55;
            BPix[2] = (byte) 55;
            BPix[3] = (byte) 0;
            byte[] WPix = new byte[4];
            WPix[0] = (byte) 255;
            WPix[1] = (byte) 255;
            WPix[2] = (byte) 255;
            WPix[3] = (byte) 0;*/
            //double q = 5; // -45 distance between the drawn detection
            //double offset = 400; //mid: 960/2 position on display
            //int drawn = 1; // toggle for writing bits..
            // draw detections
            //Log.e(" Testing int max : ", Integer.toString(max));
            for(int x = 0; x < max; x++) {
                double detections = mDetector.detected[x];  // 1 or -1
                //double corval = (corvals * q) + offset;
                if (detections > 0) {
                    //for (int a = 0; a < 10; a++) {
                    //    mRgba.put(x, (int)detections+a, WPix);
                    //}
                    sb.append("1");
                }
                else {
                    sb.append("0");
                    //setText(txt, "0");
                    //for (int a = 0; a < 10; a++) {
                    //    mRgba.put(x, (int)detections+a, BPix);
                    //}
                }

                if (sb.length() == max) {
                    for (int j = 0; j < sb.length(); j++){
                        char str = sb.charAt(j);
                        if ((j - 1) > 0) {
                            char str1 = sb.charAt(j-1);
                            if (str1 == '1' && str == '0'){
                                bb.append(" ");
                            }
                            if (str1 == '0' && str == '1'){
                                bb.append(" ");
                            }
                        }
                        if (str == '0'){
                            bb.append("0");
                        }
                        if (str == '1'){
                            bb.append("1");
                        }
                    }
                    String[] dat = bb.toString().split(" ");
                    //Imgproc.putText(mRgba, Arrays.toString(dat),new Point(10,200),1,1,new Scalar (255,255,255), 2);
                    //Log.e(" Testing data before analyzing 111 and 11111 etc... : ", Arrays.toString(dat));
                    for (int j = 0; j < dat.length; j++){
                        String temp = dat[j];
                        if (temp.contains("1111")) {
                            data.append("1");
                            if (temp.contains("1111111111")) {
                                data.append("1");
                                if (temp.contains("111111111111111")) {
                                    data.append("1");
                                    if (temp.contains("11111111111111111111")) {
                                        data.append("1");
                                    }
                                }
                            }
                        }
                        if (temp.contains("000")) {
                            data.append("0");
                            if (temp.contains("000000000")) {
                                data.append("0");
                                if (temp.contains("000000000000000")) {
                                    data.append("0");
                                    if (temp.contains("0000000000000000000")) {
                                        data.append("0");
                                    }
                                }
                            }
                        }
                    }
                    //Log.e(" Testing data after analyzing 111 and 000 : ", data.toString());
                    String[] testing = data.toString().split("011110");
                    //Imgproc.putText(mRgba, Arrays.toString(testing),new Point(10,100),1,1,new Scalar (255,255,255), 2);
                    //Log.e(" Testing data after splitting preamble: ", Arrays.toString(testing));

                    for (int u = 1; u < testing.length - 1; u++) { // u = 1, len-1 discard first and last because errors..
                        String[] new_f = testing[u].replaceAll("..(?!$)", "$0 ").split(" ");
                            //String result = Arrays.stream(new_f).collect(Collectors.joining(" "));
                        //Log.e(" Testing before manchester decoding : ", Arrays.toString(new_f));
                            // decoding Manchester code
                        for (int j = 0; j < new_f.length; j++) {
                            String temp = new_f[j];
                            if (temp.equals("10")) {
                                xrt.append("1");
                                continue;
                            }
                            if (temp.equals("01")) {
                                xrt.append("0");
                                continue;
                            }
                            if (temp.equals("00")) {
                                xrt.append("-");
                                break;
                            }
                            if (temp.equals("11")) {
                                xrt.append("-");
                                break;
                            }
                        }
                    }

                    //Log.e(" Testing", String.format(" data after manchester: " + xrt.toString()));
                    if (xrt.length() == 24 && !xrt.toString().contains("-") ) {//&& !xrt.toString().contains("-")) {
                        int arrayLength = (int) Math.ceil(((xrt.length() / (double)8)));
                        String[] resultS = new String[arrayLength];
                        int j = 0;
                        int lastIndex = resultS.length - 1;
                        for (int i = 0; i < lastIndex; i++) {
                            resultS[i] = xrt.substring(j, j + 8);
                            int charC = Integer.parseInt(resultS[i], 2);
                            tooManyBuilders.append((char) charC);
                            j += 8;
                        }
                        resultS[lastIndex] = xrt.substring(j);
                        int charC = Integer.parseInt(resultS[lastIndex], 2);
                        tooManyBuilders.append((char) charC);
                        String temp = tooManyBuilders.toString();
                        Log.e("TEST", "! " + temp + " !");
                        //Imgproc.putText(mRgba, String.format("Message: "+ tooManyBuilders.toString()), new Point(50, 100), 2, 2, new Scalar(200, 200, 200), 2);
                        if (temp.length() == 3) {
                            addText(txt, temp);
                            //Log.e(" Testing", String.format("payload: " + tooManyBuilders.toString()));
                            if (temp.contains("+")) {
                                toggle2 = 1;

                            }
                        }
                    }
                }
            }
        }
        return mRgba;
    }

    private void addText(final TextView text, final String value){
        runOnUiThread(new Runnable() {
            @Override // TODO: fix (contains) because of possible future errors.. WIP for now..
            // TODO:  'toggle' now waits for signal start (slows msg received down a bit)
            public void run() {
                String txt = text.getText().toString();
                ProgressBar progress = findViewById(R.id.progressBar);
                if (value.substring(0, 1).contains("+")) {
                    if (value.substring(1, 2).contains("+")) {
                        try {
                            countS = Integer.parseInt(value.substring(2,3));
                            progress.setMax(countS-1);
                            toggle += 1;
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                    }
                    else {
                        try {
                            countS = Integer.parseInt(value.substring(1, 3));
                            toggle += 1;
                            progress.setMax(countS-1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (toggle > 1) {
                        if (text.getText().length() != countS*3) {
                            Log.e("Error: ", String.format(text.getText().toString()+ " failed here."));
                            text.setText("");
                            toggle = 1;
                            count = 0;
                        }
                        else
                            toggle3 = 1;
                    }
                }
                if (!value.contains("+") && toggle >= 1){ //i.e., else
                        if (txt.length() < 1) {
                            text.append(value);
                        }
                        if (txt.length() >= 3){
                            if (!txt.substring(txt.length() - 3).contains(value)){
                                text.append(value);
                                count++;
                                progress.setProgress(count);
                                //Log.e(" Testing if last 3 chars: ", String.format(txt.substring(txt.length()-3) + " equals with " + value+ " or with first three: " + txt.substring(0, 3) + " "));
                            }
                        }
                }
            }
        });
    }

    public void startOOB(View view) {
        Intent intent = new Intent(this, oob.class);
        TextView txt = findViewById(R.id.textView2);
        String message = txt.getText().toString();
        intent.putExtra("OOB", message);
        startActivity(intent);
    }
}