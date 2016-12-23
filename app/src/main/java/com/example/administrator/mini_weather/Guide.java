package com.example.administrator.mini_weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenzhu on 2016/11/29.
 */
public class Guide extends Activity implements ViewPager.OnPageChangeListener,View.OnClickListener {
    private ViewPagerAdapter vpadpter;
    private ViewPager vp;
    private List<View> views;
    private ImageView[] dos;
    private Button btn;
    private int[] ids={R.id.iv1,R.id.iv2,R.id.iv3};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);

        iniView();
        iniDos();

    }



    void iniDos(){
        dos = new ImageView[views.size()];
        for(int i=0;i<views.size();i++){
            dos[i]=(ImageView)findViewById(ids[i]);
        }
    }





    public void iniView(){
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.page1,null));
        views.add(inflater.inflate(R.layout.page2,null));
        views.add(inflater.inflate(R.layout.page3,null));
        vpadpter = new ViewPagerAdapter(views,this);
        vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(vpadpter);
        vp.setOnPageChangeListener(this);
        btn = (Button) views.get(2).findViewById(R.id.start);
        btn.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.start){
            Intent i= new Intent(this,MainActivity.class);
            startActivity(i);
        }

    }
}
