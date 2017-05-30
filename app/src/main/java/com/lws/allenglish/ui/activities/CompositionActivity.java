package com.lws.allenglish.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lws.allenglish.AppConstants;
import com.lws.allenglish.R;
import com.lws.allenglish.adapter.ReaderAdapter;
import com.lws.allenglish.base.BaseApplication;
import com.lws.allenglish.bean.BaseEnglish;
import com.lws.allenglish.bean.Comprehension;
import com.lws.allenglish.bean.Reader;
import com.lws.allenglish.connector.OnItemClickListener;
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

public class CompositionActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.article_list)
    RecyclerView mRecyclerView;

    private Context mContext;
    private LinearLayoutManager mLinearLayoutManager;
    private ReaderAdapter mAdapter;
    private List<Reader.ResultsEntity> mList = new ArrayList<>();
    // 数据刷新方式，1表示初次进入fragment更新,2表示下拉刷新,3表示上拉刷新
    private int mTriggerLoadingDataForm = 1;
    // 表示最后一个可视条目
    private int lastVisibleItem = 0;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Reader reader = (Reader) msg.obj;
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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprehension);
        ButterKnife.bind(this);
        mContext = BaseApplication.getInstance();
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.md_nav_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.app_color);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mTriggerLoadingDataForm = 2;
                fetchComprehensionFromInternet(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.CHINA).format(new Date()), 10, 0);
            }
        });
        initRecyclerView();
        mSwipeRefreshLayout.setRefreshing(true);
        fetchComprehensionFromInternet(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.CHINA).format(new Date()), 10, 0);
    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new ReaderAdapter(mContext, mList, 0, true);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(mContext, ReaderActivity.class);
                intent.putExtra(AppConstants.BASE_ENGLISH, mList.get(position));
                intent.putExtra(AppConstants.DATA_SHEET, "Composition");
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
                    fetchComprehensionFromInternet(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.CHINA).format(new Date()), 10, mList.size());
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

    private void fetchComprehensionFromInternet(String date, int limit, int skip) {
        Map<String, String> headers = new HashMap<>();
        headers.put(AppConstants.CONTENT_TYPE, AppConstants.CONTENT_TYPE_VALUE);
        headers.put(AppConstants.X_LC_Id, AppConstants.X_LC_ID_VALUE);
        headers.put(AppConstants.X_LC_Key, AppConstants.X_LC_KEY_VALUE);
        VolleySingleton.getInstance(mContext)
                .addToRequestQueue(new GsonRequest<>("https://leancloud.cn:443/1.1/classes/Composition?where={\"postTime\":{\"$lte\":{\"__type\":\"Date\",\"iso\":\"" + date + "\"}}}&limit=" + limit + "&skip=" + skip + "&order=-postTime"
                        , Reader.class, headers, new Response.Listener<Reader>() {
                    @Override
                    public void onResponse(Reader response) {
                        Message msg = mHandler.obtainMessage();
                        msg.what = mTriggerLoadingDataForm;
                        msg.obj = response;
                        msg.sendToTarget();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CompositionActivity.this, R.string.error_loading, Toast.LENGTH_SHORT).show();
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }));
    }
}
