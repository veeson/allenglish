package com.lws.allenglish.ui.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lws.allenglish.R;
import com.lws.allenglish.adapter.ViewPagerAdapter;
import com.lws.allenglish.ui.fragments.ArticleCollectionFragment;
import com.lws.allenglish.ui.fragments.TranslationRecordFragment;
import com.lws.allenglish.ui.fragments.WordCollectionFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookmarkActivity extends AppCompatActivity {
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
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mAdapter.addFragment(new WordCollectionFragment(), "生词本");
        mAdapter.addFragment(new TranslationRecordFragment(), "翻译");
//        mAdapter.addFragment(new ArticleCollectionFragment(), "文章");
        viewPager.setAdapter(mAdapter);
//        viewPager.setOffscreenPageLimit(2);
        mTabLayout.setupWithViewPager(viewPager);
    }
}
