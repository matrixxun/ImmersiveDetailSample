package com.matrixxun.immersivedetail.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by matrixxun on 2017/6/19.
 */
public class BaseActivity extends AppCompatActivity {

    public static  void  startActivity(@NonNull Context context, Class<?> cls, @Nullable Bundle bundle){
        Intent intent = new Intent(context,cls);
        ActivityCompat.startActivity(context, intent, bundle);
    }
}
