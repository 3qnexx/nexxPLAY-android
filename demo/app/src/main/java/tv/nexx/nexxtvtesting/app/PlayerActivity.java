package tv.nexx.nexxtvtesting.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;

import java.io.IOException;
import java.io.InputStream;

import tv.nexx.android.player.AutoPlayMode;
import tv.nexx.android.player.AutoPlayNextMode;
import tv.nexx.android.player.ExitPlayMode;
import tv.nexx.android.player.NexxFactory;
import tv.nexx.android.player.NexxPlayer;
import tv.nexx.android.player.NexxPlayerAndroid;
import tv.nexx.android.player.NexxPlayerNotification;
import tv.nexx.android.player.PlayerEvent;
import tv.nexx.android.player.Utils;
import tv.nexx.android.player.control.LoaderSkin;
import tv.nexx.android.player.omnia.datamode.DataMode;
import tv.nexx.android.player.player.Player;

public class PlayerActivity extends Activity implements NexxPlayerNotification.Listener {

    private static final String TAG = PlayerActivity.class.getName();

    /**
     * the parameters
     */
    private String customerID;
    private String mediaID;
    private String adType;
    private String streamingFilter;
    private String playMode;
    private Boolean autoplay;
    private Boolean revolverPlay;
    private int exitPlayMode;
    private String commercial;
    private String loaderSkin;

    // player instance
    private NexxPlayerAndroid player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);


        if (savedInstanceState == null) {
            // get parameters
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey("customerID")) {
                customerID = bundle.getString("customerID");
                mediaID = bundle.getString("mediaID");
                playMode = bundle.getString("playMode");
                loaderSkin = bundle.getString("loaderSkin");
                streamingFilter = bundle.getString("streamingFilter");

                player = NexxFactory.createNexxPlayer(this);

                ViewGroup root = (ViewGroup) findViewById(R.id.root);

                player.setViewRoot(root);
                player.setWindow(getWindow());
                NexxPlayer np = (NexxPlayer) player;

                if (bundle.containsKey("autoplay")) {
                    autoplay = bundle.getBoolean("autoplay");
                    adType = bundle.getString("adType");
                    revolverPlay = bundle.getBoolean("revolverPlay");
                    exitPlayMode = bundle.getInt("exitPlayMode");
                    commercial = bundle.getString("commercial");

                    if (bundle.containsKey("staticDataMode") && bundle.getBoolean("staticDataMode")) {
                        np.setDataMode(DataMode.STATIC);
                    }

                    // auto play
                    AutoPlayMode autoPlayMode = (autoplay) ? AutoPlayMode.StartImmediately : AutoPlayMode.ShowStartScreen;
                    np.overrideAutoPlay(autoPlayMode);

                    // revolver play
                    AutoPlayNextMode autoPlayNextMode = (revolverPlay) ? AutoPlayNextMode.EnableRevolverPlay : AutoPlayNextMode.DisableRevolverPlay;
                    np.overrideAutoPlayNext(autoPlayNextMode);

                    // exit play mode
                    ExitPlayMode o_exitPlayMode = Utils.getExitMode(exitPlayMode);
                    np.overrideExitPlayMode(o_exitPlayMode);

                    LoaderSkin skin = LoaderSkin.DEFAULT;

                    if (loaderSkin.equals("metro")) {
                        skin = LoaderSkin.METRO;
                    } else if (loaderSkin.equals("material")) {
                        skin = LoaderSkin.MATERIAL;
                    } else if (loaderSkin.equals("double bounce")) {
                        skin = LoaderSkin.DOUBLE_BOUNCE;
                    }

                    if (!adType.equals("no override")) {
                        np.overrideAdType(adType.toLowerCase());
                    }

                    np.setLoaderSkin(skin);
                    if (!commercial.equals("") && !commercial.equals("=None")) {
                        np.overridePreroll(commercial);
                    }
                }

                np.startPlay(null, customerID, Utils.getPlayMode(playMode), mediaID, 0, 0);
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {

        Log.e("DebugTheRemote", "onKeyDown code: " + keyCode + ", event: " + event);

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        Log.e("DebugTheRemote", "dispatchKeyEvent code: " + event.getKeyCode());

        return super.dispatchKeyEvent(event);

    }


    @Override
    public void onResume() {
        super.onResume();

        if (this.player != null) {
            ((NexxPlayerNotification) this.player).addEventListener(this);
            player.onActivityResume();
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.player != null) {
            player.onActivityPause();
            ((NexxPlayerNotification) this.player).removeEventListener(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.player != null) {
            player.onActivityDestroyed();
        }
    }

    @Override
    public void onPlayerStateChanged(boolean b, Player.State state) {
        Log.e(TAG, "Player state changed: " + state + ", play when ready " + b);
    }

    @Override
    public void onVideoSizeChanged(int i, int i1, float v) {

    }

    @Override
    public void onPlayerError(String s, String s1) {
        Log.e(TAG, "Player error: " + s + " --- " + s1);
    }

    @Override
    public void onFullScreen(boolean b) {

    }

    @Override
    public void onPlayerEvent(PlayerEvent playerEvent, Object o) {
        if (playerEvent == PlayerEvent.AD_CLICKED) {
            Log.e("EVENTS in App", "AD CLICKED EVENT!");
        }

        if (playerEvent == PlayerEvent.AD_STARTED) {
            Log.e("EVENTS in App", "AD STARTED EVENT!");
        }

        if (playerEvent == PlayerEvent.AD_ENDED) {
            Log.e("EVENTS in App", "AD ENDED EVENT!");
        }

        if (playerEvent == playerEvent.AD_ERROR) {
            Log.e("EVENTS in App", "AD ERROR!");
        }

        if (playerEvent == playerEvent.AD_RESUMED) {
            Log.e("EVENTS in App", "AD RESUMED!");
        }

        Log.e(TAG, "Player event in App: " + playerEvent.name());
    }

}
