package com.example.blebluetoothapp;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "Bluetooth";
    Context context = this;
    Button btnTurnOnBl, connectButton;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothGatt bluetoothGatt;
    AudioManager audioManager;
    private String deviceAddress = "YOUR_BLE_DEVICE_ADDRESS";
    private ListView deviceListView;
    private List<BluetoothDevice> availableDevices = new ArrayList<>();
    private Handler handler = new Handler();
    private boolean scanning = false;


    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "Bluetooth Scan onConnectionStateChange: callback() ");

//                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Connected to BLE Device", Toast.LENGTH_SHORT).show());

                // Discover services on the connected device
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                }
                bluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "Bluetooth Scan  onConnectionStateChange: callback() ");

//                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Disconnected from BLE Device", Toast.LENGTH_SHORT).show());
            }
        }
    };


    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            if (!availableDevices.contains(device)) {
                availableDevices.add(device);
                updateDeviceList();
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            // Handle scan failure
            Toast.makeText(MainActivity.this, "Scan failed with error code: " + errorCode, Toast.LENGTH_SHORT).show();
        }
    };

    AudioDeviceCallback audioDeviceCallback = new AudioDeviceCallback() {
        @Override
        public void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
        }

        @Override
        public void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
            // Handle device removal
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTurnOnBl = findViewById(R.id.btnTurnOnBl);
        connectButton = findViewById(R.id.connectButton);
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        btnTurnOnBl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    }
                    startActivityForResult(intent, 101);
                }
            }
        });


        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToDevice();
//                startDeviceScan();

            }
        });

        requestLocationPermission();

    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 102);
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN}, 102);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            // Start scanning for BLE devices
        }
    }

    private void startDeviceScan() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
        }
        if (!scanning) {
            scanning = true;
            availableDevices.clear();
            updateDeviceList();

            bluetoothLeScanner.startScan(scanCallback);

            // Stop scanning after a certain duration (e.g., 10 seconds)
            handler.postDelayed(() -> {
                stopDeviceScan();
            }, 10000);
        }
    }

    private void stopDeviceScan() {
        if (scanning) {
            scanning = false;
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            }
            bluetoothLeScanner.stopScan(scanCallback);
        }
    }

    private void updateDeviceList() {
        Log.d(TAG, "Bluetooth Scan  updateDeviceList: ");
        List<String> deviceNames = new ArrayList<>();
        for (BluetoothDevice device : availableDevices) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            }
            deviceNames.add(device.getName() != null ? device.getName() : "Unknown Device");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceNames);

        deviceListView.setAdapter(adapter);
    }

    private void connectToDevice() {
        Log.d(TAG, "Bluetooth connectToDevice: get called");
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice("B4:9A:95:5A:1E:06");
        if (bluetoothAdapter != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            }

        }
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
        }
        Log.d(TAG, "Bluetooth Scan  connectToDevice: before connectGatt() ");
        bluetoothGatt = device.connectGatt(context,false, gattCallback);
        String uuid = Arrays.toString(device.getUuids());
        if (isBleService(uuid)) {
            Toast.makeText(context, "Connected Device is BLE", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Connected Device is not BLE", Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "Bluetooth Scan  connectToDevice: after connectGatt() ");

    }

    private boolean isBleService(String uuid) {
        // Example BLE service UUIDs
        return uuid.equals("00001801-0000-1000-8000-00805f9b34fb") ||  // Generic Attribute Profile (GATT)
                uuid.equals("00001800-0000-1000-8000-00805f9b34fb"); // Generic Access Profile (GAP)
        // Add more BLE service UUIDs as needed
    }

}