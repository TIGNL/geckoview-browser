package com.example.geckobrowser;

import android.app.Activity;

import java.util.List;

/**
 * TabsSheetDialog — شيت التبويبات
 * ترث من BaseSheetDialog وتعرض قائمة التبويبات المفتوحة
 */
public class TabsSheetDialog extends BaseSheetDialog {

    public interface OnTabSelectedListener {
        void onTabSelected(int tabId);
        void onNewTabRequested();
    }

    private final List<GeckoTab> tabs;
    private final int currentTabId;
    private OnTabSelectedListener listener;

    public TabsSheetDialog(Activity activity, List<GeckoTab> tabs, int currentTabId) {
        super(activity);
        this.tabs         = tabs;
        this.currentTabId = currentTabId;
        setTitle(activity.getString(R.string.tabs_title));
    }

    public void setOnTabSelectedListener(OnTabSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void buildItems() {
        // عرض كل تبويبة كعنصر
        for (GeckoTab tab : tabs) {
            String label = (tab.url != null && !tab.url.isEmpty())
                ? tab.url
                : activity.getString(R.string.new_tab_label);

            // تمييز التبويبة الحالية
            if (tab.id == currentTabId) label = "▶ " + label;

            final int tabId = tab.id;
            addItem(
                BaseSheetDialog.textContent(label),
                () -> { if (listener != null) listener.onTabSelected(tabId); }
            );
        }

        // زر تبويبة جديدة
        addItem(
            BaseSheetDialog.textContent("+ " + activity.getString(R.string.new_tab_label)),
            () -> { if (listener != null) listener.onNewTabRequested(); }
        );
    }
}
