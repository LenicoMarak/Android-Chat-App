package com.application.chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.chat.Adapters.MessageAdapter;
import com.application.chat.CatcheDb.ObjectRepository;
import com.application.chat.CatcheDb.UserEntity;
import com.application.chat.CatcheDb.UserEntity_;
import com.application.chat.Models.Message;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.objectbox.Box;
import io.objectbox.query.QueryBuilder;

public class ConversationActivity extends AppCompatActivity {
    TextInputEditText messagetext;
    TextView username;
    ImageView image;
    ImageView sendBtn;
    RecyclerView recyclerMsg;
    MessageAdapter messageAdapter;
    FirebaseUser fUser;
    Intent infoIntent;
    ExecutorService exe;
    List<Message> list;
    ImageView onlineDot;
    FirebaseUser currentUser;
    String remoteUserId;
    //TextView lastSeen;
    DatabaseReference userRef;
    Box<UserEntity> box;
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        this.currentUser=FirebaseAuth.getInstance().getCurrentUser();
        userRef=FirebaseDatabase.getInstance().getReference("Users");
        this.exe= Executors.newFixedThreadPool(2);
        this.username=findViewById(R.id.nameText);
        this.image=findViewById(R.id.userProfile);
        //lastSeen=findViewById(R.id.lastSeen);
        this.fUser= FirebaseAuth.getInstance().getCurrentUser();
        this.messagetext=findViewById(R.id.messageInput);
        this.onlineDot=findViewById(R.id.onlineStatus);
        loadEverything();
        this.sendBtn=findViewById(R.id.sendButton);
        sendBtn.setOnClickListener(view -> {
            String msg=messagetext.getText().toString();
            DateFormat dateFormat=new SimpleDateFormat("hh.mm aa");
            if(!msg.equals("")){
                String timeStamp=dateFormat.format(new Date()).toString();
                sendMsg(fUser.getUid(),infoIntent.getStringExtra("uid"),msg,timeStamp);
            }
            messagetext.setText("");
        });
        ImageView backbtn=findViewById(R.id.backPress);
        backbtn.setOnClickListener(v->onBackPressed());
    }
    public void dropMenu(View view){
        PopupMenu popupMenu=new PopupMenu(this,view);
        MenuInflater menuInflater=popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.menu_bar,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId=item.getItemId();
            if(itemId==R.id.background){
                return true;
            } else if(itemId==R.id.setting){
                return true;
            } else if(itemId==R.id.ringtone){
                showOptionDialog();
                return true;
            }else {
                return false;
            }
        });
        popupMenu.show();
    }
    public void loadEverything(){
        this.infoIntent=getIntent();
        this.remoteUserId=infoIntent.getStringExtra("uid");
        username.setText(infoIntent.getStringExtra("name"));
        String img=infoIntent.getStringExtra("image");
        if(img!=null){
            Glide.with(getApplicationContext()).load(Uri.parse(img))
                    .placeholder(R.drawable.default_pic)
                    .error(R.drawable.default_pic)
                    .into(image);

        }
        loadOnlineStatus(infoIntent.getStringExtra("uid"),onlineDot);
        buildChatList();
    }
    public void sendMsg(String sender, String reciver, String msg,String timeStamp){
        exe.execute(()->{
            DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Chats");
            String nodeId=ref.push().getKey();
            Message message =new Message(sender,reciver,msg,timeStamp);
            message.setSeen(false);
            message.setMesssageId(nodeId);
            ref.child(nodeId).setValue(message, ((error, ref1) -> {
                if(error!=null) {
                    Log.e("FirebaseInsertError",error.getMessage());
                }
                updateMessageCount(sender,1);
                pushNotification(currentUser.getDisplayName(),msg,currentUser.getUid(),currentUser.getPhotoUrl().toString());
            }));
        });
    }
    public void pushNotification(String tiltle,String content,String sender,String image){
        HashMap<String,String> data=new HashMap<>();
        String recipientoken=infoIntent.getStringExtra("token");
        data.put("title",tiltle);
        data.put("body",content);
        data.put("sender",sender);
        data.put("imaage",image);
        data.put("token",recipientoken);
       // FCManager.sendRemoteNotification(recipientoken,data);
    }
    public void buildChatList(){
        exe.execute(()->{
            recyclerMsg=findViewById(R.id.messageRecylerView);
            recyclerMsg.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
            linearLayoutManager.setStackFromEnd(true);
            recyclerMsg.setLayoutManager(linearLayoutManager);
            loadMessages();
        });
    }
    public void loadMessages(){
        list=new ArrayList<>();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                if (snapshot.hasChildren() && snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Message message = dataSnapshot.getValue(Message.class);
                        if (message != null && fUser != null && message.getReciver() != null && message.getSender() != null) {
                            if (!message.isSeen() && message.getReciver().equals(fUser.getUid()) &&
                                    message.getSender().equals(remoteUserId) && getApplicationContext() instanceof ConversationActivity){
                                ref.child(message.getMesssageId()).child("seen").setValue(true);
                                updateMessageCount(message.getReciver(),0);
                            }
                            if (message.getReciver().equals(fUser.getUid()) && message.getSender().equals(remoteUserId) || message.
                                    getSender().equals(fUser.getUid()) && message.getReciver().equals(remoteUserId)) {
                                list.add(message);
                            }
                            messageAdapter = new MessageAdapter(list, getApplicationContext());
                            recyclerMsg.setAdapter(messageAdapter);
                        } else {
                            Log.e("NullReference", "chat is null object at loadMessage()");
                        }
                    }
                } else {
                    Log.e("Snapshot", "has no children at loadMessage()");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void updateMessageCount(String id,int newCount){
        DatabaseReference fRef=FirebaseDatabase.getInstance().getReference("Friends").child(fUser.getUid());
        Query query=fRef.orderByChild("friendId").equalTo(id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    for(DataSnapshot data:snapshot.getChildren()){
                        String key=snapshot.getKey();
                        int currCount=snapshot.child("messageCount").getValue(Integer.class);
                        if(!key.isEmpty())
                            fRef.child(key).child("messageCount").setValue(currCount+newCount);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void recieverRingtone(){
        io.objectbox.query.Query<UserEntity> query=box.query().equal(UserEntity_.login_id,fUser.getUid(), QueryBuilder.StringOrder.CASE_SENSITIVE)
                .build();
        List<UserEntity> existing=query.find();
        if(!existing.isEmpty()) {
            UserEntity entity = existing.get(0);
            switch (entity.getRingtone()) {
                case "Wind Tone":
                    this.mediaPlayer = MediaPlayer.create(this, R.raw.wind_tone);
                    break;
                case "Auo Tone":
                    this.mediaPlayer = MediaPlayer.create(this, R.raw.a_ou_tone);
                    break;
                case "TinT Bell":
                    this.mediaPlayer = MediaPlayer.create(this, R.raw.tint_bell);
                    break;
            }
            if(mediaPlayer!=null)
                mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer!=null)
            mediaPlayer.release();
    }

    public void refreshUserAdapter(boolean seen, String sender){
        Intent intent=new Intent("com.application.chat.ACTION_REFRESH");
        intent.putExtra("seen",seen);
        intent.putExtra("sender",sender);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
    public int getPositionById(String target){
        for(int i=0;i<list.size();i++){
            if(list.get(i).getMesssageId().equals(target)){
                return i;
            }
        }
        return -1;
    }
    public void loadOnlineStatus(String uid,ImageView onlineStatus){
        userRef.child(uid).child("isOnline")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isUserOnline=snapshot.getValue(Boolean.class);
                if (isUserOnline){
                    onlineStatus.setVisibility(View.VISIBLE);
                }else {
                    onlineStatus.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CurrentUserStatus",error.getMessage());
            }
        });
    }
    int i=-1;
    public void showOptionDialog(){
        String[] items={"Wind Tone","Aou Tone","Tint Bell"};
        MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(this);
        builder.setTitle("Choose a Ringtone");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                i=which;
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(i!=-1){
                    addToCache(items[i]);
                    Toast.makeText(getApplicationContext(),items[i].toString()+" Tone Selected",Toast.LENGTH_SHORT)
                            .show();
                }else {
                    Toast.makeText(getApplicationContext(),"Not Selected",Toast.LENGTH_SHORT)
                            .show();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
    public void addToCache(String item){
        box= ObjectRepository.getBoxStore().boxFor(UserEntity.class);
        io.objectbox.query.Query<UserEntity> query=box.query().equal(UserEntity_.login_id,fUser.getUid(), QueryBuilder.StringOrder.CASE_SENSITIVE)
                .build();
        List<UserEntity> existing=query.find();
        if(!existing.isEmpty()){
            UserEntity entity=existing.get(0);
            entity.setRingtone(item);
            box.put(entity);
        }else {
            UserEntity entity = new UserEntity();
            entity.setLogin_id(fUser.getUid());
            entity.setRingtone(item);
            box.put(entity);
        }
    }
   /* public String getLastTime(long lastTimeStamp){
        long currentTime=System.currentTimeMillis();
        long timeDifference=currentTime-lastTimeStamp;
        if(timeDifference<60000){
            return "Last seen just now";
        }else if(timeDifference<3600000){
            long minutes=timeDifference/60000;
            return "Last seen "+minutes+" minutes ago";
        }else if(timeDifference<86400000){
            SimpleDateFormat format1=new SimpleDateFormat("h:mm a");
            return "Last seen today at "+format1.format(new Date(lastTimeStamp));
        }else {
            SimpleDateFormat format2=new SimpleDateFormat("MMM d 'at' h:mm a");
            return "Last seen "+format2.format(new Date(lastTimeStamp));
        }
    }*/
}
