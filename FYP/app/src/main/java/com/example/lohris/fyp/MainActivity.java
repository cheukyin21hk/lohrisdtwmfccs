package com.example.lohris.fyp;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;


public class MainActivity extends Activity {
    //UI component
    private Button btnStart;
    private Button btnStop;
    private Button btnDebug;
    private TextView status;
    private TextView debugTxt;

    private String fileName;
    private MediaRecorder recorder;
    private File recFile, recPath;
    private Button.OnClickListener btnOnclick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnRec: {
                    status.setText(recFile.getAbsolutePath());
                    try {
                        fileName = "test";
                        recFile = new File(recPath + "/" + fileName + ".amr");
                        recorder = new MediaRecorder();

                        //setting the source of recorder
                        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);

                        //setting the format and the encoder
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                        recorder.setOutputFile(recFile.getAbsolutePath());

                        //prepare to start
                        recorder.prepare();
                        recorder.start();
                        status.setText(recFile.getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }

                case R.id.btnStop: {
                    if (recFile != null) {
                        recorder.stop();
                        recorder.release();
                        recorder = null;
                    }
                    status.setText("Stopped");
                    break;
                }
            }
        }
    };
    private Button.OnClickListener debugClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            String text = "Status : " + Environment.getExternalStorageState() + "\n" +
                    "Path :" + Environment.getExternalStorageDirectory() + "\n" +
                    "Equal ava? :" + Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) + "\n" +
                    "Read Only :" + Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())+
                    "Cache Path :";
            debugTxt.setText(text);


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UI config
        btnStart = (Button) findViewById(R.id.btnRec);
        btnStop = (Button) findViewById(R.id.btnStop);
        status = (TextView) findViewById(R.id.status);
        debugTxt = (TextView) findViewById(R.id.debugTxt);
        btnDebug = (Button) findViewById(R.id.debug);
        //Onclick listener
        btnStart.setOnClickListener(btnOnclick);
        btnStop.setOnClickListener(btnOnclick);
        btnDebug.setOnClickListener(debugClick);
        //basic setup for variables
        recPath = Environment.getExternalStorageDirectory();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
