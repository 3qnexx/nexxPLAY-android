package tv.nexx.android.testapp.providers;

import android.content.Context;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;

import tv.nexx.android.play.NexxPLAY;

public class NexxPlayProvider {
    private static NexxPLAY instance;

    public static NexxPLAY init(Context context, ViewGroup root, Window window) {
        if (instance == null) {
            synchronized (NexxPlayProvider.class) {
                if (instance == null) {
                    instance = new NexxPLAY(context, root, window);
                }
            }
        }
        return instance;
    }

    @Nullable
    public static NexxPLAY getInstance() {
        return instance;
    }

    public static void cancel(){
        instance = null;
    }
}
