package com.lws.allenglish.ui.activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lws.allenglish.AppConstants;
import com.lws.allenglish.R;
import com.lws.allenglish.adapter.ReaderAdapter;
import com.lws.allenglish.base.BaseApplication;
import com.lws.allenglish.bean.VOA;
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

public class VideoActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener
        , SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener {
    //mSurfaceView
    @BindView(R.id.surface_view)
    SurfaceView mSurfaceView;
    //控制台layout
    @BindView(R.id.control_layout)
    LinearLayout mControlLayout;
    //播放、全屏button
    @BindView(R.id.control_play)
    ImageButton mControlPlay;
    @BindView(R.id.control_screen)
    ImageButton mControlScreen;
    //进度条
    @BindView(R.id.video_seek_bar)
    SeekBar mSeekBar;
    //加载视频进度progressBar
    @BindView(R.id.load_video)
    ProgressBar mProgressBar;
    //当前时间，总时间
    @BindView(R.id.current_time)
    TextView mCurrentTime;
    @BindView(R.id.count_time)
    TextView mCountTime;

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

    //surface holder
    private SurfaceHolder mSurfaceHolder;
    //媒体控制 mMediaPlayer
    private MediaPlayer mMediaPlayer;
    // 是否需要从网络上获取视频流
    private boolean mNeedToFetchFromInternet = true;
    // 是否是暂停状态
//    private boolean mIsPause = false;
    //是否全屏
    private boolean mFullScreen = false;
    //媒体音量管理
    private AudioManager mAudioManager;
    //点击纵坐标
    private float dY = 0;
    //点击横坐标
    private float dX = 0;
    //抬起纵坐标
    private float uY = 0;
    //抬起横坐标
    private float uX = 0;
    //屏幕当前亮度
    private float f = 0;

    private static final int HIDE_CONTROL_LAYOUT = -1;

    private Context mContext;
    private ReaderAdapter mAdapter;
    private VOA.ResultsEntity mResultsEntity;
    private String mVideoUrl;
    private List<VOA.ResultsEntity> mList = new ArrayList<>();

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == HIDE_CONTROL_LAYOUT) {
                refreshControlLayout(false);
            } else if (msg.what == 1) {
                VOA voa = (VOA) msg.obj;
                if (!mList.isEmpty()) {
                    mList.clear();
                }
                mList.addAll(voa.results);
                mAdapter.notifyDataSetChanged();
            } else if (msg.what == 2) {
                mSeekBar.setProgress(0);
            } else if (!mNeedToFetchFromInternet) {
                mCurrentTime.setText(formatTime(msg.what));
                mSeekBar.setProgress(msg.what);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        mContext = BaseApplication.getInstance();
        mResultsEntity = (VOA.ResultsEntity) getIntent().getSerializableExtra(AppConstants.BASE_ENGLISH);
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/MILT_RG.ttf");
        mNewsTitle.setTypeface(type);
        mNewsContent.setTypeface(type);
        mNewsTitle.setTextIsSelectable(true); // 设置文本可选
        mNewsContent.setTextIsSelectable(true); // 设置文本可选
        setContent();

        initView();
        initVideoSize();
        initSurface();
        setListener();
        initRecyclerView();

        fetchVOAFromInternet(mResultsEntity.postTime.iso, mResultsEntity.tag);
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
        mAdapter = new ReaderAdapter(mContext, mList, 2, false);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                synchronized (this) {
                    mResultsEntity = mList.get(position);
                    setContent();
                    mNeedToFetchFromInternet = true;
                    mProgressBar.setVisibility(View.GONE);
                    mCurrentTime.setText(R.string.oo_oo);
                    mCountTime.setText(R.string.oo_oo);
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                    }
                    Message msg = mHandler.obtainMessage();
                    msg.what = 2;
                    msg.sendToTarget();
                    mControlPlay.setBackgroundResource(R.drawable.ic_action_play_white_big);
                    refreshControlLayout(true);
                    mNestedScrollView.scrollTo(0, 0);
                }
                fetchVOAFromInternet(mResultsEntity.postTime.iso, mResultsEntity.tag);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setContent() {
        mVideoUrl = mResultsEntity.audioUrl;
        mNewsTitle.setText(mResultsEntity.newsTitle);
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.CHINA);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            mSourceAndDate.setText(new StringBuilder().append("来源：").append(mResultsEntity.source).append("   ").append(TimeUtils.DATE_FORMAT_DATE.format(dateFormat.parse(mResultsEntity.postTime.iso))));
        } catch (ParseException ignored) {
        }
        mNewsContent.setText(Html.fromHtml(mResultsEntity.newsContent).toString().replace("\n\n\n", "\n\n"));
    }

    private void fetchVOAFromInternet(String date, final String tag) {
        Map<String, String> headers = new HashMap<>();
        headers.put(AppConstants.CONTENT_TYPE, AppConstants.CONTENT_TYPE_VALUE);
        headers.put(AppConstants.X_LC_Id, AppConstants.X_LC_ID_VALUE);
        headers.put(AppConstants.X_LC_Key, AppConstants.X_LC_KEY_VALUE);
        String url = "https://leancloud.cn:443/1.1/classes/Video?where={\"postTime\":{\"$lt\":{\"__type\":\"Date\",\"iso\":\"" + date + "\"}},\"tag\":\"" + StringUtils.encodeText(tag) + "\",}}&limit=5&&order=-createdAt";
        VolleySingleton.getInstance(mContext)
                .addToRequestQueue(new GsonRequest<>(url, VOA.class, headers, new Response.Listener<VOA>() {
                    @Override
                    public void onResponse(VOA response) {
                        Message msg = mHandler.obtainMessage();
                        msg.what = 1;
                        msg.obj = response;
                        msg.sendToTarget();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(VideoActivity.this, R.string.error_loading, Toast.LENGTH_SHORT).show();
                    }
                }));

    }

    private void initView() {
        mCurrentTime.setText(R.string.oo_oo);
        mCountTime.setText(R.string.oo_oo);
        mSurfaceHolder = mSurfaceView.getHolder();
        mMediaPlayer = new MediaPlayer();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initScreenLight();
    }

    //初始化屏幕亮度
    private void initScreenLight() {
//        try {
        //获取亮度模式 0：手动 1：自动
//            int countLight = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
        //设置手动设置
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        //获取屏幕亮度,获取失败则返回255
        int currLight = Settings.System.getInt(getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS,
                255);
        f = currLight / 255f;
//        } catch (Settings.SettingNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    //刷新控制台 显示则隐藏 隐藏则显示 并5S之后隐藏
    private void refreshControlLayout(boolean always) {
        if (!always && mControlPlay.getVisibility() == View.VISIBLE) {
            mControlLayout.setVisibility(View.INVISIBLE);
            mControlPlay.setVisibility(View.GONE);
        } else {
            mControlLayout.setVisibility(View.VISIBLE);
            mControlPlay.setVisibility(View.VISIBLE);
            mHandler.removeMessages(HIDE_CONTROL_LAYOUT);
            if (!always) {
                mHandler.sendEmptyMessageDelayed(HIDE_CONTROL_LAYOUT, 5000);
            }
        }
    }

    private void setListener() {
        mControlPlay.setOnClickListener(this);
        mControlScreen.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = event.getX();
                        dY = event.getY();
                        refreshControlLayout(false);
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mFullScreen) {
                            uY = event.getY();
                            if (dX > getWidth() / 2) {//声音控制
                                if (Math.abs(uY - dY) > 25)
                                    setVolume(uY - dY);
                            } else if (dX <= getWidth() / 2) {//亮度控制
                                setLight(dY - uY);
                            }
                        }
                        break;
                }
                return true;
            }
        });

    }

    //手势调节音量
    private void setVolume(float vol) {
        if (vol < 0) {//增大音量
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
                    AudioManager.FX_FOCUS_NAVIGATION_UP);
        } else if (vol > 0) {//降低音量
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,
                    AudioManager.FX_FOCUS_NAVIGATION_UP);
        }
    }

    /**
     * 手势设置屏幕亮度
     * 设置当前的屏幕亮度值，及时生效 0.004-1
     * 该方法仅对当前应用屏幕亮度生效
     */
    private void setLight(float vol) {
        Window localWindow = getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
        f += vol / getWidth();
        if (f > 1) {
            f = 1f;
        } else if (f <= 0) {
            f = 0.004f;
        }
        localLayoutParams.screenBrightness = f;
        localWindow.setAttributes(localLayoutParams);
    }

    //初始化surfaceView
    private void initSurface() {
        //设置回调参数
        mSurfaceHolder.addCallback(this);
        //设置SurfaceView自己不管理的缓冲区
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //显示的分辨率,不设置为视频默认
//        mSurfaceHolder.setFixedSize(320, 220);
    }


    private void fetchVideoFromInternet(String url) {
        try {
            //使mediaPlayer重新进入ide状态
            mMediaPlayer.reset();
            //设置媒体类型
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //将影像输出到surfaceView
            mMediaPlayer.setDisplay(mSurfaceHolder);
            //设置 视频资源 可以是本地视频 也可是网络资源
//            mMediaPlayer.setDataSource("/storage/sdcard1/DCIM/Camera/VID_20160629_164144.mp4");
            mMediaPlayer.setDataSource(url);
            //同步准备
//            mMediaPlayer.prepare();
            //因为是网络视频 这里用异步准备
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //初始化视频显示的大小
    private void initVideoSize() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = getWidth() / 10 * 6;
        mSurfaceView.setLayoutParams(params);
    }

    //surfaceView创建完成
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mMediaPlayer.setDisplay(holder);
    }

    //surfaceView改变
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    //surfaceView销毁
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void onClick(View v) {
        refreshControlLayout(false);
        switch (v.getId()) {
            case R.id.control_play:
                if (mNeedToFetchFromInternet) {
                    if (NetWorkUtils.getNetworkTypeName(mContext).equals(NetWorkUtils.NETWORK_TYPE_DISCONNECT)) {
                        Toast.makeText(this, R.string.bad_internet, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!NetWorkUtils.getNetworkTypeName(mContext).equals(NetWorkUtils.NETWORK_TYPE_WIFI)) {
                        new MaterialDialog.Builder(this)
                                .content("当前非WIFI环境，将消耗移动流量，是否继续播放？")
                                .positiveText("确定")
                                .negativeText("取消")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                        mNeedToFetchFromInternet = false;
                                        fetchVideoFromInternet(mVideoUrl);
                                        mControlPlay.setVisibility(View.GONE);
                                        mProgressBar.setVisibility(View.VISIBLE);
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .negativeColor(getResources().getColor(android.R.color.holo_red_dark))
                                .positiveColor(getResources().getColor(android.R.color.holo_red_dark))
                                .contentColor(getResources().getColor(android.R.color.darker_gray))
                                .backgroundColor(getResources().getColor(android.R.color.white))
                                .show();
                    } else {
//                        mNeedToFetchFromInternet = false;
                        fetchVideoFromInternet(mVideoUrl);
                        mControlPlay.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    return;
                }
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    mControlPlay.setBackgroundResource(R.drawable.ic_action_play_white_big);
                    refreshControlLayout(true);
                } else {
                    mMediaPlayer.start();
                    mControlPlay.setBackgroundResource(R.drawable.ic_action_pause_white_big);
                    refreshControlLayout(false);
                }
                break;
            case R.id.control_screen:
                if (mFullScreen) {
                    smallScreen();
                    mControlScreen.setBackgroundResource(R.drawable.ic_action_fullscreen);
                } else {
                    fullScreen();
                    mControlScreen.setBackgroundResource(R.drawable.ic_action_fullscreen_exit);
                }
                break;
        }
    }

    //横竖屏切换
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mFullScreen = true;
            fullScreen();
            mControlScreen.setBackgroundResource(R.drawable.ic_action_fullscreen_exit);
        } else {
            mFullScreen = false;
            smallScreen();
            mControlScreen.setBackgroundResource(R.drawable.ic_action_fullscreen);
        }
        super.onConfigurationChanged(newConfig);
    }

    //全屏
    private void fullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        showFullSurface();
    }

    //竖屏
    private void smallScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
        showSmallSurface();
    }

    private void showFullSurface() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSurfaceView.setLayoutParams(params);
    }

    private void showSmallSurface() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = getWidth() / 10 * 6;
        mSurfaceView.setLayoutParams(params);
    }

    //进度改变
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mCurrentTime.setText(formatTime(seekBar.getProgress()));
    }

    //开始拖动
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mCurrentTime.setText(formatTime(seekBar.getProgress()));
    }

    //停止拖动
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        mMediaPlayer.seekTo(progress);
        mCurrentTime.setText(formatTime(progress));
    }

    public int getWidth() {
        WindowManager manager = getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    // 更新进度
    private void updateSeekBar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (mMediaPlayer != null && !mNeedToFetchFromInternet) { //结束线程标示

                        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                            Message message = new Message();
                            message.what = mMediaPlayer.getCurrentPosition();
                            mHandler.sendMessage(message);
                            Thread.sleep(1000);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //播放完成
    @Override
    public void onCompletion(MediaPlayer mp) {
        mControlPlay.setBackgroundResource(R.drawable.ic_action_play_white_big);
        mMediaPlayer.pause();
        refreshControlLayout(true);
        Message msg = mHandler.obtainMessage();
        msg.what = 2;
        msg.sendToTarget();
    }

    //播放出错
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mCurrentTime.setText(R.string.oo_oo);
        mCurrentTime.setText(R.string.oo_oo);
        mProgressBar.setVisibility(View.GONE);
        mNeedToFetchFromInternet = true;
        Message msg = mHandler.obtainMessage();
        msg.what = 2;
        msg.sendToTarget();
        refreshControlLayout(true);
        mControlPlay.setVisibility(View.VISIBLE);
        mControlPlay.setBackgroundResource(R.drawable.ic_action_play_white_big);
        return true;
    }

    private String formatTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(time);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        mSeekBar.setSecondaryProgress(percent * mSeekBar.getMax() / 100);
    }

    //准备完成
    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mMediaPlayer == null) {
            return;
        }
        mNeedToFetchFromInternet = false;
        //设置最大进度
        mSeekBar.setMax(mMediaPlayer.getDuration());
        //设置按钮背景图片
        mControlPlay.setBackgroundResource(R.drawable.ic_action_pause_white_big);
        //设置视频最大时间
        mCountTime.setText(formatTime(mMediaPlayer.getDuration()));
        //隐藏加载进度条
        mProgressBar.setVisibility(View.GONE);
        //开始播放
        mMediaPlayer.start();
        //更改状态
        //开启线程更新进度
        updateSeekBar();
    }

    //seekTo()是异步的方法 在此监听是否执行完毕
    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    //监听返回键 如果是全屏状态则返回竖屏 否则直接返回
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mFullScreen) {
            smallScreen();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mControlPlay.setBackgroundResource(R.drawable.ic_action_play_white_big);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshControlLayout(true);
    }
}
