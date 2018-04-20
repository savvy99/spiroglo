package com.kavi.spiroglo;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

public class BluetoothSender implements OutputHandler {

    private static final String TAG = "BluetoothSender";

    private MainActivity mainActivity;
    private BluetoothAdapter btAdapter;

    private final BroadcastReceiver btEnabledReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

            }
        }
    };

    private final BroadcastReceiver btPairingReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(btDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "device " + btDevice.getName() + " " + btDevice.getAddress() + " bonded");
                }
                if(btDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "device " + btDevice.getName() + " " + btDevice.getAddress() + " is bonding");
                }
                if(btDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "device " + btDevice.getName() + " " + btDevice.getAddress() + " not bonded");
                }
            }
        }
    };

    public BluetoothSender(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void enableBt() {
        if(!btAdapter.isEnabled()) {
            Intent btEnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            checkBtPermissions();
            mainActivity.startActivity(btEnableIntent);
            IntentFilter btEnabledFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            mainActivity.registerReceiver(btEnabledReceiver, btEnabledFilter);


            //IntentFilter btIntent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            //mainActivity.registerReceiver(btPairingReceiver, btIntent);
        }
    }

    private void find
    private void checkBtPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = mainActivity.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += mainActivity.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if(permissionCheck != 0) {
                mainActivity.requestPermissions(
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        10);
            }
        }
    }

    @Override
    public void processOutput(double speed, RotationalDirection direction) {

    }
}
