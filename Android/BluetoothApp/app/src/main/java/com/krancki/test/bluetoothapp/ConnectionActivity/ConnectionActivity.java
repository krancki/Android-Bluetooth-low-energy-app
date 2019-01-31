package com.krancki.test.bluetoothapp.ConnectionActivity;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import android.widget.Spinner;
import android.widget.TextView;


import com.krancki.test.bluetoothapp.R;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public class ConnectionActivity extends AppCompatActivity {


    private ConnectionController connectionController;
    private ConnectionModel connectionModel;

    //View Bar elements
    private TextView connectTitleBar;

    // View elements
    private ScrollView scrollView;
    private Button connectBtn;


    private ProgressBar progressBar;
    private LinearLayout linearLayoutForController;


    //List for spinner
    static HashMap<String, Integer> spinnerElements = new HashMap<>();
    ArrayAdapter<String> spinnerAdapter;

    //Map for fields and temporary value
    HashMap<String, LinkedList<BluetoothGattCharacteristic>> bluetoothGattServices;

    TextView temporaryReadTextView;

    // Handlers
    Handler stateHandler;
    Handler serviceHandler;
    Handler readDataHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);


        //Create MVC
        connectionModel = new ConnectionModel(this);
        connectionController = new ConnectionController(this, connectionModel);


        //Get all elements for View
        connectBtn = findViewById(R.id.conectBtn);
        connectTitleBar = findViewById(R.id.ConnectTitleBar);
        scrollView = findViewById(R.id.scrollView);

        progressBar = findViewById(R.id.progressBar);
        linearLayoutForController = findViewById(R.id.linearLayoutForController);


        //Set action listner for all elements
        connectionController.addActionListenerForConnectionBtn(connectBtn);


        // Initialization
        initialization();


    }

    private void initialization() {

        connectBtn.setEnabled(true);

        linearLayoutForController.setEnabled(false);
        linearLayoutForController.setVisibility(View.GONE);

        Intent intent = getIntent();
        setTextForConnectTitleBar(intent.getStringExtra("selectedDeviceName"));
        progressBar.setVisibility(View.GONE);


        loadSpinnerElements();


        //State Handler
        stateHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message inputMessage) {
                switch (inputMessage.what) {
                    case BluetoothGatt.STATE_CONNECTING:
                        viewConnecting();
                        break;
                    case BluetoothGatt.STATE_CONNECTED:
                        viewConnected();
                        break;
                    case BluetoothGatt.STATE_DISCONNECTING:
                        viewDisconnecting();
                        break;
                    case BluetoothGatt.STATE_DISCONNECTED:
                        viewDisconnected();
                        break;
                }
            }
        };

        // Adding service Handler
        serviceHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {


                removeElementFromList();

                for (Map.Entry<String, LinkedList<BluetoothGattCharacteristic>> entry : bluetoothGattServices.entrySet()) {

                    String key = entry.getKey();
                    LinkedList<BluetoothGattCharacteristic> characteristics = entry.getValue();
                    Log.i("Service", key);
                    for (BluetoothGattCharacteristic bluetoothGattCharacteristic : characteristics) {
                        Log.i("Characterestic", bluetoothGattCharacteristic.getUuid().toString());
                    }

                    addElementToList(key, characteristics);
                }
            }
        };


        readDataHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {

                String text = inputMessage.obj.toString();
                temporaryReadTextView.setText(text);

            }
        };

    }


    public void setTextForConnectTitleBar(String text) {
        connectTitleBar.setText(text);
    }

    public void ReturnHome(View view) {
        super.onBackPressed();
        connectionController.disconnect();
    }

    // Clear all
    public void removeElementFromList() {

        linearLayoutForController.removeAllViews();

    }


    // Create for each GattService list view of all characteristic service
    private void addElementToList(String serviceUuid, LinkedList<BluetoothGattCharacteristic> characteristics) {

        // Inflater only for reading xml file
        LayoutInflater inflater = LayoutInflater.from(this);
        // List for all characteristic elements from one service
        LinkedList<View> viewLinkedList = new LinkedList<>();


        //LinearLayout only for data about service uuid
        LinearLayout serviceLayout = new LinearLayout(this);
        serviceLayout.setOrientation(LinearLayout.VERTICAL);

        //Only break line between each Service and his characteristic list
        View divider = new View(this);
        divider.setBackgroundColor(Color.rgb(128, 128, 128));
        divider.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5));
        linearLayoutForController.addView(divider);

        // UI settings
        TextView uuidStaticName = new TextView(this);
        uuidStaticName.setText("Service UUID");
        uuidStaticName.setPadding(40, 40, 0, 0);

        TextView uuidValue = new TextView(this);
        uuidValue.setPadding(40, 0, 0, 0);
        uuidValue.setText(serviceUuid);
        uuidValue.setTypeface(null, Typeface.BOLD);

        serviceLayout.addView(uuidStaticName);
        serviceLayout.addView(uuidValue);


        //Add Service Layout with information about uuid
        linearLayoutForController.addView(serviceLayout);

        //For each characteristic create "character_list.xml" view
        for (BluetoothGattCharacteristic bluetoothGattCharacteristic : characteristics) {


            //Load new "character_list.xml"
            View v = inflater.inflate(R.layout.character_list, null, false);
            //Information about properties
            int accessSetting = bluetoothGattCharacteristic.getProperties();

            //Get reference to view from each "character_list.xml"
            TextView characteristicView = v.findViewById(R.id.characteristicUuid);
            TextView propertiesView = v.findViewById(R.id.properties);
            TextView readStaticValue = v.findViewById(R.id.readedStaticValue);
            TextView readValueView = v.findViewById(R.id.readedValue);
            EditText dataToSendView = v.findViewById(R.id.dateToSend);
            ImageButton downloadDateBtn = v.findViewById(R.id.donwloadDateBtn);
            ImageButton sendDataBtn = v.findViewById(R.id.sendDateBtn);
            Spinner typeChoice = v.findViewById(R.id.typeChoice);


            // default Visibility for service's characters
            readStaticValue.setVisibility(View.GONE);
            readValueView.setVisibility(View.GONE);
            dataToSendView.setVisibility(View.GONE);
            downloadDateBtn.setVisibility(View.GONE);
            sendDataBtn.setVisibility(View.GONE);
            typeChoice.setVisibility(View.GONE);
            //

            typeChoice.setAdapter(spinnerAdapter);


            // Uuid for characteristic
            characteristicView.setText(bluetoothGattCharacteristic.getUuid().toString());

            switch (accessSetting) {

                case (BluetoothGattCharacteristic.PROPERTY_READ + BluetoothGattCharacteristic.PROPERTY_WRITE):
                    propertiesView.setText("Read/Write");
                    readStaticValue.setVisibility(View.VISIBLE);
                    readValueView.setVisibility(View.VISIBLE);
                    dataToSendView.setVisibility(View.VISIBLE);

                    downloadDateBtn.setVisibility(View.VISIBLE);
                    sendDataBtn.setVisibility(View.VISIBLE);

                    typeChoice.setVisibility(View.VISIBLE);


                    //Set Action listener
                    connectionController.addActionListenerForRead(downloadDateBtn, bluetoothGattCharacteristic, readValueView);
                    connectionController.addActionListenerForWrite(sendDataBtn, bluetoothGattCharacteristic, dataToSendView, typeChoice);

                    break;
                case BluetoothGattCharacteristic.PROPERTY_READ:
                    propertiesView.setText("Read");
                    readStaticValue.setVisibility(View.VISIBLE);
                    readValueView.setVisibility(View.VISIBLE);

                    downloadDateBtn.setVisibility(View.VISIBLE);

                    //Set Action listener
                    connectionController.addActionListenerForRead(downloadDateBtn, bluetoothGattCharacteristic, readValueView);


                    break;

                case BluetoothGattCharacteristic.PROPERTY_WRITE:
                    propertiesView.setText("Write");
                    readStaticValue.setVisibility(View.GONE);
                    dataToSendView.setVisibility(View.VISIBLE);

                    sendDataBtn.setVisibility(View.VISIBLE);

                    typeChoice.setVisibility(View.VISIBLE);

                    //Set Action listener
                    connectionController.addActionListenerForWrite(sendDataBtn, bluetoothGattCharacteristic, dataToSendView, typeChoice);
                    break;

                default:

                    propertiesView.setText("Different " + accessSetting);
                    readStaticValue.setVisibility(View.GONE);
                    break;

            }


            viewLinkedList.add(v);

        }


        connectionController.addActionListenerForServiceLayout(serviceLayout, viewLinkedList);

        for (View view : viewLinkedList) {
            view.setVisibility(View.GONE);
            view.setEnabled(false);
            linearLayoutForController.addView(view);
        }
    }


    // Creating Message  and sending to handlers (Only call view thread) It sets state of connection for example (BluetoothGatt.STATE_CONNECTING)
    public void sendMessageToStateHandler(int state) {
        Message message = stateHandler.obtainMessage(state);
        message.sendToTarget();
    }

    // Creating Message  and sending to handlers (Only call view thread) It sets bluetooth characteristic list then it call function for creating view with characteristic
    public void sendMessageToServiceHandler(HashMap<String, LinkedList<BluetoothGattCharacteristic>> bluetoothGattServices) {

        this.bluetoothGattServices = bluetoothGattServices;
        Message message = serviceHandler.obtainMessage();
        message.sendToTarget();

    }

    // Creating Message  and sending to handlers (Only call view thread) It call from connectionModel and this method is getting data from other device
    public void sendMessageToReadHandler(TextView readTextView, String text) {
        temporaryReadTextView = readTextView;
        Message message = readDataHandler.obtainMessage();
        message.obj = text;
        message.sendToTarget();
    }

    // Create list of data type for example if you want to send Int to device then you must select uint32
    private void loadSpinnerElements() {
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        spinnerElements.clear();
        spinnerElements.put("FORMAT_FLOAT", BluetoothGattCharacteristic.FORMAT_FLOAT);
        spinnerElements.put("FORMAT_SFLOAT", BluetoothGattCharacteristic.FORMAT_SFLOAT);
        spinnerElements.put("FORMAT_SINT8", BluetoothGattCharacteristic.FORMAT_SINT8);
        spinnerElements.put("FORMAT_SINT16", BluetoothGattCharacteristic.FORMAT_SINT16);
        spinnerElements.put("FORMAT_SINT32", BluetoothGattCharacteristic.FORMAT_SINT32);
        spinnerElements.put("FORMAT_UINT8", BluetoothGattCharacteristic.FORMAT_UINT8);
        spinnerElements.put("FORMAT_UINT16", BluetoothGattCharacteristic.FORMAT_UINT16);
        spinnerElements.put("FORMAT_UINT32", BluetoothGattCharacteristic.FORMAT_UINT32);

        for (Map.Entry<String, Integer> elem : spinnerElements.entrySet()) {
            spinnerAdapter.add(elem.getKey());
        }

    }

    // Get int of data type
    public static int getSpinnerElement(String spinnerElementKey) {
        int value = spinnerElements.get(spinnerElementKey);

        return value;
    }

    // UI depends of connection status

    private void viewConnecting() {

        connectBtn.setText("Connect...");
        connectBtn.setEnabled(false);

        progressBar.setVisibility(View.VISIBLE);
        linearLayoutForController.setEnabled(false);
        linearLayoutForController.setVisibility(View.GONE);
    }

    private void viewConnected() {
        connectBtn.setText("Disconnect");
        connectBtn.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        linearLayoutForController.setEnabled(true);
        linearLayoutForController.setVisibility(View.VISIBLE);
    }

    private void viewDisconnecting() {
        connectBtn.setText("Disconnect ...");
        connectBtn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        linearLayoutForController.setEnabled(false);
        linearLayoutForController.setVisibility(View.GONE);
    }

    private void viewDisconnected() {
        connectBtn.setText("Connect");
        connectBtn.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        linearLayoutForController.setEnabled(false);
        linearLayoutForController.setVisibility(View.GONE);
    }

    //

}
