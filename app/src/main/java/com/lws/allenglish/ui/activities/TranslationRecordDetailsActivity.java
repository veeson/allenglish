package com.lws.allenglish.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.lws.allenglish.AppConstants;
import com.lws.allenglish.R;
import com.lws.allenglish.bean.TranslationRecord;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TranslationRecordDetailsActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.translation_text)
    TextView mTranslationText;
    @BindView(R.id.translation_result)
    TextView mTranslationResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation_record_details);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.md_nav_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTranslationText.setMovementMethod(new ScrollingMovementMethod()); // 设置TextView可滚动
        mTranslationResult.setMovementMethod(new ScrollingMovementMethod());
        mTranslationText.setTextIsSelectable(true); // 设置文本可选
        mTranslationResult.setTextIsSelectable(true);

        TranslationRecord record = (TranslationRecord) getIntent().getSerializableExtra(AppConstants.TRANSLATION_RECORD_DATA);
        mTranslationText.setText(record.text);
        mTranslationResult.setText(record.result);
    }
}
