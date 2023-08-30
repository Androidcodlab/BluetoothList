package com.example.pairedbluetoothlist;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    private TextView tvSwitchText;
    private SwitchCompat btnSwitch;
    private CardView cvList;
    private RecyclerView rvList;
    private ListAdapter adapter;
    private ArrayList<Model> list = new ArrayList<>();
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSwitchText = findViewById(R.id.tvswitchText);
        btnSwitch = findViewById(R.id.btnSwitch);
        cvList = findViewById(R.id.cvList);
        rvList = findViewById(R.id.devicelist);

        adapter = new ListAdapter(list, s -> {
        });
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(adapter);


        btnSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
                    if (Build.VERSION.SDK_INT >= 31) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
                        return;
                    }
                }
                bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                if (Build.VERSION.SDK_INT >= 31) {
                    bluetoothAdapter = bluetoothManager.getAdapter();
                } else {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                }
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

                if (pairedDevices.size() > 0) {
                    list.clear();
                    for (BluetoothDevice device : pairedDevices) {
                        Model bluetooth = new Model();
                        bluetooth.name = device.getName();
                        list.add(bluetooth);
                    }
                    adapter.notifyDataSetChanged();
                }
                //check self permisson
                cvList.setVisibility(View.VISIBLE);
                tvSwitchText.setText("Show List");
            } else {

                tvSwitchText.setText("Hide List");
                cvList.setVisibility(View.GONE);
            }


        });
    }


}