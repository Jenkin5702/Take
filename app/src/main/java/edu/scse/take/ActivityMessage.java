package edu.scse.take;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ActivityMessage extends AppCompatActivity {
    ConstraintLayout mainLayout;

    @Override
    protected void onResume() {
        super.onResume();
        if(getSharedPreferences("blur",MODE_PRIVATE).getBoolean("blur_bg",false)){
            try {
                Bitmap background = BitmapFactory.decodeFile("/storage/emulated/0/uclean/background_blur.jpg");
                mainLayout.setBackground(new BitmapDrawable(background));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            try {
                Bitmap background = BitmapFactory.decodeFile("/storage/emulated/0/uclean/background.jpg");
                mainLayout.setBackground(new BitmapDrawable(background));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messgae);
        mainLayout=findViewById(R.id.cl_message);
        Toolbar toolbar = findViewById(R.id.toolbar_message);
        toolbar.setTitle("消息");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final ListView lvMessage = findViewById(R.id.list_message);
        final List<ItemBeanMessage> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            list.add(new ItemBeanMessage(R.drawable.ss,
                    "12:27", "\'radiance", "location", "content content content "+i));
        }
        final ListAdapterMessage adapterMessage = new ListAdapterMessage(list, ActivityMessage.this);
        lvMessage.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(ActivityMessage.this);
                builder.setTitle("删除").setMessage("要删除这条消息吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        list.remove(position);
                        adapterMessage.notifyDataSetChanged();
                    }
                }).setNegativeButton("取消",null).show();

                return false;
            }
        });
        lvMessage.setAdapter(adapterMessage);
    }
}
