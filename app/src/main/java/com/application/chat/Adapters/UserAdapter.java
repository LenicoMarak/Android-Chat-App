package com.application.chat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.application.chat.ConversationActivity;
import com.application.chat.Models.User;
import com.application.chat.R;
import com.application.chat.UserActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {
    List<User> userInfoList;
    List<User> newFilterList;
    private SparseBooleanArray selecteditems;
    boolean isLongPress=false;
    Context context;
    public UserAdapter(List<User> userList, Context context) {
        this.userInfoList = userList;
        this.newFilterList = new ArrayList<>(userList);
        this.context = context;;
        this.selecteditems=new SparseBooleanArray();

    }
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list,parent,false);
        return new UserHolder(view);
    }
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        User user = userInfoList.get(position);
        if(user.getName()!=null  && user.getToken()!=null && user.getId()!=null) {
            holder.name.setText(user.getName());
            //int visibility=user.isOnline()?View.VISIBLE:View.GONE;
            //holder.onlinestatus.setVisibility(visibility);
            if(user.getImage()!=null){
                Glide.with(context).load(user.getImage())
                        .placeholder(R.drawable.default_pic)
                        .centerCrop()
                        .error(R.drawable.default_pic)
                        .into(holder.profile);
                holder.profile.setOnClickListener(v -> viewImage(user.getImage()));
            }
            holder.itemView.setOnClickListener(v-> {
                if(isLongPress) {
                    if(getItemCount()!=0)
                       toggleSelect(position);
                    else {
                        UserActivity userActivity=(UserActivity) v.getContext();
                        userActivity.menuBarVisibility(false);
                    }
                }
                else {
                    Intent i = new Intent(context, ConversationActivity.class);
                    i.putExtra("name", user.getName());
                    i.putExtra("image", user.getImage());
                    i.putExtra("token", user.getToken());
                    i.putExtra("uid", user.getId());
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            });
        }

        holder.itemView.setOnLongClickListener(v -> {
            UserActivity userActivity=(UserActivity) v.getContext();
            userActivity.menuBarVisibility(true);
            isLongPress=true;
            toggleSelect(position);
            return true;
        });
        boolean isSelect= selecteditems.get(position,false);
        holder.itemView.setActivated(isSelect);
        int color=isSelect?ContextCompat.getColor(context,R.color.menuBar):getDefaultColor();
        holder.itemView.setBackgroundColor(color);
    }
    public boolean isSelected(int pos){
        return selecteditems.get(pos,false);
    }

    public void setLongPress(boolean longPress) {
        isLongPress = longPress;
    }

    public int getDefaultColor(){
        int color=0;
        int currTheme=context.getResources().getConfiguration().
                uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if(currTheme==Configuration.UI_MODE_NIGHT_NO)
            color=android.R.attr.colorPrimary;
        if(currTheme==Configuration.UI_MODE_NIGHT_YES)
            color=android.R.attr.colorPrimaryDark;
        return  color;
    }

    public void toggleSelect(int pos){
        if(selecteditems.get(pos)){
            selecteditems.delete(pos);
        }else {
            selecteditems.put(pos,true);
        }
        notifyItemChanged(pos);
    }
    public void clearSelection(){
        selecteditems.clear();
        notifyDataSetChanged();
    }
    public int getSelectedCount(){
        return selecteditems.size();
    }
    public List<Integer>getSelectedItems(){
        List<Integer> items=new ArrayList<>();
        for(int i=0;i<getItemCount();i++){
            if(selecteditems.get(i))
                items.add(i);
        }
        return items;
    }
    public int getPositionById(String target){
        for(int i=0;i<userInfoList.size();i++){
            if(userInfoList.get(i).getId().equals(target)){
                return i;
            }
        }
        return -1;
    }
    public void viewImage(String uri){
        View dialogView=LayoutInflater.from(context).inflate(R.layout.image_preview,null);
        ImageView imageView=dialogView.findViewById(R.id.profile_image);
        imageView.setOnClickListener(v->{

        });
        Glide.with(context).load(Uri.parse(uri))
                .error(R.drawable.default_pic)
                .placeholder(R.drawable.default_pic)
                .centerCrop().into(imageView);
        MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(context,R.style.MaterialAlertDialog);
        builder.setView(dialogView);
        AlertDialog dialog=builder.create();
        /*Window window=dialog.getWindow();
        if(window!=null){
            WindowManager.LayoutParams layoutParams=new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width=WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height=WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(layoutParams);
        }*/
        dialog.show();
        dialogView.findViewById(R.id.backBtn).setOnClickListener(v ->dialog.dismiss());
    }
    public void buildPopup(View v, User info, int pos){
        PopupMenu popupMenu= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            popupMenu = new PopupMenu(context,v);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            popupMenu.getMenuInflater().inflate(R.menu.longpress_menu,popupMenu.getMenu());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            popupMenu.setOnMenuItemClickListener(menuItem ->{
                int menuId=menuItem.getItemId();
                if(menuId==R.id.fav) {
                    Toast.makeText(context, "Marked", Toast.LENGTH_SHORT).show();
                } else if (menuId==R.id.delete) {
                    userInfoList.remove(pos);
                    notifyItemRemoved(pos);
                    notifyItemRangeChanged(pos,userInfoList.size());
                }
                else {
                    Log.e("PopupItemFailer","Item not found");
                }
                return true;
            });
        }
        popupMenu.show();
    }
    @Override
    public int getItemCount() {
        return userInfoList.size();
    }
    public void updateList(List<User> newList){
        userInfoList=newList;
        notifyDataSetChanged();
    }

    public static class UserHolder extends RecyclerView.ViewHolder{
        TextView onlinestatus;
        TextView name;
        ImageView profile;
        public UserHolder(@NonNull View itemView) {
            super(itemView);
            onlinestatus=itemView.findViewById(R.id.status);
            name=itemView.findViewById(R.id.nameText);
            profile=itemView.findViewById(R.id.userProfile);
        }
    }
}
