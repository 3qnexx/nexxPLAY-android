package tv.nexx.android.testapp.navigation;

import androidx.fragment.app.Fragment;

import tv.nexx.android.testapp.fragments.SettingsFragment;

public interface IAppFragmentNavigation {
    void addFragment(Fragment fragment);
    void resetToFragment(Fragment fragment);
    void onBack();
    boolean isDestroyed();
    boolean isLastFragment();
}
