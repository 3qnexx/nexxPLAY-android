package tv.nexx.android.testapp.fragments;

import static tv.nexx.android.play.NexxPLAYEnvironment.alwaysInFullscreen;
import static tv.nexx.android.play.NexxPLAYEnvironment.domain;
import static tv.nexx.android.play.NexxPLAYEnvironment.userHash;
import static tv.nexx.android.play.PlayerEvent.DATA;
import static tv.nexx.android.play.PlayerEvent.EVENT;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomappbar.BottomAppBar;

import java.util.HashMap;
import java.util.Map;

import tv.nexx.android.play.Event;
import tv.nexx.android.play.INexxPLAY;
import tv.nexx.android.play.MediaSourceType;
import tv.nexx.android.play.NexxPLAYConfiguration;
import tv.nexx.android.play.NexxPLAYEnvironment;
import tv.nexx.android.play.NexxPLAYNotification;
import tv.nexx.android.play.PlayMode;
import tv.nexx.android.play.Streamtype;
import tv.nexx.android.play.player.Player;
import tv.nexx.android.play.util.InternalUtils;
import tv.nexx.android.testapp.R;
import tv.nexx.android.testapp.providers.NavigationProvider;
import tv.nexx.android.testapp.providers.NexxPlayProvider;

public class PlayerFragment extends Fragment implements NexxPLAYNotification.Listener {
    private static final String TAG = PlayerFragment.class.getName();

    private View rootView;

    private LinearLayout notificationLog;
    private ScrollView notificationLogScrollView;

    /**
     * the parameters
     */
    private String provider;
    private String domainID;
    private String mediaID;
    private String playMode;
    private boolean autoplay;
    private boolean autoNext;
    private boolean disableAds;
    private String dataMode;
    private boolean hidePrevNext;
    private boolean forcePrevNext;
    private String exitMode;
    private int startPosition;
    private float delay;
    private boolean startFullscreen;
    private String mediaSourceType;
    private String streamingFilter;
    private String adType;

    // player instance
    private INexxPLAY player;

    private BottomAppBar bottomOptions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle arguments = getArguments();
            provider = arguments.getString("provider");
            domainID = arguments.getString("domainID");
            mediaID = arguments.getString("mediaID");
            playMode = arguments.getString("playMode");
            autoplay = arguments.getBoolean("autoplay");
            autoNext = arguments.getBoolean("autoNext");
            disableAds = arguments.getBoolean("disableAds");
            dataMode = arguments.getString("dataMode");
            hidePrevNext = arguments.getBoolean("hidePrevNext");
            forcePrevNext = arguments.getBoolean("forcePrevNext");
            exitMode = arguments.getString("exitMode");
            startPosition = arguments.getInt("startPosition");
            delay = arguments.getFloat("delay");
            startFullscreen = arguments.getBoolean("startFullscreen");
            mediaSourceType = arguments.getString("mediaSourceType");
            streamingFilter = arguments.getString("streamingFilter");
            adType = arguments.getString("adType");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_player, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notificationLog = rootView.findViewById(R.id.ll_notification_log_input);
        notificationLogScrollView = rootView.findViewById(R.id.sc_notification_log_input);

        bottomOptions = rootView.findViewById(R.id.bottomOptions);
        bottomOptions.setOnMenuItemClickListener((item) -> {
            Log.v(TAG, "HANDLING BOTTOM CMD: " + item.getTitle());
            switch (item.getItemId()) {
                case R.id.cmd_play:
                    Log.v(TAG, "STARTING PLAY");
                    startPlay();
                    break;
                case R.id.cmd_pause:
                    Log.v(TAG, "STARTING ÜAUSE");
                    startPause();
                    break;
                case R.id.cmd_prev:
                    Log.v(TAG, "STARTING PREV");
                    startPrev();
                    break;
                case R.id.cmd_next:
                    Log.v(TAG, "STARTING NEXT");
                    startNext();
                    break;
                case R.id.cmd_mute:
                    Log.v(TAG, "STARTING MUTE");
                    startMute();
                    break;
                case R.id.cmd_unmute:
                    Log.v(TAG, "STARTING UNMUTE");
                    startUnmute();
                    break;
                case R.id.cmd_seekby:
                    Log.v(TAG, "STARTING SEEKBY");
                    startSeekBy();
                    break;
                case R.id.cmd_swap:
                    Log.v(TAG, "STARTING SWAP");
                    startSwapMedia();
                    break;
                case R.id.cmd_clearcache:
                    Log.v(TAG, "STARTING CLEAR CACHE");
                    startClearCache();
                    break;
            }
            return true;
        });

        rootView.findViewById(R.id.backtosettings).setOnClickListener(v -> {
            NavigationProvider.get(getActivity()).onBack();
        });

        if (savedInstanceState == null) {

            ViewGroup root = rootView.findViewById(R.id.root);

            player = NexxPlayProvider.init(getContext(), root, getActivity().getWindow());
            NexxPLAYEnvironment env = new NexxPLAYEnvironment(new HashMap<String, Object>() {{
                put(domain, domainID);
                put(contentURITemplate,"app://tv.nexx.android.play.testapp/watch/{domain}/");
                put(contentIDTemplate,"{GID}:{startAt}");
                put(alwaysInFullscreen, (startFullscreen ? 1 : 0));
            }});
            player.setEnvironment(env);

            Log.d(TAG, "mediaSourceType: " + mediaSourceType);

            NexxPLAYConfiguration conf = new NexxPLAYConfiguration(new HashMap<String, Object>() {{
                put(NexxPLAYConfiguration.dataMode, dataMode);
                put(NexxPLAYConfiguration.hidePrevNext, hidePrevNext ? 1 : 0);
                put(NexxPLAYConfiguration.forcePrevNext, forcePrevNext ? 1 : 0);
                put(NexxPLAYConfiguration.autoPlay, autoplay ? 1 : 0);
                put(NexxPLAYConfiguration.autoNext, autoNext ? 1 : 0);
                put(NexxPLAYConfiguration.exitMode, exitMode);
                put(NexxPLAYConfiguration.streamingFilter, streamingFilter);
                put(NexxPLAYConfiguration.delay, delay);
                put(NexxPLAYConfiguration.startPosition, startPosition);
                put(NexxPLAYConfiguration.adType, adType);
                put(NexxPLAYConfiguration.disableAds, disableAds ? 1 : 0);
            }});

            if (mediaSourceType.equals(MediaSourceType.NORMAL.toString())) {
                player.startPlay(playMode, mediaID, conf);
            } else if (mediaSourceType.equals(MediaSourceType.REMOTE.toString())) {
                player.startPlayWithRemoteMedia(playMode, mediaID, provider, conf);
            } else {
                player.startPlayWithGlobalID(mediaID, conf);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.player != null) {
            this.player.addPlaystateListener(this);
            this.player.onActivityResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.player != null) {
            player.removePlaystateListener(this);
            player.onActivityPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.player != null) {
            player.onActivityDestroyed();
            NexxPlayProvider.cancel();
        }
    }

    @Override
    public void onPlayerStateChanged(boolean b, Player.State state) {
        Log.e(TAG, "Player state changed: " + state + ", play when ready " + b);

        if (state.equals(Player.State.PLAYING)) {
            getActivity().setTitle(player.getMediaData().getTitle());
        }
    }

    @Override
    public void onPlayerError(String s, String s1) {
        Log.e(TAG, "Player error: " + s + " --- " + s1);
    }

    @Override
    public void onFullScreen(boolean isFullScreen) {
        Log.e(TAG, "onFullScreen: " + isFullScreen);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onPlayerEvent(NexxPLAYNotification.IPlayerEvent playerEvent) {
        if (getContext() != null) {
            if (Event.second.equals(playerEvent.getBody().get(EVENT))) {
                return;
            }
            LinearLayout.LayoutParams notificationEventLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            LinearLayout.LayoutParams notificationInfoLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            notificationEventLayoutParams.setMargins(20, 30, 20, 5);
            notificationInfoLayoutParams.setMargins(20, 0, 20, 5);

            TextView notificationEvent = new TextView(getContext());
            notificationEvent.setId(View.generateViewId());

            TextView notificationInfo = new TextView(getContext());
            notificationInfo.setId(View.generateViewId());

            notificationLog.addView(notificationEvent, notificationEventLayoutParams);
            notificationLog.addView(notificationInfo, notificationInfoLayoutParams);

            notificationEvent.setTextColor(Color.parseColor("#FFFFFF"));
            notificationEvent.setTextSize(16);
            notificationEvent.setTypeface(null, Typeface.BOLD);

            String event = playerEvent.getEvent();
            if (!event.isEmpty()) {
                notificationEvent.setText(event.toUpperCase());
            }


            StringBuilder info = new StringBuilder(
                    "mediaID: " + playerEvent.getMediaID() + ", " +
                            "globalID: " + playerEvent.getGlobalID() + ", " +
                            "playerMode: " + playerEvent.getPlayerMode() + ", " +
                            "streamtype: " + playerEvent.getStreamtype() + ", " +
                            "mediaIndex: " + playerEvent.getMediaIndex() + ", " +
                            "remoteDevice: " + playerEvent.getRemoteDevice());

            Map<Object, Object> data = playerEvent.getData();
            if (data != null) {
                for (Map.Entry<Object, Object> set : data.entrySet()) {
                    info.append(", ").append(set.getKey()).append(": ").append(set.getValue());
                }
            }

            notificationInfo.setTextColor(Color.parseColor("#999999"));
            notificationInfo.setTextSize(12);
            notificationInfo.setText(info);

            notificationLogScrollView.post(() -> {
                if (!InternalUtils.isTV()) {
                    notificationLogScrollView.fullScroll(View.FOCUS_DOWN);
                }
            });

            Log.v(TAG, "Player event in App: " + event + ", data = " + playerEvent.getBody().get(DATA));

            if (Event.metadata.toString().equals(event)) {
                rootView.findViewById(R.id.cmd_unmute).setVisibility(View.GONE);
                rootView.findViewById(R.id.cmd_pause).setVisibility(View.GONE);
                if (Streamtype.isLive(playerEvent.getStreamtype())) {
                    rootView.findViewById(R.id.cmd_seekby).setVisibility(View.GONE);
                } else {
                    rootView.findViewById(R.id.cmd_seekby).setVisibility(View.VISIBLE);
                }
                if ((PlayMode.isContainer(playerEvent.getPlayerMode())) || (PlayMode.isList(playerEvent.getPlayerMode()))) {
                    rootView.findViewById(R.id.cmd_prev).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.cmd_next).setVisibility(View.VISIBLE);
                } else {
                    rootView.findViewById(R.id.cmd_prev).setVisibility(View.GONE);
                    rootView.findViewById(R.id.cmd_next).setVisibility(View.GONE);
                }
            } else if (Event.mute.toString().equals(event)) {
                rootView.findViewById(R.id.cmd_mute).setVisibility(View.GONE);
                rootView.findViewById(R.id.cmd_unmute).setVisibility(View.VISIBLE);
            } else if (Event.unmute.toString().equals(event)) {
                rootView.findViewById(R.id.cmd_mute).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.cmd_unmute).setVisibility(View.GONE);
            } else if ((Event.play.toString().equals(event)) || (Event.startplay.toString().equals(event))) {
                rootView.findViewById(R.id.cmd_play).setVisibility(View.GONE);
                rootView.findViewById(R.id.cmd_pause).setVisibility(View.VISIBLE);
            } else if (Event.pause.toString().equals(event)) {
                rootView.findViewById(R.id.cmd_play).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.cmd_pause).setVisibility(View.GONE);
            }
        }
    }

    public void startToggle() {
        player.toggle();
    }

    public void startPlay() {
        player.play();
    }

    public void startPause() {
        player.pause();
    }

    public void startPrev() {
        player.previous();
    }

    public void startNext() {
        player.next();
    }

    public void startMute() {
        player.mute();
    }

    public void startUnmute() {
        player.unmute();
    }

    public void startSeekBy() {
        player.seekBy(30);
    }

    public void startClearCache() {
        player.clearCache();
    }

    public void startSwapMedia() {
        player.swapToMediaItem("0", "video", 0, 0);
    }
}
