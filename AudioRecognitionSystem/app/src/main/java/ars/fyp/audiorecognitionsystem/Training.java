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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import ars.fyp.utils.TimeUtils;

public class Training extends ActionBarActivity {
    private Button homeBtn,addBtn;
    private ListView eventList;
    private ArrayList<HashMap<String, String>> eventListItems;
    private SharedPreferences sharedPreferences;
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

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        addBtn.setOnClickListener(btnOnClick);
        homeBtn.setOnClickListener(btnOnClick);
        initialListView();
    }

    private void initialListView()
    {
        eventListItems = new ArrayList<HashMap<String, String>>();
        int numberOfLoop = sharedPreferences.getInt(NUMBEROFEVENTS,0);
        for(int i = 0 ; i < numberOfLoop;i++)
        {
            String eventNamePRES = "E"+i+"Name";
            String nameForSMin = "E" + i + "STM";
            String nameForSHour = "E" + i + "STH";
            String nameForEMin = "E" + i + "ETM";
            String nameForEHour = "E" + i + "ETH";
            String sTime = TimeUtils.pad(sharedPreferences.getInt(nameForSHour,0)) + ":" + TimeUtils.pad(sharedPreferences.getInt(nameForSMin,0));
            String eTime = TimeUtils.pad(sharedPreferences.getInt(nameForEHour,0)) + ":" + TimeUtils.pad(sharedPreferences.getInt(nameForEMin,0));
            HashMap<String,String> tmpMap = new HashMap<String,String>();
            tmpMap.put("name", "Event : " + sharedPreferences.getString(eventNamePRES, ""));
            tmpMap.put("time","Duration : " +sTime +" - "+ eTime);

            eventListItems.add(tmpMap);
        }

        ListAdapter adapter = new SimpleAdapter(this, eventListItems,
                R.layout.list_event_info,
                new String[]{"name", "time"}, new int[]{
                R.id.eventName, R.id.time});
        eventList.setAdapter(adapter);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        initialization();
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
