package com.weathertang.android.gson;

import com.google.gson.annotations.SerializedName;

public class AQI {
    public String code;
    @SerializedName("now")
    public AQICity city;

    public class AQICity{
        public String aqi;

        public String pm2p5;
//        public String precip;
//
//        public String uvIndex;
    }
}
