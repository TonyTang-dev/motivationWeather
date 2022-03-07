package com.weathertang.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.weathertang.android.gson.AQI;
import com.weathertang.android.gson.Forecast;
import com.weathertang.android.gson.Now;
import com.weathertang.android.gson.Suggestion;
import com.weathertang.android.gson.Weather;
import com.weathertang.android.gson.WeatherBasic;
import com.weathertang.android.util.HttpUtil;
import com.weathertang.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

    private LinearLayout forecastLayout;
    private LinearLayout today_layout;

    private ImageView bingPicImg;

    //下拉刷新
    public SwipeRefreshLayout swipeRefresh;
    private String mWeatherId;

    //选择城市
    public DrawerLayout drawerLayout;
    private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏融合
        if(Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                   View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);

        //初始化图片
        bingPicImg=(ImageView)findViewById(R.id.bing_pic_img);

        //初始化控件
        weatherLayout=(ScrollView)findViewById(R.id.weather_layout);
        titleCity=(TextView)findViewById(R.id.title_city);
        titleUpdateTime=(TextView)findViewById(R.id.title_update_time);
        degreeText=(TextView)findViewById(R.id.degree_text);
        weatherInfoText=(TextView)findViewById(R.id.weather_info_text);
        aqiText=(TextView)findViewById(R.id.aqi_text);
        pm25Text=(TextView)findViewById(R.id.pm25_text);
        carWashText=(TextView)findViewById(R.id.car_wash_text);
        sportText=(TextView)findViewById(R.id.sport_text);
        forecastLayout=(LinearLayout)findViewById(R.id.forecast_layout);
        today_layout=(LinearLayout)findViewById(R.id.today_layout);

        //下拉刷新
        swipeRefresh=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        //选择城市
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        navButton=(Button)findViewById(R.id.nav_button);
        navButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);

        String weatherString=prefs.getString("weatherBasic",null);
        if(weatherString!=null){
            //有缓存时直接解析天气数据
            //启动时请求信息
            requestWeather(weatherString);//保证用户每次打开软件都是最新信息
//            showWeatherInfo(weather);
        }
        else{
            //无缓存时去服务器查询
            mWeatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });

        //图片
        String bingPic=prefs.getString("bing_pic",null);
        if(bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }
        else{
            loadBingPic();
        }
    }

    /*
    根据天气Id请求天气数据
     */
    public void requestWeather(final String weatherId){
		String yourKey="";
		
        String weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+yourKey;
        String weatherUrl2="https://devapi.qweather.com/v7/weather/3d?location="+weatherId+yourKey;
        String weatherUrl3="https://devapi.qweather.com/v7/weather/now?location="+weatherId+yourKey;
        String weatherUrl4="https://devapi.qweather.com/v7/air/now?location="+weatherId+yourKey;
        String weatherUrl5="https://devapi.qweather.com/v7/indices/1d?type=1,2&location="+weatherId+yourKey;
        sendHttprequest(weatherUrl,1);
        sendHttprequest(weatherUrl5,5);
        sendHttprequest(weatherUrl4,4);
        sendHttprequest(weatherUrl3,3);
        sendHttprequest(weatherUrl2,2);
        loadBingPic();//请求图片
    }
    /*
    请求数据
     */
    private void sendHttprequest(String weatherUrl,final int flag){
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        ToastUtil.showToast(WeatherActivity.this,"获取天气信息失败");
                        swipeRefresh.setRefreshing(false);//隐藏进度条
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
//                Log.d("WeatherActivity", "onResponse: "+responseText);

                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        WeatherBasic weatherbasic=null;
                        Weather weather=null;
                        Now now=null;
                        AQI aqi1=null;
                        Suggestion suggestion=null;
                        if(flag==1){
                            weatherbasic=Utility.handleWeatherResponse1(responseText);
                        }
                        else if(flag==2){
                            weather=Utility.handleWeatherResponse2(responseText);
                        }
                        else if(flag==3){
                            now=Utility.handleWeatherResponse3(responseText);
                        }
                        else if(flag==4){
                            aqi1=Utility.handleWeatherResponse4(responseText);
                        }
                        else if(flag==5){
                            suggestion=Utility.handleWeatherResponse5(responseText);
                        }
                        if(weatherbasic!=null&&"ok".equals(weatherbasic.status)&&flag==1){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weatherBasic",weatherbasic.basic.weatherId);
                            editor.putString("updateTime",weatherbasic.basic.update.updateTime.split(" ")[1]);
                            editor.apply();
                            mWeatherId=weatherbasic.basic.weatherId;//便于下次刷新，这里的id已经变化
                            showWeatherInfo1(weatherbasic);
                        }
                        else if(flag==2){
                            if(weather!=null&&"200".equals(weather.code)&&flag==2){
                                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                                editor.putString("weather",responseText);
                                editor.apply();
                            showWeatherInfo2(weather);
                            }
                        }
                        else if(flag==3){
                            if(now!=null&&"200".equals(now.code)&&flag==3){
                                showWeatherInfo3(now);
                            }
                        }
                        else if(flag==4){
                            if(aqi1!=null&&"200".equals(aqi1.code)&&flag==4){
                                showWeatherInfo4(aqi1);
                            }
                        }
                        else if(flag==5){
                            if(suggestion!=null&&"200".equals(suggestion.code)&&flag==5){
                                showWeatherInfo5(suggestion);
                            }
                        }
                        else{
                            ToastUtil.showToast(WeatherActivity.this,"获取天气信息失败");
                        }
                        swipeRefresh.setRefreshing(false);//隐藏进度条
                    }
                });
            }
        });
    }
    /*
    加载每日一图
     */
    private void loadBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtil.showToast(WeatherActivity.this,"加载背景图失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();

                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    /*
    处理并展示weather类中的数据
     */
    private void showWeatherInfo1(WeatherBasic weatherBasic){
        String cityName=weatherBasic.basic.cityName;
        String updatetime[]=weatherBasic.basic.update.updateTime.split(" ");
        String updateTime=updatetime[1];
        titleCity.setText(cityName);
        titleUpdateTime.setText("刷新时间："+updateTime);
        weatherLayout.setVisibility(View.VISIBLE);
    }
    private void showWeatherInfo3(Now now){
        View view1= LayoutInflater.from(this).inflate(R.layout.today_item,today_layout,false);
        today_layout.removeAllViews();
        TextView dateText1=(TextView)view1.findViewById(R.id.today_date);
        TextView infoText1=(TextView)view1.findViewById(R.id.today_condition);
        TextView degreeText1=(TextView)view1.findViewById(R.id.today_degree);
        dateText1.setText("今天");

        String degree=now.now1.temp+"℃";
        String weatherInfo=now.now1.text;

        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        String info=now.now1.text;
        infoText1.setText(info);
        degreeText1.setText(now.now1.temp);
        today_layout.addView(view1);
    }
    private void showWeatherInfo4(AQI aqi2){
        if(aqi2!=null) {
            aqiText.setText(aqi2.city.aqi);
            pm25Text.setText(aqi2.city.pm2p5);
        }
    }
    private void showWeatherInfo2(Weather weather1){
        forecastLayout.removeAllViews();
        //今天

        for(Forecast forecast:weather1.forecastList){
            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText=(TextView)view.findViewById(R.id.date_text);
            TextView infoText=(TextView)view.findViewById(R.id.info_text);
            TextView maxText=(TextView)view.findViewById(R.id.max_text);
            TextView minText=(TextView)view.findViewById(R.id.min_text);
            dateText.setText(forecast.fxDate);
            infoText.setText(forecast.info);
            maxText.setText(forecast.max);
            minText.setText(forecast.min);

            forecastLayout.addView(view);
        }
        weatherLayout.setVisibility(View.VISIBLE);

//        Intent intent=new Intent(this,AutoUpdateService.class);
//        startService(intent);//启动服务       由于设置每次打开都自动刷新天气信息，暂时使用不到服务
    }
    private void showWeatherInfo5(Suggestion suggestion){
//        String comfort="舒适度："+suggestion.suggestList.get(0).name;
        String carWash="洗车指数："+suggestion.suggestList.get(0).category+"\n"+suggestion.suggestList.get(0).text;
        String sport="运动建议："+suggestion.suggestList.get(1).category+"\n"+suggestion.suggestList.get(1).text;

//        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
//        weatherLayout.setVisibility(View.VISIBLE);

//        Intent intent=new Intent(this,AutoUpdateService.class);
//        startService(intent);//启动服务       由于设置每次打开都自动刷新天气信息，暂时使用不到服务
    }
}