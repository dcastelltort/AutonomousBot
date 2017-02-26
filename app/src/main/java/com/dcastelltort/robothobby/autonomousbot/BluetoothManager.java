package com.dcastelltort.robothobby.autonomousbot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import java.util.Set;

/**
 * Created by dcastelltort on 26/02/17.
 */

public class BluetoothManager {

    private static final String TAG = "BluetoothManager";

    private BluetoothAdapter mBluetoothAdapter;
    private String mRobotBTHardwareAddress;

    BluetoothManager(String robotBTHardwareAddress) {
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

    Boolean Initialize() {
        BluetoothDevice robotBT = findRobotBTDevice();
        return (robotBT != null);
    }
}
