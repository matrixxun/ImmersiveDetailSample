package com.matrixxun.immersivedetail.sample.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.matrixxun.immersivedetail.sample.R;
import com.matrixxun.immersivedetail.sample.util.ViewHelper;

public class BannerPagerAdapter extends PagerAdapter {
        private LayoutInflater mInflater;
        public BannerPagerAdapter(Context context){
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mInflater.inflate(R.layout.item_image_viewpager,null);
            ImageView imageView = ViewHelper.$(view,R.id.image);
            switch(position){
                case 0:
                    imageView.setImageDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.image_0));
                    break;
                case 1:
                    imageView.setImageDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.image_1));
                    break;
                case 2:
                    imageView.setImageDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.image_2));
                    break;
                case 3:
                    imageView.setImageDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.image_3));
                    break;
                case 4:
                    imageView.setImageDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.image_4));
                    break;
                case 5:
                    imageView.setImageDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.image_5));
                    break;
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }