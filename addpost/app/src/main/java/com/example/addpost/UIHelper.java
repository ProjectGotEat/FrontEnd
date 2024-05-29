package com.example.addpost;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class UIHelper {

    public static void openGallery(AddpostActivity activity, int requestCode) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(galleryIntent, requestCode);
    }

    public static void showDateTimePicker(Context context, Calendar calendar, DatePickerDialog.OnDateSetListener dateSetListener, TimePickerDialog.OnTimeSetListener timeSetListener) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, dateSetListener, year, month, day);
        datePickerDialog.setOnDateSetListener((view1, year1, month1, dayOfMonth) -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(context, timeSetListener, hour, minute, false);
            timePickerDialog.show();
        });
        datePickerDialog.show();
    }


}
