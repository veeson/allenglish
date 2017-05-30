package com.lws.allenglish.ui.activities;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lws.allenglish.AppConstants;
import com.lws.allenglish.R;
import com.lws.allenglish.adapter.ReaderAdapter;
import com.lws.allenglish.base.BaseApplication;
import com.lws.allenglish.bean.Reader;
import com.lws.allenglish.connector.OnItemClickListener;
import com.lws.allenglish.utils.GsonRequest;
import com.lws.allenglish.utils.StringUtils;
import com.lws.allenglish.utils.TimeUtils;
import com.lws.allenglish.utils.VolleySingleton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReaderActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.nested_scrollview)
    NestedScrollView mNestedScrollView;
    @BindView(R.id.news_title)
    TextView mNewsTitle;
    @BindView(R.id.source_and_date)
    TextView mSourceAndDate;
    @BindView(R.id.news_content)
    TextView mNewsContent;
    @BindView(R.id.related_recommendation)
    RecyclerView mRecyclerView;

    private Context mContext;
    private String mDataSheet;
    private ReaderAdapter mAdapter;
    private List<Reader.ResultsEntity> mList = new ArrayList<>();

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Reader reader = (Reader) msg.obj;
                    if (!mList.isEmpty()) {
                        mList.clear();
                    }
                    mList.addAll(reader.results);
                    mAdapter.notifyDataSetChanged();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        ButterKnife.bind(this);
        mContext = BaseApplication.getInstance();
        mToolbar.setNavigationIcon(R.drawable.md_nav_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Reader.ResultsEntity mResultsEntity = (Reader.ResultsEntity) getIntent().getSerializableExtra(AppConstants.BASE_ENGLISH);
        mDataSheet = getIntent().getStringExtra(AppConstants.DATA_SHEET);
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/MILT_RG.ttf");
        mNewsTitle.setTypeface(type);
        mNewsContent.setTypeface(type);
        mNewsTitle.setTextIsSelectable(true); // 设置文本可选
        mNewsContent.setTextIsSelectable(true); // 设置文本可选
        setContent(mResultsEntity.newsTitle, mResultsEntity.source, mResultsEntity.postTime.iso, mResultsEntity.newsContent);

        initRecyclerView();

        fetchReaderFromInternet(mDataSheet, mResultsEntity.createdAt, mResultsEntity.tag);
    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new ReaderAdapter(mContext, mList, 0, false);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Reader.ResultsEntity resultsEntity = mList.get(position);
                setContent(resultsEntity.newsTitle, resultsEntity.source, resultsEntity.postTime.iso, resultsEntity.newsContent);
                mNestedScrollView.scrollTo(0, 0);
                fetchReaderFromInternet(mDataSheet, resultsEntity.createdAt, resultsEntity.tag);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setContent(String newsTitle, String source, String date, String newsContent) {
        mNewsTitle.setText(newsTitle);
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.CHINA);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            mSourceAndDate.setText(new StringBuilder().append("来源：").append(source).append("   ").append(TimeUtils.DATE_FORMAT_DATE.format(dateFormat.parse(date))));
        } catch (ParseException ignored) {
        }
        mNewsContent.setText(Html.fromHtml(newsContent).toString().replace("\n\n\n", "\n\n"));
    }

    private void fetchReaderFromInternet(String dataSheet, String date, final String tag) {
        Map<String, String> headers = new HashMap<>();
        headers.put(AppConstants.CONTENT_TYPE, AppConstants.CONTENT_TYPE_VALUE);
        headers.put(AppConstants.X_LC_Id, AppConstants.X_LC_ID_VALUE);
        headers.put(AppConstants.X_LC_Key, AppConstants.X_LC_KEY_VALUE);
        String url = "https://leancloud.cn:443/1.1/classes/" + dataSheet + "?where={\"createdAt\":{\"$lt\":{\"__type\":\"Date\",\"iso\":\"" + date + "\"}},\"tag\":\"" + StringUtils.encodeText(tag) + "\"}}&limit=5&&order=-createdAt";
        VolleySingleton.getInstance(mContext)
                .addToRequestQueue(new GsonRequest<>(url, Reader.class, headers, new Response.Listener<Reader>() {
                    @Override
                    public void onResponse(Reader response) {
                        Message msg = mHandler.obtainMessage();
                        msg.what = 1;
                        msg.obj = response;
                        msg.sendToTarget();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ReaderActivity.this, R.string.error_loading, Toast.LENGTH_SHORT).show();
                    }
                }));

    }
}
