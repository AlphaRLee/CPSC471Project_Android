package com.example.cpsc471expensesubmitter;

import android.content.Intent;
import android.graphics.Bitmap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {


    String upload_URL = "http://10.13.153.65:8000/api/expense";
    JSONObject jsonObject;
    RequestQueue rQueue;

    ImageView imageView;

    String expenseNumber;
    String description;


    EditText expenseNumberInput;
    EditText descInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mTextMessage = (TextView) findViewById(R.id.message);
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Camera button functionality
        Button btnCamera = findViewById(R.id.btnCamera);
        imageView = findViewById(R.id.imageView);

        // Take photo when camera button is clicked
        btnCamera.setOnClickListener(new View.OnClickListener() {
            //@Nullable

            public void onClick(View view) {
                Log.d("PHOTOBUTTON", "Taking picture");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

        // Submit button functionality
        expenseNumberInput = findViewById(R.id.expenseCodeInput);
        descInput = findViewById(R.id.descInput);

        // Send data to server when submit button is hit
        Button subButton = findViewById(R.id.submitButton);
        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    expenseNumber = expenseNumberInput.getText().toString();
                } catch (java.lang.NumberFormatException ex) {
                    Log.d("EXPENSE_CODE", "Getting expense code error: " + ex);
                }

                description = descInput.getText().toString();


                // TODO: add in POST request functionality
                //String s = null;
                //s.split();

                Log.d("CREATION", "Expense number is: " + expenseNumber);
                Log.d("CREATION", "Description is: " + description);


                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try  {
                            new CallAPI().execute(upload_URL, expenseNumber + ";" + description);
                            //sendDataToServer(expenseNumber, description);
                            uploadImage();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();

            }
        });


    }

    Bitmap bitmap;
    // Puts image taken by user onto screen as preview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bitmap = (Bitmap) data.getExtras().get("data");
        imageView.setImageBitmap(bitmap);
    }





    public void uploadImage(){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        try {
            jsonObject = new JSONObject();
            String imgname = String.valueOf(Calendar.getInstance().getTimeInMillis());
            jsonObject.put("name", imgname);
            //  Log.d("Image name", etxtUpload.getText().toString().trim());
            jsonObject.put("image", encodedImage);
            // jsonObject.put("aa", "aa");
        } catch (JSONException e) {
            Log.d("JSONerr", e.toString());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, upload_URL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.d("RESPONSE", jsonObject.toString());
                        rQueue.getCache().clear();
                        Toast.makeText(getApplication(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("POSTerr", volleyError.toString());
                Log.d("POSTerr", "Check if server running!");
            }
        });

        rQueue = Volley.newRequestQueue(MainActivity.this);
        rQueue.add(jsonObjectRequest);

    }

        // Test readStream from InputStream for debugging
    /*
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
    */

        // Old method of sending data to server
    /*
    private void sendDataToServer(String expenseNumber, String description) {
        Log.d("POSTReq", "In sendDataToServer");

        try {
            String data = expenseNumber + ";" + description; //This data will be sent to server
            URL url = new URL(upload_URL);
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setDoOutput(true);
        } catch (Exception e) {
            Log.d("SENDDATA", "Exception is " + e);
        }
    */

        // Test GET request for debugging
        /*
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
    */


}

// This sends the post request and expense and description data to the server
class CallAPI extends AsyncTask<String, String, String> {

    public CallAPI(){
        //set context variables if required
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String urlString = params[0]; // URL to call
        String data = params[1]; //data to post
        OutputStream out = null;

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            out = new BufferedOutputStream(urlConnection.getOutputStream());

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(data);
            writer.flush();
            writer.close();
            out.close();

            urlConnection.connect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Failure";
        }
        return "Success";
    }
}
