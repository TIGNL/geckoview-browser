package com.example.geckobrowser;

import android.app.Activity;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class BottomSheetMenuDialog {

    private final Activity activity;
    private Dialog dialog;
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
        dialog = new Dialog(activity, R.style.BottomSheetDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        View contentView = activity.getLayoutInflater().inflate(R.layout.activity_bottom_sheet_menu, null);
        dialog.setContentView(contentView);

        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            int screenHeight = dm.heightPixels;
            int dialogHeight = (int) (screenHeight * 0.75);

            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight);
            window.setGravity(Gravity.TOP);
            window.setDimAmount(0.5f);

            WindowManager.LayoutParams params = window.getAttributes();
            params.y = screenHeight - dialogHeight;
            window.setAttributes(params);

            float density = activity.getResources().getDisplayMetrics().density;
            int navbarHeight = (int) (64 * density + 0.5f);
            contentView.setPadding(0, 0, 0, navbarHeight);
        }

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
