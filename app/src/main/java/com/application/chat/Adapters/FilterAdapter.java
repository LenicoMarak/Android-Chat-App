package com.application.chat.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.application.chat.Models.User;
import com.application.chat.OnAddButtonListener;
import com.application.chat.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterHolder> {
    OnAddButtonListener onAddButtonListener;
    private static int VIEW_TYPE_USER=0;
    private static int VIEW_TYPE_NO_RESULTS=1;
    List<User> filterList;
    public FilterAdapter(){
        filterList=new ArrayList<>();
    }
    @NonNull
    @Override
    public FilterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext().getApplicationContext()).
                    inflate(R.layout.search_item, parent, false);
            return new FilterHolder(view);
        }else {
            View view1 = LayoutInflater.from(parent.getContext().getApplicationContext()).
                    inflate(R.layout.view_no_results, parent, false);
            return new FilterHolder(view1);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull FilterHolder holder, int position) {
        if(holder.getItemViewType()==VIEW_TYPE_USER) {
            User user = filterList.get(position);
            holder.name.setText(user.getName());
            if(user.getImage()!=null)
                Glide.with(holder.itemView.getContext().getApplicationContext())
                        .load(user.getImage())
                        .placeholder(R.drawable.default_pic)
                        .error(R.drawable.default_pic)
                        .into(holder.profile);
            if(user.isOnline())
                holder.status.setVisibility(View.VISIBLE);
            else
                holder.status.setVisibility(View.GONE);

            holder.buttonAddUser.setOnClickListener(view -> {
                if (onAddButtonListener != null) {
                    onAddButtonListener.onClickAddButton(holder.getAdapterPosition());
                }
            });
        }
        else {
            holder.no_result.setText("No Results");
        }
    }
    public void setOnAddButtonListener(OnAddButtonListener btn) {
        this.onAddButtonListener=btn;
    }
    @Override
    public int getItemCount() {
        return filterList.size();
    }
    public void filterAdapterList(ArrayList<User> newfilterList){
        this.filterList=newfilterList;
        notifyDataSetChanged();
    }
    @Override
    public int getItemViewType(int position) {
        if(filterList.isEmpty())
            return VIEW_TYPE_NO_RESULTS;
        else
            return VIEW_TYPE_USER;
    }
    public static class FilterHolder extends RecyclerView.ViewHolder{
        TextView name,no_result,status;
        ImageView profile;
        AppCompatButton buttonAddUser;
        public FilterHolder(@NonNull View itemView) {
            super(itemView);
            no_result=itemView.findViewById(R.id.noresultTv);
            buttonAddUser=itemView.findViewById(R.id.addButton);
            name=itemView.findViewById(R.id.usernameTv);
            status=itemView.findViewById(R.id.status);
            profile=itemView.findViewById(R.id.profile_image);
        }
    }
}
