package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResetPassword extends AppCompatActivity {

    @BindView(R.id.resetEmail)
    EditText resetEmail;
    @BindView(R.id.btn_resetPass)
    Button btnResetPass;
    @BindView(R.id.goToReg)
    TextView goToReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.btn_resetPass, R.id.goToReg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_resetPass:
                String userEmail=resetEmail.getText().toString();
                if (userEmail.isEmpty()){
                    Toast.makeText(getApplicationContext(), R.string.resetEmptyMsg, Toast.LENGTH_LONG).show();
                    return;
                }
                FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), R.string.resetEmailMsgSuccess, Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(getBaseContext(),SignInActivity.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(getApplicationContext(), R.string.resetEmailMsgFail, Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
            case R.id.goToReg:
                Intent goReg=new Intent(this,SignUpActivity.class);
                startActivity(goReg);
                break;
        }
    }
}