package com.example.geckobrowser;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class BottomSheetMenuDialog {

    private final Activity activity;
    private BottomSheetDialog dialog;
    private OnItemSelectedListener listener;

    public interface OnItemSelectedListener {
        void onSettingsSelected();
        void onDesktopModeSelected();
        void onShareSelected();
    }

    public BottomSheetMenuDialog(Activity activity) {
        this.activity = activity;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    public void show() {
        dialog = new BottomSheetDialog(activity, R.style.BottomSheetDialogTheme);

        View contentView = activity.getLayoutInflater().inflate(R.layout.activity_bottom_sheet_menu, null);
        dialog.setContentView(contentView);

        TextView sheetSettings = contentView.findViewById(R.id.sheetSettings);
        TextView sheetDesktopMode = contentView.findViewById(R.id.sheetDesktopMode);
        TextView sheetShare = contentView.findViewById(R.id.sheetShare);

        sheetSettings.setOnClickListener(v -> {
            dismiss();
            if (listener != null) listener.onSettingsSelected();
        });

        sheetDesktopMode.setOnClickListener(v -> {
            dismiss();
            if (listener != null) listener.onDesktopModeSelected();
        });

        sheetShare.setOnClickListener(v -> {
            dismiss();
            if (listener != null) listener.onShareSelected();
        });

        dialog.show();
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }
}
