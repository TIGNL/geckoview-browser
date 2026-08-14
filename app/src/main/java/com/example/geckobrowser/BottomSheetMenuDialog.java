package com.example.geckobrowser;

import android.app.Activity;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class BottomSheetMenuDialog {

    private final Activity activity;
    private Dialog dialog;
    private OnItemSelectedListener listener;
    private float startY;
    private boolean swiping;

    public interface OnItemSelectedListener {
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

            float density = activity.getResources().getDisplayMetrics().density;
            int navbarHeight = (int) (64 * density + 0.5f);
            int dialogHeight = (int) (screenHeight * 0.75);

            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight);
            window.setGravity(Gravity.TOP);
            window.setDimAmount(0.5f);

            WindowManager.LayoutParams params = window.getAttributes();
            params.y = screenHeight - dialogHeight;
            window.setAttributes(params);

            contentView.setPadding(0, 0, 0, navbarHeight);

            contentView.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startY = event.getRawY();
                        swiping = false;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float dy = event.getRawY() - startY;
                        if (dy > 10) swiping = true;
                        if (swiping && dy > 0) {
                            params.y = (int) ((screenHeight - dialogHeight) + dy);
                            window.setAttributes(params);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (swiping) {
                            float finalDy = event.getRawY() - startY;
                            if (finalDy > dialogHeight * 0.3) {
                                dismissWithAnimation(params, window, dialogHeight);
                            } else {
                                snapBack(params, window, screenHeight - dialogHeight);
                            }
                        }
                        swiping = false;
                        return true;
                }
                return false;
            });
        }

        TextView sheetDesktopMode = contentView.findViewById(R.id.sheetDesktopMode);
        TextView sheetShare = contentView.findViewById(R.id.sheetShare);

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

    private void dismissWithAnimation(WindowManager.LayoutParams params, Window window, int dialogHeight) {
        final int startY = params.y;
        final int endY = dialogHeight;

        final long duration = 250;
        final long startTime = System.currentTimeMillis();

        final android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
        final Runnable animator = new Runnable() {
            @Override
            public void run() {
                long elapsed = System.currentTimeMillis() - startTime;
                float t = Math.min((float) elapsed / duration, 1f);
                float ease = t * t;
                params.y = (int) (startY + (endY - startY) * ease);
                window.setAttributes(params);
                window.setDimAmount(0.5f * (1f - ease));
                if (t < 1f) {
                    handler.postDelayed(this, 16);
                } else {
                    dismiss();
                }
            }
        };
        handler.post(animator);
    }

    private void snapBack(WindowManager.LayoutParams params, Window window, int targetY) {
        final int startY = params.y;
        final long duration = 200;
        final long startTime = System.currentTimeMillis();

        final android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
        final Runnable animator = new Runnable() {
            @Override
            public void run() {
                long elapsed = System.currentTimeMillis() - startTime;
                float t = Math.min((float) elapsed / duration, 1f);
                float ease = 1f - (1f - t) * (1f - t);
                params.y = (int) (startY + (targetY - startY) * ease);
                window.setAttributes(params);
                if (t < 1f) {
                    handler.postDelayed(this, 16);
                }
            }
        };
        handler.post(animator);
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
