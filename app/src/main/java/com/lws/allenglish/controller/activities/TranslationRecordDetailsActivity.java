package com.lws.allenglish.controller.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.lws.allenglish.Constants;
import com.lws.allenglish.R;
import com.lws.allenglish.base.BaseActivity;
import com.lws.allenglish.bean.TranslationRecord;
import com.lws.allenglish.util.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TranslationRecordDetailsActivity extends BaseActivity {
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

        setToolbar(mToolbar);

        TranslationRecord record = (TranslationRecord) getIntent().getSerializableExtra(Constants.TRANSLATION_RECORD_DATA);
        CommonUtils.setTextView(mTranslationText, record.text);
        CommonUtils.setTextView(mTranslationResult, record.result);
    }
}
