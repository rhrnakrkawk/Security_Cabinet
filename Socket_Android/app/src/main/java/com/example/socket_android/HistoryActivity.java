package com.example.socket_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class HistoryActivity extends AppCompatActivity {
    EditText et_password;
    String password;
    Button btn_confirm;
    Button btn_findFail;
    Button btn_findSuccess;
    Button btn_findAll;
    Button btn_reset;
    TextView tv_history;
    LinearLayout ll_bottom;
    String[] historyArr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        password = fileRead("password.txt");

        et_password = findViewById(R.id.et_password);

        btn_confirm = findViewById(R.id.btn_confirm);
        btn_findFail = findViewById(R.id.btn_findFail);
        btn_findSuccess = findViewById(R.id.btn_findSuccess);
        btn_findAll = findViewById(R.id.btn_findAll);
        btn_reset = findViewById(R.id.btn_reset);

        tv_history = findViewById(R.id.tv_history);

        ll_bottom = findViewById(R.id.ll_bottom);


        btn_findAll.setOnClickListener(clickSort);
        btn_findSuccess.setOnClickListener(clickSort);
        btn_findFail.setOnClickListener(clickSort);
        btn_confirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String pass = et_password.getText().toString();
                if(pass.equals(password)){
                    String histroyStr = fileRead("History.txt");
                    if(histroyStr.equals("")){
                        Toast.makeText(getApplicationContext(), "기록이 없습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    historyArr = fileRead("History.txt").split(",");
                    tv_history.setVisibility(View.VISIBLE);
                    String setStr = "";
                    setStr = String.join("\n", historyArr);
                    tv_history.setText(setStr);
                    ll_bottom.setVisibility(View.VISIBLE);
                }
                else{
                    et_password.setText(null);
                    Toast.makeText(getApplicationContext(), "잘못된 비밀번호입니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                tv_history.setText(null);
                fileWrite("", "History.txt");
                setButtonEnable(false, false, false);
            }
        });
    }

    Button.OnClickListener clickSort= new View.OnClickListener() {
        public void onClick(View v) {
            String setStr;
            if(v.getId() == R.id.btn_findAll){
                setStr = String.join("\n", historyArr);
                setButtonEnable(false, true, true);
            }
            else if(v.getId() == R.id.btn_findSuccess) {
                setStr = conditionSort('S');
                setButtonEnable(true, false, true);
            }
            else
            {
                setStr = conditionSort('F');
                setButtonEnable(true, true, false);
            }
            tv_history.setText(setStr);
        }
    };
    public String conditionSort(char c){
        String totalStr = "";
        for(int i = 0 ; i < historyArr.length; i++){
            if(historyArr[i].charAt(historyArr[i].indexOf("/") + 1) == c)
                totalStr += historyArr[i]+"\n";
        }
        return totalStr;

    }
    public void setButtonEnable(boolean x, boolean y, boolean z){
        btn_findAll.setEnabled(x);
        btn_findSuccess.setEnabled(y);
        btn_findFail.setEnabled(z);
    }
    public String fileRead(String fileName){
        String str = "";
        try{
            FileInputStream is = openFileInput(fileName);
            byte[] result = new byte[1024];
            int resultLength = is.read(result);
            if(resultLength != -1) {
                str = new String(result, "utf-8").substring(0, resultLength);
            }
        }
        catch (IOException e){
        }
        return str;
    }

    public void fileWrite(String data, String fileName){
        try{
            FileOutputStream os  = openFileOutput(fileName, MODE_PRIVATE);
            os.write(data.getBytes());
            os.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}