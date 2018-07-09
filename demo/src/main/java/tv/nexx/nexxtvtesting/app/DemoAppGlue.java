package tv.nexx.nexxtvtesting.app;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import tv.dailyme.android.api.Dailyme;

public class DemoAppGlue implements Dailyme.AppGlue {
    @NonNull
    @Override
    public Class getStartActivity() {
        return MainActivity.class;
    }

    @NonNull
    @Override
    public Intent getSettingIntent(Context context) {
        return null;
    }

    @NonNull
    @Override
    public String getTenantName() {
        return "nexxtv";
    }

    @Override
    public int getLicenceNumber() {
        return 1;
    }

    @NonNull
    @Override
    public String getLicenceKey() {
        return "#1429059883";
    }
}