package com.lws.allenglish.controller.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.lws.allenglish.R;
import com.lws.allenglish.adapter.ViewPagerAdapter;
import com.lws.allenglish.base.BaseActivity;
import com.lws.allenglish.controller.fragments.DictionaryFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DictionaryActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);
        ButterKnife.bind(this);
        setToolbar(mToolbar);
        setupViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(DictionaryFragment.newInstance(0), "新华字典");
        adapter.addFragment(DictionaryFragment.newInstance(1), "成语词典");
        viewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(viewPager);
    }
}
