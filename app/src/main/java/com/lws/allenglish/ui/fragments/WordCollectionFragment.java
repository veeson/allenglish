package com.lws.allenglish.ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lws.allenglish.AppConstants;
import com.lws.allenglish.R;
import com.lws.allenglish.adapter.WordCollectionAdapter;
import com.lws.allenglish.base.BaseFragment;
import com.lws.allenglish.bean.BaseWord;
import com.lws.allenglish.connector.OnItemClickListener;
import com.lws.allenglish.db.AllEnglishDatabaseManager;
import com.lws.allenglish.ui.activities.BookmarkActivity;
import com.lws.allenglish.ui.activities.WordDetailsActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class WordCollectionFragment extends Fragment {
    @BindView(R.id.word_collection)
    RecyclerView mRecyclerView;

//    private static final String TAG = "WordCollectionFragment";
    private Context mContext;
    private WordCollectionAdapter mAdapter;
    private AllEnglishDatabaseManager mDatabaseManager;
    private List<BaseWord> mList = new ArrayList<>();

    public WordCollectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word_collection, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity();
        mDatabaseManager = AllEnglishDatabaseManager.getInstance(mContext);
        initRecyclerView();
        new LoadWordCollectionTask().execute();
//        mIsPrepared = true;
//        lazyLoad();
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
                intent.putExtra(AppConstants.BASE_INFO, baseWord);
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
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
//            mHasLoadedOnce = true;
        }

    }

//    @Override
//    protected void lazyLoad() {
//        if (!mIsPrepared || !mIsVisible || mHasLoadedOnce) {
//            return;
//        }
//        new LoadWordCollectionTask().execute();
//    }
}
