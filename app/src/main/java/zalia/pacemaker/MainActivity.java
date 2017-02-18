package zalia.pacemaker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.Spinner;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import static android.R.attr.enabled;
import static android.R.attr.mode;
import static android.R.id.message;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Pacemaker";
    private static final UUID HEARTBEAT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String heartbeat_mac = "98:D3:31:FB:21:45";
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    private PacemakerMode active_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup mode selection dropdown menu
        Spinner spinner = (Spinner) findViewById(R.id.modes_dropdown);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int index, long id) {

                //load current mode's layout
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                switch(index){
                    case 0:
                        active_mode = new EffectMode();
                        break;
                    case 1: default:
                        active_mode = new FullRainbowMode();
                        break;
                    case 2:
                        active_mode = new ColorPickerMode();
                        break;
                }
                ft.replace(R.id.frame_layout, active_mode);
                ft.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //setup bluetooth connection
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        //setup commit button
        Button commit_button = (Button) findViewById(R.id.commit_button);
        commit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_config(active_mode.generate_configs());
            }
        });

    }

    /*
    Bluetooth code from:
    http://digitalhacksblog.blogspot.de/2012/05/arduino-to-android-turning-led-on-and.html
     */
    @Override
    public void onResume() {
        super.onResume();

        try {
            Log.d(TAG, "...In onResume - Attempting client connect...");

            // Set up a pointer to the remote node using it's address.
            BluetoothDevice device = btAdapter.getRemoteDevice(heartbeat_mac);

            // Two things are needed to make a connection:
            //   A MAC address, which we got above.
            //   A Service ID or UUID.  In this case we are using the
            //     UUID for SPP.
            try {
                btSocket = device.createRfcommSocketToServiceRecord(HEARTBEAT_UUID);
            } catch (IOException e) {
                errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
            }

            // Discovery is resource intensive.  Make sure it isn't going on
            // when you attempt to connect and pass your message.
            btAdapter.cancelDiscovery();

            // Establish the connection.  This will block until it connects.
            Log.d(TAG, "...Connecting to Remote...");
            try {
                btSocket.connect();
                Log.d(TAG, "...Connection established and data link opened...");
            } catch (IOException e) {
                try {
                    btSocket.close();
                } catch (IOException e2) {
                    errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                }
            }

            // Create a data stream so we can talk to server.
            Log.d(TAG, "...Creating Socket...");

            try {
                outStream = btSocket.getOutputStream();
            } catch (IOException e) {
                errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
            }
        }catch(NullPointerException e){
            errorExit("Fatal Error", "NullPointer in onResume(): Bluetooth not active or not supported");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        try{
            Log.d(TAG, "...In onPause()...");

            if (outStream != null) {
                try {
                    outStream.flush();
                } catch (IOException e) {
                    errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
                }
            }

            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
            }
        } catch(NullPointerException e){
            errorExit("Fatal Error", "NullPointer in onPause(): Bluetooth not active or not supported");
        }
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on

        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth Not supported. Aborting.");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, " ...Bluetooth is enabled...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void send_config(String config){
        toast("Config commited: '" + config + "'");
        try{
            byte[] msgBuffer = config.getBytes();

            Log.d(TAG, "...Sending data: " + config + "...");

            try {
                outStream.write(msgBuffer);
            } catch (IOException e) {
                String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
                msg = msg +  ".\n\nCheck that the SPP UUID: " + HEARTBEAT_UUID.toString() + " exists on server.\n\n";
                errorExit("Fatal Error", msg);
            }
        } catch(NullPointerException e){
            errorExit("Fatal Error", "NullPointer in send_config(): Bluetooth not active or not supported");
        }
    }

    private void errorExit(String title, String message){
        Toast msg = Toast.makeText(getBaseContext(),
                title + " - " + message, Toast.LENGTH_SHORT);
        msg.show();
        //finish();
    }

    //display debug mesages on the device
    public void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    //utility method for all fragments that use progress bars
    public static int normalize_progress(int progress, int min, int max){
        double dmin = min;
        double dmax = max;
        double dprog = progress;
        double result = dmin + (dmax-dmin)/(100/dprog);
        return (int) Math.round(result);
    }

}
