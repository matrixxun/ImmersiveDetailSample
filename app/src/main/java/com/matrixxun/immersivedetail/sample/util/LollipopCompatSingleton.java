package com.matrixxun.immersivedetail.sample.util;

import android.animation.ArgbEvaluator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.animation.Interpolator;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.util.TypedValue.applyDimension;

/**
 * Created by matrixxun on 2017/6/19.
 */
public class LollipopCompatSingleton {

    public static final Interpolator INTERPOLATOR_FAST_OUT_SLOW_IN = new FastOutSlowInInterpolator();

    private static float DEFAULT_LOLLIPOP_STATUS_BAR_HEIGHT = 25.0f; //dp

    private int statusBarHeight = -1;
    private static LollipopCompatSingleton lollipopCompatSingleton;
    private static final TypeEvaluator ARGB_EVALUATOR = new ArgbEvaluator();

    private LollipopCompatSingleton(){

    }

    public static LollipopCompatSingleton getInstance(){
        if(lollipopCompatSingleton == null){
            synchronized (LollipopCompatSingleton.class){
                if(lollipopCompatSingleton == null){
                    lollipopCompatSingleton = new LollipopCompatSingleton();
                }
            }
        }
        return lollipopCompatSingleton;
    }

    public int getStatusBarHeightWhenLollipop21More(Context context) {
        if (statusBarHeight >= 0) {
            return statusBarHeight;
        } else {
            if (!isLollipop21More()) {
                statusBarHeight = 0;
                return statusBarHeight;
            } else {
                int result = 0;

                //第一次计算
                Resources resources = context!=null ? context.getResources() : Resources.getSystem();

                int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    result = resources.getDimensionPixelSize(resourceId);
                }

                //第二次计算
                if (result <= 0) {
                    result = dp2px(context, DEFAULT_LOLLIPOP_STATUS_BAR_HEIGHT);
                }

                //汇总结果
                if (result >= 0) {
                    statusBarHeight = result;
                    return statusBarHeight;
                } else {
                    return 0;
                }
            }
        }
    }

    public static void translucentStatusBar(Activity activity){
        if(activity != null && isLollipop21More()){
            Window window = activity.getWindow();
            if(window != null){
                View view = window.getDecorView();
                if(view != null){
                    view.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                }
            }

        }
    }

    /**
     * 注意: 不要持续的调这个方法, 调一次就够了.
     * @param activity
     * @param toColor
     * @param msec
     */
    @SuppressLint("NewApi")
    public static void setStatusBarColorFade(final Activity activity, final int toColor, int msec) {
        if (activity == null || !isLollipop21More()) {
            return;
        }
        final Window window = activity.getWindow();
        if (window == null) {
            return;
        }
        int statusBarColor = window.getStatusBarColor();
        if (statusBarColor != toColor) {
            ValueAnimator statusBarColorAnim = ValueAnimator.ofObject(ARGB_EVALUATOR, statusBarColor, toColor);
            statusBarColorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    window.setStatusBarColor((Integer) animation
                            .getAnimatedValue());
                }
            });
            statusBarColorAnim.setDuration(msec);
            statusBarColorAnim.setInterpolator(INTERPOLATOR_FAST_OUT_SLOW_IN);
            statusBarColorAnim.start();
        }
    }

    @SuppressLint("NewApi")
    public static void setStatusBarColorImmediately(Activity activity, int color){
        if(!isLollipop21More() || activity == null){
            return;
        }
        Window window = activity.getWindow();
        if(window != null){
            if(window.getStatusBarColor() != color){
                window.setStatusBarColor(color);
            }
        }
    }

    public void fitStatusBarTranslucentPadding(View view, Context context){
        if(view != null){
            view.setPadding(0, getStatusBarHeightWhenLollipop21More(context), 0, 0);
        }
    }


    public static int dp2px(@Nullable Context context, float dpValue) {
        float scale;
        if(context != null){
            scale = context.getResources().getDisplayMetrics().density;
        }else{
            scale =  Resources.getSystem().getDisplayMetrics().density;
        }
        scale = scale>0 ? scale: DisplayMetrics.DENSITY_DEFAULT;
        return (int) (dpValue * scale + 0.5f);
    }

    public static boolean isLollipop21More(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
