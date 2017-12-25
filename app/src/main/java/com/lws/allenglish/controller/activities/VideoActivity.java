package com.lws.allenglish.controller.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.lws.allenglish.R;
import com.lws.allenglish.base.BaseLearningActivity;
import com.lws.allenglish.util.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

public class VideoActivity extends BaseLearningActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.jc_video_player_standard)
    JZVideoPlayerStandard jcVideoPlayerStandard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        init();
        initNiceVideoPlayer();
    }

    private void initNiceVideoPlayer() {
        jcVideoPlayerStandard.setUp(leanCloudBean.mediaUrl
                , JZVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, leanCloudBean.title);
        String imageUrl = leanCloudBean.imageUrl;
        if (imageUrl == null) {
            jcVideoPlayerStandard.thumbImageView.setImageResource(R.drawable.ic_default);
        } else {
            CommonUtils.setUrlImageToImageView(jcVideoPlayerStandard.thumbImageView, leanCloudBean.imageUrl);
        }
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }
}
