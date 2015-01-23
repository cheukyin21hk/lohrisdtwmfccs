package ch3.exercise.book.exercise2;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    private Button btn0 ;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btnClr;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn0 = (Button)findViewById(R.id.btn0);
        btn1 = (Button)findViewById(R.id.btn1);
        btn2 = (Button)findViewById(R.id.btn2);
        btn3 = (Button)findViewById(R.id.btn3);
        btnClr = (Button)findViewById(R.id.btnClr);
        result = (TextView)findViewById(R.id.displayView);

        btn0.setOnClickListener(myListener);
        btn1.setOnClickListener(myListener);
        btn2.setOnClickListener(myListener);
        btn3.setOnClickListener(myListener);
        btnClr.setOnClickListener(myListener);

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

    private Button.OnClickListener myListener = new Button.OnClickListener(){
        @Override
        public void onClick(View v)
        {
           String displayTxt = result.getText().toString();
           switch(v.getId())
            {
                case R.id.btn0:
                    displayTxt=displayTxt.concat("0");
                    result.setText(displayTxt);
                    break;
                case R.id.btn1:
                    displayTxt=displayTxt.concat("1");
                    result.setText(displayTxt);
                    break;
                case R.id.btn2:
                    displayTxt=displayTxt.concat("2");
                    result.setText(displayTxt);
                    break;
                case R.id.btn3:
                    displayTxt=displayTxt.concat("3");
                    result.setText(displayTxt);
                    break;
                case R.id.btnClr:
                    displayTxt="";
                    result.setText(displayTxt);
                    break;
            }
        }
    };
}
