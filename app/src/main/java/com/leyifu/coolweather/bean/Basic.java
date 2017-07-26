package com.leyifu.coolweather.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xingxing on 2017/7/25.
 */
public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;

    }
}
