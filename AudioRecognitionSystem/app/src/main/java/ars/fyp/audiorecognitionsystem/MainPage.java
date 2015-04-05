package ars.fyp.audiorecognitionsystem;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import ars.fyp.utils.FileIOUtils;
import ars.fyp.utils.FileNameGenerator;
import ars.fyp.utils.LCSHelper;
import ars.fyp.utils.MonitorRecorder;

import static android.app.Notification.DEFAULT_ALL;


public class MainPage extends ActionBarActivity {

    private Button trainBtn, settingBtn, resetBtn, patternGenBtn, monitorBtn;
    private int noOfSample, noOfEvent;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String PREFS_NAME = "ARS_PREFS", EVENTNOPREF = "NUMBER_OF_EVENTS", SAMPLENOPERF = "NUMBER_OF_SAMPLES", MONITORSTATIS = "monitorStatus";
    private int[] samplesLength;
    private ArrayList<ArrayList<Integer>> patternLists;
    private Thread monitorThread;
    private Boolean monitorOn = false;
    private Runnable monitorLogic = new Runnable() {
        public void run() {
            Log.i(this.getClass().toString(),"Monitor Thread start");
            String nameForStH;
            String nameForStMin;
            String nameForEtH;
            String nameForEtMin;
            Calendar tempTime;
            long endTime;
            long timeDiff;
            while (!Thread.currentThread().isInterrupted()) {
                if (noOfEvent != 0) {
                    for (int i = 0; i < noOfEvent; i++) {
                        MonitorRecorder monitorRecorder = MonitorRecorder.getInstance(samplesLength[i],patternLists.get(i));
                        monitorRecorder.prepare();
                        //name for sthr,stmin,ethr,etmin

                        nameForStH = "E" + i + "STH";
                        nameForStMin = "E" + i + "STM";
                        nameForEtH = "E" + i + "ETH";
                        nameForEtMin = "E" + i + "ETM";

                        tempTime = Calendar.getInstance();
                        tempTime.set(Calendar.HOUR_OF_DAY,sharedPreferences.getInt(nameForEtH,0));
                        tempTime.set(Calendar.MINUTE,sharedPreferences.getInt(nameForEtMin,0));
                        endTime = tempTime.getTimeInMillis();
                        //variable to check the time for sleep this thread


                        tempTime.set(Calendar.HOUR_OF_DAY, sharedPreferences.getInt(nameForStH, 0));
                        tempTime.set(Calendar.MINUTE, sharedPreferences.getInt(nameForStMin, 0));

                        timeDiff = tempTime.getTimeInMillis() - System.currentTimeMillis();


                        if (timeDiff > 0) {
                            try {
                                Thread.sleep(timeDiff);
                            } catch (InterruptedException e) {
                                Log.wtf(this.getClass().toString() + " InterruptedException", e.toString());
                            }
                        }

                        //the endtime for the recording
                        while (endTime > System.currentTimeMillis()) {
                            if (MonitorRecorder.State.INITIALIZING == monitorRecorder.getState()) {
                                monitorRecorder.start();
                            } else if (MonitorRecorder.State.ERROR == monitorRecorder.getState()) {
                                monitorRecorder.release();
                                monitorRecorder = MonitorRecorder.getInstance(samplesLength[i],patternLists.get(i));
                            }
                        }
                        if (MonitorRecorder.State.RECORDING == monitorRecorder.getState()) {
                            monitorRecorder.stop();
                            monitorRecorder.reset();
                        }
                        if (!monitorRecorder.getFoundPattern()) {
                            createNotice(i);
                        }
                        Log.wtf("Success", "10 seconds have been passed");
                    }
                }
            }
        }
    };

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
                    noOfEvent = 0;
                    noOfSample = 0;
                    Toast.makeText(getApplicationContext(), "All data have been deleted", Toast.LENGTH_SHORT).show();
                    break;
                }
                case R.id.patternBtn: {
                    generatePattern();
                    break;
                }
                case R.id.monitorBtn: {
                    monitorOn = !monitorOn;
                    editor.putBoolean(MONITORSTATIS, monitorOn);
                    editor.commit();
                    updateMonitorText();
                    if(monitorOn)
                    {
                            monitorThread = new Thread(monitorLogic);
                            monitorThread.start();
                        Log.i(this.getClass().toString(),"the monitor thread have been started");
                    }
                    else
                    {
                            monitorThread.interrupt();
                        Log.i(this.getClass().toString(),"the monitor thread have been terminated");
                    }

                    break;
                }
            }
        }
    };

    private void generatePattern() {
        String directory = FileNameGenerator.getDirectory();
        Log.i(this.getClass().toString(), "The value of noOfEvent : " + noOfEvent);
        Log.i(this.getClass().toString(), "The value of noOfSample : " + noOfSample);
        for (int i = 0; i < noOfEvent; i++) {
            ArrayList<ArrayList<Integer>> datas = new ArrayList<ArrayList<Integer>>();
            for (int j = 0; j < noOfSample; j++) {
                String sampleName = FileNameGenerator.getSamepleName(i, j);
                Log.i(this.getClass().toString(), "Sample name is created : " + sampleName);

                ArrayList<Integer> data = FileIOUtils.readIntFromRandomAccessFile(directory + "/" + sampleName);
                Log.i(this.getClass().toString(), "The size of data : " + data.size());

                ArrayList<Integer> classifiedFreq = LCSHelper.getKeyFeature(data);
                Log.i(this.getClass().toString(), "The size of classifiedFreq : " + classifiedFreq.size());

                datas.add(classifiedFreq);
                Log.i(this.getClass().toString(), "The size of datas : " + datas.size());
            }
            ArrayList<Integer> result = LCSHelper.findCommonLCS(datas);
            String fileName = FileNameGenerator.getDirectory() + "/" + FileNameGenerator.getPatternName(i);
            FileIOUtils.writeDataToFile(new File(fileName), result);
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
        monitorOn = sharedPreferences.getBoolean(MONITORSTATIS, false);
        editor = sharedPreferences.edit();
        noOfSample = sharedPreferences.getInt(SAMPLENOPERF, 5);
        noOfEvent = sharedPreferences.getInt(EVENTNOPREF, 0);



        initialPattern();
        intialSampleLengths();
        monitorThread = new Thread(monitorLogic);
        updateMonitorText();
        if(monitorOn)
            monitorThread.start();

    }

    private void updateMonitorText() {
        if (monitorOn) {
            monitorBtn.setText("Turn Off\nMonitor");
        } else {
            monitorBtn.setText("Turn On\nMonitor");
        }
    }

    private void initialPattern()
    {
        patternLists = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < noOfEvent; i++) {
            String fileNmae = FileNameGenerator.getDirectory() + FileNameGenerator.getPatternName(i);
            ArrayList<Integer> pattern = FileIOUtils.readIntFromRandomAccessFile(fileNmae);
            patternLists.add(pattern);
        }

    }

    private void intialSampleLengths()
    {
        samplesLength = new int[noOfEvent];
        int dataSize = 0;
        for(int i = 0; i < noOfEvent; i++)
        {
            for(int j = 0; j < noOfSample; j++)
            {
                String fileNmae = FileNameGenerator.getDirectory() + FileNameGenerator.getSamepleName(i,j);
                ArrayList<Integer> data = FileIOUtils.readIntFromRandomAccessFile(fileNmae);
                if(data.size() > dataSize)
                {
                    dataSize = data.size();
                }
            }
            samplesLength[i] = dataSize;
            dataSize = 0;
        }
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

    private void createNotice(int eventId) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("U forget to do the event")
                        .setContentText("u DIDN'T do the event")
                        .setDefaults(DEFAULT_ALL)
                        .setPriority(2);
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), MainPage.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(MainPage.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(eventId, mBuilder.build());
    }
}
