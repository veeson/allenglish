package com.lws.allenglish.controller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.lws.allenglish.Constants;
import com.lws.allenglish.R;
import com.lws.allenglish.adapter.BaseWordAdapter;
import com.lws.allenglish.base.BaseActivity;
import com.lws.allenglish.bean.BaseWord;
import com.lws.allenglish.connector.OnItemClickListener;
import com.lws.allenglish.model.OnSearchWordListener;
import com.lws.allenglish.model.SearchWordModel;
import com.lws.allenglish.model.impl.SearchWordModelImpl;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchWordActivity extends BaseActivity {
    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.input_word)
    EditText mInputWord;
    @BindView(R.id.clear)
    ImageView mClear;
    @BindView(R.id.word_matching)
    RecyclerView mRecyclerView;

    private BaseWordAdapter mAdapter;
    private List<BaseWord> mList = new ArrayList<>();

    private SearchWordModel searchWordModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_word);
        ButterKnife.bind(this);
        initRecyclerView();
        initOtherUi();
        searchWordModel = new SearchWordModelImpl(new CustomOnSearchWordListener());
        searchWordModel.loadQueriedWords();
    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mAdapter = new BaseWordAdapter(mList);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BaseWord baseWord = mList.get(position);
                if ("".equals(mList.get(position).means)) {
                    Intent intent = new Intent();
                    intent.putExtra("TO_TRANSLATE", mList.get(position).word);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    searchWordModel.saveQueriedWord(baseWord);
                    Intent intent = new Intent(mContext, WordDetailsActivity.class);
                    intent.putExtra(Constants.BASE_INFO, baseWord);
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

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
            searchWordModel.matchingWord(keyWord);
        }
    }

    private class CustomOnSearchWordListener implements OnSearchWordListener {

        @Override
        public void onGetSearchWords(List<BaseWord> list) {
            mList.clear();
            mList.addAll(list);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onSetClearIconVisibility(int visibility) {
            mClear.setVisibility(visibility);
        }
    }
}
