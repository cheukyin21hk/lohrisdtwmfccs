package ars.fyp.audiorecognitionsystem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import ars.fyp.utils.FileIOUtils;
import ars.fyp.utils.FileNameGenerator;


public class Configuration extends ActionBarActivity {

    private Button resetBtn,homeBtn;
    private String PREFS_NAME = "ARS_PREFS";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private AlertDialog.Builder alertDialogBuilder;
    private Button.OnClickListener btnListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cResetBtn: {
                    alertDialogBuilder.setTitle("Reset all data?");
                    alertDialogBuilder.setMessage("Yes to reset all")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    editor.clear();
                                    editor.commit();
                                    try {
                                        FileUtils.deleteDirectory(new File(FileNameGenerator.getDirectory()));
                                        File dir = new File(FileNameGenerator.getDirectory());
                                        dir.mkdir();
                                    } catch (IOException e) {
                                        Log.e(this.getClass().toString(), e.toString());
                                    }
                                    Toast.makeText(getApplicationContext(), "All data have been deleted", Toast.LENGTH_SHORT).show();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    break;
                }
                case R.id.cHomeBtn:
                {
                    Intent intent = new Intent(getApplicationContext(), MainPage.class);
                    startActivity(intent);
                    break;
                }

            }
        }
    };

    private void initialization() {
        resetBtn = (Button) findViewById(R.id.cResetBtn);
        homeBtn = (Button) findViewById(R.id.cHomeBtn);

        resetBtn.setOnClickListener(btnListener);
        homeBtn.setOnClickListener(btnListener);

        alertDialogBuilder = new AlertDialog.Builder(
                Configuration.this);
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        initialization();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_configuration, menu);
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
