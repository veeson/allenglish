package com.lws.allenglish.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.lws.allenglish.AppConstants;
import com.lws.allenglish.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BlankActivity extends AppCompatActivity {
    @BindView(R.id.expand_result)
    TextView mExpandResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);
        ButterKnife.bind(this);
        mExpandResult.setMovementMethod(new ScrollingMovementMethod()); // 设置TextView可滚动
        mExpandResult.setTextIsSelectable(true); // 设置文本可选
        String transResult = getIntent().getStringExtra(AppConstants.TRANSLATION_RESULT);
        mExpandResult.setText(transResult);
    }
}
