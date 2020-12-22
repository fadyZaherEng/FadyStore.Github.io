package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class SignInActivity extends AppCompatActivity {

    @BindView(R.id.signInLogo)
    ImageView signInLogo;
    @BindView(R.id.signInEmail)
    EditText signInEmail;
    @BindView(R.id.signInPassword)
    EditText signInPassword;
    @BindView(R.id.signIn)
    Button signIn;
    @BindView(R.id.reg_signIn)
    TextView regSignIn;
    @BindView(R.id.progress)
    ProgressBar progress;
    FirebaseAuth auth;
    @BindView(R.id.showOrhidePasswordIn)
    ImageView showOrhidePasswordIn;
    @BindView(R.id.ForgotPass)
    TextView ForgotPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();
        showAndHidePasswordMethod();
        forgetPasswordMethod();
    }

    private void forgetPasswordMethod() {
      ForgotPass.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent resetPasswordIntent=new Intent(getBaseContext(),ResetPassword.class);
              startActivity(resetPasswordIntent);
          }
      });
    }

    private void showAndHidePasswordMethod() {

        showOrhidePasswordIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signInPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                    showOrhidePasswordIn.setImageResource(R.drawable.ic_show_pass);
                    //Show Password
                    signInPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    showOrhidePasswordIn.setImageResource(R.drawable.ic_hide_pass);
                    //Hide Password
                    signInPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        //check box to do hide or show password
//        checkShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    signInPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                    checkShowPassword.setText("Hide Password");
//                } else {
//                    signInPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    checkShowPassword.setText("Show Password");
//                }
//            }
//        });
    }

    @OnClick({R.id.signIn, R.id.reg_signIn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.signIn:
                SignInFun();
                break;
            case R.id.reg_signIn:
                Intent SignUp = new Intent(this, SignUpActivity.class);
                startActivity(SignUp);
                break;
        }
    }

    private void SignInFun() {
        String Email = signInEmail.getText().toString();
        String Password = signInPassword.getText().toString();
        if (!Email.isEmpty() && !Password.isEmpty()) {
            progress.setVisibility(View.VISIBLE);
            auth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progress.setVisibility(View.GONE);
                        verifyEmailAddress();
                    } else {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(SignInActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(SignInActivity.this, R.string.MassageEmpty, Toast.LENGTH_LONG).show();
        }
    }

    private void verifyEmailAddress() {
        FirebaseUser user = auth.getCurrentUser();
        if (user.isEmailVerified()) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.massageVerify, Toast.LENGTH_LONG).show();
        }
    }

}