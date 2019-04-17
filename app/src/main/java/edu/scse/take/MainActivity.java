package edu.scse.take;

import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static edu.scse.take.DataLoader.locData;

public class MainActivity extends AppCompatActivity {
    private BroadcastReceiver mReceiver;
    private Toolbar toolbar;
    private FragmentManager fragmentManager;
    private MapView mapView;
    private BaiduMap baiduMap;
    private ValueAnimator animatorDrop;
    private ValueAnimator animatorArise;
    private LocationClient mLocationClient;
    private float mapX;
    private float mapY;
    private boolean hided = false;
    private ConstraintLayout mainLayout;
    private Bitmap bitmapNav;
    private NavigationView navigationView;
    private boolean notificcation = false;
    private int selectedPosition;
    //    private Locations myLocation;
    private TakeService takeService;
    private boolean isBound = false;

    private Context context;

    public static Handler handler2 = new Handler();

    private String mokuteki;

    int n = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 1) {
                navigationView.setBackground(new BitmapDrawable(bitmapNav));
            } else if (msg.arg1 == 2) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("地点")
                        .setMessage("这里有" + n + "个人")
                        .setPositiveButton("发布委托", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(context, ActivityDeligation.class);
                                intent.putExtra("mokuteki", mokuteki);
                                startActivity(intent);
                            }
                        }).setNegativeButton("关闭", null).show();
            }
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            takeService = ((TakeService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            takeService = null;
        }
    };

    private Thread updateLocation = new Thread(new Runnable() {
        @Override
        public void run() {
            baiduMap.setMyLocationData(DataLoader.locData);
        }
    });

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

        bottomNavigationView.setSelectedItemId(R.id.navigation_dashboard);

        SharedPreferences sp = getSharedPreferences("loginStatus", MODE_PRIVATE);
        boolean logedin = sp.getBoolean("login", false);
        if (!logedin) {
            startActivityForResult(new Intent(context, ActivityLogin.class),7777);
        }
        initHeader();
    }
    public static final int REQUEST_CODE=2007;
    public Bitmap bitmap;
    public CircleImageView headerIv;


    String username;
    String intros;
    private void initHeader(){
        View view=navigationView.getHeaderView(navigationView.getHeaderCount()-1);
        TextView name1=view.findViewById(R.id.tv_username);
        TextView intro=view.findViewById(R.id.tv_id);
        SharedPreferences shp=getSharedPreferences("loginStatus",Context.MODE_PRIVATE);
        username=shp.getString("username","");
        name1.setText(username);
        intro.setText(intros);
        headerIv=view.findViewById(R.id.imageView);
        try {
            Bitmap headerBitmp = BitmapFactory.decodeFile("/storage/emulated/0/uclean/"+username+"headerIv.jpg");
            if(headerBitmp!=null){
                headerIv.setImageBitmap(headerBitmp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        headerIv.setOnClickListener(new View.OnClickListener() {
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
            //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
            intent.putExtra("crop", "true");
            // aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX", 9);
            intent.putExtra("aspectY", 9);
            // outputX outputY 是裁剪图片宽高
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(
                    new File("/storage/emulated/0/uclean/"+username+"headerIv.jpg")));
            startActivityForResult(intent, 1006);
        }else if(requestCode==1006&&resultCode==RESULT_OK){
            bitmap=BitmapFactory.decodeFile("/storage/emulated/0/uclean/"+username+"headerIv.jpg");
            headerIv.setBackgroundColor(Color.TRANSPARENT);
            headerIv.setImageBitmap(bitmap);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File f=new File("/storage/emulated/0/uclean/"+username+"headerIv.jpg");
                    Map<String,File> m=new HashMap<>();
                    m.put(username+"portrait.jpg",f);
                    try {
                        DataLoader.upLoadFilePost(m);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }else if(requestCode==7777){
            initHeader();
        }
    }

    private void initAnimator() {
        animatorArise = ValueAnimator.ofFloat(4000f, mapY);
        animatorArise.setInterpolator(new OvershootInterpolator());
        animatorArise.setDuration(500);
        animatorArise.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mapView.setY((Float) animation.getAnimatedValue());
            }
        });

        animatorDrop = ValueAnimator.ofFloat(mapY, 4000f);
        animatorDrop.setInterpolator(new OvershootInterpolator());
        animatorDrop.setDuration(500);
        animatorDrop.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mapView.setY((Float) animation.getAnimatedValue());
            }
        });
    }

    private int numOfPeople = 0;

    private void loadMap() {
        mapView = findViewById(R.id.baidu_map);
        baiduMap = mapView.getMap();

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
        mLocationClient.registerLocationListener(new DataLoader.MyLocationListener());
        mLocationClient.start(); //开启地图定位图层

        baiduMap.setMyLocationEnabled(true); //开启地图定位图层
        baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(17));
        baiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, false, BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding)));
        baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(39.24249, 117.064865))); //设置起始位置
        updateLocation.start();
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        n = DataLoader.propleNum(DataLoader.locations);
                        Message msg = new Message();
                        msg.arg1 = 2;
                        handler.sendMessage(msg);
                    }
                }).start();
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
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            if (hided) {
                showMap();
            }
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            final View view = LayoutInflater.from(context).inflate(R.layout.dialog_search, null);
            final ListView lv = view.findViewById(R.id.lv_search);
            final EditText rt = view.findViewById(R.id.editText);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    rt.setText(lv.getAdapter().getItem(position).toString());
                    mokuteki = lv.getAdapter().getItem(position).toString();
                    selectedPosition = position;
                }
            });
            lv.setAdapter(new ListAdapterSelectPlace(context));
            builder.setView(view);
            builder.setTitle("选择地点").setPositiveButton("查找", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Locations loc = Locations.values()[selectedPosition];
                    LatLng point = new LatLng(loc.lng, loc.lat);
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
            Toast.makeText(context, DataLoader.locations.name(), Toast.LENGTH_SHORT).show();
        } else if (id == R.id.notification) {
            if (notificcation) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("通知")
                        .setMessage("停止接收委托消息?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                notificcation = false;
                                item.setIcon(R.drawable.ic_notifications_none_black_24dp);
                                if (isBound) {
                                    isBound = false;
                                    unbindService(mConnection);
                                    takeService = null;
                                }
                            }
                        }).setNegativeButton("取消", null);
                builder.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("通知")
                        .setMessage("开始接收委托消息?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                notificcation = true;
                                item.setIcon(R.drawable.ic_notifications_black_24dp);
                                if (!isBound) {
                                    final Intent serviceIntent = new Intent(context, TakeService.class);
                                    bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
                                    isBound = true;
                                }
                            }
                        }).setNegativeButton("取消", null);
                builder.show();
            }
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