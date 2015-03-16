package rms.fyp.rmsphone;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


public class ChooseSize extends ActionBarActivity {
    private String restaurantId,wsForRestaurant,serverHost;
    private Intent intent;
    private Button backBtn,ticketBtn;
    private TextView rName,rAddress,rPhoneNo,rOpening,rDesc;
    private ListView sizePicker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_size);
        initialization();
        new getRestaurantInfo().execute(wsForRestaurant);
    }

    private void initialization() {
        intent = getIntent();
        restaurantId = intent.getStringExtra("restaurantId");
        backBtn = (Button)findViewById(R.id.backBtn);
        ticketBtn = (Button)findViewById(R.id.ticketBtn2);
        rName = (TextView)findViewById(R.id.restaurantNameField);
        rAddress = (TextView)findViewById(R.id.restaurantAddressField);
        rPhoneNo = (TextView) findViewById(R.id.restaurantNumberField);
        rOpening = (TextView) findViewById(R.id.restaurantOpeningField);
        rDesc = (TextView) findViewById(R.id.restaurantDescriptionField);
        sizePicker = (ListView) findViewById(R.id.ticketTypeList);
        serverHost = this.getResources().getString(R.string.serverHost);
        wsForRestaurant = serverHost+"/rms/restaurant?id="+restaurantId;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_size, menu);
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

    private class getRestaurantInfo  extends AsyncTask<String, Void, String> {

        // Required initialization

        private String Error = null;

        // Call after onPreExecute method
        protected String doInBackground(String... urls) {
            String response = "";
            for (String url : urls) {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                try {
                    HttpResponse execute = client.execute(httpGet);
                    InputStream content = execute.getEntity().getContent();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.wtf("response", response);
            return response;
        }

        protected void onPostExecute(String result) {
            // NOTE: You can call UI Element here.
            if (Error != null)
                Log.wtf("error : ", Error);
            else
            {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.wtf("JSON = ",jsonObject.toString());
                    rName.setText(jsonObject.optString("name"));
                    rOpening.setText(jsonObject.optString("openingHours"));
                    rDesc.setText(jsonObject.optString("description"));
                    rPhoneNo.setText(jsonObject.optString("phoneNo"));
                    rAddress.setText(jsonObject.optString("address") );
                } catch (JSONException e) {
                    Log.wtf("json problems",e.toString());

                }
            }
        }

    }
}
