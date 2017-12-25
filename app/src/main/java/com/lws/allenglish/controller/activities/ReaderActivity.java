package com.lws.allenglish.controller.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.lws.allenglish.R;
import com.lws.allenglish.base.BaseLearningActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReaderActivity extends BaseLearningActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        ButterKnife.bind(this);
        setToolbar(toolbar);
        init();
    }
}
