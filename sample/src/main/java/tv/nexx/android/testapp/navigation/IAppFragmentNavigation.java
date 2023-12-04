package tv.nexx.android.testapp.navigation;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public interface IAppFragmentNavigation {
    void showPlayer(@Nullable Bundle bundle);
    void showSettings(@Nullable Bundle bundle);
    void onBack();
    boolean isDestroyed();
    boolean isLastFragment();
    void addFragmentCallback(FrameLayoutFragmentCallback fragmentCallback);
    void removeFragmentCallback(FrameLayoutFragmentCallback fragmentCallback);
    void setFrameLayoutFragmentUsed(FrameLayoutFragmentUsed frameLayoutFragmentUsed);
    FrameLayoutFragmentUsed getFrameLayoutFragmentUsed();
    void attachViews(ConstraintLayout mainParentLayout, FrameLayout settingsFrameContainer, FrameLayout playerFrameContainer, View proportionLine);
    void handleFrameLayoutVisibility(int orientation);

    void setFoldableMode(FoldableMode foldableMode);
}
