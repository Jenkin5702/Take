package edu.scse.take;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FragmentCommunication extends Fragment {
    List<ItemBeanCommunication> list=new ArrayList<>();
    ListView lvCommunication;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ListAdapterCommunication adapterCommunication=new ListAdapterCommunication(list,getContext());
            lvCommunication.setAdapter(adapterCommunication);
        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_communication,container,false);
        lvCommunication=view.findViewById(R.id.lv_communication);
        TabLayout tbCommunication=view.findViewById(R.id.tabLayout2);
        FloatingActionButton fab=view.findViewById(R.id.float_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), ActivityNewPicture.class);
                getContext().startActivity(intent);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                list=DataLoader.loadCommunicate();
                for(int i=0;i<8;i++){
                    list.add(new ItemBeanCommunication(R.drawable.ss,
                            R.drawable.bg,
                            "12:27","\'radiance","This is content and following is the picturererere~~~~~"," "));
                }
                handler.sendMessage(new Message());
            }
        }).start();
        return view;
    }
}
