package ars.fyp.audiorecognitionsystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;


import ars.fyp.utils.FileIOUtils;
import ars.fyp.utils.FileNameGenerator;


public class Monitoring extends ActionBarActivity {

    private Thread monitorThread;
    private int noOfEvent;
    private int[] samplesLength;
    private SharedPreferences sharedPreferences;
    private ArrayList<ArrayList<Integer>> patternLists = new ArrayList<ArrayList<Integer>>();
    private final String PREFS_NAME = "ARS_PREFS", NUMBEROFEVENTS = "NUMBER_OF_EVENTS";
    private Runnable monitorLogic = new Runnable(){
        public void run()
        {

            Long targetTime = System.currentTimeMillis()+ 1000 * 10;
            Log.wtf("Success", "0 second started");
            while(targetTime > System.currentTimeMillis())
            {
                Log.wtf("Thread",Thread.currentThread().getName());
            }
            Log.wtf("Success", "10second have been passed");
        }
    };


    private void initialization()
    {

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        noOfEvent = sharedPreferences.getInt(NUMBEROFEVENTS,0);
        for(int i = 0;i < noOfEvent;i++)
        {
            String fileNmae = FileNameGenerator.getDirectory()+FileNameGenerator.getPatternName(i);
            FileIOUtils.readIntFromRandomAccessFile(fileNmae);

        }
        monitorThread = new Thread(monitorLogic);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);
        initialization();
        monitorThread.setName("Thread one ");
        Thread t = new Thread(monitorLogic);
        t.setName("Thread T");
        t.start();
        monitorThread.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_monitoring, menu);
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
