package com.example.nhaydel.naxosscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.io.IOException;


public class MainActivity extends AppCompatActivity{
    TextView songAuthor;
    TextView songName;
    Intent intent;
    Button mScanButton;
    IntentIntegrator integrator = new IntentIntegrator(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mScanButton = (Button) findViewById(R.id.scanButton);
        intent = new Intent(this, NaxosSearch.class);
        mScanButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                integrator.setPrompt("Scan a barcode");
                integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setOrientationLocked(false);
                integrator.setCaptureActivity(CaptureActivityPortrait.class);
                integrator.setBeepEnabled(false);
                integrator.initiateScan();

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //songAuthor = (TextView) findViewById(R.id.song_author);
        //songName = (TextView) findViewById(R.id.song_name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        String scanContent = scanningResult.getContents();
        /*RequestQueue queue = Volley.newRequestQueue(this);
        String url = "API URL HERE";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {

                            JSONObject jResults = new JSONObject(response);
                            String title = jResults.getString("title");
                            String author = jResults.getString("author");
                            searchPage(title, author);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                songName.setText("Didn't work");
            }*/
       // });
// Add the request to the RequestQueue.
        //queue.add(stringRequest);
        searchPage("Marche solennelle :(Marche du sacre) : pour orgue /Jean Fran\u00e7aix", "Fran\u00e7aix, Jean, 1912-1997.");
    }

    public void searchPage(String title, String author){
        intent.putExtra("AUTHOR", author);
        intent.putExtra("SONG", title);
        startActivity(intent);
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
