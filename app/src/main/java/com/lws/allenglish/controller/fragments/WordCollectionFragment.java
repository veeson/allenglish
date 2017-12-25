package com.lws.allenglish.controller.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lws.allenglish.Constants;
import com.lws.allenglish.R;
import com.lws.allenglish.adapter.WordCollectionAdapter;
import com.lws.allenglish.base.BaseApplication;
import com.lws.allenglish.base.BaseFragment;
import com.lws.allenglish.bean.BaseWord;
import com.lws.allenglish.connector.OnItemClickListener;
import com.lws.allenglish.controller.activities.BookmarkActivity;
import com.lws.allenglish.controller.activities.WordDetailsActivity;
import com.lws.allenglish.database.AllEnglishDatabaseManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WordCollectionFragment extends BaseFragment {
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private WordCollectionAdapter mAdapter;
    private AllEnglishDatabaseManager mDatabaseManager;
    private List<BaseWord> mList = new ArrayList<>();

    public WordCollectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_recycler_view_layout, container, false);
        ButterKnife.bind(this, view);
        unbinder = ButterKnife.bind(this, view);
        mDatabaseManager = AllEnglishDatabaseManager.getInstance(BaseApplication.getInstance());
        initRecyclerView();
        new LoadWordCollectionTask().execute();
        return view;
    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new WordCollectionAdapter(mList);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BaseWord baseWord = mList.get(position);
                Intent intent = new Intent(mContext, WordDetailsActivity.class);
                intent.putExtra(Constants.BASE_INFO, baseWord);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                showDeleteOption(position);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void showDeleteOption(final int position) {
        final CharSequence[] items = {"删除当前项", "删除所有项"};

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            BookmarkActivity activity = (BookmarkActivity) getActivity();

            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        mDatabaseManager.cancelCollectedWord(mList.get(position).word);
                        mList.remove(position);
                        mAdapter.notifyItemRemoved(position);
                        activity.mAdapter.refreshPagerTitle(0, "生词本(" + mList.size() + ")");
                        break;
                    case 1:
                        mDatabaseManager.cancelAllCollectedWords();
                        mList.clear();
                        mAdapter.notifyDataSetChanged();
                        activity.mAdapter.refreshPagerTitle(0, "生词本(" + mList.size() + ")");
                        break;
                }
            }
        }).show();
    }

    private void onRefreshComplete() {
        mAdapter.notifyDataSetChanged();
        BookmarkActivity activity = (BookmarkActivity) getActivity();
        activity.mAdapter.refreshPagerTitle(0, "生词本(" + mList.size() + ")");
    }

    private class LoadWordCollectionTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if (!mList.isEmpty()) {
                mList.clear();
            }
            mList.addAll(mDatabaseManager.loadCollectedWords());
            Collections.sort(mList);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            onRefreshComplete();
        }
    }
}
