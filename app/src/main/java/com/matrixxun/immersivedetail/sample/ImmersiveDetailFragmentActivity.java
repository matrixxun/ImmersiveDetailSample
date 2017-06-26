package com.matrixxun.immersivedetail.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.matrixxun.immersivedetail.sample.util.LollipopCompatSingleton;

/**
 * Created by matrixxun on 2017/6/19.
 */
public class ImmersiveDetailFragmentActivity extends BaseActivity {
    private ImmersiveDetailFragment immersiveDetailFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LollipopCompatSingleton.translucentStatusBar(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_immersive_detail_fragment);

        if (savedInstanceState == null) {
            immersiveDetailFragment = ImmersiveDetailFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                    immersiveDetailFragment, ImmersiveDetailFragment.class.getSimpleName()).commit();
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
