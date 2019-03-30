package edu.scse.take;

import android.animation.ValueAnimator;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private BroadcastReceiver mReceiver;
    private Toolbar toolbar;
    private FragmentManager fragmentManager;
    private MapView mapView;
    private BaiduMap baiduMap;
    private Context context;
    private ValueAnimator animatorDrop;
    private ValueAnimator animatorArise;
    private LocationClient mLocationClient;
    private float mapX;
    private float mapY;
    private boolean hided = false;
    private ConstraintLayout mainLayout;
    private Bitmap bitmapNav;
    private NavigationView navigationView;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 1) {
                navigationView.setBackground(new BitmapDrawable(bitmapNav));
            }
        }
    };

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location != null && mapView != null) {
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        .direction(location.getDirection())
                        .latitude(location.getLatitude())// 此处设置开发者获取到的方向信息，顺时针0-360
                        .longitude(location.getLongitude())
                        .build();
                baiduMap.setMyLocationData(locData);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = MainActivity.this;
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        fragmentManager = getSupportFragmentManager();

        mainLayout = findViewById(R.id.main_layout);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(listener);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(navigationListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                bitmapNav = BitmapFactory.decodeResource(getResources(), R.drawable.tbg);
                bitmapNav = FastBlur.fastblur(bitmapNav, 24);
                Message msg = new Message();
                msg.arg1 = 1;
                handler.sendMessage(msg);
            }
        }).start();


        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);


        initAnimator();
        loadMap();

        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }

    private void initAnimator() {
        animatorArise = ValueAnimator.ofFloat(2000f, mapY);
        animatorArise.setInterpolator(new OvershootInterpolator());
        animatorArise.setDuration(500);
        animatorArise.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mapView.setY((Float) animation.getAnimatedValue());
            }
        });

        animatorDrop = ValueAnimator.ofFloat(mapY, 2000f);
        animatorDrop.setInterpolator(new OvershootInterpolator());
        animatorDrop.setDuration(500);
        animatorDrop.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mapView.setY((Float) animation.getAnimatedValue());
            }
        });
    }

    private void loadMap() {
        mapView = findViewById(R.id.baidu_map);
        baiduMap = mapView.getMap();

        Log.i("mapview", "mapX:" + mapX);
        Log.i("mapview", "mapY:" + mapY);
        Log.i("mapview", "mapHeight:" + mapView.getHeight());
        Log.i("mapview", "mapWidth:" + mapView.getWidth());
        mapX = mapView.getX();
        mapY = mapView.getY();

        BaiduMapOptions options;

        options = new BaiduMapOptions();
        options.mapType(BaiduMap.MAP_TYPE_NORMAL);

        mLocationClient = new LocationClient(context);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(new MyLocationListener());
        mLocationClient.start(); //开启地图定位图层

        baiduMap.setMyLocationEnabled(true); //开启地图定位图层
        baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(17));
        baiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, false, BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding)));
        baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(39.24249, 117.064865))); //设置起始位置

        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("地点").setPositiveButton("发布委托", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        mapView.onResume();
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

    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            if (hided) {
                showMap();
            }
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setView(R.layout.dialog_search);
            builder.setTitle("查找地点").setPositiveButton("查找", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LatLng point = new LatLng(39.246, 117.063);
                    //设置起始位置
                    baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
                    baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(20));
                    baiduMap.clear();
                    BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
                    OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
                    baiduMap.addOverlay(option);
                }
            }).show();
        } else if (id == R.id.action_exit) {
            SharedPreferences sp = getSharedPreferences("loginStatus", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("login", false);
            editor.apply();
            finish();
        } else if (id == R.id.hide) {
            if (hided) {
                item.setIcon(R.drawable.ic_map_black_24dp);
                showMap();
            } else {
                item.setIcon(R.drawable.ic_map_black_24dp_opac);
                hideMap();
            }
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(context, ActivitySetting.class));
        } else if (id == R.id.notification) {

        }
        return super.onOptionsItemSelected(item);
    }

    private void showMap() {
        animatorArise.start();
        hided = false;
    }

    private void hideMap() {
        animatorDrop.start();
        hided = true;
    }

    NavigationView.OnNavigationItemSelectedListener navigationListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.nav_setting:
                    startActivity(new Intent(context, ActivitySetting.class));
                    return true;
                case R.id.nav_message:
                    startActivity(new Intent(context, ActivityMessage.class));
                    return true;
                case R.id.nav_friend:
                    startActivity(new Intent(context, ActivityFriend.class));
                    return true;
            }
            return false;
        }
    };

    BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.navigation_home:
                    toolbar.setTitle("动态");
                    if (!hided) hideMap();
                    fragmentManager.beginTransaction().replace(R.id.fragment_main, new FragmentCommunication()).commit();
                    return true;
                case R.id.navigation_dashboard:
                    if (!hided) hideMap();
                    toolbar.setTitle("记录");
                    fragmentManager.beginTransaction().replace(R.id.fragment_main, new FragmentRecord()).commit();
                    return true;
            }
            return false;
        }
    };

}