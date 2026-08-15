package com.example.geckobrowser;

import android.app.Activity;

/**
 * BottomSheetMenuDialog — شيت القائمة الرئيسية
 * ترث من BaseSheetDialog وتضيف عناصرها فقط
 */
public class BottomSheetMenuDialog extends BaseSheetDialog {

    public interface OnItemSelectedListener {
        void onSettingsSelected();
        void onDesktopModeSelected();
        void onShareSelected();
    }

    private OnItemSelectedListener listener;

    public BottomSheetMenuDialog(Activity activity) {
        super(activity);
        setTitle(activity.getString(R.string.more_menu_title));
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void buildItems() {
        addItem(
            BaseSheetDialog.textContent(activity.getString(R.string.menu_settings)),
            () -> { if (listener != null) listener.onSettingsSelected(); }
        );
        addItem(
            BaseSheetDialog.textContent(activity.getString(R.string.menu_desktop_mode)),
            () -> { if (listener != null) listener.onDesktopModeSelected(); }
        );
        addItem(
            BaseSheetDialog.textContent(activity.getString(R.string.menu_share)),
            () -> { if (listener != null) listener.onShareSelected(); }
        );
    }
}
