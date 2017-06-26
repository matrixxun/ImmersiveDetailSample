package com.matrixxun.immersivedetail.sample.util;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by matrixxun on 2017/6/13.
 */

public class ViewHelper {
    public static <T extends View> T $(@NonNull View rootView, @IdRes int resId){
        return rootView == null ? null:(T)rootView.findViewById(resId);
    }

    public static <T extends View> T $(@NonNull Activity activity, @IdRes int resId){
        return activity == null ? null: (T)activity.findViewById(resId);
    }
}
