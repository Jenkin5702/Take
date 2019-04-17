package edu.scse.take;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MyMapFragment extends Fragment {
    private static final String a = MyMapFragment.class.getSimpleName();
    private MapView mapView;
    private BaiduMap baiduMap;
    private BaiduMapOptions mapOptions;

    public MyMapFragment() {
    }

    private MyMapFragment(BaiduMapOptions var1) {
        this.mapOptions = var1;
    }

    public static MyMapFragment newInstance() {
        return new MyMapFragment();
    }

    public static MyMapFragment newInstance(BaiduMapOptions var0) {
        return new MyMapFragment(var0);
    }

    public BaiduMap getBaiduMap() {
        return this.mapView == null ? null : this.mapView.getMap();
    }

    public MapView getMapView() {
        return this.mapView;
    }

    @Override
    public void onCreate(Bundle var1) {
        super.onCreate(var1);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater var1, ViewGroup var2, Bundle var3) {
        View view = var1.inflate(R.layout.fragment_map,var2,false);
        mapView=view.findViewById(R.id.map_in_fragment);
        baiduMap=mapView.getMap();

        LocationClient mLocationClient;
        BaiduMapOptions options;

        options = new BaiduMapOptions();
        options.mapType(BaiduMap.MAP_TYPE_NORMAL);

        mLocationClient = new LocationClient(getContext());
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {

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
        });
        mLocationClient.start(); //开启地图定位图层

        baiduMap.setMyLocationEnabled(true); //开启地图定位图层
        baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(17));
        baiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, false, BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding)));
        baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(39.24249, 117.064865))); //设置起始位置

        return mapView;
    }

    @Override
    public void onViewCreated(@NonNull View var1, Bundle var2) {
        super.onViewCreated(var1, var2);
    }

    @Override
    public void onActivityCreated(Bundle var1) {
        super.onActivityCreated(var1);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle var1) {
        super.onSaveInstanceState(var1);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.mapView.onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onConfigurationChanged(Configuration var1) {
        super.onConfigurationChanged(var1);
    }
}
