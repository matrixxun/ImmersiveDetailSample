package com.matrixxun.immersivedetail.sample.widget;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.matrixxun.immersivedetail.sample.R;
import com.matrixxun.immersivedetail.sample.util.LollipopCompatSingleton;

import java.lang.ref.WeakReference;

/**
 * Created by matrixxun on 2017/6/19.
 */
public class ObservableScrollView extends ScrollView{

    private static final int TOOLBAR_STATE_NORMAL = 0;
    private static final int TOOLBAR_STATE_TRANSPARENT = 1;

    private WeakReference<Activity> activityWeakReference;
    private View imageHeaderContainer;
    private Toolbar toolbar;
    private TextView toolbarTitleView;

    private int toolbarColor;
    private int toolbarState = TOOLBAR_STATE_NORMAL;

    private int oldScrollY = 0;
    private int lastScrollYDirection = 0;

    private boolean isImmersiveEffectOpen;

    private ScrollViewListener scrollViewListener = null;

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    public void setupImmersiveEffect(@NonNull Activity activity, @NonNull View imageHeaderContainer,
                                     @NonNull Toolbar toolbar, int toolbarColor, @NonNull TextView toolbarTitleView){
        activityWeakReference = new WeakReference<>(activity);
        this.imageHeaderContainer = imageHeaderContainer;
        this.toolbar = toolbar;
        this.toolbarColor = toolbarColor;
        this.toolbarTitleView = toolbarTitleView;
        if(activity !=null && imageHeaderContainer!=null && toolbar != null&& toolbarTitleView!=null){
            isImmersiveEffectOpen = true;
            toolbarTitleView.setVisibility(View.INVISIBLE);
            setToolbarColor(TOOLBAR_STATE_TRANSPARENT);
        }
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        applyScrollControlToolbar(y);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

    private void applyScrollControlToolbar(int scrollY){
        if(!isImmersiveEffectOpen){
            return;
        }
        imageHeaderContainer.setTranslationY(scrollY * 0.5f);

        if(scrollY-getFlexibleSpace() < toolbar.getHeight() && toolbarState == TOOLBAR_STATE_TRANSPARENT){
            float y = Math.min(0, -scrollY + getFlexibleSpace());
            if(y<-1){
                LollipopCompatSingleton.setStatusBarColorFade(activityWeakReference.get(), ContextCompat.getColor(getContext(), R.color.colorPrimaryDark),300);
            }else if(y==0){
                LollipopCompatSingleton.setStatusBarColorImmediately(activityWeakReference.get(),ContextCompat.getColor(getContext(),android.R.color.transparent));
            }
            toolbar.setTranslationY(y);
        }


        if(scrollY >= imageHeaderContainer.getHeight() && isScrollDown(scrollY) && toolbarState != TOOLBAR_STATE_NORMAL){
            setToolbarColor(TOOLBAR_STATE_NORMAL);
            toolbar.setVisibility(View.INVISIBLE);
            toolbar.setTranslationY(-toolbar.getHeight());
            toolbar.setVisibility(View.VISIBLE);
        }

        if (isScrollDown(scrollY) && toolbarState == TOOLBAR_STATE_NORMAL && toolbar.getTranslationY() < 0 && (scrollY-oldScrollY != 0)) {
            if(toolbar.getTranslationY() + Math.abs(scrollY-oldScrollY) <= 0){
                toolbar.setTranslationY(toolbar.getTranslationY() + Math.abs(scrollY-oldScrollY));
            }else{
                toolbar.setTranslationY(0);
            }
        }

        if (isScrollUp(scrollY) && toolbarState == TOOLBAR_STATE_NORMAL && toolbar.getTranslationY() <= 0 && (scrollY-oldScrollY != 0)) {
            if(toolbar.getTranslationY() - Math.abs(scrollY-oldScrollY) > -toolbar.getHeight()){
                toolbar.setTranslationY(toolbar.getTranslationY() - Math.abs(scrollY-oldScrollY));
            }else{
                toolbar.setTranslationY(-toolbar.getHeight());
            }
        }

        if(imageHeaderContainer.getTranslationY()*2 <= getFlexibleSpace() && toolbarState ==TOOLBAR_STATE_NORMAL) {
            final ObjectAnimator colorFade = ObjectAnimator.ofObject(toolbar, "backgroundColor", new ArgbEvaluator(), toolbarColor, android.R.color.transparent);
            colorFade.setDuration(300);
            colorFade.start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    toolbarTitleView.setVisibility(View.INVISIBLE);
                }
            }, 150);
            toolbarState = TOOLBAR_STATE_TRANSPARENT;
        }

        setScrollDirections(scrollY);

    }

    public void setToolbarColor(int state) {
        if (toolbarState != state) {
            toolbar.setBackgroundColor(state == TOOLBAR_STATE_TRANSPARENT ? ContextCompat.getColor(getContext(), android.R.color.transparent) : toolbarColor);
            toolbarState = state;
        }

        if (state == TOOLBAR_STATE_NORMAL) {
            toolbarTitleView.setVisibility(View.VISIBLE);
        } else if (state == TOOLBAR_STATE_TRANSPARENT) {
            toolbarTitleView.setVisibility(View.INVISIBLE);
        }

    }

    private boolean isScrollDown(int scrollY) {
        return scrollY <= oldScrollY && lastScrollYDirection == -1;
    }

    private boolean isScrollUp(int scrollY){
        return scrollY >= oldScrollY && lastScrollYDirection == 1;
    }
    public int getFlexibleSpace(){
        return imageHeaderContainer.getHeight() - toolbar.getHeight();
    }

    private void setScrollDirections(int scrollY) {
        if(scrollY > oldScrollY)
            lastScrollYDirection = 1;
        if(scrollY < oldScrollY)
            lastScrollYDirection = -1;

        oldScrollY = scrollY;
    }

    public interface ScrollViewListener {
        void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);
    }
}