package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText cityEditText;
    TextView weatherTextView;
    String city;

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("Weather Content",weatherInfo);
                JSONArray arr = new JSONArray(weatherInfo);
                String message = "";
                for(int i=0; i<arr.length();i++){
                    JSONObject jsonPart = arr.getJSONObject(i);
                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");
                    if(!main.equals("") && !description.equals("")){
                        message = main +": "+ description + "\r\n";
                    }
                }
                if(!message.equals("")){
                    weatherTextView.setText(message);
                }else{
                    weatherTextView.setText("");
                    Toast.makeText(getApplicationContext(), "Couldn't find weather :(", Toast.LENGTH_SHORT).show();
                }

            }catch (Exception e){
                weatherTextView.setText("");
                Toast.makeText(getApplicationContext(), "Couldn't find weather :(", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    public void showWeather(View view){
        try{
            DownloadTask downloadTask = new DownloadTask();
            city = cityEditText.getText().toString();
            String encodedCityName = URLEncoder.encode(city,"UTF-8");
            downloadTask.execute("https://openweathermap.org/data/2.5/weather?q="+encodedCityName+"&appid=439d4b804bc8187953eb36d2a8c26a02").get();

        }catch (Exception e){
            weatherTextView.setText("");
            Toast.makeText(getApplicationContext(), "Couldn't find weather :(", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(cityEditText.getWindowToken(),0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        cityEditText = findViewById(R.id.cityEditText);
        weatherTextView = findViewById(R.id.weatherTextView);
    }
}
