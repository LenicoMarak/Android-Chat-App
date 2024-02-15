package com.application.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.chat.Adapters.FilterAdapter;
import com.application.chat.Models.Friend;
import com.application.chat.Models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class UserAddActivity extends AppCompatActivity implements OnAddButtonListener {
    DatabaseReference ref;
    FilterAdapter filterAdapter;
    Executor exe;
    RecyclerView recyclerView;
    TextInputEditText searchInput;
    ArrayList<User> filterList;
    FirebaseUser fUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add);
        filterList=new ArrayList<>();
        this.exe=Executors.newSingleThreadExecutor();
        this.ref=FirebaseDatabase.getInstance().getReference("Users");
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        buildSearchBar();
        filterAdapter=new FilterAdapter();
        filterAdapter.setOnAddButtonListener(this);
        recyclerView=findViewById(R.id.hintsRecycler);
        recyclerView.setAdapter(filterAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.backBtn).setOnClickListener(v ->onBackPressed());
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s= charSequence.toString();
                if (s!="")
                    searchUser(s);
                else {
                    filterList.clear();
                    filterAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        searchInput.setOnEditorActionListener((v,actionId,event)->{
            String name=searchInput.getText().toString();
            if(actionId==EditorInfo.IME_ACTION_SEARCH){
                if(!name.equals("")){
                    searchUser(name);
                    return true;
                }
            }
            return false;
        });
    }
    @Override
    public void onClickAddButton(int pos) {
        if(pos > -1){
            addToFriend(filterList.get(pos).getId());
            Intent i=new Intent(getApplicationContext(), UserActivity.class);
            startActivity(i);
            finish();
        }
    }
    public void addToFriend(String id){
        Friend newFriend=new Friend(id);
        if ((newFriend!=null && newFriend.getFriendId()!=null)){
            DatabaseReference dbref=FirebaseDatabase.getInstance().getReference("Friends");
            dbref.child(fUser.getUid()).push().setValue(newFriend);
        }
    }
    public void buildSearchBar(){
        this.searchInput=findViewById(R.id.searhBar);
        this.searchInput.requestFocus();
        InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(this.searchInput,InputMethodManager.SHOW_IMPLICIT);
    }
    public void searchUser(String username){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        Query query=ref.orderByChild("name").equalTo(username);
        filterList.clear();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        User user=dataSnapshot.getValue(User.class);
                        if(user!=null && user.getId()!=null && user.getImage()!=null)
                            filterToRecycler(user);
                        else
                            Log.e("searchUser()","User is null");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseInsertError",error.getMessage());

            }
        });
    }
    public void filterToRecycler(User user){
        filterList.add(user);
        filterAdapter.filterAdapterList(filterList);
        recyclerView.smoothScrollToPosition(filterAdapter.getItemCount()-1);
    }
}