package com.lws.allenglish.controller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.lws.allenglish.R;
import com.lws.allenglish.base.BaseActivity;
import com.lws.allenglish.controller.fragments.TabHomeFragment;
import com.lws.allenglish.controller.fragments.TabLearningFragment;
import com.lws.allenglish.controller.fragments.TabTranslationFragment;
import com.lws.allenglish.view.MainBottomTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tab_viewpager)
    ViewPager mTabViewpager;
    @BindView(R.id.bottom_tab_layout)
    MainBottomTabLayout mBottomTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        setupViewpager(mTabViewpager);
    }

    private void setupViewpager(ViewPager viewPager) {
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new TabHomeFragment());
        adapter.addFragment(new TabTranslationFragment());
        adapter.addFragment(new TabLearningFragment());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        mBottomTabLayout.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(this, SearchWordActivity.class);
                startActivity(intent);
                break;
            case R.id.action_bookmark:
                Intent intent2 = new Intent(this, BookmarkActivity.class);
                startActivity(intent2);
                break;
//            case R.id.action_about:
//                Intent intent3 = new Intent(this, AboutActivity.class);
//                startActivity(intent3);
//                break;
        }
        return true;
    }

    private class TabAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public TabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }

}
