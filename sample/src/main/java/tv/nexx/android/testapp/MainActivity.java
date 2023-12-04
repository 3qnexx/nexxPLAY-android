package tv.nexx.android.testapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.cast.framework.CastContext;

import java.util.concurrent.Executors;

import tv.nexx.android.play.NexxPLAY;
import tv.nexx.android.play.device.DeviceManager;
import tv.nexx.android.testapp.navigation.FrameLayoutFragmentCallback;
import tv.nexx.android.testapp.navigation.FrameLayoutFragmentUsed;
import tv.nexx.android.testapp.navigation.IAppFragmentNavigation;
import tv.nexx.android.testapp.providers.NavigationProvider;
import tv.nexx.android.testapp.providers.NexxPlayProvider;

public class MainActivity extends FragmentActivity implements FrameLayoutFragmentCallback {
    private static final String TAG = MainActivity.class.getCanonicalName();

    private static final String SHORTCUT_VIDEO = "tv.nexx.android.testapp.shortcuts.VIDEO";
    private static final String SHORTCUT_LIVE = "tv.nexx.android.testapp.shortcuts.LIVE";
    private static final String SHORTCUT_AUDIO = "tv.nexx.android.testapp.shortcuts.AUDIO";
    private static final String SHORTCUT_PLAYLIST = "tv.nexx.android.testapp.shortcuts.PLAYLIST";
    private static final String ACTION_MAIN = "android.intent.action.MAIN";
    private static final String ACTION_VIEW = "android.intent.action.VIEW";

    private CastContext mCastContext;
    private MediaSessionCompat mMediaSession;
    private RelativeLayout mainActivityProgressLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        IAppFragmentNavigation appFragmentNavigation = NavigationProvider.get(this);
        appFragmentNavigation.addFragmentCallback(this);
        mMediaSession = new MediaSessionCompat(this, getApplication().getPackageName());

        ConstraintLayout mainParentLayout = findViewById(R.id.main_parent_layout);
        mainActivityProgressLayout = findViewById(R.id.rl_main_activity_progress_layout);
        FrameLayout settingsFrameContainer = findViewById(R.id.settings_frame_container);
        FrameLayout playerFrameContainer = findViewById(R.id.player_frame_container);
        View proportionLine = findViewById(R.id.line);
        appFragmentNavigation.attachViews(mainParentLayout, settingsFrameContainer, playerFrameContainer, proportionLine);

        DeviceManager.getInstance().init(getApplicationContext());

        Log.v("APP", "MEDIASESSION INITIATED");

        CastContext.getSharedInstance(this, Executors.newSingleThreadExecutor()).addOnSuccessListener(castContext -> {
            mCastContext = castContext;
            Log.v("APP", "CAST CONTEXT INITIATED");
        }).addOnFailureListener(exception -> Log.v("APP", "CANNOT INIT CASTCONTEXT: " + exception.getMessage()));

        Intent intent = getIntent();
        if (intent != null) {
            handleIntent(intent);
        } else {
            appFragmentNavigation.showSettings(null);
        }
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBackPressed();
            }
        });
    }

    public CastContext getCastContext() {
        return (mCastContext);
    }

    public MediaSessionCompat getMediaSession() {
        return (mMediaSession);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        Uri uri = intent.getData();
        Log.v(TAG, "STARTED WITH ACTION: " + action);

        if (uri != null) {
            Log.v(TAG, "STARTED WITH DATA: " + uri.toString());
        }

        IAppFragmentNavigation appFragmentNavigation = NavigationProvider.get(this);

        String streamtype = null;
        int item = 0;
        int global = 0;
        int domain = 0;
        Float delay = 0f;

        switch (action) {
            case SHORTCUT_VIDEO:
                streamtype = "entry";
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
            case ACTION_VIEW:
                if (uri != null) {
                    try {
                        String path = uri.getPath();
                        if (path != null) {
                            path = path.replace("/watch/", "");
                            String[] parts = path.split("/");
                            domain = Integer.parseInt(parts[0]);
                            String[] items = parts[1].split(":");
                            global = Integer.parseInt(items[0]);
                            if (items.length > 1) {
                                delay = Float.parseFloat(items[1]);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "tv.nexx.android.widget.OPEN":
                streamtype = intent.getStringExtra("streamtype");
                domain = intent.getIntExtra("domainID", 0);
                item = intent.getIntExtra("itemID", 0);
                break;
        }

        Bundle bundle = null;
        if (streamtype != null) {
            Log.v(TAG, "STARTING WITH " + streamtype + "/" + item + "/" + domain + " WITH DELAY OF " + delay);

            bundle = new Bundle();
            bundle.putString("streamtype", streamtype);
            bundle.putInt("item", item);
            bundle.putInt("domain", domain);
            bundle.putInt("global", global);
            bundle.putFloat("delay", delay);

        } else if (global > 0) {
            Log.v(TAG, "STARTING WITH GLOBAL " + global + "/" + domain + " WITH DELAY OF " + delay);

            bundle = new Bundle();
            bundle.putString("streamtype", null);
            bundle.putInt("item", 0);
            bundle.putInt("domain", domain);
            bundle.putInt("global", global);
            bundle.putFloat("delay", delay);
        }
        appFragmentNavigation.showSettings(bundle);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null && intent.getAction() != null && !intent.getAction().equals(ACTION_MAIN)) {
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
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, @NonNull Configuration newConfig) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        }
        NexxPLAY player = NexxPlayProvider.getInstance();
        if (player != null) {
            player.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        }
        NavigationProvider.get(this).setFrameLayoutFragmentUsed(
                isInPictureInPictureMode ?
                        FrameLayoutFragmentUsed.TWO_FRAME_LAYOUT_USED_PIP_ENTER :
                        FrameLayoutFragmentUsed.TWO_FRAME_LAYOUT_USED_PIP_OUT
        );
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        NavigationProvider.get(this).handleFrameLayoutVisibility(newConfig.orientation);
    }

    @Override
    public void update(@NonNull FrameLayoutFragmentUsed frameLayoutFragmentUsed) {
        NavigationProvider.get(this).handleFrameLayoutVisibility(getResources().getConfiguration().orientation);
    }

    public void setLoadingViewVisibility(boolean isVisible) {
        mainActivityProgressLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}

