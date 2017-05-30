package com.lws.allenglish.ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lws.allenglish.R;
import com.lws.allenglish.ui.activities.BookmarkActivity;
import com.lws.allenglish.ui.activities.CompositionActivity;
import com.lws.allenglish.ui.activities.ComprehensionActivity;
import com.lws.allenglish.ui.activities.ComprehensionEnglishActivity;
import com.lws.allenglish.ui.activities.DictionaryActivity;
import com.lws.allenglish.ui.activities.LeaningEnglishActivity;
import com.lws.allenglish.ui.activities.WordActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabLearningFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.learning_english)
    TextView mLearning_english;
    @BindView(R.id.bookmark)
    TextView mbookmark;
    @BindView(R.id.comprehension)
    TextView mComprehension;
    @BindView(R.id.composition)
    TextView mComposition;
    @BindView(R.id.word)
    TextView mWord;
    @BindView(R.id.dictionary)
    TextView mDictionary;

    private Context mContext;


    public TabLearningFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_learning, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity();
        setUi();
        return view;
    }

    private void setUi() {
        mLearning_english.setOnClickListener(this);
        mbookmark.setOnClickListener(this);
        mComprehension.setOnClickListener(this);
        mComposition.setOnClickListener(this);
        mWord.setOnClickListener(this);
        mDictionary.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.learning_english:
                Intent intent = new Intent(mContext, LeaningEnglishActivity.class);
                startActivity(intent);
                break;
            case R.id.bookmark:
                Intent intent2 = new Intent(mContext, BookmarkActivity.class);
                startActivity(intent2);
                break;
            case R.id.comprehension:
                Intent intent3 = new Intent(mContext, ComprehensionActivity.class);
                startActivity(intent3);
                break;
            case R.id.composition:
                Intent intent4 = new Intent(mContext, CompositionActivity.class);
                startActivity(intent4);
                break;
            case R.id.word:
                Intent intent5 = new Intent(mContext, WordActivity.class);
                startActivity(intent5);
                break;
            case R.id.dictionary:
                Intent intent6 = new Intent(mContext, DictionaryActivity.class);
                startActivity(intent6);
                break;
        }
    }
}
