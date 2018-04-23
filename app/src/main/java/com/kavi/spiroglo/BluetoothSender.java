package com.kavi.spiroglo;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;

public class BluetoothSender implements OutputHandler {

    private static final String TAG = "BluetoothSender";

    private static final UUID BT_UUID_INSECURE = UUID.fromString("889b4c44-4738-11e8-842f-0ed5f89f718b");

    private MainActivity mainActivity;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice btDevice;

    private ConnectThread connectThread;
    private SendSession sendSession;

    public BluetoothSender(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        enableBt();
        findDevice();
        startSender();
    }

    private void enableBt() {
        if(!btAdapter.isEnabled()) {
            Intent btEnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            checkBtPermissions();
            mainActivity.startActivity(btEnableIntent);
        }
    }

    private void findDevice() {
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                //String deviceHardwareAddress = device.getAddress(); // MAC address
                if("RNBT-9762".equals(deviceName)) {
                    btDevice = device;
                }
            }
        }
    }

    void startSender() {
        Log.d(TAG, "startClient");
        if(connectThread != null) {
            connectThread.cancel();
        }
        connectThread = new ConnectThread();
        Executors.newSingleThreadExecutor().submit(connectThread);
    }

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

    private class ConnectThread implements Runnable {
        private BluetoothSocket btSocket;
        ConnectThread() {
            Log.d(TAG, "ConnectThread: started");
        }

        @Override
        public void run() {
            BluetoothSocket tmp = null;
            Log.i(TAG, "run: ConnectThread running");
            try {
                Log.d(TAG, "run: ConnectThread: Trying to create InsecureRfCommSocket using UUID");
                btDevice.getUuids();
                tmp = btDevice.createInsecureRfcommSocketToServiceRecord(BT_UUID_INSECURE);
            }
            catch(IOException ex) {
                Log.e(TAG, "run: ConnectThread exception" + ex.getMessage());
            }
            btSocket = tmp;
            btAdapter.cancelDiscovery();

            try {
                btSocket.connect();
                Log.d(TAG, "run: ConnectThread connected");
            }
            catch(IOException ex) {
                Log.e(TAG, "run: ConnectThread error: "+ ex.getMessage());
                try {
                    btSocket.close();
                }
                catch(IOException exx) {
                    Log.e(TAG, "run: ConnectThread: closing socket error " + exx.getMessage());
                }
                Log.d(TAG, "run: ConnectThread: Could not connect to UUID: " + BT_UUID_INSECURE);
            }

            sendSession = new SendSession(btSocket);
        }

        void cancel() {
            Log.d(TAG, "cancel: Cancelling ConnectThread");
            try {
                btSocket.close();
            }
            catch(IOException ex) {
                Log.e(TAG, "cancel: ConnectThread: IOException: " + ex.getMessage());
            }
        }
    }

    private class SendSession {

        private final BluetoothSocket btSocket;
        private final OutputStream outputStream;

        SendSession(BluetoothSocket socket) {
            Log.d(TAG, "ConnectedThread: Starting:");
            btSocket = socket;

            OutputStream tmpOut = null;
            try {
                tmpOut = btSocket.getOutputStream();
            }
            catch(IOException ex) {
                Log.e(TAG, "ConnectedThread: Error created output stream");
            }
            outputStream = tmpOut;

        }

        void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            }
            catch(IOException ex) {
                Log.e(TAG, "write: ConnectedThread: could not write to output stream " + ex.getMessage());
            }
        }

        void close() {
            try {
                btSocket.close();
            }
            catch(IOException ex) {
                Log.e(TAG, "close; ConnectedThread: could not close socket " + ex.getMessage());
            }
        }
    }

    @Override
    public void processOutput(double speed, RotationalDirection direction) {
        if(direction == RotationalDirection.ANTI_CLOCKWISE) {
            speed = -speed;
        }
        sendSession.write(ByteBuffer.allocate(8).putDouble(speed).array());
    }
}
