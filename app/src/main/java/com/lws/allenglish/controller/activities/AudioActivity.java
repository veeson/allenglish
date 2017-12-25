package com.lws.allenglish.controller.activities;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lws.allenglish.R;
import com.lws.allenglish.base.BaseLearningActivity;
import com.lws.allenglish.util.CommonUtils;
import com.lws.allenglish.util.common.NetWorkUtils;
import com.lws.allenglish.util.common.ToastUtils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AudioActivity extends BaseLearningActivity implements MediaPlayer.OnPreparedListener
        , MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener
        , MediaPlayer.OnSeekCompleteListener, SeekBar.OnSeekBarChangeListener
        , MediaPlayer.OnErrorListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.player_control)
    ImageView playerControl;
    @BindView(R.id.audio_seek_bar)
    SeekBar seekBar;
    @BindView(R.id.position)
    TextView position;

    private boolean isPrepared = false;
    private Timer mUpdateProgressTimer;
    private TimerTask mUpdateProgressTimerTask;
    private MediaPlayer mMediaPlayer;
    // 是否需要从网络上获取视频流
    private boolean mNeedToFetchFromInternet = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        ButterKnife.bind(this);

        setToolbar(toolbar);
        init();

        playerControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNeedToFetchFromInternet) {
                    if (NetWorkUtils.getNetworkTypeName(mContext).equals(NetWorkUtils.NETWORK_TYPE_DISCONNECT)) {
                        ToastUtils.show(mContext, R.string.bad_internet);
                        return;
                    }
                    mNeedToFetchFromInternet = false;
                    playerControl.setSelected(true);
                    fetchVOAAudioFromInternet(leanCloudBean.mediaUrl);
                    return;
                }
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    playerControl.setSelected(false);
                } else {
                    mMediaPlayer.start();
                    playerControl.setSelected(true);
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(this);

        initMediaPlayer();
    }

    private void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
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

    /**
     * 开启更新进度的计时器。
     */
    protected void startUpdateProgressTimer() {
        cancelUpdateProgressTimer();
        if (mUpdateProgressTimer == null) {
            mUpdateProgressTimer = new Timer();
        }
        if (mUpdateProgressTimerTask == null) {
            mUpdateProgressTimerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateProgress();
                        }
                    });
                }
            };
        }
        mUpdateProgressTimer.schedule(mUpdateProgressTimerTask, 0, 300);
    }

    private void updateProgress() {
        if (!mNeedToFetchFromInternet && mMediaPlayer != null) {
            int currentPosition = mMediaPlayer.getCurrentPosition();
            seekBar.setProgress(currentPosition);
            position.setText(CommonUtils.formatTime(currentPosition));
        }
    }

    /**
     * 取消更新进度的计时器。
     */
    protected void cancelUpdateProgressTimer() {
        if (mUpdateProgressTimer != null) {
            mUpdateProgressTimer.cancel();
            mUpdateProgressTimer = null;
        }
        if (mUpdateProgressTimerTask != null) {
            mUpdateProgressTimerTask.cancel();
            mUpdateProgressTimerTask = null;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isPrepared = true;
        playerControl.setSelected(true);
        //设置最大进度
        seekBar.setMax(mMediaPlayer.getDuration());
        mMediaPlayer.start();
        startUpdateProgressTimer();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBar.setSecondaryProgress(percent * seekBar.getMax() / 100);
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
        if (isPrepared) {
            mMediaPlayer.seekTo(seekBar.getProgress());
        } else {
            seekBar.setProgress(0);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mMediaPlayer.seekTo(0);
        mMediaPlayer.pause();
        seekBar.setProgress(0);
        playerControl.setSelected(false);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        cancelUpdateProgressTimer();
        isPrepared = false;
        playerControl.setSelected(false);
        mNeedToFetchFromInternet = true;
        seekBar.setProgress(0);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            playerControl.setSelected(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelUpdateProgressTimer();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

}
