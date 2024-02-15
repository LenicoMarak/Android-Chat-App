package com.application.chat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.application.chat.Dialogs.ProgressDialog;
import com.application.chat.Models.User;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileDetailsActivity extends AppCompatActivity {
    ImageView profileImage;
    TextInputLayout inputName;
    FirebaseUser fUser;
    boolean check=true;
    Bitmap imageBitmap;
    ExecutorService exe;
    DatabaseReference userRef;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        this.fUser =FirebaseAuth.getInstance().getCurrentUser();
        setContentView(R.layout.activity_profile_details);
        this.userRef=FirebaseDatabase.getInstance().getReference("Users");
        this.progressDialog=new ProgressDialog(this);
        this.inputName=findViewById(R.id.nameInput);
        this.exe=Executors.newFixedThreadPool(2);
        this.profileImage=findViewById(R.id.profile_pic);
        this.profileImage.setOnClickListener(v->selectImage());
        AppCompatButton nextButton=findViewById(R.id.buttonNext);
        nextButton.setOnClickListener(view -> {
            String name= inputName.getEditText().getText().toString();
            progressDialog.show();
            if(isValid(name) && check){
                updateName(name);
                firebaseStore();
                Drawable drawable=profileImage.getDrawable();
                if(drawable instanceof BitmapDrawable){
                    Bitmap bitmap=((BitmapDrawable) drawable).getBitmap();
                    if(bitmap!=null){
                        Uri uri=getUri(bitmap);
                        uploadToStorage(uri);
                    }
                }
                switchActivity(new UserActivity());
            }
            progressDialog.dismiss();
        });
    }
    public Uri getUri(Bitmap bitmap){
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
        String path=MediaStore.Images.Media.insertImage(this.getContentResolver(),bitmap,"Bitmap",null);
        return Uri.parse(path);
    }
    public void updateProfilePic(Uri image){
        UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.Builder()
                .setPhotoUri(image)
                .build();
        fUser.updateProfile(profileChangeRequest).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(getApplicationContext(),"Profile Updated",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            check=false;
            Toast.makeText(getApplicationContext(),"Couldn't update profile",Toast.LENGTH_SHORT).show();
        });
    }
    public void updateName(String name){
        UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.Builder()
                .build();
        fUser.updateProfile(profileChangeRequest).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                firebaseStore();
                Toast.makeText(getApplicationContext(),"Name Updated",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            check=false;
            Toast.makeText(getApplicationContext(),"Couldn't update profile",Toast.LENGTH_SHORT).show();
        });
    }
    public void uploadToStorage(Uri uri){
        StorageReference storageReference=FirebaseStorage.getInstance().getReference();
        StorageReference imageRef=storageReference.child(fUser.getUid()).child("photo.jpg");
        exe.execute(()-> {
            UploadTask uploadTask = imageRef.putFile(uri);
            uploadTask.addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uriImage -> {
                updateProfilePic(uriImage);
                Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            })).addOnFailureListener(e -> {
                progressDialog.dismiss();
                check = false;
                Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
            });
        });
    }
    public void firebaseStore(){
        exe.execute(()-> {
            if(fUser !=null && fUser.getPhotoUrl()!=null && fUser.getDisplayName()!=null){
                User userInfo = new User(fUser.getUid(), fUser.getDisplayName(), fUser.getPhotoUrl().toString(),null,false);
                userRef.child(fUser.getUid()).setValue(userInfo);
            }
            else{
                Log.e("Null pointer","No such firebase user");
            }
        });
    }
    public void selectImage(){
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        launchSomeActivity.launch(i);
    }
    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
               if(result.getResultCode()==Activity.RESULT_OK){
                   Intent data=result.getData();
                   if(data!=null && data.getData()!=null) {
                        Uri uri = data.getData();
                       try{
                           imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                           if(imageBitmap!=null)
                               profileImage.setImageBitmap(imageBitmap);
                       } catch (Exception ex) {
                           ex.printStackTrace();
                       }
                   }
               }
            });
    public boolean isValid(String name){
        if(TextUtils.isEmpty(name)){
            inputName.setError("this field is empty");
            check=false;
        }
        else{
            inputName.setError(null);
        }
        return check;
    }
    public void switchActivity(Activity act){
        Intent i=new Intent(getApplicationContext(), act.getClass());
        startActivity(i);
        finish();
    }
}