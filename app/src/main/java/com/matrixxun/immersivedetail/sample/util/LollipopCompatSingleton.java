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
 * Created by matrixxun on 2015/12/30.
 */
public class LollipopCompatSingleton {

    public static final Interpolator INTERPOLATOR_FAST_OUT_SLOW_IN = new FastOutSlowInInterpolator();

    private static int DEFAULT_ACTION_BAR_SIZE = 56; //dp
    private static int DEFAULT_ACTION_BAR_AND_TAB_LAYOUT_SIZE = 104; //dp
    private static float DEFAULT_LOLLIPOP_STATUS_BAR_HEIGHT = 25.0f; //dp

    private int statusBarHeight = -1;
    private int actionBarToolBarSize = -1;
    private int toolBarAndTabLayoutSize = -1;
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

    public int getActionBarToolBarSize(Context context){
        if(actionBarToolBarSize >0){
            return actionBarToolBarSize;
        }else{
            actionBarToolBarSize =  dp2px(context, DEFAULT_ACTION_BAR_SIZE);
            return actionBarToolBarSize;
        }
    }

    public int getToolBarAndTabLayoutSize(Context context){
        if(toolBarAndTabLayoutSize >0){
            return toolBarAndTabLayoutSize;
        }else{
            toolBarAndTabLayoutSize =  dp2px(context, DEFAULT_ACTION_BAR_AND_TAB_LAYOUT_SIZE);
            return toolBarAndTabLayoutSize;
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

    @SuppressLint("NewApi")
    public static void updateStatusBarColorBetweenTrans(Activity activity, int color){
        if(!isLollipop21More() || activity == null){
            return;
        }
        final Window window = activity.getWindow();
        if(window != null){
            if(window.getStatusBarColor() == Color.TRANSPARENT){
                setStatusBarColorFade(activity, color,200);
            }else {
                setStatusBarColorFade(activity, Color.TRANSPARENT, 200);
            }
        }
    }

    public static int getColorWithAlpha(float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        return a + rgb;
    }


    @SuppressLint("NewApi")
    public static int getStatusBarColor(Activity activity){
        if(!isLollipop21More() || activity == null){
            return -1;
        }
        Window window = activity.getWindow();
        if(window != null){
            return window.getStatusBarColor();
        }
        return -1;
    }

    public void fitStatusBarTranslucentPadding(View view, Context context){
        if(view != null){
            view.setPadding(0, getStatusBarHeightWhenLollipop21More(context), 0, 0);
        }
    }

    public static int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
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

    public static int convertDpToPx(Context context, float dp) {
        return (int) applyDimension(COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static void setBoundsViewOutlineProvider(View view) {
        if(isLollipop21More()){
            view.setOutlineProvider(ViewOutlineProvider.BOUNDS);
        }
    }
    /**
     * 判断当前设备是手机还是平板，代码来自AOSP
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isIceCreamSandwich14More(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static boolean isJellyBean16More(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean isKitkat19More(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean isLollipop21More(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isM23(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

}
