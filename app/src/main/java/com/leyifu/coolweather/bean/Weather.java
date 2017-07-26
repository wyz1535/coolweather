package com.leyifu.coolweather.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by xingxing on 2017/7/25.
 */
public class Weather {

    public String status;

    public AQI aqi;

    public  Basic basic;

    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
