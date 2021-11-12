package tv.nexx.android.testapp.navigation;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

import tv.nexx.android.testapp.R;

public class AppFragmentNavigation implements IAppFragmentNavigation {
    private final FragmentManager supportFragmentManager;

    public AppFragmentNavigation(FragmentManager supportFragmentManager) {
        this.supportFragmentManager = supportFragmentManager;
    }

    @Override
    public void addFragment(Fragment fragment) {
        List<Fragment> fragments = supportFragmentManager.getFragments();
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        if (!fragments.isEmpty()) {
            final Fragment oldFragment = fragments.get(0);
            transaction
                    .add(R.id.frame_container, fragment)
                    .hide(oldFragment);
        }
        transaction
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void resetToFragment(Fragment fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment)
                .commit();
    }

    @Override
    public void onBack() {
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
}
