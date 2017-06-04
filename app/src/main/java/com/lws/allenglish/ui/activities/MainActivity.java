package com.lws.allenglish.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.lws.allenglish.R;
import com.lws.allenglish.adapter.TabAdapter;
import com.lws.allenglish.ui.fragments.TabLearningFragment;
import com.lws.allenglish.ui.fragments.TabLookupDictionaryFragment;
import com.lws.allenglish.ui.fragments.TabTranslationFragment;
import com.lws.allenglish.view.MainBottomTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
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
        adapter.addFragment(new TabLookupDictionaryFragment());
        adapter.addFragment(TabTranslationFragment.newInstance(""));
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
            case R.id.action_about:

                break;
            case R.id.action_praise:

                break;
        }
        return true;
    }

}
