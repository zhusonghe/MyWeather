package com.example.administrator.mini_weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.edu.pku.zhusonghe.app.MyApplication;
import cn.edu.pku.zhusonghe.bean.City;

/**
 * Created by Administrator on 2016/10/21.
 */
public class SelectCity extends Activity implements  View.OnClickListener{
       private  ListView  mList;

    private  Myadapter myadapter;

       private List<City> cityList;

    private List<City> filterDateList = new ArrayList<City>();


    private ImageView mBackBtn;
    private List<City> mCityList;// 城市列表项

    /***搜索框***/
    private ListView sortListView;

    private ClearEditText mClearEditText;

    @Override
    protected   void  onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_city);
        initViews();

    }

    private void initViews() {
        //为mBackBtn设置监听事件
        mBackBtn = (ImageView) findViewById(R.id.title_backoff);
        mBackBtn.setOnClickListener(this);

        mClearEditText =  (ClearEditText) findViewById(R.id.search_city);


        mList=(ListView) findViewById(R.id.title_list);
        MyApplication myApplication = (MyApplication) getApplication();
        cityList = myApplication.getCityList();
        for(City city : cityList) {
            filterDateList.add(city);
        }
        myadapter = new Myadapter(SelectCity.this,cityList);
        mList.setAdapter(myadapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                City city = filterDateList.get(position);
                Intent i = new Intent();
                i.putExtra("cityCode",city.getNumber());
                setResult(RESULT_OK,i);
                finish();
            }
        });

//        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                //这里要利用adapter.getItem(position)来获取当前position所对应的对象
//                Toast.makeText(getApplication(), ((City)myadapter.getItem(position)).getCity(), Toast.LENGTH_SHORT).show();
//            }
//        });


        mClearEditText = (ClearEditText) findViewById(R.id.search_city);

        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {



            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
                mList.setAdapter(myadapter);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * @param filterStr
     */
    private void filterData(String filterStr){
        filterDateList = new ArrayList<City>();

        Log.d("Filter", filterStr);

        if(TextUtils.isEmpty(filterStr)){
            for(City city : cityList) {

                filterDateList.add(city);

            }
        }else{
            filterDateList.clear();
            for(City city : cityList){
                if(city.getCity().indexOf(filterStr.toString()) != -1){
                    filterDateList.add(city);
                }
            }
        }
        // 根据a-z进行排序
        // Collections.sort(filterDateList, pinyinComparator);
        myadapter.updateListView(filterDateList);
    }


    public  void  onClick(View v){
        switch (v.getId()){
            case  R.id.title_backoff:
                Intent i = new Intent();
                i.putExtra("cityCode","101012000");
                setResult(RESULT_OK,i);
                finish();
                break;
            default:
                break;

        }

    }


}
