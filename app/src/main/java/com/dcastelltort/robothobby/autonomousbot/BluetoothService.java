package com.dcastelltort.robothobby.autonomousbot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Created by dcastelltort on 26/02/17.
 */

public class BluetoothService {

    private static final String TAG = "BluetoothService";
    private static final UUID robotUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //default HC-06

    private BluetoothAdapter mBluetoothAdapter;
    private String mRobotBTHardwareAddress;
    private BluetoothConnectedThread connectedThread;

    BluetoothService(String robotBTHardwareAddress) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mRobotBTHardwareAddress = robotBTHardwareAddress;
    }


    // will return the BT device matching the one of the robot
    // Note: BT device of the robot must already be paired.
    BluetoothDevice findRobotBTDevice() {

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                Log.d(TAG, "is device ? : " + device.getName() + " / " + device.getAddress());

                if (device.getAddress().equals(mRobotBTHardwareAddress)) {
                    Log.d(TAG, device.getAddress() + " found !");
                    return device;
                }
            }
        }

        return null;
    }

    BluetoothSocket connectDevice(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        BluetoothSocket socket = null;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(robotUUID);
            socket = tmp;
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }

        // Cancel discovery because it otherwise slows down the connection.
        mBluetoothAdapter.cancelDiscovery();

        if(socket == null) return null;

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            socket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                socket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);

            } finally {
                socket = null;
            }
        }

        return socket;
    }

    Boolean Initialize() {
        BluetoothDevice robotBT = findRobotBTDevice();
        Boolean initSuccess = false;
        if (robotBT != null) {
            BluetoothSocket socket = connectDevice(robotBT);
            if (socket != null) {
                connectedThread = new BluetoothConnectedThread(socket);
                connectedThread.start();
                initSuccess = true;
            }
        }
        return (initSuccess);
    }


    public void write(byte[] bytes) {
        if (connectedThread != null) {
            connectedThread.write(bytes);
        } else {
            Log.d(TAG, "write: but not connected");
        }
    }
}
