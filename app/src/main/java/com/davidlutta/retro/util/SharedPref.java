package com.davidlutta.retro.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    SharedPreferences preferences;

    public SharedPref(Context context) {
        this.preferences = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }

    public Boolean getNightModeState() {
        Boolean state = preferences.getBoolean("NightMode", false);
        return state;
    }

    public void setNightModeState(Boolean state) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("NightMode", state);
        editor.commit();
    }
}
