package com.davidlutta.retro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.davidlutta.retro.util.SharedPref;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.dark_mode_switch)
    Switch darkModeSwitch;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAppearance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setTitle("Settings");
        if (sharedPref.getNightModeState()) {
            darkModeSwitch.setChecked(true);
        }
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPref.setNightModeState(true);
                    restartApp();
                } else {
                    sharedPref.setNightModeState(false);
                    restartApp();
                }
            }
        });
    }

    private void setAppearance() {
        sharedPref = new SharedPref(this);
        if (sharedPref.getNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else setTheme(R.style.AppTheme);
    }


    private void restartApp() {
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        finish();
    }
}
