package com.weathertang.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    public String code;
//    public Basic basic;
//    public AQI aqi;
//    public Now now;
//    public Suggestion suggestion;

    @SerializedName("daily")
    public List<Forecast> forecastList;
}
