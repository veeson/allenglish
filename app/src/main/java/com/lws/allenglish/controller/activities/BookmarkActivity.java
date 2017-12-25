package com.lws.allenglish.controller.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.lws.allenglish.R;
import com.lws.allenglish.adapter.ViewPagerAdapter;
import com.lws.allenglish.base.BaseActivity;
import com.lws.allenglish.controller.fragments.TranslationRecordFragment;
import com.lws.allenglish.controller.fragments.WordCollectionFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookmarkActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    public ViewPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        ButterKnife.bind(this);
        setToolbar(mToolbar);
        setupViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mAdapter.addFragment(new WordCollectionFragment(), "生词本");
        mAdapter.addFragment(new TranslationRecordFragment(), "翻译");
//        mAdapter.addFragment(new ArticleCollectionFragment(), "文章");
        viewPager.setAdapter(mAdapter);
//        viewPager.setOffscreenPageLimit(2);
        mTabLayout.setupWithViewPager(viewPager);
    }
}
