package edu.scse.take;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;


public class ActivityTalk extends AppCompatActivity {

    private List<ItemBeanTalk> mMessageList =new ArrayList<>();
    private EditText inputText;
    private RecyclerView msgRecyclerView;
    private ItemBeanTalk.MsgAdapter adapter;
    ConstraintLayout mainLayout;

    @Override
    protected void onResume() {
        super.onResume();
        if(getSharedPreferences("blur",MODE_PRIVATE).getBoolean("blur_bg",false)){
            try {
                Bitmap background = BitmapFactory.decodeFile("/storage/emulated/0/uclean/background_blur.jpg");
                mainLayout.setBackground(new BitmapDrawable(background));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            try {
                Bitmap background = BitmapFactory.decodeFile("/storage/emulated/0/uclean/background.jpg");
                mainLayout.setBackground(new BitmapDrawable(background));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        mainLayout=findViewById(R.id.ll_message);
        final Toolbar toolbar=findViewById(R.id.toolbar_message);
        toolbar.setTitle("交流");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initMsgs();
        inputText=findViewById(R.id.input_text);
        Button send = findViewById(R.id.send);
        msgRecyclerView=findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter=new ItemBeanTalk.MsgAdapter(mMessageList);
        msgRecyclerView.setAdapter(adapter);

        send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String content =inputText.getText().toString();
                if (!"".equals(content)) {
                    ItemBeanTalk message =new ItemBeanTalk(content, ItemBeanTalk.TYPE_SENT);
                    mMessageList.add(message);
                    adapter.notifyItemInserted(mMessageList.size()-1);
                    msgRecyclerView.scrollToPosition(mMessageList.size()-1);
                    inputText.setText("");
                }
            }
        });

    }

    private void initMsgs() {
        mMessageList.add(new ItemBeanTalk("Hello!", ItemBeanTalk.TYPE_RECEIVED));
        mMessageList.add(new ItemBeanTalk("World!", ItemBeanTalk.TYPE_SENT));
    }
}
