package com.example.socket_android;

import android.system.ErrnoException;
import android.util.Log;

import com.example.socket_android.Model.SocketInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

public class SocketConnection extends Thread{
    private Socket socket;
    private SocketInfo socketInfo;
    private String RcvData;
    private String SendData;

    public SocketConnection(SocketInfo socketInfo) throws IOException {
        this.socketInfo = socketInfo;
    }

    public Socket getConnection() {
        return this.socket;
    }

    public void SocketInfo(SocketInfo socketInfo) {
        this.socketInfo = socketInfo;
    }

    public String getRcvData(){ return this.RcvData; }
    public void setRcvData(String RcvData){ this.RcvData = RcvData; }

    public String getSendData(){ return this.SendData; }
    public void setSendData(String SendData){ this.SendData = SendData + '\0'; }



    public SocketInfo getSocketInfo(){ return this.socketInfo; }

    public void connect() {
        this.socket = new Socket();
            new Thread(() -> {
                try { socket.connect(new InetSocketAddress(socketInfo.getHost(), socketInfo.getPort())); }
                catch (Exception e) { e.printStackTrace(); }
            }).start();
        Log.d("SocketHost", "SocketHost: " + socketInfo.getHost() + ":" + socketInfo.getPort());
    }

    Runnable traffic = new Runnable() {
        @Override
        public void run() {
            try {
                byte[] byteCommand = null;
                OutputStream outputStream = socket.getOutputStream();
                byteCommand = getSendData().getBytes("UTF-8");
                outputStream.write(byteCommand, 0, byteCommand.length);
                outputStream.flush();
                Log.d("byteSnd", "byte: " + new String(byteCommand) + "   " + byteCommand.length + "\nString: " + socketInfo.getSend());

                InputStream inputStream = socket.getInputStream();
                byte[] byteRcv = new byte[512];
                int readByteCount = inputStream.read(byteRcv);
                setRcvData(new String(byteRcv, 0, readByteCount, "UTF-8"));
                Log.d("SocketHost", "rcvThread: " + getRcvData());
                Arrays.fill(byteRcv, (byte) 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Runnable Rcv = new Runnable() {
        @Override
        public void run() {
            try{
                InputStream inputStream = socket.getInputStream();
                byte[] byteRcv = new byte[512];
                int readByteCount = inputStream.read(byteRcv);
                setRcvData(new String(byteRcv, 0, readByteCount, "UTF-8"));
                Log.d("SocketHost", "rcvThread: " + getRcvData());
                Arrays.fill(byteRcv, (byte) 0);
            }catch (Exception e){e.printStackTrace();}
        }
    };
}