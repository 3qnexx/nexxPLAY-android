package tv.nexx.android.testapp.fragments;

import static tv.nexx.android.play.NexxPLAYEnvironment.alwaysInFullscreen;
import static tv.nexx.android.play.NexxPLAYEnvironment.castContext;
import static tv.nexx.android.play.NexxPLAYEnvironment.contentIDTemplate;
import static tv.nexx.android.play.NexxPLAYEnvironment.contentURITemplate;
import static tv.nexx.android.play.NexxPLAYEnvironment.domain;
import static tv.nexx.android.play.NexxPLAYEnvironment.respectViewSizeForAudioOnTV;
import static tv.nexx.android.play.NexxPLAYEnvironment.wearCapabilityString;
import static tv.nexx.android.play.PlayerEvent.DATA;
import static tv.nexx.android.play.PlayerEvent.EVENT;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
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
import com.google.android.material.chip.Chip;

import java.util.HashMap;
import java.util.Map;

import tv.nexx.android.play.INexxPLAY;
import tv.nexx.android.play.NexxPLAYConfiguration;
import tv.nexx.android.play.NexxPLAYEnvironment;
import tv.nexx.android.play.NexxPLAYNotification;
import tv.nexx.android.play.Streamtype;
import tv.nexx.android.play.device.DeviceManager;
import tv.nexx.android.play.enums.Event;
import tv.nexx.android.play.enums.MediaSourceType;
import tv.nexx.android.play.enums.PlayMode;
import tv.nexx.android.play.player.IPlayer;
import tv.nexx.android.play.util.Utils;
import tv.nexx.android.testapp.MainActivity;
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
    private String viewSize;
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
    private Chip storageChip;

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
            viewSize = arguments.getString("viewSize");
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

        storageChip = rootView.findViewById(R.id.storagechip);

        bottomOptions = rootView.findViewById(R.id.bottomOptions);
        bottomOptions.setOnMenuItemClickListener((item) -> {
            Utils.log(TAG, "HANDLING BOTTOM CMD: " + item.getTitle());
            switch (item.getItemId()) {
                case R.id.cmd_play:
                    Utils.log(TAG, "STARTING PLAY");
                    startPlay();
                    break;
                case R.id.cmd_pause:
                    Utils.log(TAG, "STARTING ÜAUSE");
                    startPause();
                    break;
                case R.id.cmd_prev:
                    Utils.log(TAG, "STARTING PREV");
                    startPrev();
                    break;
                case R.id.cmd_next:
                    Utils.log(TAG, "STARTING NEXT");
                    startNext();
                    break;
                case R.id.cmd_mute:
                    Utils.log(TAG, "STARTING MUTE");
                    startMute();
                    break;
                case R.id.cmd_unmute:
                    Utils.log(TAG, "STARTING UNMUTE");
                    startUnmute();
                    break;
                case R.id.cmd_seekby:
                    Utils.log(TAG, "STARTING SEEKBY");
                    startSeekBy();
                    break;
                case R.id.cmd_swap:
                    Utils.log(TAG, "STARTING SWAP");
                    startSwapMedia();
                    break;
                case R.id.cmd_clearcache:
                    Utils.log(TAG, "STARTING CLEAR CACHE");
                    startClearCache();
                    break;
            }
            return true;
        });

        bottomOptions.getMenu().setGroupVisible(R.id.playback, false);

        rootView.findViewById(R.id.backtosettings).setOnClickListener(v -> {
            DeviceManager.getInstance().performHapticFeedback(DeviceManager.getInstance().HAPTIC_FEEDBACK_EFFECT_EXTENDED);
            NavigationProvider.get(getActivity()).onBack();
        });

        if (savedInstanceState == null) {

            ViewGroup root = rootView.findViewById(R.id.root);

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            if (viewSize != null && viewSize.equals("mini")) {
                ViewGroup.LayoutParams params = root.getLayoutParams();
                params.width = Math.min(size.x, size.y);
                params.height = (int) getResources().getDimension(R.dimen.mini_player_height);
                root.setLayoutParams(params);
            } else if (viewSize != null && viewSize.equals("micro")) {
                ViewGroup.LayoutParams params = root.getLayoutParams();
                params.width = Math.min(size.x, size.y);
                params.height = (int) getResources().getDimension(R.dimen.micro_player_height);
                root.setLayoutParams(params);
            } else {
                ViewGroup.LayoutParams params = root.getLayoutParams();
                params.width = Math.min(size.x, size.y);
                params.height = (int) (params.width / 16.0 * 9);
                root.setLayoutParams(params);
            }

            player = NexxPlayProvider.init(getContext(), root, getActivity().getWindow());

            Map<String, Object> envData = new HashMap<>();
            envData.put(domain, domainID);

            //ENABLE, IF CHROMECAST SHALL BE USED
            //envData.put(castContext, ((MainActivity) getActivity()).getCastContext());

            //ENABLE, IF TV-RECOMMENDATIONS SHALL BE USED
            //envData.put(contentURITemplate, "YOUR-CONTENT-URI-TEMPLATE");
            //envData.put(contentIDTemplate, "YOUR-CONTENT-ID-TEMPLATE");

            envData.put(alwaysInFullscreen, (startFullscreen ? 1 : 0));
            envData.put(respectViewSizeForAudioOnTV, 1);


            player.setEnvironment(new NexxPLAYEnvironment(envData));
            updateStorage();

            Map<String, Object> confData = new HashMap<>();
            confData.put(NexxPLAYConfiguration.dataMode, dataMode);
            confData.put(NexxPLAYConfiguration.hidePrevNext, hidePrevNext ? 1 : 0);
            confData.put(NexxPLAYConfiguration.forcePrevNext, forcePrevNext ? 1 : 0);
            confData.put(NexxPLAYConfiguration.autoPlay, autoplay ? 1 : 0);
            confData.put(NexxPLAYConfiguration.autoNext, autoNext ? 1 : 0);
            confData.put(NexxPLAYConfiguration.exitMode, exitMode);
            confData.put(NexxPLAYConfiguration.streamingFilter, streamingFilter);
            confData.put(NexxPLAYConfiguration.delay, delay);
            confData.put(NexxPLAYConfiguration.startPosition, startPosition);
            confData.put(NexxPLAYConfiguration.adType, adType);
            confData.put(NexxPLAYConfiguration.disableAds, disableAds ? 1 : 0);
            confData.put(NexxPLAYConfiguration.enableInteractions, 0);
            confData.put(NexxPLAYConfiguration.enableStartScreenTitle, 1);

            Utils.log(TAG, "STARTING PLAYER WITH " + playMode + "/" + mediaID);

            if (mediaSourceType.equals(MediaSourceType.NORMAL.toString())) {
                player.startPlay(playMode, mediaID, new NexxPLAYConfiguration(confData));
            } else if (mediaSourceType.equals(MediaSourceType.REMOTE.toString())) {
                player.startPlayWithRemoteMedia(playMode, mediaID, provider, new NexxPLAYConfiguration(confData));
            } else {
                player.startPlayWithGlobalID(mediaID, new NexxPLAYConfiguration(confData));
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
    public void onStop() {
        super.onStop();
        if (this.player != null) {
            player.onActivityStop();
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
    public void onPlayerStateChanged(boolean b, IPlayer.State state) {
        Utils.log(TAG, "Player state changed: " + state + ", play when ready " + b);

        if (state.equals(IPlayer.State.PLAYING)) {
            getActivity().setTitle(player.getCurrentMedia().getTitle());
        }
    }

    @Override
    public void onPlayerError(String s, String s1) {
        Utils.log(TAG, "Player error: " + s + " --- " + s1);
    }

    @Override
    public void onFullScreen(boolean isFullScreen) {
        Utils.log(TAG, "onFullScreen: " + isFullScreen);
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
            notificationEvent.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

            TextView notificationInfo = new TextView(getContext());
            notificationInfo.setId(View.generateViewId());
            notificationInfo.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

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
                if (!DeviceManager.getInstance().isTV()) {
                    notificationLogScrollView.fullScroll(View.FOCUS_DOWN);
                }
            });

            Utils.log(TAG, "Player event in App: " + event + ", data = " + playerEvent.getBody().get(DATA));

            if (Event.metadata.toString().equals(event)) {
                bottomOptions.getMenu().setGroupVisible(R.id.playback, true);
                rootView.findViewById(R.id.cmd_unmute).setVisibility(View.GONE);
                rootView.findViewById(R.id.cmd_pause).setVisibility(View.GONE);
                if (Streamtype.isLive(playerEvent.getStreamtype())) {
                    rootView.findViewById(R.id.cmd_seekby).setVisibility(View.GONE);
                } else {
                    rootView.findViewById(R.id.cmd_seekby).setVisibility(View.VISIBLE);
                }
                if ((PlayMode.isContainer(Utils.getPlayMode(playerEvent.getPlayerMode()))) || (PlayMode.isList(Utils.getPlayMode(playerEvent.getPlayerMode())))) {
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
                if(rootView.findViewById(R.id.cmd_play)!=null) {
                    rootView.findViewById(R.id.cmd_play).setVisibility(View.GONE);
                    rootView.findViewById(R.id.cmd_pause).setVisibility(View.VISIBLE);
                }
            } else if (Event.pause.toString().equals(event)) {
                if(rootView.findViewById(R.id.cmd_play)!=null) {
                    rootView.findViewById(R.id.cmd_play).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.cmd_pause).setVisibility(View.GONE);
                }
            } else if (Event.downloadready.toString().equals(event)) {
                updateStorage();
            }
        }
    }

    protected void updateStorage() {
        if (player != null) {
            long disk = player.diskSpaceUsedForLocalMedia();
            if (disk > 0) {
                String msg = android.text.format.Formatter.formatShortFileSize(getContext(), disk);
                storageChip.setText(msg + " LocalMedia");
                storageChip.setVisibility(View.VISIBLE);
                Utils.log(TAG, "LOCALMEDIA USED DISK SPACE: " + msg);
            } else {
                storageChip.setVisibility(View.GONE);
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
        player.clearLocalMedia(null);
        storageChip.setVisibility(View.GONE);
    }

    public void startSwapMedia() {
        player.swapToMediaItem("MEDIA-ID","MEDIA-STREAMTYPE",0,0);
    }
}