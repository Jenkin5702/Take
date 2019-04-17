package edu.scse.take;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ListAdapterRecord extends BaseAdapter {
    private List<ItemBeanFriend> list;
    private LayoutInflater inflater;
    private Context context;
    private boolean favored = false;

    public ListAdapterRecord(List<ItemBeanFriend> list, Context context) {
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
            convertView = inflater.inflate(R.layout.item_friend, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.portrait = convertView.findViewById(R.id.civ_friend);
            viewHolder.intro = convertView.findViewById(R.id.tv_friend_intro);
            viewHolder.username = convertView.findViewById(R.id.tv_friend_username);
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
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ItemBeanFriend itemBeanFriend = list.get(position);
//        ImageLoader imageLoader=new ImageLoader();
//        String imgUrlPortrait="http://192.168.43.145:8080/media/img/"+itemBeanFriend.username+"portrait.jpg";
//        imageLoader.showImageByAsyncTask(viewHolder.portrait,imgUrlPortrait);
//        viewHolder.portrait.setTag(imgUrlPortrait);

        viewHolder.intro.setText(itemBeanFriend.intro);
        viewHolder.username.setText(itemBeanFriend.username);
        return convertView;
    }

    class ViewHolder {
        CircleImageView portrait;
        TextView intro;
        TextView username;
    }
}
