package edu.scse.take;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.load.resource.transcode.BitmapBytesTranscoder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ActivityChangeBackground extends AppCompatActivity {
    Toolbar toolbar;
    ImageView imageView;
    final int REQUEST_CODE = 1007;
    Bitmap bitmap;
    CheckBox checkBox;
    FileOutputStream fos;
    ConstraintLayout mainLayout;

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
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_bg);
        mainLayout=findViewById(R.id.cl_change_bg);
        toolbar = findViewById(R.id.toolbar_change_bg);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityChangeBackground.this.finish();
            }
        });

        imageView = findViewById(R.id.image_bg);
        bitmap = BitmapFactory.decodeFile("/storage/emulated/0/uclean/background.jpg");
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }

        checkBox = findViewById(R.id.checkbox_blur);
        checkBox.setChecked(false);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    Toast.makeText(ActivityChangeBackground.this,"处理中...",Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(FastBlur.fastblur(bitmap, 20));
                        }
                    }).start();
                } else {
                    Toast.makeText(ActivityChangeBackground.this,"处理中...",Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                        }
                    }).start();
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                ActivityChangeBackground.this.startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_check, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.check) {
            if (writeFile()) {
                Toast.makeText(ActivityChangeBackground.this, "设置成功~", Toast.LENGTH_SHORT).show();
            }
//            finish();
        }
        return true;
    }

    private boolean writeFile() {
        fos = null;
        boolean status = false;
        try {
            fos = new FileOutputStream("/storage/emulated/0/uclean/background.jpg", false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    if (fos != null) {
                        try {
                            fos.flush();
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
            status = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return status;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Toast.makeText(ActivityChangeBackground.this,data.getData().toString(),Toast.LENGTH_SHORT).show();
            bitmap = ImgUtil.handleImage(this, data);
            imageView.setBackgroundColor(Color.TRANSPARENT);
            imageView.setImageBitmap(bitmap);
            checkBox.setChecked(false);
        }
    }

}
