package com.krancki.test.bluetoothapp;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;

import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;

import android.content.Context;
import android.content.Intent;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Xml;

import com.krancki.test.bluetoothapp.ConnectionActivity.ConnectionModel;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

import java.util.UUID;

/**
 * Created by Krzysztof on 2018-12-14.
 */

public class Connect {
    private static final Connect ourInstance = new Connect();
    private final static int REQUEST_ENABLE_BT = 1;

    private static AppCompatActivity appCompatActivity;
    // Only for handlers
    private static ConnectionModel connectionModel;

    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothGatt bluetoothGatt;
    private BluetoothManager bluetoothManager;


    private LinkedList<BluetoothDevice> bluetoothDevices = new LinkedList<>();
    private  BluetoothDevice selectedDevice;

    private  int CONNECTION_STATE= BluetoothGatt.STATE_DISCONNECTED;




    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    public static UUID UUID_MACHINE =
            UUID.fromString("19B10001-E8F2-537E-4F6C-D104768A1212");

    private final static String TAG = "cdv";

 //  Created collback for connections
     BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {


            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                setConnectionState(BluetoothGatt.STATE_CONNECTING);
                intentAction = ACTION_GATT_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:" + bluetoothGatt.discoverServices());


            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
                closeConnection();
                setConnectionState(BluetoothGatt.STATE_DISCONNECTED);
            }
        }

        @Override
        // New services discovered
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            Log.i(TAG, "onServicesDiscovered received: " + status );
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                setConnectionState(BluetoothGatt.STATE_CONNECTED);
                sendReadyServices();
            } else {
                Log.i(TAG,"onServicesDiscovered received: " + status);
            }
        }

     // Result of a characteristic read operation
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {


            if (status == BluetoothGatt.GATT_SUCCESS) {


                byte [] data = characteristic.getValue();
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                ByteBuffer byteBuffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

                String uuid = characteristic.getUuid().toString().toLowerCase();
                String value= "";

               if(uuid.contains("00002A00".toLowerCase())){

                   value = new String(data);
               }
               else if(uuid.contains("00002A01".toLowerCase())){

                   value = getCategory(byteBuffer.getShort(0));
               }
               else if(uuid.contains("00002A04".toLowerCase()) && data.length>=7){


                       int min = byteBuffer.getShort(0);
                       int max = byteBuffer.getShort(2);
                       int latency = byteBuffer.getShort(4);
                       int timeout = byteBuffer.getShort(6);


                   value = getPeripheralPreferredConnection(min,max,latency,timeout);

               }
               else{


                    try{
                        stringBuilder.append("\n Int:"+String.valueOf(byteBuffer.getInt()));
                    }catch(Exception e){
                        stringBuilder.append("Value is not a Integer");
                    }

                   value = stringBuilder.toString();
               }





                ConnectionModel.sendReadDataToView(value);


            }
        }

        @Override
        public void onCharacteristicWrite (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){

            if(status==BluetoothGatt.GATT_SUCCESS){

                Log.i("Bluetooth:","Sending data" );
            }
        }





 };



    // Singleton
    private Connect() {}

    public static Connect getInstance(AppCompatActivity appCompatActivity) {
        Connect.appCompatActivity = appCompatActivity;


        return ourInstance;
    }

    // Initialization
    public boolean initialize(){
        if(bluetoothManager ==null){
            bluetoothManager = (BluetoothManager)  appCompatActivity.getSystemService(Context.BLUETOOTH_SERVICE);
            if(bluetoothManager==null){
                return false;
            }


        bluetoothAdapter = bluetoothManager.getAdapter();
        if( bluetoothAdapter==null){
            return false;
        }
    }
        return true;
    }


    // Set enable Bluetooth
    private void enableBluetooth(){
        if(!bluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            appCompatActivity.startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
        }
    }


    // Load Bonded Bluetooth Devices
    private void loadBondedDevices(){
        bluetoothDevices.clear();
        bluetoothDevices.addAll(bluetoothAdapter.getBondedDevices());
    }

    // Get Bonded Bluetooth Devices
    public LinkedList<BluetoothDevice> getFoundDevice(){
            loadBondedDevices();
        return bluetoothDevices;
    }




    // Select device whith one you want to connect
    public boolean setSelectedDevice(BluetoothDevice device){
        selectedDevice=device;

       return selectedDevice!=null?true:false;
    }


    // Get selected Device Name
    public String getSelectedDeviceName(){

        if(selectedDevice!=null){
        return selectedDevice.getName();
        }
        return "Error";
    }

    // Method for checking device
    public boolean checkDevice(BluetoothDevice bluetoothDevice){

        if(bluetoothDevice.getUuids()==null) {
            return true;
        }else{
            return false;
        }

    }



    // Helpful method informing about bluetooth status
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        appCompatActivity.sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile. Data
        // parsing is carried out as per profile specifications.
        if (UUID_MACHINE.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
               // ConnectionModel.console(  "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
              //  ConnectionModel.console(  "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
          //  ConnectionModel.console(  String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" +
                        stringBuilder.toString());
            }
        }
        appCompatActivity.sendBroadcast(intent);
    }

    // Open connection with device
    public void openConnection(){
        enableBluetooth();

        bluetoothGatt = selectedDevice.connectGatt(appCompatActivity,false, bluetoothGattCallback);

    }

    // Close connection with device
    public void disconnect(){
        if(isConnected()) {
            bluetoothGatt.disconnect();
            setConnectionState(BluetoothGatt.STATE_DISCONNECTING);
        }
    }

    public void closeConnection(){
        if(isConnected()) {
            bluetoothGatt.close();
            bluetoothGatt=null;

        }

    }

    public boolean isConnected() {
        if(bluetoothGatt== null || CONNECTION_STATE == BluetoothGatt.STATE_DISCONNECTED){
            return false;
        }
        return true;
    }

    // Set CONNECTION_STATE and transfer state to Model
    private  void setConnectionState(int connectionState) {
        CONNECTION_STATE = connectionState;
        ConnectionModel.sendStateToView(connectionState);
    }


    // After Services Discovered
    private void sendReadyServices(){
        ConnectionModel.addService( bluetoothGatt.getServices());
    }


    public void read(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
    bluetoothGatt.readCharacteristic(bluetoothGattCharacteristic);

    }

    public void write(BluetoothGattCharacteristic bluetoothGattCharacteristic){
        bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
    }


    static String getPeripheralPreferredConnection(int min,int max, int latency,int timeout){

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Connection Interval:"+min*1.25+"ms -"+max*1.25+"ms, \n Slave Latency:"+latency+",\nSupervision Timeout Multiplier: "+timeout);

        return stringBuilder.toString();
    }
    static String getCategory(int number){
        String text;



        switch(number){
            case 0:  text = "Unknown";                                  break;
            case 64:  text = "Phone";                                    break;
            case 128:  text = "Computer";                                 break;
            case 192:  text = "Watch";                                    break;
            case 193:  text = "Watch: Sports Watch";                    break;
            case 256:  text = "Generic Clock";                                  break;
            case 320:  text = "Generic Display";                           break;
            case 384:  text = "Generic Remote Control";                              break;
            case 448:  text = "Generic Eye-glasses";                                      break;
            case 512:  text = "Generic Tag";                                  break;
            case 576: text = "Generic Keyring";                             break;
            case 640: text = "Generic Media Player";                          break;
            case 704: text = "Generic Barcode Scanner";                              break;
            case 768: text = "Generic Thermometer";                        break;
            case 769: text = "Thermometer: Ear";                           break;
            case 832: text = "Generic Heart rate Sensor";             break;
            case 833: text = "Heart Rate Sensor: Heart Rate Belt";                            break;
            case 896: text = "Generic Blood Pressure";                   break;
            case 897: text = "Blood Pressure: Arm";                                  break;
            case 898: text = "Blood Pressure: Wrist";                           break;
            case 960: text = "Human Interface Device (HID)";                             break;
            case 961: text = "Keyboard";                 break;
            case 962: text = "Mouse";               break;
            case 963: text = "Joystick";                             break;
            case 964: text = "Gamepad";                      break;
            case 965: text = "Digitizer Tablet";                  break;
            case 966: text = "Card Reader";                  break;
            case 967: text = "Digital Pen";                  break;
            case 968: text = "Barcode Scanner";                  break;
            case 1024: text = "Generic Glucose Meter";                  break;
            case 1088: text = "Generic: Running Walking Sensor";                  break;
            case 1089: text = "Running Walking Sensor: In-Shoe";                  break;
            case 1090: text = "Running Walking Sensor: On-Shoe";                                 break;
            case 1091: text = "Running Walking Sensor: On-Hip";                                  break;
            case 1152: text = "Generic: Cycling";                                   break;
            case 1153: text = "Cycling: Cycling Computer";                                   break;
            case 1154: text = "Cycling: Speed Sensor";                                   break;
            case 1155: text = "Cycling: Cadence Sensor";                                break;
            case 1156: text = "Cycling: Power Sensor";                                   break;
            case 1157: text = "Cycling: Speed and Cadence Sensor";                                  break;
            case 3136: text = "Generic: Pulse Oximeter";                                 break;
            case 3137: text = "Fingertip";                                   break;
            case 3138: text = "Wrist Worn";                                  break;




            default: text = "Reserved";                                 break;
        }

        return "["+String.valueOf(number)+"]"+text;
    }



}
