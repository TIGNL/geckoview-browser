package com.example.geckobrowser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static GeckoRuntime geckoRuntime;

    private final List<GeckoTab> tabs = new ArrayList<>();
    private int currentTabId = -1;
    private int nextTabId = 0;

    private FrameLayout webViewContainer;
    private EditText urlBar;
    private TextView btnBack, btnHome, btnForward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webViewContainer = findViewById(R.id.webViewContainer);
        urlBar = findViewById(R.id.urlBar);
        btnBack = findViewById(R.id.btnBack);
        btnHome = findViewById(R.id.btnHome);
        btnForward = findViewById(R.id.btnForward);

        if (geckoRuntime == null) {
            geckoRuntime = GeckoRuntime.create(this);
        }

        btnBack.setOnClickListener(v -> goBack());
        btnForward.setOnClickListener(v -> goForward());
        btnHome.setOnClickListener(v -> {
            hideKeyboard();
            createNewTab("");
        });

        urlBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                navigateTo(urlBar.getText().toString().trim());
                hideKeyboard();
                return true;
            }
            return false;
        });

        urlBar.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) urlBar.selectAll();
        });

        if (tabs.isEmpty()) {
            createNewTab("");
        } else {
            for (GeckoTab tab : tabs) {
                webViewContainer.addView(tab.geckoView);
            }
            switchToTab(currentTabId);
        }
    }

    @Override
    public void onBackPressed() {
        GeckoTab tab = getCurrentTab();
        if (tab != null && tab.geckoView.getSession() != null) {
            tab.geckoView.getSession().goBack();
        } else {
            super.onBackPressed();
        }
    }

    public void createNewTab(String url) {
        GeckoSession session = new GeckoSession();
        GeckoView geckoView = new GeckoView(this);
        geckoView.setSession(session);

        int id = nextTabId++;
        GeckoTab tab = new GeckoTab(id, geckoView);
        tabs.add(tab);

        webViewContainer.addView(geckoView,
            new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        switchToTab(id);
        if (url != null && !url.isEmpty()) {
            loadUrl(url);
        }
    }

    public void switchToTab(int id) {
        for (GeckoTab tab : tabs) {
            tab.geckoView.setVisibility(tab.id == id ? View.VISIBLE : View.GONE);
        }
        currentTabId = id;
        GeckoTab tab = getCurrentTab();
        if (tab != null && tab.geckoView.getSession() != null) {
            updateUrlBar(tab.url);
        }
        updateNavButtons();
    }

    public GeckoTab getCurrentTab() {
        for (GeckoTab tab : tabs) {
            if (tab.id == currentTabId) return tab;
        }
        return null;
    }

    public void navigateTo(String input) {
        if (input == null || input.trim().isEmpty()) return;
        String webViewUrl;
        if (input.startsWith("http://") || input.startsWith("https://")) {
            webViewUrl = input;
        } else if (input.contains(".") && !input.contains(" ")) {
            webViewUrl = "https://" + input;
        } else {
            webViewUrl = "https://www.google.com/search?q=" + input.replace(" ", "+");
        }
        loadUrl(webViewUrl);
    }

    private void loadUrl(String url) {
        GeckoTab tab = getCurrentTab();
        if (tab != null && tab.geckoView.getSession() != null) {
            tab.geckoView.getSession().loadUri(url);
            tab.url = url;
        }
    }

    private void goBack() {
        GeckoTab tab = getCurrentTab();
        if (tab != null && tab.geckoView.getSession() != null) {
            tab.geckoView.getSession().goBack();
        }
    }

    private void goForward() {
        GeckoTab tab = getCurrentTab();
        if (tab != null && tab.geckoView.getSession() != null) {
            tab.geckoView.getSession().goForward();
        }
    }

    private void updateUrlBar(String pageUrl) {
        if (urlBar.hasFocus()) return;
        if (pageUrl == null || pageUrl.isEmpty() || "about:blank".equals(pageUrl)) {
            urlBar.setText("");
        } else {
            urlBar.setText(pageUrl);
        }
    }

    private void updateNavButtons() {
        GeckoTab tab = getCurrentTab();
        boolean canGoBack = tab != null && tab.geckoView.getSession() != null
                && tab.geckoView.getSession().canGoBack();
        boolean canGoForward = tab != null && tab.geckoView.getSession() != null
                && tab.geckoView.getSession().canGoForward();
        btnBack.setAlpha(canGoBack ? 1.0f : 0.3f);
        btnForward.setAlpha(canGoForward ? 1.0f : 0.3f);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
