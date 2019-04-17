package edu.scse.take;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FragmentRecord extends Fragment {
    List<ItemBeanMessage> list=new ArrayList<>();
    ListView listView;
    Context context;
    ListAdapterMessage adapterMessage;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.arg1==2){
                list.remove(msg.arg2);
                adapterMessage.notifyDataSetChanged();
            }else{
                adapterMessage=new ListAdapterMessage(list,context);
                listView.setAdapter(adapterMessage);
            }

        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_record,container,false);
        listView=view.findViewById(R.id.list_record);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if(DataLoader.receiving){
                    String msg="来自"+list.get(position).location+":"+list.get(position).content;
                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext())
                            .setTitle("查看委托内容")
                            .setPositiveButton("接受委托", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    list.get(position).content="(已接受)"+list.get(position).content;
                                }
                            }).setMessage(msg);
                    builder.show();
                }else{
                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext())
                            .setTitle("查看记录")
                            .setMessage(list.get(position).content);
                    builder.show();
                }
            }
        });
        context=getContext();
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(context);
                builder.setTitle("删除").setMessage("要删除这条消息吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String username=context.getSharedPreferences("loginStatus",MODE_PRIVATE).getString("username","0");
                                DataLoader.delDelegate(username,list.get(position).time);
                                Message msg=new Message();
                                msg.arg1=2;
                                msg.arg2=position;
                                handler.sendMessage(msg);
                            }
                        }).start();
                    }
                }).setNegativeButton("取消",null).show();

                return false;
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = getContext().getSharedPreferences("loginStatus", MODE_PRIVATE);
                String username=sp.getString("username","unknown");
                if(DataLoader.receiving){
                    list=DataLoader.loadDelegateByLocation(DataLoader.locations.name);
                }else{
                    list=DataLoader.loadDelegate(username);
                }
                if(list.size()==0){
//                    for (int i = 0; i < 8; i++) {
//                        list.add(new ItemBeanMessage(R.drawable.ss,
//                                "12:27", "\'radiance", "location", "content content content"));
//                    }
                }
                handler.sendMessage(new Message());
            }
        }).start();
    }
}
