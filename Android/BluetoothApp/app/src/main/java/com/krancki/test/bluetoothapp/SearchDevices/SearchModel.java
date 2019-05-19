package com.krancki.test.bluetoothapp.SearchDevices;

import com.krancki.test.bluetoothapp.Connect;

public class SearchModel {

    SearchActivity searchActivity;
    Connect connect;

    public SearchModel(SearchActivity searchActivity){
        this.searchActivity = searchActivity;

        connect = Connect.getInstance(searchActivity);
        connect.initialize();
    }

    public void setPaired(int position) {
    }

    public void getAllDevice() {

    }
}
