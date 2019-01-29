package com.krancki.test.bluetoothapp.MainMenu;

import android.widget.Button;
import android.widget.ListView;

/**
 * Created by Krzysztof on 2018-12-14.
 */

public class MainController {


    private final MainActivity mainActivity;
    private final MainModel mainModel;


    public MainController(MainActivity mainActivity, MainModel mainModel){
        this.mainModel=mainModel;
        this.mainActivity=mainActivity;




    }


public void addActionListnerForbtnShowall(Button button){
        button.setOnClickListener((view) -> {
            mainActivity.removeElemFromList();
            mainModel.getAllDevice();
        });
}


public void addActionListnerForListView(ListView listView){

        listView.setOnItemClickListener((parent, view, position, id) -> {

         mainModel.openConnection(position);
        });
}




}
