package ars.fyp.audiorecognitionsystem;

import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

import ars.fyp.utils.FileNameGenerator;
import ars.fyp.utils.WavAudioRecorder;


public class Training extends ActionBarActivity {
    private Button startBtn,stopBtn,debugBtn;
    private TextView statusTxt,debugTxt;
    private WavAudioRecorder recorder;
    private FileNameGenerator fNG;
    private String recordFilePath;

    private Button.OnClickListener btnOnclick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.startBtn: {
                    try {
                        if(WavAudioRecorder.State.INITIALIZING == recorder.getState()) {
                            recorder.setOutputFile(recordFilePath);
                            recorder.prepare();
                            recorder.start();

                            statusTxt.setText(recordFilePath + "is recording");

                        }
                        else if(WavAudioRecorder.State.ERROR == recorder.getState())
                        {
                            recorder.release();
                            recorder = WavAudioRecorder.getInstance();
                            recorder.setOutputFile(recordFilePath);
                            statusTxt.setText("Error found, recover with new recording instance");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }

                case R.id.StopBtn: {
                    if (WavAudioRecorder.State.RECORDING == recorder.getState()) {
                        recorder.stop();
                        recorder.reset();
                        statusTxt.setText("Stopped"+"\n"+recordFilePath+" is created");
                    }

                    break;
                }
            }
        }
    };


    private Button.OnClickListener debugClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            String text = "Name: "+fNG.getSampleName();
            text += "\n Directory: "+fNG.getDirectory();
            text += "\n\n\n"+recordFilePath;
            text += "\n"+Environment.getExternalStorageDirectory()+"/"+"wave.wav";
            debugTxt.setText(text);


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        fNG = FileNameGenerator.getInstance();
        recordFilePath = fNG.getDirectory() + "/" + fNG.getSampleName();
        //get Button
        startBtn = (Button) findViewById(R.id.startBtn);
        stopBtn = (Button) findViewById(R.id.StopBtn);
        debugBtn = (Button) findViewById(R.id.debugBtn);
        //get TextView
        statusTxt = (TextView) findViewById(R.id.status);
        debugTxt = (TextView) findViewById(R.id.debugTxt);

        recorder = WavAudioRecorder.getInstance();
        //set onclick listener
        debugBtn.setOnClickListener(debugClick);
        startBtn.setOnClickListener(btnOnclick);
        stopBtn.setOnClickListener(btnOnclick);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_training, menu);
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
