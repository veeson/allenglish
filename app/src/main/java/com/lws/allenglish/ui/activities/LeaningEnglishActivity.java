package com.lws.allenglish.ui.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lws.allenglish.R;
import com.lws.allenglish.adapter.ViewPagerAdapter;
import com.lws.allenglish.ui.fragments.ReaderFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LeaningEnglishActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprehensive_english);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.md_nav_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setupViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(ReaderFragment.newInstance(0), "双语阅读");
        adapter.addFragment(ReaderFragment.newInstance(1), "VOA学习");
        adapter.addFragment(ReaderFragment.newInstance(2), "英语视频");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        mTabLayout.setupWithViewPager(viewPager);
    }
}
