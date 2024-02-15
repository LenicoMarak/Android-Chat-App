package com.application.chat.Adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.application.chat.Models.Message;
import com.application.chat.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int LEFT_ITEM=1;
    private static final int RIGHT_ITEM=2;
    String reciverid;
    Context context;
    private List<Message> messageList;
    public MessageAdapter(List<Message> list, Context context){
        this.messageList=list;
        this.context=context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        if(viewType==RIGHT_ITEM){
            View senderview= inflater.inflate(R.layout.right_msg_item, parent, false);
            Log.e("Reciver layout","Done");
            return new SenderViewHolder(senderview);
        } else if(viewType==LEFT_ITEM) {
            View reciverview = inflater.inflate(R.layout.left_msg_item, parent, false);
            Log.e("Sender layout","Done");
            return new ReciverViewHolder(reciverview);
        }
        return null;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message =(Message) messageList.get(position);
        switch (holder.getItemViewType()){
            case RIGHT_ITEM:
                SenderViewHolder senderHolder=((SenderViewHolder) holder);
                senderHolder.bind(message,senderHolder);
                break;
            case LEFT_ITEM:
                ReciverViewHolder reciverHolder=((ReciverViewHolder) holder);
                reciverHolder.bind(message,reciverHolder);
                break;
            default:
                Log.e("NoAdapterItemFound","Missing left or right item in RecyclerView");
        }
    }
    public void updateSeen(int pos,boolean seen){
        Message message =messageList.get(pos);
        message.setSeen(seen);
        notifyItemChanged(pos);
    }
    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getSender().equals(FirebaseAuth.getInstance().
                getCurrentUser().getUid())){
            return RIGHT_ITEM;
        }else {
            return LEFT_ITEM;
        }
    }
    @Override
    public int getItemCount() {
        return messageList.size();
    }
    public static class SenderViewHolder extends RecyclerView.ViewHolder{
        TextView msgText;
        TextView textTimeStamp;
        ImageView seen1,seen2;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            this.seen1=itemView.findViewById(R.id.firstThick);
            this.seen2=itemView.findViewById(R.id.secondThick);
            this.msgText = itemView.findViewById(R.id.rightMessage);
            this.textTimeStamp= itemView.findViewById(R.id.rightTimeStamp);
        }
        public void bind(Message message, RecyclerView.ViewHolder holder){
            msgText.setText(message.getMessage());
            textTimeStamp.setText(message.getTimestamp());
            int mode=holder.itemView.getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (mode == Configuration.UI_MODE_NIGHT_YES){
                textTimeStamp.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.colorNavLight));
            }else {
                textTimeStamp.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.dark));
            }
            if(!message.isSeen()){
                seen2.setVisibility(View.GONE);
                seen1.setVisibility(View.VISIBLE);
            }
            else {
                seen1.setVisibility(View.GONE);
                seen2.setVisibility(View.VISIBLE);
            }
        }
    }
    public static class ReciverViewHolder extends RecyclerView.ViewHolder{
        TextView msgText;
        TextView textTimeStamp;
        public ReciverViewHolder(@NonNull View itemView) {
            super(itemView);
            this.msgText = itemView.findViewById(R.id.leftMessage);
            this.textTimeStamp= itemView.findViewById(R.id.leftTimeStamp);
        }
        public void bind(Message message, RecyclerView.ViewHolder holder){
            msgText.setText(message.getMessage());
            textTimeStamp.setText(message.getTimestamp());
            int mode=holder.itemView.getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (mode == Configuration.UI_MODE_NIGHT_YES){
                textTimeStamp.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.colorNavLight));
            }else {
                textTimeStamp.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.dark));
            }
        }
    }
}
