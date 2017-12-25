package com.lws.allenglish.controller.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.lws.allenglish.Constants;
import com.lws.allenglish.R;
import com.lws.allenglish.base.BaseActivity;
import com.lws.allenglish.util.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BlankActivity extends BaseActivity {
    @BindView(R.id.expand_result)
    TextView mExpandResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);
        ButterKnife.bind(this);
        String transResult = getIntent().getStringExtra(Constants.TRANSLATION_RESULT);
        CommonUtils.setTextView(mExpandResult, transResult);
    }
}
