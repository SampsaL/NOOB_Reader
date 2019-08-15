package org.opencv.samples.colorblobdetect;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Mat;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;



public class DetectionActivity extends Activity implements OnTouchListener, CvCameraViewListener2 {
    private static final String  TAG              = "OCVSample::Activity";
    private boolean              mIsColorSelected = false;
    private Mat                  mRgba;
    private Detector mDetector;
    private CameraBridgeViewBase mOpenCvCameraView;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                Log.i(TAG, "OpenCV loaded successfully");
                mOpenCvCameraView.enableView();
                mOpenCvCameraView.setOnTouchListener(DetectionActivity.this);
            } else {
                super.onManagerConnected(status);
            }
        }
    };
    public DetectionActivity() {}
    int count = 0;
    int countS = 128;
    int toggle = 0;
    int toggle2 = 0;
    int toggle3 = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.detection_surface_view);
        //Handler handler = new Handler();
        mOpenCvCameraView = findViewById(R.id.detection_activity_surface_view);
        //mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.enableFpsMeter();
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
        mDetector = new Detector();
        Toast.makeText(getApplicationContext(), "Detection running...", Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), "Tap screen to detect message", Toast.LENGTH_LONG).show();
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
        //Log.e("msg", "------------------- Frame captured -------------------");
        mRgba = inputFrame.rgba();
        TextView txt = findViewById(R.id.textView2);
        //ProgressBar progress = findViewById(R.id.progressBar);
        ProgressBar progress2 = findViewById(R.id.progressBar2);
        //progress.setScaleY(5f);
        //progress.setVisibility(View.VISIBLE);
        StringBuilder sb = new StringBuilder();
        StringBuilder bb = new StringBuilder();
        StringBuilder data = new StringBuilder();
        StringBuilder xrt = new StringBuilder();
        StringBuilder tooManyBuilders = new StringBuilder();

        if (count == countS - 1 && toggle3 == 1) {
            mIsColorSelected = false;
            try {
                startOOB(null);
                //progress.setVisibility(View.INVISIBLE);
                //progress.setProgress(0);
                progress2.setProgress(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mIsColorSelected) {
            Log.e("msg", "------------------- Frame captured -------------------");
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
            //int drawn = 1; // toggle for writing bits..
            // draw detections
            //Log.e(" Testing int max : ", Integer.toString(max));
            for(int x = 0; x < max; x++) {

                int detections = mDetector.detected[x];  // 1 or -1
                if (detections == 1) {
                    /*for (int a = 0; a < 6; a++) {
                        mRgba.put(x, 400+a, WPix);
                    }*/
                    sb.append("1");
                }
                else {
                    sb.append("0");
                    //setText(txt, "0");
                    /*for (int a = 0; a < 10; a++) {
                        mRgba.put(x, 400+a, BPix);
                    }*/
                }

                if (sb.length() == max) {
                    for (int j = 0; j < sb.length(); j++){
                        char str = sb.charAt(j);
                        if ((j - 1) > 0) {
                            char str1 = sb.charAt(j - 1);
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
                    //Log.e("STR", String.format("Binary: " + bb.toString()));
                    //Imgproc.putText(mRgba, Arrays.toString(dat),new Point(10,200),1,1,new Scalar (255,255,255), 2);
                    //Log.e(" Testing data before: ", Arrays.toString(dat));
                    for (String temp : dat) {
                        //Log.e("TEST", String.format(temp +" : " + temp.length() +" "));
                        if (temp.contains("1111")) { //111
                            data.append("1");
                            if (temp.length() > 10) { //1111111111
                                data.append("1");
                                if (temp.length() > 17) { //111111111111111
                                    data.append("1");
                                    if (temp.length() >= 18) { //11111111111111111111
                                        data.append("1");
                                    }
                                }
                            }
                        }
                        if (temp.contains("00")) { //000
                            data.append("0");
                            if (temp.length() >= 9) { //000000000
                                data.append("0");
                                if (temp.length() > 17) { //000000000000000
                                    data.append("0");
                                    if (temp.length() > 19) { //0000000000000000000
                                        data.append("0");
                                    }
                                }
                            }
                        }
                    }
                    //Log.e(" TEST ", String.format("data: "+data.toString()+" len: "+data.length()));
                    String[] testing = data.toString().split("011110");//("011110");
                    //Imgproc.putText(mRgba, Arrays.toString(testing),new Point(10,100),1,1,new Scalar (255,255,255), 2);
                    //Log.e(" Testing data after splitting preamble: ", Arrays.toString(testing));

                    for (String s : testing) {
                        if (s.length() == 48) {
                            Log.e("M", "48 works..");
                            String[] new_f = s.replaceAll("..(?!$)", "$0 ").split(" ");
                            //String result = Arrays.stream(new_f).collect(Collectors.joining(" "));
                            //Log.e("Message", "msg: " + testing[u]);
                            // decoding Manchester code
                            for (String temp : new_f) {
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
                            Log.e("Message", String.format("msg for 48 len: " + xrt));
                            break;
                        }
                        try { // toimii afaik
                            if (testing[1].length() == 47 && testing[0].length() != 48) {
                                StringBuilder str123 = new StringBuilder();
                                str123.append(testing[1]);
                                //Log.e("test", "attempting the fix");
                                if (str123.substring(str123.length() - 2).equals("11") || str123.substring(str123.length() - 2).equals("01")) {
                                    str123.append("0");
                                } else {
                                    str123.append("1");
                                }
                                //Log.e("Message", String.format("lyhyt mjono " + testing[1] + "ja fiksattu versio: " + str123.toString()));
                                String[] new_f = str123.toString().replaceAll("..(?!$)", "$0 ").split(" ");
                                //String result = Arrays.stream(new_f).collect(Collectors.joining(" "));
                                //Log.e("Message", "msg: " + str123);
                                // decoding Manchester code
                                for (String temp : new_f) {
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
                                //Log.e("Message", String.format("attempted fix: " + xrt));// + " from: "+ testing[1]));
                                if (xrt.length() != 24) { Log.e("Message 123", testing[1].toString());}
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try { // works afaik
                            if (testing[1].length() > 48) {
                                Log.e("M", "No t'was me too!");
                                String new_f1 = testing[1].substring(0, 48);
                                String[] new_f = new_f1.replaceAll("..(?!$)", "$0 ").split(" ");
                                //Log.e("Message", String.format("lopussa liikaa: " + new_f1 + "ja koko mjono: " + testing[1]));
                                for (String temp : new_f) {
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
                                //Log.e("Message", String.format("attempted fix for [1] > 48: " + xrt));
                                if (xrt.length() != 24) { Log.e("Message 123", testing[1].toString());}
                                break;
                                //if (xrt.length() == 24) { break; }
                            }
                            if (testing[0].length() == 47 && testing[1].length() != 48) {
                                StringBuilder str123 = new StringBuilder();
                                if (testing[0].startsWith("10") || testing[0].startsWith("11") ) {
                                    str123.append("0").append(testing[0]);
                                } else
                                    str123.append("1").append(testing[0]);
                                //Log.e("Message", String.format("lyhyt mjono " + testing[0] + "ja fiksaus: " + str123.toString()));
                                String[] new_f = str123.toString().replaceAll("..(?!$)", "$0 ").split(" ");
                                for (String temp : new_f) {
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
                                //Log.e("Message", String.format("attempted fix for [0] 47: " + xrt));
                                if (xrt.length() != 48) { Log.e("Message ", testing[0].toString());}
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try { // this works afaik
                            if (testing[0].length() > 48) {
                                String new_f1 = testing[0].substring(testing[0].length() - 48);
                                String[] new_f = new_f1.replaceAll("..(?!$)", "$0 ").split(" ");
                                //Log.e("Message", "alussa liikaa: " + new_f1 + " tai koko mjono: " + testing[0]);
                                for (String temp : new_f) {
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
                                //Log.e("STR", String.format("A:Fixed: "+ xrt.toString()));
                                //Log.e("Message", String.format("attempted fix for [0] > 48: " + xrt));
                                if (xrt.length() != 24) { Log.e("Message ", testing[0].toString());}
                                break;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
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
                        Log.e("Message", "payload: " + temp + " !");
                        //Imgproc.putText(mRgba, String.format("Message: "+ tooManyBuilders.toString()), new Point(50, 100), 2, 2, new Scalar(200, 200, 200), 2);
                        if (temp.length() == 3) {
                            addText(txt, temp);
                            //Log.e("Message", String.format("payload appended: " + tooManyBuilders.toString()));
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
                //ProgressBar progress = findViewById(R.id.progressBar);
                ProgressBar progress2 = findViewById(R.id.progressBar2);
                ObjectAnimator animation = ObjectAnimator.ofInt(progress2, "progress",count*1095);
                if (value.substring(0, 1).contains("+")) {
                    if (value.substring(1, 2).contains("+")) {
                        try {
                            countS = Integer.parseInt(value.substring(2,3));
                            //progress.setMax(countS - 1);
                            progress2.setMax(countS * 1000);
                            toggle += 1;
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                    }
                    else {
                        try {
                            countS = Integer.parseInt(value.substring(1, 3));
                            toggle += 1;
                            //progress.setMax(countS - 1);
                            progress2.setMax(countS * 1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (toggle > 1) {
                        if (text.getText().length() != countS * 3) {
                            Log.e("Error: ", String.format(text.getText().toString()+ " failed here."));
                            toggle = 1;
                            count = 0;
                            text.setText("");
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
                                //progress.setProgress(count);
                                //progress2.setProgress(count);
                                animation.setDuration(300);
                                animation.setInterpolator(new DecelerateInterpolator());
                                animation.start();
                                //Log.e(" TEST2: ", String.format("Appending: "+ value));
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
        Log.e("Message", String.format("Starting OOB with: " + message));
        count = 0;
        countS = 0;
        startActivity(intent);
    }
}