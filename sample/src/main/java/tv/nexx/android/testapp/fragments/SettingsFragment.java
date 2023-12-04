package tv.nexx.android.testapp.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import tv.nexx.android.play.Streamtype;
import tv.nexx.android.play.device.DeviceManager;
import tv.nexx.android.play.enums.MediaSourceType;
import tv.nexx.android.play.enums.PlayMode;
import tv.nexx.android.play.util.Utils;
import tv.nexx.android.recommendations.RecommendationManager;
import tv.nexx.android.testapp.MainActivity;
import tv.nexx.android.testapp.R;
import tv.nexx.android.testapp.navigation.FrameLayoutFragmentCallback;
import tv.nexx.android.testapp.navigation.FrameLayoutFragmentUsed;
import tv.nexx.android.testapp.navigation.IAppFragmentNavigation;
import tv.nexx.android.testapp.providers.NavigationProvider;
import tv.nexx.android.widget.Widget;


public class SettingsFragment extends Fragment implements AdapterView.OnItemClickListener, FrameLayoutFragmentCallback {
    private static final String TAG = SettingsFragment.class.getCanonicalName();

    private View rootView;
    private ScrollView mainScrollView;
    private ConstraintLayout clMainContent;

    private ExtendedFloatingActionButton show;
    private TextInputLayout providerEditLayout;
    private TextInputLayout mediaIDEditLayout;
    private TextInputLayout playModeSpinnerHolder;
    private TextInputLayout viewSizeSwitchHolder;
    private TextInputLayout startMutedSwitchHolder;
    private TextInputEditText providerEditText;
    private TextInputEditText domainIDEditText;
    private TextInputEditText mediaIDEditText;
    private MaterialSwitch autoPlaySwitch;
    private MaterialSwitch autoNextSwitch;
    private MaterialSwitch startMutedSwitch;
    private MaterialSwitch startFullscreenSwitch;
    private MaterialSwitch hidePrevNextSwitch;
    private MaterialSwitch forcePrevNextSwitch;
    private MaterialSwitch disableAdsSwitch;
    private AutoCompleteTextView playModeSpinner;
    private BottomNavigationView bottomNavigation;
    private AutoCompleteTextView exitModeSpinner;
    private AutoCompleteTextView streamingFilterSpinner;
    private Slider startPositionRangeSlider;
    private Slider delayRangeSlider;
    private TextView startPositionIndex;
    private TextView delayPositionIndex;
    private TextInputLayout startPositionHolder;
    private TextInputLayout delayPositionHolder;
    private TextInputLayout hidePrevNextSwitchHolder;
    private TextInputLayout forcePrevNextSwitchHolder;
    private ChipGroup viewSizeRadioGroup;

    private String streamtype;
    private Float delay = 0f;
    private int item = 0;
    private int domain = 0;
    private int global = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle arguments = getArguments();
            streamtype = arguments.getString("streamtype");
            item = arguments.getInt("item");
            domain = arguments.getInt("domain");
            global = arguments.getInt("global");
            delay = arguments.getFloat("delay");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        show = rootView.findViewById(R.id.show);
        show.setOnClickListener(v -> {
            DeviceManager.getInstance().performHapticFeedback(DeviceManager.getInstance().HAPTIC_FEEDBACK_EFFECT_EXTENDED);
            setLoadingViewVisibility(true);
            onShow();
        });
        show.setFocusableInTouchMode(true);
        show.requestFocus();

        bottomNavigation = rootView.findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnItemSelectedListener((item) -> {
            DeviceManager.getInstance().performHapticFeedback(DeviceManager.getInstance().HAPTIC_FEEDBACK_EFFECT_DEFAULT);
            onInitModeChanged((String) item.getTitle());
            return true;
        });

        mainScrollView = rootView.findViewById(R.id.mainScrollView);
        clMainContent = rootView.findViewById(R.id.cl_main_content);

        providerEditText = rootView.findViewById(R.id.et_provider);
        providerEditLayout = rootView.findViewById(R.id.et_providerHolder);
        domainIDEditText = rootView.findViewById(R.id.customerIDInput);
        mediaIDEditText = rootView.findViewById(R.id.mediaIDInput);
        mediaIDEditLayout = rootView.findViewById(R.id.mediaIDInputHolder);
        autoPlaySwitch = rootView.findViewById(R.id.autoplaySwitch);
        autoNextSwitch = rootView.findViewById(R.id.autoNextSwitch);
        startMutedSwitch = rootView.findViewById(R.id.startMutedSwitch);
        startMutedSwitchHolder = rootView.findViewById(R.id.startMutedSwitchHolder);
        hidePrevNextSwitch = rootView.findViewById(R.id.hidePrevNextSwitch);
        hidePrevNextSwitchHolder = rootView.findViewById(R.id.hidePrevNextSwitchHolder);
        forcePrevNextSwitch = rootView.findViewById(R.id.forcePrevNextSwitch);
        forcePrevNextSwitchHolder = rootView.findViewById(R.id.forcePrevNextSwitchHolder);
        startFullscreenSwitch = rootView.findViewById(R.id.startFullscreenSwitch);
        disableAdsSwitch = rootView.findViewById(R.id.disableAdsSwitch);

        startPositionHolder = rootView.findViewById(R.id.startPositionHolder);
        startPositionRangeSlider = rootView.findViewById(R.id.startPosition);
        startPositionIndex = rootView.findViewById(R.id.tv_startPosition_index);
        startPositionRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            DeviceManager.getInstance().performHapticFeedback(DeviceManager.getInstance().HAPTIC_FEEDBACK_EFFECT_SHORT);
            startPositionIndex.setText(String.valueOf(value));
        });

        delayPositionHolder = rootView.findViewById(R.id.delayHolder);
        delayRangeSlider = rootView.findViewById(R.id.delay);
        delayPositionIndex = rootView.findViewById(R.id.tv_delay_index);
        delayRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            DeviceManager.getInstance().performHapticFeedback(DeviceManager.getInstance().HAPTIC_FEEDBACK_EFFECT_SHORT);
            delayPositionIndex.setText(String.valueOf(value));
        });

        playModeSpinner = rootView.findViewById(R.id.playModeSpinner);
        playModeSpinnerHolder = rootView.findViewById(R.id.playModeSpinnerHolder);
        ArrayAdapter<CharSequence> playModesAdapter = ArrayAdapter.createFromResource(getContext(), R.array.playModes, R.layout.material_spinner);
        playModeSpinner.setAdapter(playModesAdapter);
        playModeSpinner.setOnItemClickListener(this);

        exitModeSpinner = rootView.findViewById(R.id.exitModeSpinner);
        ArrayAdapter<CharSequence> exitPlayModesAdapter = ArrayAdapter.createFromResource(getContext(), R.array.exitModes, R.layout.material_spinner);
        exitModeSpinner.setAdapter(exitPlayModesAdapter);

        streamingFilterSpinner = rootView.findViewById(R.id.streamingFilterSpinner);
        ArrayAdapter<CharSequence> streamingFilterAdapter = ArrayAdapter.createFromResource(getContext(), R.array.streamingFilters, R.layout.material_spinner);
        streamingFilterSpinner.setAdapter(streamingFilterAdapter);

        viewSizeRadioGroup = rootView.findViewById(R.id.viewSize);
        viewSizeSwitchHolder = rootView.findViewById(R.id.viewSizeSwitchHolder);

        DeviceManager.getInstance().init(getActivity().getApplicationContext());
        if (DeviceManager.getInstance().isTV()) {
            startFullscreenSwitch.setChecked(true);
            startFullscreenSwitch.setEnabled(false);
        }

        checkStreamtypeArg(streamtype);

        if (streamtype == null) {
            streamtype = playModeSpinner.getText().toString().toLowerCase();
            if (!Streamtype.isAudio(streamtype)) {
                viewSizeSwitchHolder.setVisibility(GONE);
                viewSizeRadioGroup.setVisibility(GONE);
            }
        }

        updateStartPositionByStreamtype(streamtype);
        updateDelayPositionByStreamtype(streamtype);

        show.requestFocus();

        if (DeviceManager.getInstance().isTV()) {
            RecommendationManager m = new RecommendationManager(getContext());
            m.updateChannel();
            m.enableAutoUpdate();
        } else if (DeviceManager.getInstance().isMobileDevice()) {
            int counter = Widget.getNumberOfWidgets(getContext());
            Utils.log(TAG, counter + " WIDGETS ARE INSTALLED");
            if (counter > 0) {
                Widget.updateWidgets(getContext());
            }
        } else {
            Utils.log(TAG, "NEITHER WIDGETS NOR RECOMMENDATIONS ARE SUPPORTED");
        }
        onConfigurationChanged(requireActivity().getResources().getConfiguration().orientation);
    }

    private void setLoadingViewVisibility(boolean isVisible) {
        if (requireActivity() instanceof MainActivity activity) {
            activity.setLoadingViewVisibility(isVisible);
        }
    }

    private void checkStreamtypeArg(@Nullable String streamtype) {
        if (streamtype != null) {
            bottomNavigation.setSelectedItemId(R.id.initmode_default);
            onInitModeChanged(getString(R.string.media_default));

            Log.v(TAG, "SETTING STREAMTYPE TO " + streamtype);
            playModeSpinner.setText(streamtype.substring(0, 1).toUpperCase() + streamtype.substring(1).toLowerCase(), false);

            domainIDEditText.setText(String.valueOf(domain));
            mediaIDEditText.setText(String.valueOf(item));

            if (!Streamtype.isAudio(streamtype)) {
                viewSizeSwitchHolder.setVisibility(GONE);
                viewSizeRadioGroup.setVisibility(GONE);
                startMutedSwitch.setVisibility(VISIBLE);
                startMutedSwitchHolder.setVisibility(VISIBLE);
            } else {
                startMutedSwitch.setVisibility(GONE);
                startMutedSwitchHolder.setVisibility(GONE);
            }

        } else if (global > 0) {
            bottomNavigation.setSelectedItemId(R.id.initmode_global);
            onInitModeChanged("GlobalID");

            domainIDEditText.setText(String.valueOf(domain));
            mediaIDEditText.setText(String.valueOf(global));
        }

        if (delay > 0) {
            delayRangeSlider.setValue(Math.round(delay));
        }
    }

    private void onInitModeChanged(String initMode) {
        Log.v(TAG, "UPDATING INITMODE TO " + initMode);
        ArrayAdapter<CharSequence> playModesAdapter;
        if ((initMode.equals(getString(R.string.media_default))) || (initMode.equals(getString(R.string.list)))) {
            Log.v(TAG, "RESETTING LAYOUT TO DEFAULT");
            mediaIDEditLayout.setHint(R.string.media_id);
            if (initMode.equals(getString(R.string.media_default))) {
                mediaIDEditLayout.setHint(R.string.media_id);
                mediaIDEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                playModesAdapter = ArrayAdapter.createFromResource(getContext(), R.array.playModes, R.layout.material_spinner);
            } else {
                mediaIDEditLayout.setHint(R.string.media_ids);
                mediaIDEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                playModesAdapter = ArrayAdapter.createFromResource(getContext(), R.array.playModes_list, R.layout.material_spinner);
            }
            providerEditLayout.setVisibility(GONE);
            playModeSpinner.setAdapter(playModesAdapter);
            playModeSpinner.setText(playModeSpinner.getAdapter().getItem(0).toString(), false);
            playModeSpinnerHolder.setVisibility(VISIBLE);
            if (!Streamtype.isAudio(playModeSpinner.getText().toString().toLowerCase())) {
                viewSizeSwitchHolder.setVisibility(GONE);
                viewSizeRadioGroup.setVisibility(GONE);
                startMutedSwitch.setVisibility(VISIBLE);
                startMutedSwitchHolder.setVisibility(VISIBLE);
            } else {
                startMutedSwitch.setVisibility(GONE);
                startMutedSwitchHolder.setVisibility(GONE);
            }
        } else if (initMode.equals(getString(R.string.global_id))) {
            Log.v(TAG, "RESETTING LAYOUT TO GLOBALID");
            playModeSpinnerHolder.setVisibility(GONE);
            mediaIDEditLayout.setHint(R.string.global_id);
            mediaIDEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            providerEditLayout.setVisibility(GONE);
            viewSizeSwitchHolder.setVisibility(VISIBLE);
            viewSizeRadioGroup.setVisibility(VISIBLE);
            startMutedSwitch.setVisibility(VISIBLE);
            startMutedSwitchHolder.setVisibility(VISIBLE);
        } else {
            Log.v(TAG, "RESETTING LAYOUT TO REMOTE");
            playModesAdapter = ArrayAdapter.createFromResource(getContext(), R.array.playModes_cut, R.layout.material_spinner);
            playModeSpinner.setAdapter(playModesAdapter);
            playModeSpinner.setText(playModeSpinner.getAdapter().getItem(0).toString(), false);
            playModeSpinnerHolder.setVisibility(VISIBLE);
            mediaIDEditLayout.setHint(R.string.foreign_ref);
            mediaIDEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            providerEditLayout.setVisibility(VISIBLE);
            viewSizeSwitchHolder.setVisibility(VISIBLE);
            viewSizeRadioGroup.setVisibility(VISIBLE);
            startMutedSwitch.setVisibility(VISIBLE);
            startMutedSwitchHolder.setVisibility(VISIBLE);
        }
        if (initMode.equals(getString(R.string.global_id))) {
            startPositionHolder.setVisibility(VISIBLE);
            startPositionRangeSlider.setVisibility(VISIBLE);
            hidePrevNextSwitch.setVisibility(VISIBLE);
            hidePrevNextSwitchHolder.setVisibility(VISIBLE);
            delayPositionHolder.setVisibility(VISIBLE);
            delayRangeSlider.setVisibility(VISIBLE);
        } else {
            updateStartPositionByStreamtype(playModeSpinner.getText().toString().toLowerCase());
            updateDelayPositionByStreamtype(playModeSpinner.getText().toString().toLowerCase());
        }
        mainScrollView.fullScroll(ScrollView.FOCUS_UP);
        mediaIDEditText.requestFocus();
    }

    private void onShow() {
        String initMode = (String) bottomNavigation.getMenu().findItem(bottomNavigation.getSelectedItemId()).getTitle();
        if ((initMode.equals(getString(R.string.media_default))) || (initMode.equals(getString(R.string.list)))) {
            onPlay(MediaSourceType.NORMAL);
        } else if (initMode.equals(getString(R.string.global_id))) {
            onPlay(MediaSourceType.GLOBAL);
        } else {
            onPlay(MediaSourceType.REMOTE);
        }
    }

    public void onPlay(MediaSourceType mediaSourceType) {
        String provider = providerEditText.getVisibility() == VISIBLE ? providerEditText.getText().toString() : "";
        String domainID = domainIDEditText.getText().toString();
        String playMode = playModeSpinner.getText().toString().toLowerCase();
        String mediaID = mediaIDEditText.getText().toString();
        boolean autoPlay = autoPlaySwitch.isChecked();
        boolean autoNext = autoNextSwitch.isChecked();
        boolean startMuted = startMutedSwitch.isChecked();
        boolean hidePrevNext = hidePrevNextSwitch.isChecked();
        boolean forcePrevNext = forcePrevNextSwitch.isChecked();
        boolean disableAds = disableAdsSwitch.isChecked();
        String exitMode = exitModeSpinner.getText().toString().toLowerCase();
        int startPosition = Float.valueOf(startPositionIndex.getText().toString()).intValue();
        float delay = Float.parseFloat(delayPositionIndex.getText().toString());
        boolean startFullscreen = startFullscreenSwitch.isChecked();
        String streamingFilter = streamingFilterSpinner.getText().toString().toLowerCase();

        String dataMode = "";
        ChipGroup dataModeRadioGroup = rootView.findViewById(R.id.dataMode);
        Chip dataModeRadioButton = rootView.findViewById(dataModeRadioGroup.getCheckedChipId());
        if (dataModeRadioButton != null) {
            dataMode = (String) dataModeRadioButton.getText();
        }
        Log.v(TAG, "USING DATAMODE: " + (!dataMode.equals("") ? dataMode : "unset"));

        String viewSize = "standard";
        Chip viewSizeRadioButton = rootView.findViewById(viewSizeRadioGroup.getCheckedChipId());
        if (viewSizeRadioButton != null) {
            viewSize = viewSizeRadioButton.getText().toString().toLowerCase();
        }
        Log.v(TAG, "USING VIEW SIZE: " + viewSize);

        Bundle bundle = new Bundle();
        bundle.putString("provider", provider);
        bundle.putString("domainID", domainID);
        bundle.putString("mediaID", mediaID);
        bundle.putString("playMode", playMode);
        bundle.putBoolean("autoPlay", autoPlay);
        bundle.putBoolean("startMuted", startMuted);
        bundle.putBoolean("autoNext", autoNext);
        bundle.putBoolean("hidePrevNext", hidePrevNext);
        bundle.putBoolean("forcePrevNext", forcePrevNext);
        bundle.putBoolean("disableAds", disableAds);
        bundle.putString("exitMode", exitMode);
        bundle.putBoolean("startFullscreen", startFullscreen);
        bundle.putString("streamingFilter", streamingFilter);
        bundle.putString("mediaSourceType", mediaSourceType.toString());

        if (startPosition > 0) {
            Log.v(TAG, "USING StartPosition: " + startPosition);
            bundle.putInt("startPosition", startPosition);
        }
        if (delay > 0) {
            Log.v(TAG, "USING DELAY: " + delay);
            bundle.putFloat("delay", delay);
        }
        if (!dataMode.equals("")) {
            bundle.putString("dataMode", dataMode);
        }
        if (!viewSize.equals("")) {
            bundle.putString("viewSize", viewSize);
        }

        NavigationProvider.get(getActivity()).showPlayer(bundle);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String streamtype = parent.getItemAtPosition(position).toString().toLowerCase();

        updateStartPositionByStreamtype(streamtype);
        updateDelayPositionByStreamtype(streamtype);

        if (Streamtype.isAudio(streamtype)) {
            viewSizeSwitchHolder.setVisibility(VISIBLE);
            viewSizeRadioGroup.setVisibility(VISIBLE);
        } else {
            viewSizeSwitchHolder.setVisibility(GONE);
            viewSizeRadioGroup.setVisibility(GONE);
        }

        Log.v(TAG, "CHOSEN STREAMTYPE: " + streamtype);
    }

    private void updateStartPositionByStreamtype(String streamtype) {
        if ((PlayMode.isContainer(Utils.getPlayMode(streamtype))) || (PlayMode.isList(Utils.getPlayMode(streamtype)))) {
            startPositionHolder.setVisibility(VISIBLE);
            startPositionRangeSlider.setVisibility(VISIBLE);
            hidePrevNextSwitch.setVisibility(VISIBLE);
            hidePrevNextSwitchHolder.setVisibility(VISIBLE);
            forcePrevNextSwitch.setVisibility(GONE);
            forcePrevNextSwitchHolder.setVisibility(GONE);
        } else {
            startPositionHolder.setVisibility(GONE);
            startPositionRangeSlider.setVisibility(GONE);
            hidePrevNextSwitch.setVisibility(GONE);
            hidePrevNextSwitchHolder.setVisibility(GONE);
            forcePrevNextSwitch.setVisibility(VISIBLE);
            forcePrevNextSwitchHolder.setVisibility(VISIBLE);
        }
        if (Streamtype.isAudio(streamtype)) {
            startMutedSwitchHolder.setVisibility(GONE);
            startMutedSwitch.setVisibility(GONE);
        } else {
            startMutedSwitchHolder.setVisibility(VISIBLE);
            startMutedSwitch.setVisibility(VISIBLE);
        }
    }

    private void updateDelayPositionByStreamtype(String streamtype) {
        if (Streamtype.isLive(streamtype)) {
            delayPositionHolder.setVisibility(GONE);
            delayRangeSlider.setVisibility(GONE);
        } else {
            delayPositionHolder.setVisibility(VISIBLE);
            delayRangeSlider.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onConfigurationChanged(newConfig.orientation);
    }

    private void onConfigurationChanged(int orientation) {
        if (DeviceManager.getInstance().isTablet()) {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mainScrollView.getLayoutParams();
            if (orientation == Configuration.ORIENTATION_LANDSCAPE &&
                    NavigationProvider.get(requireActivity()).getFrameLayoutFragmentUsed() == FrameLayoutFragmentUsed.ONE_FRAME_LAYOUT_USED) {
                params.leftMargin = (int) getResources().getDimension(R.dimen.activity_horizontal_margin_land_tablet);
                params.rightMargin = (int) getResources().getDimension(R.dimen.activity_horizontal_margin_land_tablet);
            } else {
                params.leftMargin = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
                params.rightMargin = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
            }
            mainScrollView.setLayoutParams(params);
        }
    }

    @Override
    public void update(FrameLayoutFragmentUsed frameLayoutFragmentUsed) {
        onConfigurationChanged(requireActivity().getResources().getConfiguration().orientation);
    }

    @Override
    public void onResume() {
        super.onResume();
        IAppFragmentNavigation appFragmentNavigation = NavigationProvider.get(requireActivity());
        appFragmentNavigation.addFragmentCallback(this);
        setLoadingViewVisibility(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        IAppFragmentNavigation appFragmentNavigation = NavigationProvider.get(requireActivity());
        appFragmentNavigation.removeFragmentCallback(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        IAppFragmentNavigation appFragmentNavigation = NavigationProvider.get(requireActivity());
        appFragmentNavigation.removeFragmentCallback(this);
    }
}
