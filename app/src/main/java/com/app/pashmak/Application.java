package com.app.pashmak;

import com.app.pashmak.Utils.FontsOverride;
import com.app.pashmak.Utils.Utils;

public final class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();


        FontsOverride.setDefaultFont(this, "DEFAULT", "B Koodak.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "B Koodak.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "B Koodak.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "B Koodak.ttf");

        Utils.prepareResources(this);
        Utils.prepareRealm(this);


    }


}
