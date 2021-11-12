package tv.nexx.android.testapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.FragmentActivity;

import tv.nexx.android.play.NexxPLAY;
import tv.nexx.android.testapp.fragments.SettingsFragment;
import tv.nexx.android.testapp.navigation.IAppFragmentNavigation;
import tv.nexx.android.testapp.providers.NavigationProvider;
import tv.nexx.android.testapp.providers.NexxPlayProvider;

public class MainActivity extends FragmentActivity {
    private static final String TAG = MainActivity.class.getCanonicalName();

    private static final String SHORTCUT_VIDEO = "tv.nexx.android.testapp.shortcuts.VIDEO";
    private static final String SHORTCUT_LIVE = "tv.nexx.android.testapp.shortcuts.LIVE";
    private static final String SHORTCUT_AUDIO = "tv.nexx.android.testapp.shortcuts.AUDIO";
    private static final String SHORTCUT_PLAYLIST = "tv.nexx.android.testapp.shortcuts.PLAYLIST";
    private static final String ACTION_MAIN = "android.intent.action.MAIN";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        IAppFragmentNavigation appFragmentNavigation = NavigationProvider.get(this);

        Intent intent = getIntent();
        if (intent != null) {
            handleIntent(intent);
        } else {
            appFragmentNavigation.resetToFragment(new SettingsFragment());
        }
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "STARTED WITH ACTION: " + action);
        IAppFragmentNavigation appFragmentNavigation = NavigationProvider.get(this);

        String streamtype = null;
        int item = 0;
        int domain = 0;
        switch (action) {
            case SHORTCUT_VIDEO:
                streamtype = "video";
                break;
            case SHORTCUT_LIVE:
                streamtype = "live";
                break;
            case SHORTCUT_AUDIO:
                streamtype = "audio";
                break;
            case SHORTCUT_PLAYLIST:
                streamtype = "playlist";
                break;
            case "tv.nexx.android.widget.OPEN":
                streamtype = intent.getStringExtra("streamtype");
                domain = intent.getIntExtra("domainID", 0);
                item = intent.getIntExtra("itemID", 0);
                break;
        }

        SettingsFragment fragment = new SettingsFragment();
        if (streamtype != null) {
            Log.v(TAG, "STARTING WITH " + streamtype + "/" + item + "/" + domain);
            Bundle bundle = new Bundle();
            bundle.putString("streamtype", streamtype);
            bundle.putInt("item", item);
            bundle.putInt("domain", domain);
            fragment.setArguments(bundle);
        }
        appFragmentNavigation.resetToFragment(fragment);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null && !intent.getAction().equals(ACTION_MAIN)) {
            handleIntent(intent);
        } else {
            super.onNewIntent(intent);
        }
    }

    @Override
    public void onBackPressed() {
        IAppFragmentNavigation navigation = NavigationProvider.get(this);
        if (navigation.isLastFragment()) {
            finishAffinity();
        } else {
            navigation.onBack();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        Log.e("DebugTheRemote", "onKeyDown code: " + keyCode + ", event: " + event);
        return super.onKeyDown(keyCode, event);
    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        NexxPLAY player = NexxPlayProvider.getInstance();
        if (player != null && player.dispatchKeyEvent(event)) {
            return true;
        }

        return super.dispatchKeyEvent(event);
    }


    @Override
    public void onUserLeaveHint() {
        NexxPLAY player = NexxPlayProvider.getInstance();
        if (player != null) {
            player.onUserLeaveHint();
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        NexxPLAY player = NexxPlayProvider.getInstance();
        if (player != null) {
            player.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        }
    }
}
