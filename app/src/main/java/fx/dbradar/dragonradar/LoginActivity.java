package fx.dbradar.dragonradar;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import fx.dbradar.dragonradar.overlay.testActivity;


public class LoginActivity extends ActionBarActivity {

    //private EditText usernameEditText;
    private Button button;
    private Spinner spinner;
    private String username;



    @Override
    public void onStart(){
        super.onStart();
        //Toast.makeText(this, "onStart()", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onStop(){
        super.onStop();
        //Toast.makeText(this, "onStop()", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onResume(){
        super.onResume();
        //Toast.makeText(this, "onResume()", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onPause(){
        super.onPause();
        //Toast.makeText(this, "onPause()", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onRestart(){
        super.onRestart();
        //Toast.makeText(this, "onRestart()", Toast.LENGTH_LONG).show();
    }

    //@Override
    /*public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "onCreate()", Toast.LENGTH_LONG).show();
        setContentView(R.layout.activity_login);

        //usernameEditText = (EditText) findViewById(R.id.usernameEditExt);
        button   = (Button) findViewById(R.id.button);

        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccounts();
        String googleAccount = "";
        List<String> list = new ArrayList<String>();

        for (Account ac : accounts) {
            String acname = ac.name;
            String actype = ac.type;
            if(true){
                list.add(ac.name+" ("+ac.type+")");
            }
        }

        //usernameEditText.setText(googleAccount);

        spinner = (Spinner) findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
// Specify the layout to use when the list of choices appears
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

    }


    public void login(View view) {

        Toast.makeText(this, "Got it", Toast.LENGTH_SHORT).show();

        //username = usernameEditText.getText().toString();
        username = spinner.getSelectedItem().toString();

        new LoginTask(this).execute(username);

//        Intent intent = new Intent(this, RadarActivity.class);
//        intent.putExtra("fx.username", username);
//        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
            //Intent intent = new Intent(this, testActivity.class);
            //startActivity(intent);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            // set title
            alertDialogBuilder.setTitle("Your Title");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Click yes to exit!")
                    .setCancelable(false)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, close
                            // current activity
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LoginTask extends AsyncTask<String, Void, String> {
        private Context ctx;

        public LoginTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            return "ok";

        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(ctx, "Entrando", Toast.LENGTH_LONG).show();


            Intent intent = new Intent(ctx, RadarActivity.class);
            intent.putExtra("fx.username", username);
            startActivity(intent);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }


    }
}
