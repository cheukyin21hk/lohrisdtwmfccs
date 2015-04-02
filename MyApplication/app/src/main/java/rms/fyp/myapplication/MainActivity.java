package rms.fyp.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;


public class MainActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String url = "http://www.azlyrics.com/lyrics/kellyclarkson/whatdoesntkillyoustronger.html";
        new getHtml().execute(url);


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

    private class getHtml extends AsyncTask<String, Void, String> {
        private String Error = null;
        private String lyrics = "";

        // Call after onPreExecute method
        protected String doInBackground(String... urls) {

            try {
                boolean print = false;
                String inputLine;
                URL yahoo = new URL(
                        "http://www.azlyrics.com/lyrics/kellyclarkson/whatdoesntkillyoustronger.html");
                InputStreamReader isr = new InputStreamReader(yahoo.openStream(), "UTF-8");
                BufferedReader in = null;
                if(isr.ready())
                    in = new BufferedReader(isr,100000);
                if (in != null) {
                    while ((inputLine = in.readLine()) != null) {
                        if (inputLine.equals("<!-- end of lyrics -->"))
                            print = false;
                        if (print == true)
                            lyrics = lyrics + inputLine;
                        if (inputLine.equals("<!-- start of lyrics -->"))
                            print = true;
                        Log.wtf("Input Line", inputLine);
                    }
                    in.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.wtf("IO error", e.toString());
                e.printStackTrace();

            } catch (NullPointerException e) {
                Log.wtf("NULl pointer error", e.toString());
            }
            return lyrics;
        }

        protected void onPostExecute(String result) {
            // NOTE: You can call UI Element here.
            if (Error != null)
                Log.wtf("error : ", Error);
            else {
                Log.wtf("result ", result);
            }

        }

    }
}
