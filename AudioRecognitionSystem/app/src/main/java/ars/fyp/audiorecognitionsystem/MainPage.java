package ars.fyp.audiorecognitionsystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import ars.fyp.utils.FileNameGenerator;
import ars.fyp.utils.FileIOUtils;
import ars.fyp.utils.LCSHelper;


public class MainPage extends ActionBarActivity {

    private Button trainBtn, settingBtn, resetBtn,patternGenBtn,monitorBtn;
    private int noOfSample,noOfEvent;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String PREFS_NAME = "ARS_PREFS",EVENTNOPREF="NUMBER_OF_EVENTS",SAMPLENOPERF="NUMBER_OF_SAMPLES";

    private Button.OnClickListener btnListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.trainingBtn: {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), Training.class);
                    startActivity(intent);
                    break;
                }
                case R.id.settingBtn: {
                    break;
                }
                case R.id.resetBtn: {
                    editor.clear();
                    editor.commit();
                    Toast.makeText(getApplicationContext(),"All data have been deleted",Toast.LENGTH_SHORT).show();
                    break;
                }
                case R.id.patternBtn:
                {
                    generatePattern();
                    break;
                }
                case R.id.monitorBtn:
                {
                    Intent intent = new Intent(getApplicationContext(),Monitoring.class);
                    startActivity(intent);
                    break;
                }
            }
        }
    };

    private void generatePattern()
    {
        String directory = FileNameGenerator.getDirectory();

        for(int i = 0; i < noOfEvent; i++)
        {
            ArrayList<ArrayList<Integer>> datas = new ArrayList<ArrayList<Integer>>();
            for(int j = 0 ; j < noOfSample;j++)
            {
               String sampleName = FileNameGenerator.getSamepleName(i,j);
               ArrayList<Integer> data = FileIOUtils.readIntFromRandomAccessFile(directory + sampleName);
               ArrayList<Integer> classifiedFreq = LCSHelper.skipLowAmplitude(data);
               datas.add(classifiedFreq);
            }
            ArrayList<Integer> result = LCSHelper.findCommonLCS(datas);
            String fileName = FileNameGenerator.getDirectory()+FileNameGenerator.getPatternName(i);
            FileIOUtils.writeDataToFile(new File(fileName),result);
        }
    }

    private void initialization() {
        trainBtn = (Button) findViewById(R.id.trainingBtn);
        settingBtn = (Button) findViewById(R.id.settingBtn);
        resetBtn = (Button) findViewById(R.id.resetBtn);
        patternGenBtn = (Button) findViewById(R.id.patternBtn);
        monitorBtn = (Button) findViewById(R.id.monitorBtn);


        patternGenBtn.setOnClickListener(btnListener);
        trainBtn.setOnClickListener(btnListener);
        settingBtn.setOnClickListener(btnListener);
        resetBtn.setOnClickListener(btnListener);
        monitorBtn.setOnClickListener(btnListener);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        noOfSample= sharedPreferences.getInt(SAMPLENOPERF,5);
        noOfEvent = sharedPreferences.getInt(EVENTNOPREF,0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        initialization();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_page, menu);
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
