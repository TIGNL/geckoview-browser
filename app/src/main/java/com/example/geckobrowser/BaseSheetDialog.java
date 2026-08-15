package com.example.geckobrowser;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

/**
 * BaseSheetDialog — الكلاس الأساسي لكل الـ sheets في التطبيق
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * الهيكل الثابت:
 *   • Header  256dp = Handle 32dp (يطفو فوق العنوان) + عنوان مُتمركز في كامل الـ 256dp
 *   • قائمة عناصر — كل عنصر 64dp مع padding 16dp جوانب ومحتوى 32dp متمركز
 *   • Divider 2dp قبل أول عنصر وبعد كل عنصر
 *
 * الاستخدام:
 *   1. مرر العنوان عبر setTitle()
 *   2. أضف العناصر عبر addItem()
 *   3. استدع show()
 */
public class BaseSheetDialog {

    protected final Activity activity;
    private BottomSheetDialog dialog;
    private LinearLayout itemsContainer;
    private String title = "";

    public BaseSheetDialog(Activity activity) {
        this.activity = activity;
    }

    public BaseSheetDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * يضيف عنصراً إلى القائمة.
     * @param contentBuilder دالة تملأ مساحة الـ 32dp بالمحتوى المطلوب
     * @param onClick         الحدث عند الضغط على العنصر
     */
    public BaseSheetDialog addItem(ContentBuilder contentBuilder, Runnable onClick) {
        if (itemsContainer == null) return this;

        // inflate العنصر
        View row = LayoutInflater.from(activity)
            .inflate(R.layout.item_sheet_row, itemsContainer, false);

        FrameLayout rowContent = row.findViewById(R.id.rowContent);
        contentBuilder.build(rowContent, activity);

        row.setOnClickListener(v -> {
            dismiss();
            if (onClick != null) onClick.run();
        });

        itemsContainer.addView(row);

        // Divider بعد كل عنصر
        addDivider();
        return this;
    }

    public void show() {
        dialog = new BottomSheetDialog(activity, R.style.BottomSheetDialogTheme);

        // الـ layout الرئيسي للـ sheet
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF000000);
        root.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));

        // ── Header 256dp ─────────────────────────────────────────────────
        FrameLayout header = new FrameLayout(activity);
        header.setBackgroundColor(0xFF000000);
        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(256));
        header.setLayoutParams(headerParams);

        // Handle — يطفو في الأعلى
        View handle = new View(activity);
        handle.setBackgroundColor(0x80FFFFFF);
        FrameLayout.LayoutParams handleParams = new FrameLayout.LayoutParams(
            dpToPx(40), dpToPx(4));
        handleParams.gravity = android.view.Gravity.TOP | android.view.Gravity.CENTER_HORIZONTAL;
        handleParams.topMargin = dpToPx(14);
        handle.setLayoutParams(handleParams);
        header.addView(handle);

        // العنوان — يتمركز في كامل الـ 256dp
        TextView titleView = new TextView(activity);
        titleView.setText(title);
        titleView.setTextColor(0xFFFFFFFF);
        titleView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 24);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setGravity(android.view.Gravity.CENTER);
        titleView.setPadding(dpToPx(16), 0, dpToPx(16), 0);
        FrameLayout.LayoutParams titleParams = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);
        titleParams.gravity = android.view.Gravity.CENTER;
        titleView.setLayoutParams(titleParams);
        header.addView(titleView);

        root.addView(header);

        // ── حاوية العناصر ────────────────────────────────────────────────
        itemsContainer = new LinearLayout(activity);
        itemsContainer.setOrientation(LinearLayout.VERTICAL);
        itemsContainer.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(itemsContainer);

        // divider واحد قبل أول عنصر
        addDivider();

        // بناء العناصر (يُستدعى من الكلاسات الوارثة قبل show())
        buildItems();

        dialog.setContentView(root);

        // الـ sheet تملأ الشاشة وتبقى مفتوحة
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        View bottomSheet = dialog.findViewById(
            com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setPeekHeight(dm.heightPixels);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true);
        }

        dialog.show();
    }

    /** يُعيد تعريفه الكلاس الوارث لإضافة العناصر */
    protected void buildItems() {}

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
    }

    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private void addDivider() {
        if (itemsContainer != null) itemsContainer.addView(buildDividerView());
    }

    private View buildDividerView() {
        View divider = new View(activity);
        divider.setBackgroundColor(0x80808080);
        divider.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(2)));
        return divider;
    }

    private int dpToPx(int dp) {
        float density = activity.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    /** واجهة لبناء محتوى العنصر الـ 32dp */
    public interface ContentBuilder {
        void build(FrameLayout container, Activity activity);
    }

    /** مساعد — ينشئ TextView بسيط كمحتوى للعنصر */
    public static ContentBuilder textContent(String text) {
        return (container, activity) -> {
            TextView tv = new TextView(activity);
            tv.setText(text);
            tv.setTextColor(0xFFFFFFFF);
            tv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 15);
            tv.setGravity(android.view.Gravity.CENTER_VERTICAL);
            tv.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
            container.addView(tv);
        };
    }
}
