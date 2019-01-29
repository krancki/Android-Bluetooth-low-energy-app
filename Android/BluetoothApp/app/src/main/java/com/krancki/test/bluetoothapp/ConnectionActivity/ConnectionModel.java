package com.krancki.test.bluetoothapp.ConnectionActivity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.res.AssetManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.util.Xml;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.krancki.test.bluetoothapp.Connect;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

public class ConnectionModel {

    private Connect connect;
    static ConnectionActivity connectionActivity ;

    static TextView temporaryView;



    public ConnectionModel(ConnectionActivity connectionActivity) {
        this.connectionActivity = connectionActivity;
        connect= Connect.getInstance(connectionActivity);
        connect.initialize();
    }


    public void tryConnect() {

        if(connect.isConnected()){
            connect.disconnect();
        }else{
            connect.openConnection();
        }

    }


    public void disconnect(){
        if(connect.isConnected()){
            connect.disconnect();
        }
    }


    public void characteristicRead(BluetoothGattCharacteristic bluetoothGattCharacteristic, TextView readValueView) {
        temporaryView=readValueView;
        connect.read(bluetoothGattCharacteristic);
    }




    public void characteristicWrite(BluetoothGattCharacteristic bluetoothGattCharacteristic, EditText writeValueView, Spinner spinner) {


        int value = Integer.parseInt(writeValueView.getText().toString());
        Log.i("Value", String.valueOf(writeValueView.getText().toString()));
        int type = connectionActivity.getSpinnerElement(spinner.getSelectedItem().toString());

        bluetoothGattCharacteristic.setValue(value,type,0);
        connect.write(bluetoothGattCharacteristic);
    }


    //Only passing the parameter

    public static void sendStateToView(int state){
        connectionActivity.sendMessageToStateHandler(state);
    }

    public static void addService(List<BluetoothGattService> list) {

        HashMap<String, LinkedList<BluetoothGattCharacteristic>> services = new HashMap<>();


        for (BluetoothGattService service : list) {

            LinkedList<BluetoothGattCharacteristic> characteristics = new LinkedList<>();
            characteristics.addAll(service.getCharacteristics());

            services.put(service.getUuid().toString(),characteristics);
        }

        connectionActivity.sendMessageToServiceHandler(services);

    }

    public static void sendReadDataToView(String message){

        connectionActivity.sendMessageToReadHandler(temporaryView,message);

    }





}

