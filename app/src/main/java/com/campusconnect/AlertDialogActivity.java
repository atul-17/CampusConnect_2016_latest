package com.campusconnect;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

/**
 * Created by Atul on 18-08-2016.
 */
public class AlertDialogActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Creating an Alert Dialog Window */
        AlarmAlert alert = new AlarmAlert();
        Intent i = new Intent(AlertDialogActivity.this,AlarmAlert.class);
        startActivity(i);

        /** Opening the Alert Dialog Window. This will be opened when the alarm goes off */
       // alert.show(getSupportFragmentManager(), "AlARM WAKE UP!!!");

    }
}
