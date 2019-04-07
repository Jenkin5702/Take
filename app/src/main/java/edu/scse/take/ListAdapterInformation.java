package edu.scse.take;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListAdapterInformation extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String[] keyword={"用户名","学号"};
    ListAdapterInformation(Context context){
        this.context=context;
        inflater=LayoutInflater.from(context);

    }
    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView=inflater.inflate(R.layout.item_information,parent,false);
        TextView tvKey=convertView.findViewById(R.id.item_info_key);
        TextView tvValue=convertView.findViewById(R.id.item_info_value);
        tvKey.setText(keyword[position]);
        tvValue.setText(keyword[position]+"的值");
        tvValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return null;
    }
}
