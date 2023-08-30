package com.example.pairedbluetoothlist;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DiscoverActivity extends AppCompatActivity {
    Button btnDiscover;
    private CardView cvList;
    private RecyclerView rvList;
    private ListAdapter adapter;
    Set<BluetoothDevice> pairedDevices;

    private ArrayList list = new ArrayList();
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    //  private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final UUID MY_UUID = UUID.fromString("0000110A-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descover);

        btnDiscover = findViewById(R.id.btnDiscover);
        cvList = findViewById(R.id.cvList);
        rvList = findViewById(R.id.devicelist);


        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (Build.VERSION.SDK_INT >= 31) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        } else {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(DiscoverActivity.this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_DENIED) {
                    if (Build.VERSION.SDK_INT >= 31) {
                        ActivityCompat.requestPermissions(DiscoverActivity.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 100);
                        return;
                    }
                }
                IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                registerReceiver(receiver, intentFilter);
                bluetoothAdapter.startDiscovery();
            }
        });
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);


        adapter = new ListAdapter(list, new RecylerviewCliCK() {
            @Override
            public void itemClick(String s) {
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {

                        // BluetoothDevice device = ... // Get the device to connect to
                        if (ActivityCompat.checkSelfPermission(DiscoverActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        try {
                            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                            socket.connect();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });

        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(adapter);

    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equalsIgnoreCase(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(DiscoverActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                list.add(device.getName());
            }
        }
    };


}

