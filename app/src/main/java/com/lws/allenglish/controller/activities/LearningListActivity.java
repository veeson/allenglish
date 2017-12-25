package com.lws.allenglish.controller.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lws.allenglish.Constants;
import com.lws.allenglish.R;
import com.lws.allenglish.adapter.ViewPagerAdapter;
import com.lws.allenglish.base.BaseActivity;
import com.lws.allenglish.controller.fragments.LearningListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LearningListActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.frame_layout)
    FrameLayout frameLayout;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.linear_layout)
    LinearLayout linearLayout;

    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_list);
        ButterKnife.bind(this);

        init();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initView();
    }

    private void init() {
        setToolbar(toolbar);
        getFlag();
    }

    private void getFlag() {
        flag = getIntent().getIntExtra("flag", 0);
    }

    private void initView() {
        switch (flag) {
            case 0:
                toolbar.setTitle("双语阅读");
                handleBilingualReadingFragment(Constants.BILINGUAL_READING_TAGS, Constants.READER, true);
                break;
            case 1:
                toolbar.setTitle("VOA英语");
                handleBilingualReadingFragment(Constants.VOA_ENGLISH_TAGS, Constants.VOAENGLISH, false);
                break;
            case 2:
                toolbar.setTitle("看视频学英语");
                handleVideoFragment();
                break;
        }
    }

    private void handleVideoFragment() {
        linearLayout.setVisibility(View.GONE);
        frameLayout.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, LearningListFragment.newInstance(Constants.Video, null, true)).commit();
    }

    private void handleBilingualReadingFragment(String[] tags, String tableName, boolean isShowPicture) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        for (String tag :
                tags) {
            adapter.addFragment(LearningListFragment.newInstance(tableName, tag, isShowPicture), tag);
        }
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        if (flag == 0) {
            tabLayout.setTabMode(TabLayout.GRAVITY_FILL);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }
    }
}
