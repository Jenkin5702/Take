package edu.scse.take;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ListAdapterCommunication extends BaseAdapter {
    private List<ItemBeanCommunication> list;
    private LayoutInflater inflater;
    private Context context;
    private boolean favored=false;

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
        final ViewHolder viewHolder;
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

            viewHolder.portrait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    View view=View.inflate(context,R.layout.activity_person_info,null);
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

            viewHolder.btnFavor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!favored){
                        viewHolder.btnFavor.setImageResource(R.drawable.ic_thumb_up_read_24dp);
                        favored=true;
                    }else{
                        viewHolder.btnFavor.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                        favored=false;
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
        ItemBeanCommunication itemBeanCommunication=list.get(position);
        viewHolder.portrait.setImageBitmap(BitmapFactory.decodeResource(parent.getResources(), itemBeanCommunication.portraitRes));
        viewHolder.image.setImageBitmap(BitmapFactory.decodeResource(parent.getResources(), itemBeanCommunication.imageResId));
        viewHolder.time.setText(itemBeanCommunication.time);
        viewHolder.username.setText(itemBeanCommunication.username);
        viewHolder.intro.setText(itemBeanCommunication.intro);
        viewHolder.title.setText(itemBeanCommunication.title);
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
        ImageButton btnResend;
    }
}
