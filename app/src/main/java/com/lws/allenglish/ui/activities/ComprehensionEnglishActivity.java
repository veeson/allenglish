package com.lws.allenglish.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lws.allenglish.AppConstants;
import com.lws.allenglish.R;
import com.lws.allenglish.adapter.ReaderAdapter;
import com.lws.allenglish.base.BaseApplication;
import com.lws.allenglish.bean.Comprehension;
import com.lws.allenglish.connector.OnItemClickListener;
import com.lws.allenglish.utils.GsonRequest;
import com.lws.allenglish.utils.NetWorkUtils;
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

public class ComprehensionEnglishActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener
        , MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener
        , MediaPlayer.OnSeekCompleteListener, SeekBar.OnSeekBarChangeListener
        , MediaPlayer.OnErrorListener {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.nested_scrollview)
    NestedScrollView mNestedScrollView;
    @BindView(R.id.news_title)
    TextView mNewsTitle;
    @BindView(R.id.source_and_date)
    TextView mSourceAndDate;
    @BindView(R.id.player_control)
    ImageView mPlayerControl;
    @BindView(R.id.audio_seek_bar)
    SeekBar mSeekBar;
    @BindView(R.id.news_content)
    TextView mNewsContent;
    @BindView(R.id.related_recommendation)
    RecyclerView mRecyclerView;

    private Context mContext;
    private Comprehension.ResultsEntity mResultsEntity;
    private MediaPlayer mMediaPlayer;
    private ReaderAdapter mAdapter;
    // 是否需要从网络上获取视频流
    private boolean mNeedToFetchFromInternet = true;

    private List<Comprehension.ResultsEntity> mList = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Comprehension comprehension = (Comprehension) msg.obj;
                if (!mList.isEmpty()) {
                    mList.clear();
                }
                mList.addAll(comprehension.results);
                mAdapter.notifyDataSetChanged();
            } else if (msg.what == 2) {
                mSeekBar.setProgress(0);
            } else if (!mNeedToFetchFromInternet) {
                mSeekBar.setProgress(msg.what);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voa);
        ButterKnife.bind(this);
        mContext = BaseApplication.getInstance();
        mToolbar.setNavigationIcon(R.drawable.md_nav_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mResultsEntity = (Comprehension.ResultsEntity) getIntent().getSerializableExtra(AppConstants.BASE_ENGLISH);
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/MILT_RG.ttf");
        mNewsTitle.setTypeface(type);
        mNewsContent.setTypeface(type);
        mNewsTitle.setTextIsSelectable(true); // 设置文本可选
        mNewsContent.setTextIsSelectable(true); // 设置文本可选
        setContent(mResultsEntity.newsTitle, mResultsEntity.source, mResultsEntity.postTime.iso, mResultsEntity.newsContent);

        mPlayerControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNeedToFetchFromInternet) {
                    if (NetWorkUtils.getNetworkTypeName(mContext).equals(NetWorkUtils.NETWORK_TYPE_DISCONNECT)) {
                        Toast.makeText(ComprehensionEnglishActivity.this, R.string.bad_internet, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mNeedToFetchFromInternet = false;
                    mPlayerControl.setSelected(true);
                    fetchVOAAudioFromInternet(mResultsEntity.audioUrl);
                    return;
                }
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    mPlayerControl.setSelected(false);
                } else {
                    mMediaPlayer.start();
                    mPlayerControl.setSelected(true);
                }
            }
        });
        mSeekBar.setOnSeekBarChangeListener(this);

        initMediaPlayer();
        initRecyclerView();

        fetchVOAAudioFromInternet(mResultsEntity.postTime.iso, mResultsEntity.tag);
    }

    private void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
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
        mAdapter = new ReaderAdapter(mContext, mList, 1, false);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = getIntent();
                intent.putExtra(AppConstants.BASE_ENGLISH, mList.get(position));
                finish();
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

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

    private void fetchVOAAudioFromInternet(String url) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchVOAAudioFromInternet(String date, final String tag) {
        Map<String, String> headers = new HashMap<>();
        headers.put(AppConstants.CONTENT_TYPE, AppConstants.CONTENT_TYPE_VALUE);
        headers.put(AppConstants.X_LC_Id, AppConstants.X_LC_ID_VALUE);
        headers.put(AppConstants.X_LC_Key, AppConstants.X_LC_KEY_VALUE);
        String url = "https://leancloud.cn:443/1.1/classes/Comprehension?where={\"postTime\":{\"$lt\":{\"__type\":\"Date\",\"iso\":\"" + date + "\"}},\"tag\":\"" + StringUtils.encodeText(tag) + "\",}}&limit=5&&order=-createdAt";
        VolleySingleton.getInstance()
                .addToRequestQueue(new GsonRequest<>(url, Comprehension.class, headers, new Response.Listener<Comprehension>() {
                    @Override
                    public void onResponse(Comprehension response) {
                        Message msg = mHandler.obtainMessage();
                        msg.what = 1;
                        msg.obj = response;
                        msg.sendToTarget();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }));

    }

    //更新进度
    private void updateSeekBar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mMediaPlayer != null && !mNeedToFetchFromInternet) { //结束线程标示

                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        try {
                            Message message = mHandler.obtainMessage();
                            message.what = mMediaPlayer.getCurrentPosition();
                            message.sendToTarget();
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mPlayerControl.setSelected(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mPlayerControl.setSelected(true);
        //设置最大进度
        mSeekBar.setMax(mMediaPlayer.getDuration());
        mMediaPlayer.start();
        updateSeekBar();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        mSeekBar.setSecondaryProgress(percent * mSeekBar.getMax() / 100);
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mMediaPlayer.seekTo(seekBar.getProgress());
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mMediaPlayer.pause();
        Message msg = mHandler.obtainMessage();
        msg.what = 2;
        msg.sendToTarget();
        mPlayerControl.setSelected(false);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mPlayerControl.setSelected(false);
        mNeedToFetchFromInternet = true;
        Message msg = mHandler.obtainMessage();
        msg.what = 2;
        msg.sendToTarget();
        return true;
    }
}
