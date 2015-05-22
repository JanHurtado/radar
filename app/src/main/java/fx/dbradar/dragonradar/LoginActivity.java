package fx.dbradar.dragonradar;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class LoginActivity extends ActionBarActivity {

    private EditText usernameEditText;
    private Button button;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = (EditText) findViewById(R.id.usernameEditExt);
        button   = (Button) findViewById(R.id.button);


    }

    public void login(View view) {

        Toast.makeText(this, "Got it", Toast.LENGTH_SHORT).show();

        username = usernameEditText.getText().toString();

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
            String url="http://179.43.127.156:9001/add_user";

            System.setProperty("http.proxyHost", "proxy.example.com");
            System.setProperty("http.proxyPort", "8080");

            ///
            try {
                URL object = new URL(url);

                HttpURLConnection con = (HttpURLConnection) object.openConnection();

                con.setDoOutput(true);
                con.setRequestMethod("POST");
                con.setUseCaches(false);
                con.setDoInput(true);
                con.setConnectTimeout(10000);
                con.setReadTimeout(10000);
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");


                JSONObject data = new JSONObject();
                data.put("username", username);

                OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
                out.write(data.toString());
                out.close();

                StringBuilder sb = new StringBuilder();

                int HttpResult = con.getResponseCode();

                if(HttpResult == HttpURLConnection.HTTP_OK){

                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));

                    String line = null;

                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    br.close();

                    Log.d("RadarActivity", "" + sb.toString());

                }else{
                    Log.d("RadarActivity", "" + con.getResponseMessage());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ///

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
