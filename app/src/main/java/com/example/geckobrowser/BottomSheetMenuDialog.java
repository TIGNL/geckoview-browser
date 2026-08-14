package com.example.geckobrowser;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

public class BottomSheetMenuDialog {

    private final Activity activity;
    private Dialog dialog;
    private OnItemSelectedListener listener;
    private float startY;
    private float startRawY;
    private boolean swiping;
    private int dialogHeight;
    private int originalY;

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
            dialogHeight = (int) (screenHeight * 0.75);

            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight);
            window.setGravity(Gravity.TOP);
            window.setDimAmount(0.5f);

            WindowManager.LayoutParams params = window.getAttributes();
            originalY = screenHeight - dialogHeight;
            params.y = originalY;
            window.setAttributes(params);

            contentView.setPadding(0, 0, 0, navbarHeight);

            setupSwipeToDismiss(contentView, window, params);
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

    private void setupSwipeToDismiss(View contentView, Window window, WindowManager.LayoutParams params) {
        contentView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startY = params.y;
                    startRawY = event.getRawY();
                    swiping = false;
                    return false;

                case MotionEvent.ACTION_MOVE:
                    float dy = event.getRawY() - startRawY;
                    if (!swiping && dy > 20) {
                        swiping = true;
                    }
                    if (swiping) {
                        float newY = startY + dy;
                        if (newY >= originalY) {
                            params.y = (int) newY;
                            window.setAttributes(params);
                            float progress = Math.min(dy / dialogHeight, 1f);
                            window.setDimAmount(0.5f * (1f - progress));
                        }
                        return true;
                    }
                    return false;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (swiping) {
                        float finalDy = event.getRawY() - startRawY;
                        if (finalDy > dialogHeight * 0.25) {
                            animateDismiss(window, params);
                        } else {
                            animateSnapBack(window, params);
                        }
                        swiping = true;
                        return true;
                    }
                    swiping = false;
                    return false;
            }
            return false;
        });
    }

    private void animateDismiss(Window window, WindowManager.LayoutParams params) {
        int startYVal = params.y;
        int endY = dialogHeight + 100;

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(350);
        animator.setInterpolator(new DecelerateInterpolator(2f));
        animator.addUpdateListener(animation -> {
            float t = (float) animation.getAnimatedValue();
            params.y = (int) (startYVal + (endY - startYVal) * t);
            window.setAttributes(params);
            window.setDimAmount(0.5f * (1f - t));
        });
        animator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                dismiss();
            }
        });
        animator.start();
    }

    private void animateSnapBack(Window window, WindowManager.LayoutParams params) {
        int startYVal = params.y;
        int endY = originalY;

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator(1.5f));
        animator.addUpdateListener(animation -> {
            float t = (float) animation.getAnimatedValue();
            params.y = (int) (startYVal + (endY - startYVal) * t);
            window.setAttributes(params);
            window.setDimAmount(0.5f);
        });
        animator.start();
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
