package edu.scse.take;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapFragment;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private BroadcastReceiver mReceiver;
    private Toolbar toolbar;
    private FragmentManager fragmentManager;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private MyMapFragment myMapFragment=MyMapFragment.newInstance();
    @Override
    public void onClick(View v) {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        fragmentManager = getSupportFragmentManager();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);

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
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_search) {
//            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//            builder.setView(R.layout.dialog_search);
//            builder.setTitle("查找地点").setPositiveButton("查找", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    LatLng llText = new LatLng(39.246, 117.063);
//                    //设置起始位置
//                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(llText));
//                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(20));
//                    mBaiduMap.clear();
//                    new MarkPoint(llText.latitude,llText.longitude,"快递").mark();
////                    final TextOptions mTextOptions = new TextOptions() // .bgColor(0x77000000)
////                            .fontSize(66) //字号
////                            .fontColor(0xFF000000) //文字颜色
////                            .position(llText);
////                    ValueAnimator animator = ValueAnimator.ofInt(0, 32);
////                    //构建TextOptions对象
////                    animator.setDuration(1000);
////                    animator.setInterpolator(new DecelerateInterpolator());
////                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
////                        @Override
////                        public void onAnimationUpdate(ValueAnimator animation) {
////                            int value = (int) animation.getAnimatedValue();
////                            Overlay mText = mBaiduMap.addOverlay(mTextOptions.text("" + value));
////                            //文字内容
////                        }
////                    });
////                    animator.start();
//                }
//            }).show();
//        } else if (id == R.id.action_exit) {
//            SharedPreferences sp = getSharedPreferences("loginStatus", MODE_PRIVATE);
//            SharedPreferences.Editor editor = sp.edit();
//            editor.putBoolean("login", false);
//            editor.apply();
//            finish();
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                toolbar.setTitle("地图");
                fragmentManager.beginTransaction().replace(R.id.fl_container, new FragmentMap()).commit();
//                mMapView=myMapFragment.getMapView();
//                mBaiduMap=mMapView.getMap();
//
//                LocationClient mLocationClient;
//                BaiduMapOptions options;
//
//                options = new BaiduMapOptions();
//                options.mapType(BaiduMap.MAP_TYPE_NORMAL);
//
//                mLocationClient = new LocationClient(MainActivity.this);
//                LocationClientOption option = new LocationClientOption();
//                option.setOpenGps(true); // 打开gps
//                option.setCoorType("bd09ll"); // 设置坐标类型
//                option.setScanSpan(1000);
//                mLocationClient.setLocOption(option);
//                mLocationClient.registerLocationListener(new MyLocationListener());
//                mLocationClient.start(); //开启地图定位图层
//
//
//                mBaiduMap.setMyLocationEnabled(true); //开启地图定位图层
//                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(17));
//                mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, false, BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding)));
//                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(39.24249, 117.064865))); //设置起始位置
                return true;
            case R.id.navigation_dashboard:
                toolbar.setTitle("分类");
//                fragmentManager.beginTransaction().remove(myMapFragment).commit();
                return true;
            case R.id.navigation_notifications:
                toolbar.setTitle("动态");
                fragmentManager.beginTransaction().replace(R.id.fl_container, new FragmentCommunication()).commit();
                return true;
            case R.id.navigation_shop:
                toolbar.setTitle("商城");
//                fragmentManager.beginTransaction().remove(myMapFragment).commit();
                return true;
        }
        return false;
    }
}