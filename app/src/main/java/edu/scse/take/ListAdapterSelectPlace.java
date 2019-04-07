package edu.scse.take;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListAdapterSelectPlace extends BaseAdapter {
    String[] places = {
            "教学楼",
            "图书馆",
            "大活",
            "西区宿舍",
            "东区宿舍",
            "东区食堂",
            "实验楼",
            "西教学院楼",
            "亨顺广场",
            "西区食堂",
            "教师公寓365",
            "校医院",
            "西区篮球场",
            "体育中心",
            "行政楼"
    };
    Context context;
    ListAdapterSelectPlace(Context context){
        this.context=context;
    }
    @Override
    public int getCount() {
        return places.length;
    }

    @Override
    public Object getItem(int position) {
        return places[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tvPlace=new TextView(context);
        tvPlace.setText(places[position]);
        tvPlace.setTextSize(20);
        tvPlace.setPadding(0,10,0,10);
        tvPlace.setTextColor(Color.BLACK);
        return tvPlace;
    }
}
