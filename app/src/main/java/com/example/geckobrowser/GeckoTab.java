package com.example.geckobrowser;

import org.mozilla.geckoview.GeckoView;

public class GeckoTab {
    public final int id;
    public final GeckoView geckoView;
    public String title;
    public String url;

    public GeckoTab(int id, GeckoView geckoView) {
        this.id = id;
        this.geckoView = geckoView;
        this.title = "New Tab";
        this.url = "";
    }
}
