package edu.scse.take;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ActivityFriend extends AppCompatActivity {
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
        setContentView(R.layout.activity_friends);
        mainLayout=findViewById(R.id.cl_friend);
        Toolbar toolbar = findViewById(R.id.toolbar_friends);
        toolbar.setTitle("好友");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ListView lvFriend = findViewById(R.id.list_friend);
        List<ItemBeanFriend> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            list.add(new ItemBeanFriend(R.drawable.ss, "\'radiance", "This is introoo~~~~~"));
        }
        ListAdapterFriend adapterFriend = new ListAdapterFriend(list, ActivityFriend.this);
        lvFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(ActivityFriend.this,ActivityTalk.class);
                startActivity(intent);
            }
        });
        lvFriend.setAdapter(adapterFriend);
    }
}
