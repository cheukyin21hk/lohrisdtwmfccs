package ars.fyp.audiorecognitionsystem;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import ars.fyp.utils.AudioPatterRecorder;
import ars.fyp.utils.FileNameGenerator;


public class EventRecorder extends ActionBarActivity {

    private GridLayout topGlayout,middleGlayout;
    private LinearLayout bgLLayout;
    private final String PREFS_NAME = "ARS_PREFS", NUMBEROFEVENTS = "NUMBER_OF_EVENTS";
    private Button endTimeBtn, startTimeBtn, homeBtn, startRcBtn, endRcBtn,eCancelBtn,eSaveBtn;
    private TextView statusDisplay;
    private EditText eventName;
    private ListView sampleList;
    private int hour, minute, eventNumber, sampleNumber;
    private AudioPatterRecorder recorder;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private String recordFilePath;

    private View.OnClickListener hideKeyboardListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(eventName.getWindowToken(), 0);
        }
    };

    private Button.OnClickListener btnListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.startTimeBtn:
                case R.id.endTimeBtn: {
                    showDialog(v.getId());
                    break;
                }
                case R.id.eHomeBtn: {
                    String nameForSMin = "E" + eventNumber + "STM";
                    String nameForSHour = "E" + eventNumber + "STH";
                    String nameForEMin = "E" + eventNumber + "ETM";
                    String nameForEHour = "E" + eventNumber + "ETH";
                    if(sharedpreferences.contains(nameForSMin))
                        editor.remove(nameForSMin);
                    if(sharedpreferences.contains(nameForSHour))
                        editor.remove(nameForSHour);
                    if(sharedpreferences.contains(nameForEMin))
                        editor.remove(nameForEMin);
                    if(sharedpreferences.contains(nameForEHour))
                        editor.remove(nameForEHour);

                    for(int i = 0; i < 5 ; i++)
                    {
                        if(sharedpreferences.contains(FileNameGenerator.getSamepleName(eventNumber,i)))
                        {
                            editor.remove(FileNameGenerator.getSamepleName(eventNumber,i));
                        }
                    }
                    editor.commit();
                    String editTextVal = eventName.getText().toString();
                    eventName.setText("");
                    sampleNumber =0;
                    updateListView();
                    Toast.makeText(getApplicationContext(),editTextVal +" have been cancelled.",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainPage.class);
                    startActivity(intent);
                    break;
                }
                case R.id.endRcBtn: {
                    if (AudioPatterRecorder.State.RECORDING == recorder.getState()) {
                        recorder.stop();
                        recorder.reset();
                        statusDisplay.setText("Stopped" + "\n" + recordFilePath + " is created");
                        editor.putBoolean(FileNameGenerator.getSamepleName(eventNumber, sampleNumber), true);
                        editor.commit();
                        sampleNumber++;
                        updateListView();
                    }
                    break;
                }
                case R.id.startRcBtn: {
                    if (sampleNumber < 5) {

                            if (AudioPatterRecorder.State.INITIALIZING == recorder.getState()) {
                                recordFilePath = FileNameGenerator.getDirectory() + "/" + FileNameGenerator.getSamepleName(eventNumber, sampleNumber);
                                recorder.setOutputFile(recordFilePath);
                                recorder.prepare();
                                recorder.start();
                                statusDisplay.setText(recordFilePath + " is recording");
                            } else if (AudioPatterRecorder.State.ERROR == recorder.getState()) {
                                recorder.release();
                                recorder = AudioPatterRecorder.getInstance();
                                recorder.setOutputFile(recordFilePath);
                                statusDisplay.setText("Error found, recover with new recording instance");
                            }

                    } else {
                        statusDisplay.setText("The number of sample cannot exceed 5.");
                    }
                    break;
                }
                case R.id.saveBtn: {
                    if (sampleNumber < 5) {
                        Toast.makeText(getApplicationContext(), "The sample of sample must reach 5 or above", Toast.LENGTH_SHORT).show();

                    } else
                    {
                        String eventNamePRES = "E"+eventNumber+"Name";
                        String editTextVal = eventName.getText().toString();
                        editor.putString(eventNamePRES,editTextVal);
                        editor.putInt(NUMBEROFEVENTS,++eventNumber);
                        editor.commit();

                        Toast.makeText(getApplicationContext(),editTextVal+" have been saved.",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),MainPage.class);
                        startActivity(intent);
                    }
                    break;
                }
                case R.id.eCancelBtn:
                {
                    String nameForSMin = "E" + eventNumber + "STM";
                    String nameForSHour = "E" + eventNumber + "STH";
                    String nameForEMin = "E" + eventNumber + "ETM";
                    String nameForEHour = "E" + eventNumber + "ETH";
                    if(sharedpreferences.contains(nameForSMin))
                        editor.remove(nameForSMin);
                    if(sharedpreferences.contains(nameForSHour))
                        editor.remove(nameForSHour);
                    if(sharedpreferences.contains(nameForEMin))
                        editor.remove(nameForEMin);
                    if(sharedpreferences.contains(nameForEHour))
                        editor.remove(nameForEHour);

                    for(int i = 0; i < 5 ; i++)
                    {
                        if(sharedpreferences.contains(FileNameGenerator.getSamepleName(eventNumber,i)))
                        {
                            editor.remove(FileNameGenerator.getSamepleName(eventNumber,i));
                        }
                    }
                    editor.commit();
                    String editTextVal = eventName.getText().toString();
                    eventName.setText("");
                    sampleNumber =0;
                    updateListView();
                    Toast.makeText(getApplicationContext(),editTextVal +" have been cancelled.",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainPage.class);
                    startActivity(intent);
                    break;
                }
            }
        }
    };

    private TimePickerDialog.OnTimeSetListener endTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                // the callback received when the user "sets" the TimePickerDialog in the dialog
                public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                    String nameForMin = "E" + eventNumber + "ETM";
                    String nameForHour = "E" + eventNumber + "ETH";

                    editor.putInt(nameForHour, hourOfDay);
                    editor.putInt(nameForMin, min);
                    editor.commit();
                    endTimeBtn.setText("Start time - " + pad(hourOfDay) + ":" + pad(min));
                    Toast.makeText(getApplicationContext(), "Time selected is : " + pad(hourOfDay) + ":" + pad(min), Toast.LENGTH_LONG).show();
                }
            };

    private TimePickerDialog.OnTimeSetListener startTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                    String nameForMin = "E" + eventNumber + "STM";
                    String nameForHour = "E" + eventNumber + "STH";

                    editor.putInt(nameForHour, hourOfDay);
                    editor.putInt(nameForMin, min);
                    editor.commit();
                    startTimeBtn.setText("Start time - " + pad(hourOfDay) + ":" + pad(min));
                    Toast.makeText(getApplicationContext(), "Time selected is : " + pad(hourOfDay) + ":" + pad(min), Toast.LENGTH_LONG).show();
                }
            };

    //add leading function
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    private void updateListView()
    {
        ArrayList<String> sampleListItems = new ArrayList<String>();
        for(int i = 0; i < 5 ; i++)
        {
            Log.wtf("",""+sharedpreferences.getAll());
            Log.wtf(this.getClass().toString(),sharedpreferences.getBoolean(FileNameGenerator.getSamepleName(eventNumber,sampleNumber),false)+"");
            if(sharedpreferences.getBoolean(FileNameGenerator.getSamepleName(eventNumber,i),false))
            {
                sampleListItems.add("Sample "+i + " is created");
            }
        }

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sampleListItems);
        sampleList.setAdapter(itemsAdapter);


    }


    //function for initialization for all attribute
    private void initialization() {
        endTimeBtn = (Button) findViewById(R.id.endTimeBtn);
        startTimeBtn = (Button) findViewById(R.id.startTimeBtn);
        homeBtn = (Button) findViewById(R.id.eHomeBtn);
        startRcBtn = (Button) findViewById(R.id.startRcBtn);
        endRcBtn = (Button) findViewById(R.id.endRcBtn);
        eCancelBtn = (Button) findViewById(R.id.eCancelBtn);
        eSaveBtn = (Button) findViewById(R.id.saveBtn);

        statusDisplay = (TextView) findViewById(R.id.statusDisplay);
        sampleList = (ListView) findViewById(R.id.sampleList);
        eventName = (EditText) findViewById(R.id.nameEdit);

        bgLLayout = (LinearLayout) findViewById(R.id.bgLLayout);
        topGlayout = (GridLayout) findViewById(R.id.topGLayout);
        middleGlayout = (GridLayout) findViewById(R.id.middleGLayout);


        recorder = AudioPatterRecorder.getInstance();

        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        eventNumber = sharedpreferences.getInt(NUMBEROFEVENTS, 0);

        sampleNumber = 0;

        eventName.setText("event " + eventNumber);

        startTimeBtn.setOnClickListener(btnListener);
        endTimeBtn.setOnClickListener(btnListener);
        homeBtn.setOnClickListener(btnListener);
        startRcBtn.setOnClickListener(btnListener);
        endRcBtn.setOnClickListener(btnListener);
        eCancelBtn.setOnClickListener(btnListener);
        eSaveBtn.setOnClickListener(btnListener);

        bgLLayout.setOnClickListener(hideKeyboardListener);
        topGlayout.setOnClickListener(hideKeyboardListener);
        middleGlayout.setOnClickListener(hideKeyboardListener);

        setCurrentTimeOnView();

    }

    public void setCurrentTimeOnView() {
        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        startTimeBtn.setText("Start Time : " +
                new StringBuilder().append(pad(hour))
                        .append(":").append(pad(minute)));
        endTimeBtn.setText("End Time : " +
                new StringBuilder().append(pad(hour))
                        .append(":").append(pad(minute)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_recorder);
        initialization();
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(eventName.getWindowToken(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_recorder, menu);
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

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        Log.wtf("", id + "");
        switch (id) {
            case R.id.startTimeBtn:
                return new TimePickerDialog(this,
                        startTimeSetListener, hour, minute, true);
            case R.id.endTimeBtn:
                return new TimePickerDialog(this,
                        endTimeSetListener, hour, minute, true);
        }
        return null;
    }


}
