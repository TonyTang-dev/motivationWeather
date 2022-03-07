package com.weathertang.android.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.weathertang.android.db.City;
import com.weathertang.android.db.County;
import com.weathertang.android.db.Province;
import com.weathertang.android.gson.AQI;
import com.weathertang.android.gson.Now;
import com.weathertang.android.gson.Suggestion;
import com.weathertang.android.gson.Weather;
import com.weathertang.android.gson.WeatherBasic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    /*
    解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allProvinces=new JSONArray(response);
                for(int i=0;i<allProvinces.length();i++){
                    JSONObject provinceObject=allProvinces.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            }
            catch(JSONException e){
                //暂时找不到处理方法
            }
        }
        return false;
    }

    /*
    解析和处理服务器返回的城市级数据
     */
    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCities=new JSONArray(response);
                for(int i=0;i<allCities.length();i++){
                    JSONObject cityObject=allCities.getJSONObject(i);
                    City city=new City();
                    city.setcityName(cityObject.getString("name"));
                    city.setcityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }
            catch(JSONException e){
                //暂时找不到处理方法
            }
        }
        return false;
    }

    /*
    解析和处理服务器返回的县级数据
     */
    public static boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCounties=new JSONArray(response);
                for(int i=0;i<allCounties.length();i++){
                    JSONObject countyObject=allCounties.getJSONObject(i);
                    County county=new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            }
            catch(JSONException e){
                //暂时找不到处理方法
            }
        }
        return false;
    }

    /*
    将返回的JSOn数据解析成Weather实体类
     */
    public static WeatherBasic handleWeatherResponse1(String response){
        try{
            JSONObject jsonobject=new JSONObject(response);
            JSONArray jsonArray=jsonobject.getJSONArray("HeWeather");
            String weatherContent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, WeatherBasic.class);
        }
        catch(Exception e){
            //暂时没有处理方法
        }
        return null;
    }
    public static Suggestion handleWeatherResponse5(String response){
        try{
            JSONObject jsonobject=new JSONObject(response);
//            JSONArray jsonArray=jsonobject.getJSONArray("daily");
//            String weatherContent=jsonArray.getJSONObject(0).toString();
            String weatherContent=jsonobject.toString();
            return new Gson().fromJson(weatherContent, Suggestion.class);
        }
        catch(Exception e){
            //暂时没有处理方法
        }
        return null;
    }
    public static Weather handleWeatherResponse2(String response){
        try{
            JSONObject jsonobject=new JSONObject(response);
//            JSONArray jsonArray=jsonobject.getJSONArray("HeWeather");
//            String weatherContent=jsonArray.getJSONObject(0).toString();
            String weatherContent=jsonobject.toString();
            return new Gson().fromJson(weatherContent, Weather.class);
        }
        catch(Exception e){
            //暂时没有处理方法
        }
        return null;
    }
    public static Now handleWeatherResponse3(String response){
        try{
            JSONObject jsonobject=new JSONObject(response);
//            JSONArray jsonArray=jsonobject.getJSONArray("HeWeather");
//            String weatherContent=jsonArray.getJSONObject(0).toString();
            String weatherContent=jsonobject.toString();
            return new Gson().fromJson(weatherContent, Now.class);
        }
        catch(Exception e){
            //暂时没有处理方法
        }
        return null;
    }
    public static AQI handleWeatherResponse4(String response){
        try{
            JSONObject jsonobject=new JSONObject(response);
//            JSONArray jsonArray=jsonobject.getJSONArray("HeWeather");
//            String weatherContent=jsonArray.getJSONObject(0).toString();
            String weatherContent=jsonobject.toString();
            return new Gson().fromJson(weatherContent, AQI.class);
        }
        catch(Exception e){
            //暂时没有处理方法
        }
        return null;
    }
}
