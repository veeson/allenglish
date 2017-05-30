package com.lws.allenglish.ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lws.allenglish.AppConstants;
import com.lws.allenglish.R;
import com.lws.allenglish.adapter.ReaderAdapter;
import com.lws.allenglish.base.BaseFragment;
import com.lws.allenglish.bean.BaseEnglish;
import com.lws.allenglish.bean.Reader;
import com.lws.allenglish.bean.VOA;
import com.lws.allenglish.connector.OnItemClickListener;
import com.lws.allenglish.ui.activities.ReaderActivity;
import com.lws.allenglish.ui.activities.VOAActivity;
import com.lws.allenglish.ui.activities.VideoActivity;
import com.lws.allenglish.utils.GsonRequest;
import com.lws.allenglish.utils.VolleySingleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReaderFragment extends BaseFragment {
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.article_list)
    RecyclerView mRecyclerView;

    //    private static final String TAG = "ReaderFragment";
    private Context mContext;
    private LinearLayoutManager mLinearLayoutManager;
    private ReaderAdapter mAdapter;
    private List<BaseEnglish.ResultsEntity> mList = new ArrayList<>();
    // 表示viewpaper的页面
    private int mPageNumber = 0;
    // 数据刷新方式，1表示初次进入fragment更新,2表示下拉刷新,3表示上拉刷新
    private int mTriggerLoadingDataForm = 0;
    // 表示最后一个可视条目
    private int lastVisibleItem = 0;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            handlePageData(msg);
        }
    };

    private void handlePageData(Message msg) {
        switch (mPageNumber) { // mPageNumber
            case 0:
                Reader reader = (Reader) msg.obj;
                handleReaderMessage(msg, reader);
                break;
            case 1:
            case 2:
                VOA voa = (VOA) msg.obj;
                handleVOAMessage(msg, voa);
                break;
        }
    }

    private void handleReaderMessage(Message msg, Reader reader) {
        switch (msg.what) { // mTriggerLoadingDataForm
            case 1:
                mList.addAll(reader.results);
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                break;
            case 2:
                if (!mList.isEmpty()) {
                    mList.clear();
                }
                mList.addAll(reader.results);
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                break;
            case 3:
                mList.addAll(reader.results);
                mAdapter.changeMoreStatus(ReaderAdapter.PULLUP_LOAD_MORE);
                break;

        }
    }

    private void handleVOAMessage(Message msg, VOA voa) {
        switch (msg.what) {
            case 1:
                mList.addAll(voa.results);
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                break;
            case 2:
                if (!mList.isEmpty()) {
                    mList.clear();
                }
                mList.addAll(voa.results);
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                break;
            case 3:
                mList.addAll(voa.results);
                mAdapter.changeMoreStatus(ReaderAdapter.PULLUP_LOAD_MORE);
                break;

        }
    }

    // newInstance constructor for creating fragment with arguments
    public static ReaderFragment newInstance(int page) {
        ReaderFragment fragmentFirst = new ReaderFragment();
        Bundle args = new Bundle();
        args.putInt(AppConstants.PAGE_NUMBER, page);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(AppConstants.PAGE_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_learning_english_layout, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity();
        mSwipeRefreshLayout.setColorSchemeResources(R.color.app_color);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mTriggerLoadingDataForm = 2;
                fetchReaderFromInternet(whichPage(mPageNumber), whichClass(mPageNumber), new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.CHINA).format(new Date()), 10, 0);
            }
        });
        initRecyclerView();
        mIsPrepared = true;
        lazyLoad();
        return view;
    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new ReaderAdapter(mContext, mList, mPageNumber, true);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = null;
                switch (mPageNumber) {
                    case 0:
                        intent = new Intent(mContext, ReaderActivity.class);
                        intent.putExtra(AppConstants.DATA_SHEET, "Reader");
                        intent.putExtra(AppConstants.BASE_ENGLISH, ((List<Reader.ResultsEntity>) (List<?>) mList).get(position));
                        break;
                    case 1:
                        intent = new Intent(mContext, VOAActivity.class);
                        intent.putExtra(AppConstants.BASE_ENGLISH, ((List<VOA.ResultsEntity>) (List<?>) mList).get(position));
                        break;
                    case 2:
                        intent = new Intent(mContext, VideoActivity.class);
                        intent.putExtra(AppConstants.BASE_ENGLISH, ((List<VOA.ResultsEntity>) (List<?>) mList).get(position));
                        break;
                }
                startActivity(intent);
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mAdapter.getItemCount()) {
                    mAdapter.changeMoreStatus(ReaderAdapter.LOADING_MORE);
                    mTriggerLoadingDataForm = 3;
                    fetchReaderFromInternet(whichPage(mPageNumber), whichClass(mPageNumber), new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.CHINA).format(new Date()), 10, mList.size());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private String whichPage(int pageNumber) {
        String page = null;
        switch (pageNumber) {
            case 0:
                page = "Reader";
                break;
            case 1:
                page = "VOA";
                break;
            case 2:
                page = "Video";
                break;
        }
        return page;
    }

    private Class whichClass(int pageNumber) {
        Class aClass = null;
        switch (pageNumber) {
            case 0:
                aClass = Reader.class;
                break;
            case 1:
                aClass = VOA.class;
                break;
            case 2:
                aClass = VOA.class;
                break;
        }
        return aClass;
    }

    /**
     * 向handle发送信息
     *
     * @param t                      要发送的对象object
     * @param triggerLoadingDataForm 数据加载方式，1表示初次进入fragment时加载，2表示上拉加载，3表示下拉加载。
     * @param <T>
     */
    private <T extends BaseEnglish> void sendMessage(T t, int triggerLoadingDataForm) {
        Message msg = mHandler.obtainMessage();
        msg.what = triggerLoadingDataForm;
        msg.obj = t;
        msg.sendToTarget();
    }

    private <T extends BaseEnglish> void fetchReaderFromInternet(String object, final Class<T> type, String date, int limit, int skip) {
        Map<String, String> headers = new HashMap<>();
        headers.put(AppConstants.CONTENT_TYPE, AppConstants.CONTENT_TYPE_VALUE);
        headers.put(AppConstants.X_LC_Id, AppConstants.X_LC_ID_VALUE);
        headers.put(AppConstants.X_LC_Key, AppConstants.X_LC_KEY_VALUE);
        VolleySingleton.getInstance(mContext)
                .addToRequestQueue(new GsonRequest<>("https://leancloud.cn:443/1.1/classes/" + object + "?where={\"postTime\":{\"$lte\":{\"__type\":\"Date\",\"iso\":\"" + date + "\"}}}&limit=" + limit + "&skip=" + skip + "&order=-postTime"
                        , type, headers, new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {
                        sendMessage(response, mTriggerLoadingDataForm);
                        mHasLoadedOnce = true;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mHasLoadedOnce = true;
                        Toast.makeText(mContext, R.string.error_loading, Toast.LENGTH_SHORT).show();
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }));
    }

    @Override
    protected void lazyLoad() {
        if (!mIsPrepared || !mIsVisible || mHasLoadedOnce) {
            return;
        }
        mTriggerLoadingDataForm = 1;
        mSwipeRefreshLayout.setRefreshing(true);
        fetchReaderFromInternet(whichPage(mPageNumber), whichClass(mPageNumber), new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.CHINA).format(new Date()), 10, 0);
    }
}
