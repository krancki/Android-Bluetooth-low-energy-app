package com.krancki.test.bluetoothapp.MainMenu;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.krancki.test.bluetoothapp.R;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {


    private MainController mainController;
    private MainModel mainModel;



    private Button btnShowAll;
    private ListView listView;

    ArrayList<String> listOfDevice=new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Create MVC
        mainModel = new MainModel(this);
        mainController = new MainController(this, mainModel);


        //Get all elements for View
        btnShowAll = findViewById(R.id.btnShowAll);
        listView = findViewById(R.id.listView);

        //Initialization for ListView with adapter
        arrayAdapter= new ArrayAdapter(this, android.R.layout.simple_list_item_1,listOfDevice);
        listView.setAdapter(arrayAdapter);


        //Set Action for all view Element
        mainController.addActionListnerForListView(listView);
        mainController.addActionListnerForbtnShowall(btnShowAll);



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
