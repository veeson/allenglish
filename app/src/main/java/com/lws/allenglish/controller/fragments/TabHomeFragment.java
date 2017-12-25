package com.lws.allenglish.controller.fragments;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lws.allenglish.Constants;
import com.lws.allenglish.R;
import com.lws.allenglish.base.BaseApplication;
import com.lws.allenglish.base.BaseFragment;
import com.lws.allenglish.bean.BaseWord;
import com.lws.allenglish.bean.IcibaSentence;
import com.lws.allenglish.controller.activities.WordDetailsActivity;
import com.lws.allenglish.database.DictionaryDatabaseManager;
import com.lws.allenglish.model.OnTabHomeModelListener;
import com.lws.allenglish.model.TabHomeModel;
import com.lws.allenglish.model.impl.TabHomeModelImpl;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TabHomeFragment extends BaseFragment {
    @BindView(R.id.random_word_layout)
    LinearLayout mRandomWordLayout;
    @BindView(R.id.random_word)
    TextView mRandomWord;
    @BindView(R.id.update_word)
    ImageView mUpdateWord;
    @BindView(R.id.random_word_explain)
    TextView mRandomWordExplain;
    @BindView(R.id.iciba_sentence)
    TextView icibaSentence;

    private BaseWord mBaseWord;
    private String sentenceUrl;
    private TabHomeModel model;

    public TabHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_home, container, false);
        ButterKnife.bind(this, view);
        unbinder = ButterKnife.bind(this, view);
        icibaSentence.setVisibility(View.GONE);
        DictionaryDatabaseManager.openDatabase(BaseApplication.getInstance());  // 打开离线词典数据库
        model = new TabHomeModelImpl(new CustomOnTabHomeModelListener());
        setListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        model.getRandomWord();
        model.getIcibaSentence();
    }

    private void setListener() {
        mRandomWordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WordDetailsActivity.class);
                intent.putExtra(Constants.BASE_INFO, mBaseWord);
                startActivity(intent);
            }
        });
        mUpdateWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.getRandomWord();
            }
        });
    }

    private class CustomOnTabHomeModelListener implements OnTabHomeModelListener {

        @Override
        public void onShowRandomWord(BaseWord baseWord) {
            mBaseWord = baseWord;
            ObjectAnimator//
                    .ofFloat(mUpdateWord, "rotationX", 0.0F, 360.0F)//
                    .setDuration(500)//
                    .start();
            mRandomWord.setText(baseWord.word);
            mRandomWordExplain.setText(baseWord.means);
        }

        @Override
        public void onGetIcibaSentence(IcibaSentence sentence) {
            icibaSentence.setVisibility(View.VISIBLE);
            sentenceUrl = sentence.tts;
            String note = sentence.content + "\n\n" + sentence.note;
            icibaSentence.setText(note);
        }
    }
}
