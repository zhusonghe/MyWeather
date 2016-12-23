package com.example.administrator.mini_weather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.zhusonghe.bean.TodayWeather;
import cn.edu.pku.zhusonghe.util.NetUtil;

/** * Created by zhusonghe on 16/10/11. */
public class MainActivity extends Activity implements ViewPager.OnPageChangeListener,View.OnClickListener {
    private  static  final  int UPDATE_TODAY_WEATHER = 1;

    private ImageView mUpdateBtn;
    private ProgressBar mProgressBtn;

    private ImageView CitySelect;

    private ViewPagerAdapter vpadpter;
    private ViewPager vp;
    private List<View> views;
    private ImageView[] dos;

    private int[] ids={R.id.iv1,R.id.iv2};


    private TextView wendu,cityTv,timeTv,humidityTV,weekTv,pmDataTv,pmQualityTV,temperatureTv,
             climateTv,windTv,city_name_Tv,
            temperatureTv_1,weekTv_1,climateTv_1,windTv_1,
            temperatureTv_2,weekTv_2,climateTv_2,windTv_2,
            temperatureTv_3,weekTv_3,climateTv_3,windTv_3;
    private ImageView weatherImg,pmImg,weatherImg_1,weatherImg_2,weatherImg_3,weatherImg_4;

    private Handler mHandler = new Handler(){
        public  void  handleMessage(android.os.Message msg){
            switch (msg.what){
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj );
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mProgressBtn = (ProgressBar)findViewById(R.id.title_update_progress);
        mUpdateBtn.setOnClickListener(this);
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络OK！");
            Toast.makeText(MainActivity.this, "网络OK！", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myWeather", "网络挂了！");
            Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
        }
          CitySelect = (ImageView) findViewById(R.id.title_city_manager);
          CitySelect.setOnClickListener(this);


        initView();
        iniDos();
    }


    /**
     * @param cityCode
     */
    private void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
       // Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try {
                    URL url = new URL(address);
                    con = (HttpURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        response.append(str);
                       Log.d("myWeather",str);
                    }
                    String responseStr = response.toString();
                    //Log.d("myWeather",responseStr);
                    todayWeather = parseXML(responseStr);

                    if (todayWeather != null){
                       //Log.d("myWeather",todayWeather.toString());


                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        }).start();
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.title_city_manager){
         Intent i= new Intent(this,SelectCity.class);
         startActivityForResult(i,1);
        }


        if(view.getId() == R.id.title_update_btn) {
            mUpdateBtn.setVisibility(view.INVISIBLE);
            mProgressBtn.setVisibility(view.VISIBLE);
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            Log.d("myWeather", cityCode);
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(cityCode);
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }


    }

    protected  void  onActivityResult(int requestCode, int resultCode ,Intent data){
        if (requestCode==1&&resultCode == RESULT_OK){
            String newCityCode = data.getStringExtra("cityCode");
            Log.d("myWeather","选择的城市代码为:"+newCityCode);

            if (NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE){
                Log.d("myWeather","网络OK");
                queryWeatherCode(newCityCode);
            }else {
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了！",Toast.LENGTH_LONG).show();
            }
        }


    }



    private TodayWeather parseXML(String xmldata) {
        TodayWeather todayWeather = null;
        int fengxiangCount=0;
        int fengliCount =0;
        int dateCount=0;
        int highCount =0;
        int lowCount=0;
        int typeCount=0;
        int dayCount=0;
        int isDay=0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    // 判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    // 判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("resp" )){
                            todayWeather= new TodayWeather();
                        }
                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next() ;
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next() ;
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next() ;
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next() ;
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next() ;
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next() ;
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next() ;
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next() ;
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next() ;
                                todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("day")&&dayCount==0){
                                eventType = xmlPullParser.next() ;
                                dayCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0&&dayCount==1) {
                                eventType = xmlPullParser.next() ;
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            }else if (xmlPullParser.getName().equals("fengli") && fengliCount == 1) {
                                eventType = xmlPullParser.next() ;
                                todayWeather.setFengli_1(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 1) {
                                eventType = xmlPullParser.next() ;
                                todayWeather.setDate_1(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh_1(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 1) {
                                eventType = xmlPullParser.next() ;
                                todayWeather.setLow_1(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("day")&&dayCount==1){
                                eventType = xmlPullParser.next() ;
                                dayCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 1&&dayCount==2) {
                                eventType = xmlPullParser.next() ;
                                todayWeather.setType_1(xmlPullParser.getText());
                                Log.d("haha",todayWeather.getType_1());
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 2) {
                                eventType = xmlPullParser.next() ;
                                todayWeather.setFengli_2(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 2) {
                                eventType = xmlPullParser.next() ;
                                todayWeather.setDate_2(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh_2(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 2) {
                                eventType = xmlPullParser.next() ;
                                todayWeather.setLow_2(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("day")&&dayCount==2){
                                eventType = xmlPullParser.next() ;
                                dayCount++;
                            }else if (xmlPullParser.getName().equals("type") && typeCount == 2&&dayCount==3) {
                                eventType = xmlPullParser.next() ;
                                todayWeather.setType_2(xmlPullParser.getText());
                                Log.d("haha",todayWeather.getType_2());
                                typeCount++;
                            }else if (xmlPullParser.getName().equals("fengli") && fengliCount == 3) {
                                eventType = xmlPullParser.next() ;
                                todayWeather.setFengli_3(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 3) {
                                eventType = xmlPullParser.next() ;
                                todayWeather.setDate_3(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh_3(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 3) {
                                eventType = xmlPullParser.next() ;
                                todayWeather.setLow_3(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("day")&&dayCount==3){
                                eventType = xmlPullParser.next() ;
                                dayCount++;
                            }else if (xmlPullParser.getName().equals("type") && typeCount == 3&&dayCount==4) {
                                eventType = xmlPullParser.next() ;
                                todayWeather.setType_3(xmlPullParser.getText());
                                Log.d("haha",todayWeather.getType_3());
                                typeCount++;
                            }
                        }


                        break;
                    // 判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
                    // 进入下一个元素并触发相应事件
                     eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;


    }

    void iniDos(){
        dos = new ImageView[views.size()];
        for(int i=0;i<views.size();i++){
            dos[i]=(ImageView)findViewById(ids[i]);
        }
    }

    void  initView(){
        wendu = (TextView)findViewById(R.id.Currenttemper);
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTV = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView)  findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTV = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);


        LayoutInflater inflater = LayoutInflater.from(this);
        views =new ArrayList<View>();
        views.add(inflater.inflate(R.layout.nextday1,null));
        views.add(inflater.inflate(R.layout.nextday2,null));
        vpadpter = new ViewPagerAdapter(views,this);
        vp = (ViewPager) findViewById(R.id.viewpager2);
        vp.setAdapter(vpadpter);
        vp.setOnPageChangeListener(this);

        //明日天气信息
        weekTv_1=(TextView)findViewById(R.id.tomorrow_date);
        weatherImg_1 = (ImageView) findViewById(R.id.tomorrow_img);
        climateTv_1=(TextView)findViewById(R.id.tomorrow_climate);
        temperatureTv_1=(TextView)findViewById(R.id.tomorrow_wendu);
        windTv_1=(TextView)findViewById(R.id.tomorrow_fengli);

        //后天天气信息
        weekTv_2=(TextView)findViewById(R.id.houtian_date);
        weatherImg_2 = (ImageView) findViewById(R.id.houtian_img);
        climateTv_2=(TextView)findViewById(R.id.houtian_climate);
        temperatureTv_2=(TextView)findViewById(R.id.houtian_wendu);
        windTv_2=(TextView)findViewById(R.id.houtian_fengli);

        //大后天天气信息
        weekTv_3=(TextView)findViewById(R.id.dahoutian_date);
        weatherImg_3 = (ImageView) findViewById(R.id.dahoutian_img);
        climateTv_3=(TextView)findViewById(R.id.dahoutian_climate);
        temperatureTv_3=(TextView)findViewById(R.id.dahoutian_wendu);
        windTv_3=(TextView)findViewById(R.id.dahoutian_fengli);


        wendu.setText("N/A");
        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTV.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTV.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");


    }

    void updateTodayWeather(TodayWeather todayWeather){
        initView();
        wendu.setText(todayWeather.getWendu()+"℃");
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+"发布");
        humidityTV.setText("湿度："+todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTV.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getLow()+"~"+todayWeather.getHigh());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力："+todayWeather.getFengli());
        chooseType(todayWeather.getType(),weatherImg);

        //明日天气组件
        weekTv_1.setText(todayWeather.getDate_1());
        chooseType(todayWeather.getType_1(),weatherImg_1);
        temperatureTv_1.setText(todayWeather.getLow_1()+"~"+todayWeather.getHigh_1());
        climateTv_1.setText(todayWeather.getType_1());
        windTv_1.setText(todayWeather.getFengli_1());

        //后天天气组件
        weekTv_2.setText(todayWeather.getDate_2());
        chooseType(todayWeather.getType_2(),weatherImg_2);
        temperatureTv_2.setText(todayWeather.getLow_2()+"~"+todayWeather.getHigh_2());
        climateTv_2.setText(todayWeather.getType_2());
        windTv_2.setText(todayWeather.getFengli_2());

        //大后天天气组件
        weekTv_3.setText(todayWeather.getDate_3());
        chooseType(todayWeather.getType_3(),weatherImg_3);
        temperatureTv_3.setText(todayWeather.getLow_3()+"~"+todayWeather.getHigh_3());
        climateTv_3.setText(todayWeather.getType_3());
        windTv_3.setText(todayWeather.getFengli_3());


        String pm = todayWeather.getPm25();
        int pmIntValue=0;
        if(pm != null) {
            pmIntValue = Integer.parseInt(pm);
        }
        if(pmIntValue>300){
            pmImg.setImageResource(R.drawable.biz_plugin_weather_greater_300);
        }else if(pmIntValue>200){
            pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
        }else if(pmIntValue>150){
            pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
        }else if(pmIntValue>100){
            pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
        }else if(pmIntValue>50){
            pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
        }else if(pmIntValue>0){
            pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);

        }
        mUpdateBtn.setVisibility(View.VISIBLE);
        mProgressBtn.setVisibility(View.INVISIBLE);

        //Toast.makeText(MainActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
    }

    //选择天气图片
    void chooseType(String s,ImageView weatherImg){
        switch(s){
            case "晴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "暴雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case "暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "大雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case "多云":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雷阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "沙尘暴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "特大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "雾":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "小雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "小雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "阴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "雨夹雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "阵雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            case "中雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "中雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int i) {
        for(int j=0;j<ids.length;j++){
            if(j==i){
                dos[j].setImageResource(R.drawable.page_indicator_focused);
            }else {
                dos[j].setImageResource(R.drawable.page_indicator_unfocused);
            }
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}




