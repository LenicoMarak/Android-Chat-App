package com.application.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.application.chat.Dialogs.ProgressDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    TextInputLayout inputEmail,inputPass;
    ProgressDialog progressDialog;
    ProgressDialog progressDialog2;
    TextView resetPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        this.auth=FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();
        if (user!=null){
            if(user.getDisplayName()!=null || user.getPhotoUrl()!=null){
                redirect(new UserActivity());
            }
            else {
                redirect(new ProfileDetailsActivity());
            }
        }
        setTheme(R.style.Base_Theme_Bingchat);
        setContentView(R.layout.activity_main);
        this.resetPass=findViewById(R.id.reset_password);
        resetPass.setOnClickListener(v->{
            showDialogInput();
        });
        this.progressDialog=new ProgressDialog(this);
        this.progressDialog2=new ProgressDialog(this);
        AppCompatButton login = findViewById(R.id.loginButton);
        TextView signupText = findViewById(R.id.textView2);
        this.inputEmail=findViewById(R.id.inputEmail);
        this.inputPass=findViewById(R.id.inputPass);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String email=inputEmail.getEditText().getText().toString();
                String password=inputPass.getEditText().getText().toString();
                if(isValid(email,password)) {
                    progressDialog.show();
                    login(email, password);
                }
            }
        });

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirect(new SignupActivity());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public boolean isValid(String email, String password){
        boolean check=true;
        if(TextUtils.isEmpty(email)){
            inputEmail.setError("Email cannot be empty");
            check=false;
        }
        else{
            inputEmail.setError(null);
        }
        if(TextUtils.isEmpty(password)){
            inputPass.setError("Password cannot be empty");
            check=false;
        }
        else{
            inputPass.setError(null);
        }
        return check;
    }
   public void login(String email, String pass) {
        this.auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast toastSuccess=Toast.makeText(getApplicationContext(), "Successfully login", Toast.LENGTH_SHORT);
                    toastSuccess.show();
                    progressDialog.dismiss();
                    redirect(new UserActivity());
                }else{
                    progressDialog.dismiss();
                    Toast toastfailled=Toast.makeText(getApplicationContext(), "Failed to login", Toast.LENGTH_SHORT);
                    toastfailled.show();
                    return;
                }
            }
        });
    }
    public void showDialogInput(){
        View inputView= LayoutInflater.from(this).inflate(R.layout.input_dialog,null);
        TextView textTitle=inputView.findViewById(R.id.input_heading);
        textTitle.setText("Enter your email");
        TextInputEditText inputText=inputView.findViewById(R.id.inputText);
        inputText.requestFocus();
        inputText.setHint("Type here..");
        MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(this);
        builder.setView(inputView);
        builder.setPositiveButton("Ok", (dialogInterface, i) -> {
            String email=inputText.getText().toString();
            progressDialog2.setMessage("Sending email...");
            progressDialog2.show();
            if(isEmailValid(email) && email!=null){
                sendPasswordRecovery(email);
            }else{
                progressDialog2.dismiss();
                showDialogInput();
                inputText.setError("Invalid email");
            }
        });
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputText.setError(null);
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        AlertDialog dialog=builder.create();
        dialog.show();
    }
    public void redirect(Activity userUi){
        Intent intent=new Intent(getApplicationContext(),userUi.getClass());
        startActivity(intent);
        finish();
    }
    public boolean isEmailValid(CharSequence sequence){
        return (sequence!=null && Patterns.EMAIL_ADDRESS.matcher(sequence).matches());
    }
    public void sendPasswordRecovery(String email){
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            progressDialog2.dismiss();
            showDialog("Please check the email we have send to you");
        }).addOnFailureListener(e -> {
            progressDialog2.dismiss();
            showDialog("Failed while sending email");
        });
    }
    public void showDialog(String message){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(message);
        builder.setPositiveButton("Ok", (dialogInterface, i) -> {
            dialogInterface.cancel();
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }
}