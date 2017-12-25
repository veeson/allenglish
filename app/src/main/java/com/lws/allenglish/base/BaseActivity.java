package com.lws.allenglish.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lws.allenglish.R;

public class BaseActivity extends AppCompatActivity {
    protected Context mContext;
    protected Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }

    protected void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.md_nav_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
