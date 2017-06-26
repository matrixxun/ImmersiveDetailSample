package com.matrixxun.immersivedetail.sample;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.matrixxun.immersivedetail.sample.adapter.BannerPagerAdapter;
import com.matrixxun.immersivedetail.sample.util.LollipopCompatSingleton;
import com.matrixxun.immersivedetail.sample.util.ViewHelper;
import com.matrixxun.immersivedetail.sample.widget.ObservableScrollView;
import com.matrixxun.immersivedetail.sample.widget.PagerIndicator;

/**
 * Created by matrixxun on 2017/6/19.
 */

public class ImmersiveDetailFragment  extends Fragment implements ObservableScrollView.ScrollViewListener{

    private Toolbar toolbar;
    private ActionBar supportActionBar;
    private TextView toolbarTitle;

    private ViewPager mViewPager;
    private PagerIndicator mPagerIndicator;
    private ObservableScrollView scrollview;
    private FrameLayout imageContainer;

    private int toolbarColor;
    private ImmersiveDetailActivity.ToolbarState toolbarState = ImmersiveDetailActivity.ToolbarState.TOOLBAR_STATE_NORMAL;
    private int oldScrollY = 0;
    private int lastScrollYDirection = 0;

    public static ImmersiveDetailFragment newInstance(){
        ImmersiveDetailFragment instance = new ImmersiveDetailFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_immersive_detail,null,false);
        toolbar = ViewHelper.$(view,R.id.toolbar_default);
        toolbarTitle = ViewHelper.$(view,R.id.toolbarTitle);
        scrollview = ViewHelper.$(view,R.id.scrollview);
        imageContainer = ViewHelper.$(view,R.id.image_container);
        mViewPager = ViewHelper.$(view,R.id.bulletpager);
        mPagerIndicator = ViewHelper.$(view,R.id.view_pager_indicator);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity rootActivity = null;
        if(getActivity() instanceof AppCompatActivity){
            rootActivity = ((AppCompatActivity)getActivity());
        }
        if(rootActivity == null){
           throw  new RuntimeException("The fragment's parent activity must be inherited from AppCompatActivity");
        }
        BannerPagerAdapter pagerAdapter = new BannerPagerAdapter(rootActivity);
        mViewPager.setAdapter(pagerAdapter);
        mPagerIndicator.setViewPager(mViewPager);

        rootActivity.setSupportActionBar(toolbar);
        supportActionBar = rootActivity.getSupportActionBar();

        LollipopCompatSingleton.getInstance().fitStatusBarTranslucentPadding(toolbar, rootActivity);
        showToolbarTitle(false);
        rootActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rootActivity.getSupportActionBar().setHomeButtonEnabled(true);
        toolbarTitle.setText("Activity with Fragment");
        toolbarTitle.setVisibility(View.INVISIBLE);

        toolbarColor = ContextCompat.getColor(rootActivity,R.color.colorPrimary);

        setToolbarColor(ImmersiveDetailActivity.ToolbarState.TOOLBAR_STATE_TRANSPARENT);
        scrollview.setScrollViewListener(this);
        scrollview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(toolbar.getTranslationY() != 0 && toolbarState.equals(ImmersiveDetailActivity.ToolbarState.TOOLBAR_STATE_NORMAL) && lastScrollYDirection == 1){ // UP
                        final AlphaAnimation fadeIn = new AlphaAnimation(1.0f, 0.0f);
                        fadeIn.setDuration(400);
                        fadeIn.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                toolbar.setVisibility(View.INVISIBLE);
                                setToolbarColor(ImmersiveDetailActivity.ToolbarState.TOOLBAR_STATE_TRANSPARENT);
                                toolbar.setTranslationY(-toolbar.getHeight());
                                toolbar.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                        toolbar.startAnimation(fadeIn);
                    }
                }
                return false;
            }
        });
    }

    public void showToolbarTitle(boolean showTitle){
        if(supportActionBar != null)
            supportActionBar.setDisplayShowTitleEnabled(showTitle);
    }

    public void setToolbarColor(ImmersiveDetailActivity.ToolbarState state){
        if(!toolbarState.equals(state)) {
            toolbar.setBackgroundColor(state.isTransparent() ? ContextCompat.getColor(getActivity(),android.R.color.transparent): toolbarColor);
            toolbarState = state;
        }

        if(state.equals(ImmersiveDetailActivity.ToolbarState.TOOLBAR_STATE_NORMAL))
            toolbarTitle.setVisibility(View.VISIBLE);
        else if(state.equals(ImmersiveDetailActivity.ToolbarState.TOOLBAR_STATE_TRANSPARENT))
            toolbarTitle.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int scrollY, int oldx, int oldy) {

        imageContainer.setTranslationY(scrollY * 0.5f);

        if(scrollY-getFlexibleSpace() < toolbar.getHeight() && toolbarState.equals(ImmersiveDetailActivity.ToolbarState.TOOLBAR_STATE_TRANSPARENT)){
            float y = Math.min(0, -scrollY + getFlexibleSpace());
            if(y<-1){
                LollipopCompatSingleton.setStatusBarColorFade(getActivity(),ContextCompat.getColor(getActivity(),R.color.colorPrimaryDark),300);
            }else if(y==0){
                LollipopCompatSingleton.setStatusBarColorImmediately(getActivity(),ContextCompat.getColor(getActivity(),android.R.color.transparent));
            }
            toolbar.setTranslationY(y);
        }


        if(scrollY >= imageContainer.getHeight() && isScrollDown(scrollY) && !toolbarState.equals(ImmersiveDetailActivity.ToolbarState.TOOLBAR_STATE_NORMAL)){
            setToolbarColor(ImmersiveDetailActivity.ToolbarState.TOOLBAR_STATE_NORMAL);
            toolbar.setVisibility(View.INVISIBLE);
            toolbar.setTranslationY(-toolbar.getHeight());
            toolbar.setVisibility(View.VISIBLE);
        }

        if (isScrollDown(scrollY) && toolbarState.equals(ImmersiveDetailActivity.ToolbarState.TOOLBAR_STATE_NORMAL) && toolbar.getTranslationY() < 0 && (scrollY-oldScrollY != 0)) {
            if(toolbar.getTranslationY() + Math.abs(scrollY-oldScrollY) <= 0){
                toolbar.setTranslationY(toolbar.getTranslationY() + Math.abs(scrollY-oldScrollY));
            }else{
                toolbar.setTranslationY(0);
            }
        }

        if (isScrollUp(scrollY) && toolbarState.equals(ImmersiveDetailActivity.ToolbarState.TOOLBAR_STATE_NORMAL) && toolbar.getTranslationY() <= 0 && (scrollY-oldScrollY != 0)) {
            if(toolbar.getTranslationY() - Math.abs(scrollY-oldScrollY) > -toolbar.getHeight()){
                toolbar.setTranslationY(toolbar.getTranslationY() - Math.abs(scrollY-oldScrollY));
            }else{
                toolbar.setTranslationY(-toolbar.getHeight());
            }
        }

        if(imageContainer.getTranslationY()*2 <= getFlexibleSpace() && toolbarState.equals(ImmersiveDetailActivity.ToolbarState.TOOLBAR_STATE_NORMAL)) {
            final ObjectAnimator colorFade = ObjectAnimator.ofObject(toolbar, "backgroundColor", new ArgbEvaluator(), toolbarColor, android.R.color.transparent);
            colorFade.setDuration(300);
            colorFade.start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    toolbarTitle.setVisibility(View.INVISIBLE);
                }
            }, 150);
            toolbarState = ImmersiveDetailActivity.ToolbarState.TOOLBAR_STATE_TRANSPARENT;
        }

        setScrollDirections(scrollY);
    }

    private void setScrollDirections(int scrollY) {
        if(scrollY > oldScrollY)
            lastScrollYDirection = 1;
        if(scrollY < oldScrollY)
            lastScrollYDirection = -1;

        oldScrollY = scrollY;
    }

    private boolean isScrollDown(int scrollY) {
        return scrollY <= oldScrollY && lastScrollYDirection == -1;
    }

    private boolean isScrollUp(int scrollY){
        return scrollY >= oldScrollY && lastScrollYDirection == 1;
    }

    public int getFlexibleSpace(){
        return imageContainer.getHeight() - toolbar.getHeight();
    }
}
