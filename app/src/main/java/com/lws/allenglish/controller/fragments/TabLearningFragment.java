package com.lws.allenglish.controller.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lws.allenglish.R;
import com.lws.allenglish.base.BaseFragment;
import com.lws.allenglish.controller.activities.AboutActivity;
import com.lws.allenglish.controller.activities.BookmarkActivity;
import com.lws.allenglish.controller.activities.DictionaryActivity;
import com.lws.allenglish.controller.activities.LearningListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TabLearningFragment extends BaseFragment implements View.OnClickListener {
    @BindView(R.id.bilingual_reading)
    TextView mBilingualReading;
    @BindView(R.id.bookmark)
    TextView mBookmark;
    @BindView(R.id.english_video_learning)
    TextView mEnglishVideoLearning;
    @BindView(R.id.voa_english)
    TextView mVoaEnglish;
    @BindView(R.id.dictionary)
    TextView mDictionary;
    @BindView(R.id.about)
    TextView mAbout;

    public TabLearningFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_learning, container, false);
        ButterKnife.bind(this, view);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        mBilingualReading.setOnClickListener(this);
        mBookmark.setOnClickListener(this);
        mEnglishVideoLearning.setOnClickListener(this);
        mVoaEnglish.setOnClickListener(this);
        mDictionary.setOnClickListener(this);
        mAbout.setOnClickListener(this);
    }

    private void start(int flag) {
        Intent intent = new Intent(mContext, LearningListActivity.class);
        intent.putExtra("flag", flag);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bilingual_reading:
                start(0);
                break;
            case R.id.voa_english:
                start(1);
                break;
            case R.id.english_video_learning:
                start(2);
                break;
            case R.id.bookmark:
                Intent intent2 = new Intent(mContext, BookmarkActivity.class);
                startActivity(intent2);
                break;
            case R.id.dictionary:
                Intent intent6 = new Intent(mContext, DictionaryActivity.class);
                startActivity(intent6);
                break;
            case R.id.about:
                Intent intent7 = new Intent(mContext, AboutActivity.class);
                startActivity(intent7);
                break;
        }
    }
}
