package tv.nexx.android.testapp.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import tv.nexx.android.play.Streamtype;
import tv.nexx.android.play.device.DeviceManager;
import tv.nexx.android.play.enums.MediaSourceType;
import tv.nexx.android.play.enums.PlayMode;
import tv.nexx.android.play.util.Utils;
import tv.nexx.android.recommendations.RecommendationManager;
import tv.nexx.android.testapp.R;
import tv.nexx.android.testapp.providers.NavigationProvider;
import tv.nexx.android.widget.Widget;


public class SettingsFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = SettingsFragment.class.getCanonicalName();

    private View rootView;
    private ScrollView mainScrollView;

    private ExtendedFloatingActionButton show;
    private TextInputLayout providerEditLayout;
    private TextInputLayout mediaIDEditLayout;
    private TextInputLayout playModeSpinnerHolder;
    private TextInputEditText providerEditText;
    private TextInputEditText domainIDEditText;
    private TextInputEditText mediaIDEditText;
    private SwitchMaterial autoPlaySwitch;
    private SwitchMaterial autoNextSwitch;
    private SwitchMaterial startFullscreenSwitch;
    private SwitchMaterial hidePrevNextSwitch;
    private SwitchMaterial forcePrevNextSwitch;
    private SwitchMaterial disableAdsSwitch;
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

        providerEditText = rootView.findViewById(R.id.et_provider);
        providerEditLayout = rootView.findViewById(R.id.et_providerHolder);
        domainIDEditText = rootView.findViewById(R.id.customerIDInput);
        mediaIDEditText = rootView.findViewById(R.id.mediaIDInput);
        mediaIDEditLayout = rootView.findViewById(R.id.mediaIDInputHolder);
        autoPlaySwitch = rootView.findViewById(R.id.autoplaySwitch);
        autoNextSwitch = rootView.findViewById(R.id.autoNextSwitch);
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

        DeviceManager.getInstance().init(getActivity().getApplicationContext());
        if (DeviceManager.getInstance().isTV()) {
            startFullscreenSwitch.setChecked(true);
            startFullscreenSwitch.setEnabled(false);
        }

        checkStreamtypeArg(streamtype);

        updateStartPositionByStreamtype(playModeSpinner.getText().toString().toLowerCase());
        updateDelayPositionByStreamtype(playModeSpinner.getText().toString().toLowerCase());

        show.requestFocus();

        if (DeviceManager.getInstance().isTV()) {
            RecommendationManager m = new RecommendationManager(getContext());
            m.updateChannel();
            m.enableAutoUpdate();
        } else if(DeviceManager.getInstance().isMobileDevice()) {
            Widget widget = new Widget();
            int counter = widget.getNumberOfWidgets(getContext());
            Utils.log(TAG, counter + " WIDGETS ARE INSTALLED");
            if (counter > 0) {
                widget.updateWidgets(getContext());
            }
        }else{
            Utils.log(TAG,"NEITHER WIDGETS NOR RECOMMENDATIONS ARE SUPPORTED");
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
        Log.v(TAG, "UPDATING INITMODE WITH " + initMode);
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
        } else if (initMode.equals(getString(R.string.global_id))) {
            Log.v(TAG, "RESETTING LAYOUT TO GLOBALID");
            playModeSpinnerHolder.setVisibility(GONE);
            mediaIDEditLayout.setHint(R.string.global_id);
            mediaIDEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            providerEditLayout.setVisibility(GONE);
        } else {
            Log.v(TAG, "RESETTING LAYOUT TO REMOTE");
            playModesAdapter = ArrayAdapter.createFromResource(getContext(), R.array.playModes_cut, R.layout.material_spinner);
            playModeSpinner.setAdapter(playModesAdapter);
            playModeSpinner.setText(playModeSpinner.getAdapter().getItem(0).toString(), false);
            playModeSpinnerHolder.setVisibility(VISIBLE);
            mediaIDEditLayout.setHint(R.string.foreign_ref);
            mediaIDEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            providerEditLayout.setVisibility(VISIBLE);

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

        String viewSize = "normal";
        ChipGroup viewSizeRadioGroup = rootView.findViewById(R.id.viewSize);
        Chip viewSizeRadioButton = rootView.findViewById(viewSizeRadioGroup.getCheckedChipId());
        if (viewSizeRadioButton != null) {
            viewSize = (String) viewSizeRadioButton.getText();
        }
        Log.v(TAG, "USING VIEW SIZE: " + viewSize);

        String adType = "";
        ChipGroup adTypeRadioGroup = rootView.findViewById(R.id.adType);
        Chip adTypeRadioButton = rootView.findViewById(adTypeRadioGroup.getCheckedChipId());
        if (adTypeRadioButton != null) {
            adType = (String) adTypeRadioButton.getText();
        }
        Log.v(TAG, "USING ADTYPE: " + (!adType.equals("") ? adType : "unset"));

        Bundle bundle = new Bundle();
        bundle.putString("provider", provider);
        bundle.putString("domainID", domainID);
        bundle.putString("mediaID", mediaID);
        bundle.putString("playMode", playMode);
        bundle.putBoolean("autoplay", autoPlay);
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
        if (!adType.equals("")) {
            bundle.putString("adType", adType);
        }

        PlayerFragment fragment = new PlayerFragment();
        fragment.setArguments(bundle);
        NavigationProvider.get(getActivity()).addFragment(fragment);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String streamtype = (String) parent.getItemAtPosition(position).toString().toLowerCase();

        updateStartPositionByStreamtype(streamtype);
        updateDelayPositionByStreamtype(streamtype);

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
}