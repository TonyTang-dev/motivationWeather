package com.weathertang.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Suggestion {
    public String code;

    @SerializedName("daily")
    public List<suggestion_item> suggestList;
//    public String name;
}
