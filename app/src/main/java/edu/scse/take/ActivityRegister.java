package edu.scse.take;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/4/1.
 */

public class ActivityRegister extends AppCompatActivity {
    private static boolean validIdentify=false;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.arg1==1){
                Toast.makeText(ActivityRegister.this,"请填写完整的信息",Toast.LENGTH_SHORT).show();
            }else if(msg.arg1==2){
                Toast.makeText(ActivityRegister.this,"用户名已存在",Toast.LENGTH_SHORT).show();
            }else{
                //TODO 把下面代码放进来
                Toast.makeText(ActivityRegister.this,"注册成功",Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    };
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar=findViewById(R.id.toolbar_register);
        toolbar.setTitle("注册");
        setSupportActionBar(toolbar);
        Button btnRegister=findViewById(R.id.btn_register);
        final EditText etRealName=findViewById(R.id.realname_regi);
        final EditText etUsername=findViewById(R.id.username_regi);
        final EditText etPassword=findViewById(R.id.password_regi);
        final EditText etStuid=findViewById(R.id.uid_regi);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String RealName=etRealName.getText().toString();
                final String Username=etUsername.getText().toString();
                final String Password=etPassword.getText().toString();
                final String Stuid=etStuid.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        validIdentify=DataLoader.register(Username,Password,Stuid,RealName).equals("NO");
                        Message msg=new Message();
                        if(RealName.equals("")|| Username.equals("")|| Password.equals("")|| Stuid.equals("")){
                            msg.arg1=1;
                        }else if(validIdentify){
                            msg.arg1=2;
                        }else{
                            msg.arg1=3;
                        }
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
