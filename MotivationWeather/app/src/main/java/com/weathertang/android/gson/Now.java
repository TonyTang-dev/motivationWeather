package com.weathertang.android.gson;

import com.google.gson.annotations.SerializedName;

public class Now {
    @SerializedName("code")
    public String code;
    @SerializedName("now")
    public now1 now1;

    public class now1{
        public String text;
        public String temp;
    }

//    @SerializedName("temp")
//    public String temperature;
//    @SerializedName("text")
//    public String text;
}
