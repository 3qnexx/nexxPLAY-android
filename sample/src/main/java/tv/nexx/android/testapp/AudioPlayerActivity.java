package tv.nexx.android.testapp;

import static tv.nexx.android.play.NexxPLAYEnvironment.affiliatePartner;
import static tv.nexx.android.play.NexxPLAYEnvironment.alwaysInFullscreen;
import static tv.nexx.android.play.NexxPLAYEnvironment.consentString;
import static tv.nexx.android.play.NexxPLAYEnvironment.contextReference;
import static tv.nexx.android.play.NexxPLAYEnvironment.deliveryPartner;
import static tv.nexx.android.play.NexxPLAYEnvironment.domain;
import static tv.nexx.android.play.NexxPLAYEnvironment.externalUserReference;
import static tv.nexx.android.play.NexxPLAYEnvironment.language;
import static tv.nexx.android.play.NexxPLAYEnvironment.sessionID;
import static tv.nexx.android.play.NexxPLAYEnvironment.trackingOptOuted;
import static tv.nexx.android.play.NexxPLAYEnvironment.useSSL;
import static tv.nexx.android.play.NexxPLAYEnvironment.userHash;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.HashMap;

import tv.nexx.android.play.NexxPLAY;
import tv.nexx.android.play.NexxPLAYConfiguration;
import tv.nexx.android.play.NexxPLAYEnvironment;
import tv.nexx.android.play.NexxPLAYNotification;
import tv.nexx.android.play.NexxPLAYNotification.IPlayerEvent;
import tv.nexx.android.play.PlayerEvent;
import tv.nexx.android.play.player.Player;

public class AudioPlayerActivity extends Activity implements NexxPLAYNotification.Listener {

    public static final String MEDIA_ID = "a4241ebd-3518-11eb-b839-0cc47a188158";
    public static final int DOMAIN_ID = 484;

    private TextView debugOutput;
    private NexxPLAY player;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        this.debugOutput = findViewById(R.id.debug_output);
        this.debugOutput.setMovementMethod(new ScrollingMovementMethod());

        this.initPlayer();
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, Player.State current) {
        this.debugOutput.append(current.name() + "\n");
    }

    @Override
    public void onPlayerError(String reason, String details) {
        this.debugOutput.append("Error:" + reason + ", " + details + "\n");
    }

    @Override
    public void onFullScreen(boolean fullScreen) {

    }

    @Override
    public void onPlayerEvent(IPlayerEvent event) {
        this.debugOutput.append(event.getBody().get(PlayerEvent.EVENT) + "\n");
    }


    private void initPlayer() {
        ViewGroup root = findViewById(R.id.player_root);

        player = new NexxPLAY(this, root, getWindow());
        NexxPLAYEnvironment env = new NexxPLAYEnvironment(new HashMap<String, Object>() {{
            put(domain, DOMAIN_ID);
            put(sessionID, null);
            put(language, null);
            put(userHash, "");
            put(useSSL, true);
            put(trackingOptOuted, false);
            put(alwaysInFullscreen, false);
            put(consentString, "");
            put(affiliatePartner, 0);
            put(deliveryPartner, 0);
            put(contextReference, "");
            put(externalUserReference, "");
        }});
        player.setEnvironment(env);
        this.player.addPlaystateListener(this);

        //player.setDataMode(DataMode.OFFLINE);
        //player.startPlay(null, "" + DOMAIN_ID, PlayMode.audio, MEDIA_ID, 0, 0);
        player.startPlayWithRemoteMedia("audio", MEDIA_ID, "3q", new NexxPLAYConfiguration());

    }


    @Override
    public void onResume() {
        super.onResume();
        if (this.player != null) {
            player.onActivityResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.player != null) {
            player.onActivityPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.player != null) {
            player.onActivityDestroyed();
        }
    }
}
