package edu.scse.take;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityDeligation extends AppCompatActivity {
    ConstraintLayout mainLayout;
    String mokyteki= null;

//    Locations loc=Locations.DEFAULT;

//    LocationManager locationManager;

//    Thread thread=new Thread(new Runnable() {
//        @Override
//        public void run() {
//            LocationClient mLocationClient = new LocationClient(ActivityDeligation.this);
//            LocationClientOption option = new LocationClientOption();
//            option.setOpenGps(true); // 打开gps
//            option.setCoorType("bd09ll"); // 设置坐标类型
//            option.setScanSpan(1000);
//            mLocationClient.setLocOption(option);
//            mLocationClient.registerLocationListener(new BDAbstractLocationListener() {
//                @Override
//                public void onReceiveLocation(BDLocation location) {
//                    Message locationMsg = new Message();
//                    loc=DataLoader.IMWhere(location.getLongitude(),location.getLatitude());
//                    locationMsg.obj = "我的位置："+loc.name;
//                    locateHandler.sendMessage(locationMsg);
//                }
//            });
//            mLocationClient.start(); //开启地图定位图层
//        }
//    });

    Button btnLocation;
    Button btnDelegation;
//    Handler locateHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            btnDelegation.setText((String) msg.obj);
//        }
//    };

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
            Intent intent=getIntent();
            mokyteki = intent.getStringExtra("mokuteki");
//            mokyteki=savedInstanceState.
        if(mokyteki!=null){
            Toast.makeText(ActivityDeligation.this,mokyteki,Toast.LENGTH_SHORT).show();
        }
        setContentView(R.layout.activity_deligation);
        mainLayout=findViewById(R.id.cl_deligation);
        Toolbar toolbar=findViewById(R.id.toolbar_deligation);
        toolbar.setTitle("发布委托");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final EditText etDelegation=findViewById(R.id.editText_deligation);
        btnDelegation=findViewById(R.id.btn_send_deligation);
        btnLocation=findViewById(R.id.btn_deligation);

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etDelegation.getText().toString().equals("")){

                    Toast.makeText(ActivityDeligation.this,"委托消息为空哦",Toast.LENGTH_SHORT).show();
                }else{
                    final String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences sp = getSharedPreferences("loginStatus", MODE_PRIVATE);
                            String username=sp.getString("username","unknown");
                            DataLoader.newDelegate(username,time, DataLoader.locations.name,mokyteki,etDelegation.getText().toString());
                        }
                    }).start();
                    Toast.makeText(ActivityDeligation.this,"发送成功",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        btnDelegation.setText("我的位置："+DataLoader.locations.name);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
