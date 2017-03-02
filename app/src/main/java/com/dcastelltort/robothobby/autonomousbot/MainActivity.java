package com.dcastelltort.robothobby.autonomousbot;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    //Some constants
    private static final String ROBOT_HW_ADDRESS = "20:16:11:17:84:68"; //Bad Hardcode
    private static final String TAG = "MainActivity";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int FRAMERATE = 30;

    // members
    private BluetoothService bluetoothService;
    private Timer loopTimer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BluetoothAdapter mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        Boolean deviceHasAdapter = mBTAdapter != null;
        if (!deviceHasAdapter) {
            Log.d(TAG, "no bluetooth adapter");
        }

        if (deviceHasAdapter) {
            if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

            //init bluetooth manager
            bluetoothService = new BluetoothService(ROBOT_HW_ADDRESS);

            //initialize, try to find the robot
            Boolean bInitSuccess = bluetoothService.Initialize();
            Log.d(TAG, bInitSuccess ? "BluetoothManager Init Success" : "BluetoothManager Init Fail");
            byte[] msg = new byte[1];
            msg[0] = (byte)'f';
            bluetoothService.write(msg);
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        // initiate timer to call c++ at given frame for processing
        loopTimer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                String outputCommands = coreProcess();
                if (outputCommands.isEmpty() == false) {
                    Log.d("TAG", outputCommands);
                }
            }
        } , 0 , 1000 / FRAMERATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult (int requestCode,
                           int resultCode,
                           Intent data) {

        switch(requestCode) {
            case REQUEST_ENABLE_BT:
                Log.d(TAG, resultCode !=RESULT_OK? "BT Enabled":"BT Disabled");
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String coreProcess();
}
