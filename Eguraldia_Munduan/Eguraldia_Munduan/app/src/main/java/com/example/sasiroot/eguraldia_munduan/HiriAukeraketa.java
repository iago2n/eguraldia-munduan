package com.example.sasiroot.eguraldia_munduan;

import android.app.Activity;
import android.content.SharedPreferences;

public class HiriAukeraketa {

    SharedPreferences prefs;

    public HiriAukeraketa(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    void setCity(String city){
        prefs.edit().putString("city", city).commit();
    }

}