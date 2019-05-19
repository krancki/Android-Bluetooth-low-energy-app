package com.krancki.test.bluetoothapp.SearchDevices;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.krancki.test.bluetoothapp.R;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    SearchController searchController;
    SearchModel searchModel;

    private Button btnDiscoverDevice;
    private ListView listView;

    ArrayList<String> listOfDevice=new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchModel = new SearchModel(this);
        searchController= new SearchController(this,searchModel);

        //Get all elements for View
        btnDiscoverDevice = findViewById(R.id.btnDiscoverDevice);
        listView = findViewById(R.id.findedDeviceList);

        //Initialization for ListView with adapter
        arrayAdapter= new ArrayAdapter(this, android.R.layout.simple_list_item_1,listOfDevice);
        listView.setAdapter(arrayAdapter);


        //Set Action for all view Element
        searchController.addActionListnerForListView(listView);
        searchController.addActionListnerForbtnDiscoverDevice(btnDiscoverDevice);


    }

    public void addElemToList(String text){
        arrayAdapter.add(text);
    }

    public void removeElemFromList(){
        arrayAdapter.clear();
    }



    public  void sendErrorMessage(String text){
        Toast toast = Toast.makeText(this,text,Toast.LENGTH_SHORT);
        toast.show();
    }

}
