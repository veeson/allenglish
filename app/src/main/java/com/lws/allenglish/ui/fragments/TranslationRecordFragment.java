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

import com.afollestad.materialdialogs.MaterialDialog;
import com.lws.allenglish.AppConstants;
import com.lws.allenglish.R;
import com.lws.allenglish.adapter.TranslationRecordAdapter;
import com.lws.allenglish.base.BaseFragment;
import com.lws.allenglish.bean.TranslationRecord;
import com.lws.allenglish.connector.OnItemClickListener;
import com.lws.allenglish.db.AllEnglishDatabaseManager;
import com.lws.allenglish.ui.activities.BookmarkActivity;
import com.lws.allenglish.ui.activities.TranslationRecordDetailsActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.id.list;

/**
 * A simple {@link Fragment} subclass.
 */
public class TranslationRecordFragment extends Fragment {
    @BindView(R.id.translation_record)
    RecyclerView mRecyclerView;

    private Context mContext;
    TranslationRecordAdapter mAdapter;
    AllEnglishDatabaseManager mDatabaseManager;
    private List<TranslationRecord> mList = new ArrayList<>();

    public TranslationRecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translation_record, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity();
        mDatabaseManager = AllEnglishDatabaseManager.getInstance(mContext);
        initRecyclerView();
        new LoadTranslationRecordTask().execute();
        return view;
    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TranslationRecordAdapter(mList);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(mContext, TranslationRecordDetailsActivity.class);
                intent.putExtra(AppConstants.TRANSLATION_RECORD_DATA, mList.get(position));
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
        new MaterialDialog.Builder(mContext)
                .items(R.array.delete_option)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    BookmarkActivity activity = (BookmarkActivity) getActivity();
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                mDatabaseManager.deleteTranslationRecord(mList.get(position));
                                mList.remove(position);
                                mAdapter.notifyItemRemoved(position);
                                activity.mAdapter.refreshPagerTitle(1, "翻译(" + mList.size() + ")");
                                break;
                            case 1:
                                mDatabaseManager.deleteAllTranslationRecords();
                                mList.clear();
                                mAdapter.notifyDataSetChanged();
                                activity.mAdapter.refreshPagerTitle(1, "翻译(" + mList.size() + ")");
                                break;
                        }
                    }
                })
                .contentColor(getResources().getColor(android.R.color.darker_gray))
                .backgroundColor(getResources().getColor(android.R.color.white))
                .show();
    }

    private void onRefreshComplete() {
        mAdapter.notifyDataSetChanged();
        BookmarkActivity activity = (BookmarkActivity) getActivity();
        activity.mAdapter.refreshPagerTitle(1, "翻译(" + mList.size() + ")");
    }

    private class LoadTranslationRecordTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if (!mList.isEmpty()) {
                mList.clear();
            }
            mList.addAll(mDatabaseManager.loadTranslationRecords());
            Collections.reverse(mList);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            onRefreshComplete();
        }
    }
}
