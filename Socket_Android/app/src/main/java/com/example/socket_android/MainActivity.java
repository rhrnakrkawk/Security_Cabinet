package com.example.socket_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socket_android.Model.SocketInfo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    SocketInfo socketInfo;
    SocketConnection socketConnection;
    String password="";
    String inputPassword = "";
    Button btn_0;Button btn_1;Button btn_2;Button btn_3;Button btn_4;
    Button btn_5;Button btn_6;Button btn_7;Button btn_8;Button btn_9;
    Button btn_asterisk; Button btn_poundKey;
    Button btn_enterAdmin;
    TextView tv_password;
    TextView tv_status;
    long KeyPressedTime = 0;
    boolean isFirst;
    boolean isOpen = false;
    private View decorView;
    private int	uiOption;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        socketInfo = new SocketInfo();
        socketInfo.setHost("192.168.200.110");
        try {
            socketConnection = new SocketConnection(socketInfo);
            socketConnection.connect();
            socketInfo.setSocket(socketConnection.getConnection());
            Toast.makeText(getApplicationContext(), "Connected!!.", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Unconnected", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }


//        try {
//            socketConnection.setSendData("LED");
//            socketConnection.SocketInfo(socketInfo);
//            Thread thread = new Thread(socketConnection.traffic);
//            thread.start();
//            Log.d("SocketHost", "led_btn SendThread Start");
//            thread.join(100);
//            Log.d("SocketHost", "led_btn SendThread Join " + socketConnection.getSendData());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        decorView = getWindow().getDecorView();
        uiOption = getWindow().getDecorView().getSystemUiVisibility();
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH )
            uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
            uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decorView.setSystemUiVisibility( uiOption );
        tv_password = findViewById(R.id.tv_password);
        tv_status = findViewById(R.id.tv_status);
        btn_poundKey = findViewById(R.id.btn_poundKey);
        btn_enterAdmin = findViewById(R.id.btn_enterAdmin);
        btn_0 = findViewById(R.id.btn_0);
        btn_1 = findViewById(R.id.btn_1);
        btn_2 = findViewById(R.id.btn_2);
        btn_3 = findViewById(R.id.btn_3);
        btn_4 = findViewById(R.id.btn_4);
        btn_5 = findViewById(R.id.btn_5);
        btn_6 = findViewById(R.id.btn_6);
        btn_7 = findViewById(R.id.btn_7);
        btn_8 = findViewById(R.id.btn_8);
        btn_9 = findViewById(R.id.btn_9);
        btn_asterisk = findViewById(R.id.btn_asterisk);

        btn_0.setOnClickListener(clickNum);
        btn_1.setOnClickListener(clickNum);
        btn_2.setOnClickListener(clickNum);
        btn_3.setOnClickListener(clickNum);
        btn_4.setOnClickListener(clickNum);
        btn_5.setOnClickListener(clickNum);
        btn_6.setOnClickListener(clickNum);
        btn_7.setOnClickListener(clickNum);
        btn_8.setOnClickListener(clickNum);
        btn_9.setOnClickListener(clickNum);

        try{
            FileInputStream is = openFileInput("password.txt");
            byte[] result = new byte[1024];
            int resultLength = is.read(result);
            if(resultLength == -1) {
                tv_status.setText("(초기설정)\n비밀번호를 입력하고 #을 눌러주세요");
                isFirst = true;
            }
            else {
                isFirst = false;
                password = new String(result, "utf-8").substring(0, resultLength);
            }
        }
        catch (IOException e){
            tv_status.setText("(초기설정)\n비밀번호를 입력하고 #을 눌러주세요");
            isFirst = true;
            Log.i("err", String.valueOf(e));
        }

        btn_poundKey.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(isFirst){
                    fileWrite(inputPassword, "password.txt");
                    tv_status.setText("설정완료!!");
                    isFirst = false;
                    tv_password.setText("");
                    password = inputPassword;
                    inputPassword = "";

                    new Thread(){
                        public void run(){
                            Integer i = 3;
                            while(i != 0){
                                SystemClock.sleep(1000);
                                i--;
                            }
                            tv_status.setText("");
                        }
                    }.start();
                    return;
                }
                if(isOpen){
                    sendLED(0);
                    tv_status.setText("CLOSE!");
                    new Thread(){
                        public void run(){
                            Integer i = 3;
                            while(i != 0){
                                SystemClock.sleep(1000);
                                i--;
                            }
                            tv_status.setText("");
                        }
                    }.start();
                    isOpen = false;
                    btn_0.setVisibility(View.VISIBLE);
                    btn_1.setVisibility(View.VISIBLE);
                    btn_2.setVisibility(View.VISIBLE);
                    btn_3.setVisibility(View.VISIBLE);
                    btn_4.setVisibility(View.VISIBLE);
                    btn_5.setVisibility(View.VISIBLE);
                    btn_6.setVisibility(View.VISIBLE);
                    btn_7.setVisibility(View.VISIBLE);
                    btn_8.setVisibility(View.VISIBLE);
                    btn_9.setVisibility(View.VISIBLE);
                    btn_asterisk.setVisibility(View.VISIBLE);
                    return;
                }
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String getTime = sdf.format(date);
                String fileName = "History.txt";
                String oldStr = fileRead(fileName);
                if(inputPassword.equals(password)){
                    //Open Door
                    tv_status.setTextColor(Color.parseColor("#4CAF50"));
                    tv_status.setText("OPEN");
                    tv_password.setText("");
                    inputPassword = "";
                    sendLED(1);
                    isOpen = true;
                    btn_0.setVisibility(View.INVISIBLE);
                    btn_1.setVisibility(View.INVISIBLE);
                    btn_2.setVisibility(View.INVISIBLE);
                    btn_3.setVisibility(View.INVISIBLE);
                    btn_4.setVisibility(View.INVISIBLE);
                    btn_5.setVisibility(View.INVISIBLE);
                    btn_6.setVisibility(View.INVISIBLE);
                    btn_7.setVisibility(View.INVISIBLE);
                    btn_8.setVisibility(View.INVISIBLE);
                    btn_9.setVisibility(View.INVISIBLE);
                    btn_asterisk.setVisibility(View.INVISIBLE);
//                    try {
//                        socketConnection.setSendData("LED");
//                        socketConnection.SocketInfo(socketInfo);
//                        Thread thread = new Thread(socketConnection.traffic);
//                        thread.start();
//                        Log.d("SocketHost", "led_btn SendThread Start");
//                        thread.join(100);
//                        Log.d("SocketHost", "led_btn SendThread Join " + socketConnection.getSendData());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }


                    fileWrite(oldStr + getTime+"/"+"Success,", fileName);
                    new Thread(){
                        public void run(){
                            Integer i = 3;
                            while(i != 0){
                                SystemClock.sleep(1000);
                                i--;
                            }
                            tv_status.setText("");
                        }
                    }.start();
                }
                else{
                    tv_status.setTextColor(Color.parseColor("#FF0000"));
                    tv_status.setText("Fail!");
                    tv_password.setText("");
                    inputPassword = "";
                    fileWrite(oldStr + getTime+"/"+"Fail,", fileName);
                    new Thread(){
                        public void run(){
                            Integer i = 3;
                            while(i != 0){
                                SystemClock.sleep(1000);
                                i--;
                            }
                            tv_status.setText("");
                        }
                    }.start();
                }
            }
        });


        btn_enterAdmin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() > KeyPressedTime + 2000) {
                    KeyPressedTime = System.currentTimeMillis();
                    return;
                }
                // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
                // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지나지 않았으면 종료
                if (System.currentTimeMillis() <= KeyPressedTime + 2000) {
                    //다른 화면으로 이동
                    Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                    startActivity(intent);
                }
            }
        });

//         connect_btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//            public void onClick(View v) {
//                socketInfo.setHost(String.valueOf(host_txt.getText()));
//                try {
//                    socketConnection = new SocketConnection(socketInfo);
//                    socketConnection.connect();
//                    socketInfo.setSocket(socketConnection.getConnection());
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });


    }
    public void sendLED(int state){
        try {
            if(state == 1)
                socketConnection.setSendData("LED_ON");
            else if(state == 0)
                socketConnection.setSendData("LED_OFF");
            socketConnection.SocketInfo(socketInfo);
            Thread thread = new Thread(socketConnection.traffic);
            thread.start();
            Log.d("SocketHost", "led_btn SendThread Start");
            thread.join(100);
            Log.d("SocketHost", "led_btn SendThread Join " + socketConnection.getSendData());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            Log.i("err", "error");
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

    Button.OnClickListener clickNum= new View.OnClickListener() {
        public void onClick(View v) {
            Button btn_num = findViewById(v.getId());
            String clickedNum = (String) btn_num.getText();
            inputPassword += clickedNum;
            tv_password.setText(tv_password.getText() + "*");
        }
    };
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
    @Override
    protected void onUserLeaveHint() {
        //super.onUserLeaveHint();

	/*
		이벤트 작성
	*/
    }
}