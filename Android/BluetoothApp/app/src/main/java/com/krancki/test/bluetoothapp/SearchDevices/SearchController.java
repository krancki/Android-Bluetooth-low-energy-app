package com.krancki.test.bluetoothapp.SearchDevices;

import android.content.Context;
import android.widget.Button;
import android.widget.ListView;

public class SearchController {

    private final SearchActivity searchActivity;
    private final SearchModel searchModel;

    public SearchController(SearchActivity searchActivity, SearchModel searchModel){
        this.searchActivity = searchActivity;
        this.searchModel = searchModel;
    }


    public void addActionListnerForListView(ListView listView) {
        listView.setOnItemClickListener((parent, view, position, id) -> {

            searchModel.setPaired(position);
        });
    }

    public void addActionListnerForbtnDiscoverDevice(Button btnDiscoverDevice) {

        btnDiscoverDevice.setOnClickListener((view) -> {
            searchActivity.removeElemFromList();
            searchModel.getAllDevice();
        });
    }
}
