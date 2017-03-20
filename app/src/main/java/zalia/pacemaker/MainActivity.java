package zalia.pacemaker;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.ParcelUuid;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Spinner;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static zalia.pacemaker.R.id.connect_button;


public class MainActivity extends AppCompatActivity {

    private MainActivity context = this;
    private int REQUEST_ENABLE_BT = 87;
    private static final String TAG = "Pacemaker";
    private static final UUID HEARTBEAT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final boolean ONLY_SHHOW_HEARTBEAT_DEVICES = false;
//    private String heartbeat_mac = "98:D3:31:FB:21:45";
    private String heartbeat_mac = null;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    private ProgressDialog progress = null;
    private AlertDialog paired_dialog = null;
    private AlertDialog discovered_dialog = null;
    private ArrayList<String> arrayOfFoundBTDevices;
    private BroadcastReceiver bt_scan_receiver = null;

    private PacemakerMode active_mode;
    //keep references to each mode to keep configurations
    private ColorPickerMode color_picker_mode;
    private FullRainbowMode rainbow_mode;
    private MeteorMode comet_mode;
    private EffectMode effect_mode;
    private CreativeMode creative_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "in onCreate() ...");
        setContentView(R.layout.activity_main);

        //setup mode selection dropdown menu
        Spinner spinner = (Spinner) findViewById(R.id.modes_dropdown);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int index, long id) {
                //load current mode's layout
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                switch (index) {
                    default:
                    case 0:
                        //"Konstant"
//                        if(color_picker_mode == null) color_picker_mode = new ColorPickerMode();
                        color_picker_mode = new ColorPickerMode();
                        active_mode = color_picker_mode;
                        break;
                    case 1:
                        //"Regenbogen"
                        if(rainbow_mode == null) rainbow_mode = new FullRainbowMode();
                        active_mode = rainbow_mode;
                        break;
                    case 2:
                        //"Komet"
//                        if(comet_mode == null) comet_mode = new MeteorMode();
                        comet_mode = new MeteorMode();
                        active_mode = comet_mode;
                        break;
                    case 3:
                        //"Test Mode"
                        if(effect_mode == null) effect_mode = new EffectMode();
                        active_mode = effect_mode;
                        break;
                    case 4:
                        //"Creative Mode"
//                        if(creative_mode == null) creative_mode = new CreativeMode();
                        creative_mode = new CreativeMode();
                        active_mode = creative_mode;
                        break;
                }
                ft.replace(R.id.frame_layout, active_mode);
                ft.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //initiate Bluetooth adapter
        btAdapter = BluetoothAdapter.getDefaultAdapter();
//        checkBTState();
        enableConnect();
    }

    // Check for Bluetooth support, check state and prompt user to turn it on if its not
    private void checkBTState() {
        // Emulator doesn't support Bluetooth and will return null
        if (btAdapter == null) {
            enableConnect();
            toast("Fatal Error: Bluetooth Not supported. Aborting.");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "Bluetooth is enabled");
                if (heartbeat_mac != null) {
                    bt_connect();
                } else {
                    bt_show_paired_devices();
                }
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG, "in onPause() ...");
        try{
            btAdapter.cancelDiscovery();
        }catch(Exception e){
            Log.d(TAG, "Exception while attempting to cancel discovery. It propably was not active in which case everything is fine.");
        }
        try{
            discovered_dialog.dismiss();
        }catch(NullPointerException npe){
            Log.d(TAG, "Exception while attempting to dismiss discovery dialog. It propably was not active in which case everything is fine.");
        }
        try{
            paired_dialog.dismiss();
        }catch(Exception e){
            Log.d(TAG, "Exception while attempting to dismiss paired devices dialog. It propably was not active in which case everything is fine.");
        }
        try{
            progress.dismiss();
        }catch(Exception e){
            Log.d(TAG, "Exception while attempting to dismiss processing dialog. It propably was not active in which case everything is fine.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(bt_scan_receiver);
        }
        catch(Exception e){
            Log.d(TAG, "Failed to unregister Bluetooth Scan receiver. It propably was not active in which case everything is fine.");
        }
        try{
            btAdapter.cancelDiscovery();
        }catch(Exception e){
            Log.d(TAG, "Exception while attempting to cancel discovery. It propably was not active in which case everything is fine.");
        }
        try{
            discovered_dialog.dismiss();
        }catch(NullPointerException npe){
            Log.d(TAG, "Exception while attempting to dismiss discovery dialog. It propably was not active in which case everything is fine.");
        }
        try{
            paired_dialog.dismiss();
        }catch(Exception e){
            Log.d(TAG, "Exception while attempting to dismiss paired devices dialog. It propably was not active in which case everything is fine.");
        }
        try{
            progress.dismiss();
        }catch(Exception e){
            Log.d(TAG, "Exception while attempting to dismiss processing dialog. It propably was not active in which case everything is fine.");
        }
    }

    //handle results from bluetooth activation prompt and initiate connection if possible
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "in onActivityResult() ...");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == 0) {
                // If the resultCode is 0, the user selected "No" when prompt to
                // allow the app to enable bluetooth.
                Log.i(TAG, "User declined bluetooth access!");
                enableConnect();
            } else {
                Log.i(TAG, "User allowed bluetooth access!");
                if (heartbeat_mac != null) {
                    bt_connect();
                } else {
                    bt_show_paired_devices();
                }
            }
        }
    }

    //wrapper for actual bt_connect to make async stuff easier
    private void bt_connect(){
        AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                progress = ProgressDialog.show(context, "Verbindungsversuch", "Bluetooth Verbindung wird hergestellt..", true);
            }

            @Override
            protected Boolean doInBackground(String... params) {
                return bt_connect_inner();
            }

            @Override
            protected void onPostExecute(Boolean isConnected) {
                if(isConnected){
                    toast("Verbindung erfolgreich hergestellt.");
                    try{
                        discovered_dialog.dismiss();
                    }catch(NullPointerException npe){
                        Log.d(TAG, "Exception while attempting to dismiss discovery dialog. It propably was not active in which case everything is fine.");
                    }
                    try{
                        paired_dialog.dismiss();
                    }catch(Exception e){
                        Log.d(TAG, "Exception while attempting to dismiss paired devices dialog. It propably was not active in which case everything is fine.");
                    }
                    disableConnect();
                }else{
                    toast("Verbindungsversuch fehlgeschlagen.");
                    enableConnect();
                    heartbeat_mac = null;
                }
                progress.dismiss();
            }
        }.execute("");
//        progress = ProgressDialog.show(this, "Bluetooth Verbindung", "Bluetooth Verbindung wird hergestellt..", true);
//        bt_connect_inner();
//        progress.dismiss();
    }

    //connection attempt to the device corresponding to the mac stored in heartbeat_mac
    private boolean bt_connect_inner() {
        try {
            Log.d(TAG, "Attempting client connect...");

            // Set up a pointer to the remote node using it's address.
            BluetoothDevice device = btAdapter.getRemoteDevice(heartbeat_mac);

            // Two things are needed to make a connection:
            //   A MAC address, which we got above.
            //   A Service ID or UUID.  In this case we are using the UUID for SPP.
            try {
                btSocket = device.createRfcommSocketToServiceRecord(HEARTBEAT_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Fatal Error: Failed to create BT socket: " + e.getMessage() + ".");
                return false;
            }

            Log.d(TAG, "Connecting to Remote...");
            try {
                btSocket.connect();
                Log.d(TAG, "Connection established and data link opened...");


            } catch (IOException e) {
//                toast("Verbindungsversuch fehlgeschlagen");
                try {
                    btSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "Fatal Error: Unable to close socket during connection failure: " + e2.getMessage() + ".");
                }
                return false;
            }

            // Create a data stream so we can talk to server.
            Log.d(TAG, "Creating Socket...");

            try {
                outStream = btSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Fatal Error: Output stream creation failed: " + e.getMessage() + ".");
                return false;
            }
        } catch (NullPointerException e) {
            Log.w(TAG, "Warning: Bluetooth not active/supported or connection broken");
        }
        return true;
    }

    //get all known bt devices, compile them into a list and display a dialog
    private void bt_show_paired_devices() {

        Log.d(TAG, "Showing list of paired devices...");

        List<String> arrayOfAlreadyPairedBTDevices = null;
        // Query paired devices
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        // If there are any paired devices
        if (pairedDevices.size() > 0) {
            arrayOfAlreadyPairedBTDevices = new ArrayList<String>();
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                ParcelUuid[] uuids = device.getUuids();
                boolean compatible = false;
                if(uuids != null) {
                    for (ParcelUuid uuid : uuids) {
                        if (uuid.equals(HEARTBEAT_UUID)) {
                            compatible = true;
                        }
                    }
                }
                if(ONLY_SHHOW_HEARTBEAT_DEVICES) {
                    if (compatible) {
                        arrayOfAlreadyPairedBTDevices.add(device.getName() + " / " + device.getAddress());
                    } else {
                        Log.d(TAG, "Found incompatible device in list of known bluetooth devices.");
                    }
                }else{
                    arrayOfAlreadyPairedBTDevices.add(device.getName() + " / " + device.getAddress());
                }
            }
        } else {
            //there are no paired devices so we skip this and directly go to discovery
            bt_discover();
            enableConnect();
            return;
        }

        //at this point we have a list of paired devices that needs to be displayed
        final CharSequence[] known_devices = arrayOfAlreadyPairedBTDevices.toArray(new String[arrayOfAlreadyPairedBTDevices.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Bekannte Geräte:")
                .setItems(known_devices, null)
                .setPositiveButton("Neue Geräte suchen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        paired_dialog.dismiss();
                        bt_init_discovery();
                    }
                })
                .setNeutralButton("Abbrechen", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        paired_dialog.dismiss();
                        enableConnect();
                    }
                });
        paired_dialog = builder.create();
        //setting the onClickListener after creation, allows for the possibility to keep the
        //dialog active after an item was selected
        paired_dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedText = known_devices[position].toString();
                String[] tmp = selectedText.split("/");
                heartbeat_mac = (tmp[tmp.length-1]).trim();
                bt_connect();
            }
        });
        paired_dialog.setCancelable(false);
        paired_dialog.show();
    }

    private void bt_init_discovery()
    {
        // Discover new devices
        // Create a BroadcastReceiver for ACTION_FOUND
        bt_scan_receiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();
                // When discovery finds a device
                if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                    progress.dismiss();
                    Log.d(TAG, "Finished Bluetooth discovery and found " + arrayOfFoundBTDevices.size() + " devices");
                    final CharSequence[] found_devices = arrayOfFoundBTDevices.toArray(new String[arrayOfFoundBTDevices.size()]);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle("Gefundene Geräte:")
                            .setItems(found_devices, null)
                            .setPositiveButton("Suche wiederholen", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    discovered_dialog.dismiss();
                                    bt_discover();
                                }
                            })
                            .setNeutralButton("Abbrechen", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    discovered_dialog.dismiss();
                                    unregisterReceiver(bt_scan_receiver);
                                    enableConnect();
                                }
                            });
                    discovered_dialog = builder.create();
                    discovered_dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String selectedText = found_devices[position].toString();
                                String[] tmp = selectedText.split("/");
                                heartbeat_mac = (tmp[tmp.length-1]).trim();
                                bt_connect();
                            }
                    });
                    discovered_dialog.setCancelable(false);
                    discovered_dialog.show();
                } else if (BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    // Get the bluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    ParcelUuid[] uuids = device.getUuids();
                    boolean compatible = false;
                    if(uuids != null) {
                        for (ParcelUuid uuid : uuids) {
                            if (uuid.equals(HEARTBEAT_UUID)) {
                                compatible = true;
                            }
                        }
                    }
                    if(ONLY_SHHOW_HEARTBEAT_DEVICES) {
                        if (compatible) {
                            Log.d(TAG, "Found compatible Bluetooth device during discovery! Expanding list to " + arrayOfFoundBTDevices.size() + " devices.");
                            arrayOfFoundBTDevices.add(device.getName() + " / " + device.getAddress());
                        } else {
                            Log.d(TAG, "Found incompatible Bluetooth device during discovery.");
                        }
                    } else{
                        arrayOfFoundBTDevices.add(device.getName() + " / " + device.getAddress());
                    }
                } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    enableConnect();
                    toast("Bluetooth Verbindung wurde getrennt.");
                }
            }
        };
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(bt_scan_receiver, filter);
        bt_discover();
    }

    private void bt_discover(){
        // start looking for bluetooth devices
        Log.d(TAG, "Discovering new Bluetooth devices...");
        arrayOfFoundBTDevices = new ArrayList<String>();
        progress = ProgressDialog.show(this, "Bluetooth Entdeckung", "Kompatible Geräte in der Nähe werden gesucht..", true);
        btAdapter.startDiscovery();
    }

    //toggles commit button to connect button
    private void enableConnect() {
        //setup commit button
        Button connect_button = (Button) findViewById(R.id.connect_button);
        connect_button.setText("Verbindung herstellen");
//        toast("Set button text to 'Verbinden'");
        connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBTState();
            }
        });
    }

    //toggles connect button to commit button
    private void disableConnect() {
        Button connect_button = (Button) findViewById(R.id.connect_button);
        connect_button.setText("Verbindung trennen");
//        toast("Set button text to 'Übernehmen'");
        connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try { outStream.close(); } catch (IOException e) { }
                outStream = null;
                try{ btSocket.close(); } catch (IOException e) { }
                btSocket = null;
                enableConnect();
            }
        });
    }

    //send the config string of the currently active PacemakerMode via bluetooth to the heartbeat device
    protected void send_config(String config) {
        Log.d(TAG, "Config generiert: '" + config + "'");
        if(btSocket != null) {
            try {
                byte[] msgBuffer = config.getBytes();
                try {
                    outStream.write(msgBuffer);
                } catch (IOException e) {
                    toast("Fatal Error: " + e.getMessage());
                }
            } catch (NullPointerException e) {
                toast("Fatal Error: Bluetooth not active or not supported");
            }
        }
    }

    //returns black if color is light and white if color is dark
    protected int find_viable_ui_color(int color){
        double y = (299 * Color.red(color) + 587 * Color.green(color) + 114 * Color.blue(color)) / 1000;
        return y >= 128 ? Color.BLACK : Color.WHITE;
    }

    //display debug mesages on the device
    protected void toast(String text) {
        Log.d("Toast", text);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    //utility method for all fragments that use progress bars
    protected static int normalize_progress(int progress, int min, int max) {
        double result = (double) min + ((double) max - (double) min) / (100 / (double) progress);
        return (int) Math.round(result);
    }
}
