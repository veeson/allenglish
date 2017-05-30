package com.lws.allenglish.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.lws.allenglish.AppConstants;
import com.lws.allenglish.R;
import com.lws.allenglish.adapter.BaseWordAdapter;
import com.lws.allenglish.bean.BaseWord;
import com.lws.allenglish.connector.OnItemClickListener;
import com.lws.allenglish.db.AllEnglishDatabaseManager;
import com.lws.allenglish.db.DictionaryDatabaseManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchWordActivity extends AppCompatActivity {
    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.input_word)
    EditText mInputWord;
    @BindView(R.id.clear)
    ImageView mClear;
    @BindView(R.id.word_matching)
    RecyclerView mRecyclerView;

    //    private static final String TAG = "SearchWordActivity";
    private BaseWordAdapter mAdapter;
    private AllEnglishDatabaseManager mDatabaseManager;
    private List<BaseWord> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_word);
        ButterKnife.bind(this);
        mDatabaseManager = AllEnglishDatabaseManager.getInstance(this);
//        DictionaryDatabaseManager.openDatabase(this); // 打开离线词典数据库
        mList.addAll(mDatabaseManager.loadQueriedWords());
        Collections.reverse(mList);
        initRecyclerView();
        initOtherUi();
    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
//        mRecyclerView.addItemDecoration(new DividerDecoration(this));

        mAdapter = new BaseWordAdapter(mList);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BaseWord baseWord = mList.get(position);
                if (!mDatabaseManager.existSearchWordHistory(baseWord.word))
                    mDatabaseManager.saveQueriedWord(baseWord);
                Intent intent = new Intent(SearchWordActivity.this, WordDetailsActivity.class);
                intent.putExtra(AppConstants.BASE_INFO, baseWord);
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initOtherUi() {
        mInputWord.addTextChangedListener(new TextChangedListener());
        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInputWord.setText("");
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private class TextChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String keyWord = editable.toString().trim();
            if (keyWord.isEmpty()) {
                mClear.setVisibility(View.INVISIBLE);
                List<BaseWord> list = mDatabaseManager.loadQueriedWords();
                if (list.isEmpty()) {
                    return;
                }
                Collections.reverse(list);
                matchingWord(list);
                return;
            } else {
                mClear.setVisibility(View.VISIBLE);
            }
            if (keyWord.contains("%")) {
                ignoreKeyChar(keyWord, "%");
                return;
            }
            if (keyWord.contains("'")) {
                ignoreKeyChar(keyWord, "'");
                return;
            }
            List<BaseWord> list = DictionaryDatabaseManager.matchingWord(keyWord);
            if (!list.isEmpty()) {
                matchingWord(list);
            }
        }
    }

    private void matchingWord(List<BaseWord> list2) {
        clearListData();
        mList.addAll(list2);
        mAdapter.notifyDataSetChanged();
    }

    private void ignoreKeyChar(String keyWord, String c) {
        String subKeyWord = null;
        try {
            subKeyWord = keyWord.split(c)[0];
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        if (subKeyWord != null) {
            matchingWord(DictionaryDatabaseManager.matchingWord(subKeyWord));
        }
    }

    private void clearListData() {
        if (!mList.isEmpty()) {
            mList.clear();
        }
    }
}
