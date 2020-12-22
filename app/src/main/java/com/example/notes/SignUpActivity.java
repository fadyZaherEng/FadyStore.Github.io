package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.signUpLogo)
    ImageView signUpLogo;
    @BindView(R.id.signUPEmail)
    EditText signUPEmail;
    @BindView(R.id.signUPPassword)
    EditText signUPPassword;
    @BindView(R.id.signUp)
    Button signUp;
    FirebaseAuth auth;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.showOrhidePassword)
    ImageView showOrhidePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();
        showAndHidePasswordMethod();
    }

    private void showAndHidePasswordMethod() {
        showOrhidePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signUPPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    showOrhidePassword.setImageResource(R.drawable.ic_hide_pass);
                    //Show Password
                    signUPPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    showOrhidePassword.setImageResource(R.drawable.ic_show_pass);
                    //Hide Password
                    signUPPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        //second way to show/hide password
//        checkShowpassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    signUPPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                    checkShowpassword.setText("Hide Password");
//                } else {
//                    signUPPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    checkShowpassword.setText("Show Password");
//                }
//            }
//        });
    }

    @OnClick(R.id.signUp)
    public void onViewClicked() {
        if (!signUPEmail.getText().toString().isEmpty() && !signUPPassword.getText().toString().isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            auth.createUserWithEmailAndPassword(signUPEmail.getText().toString(), signUPPassword.getText().toString()).
                    addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                sendEmailVerification();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(SignUpActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        } else {
            Toast.makeText(SignUpActivity.this, R.string.MassageEmpty, Toast.LENGTH_LONG).show();
        }
    }

    private void sendEmailVerification() {
        FirebaseUser user = auth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignUpActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
