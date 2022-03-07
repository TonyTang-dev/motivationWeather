package com.weathertang.android;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.weathertang.android.gson.Weather;
import com.weathertang.android.gson.WeatherBasic;
import com.weathertang.android.util.HttpUtil;
import com.weathertang.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public int onStartCommand(Intent intent,int flags,int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int anhour=12*60*60*1000;
        long triggerAtTime= SystemClock.elapsedRealtime()+anhour;
        Intent i=new Intent(this,AutoUpdateService.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
    更新天气信息
     */
    private void updateWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=prefs.getString("weather",null);
        if(weatherString!=null){
            //有缓存时直接解析天气数据获取uID
            WeatherBasic weatherBasic= Utility.handleWeatherResponse1(weatherString);
            String weatherId=weatherBasic.basic.weatherId;

            //String weatherUrl="https://devapi.qweather.com/v7/weather/now?location="+weatherId+"&key=e2ce7cc9853740cabccf4b5c823b133d";
            //String weatherUrl="https://devapi.qweather.com/v7/weather/3d?location=CN101010100&key=e2ce7cc9853740cabccf4b5c823b133d";
            String weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+"&key=e2ce7cc9853740cabccf4b5c823b133d";
            String weatherUrl2="https://devapi.qweather.com/v7/weather/3d?location="+weatherId+"&key=e2ce7cc9853740cabccf4b5c823b133d";
//        String weatherUrl="https://devapi.qweather.com/v7/weather/now?location="+weatherId+"&key=e2ce7cc9853740cabccf4b5c823b133d";
            sendHttprequest(weatherUrl,1);
            sendHttprequest(weatherUrl2,2);
        }
    }
    private void sendHttprequest(String weatherUrl,final int flag){
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtil.showToast(AutoUpdateService.this, "获取天气信息失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                WeatherBasic weatherbasic = null;
                Weather weather = null;
                if (flag == 1) {
                    weatherbasic = Utility.handleWeatherResponse1(responseText);
                } else if (flag == 2) {
                    weather = Utility.handleWeatherResponse2(responseText);
                }
                if (weatherbasic != null && "ok".equals(weatherbasic.status) && flag == 1) {
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                    editor.putString("weatherBasic", weatherbasic.basic.weatherId);
                    editor.putString("updateTime", weatherbasic.basic.update.updateTime.split(" ")[1]);
                    editor.apply();
                } else if (flag == 2) {
                    if (weather != null && "200".equals(weather.code) && flag == 2) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                    }
                } else {
                    ToastUtil.showToast(AutoUpdateService.this, "获取天气信息失败");
                }
            }
        });
    }

    /*
    获取每日一图
     */
    private void updateBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //暂时没有处理方法
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
            }
        });
    }
}
