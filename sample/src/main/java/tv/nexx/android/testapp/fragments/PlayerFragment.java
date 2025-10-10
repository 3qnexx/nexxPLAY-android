package tv.nexx.android.testapp.fragments;

import static tv.nexx.android.play.NexxPLAYEnvironment.adManager;
import static tv.nexx.android.play.NexxPLAYEnvironment.alwaysInFullscreen;
import static tv.nexx.android.play.NexxPLAYEnvironment.castContext;
import static tv.nexx.android.play.NexxPLAYEnvironment.contentIDTemplate;
import static tv.nexx.android.play.NexxPLAYEnvironment.contentURITemplate;
import static tv.nexx.android.play.NexxPLAYEnvironment.domain;
import static tv.nexx.android.play.NexxPLAYEnvironment.mediaSession;
import static tv.nexx.android.play.NexxPLAYEnvironment.respectViewSizeForAudio;
import static tv.nexx.android.play.PlayerEvent.DATA;
import static tv.nexx.android.play.PlayerEvent.EVENT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.window.java.layout.WindowInfoTrackerCallbackAdapter;
import androidx.window.layout.DisplayFeature;
import androidx.window.layout.FoldingFeature;
import androidx.window.layout.WindowInfoTracker;
import androidx.window.layout.WindowLayoutInfo;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.chip.Chip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.nexx.android.admanager.NexxPlayAdManager;
import tv.nexx.android.play.CurrentPlaybackState;
import tv.nexx.android.play.INexxPLAY;
import tv.nexx.android.play.MediaData;
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
import tv.nexx.android.testapp.navigation.FoldableMode;
import tv.nexx.android.testapp.navigation.FrameLayoutFragmentUsed;
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
    private boolean autoPlay;
    private boolean startMuted;
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

    // player instance
    private INexxPLAY player;

    private BottomAppBar bottomOptions;
    private Chip storageChip;

    private WindowInfoTrackerCallbackAdapter windowInfoTracker;
    private final LayoutStateChangeCallback layoutStateChangeCallback =
            new LayoutStateChangeCallback();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle arguments = getArguments();
            updatePlayerValues(arguments);
        }
        if (DeviceManager.getInstance().isMobileDevice()) {
            windowInfoTracker =
                    new WindowInfoTrackerCallbackAdapter(WindowInfoTracker.getOrCreate(requireContext()));
        }
    }

    private void updatePlayerValues(Bundle arguments) {
        provider = arguments.getString("provider");
        domainID = arguments.getString("domainID");
        mediaID = arguments.getString("mediaID");
        playMode = arguments.getString("playMode");
        autoPlay = arguments.getBoolean("autoPlay");
        startMuted = arguments.getBoolean("startMuted");
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_player, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavigationProvider.get(requireActivity()).setFrameLayoutFragmentUsed(FrameLayoutFragmentUsed.TWO_FRAME_LAYOUT_USED);

        notificationLog = rootView.findViewById(R.id.ll_notification_log_input);
        notificationLogScrollView = rootView.findViewById(R.id.sc_notification_log_input);

        storageChip = rootView.findViewById(R.id.storagechip);

        bottomOptions = rootView.findViewById(R.id.bottomOptions);
        bottomOptions.setOnMenuItemClickListener(item -> {
            Utils.log(TAG, "HANDLING BOTTOM CMD: " + item.getTitle());
            switch (item.getItemId()) {
                case R.id.cmd_play -> {
                    Utils.log(TAG, "STARTING PLAY");
                    startPlay();
                }
                case R.id.cmd_pause -> {
                    Utils.log(TAG, "STARTING PAUSE");
                    startPause();
                }
                case R.id.cmd_prev -> {
                    Utils.log(TAG, "STARTING PREV");
                    startPrev();
                }
                case R.id.cmd_next -> {
                    Utils.log(TAG, "STARTING NEXT");
                    startNext();
                }
                case R.id.cmd_mute -> {
                    Utils.log(TAG, "STARTING MUTE");
                    startMute();
                }
                case R.id.cmd_unmute -> {
                    Utils.log(TAG, "STARTING UNMUTE");
                    startUnmute();
                }
                case R.id.cmd_seekby -> {
                    Utils.log(TAG, "STARTING SEEKBY");
                    startSeekBy();
                }
                case R.id.cmd_swap -> {
                    Utils.log(TAG, "STARTING SWAP");
                    startSwapMedia();
                }
                case R.id.cmd_clearcache -> {
                    Utils.log(TAG, "STARTING CLEAR CACHE");
                    startClearCache();
                }
            }
            return true;
        });

        bottomOptions.getMenu().setGroupVisible(R.id.playback, false);

        rootView.findViewById(R.id.backtosettings).setOnClickListener(v -> {
            DeviceManager.getInstance().performHapticFeedback(DeviceManager.getInstance().HAPTIC_FEEDBACK_EFFECT_EXTENDED);
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        if (savedInstanceState == null) {

            ViewGroup root = rootView.findViewById(R.id.root);

            Point size = getScreenSize();
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

            player = NexxPlayProvider.init(getContext(), root, requireActivity().getWindow());
            setupPlayerValuesAndStart();
        }
        onConfigurationChanged(requireActivity().getResources().getConfiguration().orientation);
    }

    @SuppressWarnings("deprecation")
    private Point getScreenSize() {
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowManager wm = requireContext().getSystemService(WindowManager.class);
            if (wm != null) {
                WindowMetrics metrics = wm.getCurrentWindowMetrics();
                Rect bounds = metrics.getBounds();
                size.x = bounds.width();
                size.y = bounds.height();
            }
        } else {
            Display display = ((WindowManager) requireContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            display.getSize(size);
        }
        return size;
    }


    private void setupPlayerValuesAndStart() {
        Map<String, Object> envData = new HashMap<>();
        envData.put(domain, domainID);
        envData.put(castContext, ((MainActivity) getActivity()).getCastContext());
        envData.put(mediaSession, ((MainActivity) getActivity()).getMediaSession());
        envData.put(contentURITemplate, "nexxplay://tv.nexx.android.play.testapp/watch/{domain}/");
        envData.put(contentIDTemplate, "{GID}:{startAt}");

            /*
            envData.put(platformVariant, "previewlink");
            envData.put(platformVariantIndex, 1445);
            envData.put(campaign, 3);
            envData.put(userHash, "1HWZXB120122YXV");
            envData.put(consentString, "CPFnsYuPFnsYuAfOBBDEBYCsAP_AAH_AAAigHjwCgAWABigEQARIAmADFAGsASCAvMBtgDxwPHgFAAsADFAIgAiQBMAGKANYAkEBeYDbAHjgAA");
             */

        envData.put(alwaysInFullscreen, (startFullscreen ? 1 : 0));
        envData.put(respectViewSizeForAudio, 1);

        if (!disableAds) {
            Utils.log(TAG, "ADDING ADMANAGER TO SDK");
            envData.put(adManager, new NexxPlayAdManager(requireContext()));
        }


        player.setEnvironment(new NexxPLAYEnvironment(envData));
        updateStorage();

        Map<String, Object> confData = new HashMap<>();
        confData.put(NexxPLAYConfiguration.dataMode, dataMode);
        confData.put(NexxPLAYConfiguration.hidePrevNext, hidePrevNext ? 1 : 0);
        confData.put(NexxPLAYConfiguration.forcePrevNext, forcePrevNext ? 1 : 0);
        confData.put(NexxPLAYConfiguration.autoPlay, autoPlay ? 1 : 0);
        confData.put(NexxPLAYConfiguration.autoNext, autoNext ? 1 : 0);
        confData.put(NexxPLAYConfiguration.startMuted, startMuted ? 1 : 0);
        confData.put(NexxPLAYConfiguration.adStartWhenMuted, 0);
        confData.put(NexxPLAYConfiguration.exitMode, exitMode);
        confData.put(NexxPLAYConfiguration.streamingFilter, streamingFilter);
        confData.put(NexxPLAYConfiguration.delay, delay);
        confData.put(NexxPLAYConfiguration.startPosition, startPosition);
        confData.put(NexxPLAYConfiguration.notificationIcon, R.drawable.widget_icon);

        if (viewSize != null && viewSize.equals("hero")) {
            confData.put(NexxPLAYConfiguration.audioSkin, viewSize);
        }

        confData.put(NexxPLAYConfiguration.playOnExistingCastSession, 1);

        confData.put(NexxPLAYConfiguration.disableAds, disableAds ? 1 : 0);
        confData.put(NexxPLAYConfiguration.enableInteractions, 1);

        Utils.log(TAG, "STARTING PLAYER WITH " + playMode + "/" + mediaID);

        if (mediaSourceType.equals(MediaSourceType.NORMAL.toString())) {
            player.startPlay(playMode, mediaID, new NexxPLAYConfiguration(confData));
        } else if (mediaSourceType.equals(MediaSourceType.REMOTE.toString())) {
            player.startPlayWithRemoteMedia(playMode, mediaID, provider, new NexxPLAYConfiguration(confData));
        } else {
            player.startPlayWithGlobalID(mediaID, new NexxPLAYConfiguration(confData));
        }
    }

    private void hideLoadingViewVisibility() {
        if (requireActivity() instanceof MainActivity activity) {
            activity.setLoadingViewVisibility(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideLoadingViewVisibility();
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
    public void onStart() {
        super.onStart();
        if (windowInfoTracker != null) {
            windowInfoTracker.addWindowLayoutInfoListener(requireContext(), Runnable::run, layoutStateChangeCallback);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (this.player != null) {
            player.onActivityStop();
        }
        if (windowInfoTracker != null) {
            windowInfoTracker.removeWindowLayoutInfoListener(layoutStateChangeCallback);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideLoadingViewVisibility();
        if (this.player != null) {
            player.onActivityDestroyed();
            NexxPlayProvider.cancel();
        }
    }

    @Override
    public void onPlayerStateChanged(IPlayer.State state) {
        Utils.log(TAG, "Player state changed: " + state);

        if (state.equals(IPlayer.State.PLAYING)) {
            getActivity().setTitle(player.getCurrentMedia().getTitle());
        }
    }

    @Override
    public void onPlayerError(String reason, String details) {
        Utils.log(TAG, "Player error: " + reason + " --- " + details);
    }

    @Override
    public void onFullScreen(boolean isFullScreen) {
        Utils.log(TAG, "onFullScreen: " + isFullScreen);
        NavigationProvider.get(requireActivity()).setFrameLayoutFragmentUsed(
                isFullScreen ?
                        FrameLayoutFragmentUsed.TWO_FRAME_LAYOUT_USED_FULLSCREEN_ENTER :
                        FrameLayoutFragmentUsed.TWO_FRAME_LAYOUT_USED_FULLSCREEN_OUT
        );
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

                //lets add a permanent check with random values, if this actually works.
                CurrentPlaybackState cps = player.getCurrentPlaybackState();
                Utils.log(TAG, cps.getAudioLanguage() + "/" + cps.getIsPlaying() + "/" + cps.getStreamingFilter());

                MediaData md = player.getCurrentMedia();
                Utils.log(TAG, md.getGlobalID() + "/" + md.getChannel() + "/" + md.getFormat());

                if (rootView.findViewById(R.id.cmd_play) != null) {
                    rootView.findViewById(R.id.cmd_play).setVisibility(View.GONE);
                    rootView.findViewById(R.id.cmd_pause).setVisibility(View.VISIBLE);
                }
            } else if (Event.pause.toString().equals(event)) {
                if (rootView.findViewById(R.id.cmd_play) != null) {
                    rootView.findViewById(R.id.cmd_play).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.cmd_pause).setVisibility(View.GONE);
                }
            } else if (Event.downloadready.toString().equals(event)) {
                updateStorage();
            } else if (Event.closerequest.toString().equals(event)) {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
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
        player.swapToRemoteMedia("a4241ebd-3518-11eb-b839-0cc47a188158", "audio", "3q", 15);
//        player.swapToGlobalID("106775",0,0,"",true);
//        player.swapToMediaItem("5325", "playlist", 0, 0);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onConfigurationChanged(newConfig.orientation);
    }

    private void onConfigurationChanged(int orientation) {
        if (DeviceManager.getInstance().isTablet()) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                rootView.findViewById(R.id.backtosettings).setVisibility(View.GONE);
            } else {
                rootView.findViewById(R.id.backtosettings).setVisibility(View.VISIBLE);
            }
        }
    }

    protected void updateFoldable(WindowLayoutInfo windowLayoutInfo) {
        Utils.log(TAG, "UPDATING FOLDABLE DETAILS");
        FoldableMode foldableMode = FoldableMode.UNDEFINED_MODE;

        List<DisplayFeature> displayFeatures = windowLayoutInfo.getDisplayFeatures();
        for (DisplayFeature feature : displayFeatures) {
            if (feature instanceof FoldingFeature) {
                if (isTableTopPosture((FoldingFeature) feature)) {
                    foldableMode = FoldableMode.TABLETOP_MODE;
                } else if (isBookPosture((FoldingFeature) feature)) {
                    foldableMode = FoldableMode.BOOK_MODE;
                } else if (isSeparating((FoldingFeature) feature)) {
                    // Dual-screen device
                    if (((FoldingFeature) feature).getOrientation() == FoldingFeature.Orientation.HORIZONTAL) {
                        foldableMode = FoldableMode.TABLETOP_MODE;
                    } else {
                        foldableMode = FoldableMode.BOOK_MODE;
                    }
                } else {
                    foldableMode = FoldableMode.NORMAL_MODE;
                }
            }
        }

        NavigationProvider.get(requireActivity()).setFoldableMode(foldableMode);
        NavigationProvider.get(requireActivity()).handleFrameLayoutVisibility(getResources().getConfiguration().orientation);
    }

    private boolean isTableTopPosture(FoldingFeature foldFeature) {
        return foldFeature != null &&
                foldFeature.getState() == FoldingFeature.State.HALF_OPENED &&
                foldFeature.getOrientation() == FoldingFeature.Orientation.HORIZONTAL;
    }

    private boolean isBookPosture(FoldingFeature foldFeature) {
        return foldFeature != null &&
                foldFeature.getState() == FoldingFeature.State.HALF_OPENED &&
                foldFeature.getOrientation() == FoldingFeature.Orientation.VERTICAL;
    }

    private boolean isSeparating(FoldingFeature foldFeature) {
        return foldFeature != null &&
                foldFeature.getState() == FoldingFeature.State.FLAT &&
                foldFeature.isSeparating();
    }


    class LayoutStateChangeCallback implements Consumer<WindowLayoutInfo> {
        @Override
        public void accept(WindowLayoutInfo windowLayoutInfo) {
            updateFoldable(windowLayoutInfo);
        }
    }

}
