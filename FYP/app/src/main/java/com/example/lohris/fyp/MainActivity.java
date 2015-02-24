package com.example.lohris.fyp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

import utili.FileNameGenerator;
import utili.WavAudioRecorder;


public class MainActivity extends Activity {
    //UI component
    private Button btnStart;
    private Button btnStop;
    private Button btnDebug;
    private TextView status;
    private TextView debugTxt;

    private String fileName;
    private WavAudioRecorder recorder;
    private File recFile, recPath;
    private Button.OnClickListener btnOnclick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnRec: {
                    try {
                        fileName = "sample_";
                        recFile = new File(recPath + "/" + fileName + ".wav");
                        if(WavAudioRecorder.State.INITIALIZING == recorder.getState()) {

                            recorder.setOutputFile(recFile.getAbsolutePath());
                            recorder.prepare();
                            recorder.start();
                            status.setText(recFile.getAbsolutePath());

                        }
                        else if(WavAudioRecorder.State.ERROR == recorder.getState())
                        {
                            recorder.release();
                            recorder = WavAudioRecorder.getInstanse();
                            recorder.setOutputFile(recFile.getAbsolutePath());
                            status.setText("Recover with a new Instance again");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }

                case R.id.btnStop: {
                    if (WavAudioRecorder.State.RECORDING == recorder.getState()) {
                    recorder.stop();
                    recorder.reset();
                    status.setText("Stopped");
                    }

                    break;
                }
            }
        }
    };
    private Button.OnClickListener debugClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            FileNameGenerator fNG = FileNameGenerator.getInstance();
            String text = "Name: "+fNG.getSampleName();
            text += "\n Directory: "+fNG.getDirectory();
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
        recorder = WavAudioRecorder.getInstanse();
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
