package com.example.geckobrowser;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private boolean desktopMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        desktopMode = getIntent().getBooleanExtra("desktop_mode", false);

        TextView rowDesktopMode = findViewById(R.id.rowDesktopMode);
        updateDesktopModeStatus(rowDesktopMode, desktopMode);

        rowDesktopMode.setOnClickListener(v -> {
            desktopMode = !desktopMode;
            updateDesktopModeStatus(rowDesktopMode, desktopMode);
            getIntent().putExtra("desktop_mode", desktopMode);
            setResult(RESULT_OK, getIntent());
        });
    }

    private void updateDesktopModeStatus(TextView row, boolean enabled) {
        row.setText(enabled ? R.string.desktop_mode_on : R.string.desktop_mode_off);
    }
}
