package tv.nexx.android.testapp.navigation;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.HashSet;
import java.util.Set;

import tv.nexx.android.play.device.DeviceManager;
import tv.nexx.android.testapp.R;
import tv.nexx.android.testapp.fragments.PlayerFragment;
import tv.nexx.android.testapp.fragments.SettingsFragment;

public class AppFragmentNavigation implements IAppFragmentNavigation {
    private final FragmentManager supportFragmentManager;
    private final Set<FrameLayoutFragmentCallback> fragmentCallbacks;
    private FrameLayoutFragmentUsed frameLayoutFragmentUsed = FrameLayoutFragmentUsed.ONE_FRAME_LAYOUT_USED;
    private FoldableMode foldableMode = FoldableMode.UNDEFINED_MODE;

    private ConstraintLayout mainParentLayout;
    private FrameLayout settingsFrameContainer;
    private FrameLayout playerFrameContainer;
    private View proportionLine;

    public AppFragmentNavigation(FragmentManager supportFragmentManager) {
        this.supportFragmentManager = supportFragmentManager;
        fragmentCallbacks = new HashSet<>();
    }

    @Override
    public void showPlayer(@Nullable Bundle bundle) {
        Fragment oldFragment = supportFragmentManager.findFragmentById(R.id.player_frame_container);

        PlayerFragment fragment = new PlayerFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        if (oldFragment instanceof PlayerFragment) {
            supportFragmentManager.popBackStack();
        }
        transaction
                .replace(R.id.player_frame_container, fragment)
                .addToBackStack(null);

        transaction.commit();
    }

    @Override
    public void showSettings(@Nullable Bundle bundle) {
        setFrameLayoutFragmentUsed(FrameLayoutFragmentUsed.ONE_FRAME_LAYOUT_USED);
        SettingsFragment fragment = new SettingsFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.settings_frame_container, fragment)
                .commit();
    }

    @Override
    public void onBack() {
        setFrameLayoutFragmentUsed(FrameLayoutFragmentUsed.ONE_FRAME_LAYOUT_USED);
        supportFragmentManager.popBackStack();
    }

    @Override
    public boolean isDestroyed() {
        return supportFragmentManager.isDestroyed();
    }

    @Override
    public boolean isLastFragment() {
        return supportFragmentManager.getBackStackEntryCount() == 0;
    }

    @Override
    public void addFragmentCallback(FrameLayoutFragmentCallback fragmentCallback) {
        fragmentCallbacks.add(fragmentCallback);
    }

    @Override
    public void removeFragmentCallback(FrameLayoutFragmentCallback fragmentCallback) {
        fragmentCallbacks.remove(fragmentCallback);
    }

    @Override
    public void setFrameLayoutFragmentUsed(FrameLayoutFragmentUsed frameLayoutFragmentUsed) {
        this.frameLayoutFragmentUsed = frameLayoutFragmentUsed;
        for (FrameLayoutFragmentCallback fragmentCallback : fragmentCallbacks) {
            fragmentCallback.update(frameLayoutFragmentUsed);
        }
    }

    @Override
    public FrameLayoutFragmentUsed getFrameLayoutFragmentUsed() {
        return frameLayoutFragmentUsed;
    }

    @Override
    public void attachViews(ConstraintLayout mainParentLayout, FrameLayout settingsFrameContainer, FrameLayout playerFrameContainer, View proportionLine) {
        this.mainParentLayout = mainParentLayout;
        this.settingsFrameContainer = settingsFrameContainer;
        this.playerFrameContainer = playerFrameContainer;
        this.proportionLine = proportionLine;
    }

    @Override
    public void setFoldableMode(FoldableMode foldableMode) {
        this.foldableMode = foldableMode;
    }

    @Override
    public void handleFrameLayoutVisibility(int orientation) {
        if (mainParentLayout != null && settingsFrameContainer != null && playerFrameContainer != null && proportionLine != null) {
            if (DeviceManager.getInstance().isFoldable() && (foldableMode == FoldableMode.TABLETOP_MODE || foldableMode == FoldableMode.BOOK_MODE)) {
                switch (frameLayoutFragmentUsed) {
                    case ONE_FRAME_LAYOUT_USED:
                        showJustSettingFrame();
                        break;
                    case TWO_FRAME_LAYOUT_USED, TWO_FRAME_LAYOUT_USED_PIP_OUT, TWO_FRAME_LAYOUT_USED_FULLSCREEN_OUT:
                        showSettingAndPlayerFramesFoldable(foldableMode);
                        break;
                    case TWO_FRAME_LAYOUT_USED_PIP_ENTER, TWO_FRAME_LAYOUT_USED_FULLSCREEN_ENTER:
                        showJustPlayerFrame();
                        break;
                }
            } else if (DeviceManager.getInstance().isTablet() && orientation == Configuration.ORIENTATION_LANDSCAPE) {
                switch (frameLayoutFragmentUsed) {
                    case ONE_FRAME_LAYOUT_USED:
                        showJustSettingFrame();
                        break;
                    case TWO_FRAME_LAYOUT_USED, TWO_FRAME_LAYOUT_USED_PIP_OUT, TWO_FRAME_LAYOUT_USED_FULLSCREEN_OUT:
                        showSettingAndPlayerFrames(0.35f);
                        break;
                    case TWO_FRAME_LAYOUT_USED_PIP_ENTER, TWO_FRAME_LAYOUT_USED_FULLSCREEN_ENTER:
                        showJustPlayerFrame();
                        break;
                }
            } else {
                switch (frameLayoutFragmentUsed) {
                    case ONE_FRAME_LAYOUT_USED:
                        showJustSettingFrame();
                        break;
                    case TWO_FRAME_LAYOUT_USED, TWO_FRAME_LAYOUT_USED_FULLSCREEN_OUT, TWO_FRAME_LAYOUT_USED_PIP_OUT:
                        showJustPlayerFrame();
                        break;
                }
            }
        }
    }

    private void showJustSettingFrame() {
        ConstraintSet set = new ConstraintSet();
        set.clone(mainParentLayout);

        set.connect(settingsFrameContainer.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        set.connect(settingsFrameContainer.getId(), ConstraintSet.END, proportionLine.getId(), ConstraintSet.START);
        set.connect(settingsFrameContainer.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        set.connect(settingsFrameContainer.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);

        set.clear(proportionLine.getId(), ConstraintSet.START);
        set.connect(proportionLine.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        set.applyTo(mainParentLayout);

        settingsFrameContainer.setVisibility(View.VISIBLE);
        playerFrameContainer.setVisibility(View.GONE);
    }

    private void showJustPlayerFrame() {
        ConstraintSet set = new ConstraintSet();
        set.clone(mainParentLayout);

        set.connect(playerFrameContainer.getId(), ConstraintSet.START, proportionLine.getId(), ConstraintSet.END);
        set.connect(playerFrameContainer.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        set.connect(playerFrameContainer.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        set.connect(playerFrameContainer.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

        set.clear(proportionLine.getId(), ConstraintSet.END);
        set.connect(proportionLine.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        set.applyTo(mainParentLayout);

        settingsFrameContainer.setVisibility(View.GONE);
        playerFrameContainer.setVisibility(View.VISIBLE);
    }

    private void showSettingAndPlayerFrames(float horizontalBias) {
        ConstraintSet set = new ConstraintSet();
        set.clone(mainParentLayout);

        set.clear(settingsFrameContainer.getId());
        set.clear(playerFrameContainer.getId());

        set.connect(settingsFrameContainer.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        set.connect(settingsFrameContainer.getId(), ConstraintSet.END, proportionLine.getId(), ConstraintSet.START);
        set.connect(settingsFrameContainer.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        set.connect(settingsFrameContainer.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);

        set.connect(playerFrameContainer.getId(), ConstraintSet.START, proportionLine.getId(), ConstraintSet.END);
        set.connect(playerFrameContainer.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        set.connect(playerFrameContainer.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        set.connect(playerFrameContainer.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

        set.applyTo(mainParentLayout);

        set.connect(proportionLine.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        set.connect(proportionLine.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        set.setHorizontalBias(proportionLine.getId(), horizontalBias);
        set.applyTo(mainParentLayout);

        settingsFrameContainer.setVisibility(View.VISIBLE);
        playerFrameContainer.setVisibility(View.VISIBLE);
    }

    private void showSettingAndPlayerFramesFoldable(FoldableMode foldableMode) {
        if (foldableMode == FoldableMode.TABLETOP_MODE) {
            ConstraintSet set = new ConstraintSet();
            set.clone(mainParentLayout);
            set.clear(settingsFrameContainer.getId());
            set.clear(playerFrameContainer.getId());

            set.connect(settingsFrameContainer.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            set.connect(settingsFrameContainer.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            set.connect(settingsFrameContainer.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            set.connect(settingsFrameContainer.getId(), ConstraintSet.TOP, proportionLine.getId(), ConstraintSet.BOTTOM);

            set.connect(playerFrameContainer.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            set.connect(playerFrameContainer.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            set.connect(playerFrameContainer.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            set.connect(playerFrameContainer.getId(), ConstraintSet.BOTTOM, proportionLine.getId(), ConstraintSet.TOP);

            set.applyTo(mainParentLayout);

            settingsFrameContainer.setVisibility(View.VISIBLE);
            playerFrameContainer.setVisibility(View.VISIBLE);
        } else if (foldableMode == FoldableMode.BOOK_MODE) {
            showSettingAndPlayerFrames(0.5f);
        } else {
            showJustPlayerFrame();
        }
    }
}
