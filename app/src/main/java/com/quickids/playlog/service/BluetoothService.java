package com.quickids.playlog.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BluetoothService implements Serializable {

    private static final long serialVersionUID = 1L;
    private static BluetoothService appBluetoothService = null;

    Context context;
    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> mPairedDevices;
    ArrayList<String> mListPairedDevices;

    BluetoothDevice mBluetoothDevice;
    BluetoothSocket mBluetoothSocket;

    public Handler mBluetoothHandler;
    public ConnectedBluetoothThread mThreadConnectedBluetooth;

    public final static int BT_REQUEST_ENABLE = 1;
    public final static int BT_MESSAGE_READ = 2;
    public final static int BT_CONNECTING_STATUS = 3;
    // Serial UUID
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static BluetoothService getInstance(Context context){
        if (appBluetoothService == null){
            appBluetoothService = new BluetoothService(context);
        }

        return appBluetoothService;
    }

    public void setContext(Context context){
        this.context = context;
    }

    private BluetoothService(Context context){
        this.context = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mBluetoothHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == BT_MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public boolean exists(){
        if(mBluetoothAdapter == null){
            return false;
        }
        return true;
    }

    public boolean isEnabled(){
        if(mBluetoothAdapter == null){
            return false;
        }
       return mBluetoothAdapter.isEnabled();
    }

    public Intent enable(){
        return new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    }

    public void disable(){
        if(mBluetoothAdapter != null){
            mBluetoothAdapter.disable();
        }
    }

    public ArrayList<String> getPairedDevices(){
        mListPairedDevices = new ArrayList<>();

        if (!isEnabled()) {
            mListPairedDevices.add("블루투스를 활성화 해 주세요.");
        }
        else{
            mPairedDevices = mBluetoothAdapter.getBondedDevices();
            if (mPairedDevices.size() > 0){
                for (BluetoothDevice device : mPairedDevices) {
                    mListPairedDevices.add(device.getName());
                    //mListPairedDevices.add(device.getName() + "\n" + device.getAddress());
                }
            }
            else{
                mListPairedDevices.add("페어링된 장치가 없습니다.");
            }
        }

        return mListPairedDevices;
    }

    public boolean connectSelectedDevice(String selectedDeviceName) {
        for(BluetoothDevice tempDevice : mPairedDevices) {
            if (selectedDeviceName.equals(tempDevice.getName())) {
                mBluetoothDevice = tempDevice;
                break;
            }
        }

        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(BT_UUID);
            mBluetoothSocket.connect();
            mThreadConnectedBluetooth = new ConnectedBluetoothThread(mBluetoothSocket);
            mThreadConnectedBluetooth.start();
            mBluetoothHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1).sendToTarget();
        } catch (IOException e) {
            Log.i("Error", "IOException");
            return false;
        }
        return true;
    }

    public void sendMsg(String input){
        if(mThreadConnectedBluetooth != null) {
            if(input == "end"){
                mThreadConnectedBluetooth.cancel();
                try {
                    mBluetoothSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                mThreadConnectedBluetooth.write(input);
            }
        }
    }

    public class ConnectedBluetoothThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedBluetoothThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(context, "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes);
                        mBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
        public void write(String str) {
            byte[] bytes = str.getBytes();
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Toast.makeText(context, "데이터 전송 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
        public void cancel() {
            try {
                if(mmInStream != null){
                    mmInStream.close();
                }
                if(mmOutStream != null){
                    mmOutStream.close();
                }
                if(mmSocket != null){
                    mmSocket.close();
                }
            } catch (IOException e) {
                Toast.makeText(context, "소켓 해제 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
