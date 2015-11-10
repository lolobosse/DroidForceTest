package jobninja.eu.internetinstrumentationtest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity implements jobninja.eu.internetinstrumentationtest.Callback {

    TextView response, status;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        response = (TextView) findViewById(R.id.response);
        status = (TextView) findViewById(R.id.status);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl();
            }
        });
        setSupportActionBar(toolbar);
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

    private void openUrl(){
        new InternetThread(this).start();
    }

    @Override
    public void response(final boolean isOk, final String responseText) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isOk){
                    status.setTextColor(Color.GREEN);
                    status.setText("Status: OK");
                    response.setText(responseText);
                }
                else{
                    status.setTextColor(Color.RED);
                    status.setText("Status: ERROR");
                    response.setText("No Response yet");
                }
            }
        });
    }


    private class InternetThread extends Thread{

        Callback c;
        public InternetThread(Callback c) {
            this.c = c;
        }

        @Override
        public void run() {
            try {
                // get URL content
                URL url = new URL("http://www.jobninja.eu");
                URLConnection conn = url.openConnection();

                // open the stream and put it into BufferedReader
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

                String inputLine;
                StringBuilder sb = new StringBuilder();
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                c.response(true, sb.toString());
                br.close();
            } catch (Exception e){
                c.response(false, null);
                e.printStackTrace();
            }

        }
    }
}
