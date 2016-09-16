package com.example.tamil.androstick;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends Activity {
    Set<BluetoothDevice> pairedDevices;
    private static final String TAG = "THINBTCLIENT";
    private static final boolean D = true;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    Button b1,b2,b3;
    ListView lv;
    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = "XX:XX:XX:XX:XX:XX";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b1 = (Button) findViewById(R.id.button1);
        b2=(Button)findViewById(R.id.button2);
        b3=(Button)findViewById(R.id.button3);
        lv = (ListView)findViewById(R.id.listView);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this,
                    "Bluetooth is not available.",
                    Toast.LENGTH_LONG).show();


        }

        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this,
                    "Please enable your BT and re-run this program.",
                    Toast.LENGTH_LONG).show();
            on();
            visible();
        }

    }

   @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();


        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "ON RESUME: Socket creation failed.", e);
        }

        mBluetoothAdapter.cancelDiscovery();

        try {
            btSocket.connect();
            Log.e(TAG, "ON RESUME: BT connection established, data transfer link open.");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                Log.e(TAG,
                        "ON RESUME: Unable to close socket during connection failure", e2);
            }
        }

        // Create a data stream so we can talk to server.


        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
        }

        String message = "Hello message from client to server.";
        byte[] msgBuffer = message.getBytes();
        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            Log.e(TAG, "ON RESUME: Exception during write.", e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (D)
            Log.e(TAG, "- ON PAUSE -");

        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                Log.e(TAG, "ON PAUSE: Couldn't flush output stream.", e);
            }
        }

        try     {
            btSocket.close();
        } catch (IOException e2) {
            Log.e(TAG, "ON PAUSE: Unable to close socket.", e2);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    public void on(View v){
        if (!mBluetoothAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(),"Turned on",Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Already on", Toast.LENGTH_LONG).show();
        }
    }


    public  void visible(){
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public void list(View v){
        pairedDevices =mBluetoothAdapter.getBondedDevices();
        ArrayList list1 = new ArrayList();

        for(BluetoothDevice bt: pairedDevices)
            list1.add(bt.getName());
        Toast.makeText(getApplicationContext(),"Showing Paired Devices",Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list1);
        lv.setAdapter(adapter);
    }
}
