package org.opencv.samples.colorblobdetect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

public class oob extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oob);
        Toast.makeText(getApplicationContext(),"OOB message detected!",Toast.LENGTH_SHORT).show();
        Bundle extras = getIntent().getExtras();
        String oob;
        if(extras != null) {
            oob = extras.getString("OOB");
            TextView text = (TextView) findViewById(R.id.textView);
            text.setMovementMethod(new ScrollingMovementMethod());
            text.setText(oob);
        }
    }
}
