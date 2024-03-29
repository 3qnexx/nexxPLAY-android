package tv.nexx.android.testapp.providers;

import androidx.fragment.app.FragmentActivity;

import tv.nexx.android.testapp.navigation.AppFragmentNavigation;
import tv.nexx.android.testapp.navigation.IAppFragmentNavigation;

public class NavigationProvider {

    private NavigationProvider() {
    }

    private static IAppFragmentNavigation navigation;

    public static synchronized IAppFragmentNavigation get(FragmentActivity fragmentActivity) {
        if (navigation == null || navigation.isDestroyed()) {
            navigation = new AppFragmentNavigation(fragmentActivity.getSupportFragmentManager());
        }
        return navigation;
    }
}
