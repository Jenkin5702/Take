package edu.scse.take;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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

public class FragmentMap extends Fragment implements BaiduMap.OnMarkerClickListener {
    private MapView mapView;
    private BaiduMap baiduMap;
    private LocationClient mLocationClient;
    BaiduMapOptions options;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        
        mapView=view.findViewById(R.id.map_in_fragment);
        baiduMap=mapView.getMap();

        BaiduMapOptions options;

        options = new BaiduMapOptions();
        options.mapType(BaiduMap.MAP_TYPE_NORMAL);

        mLocationClient = new LocationClient(getContext());
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

        return view;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    class MarkPoint {
        LatLng point;
        String title;

        MarkPoint(double lat, double lng, String title) {
            point = new LatLng(lat, lng);
            this.title = title;
        }

        private void mark() {
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions overlayOptions = new MarkerOptions()
                    .position(point)
                    .icon(bitmap)
                    .animateType(MarkerOptions.MarkerAnimateType.grow)
                    .title(title);
            //在地图上添加Marker，并显示
            baiduMap.addOverlay(overlayOptions);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        baiduMap = null;
    }

    public BaiduMap getBaiduMap() {
        return baiduMap;
    }

    public MapView getMapView() {
        return mapView;
    }



}
