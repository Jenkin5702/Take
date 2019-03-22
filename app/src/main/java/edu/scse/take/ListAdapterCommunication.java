package edu.scse.take;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

            viewHolder.btnFavor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!favored){
                        viewHolder.btnFavor.setImageResource(R.drawable.ic_star_accent_24dp);
                        favored=true;
                    }else{
                        viewHolder.btnFavor.setImageResource(R.drawable.ic_star_black_24dp);
                        favored=false;
                    }
                }
            });
            viewHolder.btnResend=convertView.findViewById(R.id.imageButton2);
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
