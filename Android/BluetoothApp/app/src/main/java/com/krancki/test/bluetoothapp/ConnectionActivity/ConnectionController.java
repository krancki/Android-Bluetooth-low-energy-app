package com.krancki.test.bluetoothapp.ConnectionActivity;


import android.bluetooth.BluetoothGattCharacteristic;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;




import java.util.List;

public class ConnectionController {

    ConnectionActivity connectionActivity;
    ConnectionModel connectionModel;

    //Set MVC
    public ConnectionController(ConnectionActivity connectionActivity, ConnectionModel connectionModel) {
        this.connectionActivity = connectionActivity;
        this.connectionModel = connectionModel;
    }

    public void addActionListenerForConnectionBtn(Button connectionBtn) {
        connectionBtn.setOnClickListener((View) -> {
            connectionModel.openCloseConnection();
        });

    }

    public void addActionListenerForServiceLayout(LinearLayout LinearLayout, List<View> viewList) {
        LinearLayout.setOnClickListener((View) -> {

            for (View view : viewList) {
                if (view.getVisibility() == android.view.View.GONE) {
                    view.setVisibility(android.view.View.VISIBLE);
                    view.setEnabled(true);
                } else {
                    view.setVisibility(android.view.View.GONE);
                    view.setEnabled(false);
                }
            }
        });
    }

    public void addActionListenerForRead(ImageButton imageButton, BluetoothGattCharacteristic bluetoothGattCharacteristic, TextView readValueView) {
        imageButton.setOnClickListener((view) -> {
            if (bluetoothGattCharacteristic != null) {
                connectionModel.characteristicRead(bluetoothGattCharacteristic, readValueView);
            }
        });
    }

    public void addActionListenerForWrite(ImageButton imageButton, BluetoothGattCharacteristic bluetoothGattCharacteristic, EditText writeValueView, Spinner dataType) {
        imageButton.setOnClickListener((view) -> {
            connectionModel.characteristicWrite(bluetoothGattCharacteristic, writeValueView, dataType);
        });
    }


    public void disconnect() {
        connectionModel.disconnect();
    }

}
