package ua.org.ahf.ahfdb;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static DBHelper dbHelper;

    private static final String JSON_URL = "http://ahf.org.ua/get-data.php";
    String myJSON;
    JSONArray companies = null;
    private static final String TAG_RESULT = "result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart() {
        super.onStart();
        dbHelper = new DBHelper(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        dbHelper.close();
    }

    public void showHuntingGrounds(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("GROUND_TYPE", 1);
        startActivity(intent);
    }

    public void showFishingGrounds(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("GROUND_TYPE", 2);
        startActivity(intent);
    }

    public void showPondFarms(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("GROUND_TYPE", 3);
        startActivity(intent);
    }

    public void updateDatabase(View view) {
        clearDatabase(null);
        getJSON(JSON_URL);
    }

    public void clearDatabase(View view) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(dbHelper.TABLE_COMPANY, null, null);
    }

    private void getJSON(String url) {
        class GetJSON extends AsyncTask<String, Void, String> {
//            ProgressDialog loading;

//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                loading = ProgressDialog.show(MainActivity.this, "Please Wait...",null,true,true);
//            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while((json = bufferedReader.readLine()) != null){
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch(Exception e){
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String result) {
//                super.onPostExecute(s);
                myJSON = result;
                parseJSON();
//                loading.dismiss();
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute(url);
    }

    protected void parseJSON(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            companies = jsonObj.getJSONArray(TAG_RESULT);

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            for(int i = 0; i < companies.length(); i++){
                JSONObject c = companies.getJSONObject(i);

                String lat = c.getString(DBHelper.COLUMN_LAT);
                String lng = c.getString(DBHelper.COLUMN_LNG);

                if(lat.equals("") || lng.equals("")) {
                    lat = "0.0";
                    lng = "0.0";
                }

                values.put(DBHelper.COLUMN_ID, c.getInt(DBHelper.COLUMN_ID));
                values.put(DBHelper.COLUMN_IS_MEMBER, c.getInt(DBHelper.COLUMN_IS_MEMBER));
                values.put(DBHelper.COLUMN_IS_HUNTING_GROUND, c.getInt(DBHelper.COLUMN_IS_HUNTING_GROUND));
                values.put(DBHelper.COLUMN_IS_FISHING_GROUND, c.getInt(DBHelper.COLUMN_IS_FISHING_GROUND));
                values.put(DBHelper.COLUMN_IS_POND_FARM, c.getInt(DBHelper.COLUMN_IS_POND_FARM));
                values.put(DBHelper.COLUMN_LAT, Double.parseDouble(lat));
                values.put(DBHelper.COLUMN_LNG, Double.parseDouble(lng));
                values.put(DBHelper.COLUMN_NAME, c.getString(DBHelper.COLUMN_NAME));
                values.put(DBHelper.COLUMN_DESCRIPTION, c.getString(DBHelper.COLUMN_DESCRIPTION));

                db.insert(DBHelper.TABLE_COMPANY, null, values);
            }
            Toast.makeText(getApplicationContext(), "Update success!", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Update failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
