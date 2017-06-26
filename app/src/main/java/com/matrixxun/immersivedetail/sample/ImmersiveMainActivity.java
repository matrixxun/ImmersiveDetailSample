package com.matrixxun.immersivedetail.sample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.matrixxun.immersivedetail.sample.util.ViewHelper;

/**
 * Created by matrixxun on 2017/6/19.
 */
public class ImmersiveMainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = ViewHelper.$(this,R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = ViewHelper.$(this, R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        ViewHelper.$(this,R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImmersiveDetailActivity.startActivity(ImmersiveMainActivity.this,ImmersiveDetailActivity.class,null);
            }
        });

        ViewHelper.$(this,R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImmersiveDetailFragmentActivity.startActivity(ImmersiveMainActivity.this,ImmersiveDetailFragmentActivity.class,null);
            }
        });
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
        }

        return super.onOptionsItemSelected(item);
    }
}
