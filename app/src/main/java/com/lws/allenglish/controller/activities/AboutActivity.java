package com.lws.allenglish.controller.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.lws.allenglish.R;
import com.lws.allenglish.base.BaseActivity;
import com.lws.allenglish.controller.fragments.AboutFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.frame_layout)
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        setToolbar(toolbar);
        getFragmentManager().beginTransaction().replace(R.id.frame_layout, new AboutFragment()).commit();
    }
}
