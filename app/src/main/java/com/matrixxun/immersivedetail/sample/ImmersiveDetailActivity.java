package com.matrixxun.immersivedetail.sample;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
public class ImmersiveDetailActivity extends BaseActivity implements ObservableScrollView.ScrollViewListener{

    private static final int TOOLBAR_STATE_NORMAL = 0;
    private static final int TOOLBAR_STATE_TRANSPARENT = 1;

    private ObservableScrollView scrollview;
    private FrameLayout imageContainer;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    protected ActionBar supportActionBar;
    private ViewPager mViewPager;
    private PagerIndicator mPagerIndicator;
    private FloatingActionButton fab;

    private int toolbarColor;
    private int toolbarState = TOOLBAR_STATE_NORMAL;
    private int oldScrollY = 0;
    private int lastScrollYDirection = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LollipopCompatSingleton.translucentStatusBar(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_immersive_detail_);
        toolbar = ViewHelper.$(this,R.id.toolbar_default);
        toolbarTitle = ViewHelper.$(this,R.id.toolbarTitle);
        scrollview = ViewHelper.$(this,R.id.scrollview);
        imageContainer = ViewHelper.$(this,R.id.image_container);
        mViewPager = ViewHelper.$(this,R.id.bulletpager);
        mPagerIndicator = ViewHelper.$(this,R.id.view_pager_indicator);
        fab = ViewHelper.$(this,R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"Add wish list success!",Snackbar.LENGTH_LONG).show();
            }
        });

        BannerPagerAdapter pagerAdapter = new BannerPagerAdapter(this);
        mViewPager.setAdapter(pagerAdapter);
        mPagerIndicator.setViewPager(mViewPager);

        if(toolbar != null){
            setSupportActionBar(toolbar);
            supportActionBar = getSupportActionBar();
        }


        LollipopCompatSingleton.getInstance().fitStatusBarTranslucentPadding(toolbar, this);
        showToolbarTitle(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbarTitle.setText("Single Activity");
        toolbarTitle.setVisibility(View.INVISIBLE);

        toolbarColor = ContextCompat.getColor(this,R.color.colorPrimary);

        setToolbarColor(TOOLBAR_STATE_TRANSPARENT);
        scrollview.setScrollViewListener(this);
    }

    public void setToolbarColor(int state) {
        if (toolbarState != state) {
            toolbar.setBackgroundColor(state == TOOLBAR_STATE_TRANSPARENT ? ContextCompat.getColor(this, android.R.color.transparent) : toolbarColor);
            toolbarState = state;
        }

        if (state == TOOLBAR_STATE_NORMAL) {
            toolbarTitle.setVisibility(View.VISIBLE);
        } else if (state == TOOLBAR_STATE_TRANSPARENT) {
            toolbarTitle.setVisibility(View.INVISIBLE);
        }

    }

    public void showToolbarTitle(boolean showTitle){
        if(supportActionBar != null)
            supportActionBar.setDisplayShowTitleEnabled(showTitle);
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



    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int scrollY, int oldx, int oldyY) {
        imageContainer.setTranslationY(scrollY * 0.5f);

        if(scrollY-getFlexibleSpace() < toolbar.getHeight() && toolbarState == TOOLBAR_STATE_TRANSPARENT){
            float y = Math.min(0, -scrollY + getFlexibleSpace());
            if(y<-1){
                LollipopCompatSingleton.setStatusBarColorFade(this,ContextCompat.getColor(this,R.color.colorPrimaryDark),300);
            }else if(y==0){
                LollipopCompatSingleton.setStatusBarColorImmediately(this,ContextCompat.getColor(this,android.R.color.transparent));
            }
            toolbar.setTranslationY(y);
        }


        if(scrollY >= imageContainer.getHeight() && isScrollDown(scrollY) && toolbarState !=TOOLBAR_STATE_NORMAL){
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

        if(imageContainer.getTranslationY()*2 <= getFlexibleSpace() && toolbarState == TOOLBAR_STATE_NORMAL) {
            final ObjectAnimator colorFade = ObjectAnimator.ofObject(toolbar, "backgroundColor", new ArgbEvaluator(), toolbarColor, android.R.color.transparent);
            colorFade.setDuration(300);
            colorFade.start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    toolbarTitle.setVisibility(View.INVISIBLE);
                }
            }, 150);
            toolbarState = TOOLBAR_STATE_TRANSPARENT;
        }

        setScrollDirections(scrollY);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if(id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

}
