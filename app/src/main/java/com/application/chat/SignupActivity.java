package com.application.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.application.chat.Dialogs.ProgressDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    TextInputLayout inputEmail,inputPass,inputConfirm;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        this.auth=FirebaseAuth.getInstance();
        this.progressDialog=new ProgressDialog(this);
        AppCompatButton signup= findViewById(R.id.signupButton);
        this.inputEmail = findViewById(R.id.inputEmail);
        this.inputPass = findViewById(R.id.inputPass);
        this.inputConfirm = findViewById(R.id.inputConfirm);
        TextView backText=findViewById(R.id.signBack);
        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String email=inputEmail.getEditText().getText().toString();
                String password=inputPass.getEditText().getText().toString();
                String confirm=inputConfirm.getEditText().getText().toString();
                if(validate(email,password,confirm)){
                    progressDialog.show();
                    signup(email,password);
                } else
                    return;
            }
        });
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirect(new MainActivity());
            }
        });
    }
    public boolean validate(String email,String password,String confirmPass){
        boolean check=true;
        if(email.isEmpty()){
            this.inputEmail.setError("Email is required");
            check=false;
        }
        else{
            this.inputEmail.setError(null);
        }
        if(password.isEmpty()){
            this.inputPass.setError("Password is required");
            check=false;
        }
        else{
            this.inputPass.setError(null);
        }
        if(confirmPass.isEmpty()){
            this.inputConfirm.setError("Confirm password is required");
            check=false;
        }
        else{
            this.inputConfirm.setError(null);
        }
        if(!password.equals(confirmPass)){
            this.inputConfirm.setError("Password didn't match");
            check=false;
        }
        else
            this.inputConfirm.setError(null);

        return check;
    }
    public void signup(String email,String password) {
        this.auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast toastSuccess = Toast.makeText(getApplicationContext(), "Successfully signup", Toast.LENGTH_SHORT);
                    toastSuccess.show();
                    redirect(new ProfileDetailsActivity());

                } else {
                    Toast toastfailled = Toast.makeText(getApplicationContext(), "something wrong", Toast.LENGTH_SHORT);
                    toastfailled.show();
                    progressDialog.dismiss();
                }
            }
        });
    }
    public void redirect(Activity userUi){
        Intent intent = new Intent(getApplicationContext(), userUi.getClass());
        startActivity(intent);
        finish();
    }
}