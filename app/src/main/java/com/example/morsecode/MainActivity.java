package com.example.morsecode;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.Morsecode";


    final Handler handler = new Handler();
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("OhYes")) { //Note, you will need to change this to match the name of your device{
                    mmDevice = device;
                    break;
                }
            }
        }







        final Button buttonOn = (Button) findViewById(R.id.sendButton);
        buttonOn.setOnClickListener (new View.OnClickListener() {
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.editText);

                String message = editText.getText().toString();
                (new Thread(new WorkerThread(message))).start();
            }
        });


        final Button buttonDot = (Button) findViewById(R.id.dotButton);
        buttonDot.setOnClickListener (new View.OnClickListener() {
            public void onClick(View v) {

                (new Thread(new WorkerThread("."))).start();
            }
        });


        final Button buttonDash = (Button) findViewById(R.id.dashButton);
        buttonDash.setOnClickListener (new View.OnClickListener() {
            public void onClick(View v) {

                (new Thread(new WorkerThread("-"))).start();
            }
        });


    }


    public void sendBtMsg(String msg) {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fc"); //Standard SerialPortService ID
        try {
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            if (!mmSocket.isConnected()) {
                mmSocket.connect();
            }
            OutputStream mmOutputStream = mmSocket.getOutputStream();
            mmOutputStream.write(msg.getBytes());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    final class WorkerThread implements Runnable {
        private String btMsg;

        public WorkerThread(String msg) {
            btMsg = msg;
        }


        public void run() {


            sendBtMsg(btMsg);


        }
    }
}