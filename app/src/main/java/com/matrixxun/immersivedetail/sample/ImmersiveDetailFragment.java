package com.matrixxun.immersivedetail.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class ImmersiveDetailFragment  extends Fragment{

    private Toolbar toolbar;
    private TextView toolbarTitle;


    private ViewPager mViewPager;
    private PagerIndicator mPagerIndicator;
    private ObservableScrollView scrollview;
    private FrameLayout imageContainer;

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
        LollipopCompatSingleton.getInstance().fitStatusBarTranslucentPadding(toolbar, rootActivity);

        BannerPagerAdapter pagerAdapter = new BannerPagerAdapter(rootActivity);
        mViewPager.setAdapter(pagerAdapter);
        mPagerIndicator.setViewPager(mViewPager);

        rootActivity.setSupportActionBar(toolbar);
        toolbarTitle.setText("Activity with Fragment");
        ActionBar supportActionBar = rootActivity.getSupportActionBar();
        if(supportActionBar != null){
            supportActionBar.setDisplayShowTitleEnabled(false);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }

        int toolbarColor = ContextCompat.getColor(rootActivity,R.color.colorPrimary);
        scrollview.setupImmersiveEffect(getActivity(),imageContainer,toolbar,toolbarColor,toolbarTitle);
    }
}
