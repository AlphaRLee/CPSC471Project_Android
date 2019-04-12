package com.example.cpsc471expensesubmitter;

import android.content.Intent;
import android.graphics.Bitmap;

import android.net.Network;
import android.os.Bundle;
import android.provider.MediaStore;
//import android.support.constraint.solver.Cache;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;

//import android.widget.Toast;
//android.permission.INTERNET;

//public static final String INTERNET;

public class MainActivity extends AppCompatActivity {

//    private TextView mTextMessage;
//
//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            Fragment selectedFragment = null;
//            switch (item.getItemId()) {
//                case R.id.navigation_home:
//                    mTextMessage.setText(R.string.title_home);
//                    return true;
//                case R.id.navigation_newexpense:
//                    mTextMessage.setText(R.string.title_newexpense);
//                    return true;
//                case R.id.navigation_account:
//                    mTextMessage.setText(R.string.title_account);
//                    return true;
//            }
//
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                    selectedFragment).commit();
//
//            return false;
//        }
//    };


    ImageView imageView;

    int expenseNumber;
    String description;


    EditText expenseNumberInput;
    EditText descInput;

    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mTextMessage = (TextView) findViewById(R.id.message);
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Camera button functionality
        Button btnCamera = (Button) findViewById(R.id.btnCamera);
        imageView = (ImageView) findViewById(R.id.imageView);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            //@Nullable

            public void onClick(View view) {
                Log.d("PHOTOBUTTON", "Taking picture");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

        // Submit button functionality
        expenseNumberInput = (EditText) findViewById(R.id.expenseCodeInput);
        descInput = (EditText) findViewById(R.id.descInput);

        Button subButton = (Button) findViewById(R.id.submitButton);
        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    expenseNumber = Integer.valueOf(expenseNumberInput.getText().toString());
                } catch (java.lang.NumberFormatException ex) {
                    ;
                } //TODO add error message?

                description = descInput.getText().toString();


                // TODO: add in POST request functionality
                //String s = null;
                //s.split();

                Log.d("CREATION", "Expensenumber is: " + expenseNumber);
                Log.d("CREATION", "Description is: " + description);


                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try  {
                            sendDataToServer(imageView, expenseNumber, description);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();

            }
        });


    }

    // Puts image taken by user onto screen as preview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        imageView.setImageBitmap(bitmap);
    }

    private void sendDataToServer(ImageView imageView, int expenseNumber, String description) {
        Log.d("POSTReq", "In sendDataToServer");



        try{
        URL url = new URL("http://httpbin.org/ip");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            Log.d("POSTreq", "Connection successful ");
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                Log.d("POSTreq", "Inputstream successful ");
            String response = readStream(in);
                Log.d("POSTreq", "Reading inputstream successful ");
            Log.d("POSTresp", "Response was: " + response);
        } finally {
            urlConnection.disconnect();
        }
    } catch (Exception e) {Log.d("POSTresp", "Exception : " + e);}



/*
        RequestQueue requestQueue;


// Instantiate the cache
        DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

// Set up the network to use HttpURLConnection as the HTTP client.
        BasicNetwork network = new BasicNetwork(new HurlStack());

// Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

// Start the queue
        requestQueue.start();

        String url ="http://httpbin.org/ip";

// Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                        Log.d("1stHTTPresp", "Response was: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.d("1stHTTPresp", "No response");
                    }
                });

// Add the request to the RequestQueue.
        requestQueue.add(stringRequest);


         beginning of request



        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        url ="http://httpbin.org/ip"; //TODO get URL from Richard
        Log.d("POSTReq", "Sending request to " + url);

        // Request a string response from the provided URL.
        stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("HTTPresp", "Response was: " + response);
                        //textView.setText("Response is: "+ response.substring(0,500)); //TODO add response to screen - popup?
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //textView.setText("That didn't work!"); //TODO add response to screen - popup?
                Log.d("HTTPresp", "No response from request!!!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
    */
    }

        private String readStream (InputStream is){
            try {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                int i = is.read();
                while (i != -1) {
                    bo.write(i);
                    i = is.read();
                }
                Log.d("READSTREAM", "bo was: " + bo.toString());
                return bo.toString();
            } catch (IOException e) {
                Log.d("READSTREAM", "Exception was: " + e);
                return "";
            }
        }

    public String convert(InputStream inputStream) throws IOException {




        String readLine;
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        while (((readLine = br.readLine()) != null)) {
            Log.d("CONVERT", "String was: " + readLine);

        }
        /*
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        byte[] byteArray = buffer.toByteArray();

        String text = new String(byteArray, StandardCharsets.UTF_8);
        Log.d("CONVERT", "String was: " + text);
        return text;
        */

        return null;

    }




}
