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
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        if( isOnline() ) {
            //connect to DB and send episode
            //retrieve new set of params
        } else {
            Toast.makeText(this,"You are offline! Turn on your network. Episode will be saved", Toast.LENGTH_LONG).show();
        }

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

    private class RegisterEpisode extends AsyncTask<String, Void, Integer > {

        ProgressDialog progressDialog;

        public RegisterEpisode() { super(); }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(mContext,"Wait!","Connecting to DB");
        }

        @Override
        protected Integer doInBackground(String... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            if(result == 1) {

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
