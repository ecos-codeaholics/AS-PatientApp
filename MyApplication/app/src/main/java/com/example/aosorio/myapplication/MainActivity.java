/** Copyright or License
 *
 */
package com.example.aosorio.myapplication;
/**
 * Package: com.example.aosorio.myapplication
 *
 * Class: MyApplication
 *
 * Original Author: @author AOSORIO
 *
 * Description: Basic application for patient
 *
 * Implementation: To be used as part of our AS experiment or as a base
 *
 * Created: Jun 6, 2016
 *
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonToken;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    Context mContext;
    Button btnPost;
    private JsonEpisodeHelper data;
    private ArrayList<JsonEpisodeHelper> listOfEpisodes;
    private static String STORAGE_PATH;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        STORAGE_PATH = this.getFilesDir() + "/";
        listOfEpisodes = new ArrayList<JsonEpisodeHelper>();

        //**********************************************************
        //  In here, implement a form to capture data from the View
        //Get episode from Pacient
        data = createData();
        data = createData();
        listOfEpisodes.add(data);

        //**********************************************************

        btnPost = (Button) findViewById(R.id.btnPost);

        //**********************************************************
        //Check we are online to send it to webserver
        if( isOnline() ) {
            btnPost.setBackgroundColor(0xFF00CC00);
        } else {
            Toast.makeText(this,"You are offline! Turn on your network. Episode will be saved", Toast.LENGTH_LONG).show();
        }

        //**********************************************************
        //Syncronize with server if there are episodes saved locally

        //1. Get list of files. if none, there is no need for synchronizing
        FileFinder finder = new FileFinder(STORAGE_PATH);
        finder.processRoot();
        ArrayList<String> json_files = finder.getAllFiles();

        if ( !json_files.isEmpty() ) {
            //2 . synchronize
            int result = Synchronize( json_files );
        }

        //**********************************************************
        btnPost.setOnClickListener(this);
        //**********************************************************
    }

    private int Synchronize(ArrayList<String> json_files) {

        if (isOnline()) {
            Gson GSON = new GsonBuilder().create();
            Log.d(TAG, "Total files found: " + json_files.size());
            Iterator<String> files = json_files.iterator();
            while( files.hasNext() ) {
                String file = files.next();
                String[] sfile = file.split("/");


                Log.d(TAG, "Found json file: " + sfile[sfile.length-1]);
                try {
                    Gson gson = new GsonBuilder().create();
                    FileInputStream inputStream = openFileInput(sfile[sfile.length-1]);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    JsonEpisodeHelper e1 = gson.fromJson(reader, JsonEpisodeHelper.class);
                    Log.d(TAG, "Read episode with CC:  " + e1.getCedula());
                    listOfEpisodes.add(e1);
                } catch ( Exception e) {
                    Log.d(TAG, "Cannot read: " + sfile[sfile.length-1]);
                } finally {
                    deleteFile(sfile[sfile.length-1]);
                }
            }


            btnPost.setBackgroundColor(0xFF00CC00);
            String fullPath = ConnectionConfig.WEB_SERVER + ":"
                    + ConnectionConfig.WEB_SERVER_PORT + ConnectionConfig.CREATE_PATH;
            RegisterEpisode mytask = new RegisterEpisode();
            mytask.execute(fullPath);


            return 1;

        }

        return 0;
    }

    private JsonEpisodeHelper createData() {
        JsonEpisodeHelper e1 = new JsonEpisodeHelper();
        e1.setCedula(21654436);
        e1.setFecha("2016/12/31");
        e1.setHora("12:00:00");
        e1.setIntensidad(2);
        e1.setNivelDolor(4);
        Log.d(TAG, "Episode created");
        Writer writer = null;
        try {
            String filename = STORAGE_PATH + generateUniqueFileName();
            writer = new FileWriter(filename);
            Gson gson = new GsonBuilder().create();
            gson.toJson(e1, writer);
        } catch (Exception e) {
            // TODO: handle exception
            Log.d(TAG, "Problem saving to JSON file");
            e.printStackTrace();
        } finally {
            if (writer != null)
                try {
                    writer.close();
                    Log.d(TAG, "Writer closed");
                } catch ( Exception e) {
                    // TODO: handle exception
                }
        }

        return e1;
    }

    private String generateUniqueFileName() {
        String filename = "";
        long millis = System.currentTimeMillis();
        String datetime = new Date().toGMTString();
        datetime = datetime.replace(" ", "");
        datetime = datetime.replace(":", "");
        String rndchars = RandomStringUtils.randomAlphanumeric(15).toUpperCase();
        filename = rndchars + "_" + datetime + "_" + millis  + ".json";
        Log.d(TAG, "filename created: " + filename );
        return filename;
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if ( networkInfo != null && networkInfo.isConnected() ) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void onClick(View v) {
        if (isOnline()) {
            btnPost.setBackgroundColor(0xFF00CC00);
            String fullPath = ConnectionConfig.WEB_SERVER + ":" + ConnectionConfig.WEB_SERVER_PORT + ConnectionConfig.CREATE_PATH;
            RegisterEpisode mytask = new RegisterEpisode();
            mytask.execute(fullPath);
        }
        else {
            btnPost.setBackgroundColor(0xFF00CC00);

        }
    }

    private class RegisterEpisode extends AsyncTask<String, Void, Integer > {

        ProgressDialog progressDialog;

        public RegisterEpisode() { super(); }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(mContext,"Wait!","Starting...");
        }

        @Override
        protected Integer doInBackground(String... params) {

            //InputStream inputStream = null;

            Gson GSON = new GsonBuilder().create();
            HttpURLConnection urlConnection = null;
            Integer result = 0;

            try {
                /* forming th java.net.URL object */

                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                 /* optional request header */
                urlConnection.setRequestProperty("Content-Type", "application/json");

                /* for Post request */
                urlConnection.setRequestMethod("POST");

                int HttpResult = 0;

                Iterator<JsonEpisodeHelper> episode = listOfEpisodes.iterator();
                while( episode.hasNext() ) {
                    data = episode.next();

                    String JsonDATA = GSON.toJson(data).toString();

                    Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                    writer.write(JsonDATA);
                    writer.flush();
                    writer.close();
                    HttpResult = urlConnection.getResponseCode();

                    if (HttpResult == 200) {
                        progressDialog = ProgressDialog.show(mContext, "Result", "Ok");
                    }

                }

                return HttpResult;

            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return result;

        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            if(result == 1) {
                progressDialog = ProgressDialog.show(mContext,"Done!","Done!");
            }
            progressDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressDialog.dismiss();
        }
    }

}
