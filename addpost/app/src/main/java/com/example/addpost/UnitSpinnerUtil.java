package com.example.addpost;

// UnitSpinnerUtil.java

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class UnitSpinnerUtil {

    public static void setupUnitSpinner(Activity activity) {
        String[] unitList = {"g", "kg", "L","ml","개"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, unitList);
        Spinner spinner = activity.findViewById(R.id.unit_spinner);
        spinner.setAdapter(adapter);
    }
}

