package ars.fyp.audiorecognitionsystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ars.fyp.utils.AudioPatterRecorder;
import ars.fyp.utils.FileNameGenerator;
import ars.fyp.utils.WavAudioRecorder;

public class Training extends ActionBarActivity {
    private Button homeBtn,addBtn;
    private ListView eventList;
    private List<String> eventListItems;
    private String recordFilePath;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private final String PREFS_NAME = "ARS_PREFS", NUMBEROFEVENTS = "NUMBER_OF_EVENTS";

    private Button.OnClickListener btnOnClick = new Button.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch(v.getId())
            {
                case R.id.addBtn:
                {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(),EventRecorder.class);
                    startActivity(intent);
                    break;
                }
                case R.id.tHomeBtn:
                {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(),MainPage.class);
                    startActivity(intent);
                    break;
                }
            }
        }

    };


    private void initialization()
    {
        homeBtn = (Button)findViewById(R.id.tHomeBtn);
        addBtn = (Button)findViewById(R.id.addBtn);
        eventList = (ListView)findViewById(R.id.tEventList);

        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        addBtn.setOnClickListener(btnOnClick);
        homeBtn.setOnClickListener(btnOnClick);
        initialListView();
    }

    private void initialListView()
    {
        eventListItems = new ArrayList<String>();
        int numberOfLoop = sharedpreferences.getInt(NUMBEROFEVENTS,0);
        for(int i = 0 ; i < numberOfLoop;i++)
        {
            String eventNamePRES = "E"+i+"Name";
            eventListItems.add("Event : "+sharedpreferences.getString(eventNamePRES,""));
        }

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, eventListItems);
        eventList.setAdapter(itemsAdapter);

    }




    /*
        private Button.OnClickListener btnOnclick  new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.startBtn: {
                        if (WavAudioRecorder.State.RECORDING != waveRecorder.getState()) {
                            try {
                                if (AudioPatterRecorder.State.INITIALIZING == recorder.getState()) {
                                    recordFilePath = fNG.getDirectory() + "/" + fNG.getTextSampleName();
                                    recorder.setOutputFile(recordFilePath);
                                    recorder.prepare();
                                    recorder.start();
                                    statusTxt.setText(recordFilePath + " is recording");
                                } else if (AudioPatterRecorder.State.ERROR == recorder.getState()) {
                                    recorder.release();
                                    recorder = AudioPatterRecorder.getInstance();
                                    recorder.setOutputFile(recordFilePath);
                                    statusTxt.setText("Error found, recover with new recording instance");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }

                    case R.id.StopBtn: {
                        if (AudioPatterRecorder.State.RECORDING == recorder.getState()) {
                            recorder.stop();
                            recorder.reset();
                            statusTxt.setText("Stopped" + "\n" + recordFilePath + " is created");
                        } else if (WavAudioRecorder.State.RECORDING == waveRecorder.getState()) {
                            waveRecorder.stop();
                            waveRecorder.reset();
                            statusTxt.setText("Stopped" + "\n" + recordFilePath + " is created");
                        }

                        break;
                    }

                    case R.id.recordWaveBtn: {
                        try {
                            if (AudioPatterRecorder.State.RECORDING != recorder.getState()) {
                                if (WavAudioRecorder.State.INITIALIZING == waveRecorder.getState()) {
                                    recordFilePath = fNG.getDirectory() + "/" + fNG.getWaveSampleName();
                                    waveRecorder.setOutputFile(recordFilePath);
                                    waveRecorder.prepare();
                                    waveRecorder.start();
                                    statusTxt.setText(recordFilePath + " is recording");
                                } else if (WavAudioRecorder.State.ERROR == waveRecorder.getState()) {
                                    waveRecorder.release();
                                    waveRecorder = WavAudioRecorder.getInstance();
                                    waveRecorder.setOutputFile(recordFilePath);
                                    statusTxt.setText("Error found, recover with new recording instance");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        };


        private Button.OnClickListener debugClick = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "Name: " + fNG.getTextSampleName();
                text += "\n Directory: " + fNG.getDirectory();
                text += "\n\n\n" + recordFilePath;
                text += "\n" + Environment.getExternalStorageDirectory() + "/" + "wave.wav";
                debugTxt.setText(text);
            }
        };
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        initialization();
        //fNG = FileNameGenerator.getInstance();
        //recordFilePath = fNG.getDirectory() + "/" + fNG.getTextSampleName();
        //get Button
        //    startBtn = (Button) findViewById(R.id.startBtn);
        //  stopBtn = (Button) findViewById(R.id.StopBtn);
        // wavRecordingBtn = (Button) findViewById(R.id.recordWaveBtn);
        //get TextView
        // statusTxt = (TextView) findViewById(R.id.status);
        // recorder = AudioPatterRecorder.getInstance();
        // waveRecorder = WavAudioRecorder.getInstance();
        //set onclick listener
        //startBtn.setOnClickListener(btnOnclick);
        //stopBtn.setOnClickListener(btnOnclick);
        //wavRecordingBtn.setOnClickListener(btnOnclick);
        //Log.wtf("Thread id for main activities class : ", Thread.currentThread().getId() + "");
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
