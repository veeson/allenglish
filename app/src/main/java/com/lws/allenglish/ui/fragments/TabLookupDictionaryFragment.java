package com.lws.allenglish.ui.fragments;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lws.allenglish.AppConstants;
import com.lws.allenglish.R;
import com.lws.allenglish.bean.BaseWord;
import com.lws.allenglish.db.DictionaryDatabaseManager;
import com.lws.allenglish.ui.activities.BookmarkActivity;
import com.lws.allenglish.ui.activities.LeaningEnglishActivity;
import com.lws.allenglish.ui.activities.SearchWordActivity;
import com.lws.allenglish.ui.activities.WordDetailsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabLookupDictionaryFragment extends Fragment {
    @BindView(R.id.random_word_layout)
    LinearLayout mRandomWordLayout;
    @BindView(R.id.random_word)
    TextView mRandomWord;
    @BindView(R.id.update_word)
    ImageView mUpdateWord;
    @BindView(R.id.random_word_explain)
    TextView mRandomWordExplain;
    @BindView(R.id.search_word)
    TextView mSearchWord;
    @BindView(R.id.learning_english)
    TextView mLearningEnglish;
    @BindView(R.id.bookmark)
    TextView mBookmark;

    private Context mContext;
    private BaseWord mBaseWord;

    public TabLookupDictionaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_lookup_dictionary, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity();
        DictionaryDatabaseManager.openDatabase(mContext);  // 打开离线词典数据库
        setListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        randomWord();
    }

    private void randomWord() {
        mBaseWord = DictionaryDatabaseManager.randomWord();
        if (TextUtils.isEmpty(mBaseWord.word)) {
            mBaseWord = DictionaryDatabaseManager.randomWord();
        }
        mRandomWord.setText(mBaseWord.word);
        mRandomWordExplain.setText(mBaseWord.means);
    }

    private void setListener() {
        mRandomWordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WordDetailsActivity.class);
                intent.putExtra(AppConstants.BASE_INFO, mBaseWord);
                startActivity(intent);
            }
        });
        mUpdateWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator//
                        .ofFloat(mUpdateWord, "rotationX", 0.0F, 360.0F)//
                        .setDuration(500)//
                        .start();
                randomWord();
            }
        });
        mSearchWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SearchWordActivity.class);
                startActivity(intent);
            }
        });

        mLearningEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LeaningEnglishActivity.class);
                startActivity(intent);
            }
        });

        mBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BookmarkActivity.class);
                startActivity(intent);
            }
        });
    }

}
