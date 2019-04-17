package edu.scse.take;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ListAdapterCommunication extends BaseAdapter {
    private List<ItemBeanCommunication> list;
    private LayoutInflater inflater;
    private Context context;
    private boolean favored=false;
    private Bitmap bmp;
    private ViewHolder viewHolder;
    private ImageLoader imageLoader;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(context,"---",Toast.LENGTH_SHORT).show();
            viewHolder.image.setImageBitmap(bmp);
        }
    };
    public ListAdapterCommunication(List<ItemBeanCommunication> list, Context context) {
        this.list = list;
        this.context=context;
        inflater= LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final ItemBeanCommunication itemBeanCommunication=list.get(position);
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_communication,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.portrait =convertView.findViewById(R.id.imageView3);
            viewHolder.image=convertView.findViewById(R.id.imageView4);
            viewHolder.username=convertView.findViewById(R.id.textView);
            viewHolder.time=convertView.findViewById(R.id.textView2);
            viewHolder.intro=convertView.findViewById(R.id.textView3);
            viewHolder.title=convertView.findViewById(R.id.textView4);
            viewHolder.btnFavor=convertView.findViewById(R.id.imageButton3);
            viewHolder.zan=convertView.findViewById(R.id.num_good);

            viewHolder.portrait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    View view=View.inflate(context,R.layout.activity_person_info,null);
                    ConstraintLayout cl=view.findViewById(R.id.cl_information);
                    cl.setBackground(new BitmapDrawable(
                            FastBlur.fastblur(BitmapFactory.decodeResource(parent.getResources(),
                                    itemBeanCommunication.portraitRes),20)
                    ));
                    Button btnAddFriend=view.findViewById(R.id.btn_add_friend);
                    btnAddFriend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context,"123",Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setView(view).show();
                }
            });
            final SharedPreferences sp=context.getSharedPreferences("zan",Context.MODE_PRIVATE);
            final String loged=context.getSharedPreferences("loginStatus",Context.MODE_PRIVATE).getString("username","");
            final String k=loged+itemBeanCommunication.username+itemBeanCommunication.time;
            favored=sp.getBoolean(k,false);
            if(!favored){
                viewHolder.btnFavor.setImageResource(R.drawable.ic_thumb_up_black_24dp);
            }else{
                viewHolder.btnFavor.setImageResource(R.drawable.ic_thumb_up_read_24dp);
            }

            viewHolder.btnFavor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favored=sp.getBoolean(k,false);
                    if(!favored){
                        viewHolder.btnFavor.setImageResource(R.drawable.ic_thumb_up_read_24dp);
                        viewHolder.zan.setText(String.valueOf(Integer.parseInt(viewHolder.zan.getText().toString())+1));
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SharedPreferences.Editor editor=sp.edit();
                                editor.putBoolean(k,true);
                                editor.apply();
                                DataLoader.zanPlus(itemBeanCommunication.username,itemBeanCommunication.time);
                                favored=true;
                            }
                        }).start();
                    }else{
                        viewHolder.btnFavor.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                        viewHolder.zan.setText(String.valueOf(Integer.parseInt(viewHolder.zan.getText().toString())-1));
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SharedPreferences.Editor editor=sp.edit();
                                editor.putBoolean(k,false);
                                editor.apply();
                                DataLoader.zanMinus(itemBeanCommunication.username,itemBeanCommunication.time);
                                favored=false;
                            }
                        }).start();
                    }
                }
            });
            viewHolder.btnComment=convertView.findViewById(R.id.imageButton);
            viewHolder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
//        viewHolder.portrait.setImageBitmap(BitmapFactory.decodeResource(parent.getResources(), itemBeanCommunication.portraitRes));
        Log.e("123",(itemBeanCommunication.username+itemBeanCommunication.time+".jpg").replace(":","").replace(" ","_"));
        new Thread(new Runnable() {
            @Override
            public void run() {
                String filename=itemBeanCommunication.username+itemBeanCommunication.time+".jpg";
                filename=filename.replace(":","");
                filename=filename.replace(" ","_");
                bmp = DataLoader.getURLimage(filename);
                Message msg=new Message();
                handler.sendMessage(msg);
            }
        });
        String filename=itemBeanCommunication.username+itemBeanCommunication.time+".jpg";
        imageLoader=new ImageLoader();
        String imgUrl="http://192.168.43.145:8080/media/img/"+filename.replace(":","").replace(" ","_");
        String imgUrlPortrait="http://192.168.43.145:8080/media/img/"+itemBeanCommunication.username+"portrait.jpg";
        imageLoader.showImageByAsyncTask(viewHolder.portrait,imgUrlPortrait);
        viewHolder.portrait.setTag(imgUrlPortrait);
        imageLoader.showImageByAsyncTask(viewHolder.image,imgUrl);
        viewHolder.image.setTag(imgUrl);
        viewHolder.time.setText(itemBeanCommunication.time);
        viewHolder.username.setText(itemBeanCommunication.username);
        viewHolder.intro.setText(itemBeanCommunication.intro);
        viewHolder.title.setText(itemBeanCommunication.title);
        viewHolder.zan.setText(String.valueOf(itemBeanCommunication.zan));
        return convertView;
    }

    class ViewHolder{
        CircleImageView portrait;
        ImageView image;
        TextView time;
        TextView username;
        TextView intro;
        TextView title;
        ImageButton btnFavor;
        ImageButton btnComment;
        TextView zan;
    }
}
