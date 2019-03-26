package edu.scse.take;

import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private BroadcastReceiver mReceiver;
    private Toolbar toolbar;
    private FragmentManager fragmentManager;
    private MapView mapView;
    private BaiduMap baiduMap;
    private Context context;
    private LocationClient mLocationClient;
    private ValueAnimator animatorDrop;
    private ValueAnimator animatorArise;
    private float mapX;
    private float mapY;
    private Bitmap background;
    private boolean hided = false;
    private FileOutputStream fos;
    private ConstraintLayout mainLayout;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_setting:
                startActivity(new Intent(context, ActivitySetting.class));
                return true;
        }
        return false;
    }

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
    public void onClick(View v) {

    }

    private void setMapCustomFile(Context context, String fileName) {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        String moduleName = null;
        try {
            Toast.makeText(context, "加载资源", Toast.LENGTH_SHORT).show();
            inputStream = context.getAssets().open("custom_config/" + fileName);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            moduleName = context.getFilesDir().getAbsolutePath();
            File file = new File(moduleName + "/" + fileName);
            if (file.exists()) file.delete();
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            //将自定义样式文件写入本地
            fileOutputStream.write(b);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "未能加载资源", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//设置自定义样式文件
        MapView.setCustomMapStylePath(moduleName + "/" + fileName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = MainActivity.this;
//        setMapCustomFile(context, "yanmou.json");
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
        super.onResume();
        try {
            Bitmap background = BitmapFactory.decodeFile("/storage/emulated/0/uclean/background.jpg");
//            BackgroundSrc.setBackground(background,true);
            background=FastBlur.fastblur(background,20);
            mainLayout.setBackground(new BitmapDrawable(background));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
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
                    LatLng llText = new LatLng(39.246, 117.063);
                    //设置起始位置
                    baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(llText));
                    baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(20));
                    baiduMap.clear();
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