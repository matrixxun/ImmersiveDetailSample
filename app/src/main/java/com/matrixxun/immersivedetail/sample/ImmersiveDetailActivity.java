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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.matrixxun.immersivedetail.sample.adapter.BannerPagerAdapter;
import com.matrixxun.immersivedetail.sample.util.LollipopCompatSingleton;
import com.matrixxun.immersivedetail.sample.util.ViewHelper;
import com.matrixxun.immersivedetail.sample.widget.ObservableScrollView;
import com.matrixxun.immersivedetail.sample.widget.PagerIndicator;


public class ImmersiveDetailActivity extends BaseActivity implements ObservableScrollView.ScrollViewListener{

    private ObservableScrollView scrollview;
    private FrameLayout imageContainer;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    protected ActionBar supportActionBar;
    private ViewPager mViewPager;
    private PagerIndicator mPagerIndicator;
    private FloatingActionButton fab;

    private int toolbarColor;
    private ToolbarState toolbarState = ToolbarState.TOOLBAR_STATE_NORMAL;
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

        setToolbarColor(ToolbarState.TOOLBAR_STATE_TRANSPARENT);
        scrollview.setScrollViewListener(this);
        scrollview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(toolbar.getTranslationY() != 0 && toolbarState.equals(ToolbarState.TOOLBAR_STATE_NORMAL) && lastScrollYDirection == 1){ // UP
                        final AlphaAnimation fadeIn = new AlphaAnimation(1.0f, 0.0f);
                        fadeIn.setDuration(400);
                        fadeIn.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                toolbar.setVisibility(View.INVISIBLE);
                                setToolbarColor(ToolbarState.TOOLBAR_STATE_TRANSPARENT);
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

    public void setToolbarColor(ToolbarState state){
        if(!toolbarState.equals(state)) {
            toolbar.setBackgroundColor(state.isTransparent() ? ContextCompat.getColor(this,android.R.color.transparent): toolbarColor);
            toolbarState = state;
        }

        if(state.equals(ToolbarState.TOOLBAR_STATE_NORMAL))
            toolbarTitle.setVisibility(View.VISIBLE);
        else if(state.equals(ToolbarState.TOOLBAR_STATE_TRANSPARENT))
            toolbarTitle.setVisibility(View.INVISIBLE);
    }

    public void showToolbarTitle(boolean showTitle){
        if(supportActionBar != null)
            supportActionBar.setDisplayShowTitleEnabled(showTitle);
    }

    /**
     * This refers to the following 3 methods
     *
     * The onUpOrCancelMotionEvent() listener provided by the Observable library allow us to listen for Scroll-Up and Scroll-Down events
     * but won't fit this case because it will only notify us of Scroll-Directions after the scrolling has ended. We need a way to get
     * the scroll-direction while the user is still scrolling, not when he ended it. lastScrollDirection will contain that information,
     * 1 meaning Scroll-UP and -1 meaning Scroll-Down.
     *
     */
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



    /**
     * Flexible Space is the name given to the space between the imageContainer bottom border, and the toolbar bottom border.
     */
    public int getFlexibleSpace(){
        return imageContainer.getHeight() - toolbar.getHeight();
    }



    /**
     * This listener will allow us to hide back the toolbar once the solid toolbar is shown, the last scrolling was scroll-down and the scrolling
     * has ended.
     * Basically, if the user scrolls enough down so that when he scroll up again the solid toolbar is shown, the same can be partially hidden
     * if the user suddenly stops scrolling. For this reason, if the solid toolbar is shown, the last scroll action was down and the solid-toolbar is
     * not on its right position, the toolbar will hide itself automatically.
     */
    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int scrollY, int oldx, int oldyY) {
        /**
         * Set the Parallax effect through translation. We only need this.
         * While the whole view scrolls 1x, the image scrolls 0.5x
         */
        imageContainer.setTranslationY(scrollY * 0.5f);

        /**
         * This Condition will move the Toolbar Up, after the imageContainer bottom border reaches the toolbar bottom border
         * This effect will occur as long as the Toolbar is in transparent mode. when the toolbar is in color mode, the interactions
         * will be different.
         */
        if(scrollY-getFlexibleSpace() < toolbar.getHeight() && toolbarState.equals(ToolbarState.TOOLBAR_STATE_TRANSPARENT)){
            float y = Math.min(0, -scrollY + getFlexibleSpace());
            if(y<-1){
                LollipopCompatSingleton.setStatusBarColorFade(this,ContextCompat.getColor(this,R.color.colorPrimaryDark),300);
            }else if(y==0){
                LollipopCompatSingleton.setStatusBarColorImmediately(this,ContextCompat.getColor(this,android.R.color.transparent));
            }
            toolbar.setTranslationY(y);
        }


        /**
         * After a certain amount of scroll down after the image is hidden, the toolbar can be shows again by Scrolling-UP.
         * For that, first we need to reposition the toolbar back in its right position, which is up immediately after the top offscreen. This is because
         * the toolbar will continuously scrolling, even after offscreen.
         *
         * We also change its state (the backgroundColor) back to normal, although, at first, it is invisible (offscreen).         *
         * So after this point, the toolbar is immediately after the topBorder of the screen and it visible.
         */
        if(scrollY >= imageContainer.getHeight() && isScrollDown(scrollY) && !toolbarState.equals(ToolbarState.TOOLBAR_STATE_NORMAL)){
            setToolbarColor(ToolbarState.TOOLBAR_STATE_NORMAL);
            toolbar.setVisibility(View.INVISIBLE);
            toolbar.setTranslationY(-toolbar.getHeight());
            Log.d("justxdh","11111");
            toolbar.setVisibility(View.VISIBLE);
        }
        /**
         * So, after the toolbar is hidden, and enough scroll is 'scrolled' down, if we scroll-Up, the toolbar will be shown. The following
         * method translates the toolbar (slide top/offscreen - bottom) till its visible.
         * One thing here, if the amount of scroll is too big, the scroll of the toolbar will be iinstantaneous while if it is slow, the toolbar
         * scroll speed will match the scroll speed.
         */

        if (isScrollDown(scrollY) && toolbarState.equals(ToolbarState.TOOLBAR_STATE_NORMAL) && toolbar.getTranslationY() < 0 && (scrollY-oldScrollY != 0)) {
            if(toolbar.getTranslationY() + Math.abs(scrollY-oldScrollY) <= 0){
                toolbar.setTranslationY(toolbar.getTranslationY() + Math.abs(scrollY-oldScrollY));
                Log.d("justxdh","22222");
            }else{
                toolbar.setTranslationY(0);
                Log.d("justxdh","33333");
            }
        }

        /**
         * This method is the same as the previous one, but the reverse scrolling, Scrolling-UP.
         */

        if (isScrollUp(scrollY) && toolbarState.equals(ToolbarState.TOOLBAR_STATE_NORMAL) && toolbar.getTranslationY() <= 0 && (scrollY-oldScrollY != 0)) {
            if(toolbar.getTranslationY() - Math.abs(scrollY-oldScrollY) > -toolbar.getHeight()){
                toolbar.setTranslationY(toolbar.getTranslationY() - Math.abs(scrollY-oldScrollY));
                Log.d("justxdh","4444");
            }else{
                toolbar.setTranslationY(-toolbar.getHeight());
                Log.d("justxdh","55555");
            }
        }

        /**
         * If the user scrolled enough down so a Scroll-UP will show the solid toolbar, the same wont become transparent again unless the imageContainer
         * visible part in the screen has the same Height as the toolbar height. When that amount of scroll is reached, we fade the toolbar back
         * to its original state, which is transparent. The title is only shown in solid mode, so when changing modes we need to hide the text as well.
         */

        if(imageContainer.getTranslationY()*2 <= getFlexibleSpace() && toolbarState.equals(ToolbarState.TOOLBAR_STATE_NORMAL)) {
            Log.d("justxdh","666666");
            final ObjectAnimator colorFade = ObjectAnimator.ofObject(toolbar, "backgroundColor", new ArgbEvaluator(), toolbarColor, android.R.color.transparent);
            colorFade.setDuration(300);
            colorFade.start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    toolbarTitle.setVisibility(View.INVISIBLE);
                }
            }, 150);
            toolbarState = ToolbarState.TOOLBAR_STATE_TRANSPARENT;
        }

        setScrollDirections(scrollY);

    }

    /**
     * Enum used to keep track of tollbar states. When its on transparent mode, and when its on solid-color mode.
     */
    enum ToolbarState {
        TOOLBAR_STATE_NORMAL,
        TOOLBAR_STATE_TRANSPARENT;

        public boolean isNormal(){
            return this.equals(ToolbarState.TOOLBAR_STATE_NORMAL);
        }

        public boolean isTransparent(){
            return this.equals(ToolbarState.TOOLBAR_STATE_TRANSPARENT);
        }
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
