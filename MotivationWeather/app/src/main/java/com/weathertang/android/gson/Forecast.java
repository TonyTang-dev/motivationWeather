package com.weathertang.android.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {
    public String fxDate;

    @SerializedName("tempMax")
    public String max;
    @SerializedName("tempMin")
    public String min;
//    public Temperature temperature;

    @SerializedName("textDay")
    public String info;
//    public More more;

//    public class Temperature{
//        public String max;
//
//        public String min;
//    }

//    public class More{
//        @SerializedName("txt_d")
//        public String info;
//    }
}
