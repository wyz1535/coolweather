package com.leyifu.coolweather.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xingxing on 2017/7/25.
 */
public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;
    }
}
