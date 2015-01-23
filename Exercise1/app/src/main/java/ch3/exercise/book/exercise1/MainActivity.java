package ch3.exercise.book.exercise1;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

    private EditText inputInt;
    private TextView result;
    private Button calBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get Button View Textfield
        inputInt =(EditText)findViewById(R.id.input);
        result =(TextView)findViewById(R.id.result);
        calBtn = (Button)findViewById(R.id.button);

        calBtn.setOnClickListener(calBtnListener);
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

    private Button.OnClickListener calBtnListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            int input = Integer.parseInt(inputInt.getText().toString());
            int sum = 0;
            for(int i = 1; i <= input ;i++) {
                sum += i;
            }
            result.setText(sum+"");
        }
    };


}
