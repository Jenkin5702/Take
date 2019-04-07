package edu.scse.take;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


public class ItemBeanTalk {
    public static final int TYPE_RECEIVED=0;
    public static final int TYPE_SENT=1;
    private String content;
    private int type;
    public ItemBeanTalk(String content, int type){
        this.content=content;
        this.type=type;
    }
    public String getContent(){
        return content;
    }
    int getType(){
        return type;
    }


    public static class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
        private List<ItemBeanTalk> mMessageList;
        static class ViewHolder extends RecyclerView.ViewHolder{
            LinearLayout leftlayout;
            LinearLayout rightlayout;
            TextView leftMsg;
            TextView rightMsg;
            ViewHolder(View view){
                super(view);
                leftlayout=view.findViewById(R.id.left_layout);
                rightlayout=view.findViewById(R.id.right_layout);
                leftMsg=view.findViewById(R.id.left_msg);
                rightMsg=view.findViewById(R.id.right_msg);
            }
        }
        public MsgAdapter(List<ItemBeanTalk> messageList){
            mMessageList = messageList;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itembean_message,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ItemBeanTalk message = mMessageList.get(position);
            if(message.getType()== TYPE_RECEIVED){
                holder.leftlayout.setVisibility(View.VISIBLE);
                holder.rightlayout.setVisibility(View.GONE);
                holder.leftMsg.setText(message.getContent());
            } else if (message.getType() == TYPE_SENT) {
                holder.rightlayout.setVisibility(View.VISIBLE);
                holder.leftlayout.setVisibility(View.GONE);
                holder.rightMsg.setText(message.getContent());
            }
        }

        @Override
        public int getItemCount() {
            return mMessageList.size();
        }
    }
}
