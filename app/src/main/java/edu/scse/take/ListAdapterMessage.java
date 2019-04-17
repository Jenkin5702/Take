package edu.scse.take;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
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

public class ListAdapterMessage extends BaseAdapter {
    private List<ItemBeanMessage> list;
    private LayoutInflater inflater;
    private Context context;
    private boolean favored = false;

    public ListAdapterMessage(List<ItemBeanMessage> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
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
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_message, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.portrait = convertView.findViewById(R.id.civ_msg);
            viewHolder.time = convertView.findViewById(R.id.tv_msg_time);
            viewHolder.username = convertView.findViewById(R.id.tv_msg_username);
            viewHolder.location = convertView.findViewById(R.id.tv_msg_location);
            viewHolder.content = convertView.findViewById(R.id.tv_msg);
            convertView.setTag(viewHolder);
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
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ItemBeanMessage itemBeanMessage = list.get(position);
        ImageLoader imageLoader=new ImageLoader();
        String imgUrlPortrait="http://192.168.43.145:8080/media/img/"+itemBeanMessage.username+"portrait.jpg";
        imageLoader.showImageByAsyncTask(viewHolder.portrait,imgUrlPortrait);
        viewHolder.portrait.setTag(imgUrlPortrait);
        viewHolder.time.setText(itemBeanMessage.time);
        viewHolder.username.setText(itemBeanMessage.username);
        viewHolder.location.setText(itemBeanMessage.location);
        viewHolder.content.setText(itemBeanMessage.content);
        return convertView;
    }

    class ViewHolder {
        CircleImageView portrait;
        TextView time;
        TextView username;
        TextView location;
        TextView content;
    }
}
