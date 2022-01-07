package com.example.goodmorningweather20;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    EditText inCity;
    TextView Result;
    TextView FeelsLike;
    ImageView weatherIcon;
    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String appid = "7940c5e3e502f128d987daedaf9a60db";
    DecimalFormat df = new DecimalFormat("#");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inCity = findViewById(R.id.inCity);
        Result = findViewById(R.id.tvResult);
        FeelsLike = findViewById(R.id.FeelsLike);
        weatherIcon = findViewById(R.id.weatherIcon);

    }

    public void getWeatherDetails(View view) {
        String tempUrl = "";
        String city = inCity.getText().toString().trim();
        if (city.equals("")) {
            Result.setText("Please input a city");
        }else{
            tempUrl = url+"?q="+city+"&appid="+appid;
            }
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String output = "";
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                        String icon = jsonObjectWeather.getString("icon");
                        String iconUrl = "http://openweathermap.org/img/wn/"+icon+"@2x.png";
                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                        double temp = (1.8)*(jsonObjectMain.getDouble("temp") - 273.15)+32;
                        double feelsLike = (1.8)*(jsonObjectMain.getDouble("feels_like") - 273.15)+32;
                        double low = (1.8)*(jsonObjectMain.getDouble("temp_min") - 273.15)+32;
                        double high = (1.8)*(jsonObjectMain.getDouble("temp_max") - 273.15)+32;
                        JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                        String clouds = jsonObjectClouds.getString("all");
                        JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                        String countryName = jsonObjectSys.getString("country");
                        String cityName = jsonResponse.getString("name");

                        Result.setTextColor(Color.rgb(0,0,0));
                        Result.setTextSize(30);
                        output +=" " + df.format(temp) + "°F"; //"Current weather of " + cityName + " (" + countryName + ")"
                                //+ " Temp: " + df.format(temp) + "°F";
                                //+ "\n Feels Like: " + df.format(feelsLike) + "°F"
                                //+ "\n Description: " + description;
                                //+ "\n Cloudiness: " + clouds + "%";
                        FeelsLike.setText("Feels Like: "+df.format(feelsLike) + "°F" +"\n"+df.format(low)+"/"+df.format(high));
                        Result.setText(output);
                        Result.setTextColor(Color.rgb(238, 238, 238));
                        inCity.setText(cityName+", "+countryName);
                        Glide.with(weatherIcon).load(iconUrl).into(weatherIcon);

                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }