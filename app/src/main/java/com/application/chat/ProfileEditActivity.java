package com.application.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileEditActivity extends AppCompatActivity {
    ImageView profile;
    FirebaseUser user;
    RelativeLayout editNameL,editEmailL;
    Uri imageUri;
    ExecutorService exe;
    TextView nameTv,emailTv;
    DatabaseReference ref1;
    boolean checked=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        this.exe= Executors.newFixedThreadPool(3);
        this.ref1=FirebaseDatabase.getInstance().getReference("Users");
        this.user= FirebaseAuth.getInstance().getCurrentUser();
        this.editNameL=findViewById(R.id.relativeLayout1);
        this.editEmailL=findViewById(R.id.relativeLayout2);
        this.nameTv=findViewById(R.id.nameTv);
        this.emailTv=findViewById(R.id.emailTv);
        editNameL.setOnClickListener(view -> {
            showDialogInput("Enter your name",nameTv);
        });
        editEmailL.setOnClickListener(view -> showDialogInput("Enter your email",emailTv));
        this.profile=findViewById(R.id.imageProfile);
        Glide.with(getApplicationContext()).load(user.getPhotoUrl())
                        .placeholder(R.drawable.default_pic)
                                .error(R.drawable.default_pic)
                                        .into(profile);
        nameTv.setText(user.getDisplayName());
        emailTv.setText(user.getEmail());
        AppCompatButton save=findViewById(R.id.saveBtn);
        save.setOnClickListener(v->{
            if(checked) {
                updateName(nameTv.getText().toString());
                onBackPressed();
            }
            else {
                Log.e("checked error:","Falied to check");
            }
        });
        ImageView backbutton=findViewById(R.id.backBtn);
        ImageView editButton=findViewById(R.id.editImageButton);
        backbutton.setOnClickListener(view -> onBackPressed());
        editButton.setOnClickListener(view -> chooseImage());
        profile.setOnClickListener(v->chooseImage());
    }
    public void showDialogInput(String title,TextView textView){
        View inputView= LayoutInflater.from(this).inflate(R.layout.input_dialog,null);
        TextView textTitle=inputView.findViewById(R.id.input_heading);
        textTitle.setText(title);
        TextInputEditText inputText=inputView.findViewById(R.id.inputText);
        inputText.requestFocus();
        InputMethodManager methodManager=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        methodManager.showSoftInput(inputText,InputMethodManager.SHOW_IMPLICIT);
        MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(this);
        inputText.setInputType(InputType.TYPE_CLASS_TEXT);
        inputText.setHint("Type here..");
        builder.setView(inputView);
        builder.setPositiveButton("Ok", (dialogInterface, i) -> {
            String name=inputText.getText().toString();
            textView.setText(name);
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    public void firebaseUpdateName(String name){
        ref1.child(user.getUid()).child("name").setValue(name)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){

                    }
                }).addOnFailureListener(f->{
                    checked=false;
                    Log.e("FirebaseDatabase error",f.getMessage());
                });
    }

    public void updateName(String name){
        exe.execute(()->{
            UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();
            user.updateProfile(profileChangeRequest).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    firebaseUpdateName(name);
                }
            }).addOnFailureListener(e -> {
                checked=false;
                Toast.makeText(getApplicationContext(),"Couldn't update profile",Toast.LENGTH_SHORT).show();
            });
        });
    }
    Bitmap imageBitmap;
    ActivityResultLauncher<Intent> launchActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode()== Activity.RESULT_OK){
                    Intent data=result.getData();
                    if(data!=null && data.getData()!=null) {
                        Uri uri = data.getData();
                        uploadToStorage(uri);
                        try {
                            imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        profile.setImageBitmap(imageBitmap);
                    }
                }
            }
    );
    public void chooseImage(){
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        launchActivity.launch(i);
    }
    public void firebaseUpdateImage(String image){
        ref1.child(user.getUid()).child("image").setValue(image)
                .addOnCompleteListener(task -> {
                    if ((task.isSuccessful())){
                       Toast.makeText(getApplicationContext(),"Photo updated",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(f->{
                    checked=false;
                    Log.e("FirebaseDatabase error",f.getMessage());
                });
    }
    public void updateProfilePic(Uri image){
        exe.execute(()->{
            UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.Builder()
                    .setPhotoUri(image)
                    .build();
            user.updateProfile(profileChangeRequest).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    firebaseUpdateImage(user.getPhotoUrl().toString());
                }
            }).addOnFailureListener(e -> {
                checked=false;
                Toast.makeText(getApplicationContext(),"Couldn't update profile",Toast.LENGTH_SHORT).show();
            });
        });
    }
    public void uploadToStorage(Uri uri){
        exe.execute(()->{
            StorageReference storageReference=FirebaseStorage.getInstance().getReference();
            StorageReference imageRef=storageReference.child(user.getUid()).child("photo.jpg");
            UploadTask uploadTask = imageRef.putFile(uri);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uriImage -> {
                    imageUri=uriImage;
                    updateProfilePic(uriImage);
                });
            }).addOnFailureListener(e -> {
                checked=false;
                Log.e("FirebaseStorage error",e.getMessage());
            });
        });
    }
}