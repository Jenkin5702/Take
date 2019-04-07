package edu.scse.take;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityLogin extends AppCompatActivity {
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.arg1==0){
                Toast.makeText(ActivityLogin.this,"请输入用户名和密码",Toast.LENGTH_SHORT).show();
            }else if(msg.arg1==1){
                Toast.makeText(ActivityLogin.this,"用户名或密码不正确",Toast.LENGTH_SHORT).show();
            }else if(msg.arg1==2){
                Toast.makeText(ActivityLogin.this,"欢迎回来",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar=findViewById(R.id.toolbar_login);
        toolbar.setTitle("登录");
        setSupportActionBar(toolbar);
        final EditText etUsername=findViewById(R.id.username);
        final EditText etPassword=findViewById(R.id.password);
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username=etUsername.getText().toString();
                final String password=etPassword.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int result;
                        if(username.equals("") || password.equals("")){
                            Message msg=new Message();
                            msg.arg1=0;
                            handler.sendMessage(msg);
                        }else if(DataLoader.login(username,password)){
                            //TODO 把下面的代码放进来

                        }else{
                            SharedPreferences sp = getSharedPreferences("loginStatus", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putBoolean("login", true);
                            editor.putString("username",etUsername.getText().toString());
                            editor.putString("password",etPassword.getText().toString());
                            editor.apply();
                            Message msg=new Message();
                            msg.arg1=2;
                            handler.sendMessage(msg);
                        }

                    }
                }).start();
            }
        });
        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityLogin.this,ActivityRegister.class));
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
