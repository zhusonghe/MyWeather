package com.example.administrator.mini_weather;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.List;

import cn.edu.pku.zhusonghe.bean.City;

/**
 * Created by shenzhu on 2016/11/8.
 */
public class Myadapter extends BaseAdapter implements SectionIndexer {
    private List<City> list = null;
    private Context mContext;



    private  int resourceId;
    public Myadapter(Context context, int resource, List<City> objects) {
        this.resourceId = resource;
        this.list = objects;
    }

    public Myadapter(Context mContext, List<City> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final City mContent = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter, null);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.city_name);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.city_name);
            TextView cityName = (TextView) view.findViewById(R.id.city_name);
            cityName.setText( mContent.getCity() + " "+mContent.getNumber());
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }


        return view;

    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int i) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int i) {
        return 0;
    }


    final static class ViewHolder {
        TextView tvLetter;
        TextView tvTitle;
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//       City city = getItem(position);
//        View view;
//        if (convertView==null){
//            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
//        }else {
//            view = convertView;
//        }
//        TextView cityName = (TextView) view.findViewById(R.id.city_name);
//        cityName.setText( city.getCity() + " "+city.getNumber());
//        return  view;
//    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     * @param list
     */
    public void updateListView(List<City> list){
        this.list = list;
        notifyDataSetChanged();
    }

}



