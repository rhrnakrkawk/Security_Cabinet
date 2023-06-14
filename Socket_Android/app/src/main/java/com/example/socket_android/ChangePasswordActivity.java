package com.example.socket_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText et_oriPassword;
    EditText et_newPass;
    EditText et_newPassAgain;

    Button btn_oriConfirm;
    Button btn_change;

    ConstraintLayout cll_newPassword;
    String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        et_oriPassword = findViewById(R.id.et_oriPassword);
        et_newPass = findViewById(R.id.et_newPass);
        et_newPassAgain = findViewById(R.id.et_newPassAgain);

        btn_oriConfirm = findViewById(R.id.btn_oriConfirm);
        btn_change = findViewById(R.id.btn_change);

        cll_newPassword = findViewById(R.id.cll_newPassword);

        try{
            FileInputStream is = openFileInput("password.txt");
            byte[] result = new byte[1024];
            int resultLength = is.read(result);
            password = new String(result, "utf-8").substring(0, resultLength);
        }
        catch (IOException e){
            Log.i("err", String.valueOf(e));
        }

        btn_oriConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String str = et_oriPassword.getText().toString();
                if(str.equals(password)){
                    cll_newPassword.setVisibility(View.VISIBLE);
                }
                else{
                    Toast.makeText(getApplicationContext(), "비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show();
                    et_oriPassword.setText(null);
                }
            }
        });
        btn_change.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String newPass = et_newPass.getText().toString();
                String newPassAgain = et_newPassAgain.getText().toString();
                if(newPass.equals(newPassAgain)){
                    Toast.makeText(getApplicationContext(), "비밀번호가 변경됐습니다.", Toast.LENGTH_SHORT).show();
                    try{
                        FileOutputStream os  = openFileOutput("password.txt", MODE_PRIVATE);
                        os.write(newPass.getBytes());
                        os.close();
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                    PackageManager packageManager = getPackageManager();
                    Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
                    ComponentName componentName = intent.getComponent();
                    Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                    startActivity(mainIntent);
                    System.exit(0);
//                    cll_newPassword.setVisibility(View.INVISIBLE);
//                    et_oriPassword.setText(null);
//                    et_newPass.setText(null);
//                    et_newPassAgain.setText(null);
                }
                else{
                    Toast.makeText(getApplicationContext(), "비밀번호가 불일치합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}