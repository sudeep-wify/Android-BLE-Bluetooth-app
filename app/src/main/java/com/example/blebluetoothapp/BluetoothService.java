package com.example.blebluetoothapp;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BluetoothService extends Service {
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
//        @Override
//        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//            if (newState == STATE_CONNECTED) {
//                // successfully connected to the GATT Server
//                connectionState = STATE_CONNECTED;
//                broadcastUpdate(ACTION_GATT_CONNECTED);
//                // Attempts to discover services after successful connection.
//                bluetoothGatt.discoverServices();
//            } else if (newState == STATE_DISCONNECTED) {
//                // disconnected from the GATT Server
//                connectionState = STATE_DISCONNECTED;
//                broadcastUpdate(ACTION_GATT_DISCONNECTED);
//            }
//        }
//    };

}
