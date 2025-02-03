package com.example.weatherly;



import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherly.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private static final int LOCATION_PERMISSION_STATE=1001;
    private LinearLayout forecastLayout;
    private static final String API_KEY = "b388d7c80d591e6e38e1e2875e58dc0f";
    private LinearLayout hourlyContainer;
    private static final String API_KEY_HOURLY = "c4c15e95e31a06ac35d95e801a521187";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        forecastLayout = findViewById(R.id.forecastLayout);
        hourlyContainer = findViewById(R.id.hourlyContainer);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_STATE);
        } else {
            // Permission is already granted, proceed with location-related tasks
            // ...
        }

        binding.getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchWeatherData();
                fetchHourlyForecast();
                String api = "c060fc1268bbbad886be26053138bc92";
                String city = binding.etCity.getText().toString();if (city.isEmpty()) {
                    Toast.makeText(MainActivity.this, "City Name cannot be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + api;
                    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        // Main weather data
                                        JSONObject jsonObject = response.getJSONObject("main");
                                        double temp = jsonObject.getDouble("temp");
                                        double temp_in_celcius = temp - 273.15;
                                        temp = Double.parseDouble(String.format("%.2f", temp_in_celcius));
                                        binding.temp.setText(Double.toString(temp) + "°C");

                                        double feels_like = jsonObject.getDouble("feels_like");
                                        double feels_like_in_celcius = feels_like - 273.15;
                                        feels_like = Double.parseDouble(String.format("%.2f", feels_like_in_celcius));
                                        binding.feelsLiketemp.setText(Double.toString(feels_like) + "°C");

                                        double humidity = jsonObject.getDouble("humidity");
                                        binding.hum.setText(Double.toString(humidity) + "%");

                                        double pressure = jsonObject.getDouble("pressure");
                                        binding.pre.setText(Double.toString(pressure) + "hPa");

                                        // Weather condition and description
                                        JSONArray jsonArray = response.getJSONArray("weather");
                                        JSONObject weatherobj = jsonArray.getJSONObject(0);
                                        String cond = weatherobj.getString("main");
                                        String desc = weatherobj.getString("description");
                                        String optimized_desc=Character.toUpperCase(desc.charAt(0))+desc.substring(1);
                                        binding.condition.setText(cond);
                                        binding.description.setText(optimized_desc);

                                        // Wind data
                                        JSONObject jsonObject1 = response.getJSONObject("wind");
                                        double speed = jsonObject1.getDouble("speed");
                                        binding.spe.setText(Double.toString(speed) + "m/s");

                                        // Gust data (check if available)
                                        if (jsonObject1.has("gust")) {
                                            double gust = jsonObject1.getDouble("gust");
                                            binding.gus.setText(Double.toString(gust) + "m/s");
                                        } else {
                                            binding.gus.setText("N/A");
                                        }

                                        // Visibility
                                        int visibility = response.getInt("visibility");
                                        binding.vis.setText(Integer.toString(visibility) + "m");

//                                JSONObject jsonObject2=response.getJSONObject("sys");
//                                String country=jsonObject2.getString("country");
//                                binding.country.setText(country);

                                        switch (cond)
                                        {
                                            case "Rain":
                                                binding.image.setImageResource(R.drawable.rainn);
                                                break;
                                            case "Clear":
                                                binding.image.setImageResource(R.drawable.clear);
                                                break;
                                            case "Clouds":
                                                binding.image.setImageResource(R.drawable.clouds);
                                                break;
                                            case "Haze":
                                                binding.image.setImageResource(R.drawable.fog);
                                                break;
                                            case "Snow":
                                                binding.image.setImageResource(R.drawable.snow);
                                                break;
                                        }
//                                        String city=binding.etCity.getText().toString();
//                                        String forecast_url="https://api.openweathermap.org/data/2.5/forecast?q="+city+"&appid="+api;
//                                        fetchForecast(forecast_url);
                                    } catch (JSONException e) {
                                        Log.e("WeatherActivity", "JSON parsing error: " + e.getMessage());
                                        Toast.makeText(MainActivity.this, "Error fetching weather data", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if(error.networkResponse!=null && error.networkResponse.statusCode==404)
                                    {
                                        Toast.makeText(MainActivity.this, "Sorry,We could not find the city's data!", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Log.e("WeatherActivity", "Error: " + error.getMessage());
                                        Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    queue.add(jsonObjectRequest);
                }
            }
        });

        }

    private void fetchHourlyForecast() {
        String CITY_NAME = binding.etCity.getText().toString(); // Replace with user input if needed
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + CITY_NAME + "&appid=" + API_KEY_HOURLY + "&units=metric";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        populateHourlyForecast(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("WeatherApp", "Failed to fetch data: " + error.getMessage());
                Toast.makeText(MainActivity.this, "Error fetching weather data", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsonObjectRequest);
    }

    private void populateHourlyForecast(JSONObject response) {
        try {
            JSONArray listArray = response.getJSONArray("list");

            for (int i = 0; i < 96; i++) { // 96 hours for 4 days
                JSONObject hourlyData = listArray.getJSONObject(i);
                long timestamp = hourlyData.getLong("dt");
                JSONObject main = hourlyData.getJSONObject("main");
                double temp = main.getDouble("temp");
                String tempText = Math.round(temp) + "°C";

                JSONArray weatherArray = hourlyData.getJSONArray("weather");
                JSONObject weather = weatherArray.getJSONObject(0);
                String description = weather.getString("description");
                String iconCode = weather.getString("icon");

                JSONObject wind = hourlyData.getJSONObject("wind");
                double windSpeed = wind.getDouble("speed");
                String windText = windSpeed + " m/s";

                String dateTime = formatTimestamp(timestamp);

                // Inflate and add view to the HorizontalScrollView container
                View itemView = LayoutInflater.from(this).inflate(R.layout.item_hourly_forecast, hourlyContainer, false);

                TextView timeTextView = itemView.findViewById(R.id.textTime);
                TextView tempTextView = itemView.findViewById(R.id.textTemperature);
                TextView descTextView = itemView.findViewById(R.id.textDescription);
                TextView windTextView = itemView.findViewById(R.id.textWind);
                ImageView weatherIcon = itemView.findViewById(R.id.imageWeatherIcon);

                timeTextView.setText(dateTime);
                tempTextView.setText(tempText);
                descTextView.setText(description);
                windTextView.setText(windText);

                int iconResId = getResources().getIdentifier("ic_weather_" + iconCode, "drawable", getPackageName());
                if (iconResId != 0) {
                    weatherIcon.setImageResource(iconResId);
                }

                hourlyContainer.addView(itemView);
            }

        } catch (JSONException e) {
            Log.e("WeatherApp", "JSON Parsing Error: " + e.getMessage());
        }
    }
    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, hh a", Locale.getDefault());
        return sdf.format(new Date(timestamp * 1000));
    }

    private void fetchWeatherData() {
        String CITY_NAME=binding.etCity.getText().toString();
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + CITY_NAME + "&appid=" + API_KEY + "&units=metric";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseWeatherData(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("WeatherApp", "Failed to fetch data: " + error.getMessage());
            }
        });

        queue.add(jsonObjectRequest);
    }
    private void parseWeatherData(JSONObject response) {
        try {
            JSONArray listArray = response.getJSONArray("list");
            forecastLayout.removeAllViews();

            // Loop through the list and extract data for each day
            for (int i = 0; i < listArray.length(); i += 8) { // Every 8th entry represents a new day
                JSONObject dailyData = listArray.getJSONObject(i);
                long timestamp = dailyData.getLong("dt");
                JSONObject main = dailyData.getJSONObject("main");
                double temp = main.getDouble("temp");

                JSONArray weatherArray = dailyData.getJSONArray("weather");
                JSONObject weather = weatherArray.getJSONObject(0);
                String description = weather.getString("description");
                String iconCode = weather.getString("icon");

                // Convert timestamp to readable date
                String date = formatTimestampToDate(timestamp);

                // Inflate the custom layout for each forecast day
                View forecastItem = LayoutInflater.from(this).inflate(R.layout.item_forecast, forecastLayout, false);

                TextView dateText = forecastItem.findViewById(R.id.forecastDate);
                TextView tempText = forecastItem.findViewById(R.id.forecastTemp);
                TextView descText = forecastItem.findViewById(R.id.forecastDesc);
                ImageView weatherIcon = forecastItem.findViewById(R.id.weatherIcon);

                // Set values
                dateText.setText(date);
                tempText.setText(temp + "°C");
                descText.setText(description);
                weatherIcon.setImageResource(getWeatherIcon(iconCode));

                // Add view to horizontal layout
                forecastLayout.addView(forecastItem);
            }

        } catch (JSONException e) {
            Log.e("WeatherApp", "JSON Parsing Error: " + e.getMessage());
        }
    }

    private String formatTimestampToDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
        return sdf.format(new Date(timestamp * 1000));
    }

    private int getWeatherIcon(String iconCode) {
        switch (iconCode) {
            case "01d": return R.drawable.clear;
            case "01n": return R.drawable.clearnight;
            case "02d": return R.drawable.clouds;
            case "02n": return R.drawable.clodynight;
            case "03d": case "03n": return R.drawable.clouds;
            case "04d": case "04n": return R.drawable.overcast;
            case "09d": case "09n": return R.drawable.rainy;
            case "10d": case "10n": return R.drawable.heavyrain;
            case "11d": case "11n": return R.drawable.thunderstorm;
            case "13d": case "13n": return R.drawable.snow;
            case "50d": case "50n": return R.drawable.fog;
            default: return R.drawable.clear;
        }
    }
}
