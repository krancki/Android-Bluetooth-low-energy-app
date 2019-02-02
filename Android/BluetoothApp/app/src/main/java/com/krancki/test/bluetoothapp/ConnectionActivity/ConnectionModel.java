package com.krancki.test.bluetoothapp.ConnectionActivity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.krancki.test.bluetoothapp.Connect;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

public class ConnectionModel {

    private Connect connect;
    static ConnectionActivity connectionActivity;

    static TextView temporaryView;

    // Set MVC and init
    public ConnectionModel(ConnectionActivity connectionActivity) {
        this.connectionActivity = connectionActivity;
        connect = Connect.getInstance(connectionActivity);
        connect.initialize();
    }

    // Connect or disconnect
    public void openCloseConnection() {

        if (connect.isConnected()) {
            connect.disconnect();
        } else {
            connect.openConnection();
        }

    }

    // Disconnect
    public void disconnect() {
        if (connect.isConnected()) {
            connect.disconnect();
        }
    }


    public void characteristicRead(BluetoothGattCharacteristic bluetoothGattCharacteristic, TextView readValueView) {
        temporaryView = readValueView;
        connect.read(bluetoothGattCharacteristic);
    }


    public void characteristicWrite(BluetoothGattCharacteristic bluetoothGattCharacteristic, EditText writeValueView, Spinner spinner) {
        int value;
        String stringValue = writeValueView.getText().toString();

        try {
            value = Integer.parseInt(stringValue);
        } catch (NumberFormatException e) {
            value = 0;
        }

        int type = connectionActivity.getSpinnerElement(spinner.getSelectedItem().toString());

        bluetoothGattCharacteristic.setValue(value, type, 0);
        connect.write(bluetoothGattCharacteristic);
    }


    //Only passing the parameter to handler

    public static void sendStateToView(int state) {
        connectionActivity.sendMessageToStateHandler(state);
    }

    public static void addService(List<BluetoothGattService> list) {

        HashMap<String, LinkedList<BluetoothGattCharacteristic>> services = new HashMap<>();


        for (BluetoothGattService service : list) {

            LinkedList<BluetoothGattCharacteristic> characteristics = new LinkedList<>();
            characteristics.addAll(service.getCharacteristics());

            services.put(service.getUuid().toString(), characteristics);
        }

        connectionActivity.sendMessageToServiceHandler(services);

    }

    public static void sendReadDataToView(String message) {

        connectionActivity.sendMessageToReadHandler(temporaryView, message);

    }


}

