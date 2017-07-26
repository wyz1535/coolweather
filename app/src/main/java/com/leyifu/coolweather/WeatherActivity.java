package com.leyifu.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.leyifu.coolweather.bean.Forecast;
import com.leyifu.coolweather.bean.Weather;
import com.leyifu.coolweather.constant.Constant;
import com.leyifu.coolweather.util.HttpUtil;
import com.leyifu.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private static final String TAG = WeatherActivity.class.getSimpleName();
    private ScrollView weather_layout;
    private TextView title_city;
    private TextView title_update_time;
    private TextView degree_text;
    private TextView weather_info_text;
    private LinearLayout forecast_layout;
    private TextView aqi_text;
    private TextView pm25_text;
    private TextView comfort_text;
    private TextView car_wash_text;
    private TextView sport_text;
    private TextView date_text;
    private TextView info_text;
    private TextView max_text;
    private TextView min_text;
    private ImageView bing_pic_img;
    public SwipeRefreshLayout swipe_refresh;
    private String mWeather;
    public DrawerLayout drawer_layout;
    private Button nav_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        initView();
        init();
    }

    private void initView() {
        weather_layout = ((ScrollView) findViewById(R.id.weather_layout));
        title_city = ((TextView) findViewById(R.id.title_city));
        title_update_time = ((TextView) findViewById(R.id.title_update_time));
        degree_text = ((TextView) findViewById(R.id.degree_text));
        weather_info_text = ((TextView) findViewById(R.id.weather_info_text));
        forecast_layout = ((LinearLayout) findViewById(R.id.forecast_layout));
        aqi_text = ((TextView) findViewById(R.id.aqi_text));
        pm25_text = ((TextView) findViewById(R.id.pm25_text));
        comfort_text = ((TextView) findViewById(R.id.comfort_text));
        car_wash_text = ((TextView) findViewById(R.id.car_wash_text));
        sport_text = ((TextView) findViewById(R.id.sport_text));
        bing_pic_img = ((ImageView) findViewById(R.id.bing_pic_img));
        swipe_refresh = ((SwipeRefreshLayout) findViewById(R.id.swipe_refresh));
        nav_button = ((Button) findViewById(R.id.nav_button));
        drawer_layout = ((DrawerLayout) findViewById(R.id.drawer_layout));
//        choose_area_fragment = ((ChooseAreaFragment) findViewById(R.id.choose_area_fragment));

    }

    private void init() {
        swipe_refresh.setColorSchemeResources(R.color.colorPrimary);
        nav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.openDrawer(GravityCompat.START);
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            //有缓存是直接解析天气数据
            Weather weather = Utility.handleWeahterResponse(weatherString);
            mWeather = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            mWeather=getIntent().getStringExtra("weather_id");
            String weatherId = getIntent().getStringExtra("weather_id");
            weather_layout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeather);
            }
        });

        String bing_pic = prefs.getString("bing_pic", null);
        if (bing_pic != null) {
            Glide.with(this).load(bing_pic).into(bing_pic_img);
        } else {
            loadBingPic();
        }
    }

    private void loadBingPic() {
        HttpUtil.sendOkHttpRequst(Constant.BING_PIC_URL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bing_pic_img);
                    }
                });
            }
        });
    }

    //根据天气id请求城市天气信息
    public void requestWeather(final String weatherId) {
        String weatherUrl = Constant.URL + "weather?cityid=" + weatherId + "&key=" + Constant.KEY;
        Log.e(TAG, "weatherUrl: " + weatherUrl);
        HttpUtil.sendOkHttpRequst(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气失败02", Toast.LENGTH_SHORT).show();
                        swipe_refresh.setRefreshing(false);
                    }
                });
                loadBingPic();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeahterResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
                        }
                        swipe_refresh.setRefreshing(false);
                    }
                });
            }
        });
    }

    //处理并展示weahter实体类的数据
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        title_city.setText(cityName);
        title_update_time.setText(updateTime);
        degree_text.setText(degree);
        weather_info_text.setText(weatherInfo);
        forecast_layout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecast_layout, false);
            date_text = ((TextView) view.findViewById(R.id.date_text));
            info_text = ((TextView) view.findViewById(R.id.info_text));
            max_text = ((TextView) view.findViewById(R.id.max_text));
            min_text = ((TextView) view.findViewById(R.id.min_text));

            date_text.setText(forecast.date);
            info_text.setText(forecast.more.info);
            max_text.setText(forecast.temperature.max);
            min_text.setText(forecast.temperature.max);
            forecast_layout.addView(view);
        }

        if (weather.aqi != null) {
            aqi_text.setText(weather.aqi.city.aqi);
            pm25_text.setText(weather.aqi.city.pm25);
        }

        comfort_text.setText("舒适度:" + weather.suggestion.comfort.info);
        car_wash_text.setText("洗车指数:" + weather.suggestion.carWash.info);
        sport_text.setText("运动建议:" + weather.suggestion.sport.info);
        weather_layout.setVisibility(View.VISIBLE);
    }
}
