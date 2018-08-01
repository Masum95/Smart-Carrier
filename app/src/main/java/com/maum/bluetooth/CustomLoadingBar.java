package com.maum.bluetooth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.style.WanderingCubes;

/**
 * Created by Masum Rahman on 7/16/2018.
 */
public class CustomLoadingBar extends AppCompatActivity {

    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.loading_screen);

        progressBar = (ProgressBar)findViewById(R.id.spin_kit);
        WanderingCubes wanderingCubes = new WanderingCubes();
        progressBar.setIndeterminateDrawable(wanderingCubes);
    }
}
