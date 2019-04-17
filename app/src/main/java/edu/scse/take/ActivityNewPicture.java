package edu.scse.take;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActivityNewPicture extends AppCompatActivity {
    Toolbar toolbar;
    ImageView imageView;
    final int REQUEST_CODE = 1007;
    private String url = "https://gss1.bdstatic.com/9vo3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike92%2C5%2C5%2C92%2C30/sign=f1f1e4f23c7adab429dd1311eabdd879/30adcbef76094b36a5474c37a9cc7cd98c109d16.jpg";
    ConstraintLayout mainLayout;
    private Bitmap bitmap;
//    Locations loc;
    Button btnLocation;
    LocationManager locationManager;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bitmap bmp = (Bitmap) msg.obj;
            imageView.setImageBitmap(bmp);
        }
    };

    Handler locateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            btnLocation.setText((String) msg.obj);
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        if (getSharedPreferences("blur", MODE_PRIVATE).getBoolean("blur_bg", false)) {
            try {
                Bitmap background = BitmapFactory.decodeFile("/storage/emulated/0/uclean/background_blur.jpg");
                mainLayout.setBackground(new BitmapDrawable(background));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Bitmap background = BitmapFactory.decodeFile("/storage/emulated/0/uclean/background.jpg");
                mainLayout.setBackground(new BitmapDrawable(background));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_picture);
        mainLayout = findViewById(R.id.cl_new_picture);
        toolbar = findViewById(R.id.toolbar_new_picture);
        toolbar.setTitle("发布动态");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityNewPicture.this.finish();
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        btnLocation = findViewById(R.id.btn_location);
        imageView = findViewById(R.id.imageView_new_picture);
        imageView.setImageURI(Uri.parse(url));
        imageView.setBackgroundColor(Color.TRANSPARENT);
        final EditText etContent=findViewById(R.id.editText_new_picture);

        Button btnSend=findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etContent.getText().toString().equals("")){
                    Toast.makeText(ActivityNewPicture.this,"内容为空哦",Toast.LENGTH_SHORT).show();
                }else{
                    final String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences sp = getSharedPreferences("loginStatus", MODE_PRIVATE);
                            String username=sp.getString("username","unknown");
                            DataLoader.newCommunicate(username,time,DataLoader.locations.name,DataLoader.locations.name,etContent.getText().toString());
                            File f=new File("/storage/emulated/0/uclean/communication_temp.jpg");
                            Map<String,File> m=new HashMap<>();
                            m.put(username+time+".jpg",f);
                            try {
                                DataLoader.upLoadFilePost(m);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    Toast.makeText(ActivityNewPicture.this,"发送成功",Toast.LENGTH_SHORT).show();
                    setResult(1001);
                    finish();
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
//                 bmp = ActivityNewPicture.this.getURLimage(url);
                Bitmap bmp=BitmapFactory.decodeResource(getResources(),R.drawable.ic_photo_size_select_actual_black_24dp);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = bmp;
                System.out.println("000");
                handler.sendMessage(msg);


//                LocationClient mLocationClient = new LocationClient(ActivityNewPicture.this);
//                LocationClientOption option = new LocationClientOption();
//                option.setOpenGps(true); // 打开gps
//                option.setCoorType("bd09ll"); // 设置坐标类型
//                option.setScanSpan(1000);
//                mLocationClient.setLocOption(option);
//                mLocationClient.registerLocationListener(new BDAbstractLocationListener() {
//                    @Override
//                    public void onReceiveLocation(BDLocation location) {
//                        Message locationMsg = new Message();
//                        loc=DataLoader.IMWhere(location.getLongitude(),location.getLatitude());
//                        locationMsg.obj = "我的位置："+loc.name;
//                        locateHandler.sendMessage(locationMsg);
//                    }
//                });
//                mLocationClient.start(); //开启地图定位图层
            }
        }).start();


        btnLocation.setText("我的位置："+DataLoader.locations.name);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(data.getData(), "image/*");
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 38);
            intent.putExtra("aspectY", 26);
            intent.putExtra("outputX", 380);
            intent.putExtra("outputY", 260);
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(
                    new File("/storage/emulated/0/uclean/communication_temp.jpg")));
            startActivityForResult(intent, 1006);
        }else if(requestCode==1006&&resultCode==RESULT_OK){
            bitmap=BitmapFactory.decodeFile("/storage/emulated/0/uclean/communication_temp.jpg");
            imageView.setBackgroundColor(Color.TRANSPARENT);
            imageView.setImageBitmap(bitmap);
        }
    }

}
