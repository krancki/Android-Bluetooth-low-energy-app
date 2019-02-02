package com.krancki.test.bluetoothapp.MainMenu;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import com.krancki.test.bluetoothapp.Connect;
import com.krancki.test.bluetoothapp.ConnectionActivity.ConnectionActivity;

import java.util.LinkedList;

/**
 * Created by Krzysztof on 2018-12-14.
 */

public class MainModel  {


    private static MainActivity mainActivity;
    private Connect connect;

    private LinkedList<BluetoothDevice> findedDevices;

    public MainModel(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        connect = Connect.getInstance(mainActivity);
        connect.initialize();
    }


    public void getAllDevice() {
        mainActivity.removeElemFromList();
         findedDevices=connect.getFoundDevice();
        
        for(BluetoothDevice bluetoothDevice: findedDevices){
            mainActivity.addElemToList(bluetoothDevice.getName());
        }

    }

  // Opening new Activity and send information about device
    public void openConnection(int position){

        BluetoothDevice bluetoothDevice=findedDevices.get(position);
        if(connect.checkDevice(bluetoothDevice))
        {
        connect.setSelectedDevice(bluetoothDevice);
        Intent intent = new Intent(mainActivity, ConnectionActivity.class);
        intent.putExtra("selectedDeviceName",connect.getSelectedDeviceName());
        mainActivity.startActivity(intent);
        }else{
            mainActivity.sendErrorMessage("This device is not Bluetooth Low Energy");
        }


    }

    public static void console(String text){
        mainActivity.sendErrorMessage(text);
    }

}



