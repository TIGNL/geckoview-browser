package com.example.geckobrowser;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView rowDesktopMode = findViewById(R.id.rowDesktopMode);

        boolean isDesktopMode = getIntent().getBooleanExtra("desktop_mode", false);
        updateDesktopModeStatus(rowDesktopMode, isDesktopMode);

        rowDesktopMode.setOnClickListener(v -> {
            isDesktopMode = !isDesktopMode;
            updateDesktopModeStatus(rowDesktopMode, isDesktopMode);
            getIntent().putExtra("desktop_mode", isDesktopMode);
            setResult(RESULT_OK, getIntent());
        });
    }

    private void updateDesktopModeStatus(TextView row, boolean enabled) {
        row.setText(enabled ? R.string.desktop_mode_on : R.string.desktop_mode_off);
    }
}
