package com.lws.allenglish.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lws.allenglish.AppConstants;
import com.lws.allenglish.R;
import com.lws.allenglish.adapter.WordDetailsAdapter;
import com.lws.allenglish.base.BaseApplication;
import com.lws.allenglish.bean.BaseWord;
import com.lws.allenglish.bean.DetailedWord;
import com.lws.allenglish.db.AllEnglishDatabaseManager;
import com.lws.allenglish.utils.GsonRequest;
import com.lws.allenglish.utils.NetWorkUtils;
import com.lws.allenglish.utils.PlayAudio;
import com.lws.allenglish.utils.StringUtils;
import com.lws.allenglish.utils.VolleySingleton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WordDetailsActivity extends AppCompatActivity{
    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.search)
    ImageView mSearch;
    @BindView(R.id.word)
    TextView mWord;
    @BindView(R.id.collection)
    ImageView mCollection;
    @BindView(R.id.ph_en)
    TextView mPhen;
    @BindView(R.id.ph_am)
    TextView mPham;
    @BindView(R.id.phen_horn)
    ImageView mPhenHorn;
    @BindView(R.id.pham_horn)
    ImageView mPhamHorn;
    @BindView(R.id.word_details)
    RecyclerView mRecyclerView;

    private AnimationDrawable mPhenHornAnimation;
    private AnimationDrawable mPhamHornAnimation;

    private PlayAudio mPlayAudio;
    private Context mContext;
    private WordDetailsAdapter mAdapter;
    private BaseWord mBaseWord;
    private DetailedWord mDetailedWord = new DetailedWord();
    private AllEnglishDatabaseManager mDatabaseManager;
    private List<AnimationDrawable> mSentenceHornAnimations = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    DetailedWord detailedWord = (DetailedWord) msg.obj;
                    mDetailedWord.baesInfo = detailedWord.baesInfo;
                    mDetailedWord.sentence = detailedWord.sentence;
                    mDetailedWord.phrase = detailedWord.phrase;
                    mDetailedWord.ee_mean = detailedWord.ee_mean;
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_details);
        ButterKnife.bind(this);
        mContext = BaseApplication.getInstance();
        mPlayAudio = new PlayAudio();
        mBaseWord = (BaseWord) getIntent().getSerializableExtra(AppConstants.BASE_INFO);
        mDetailedWord.baseWord = mBaseWord;
        mDatabaseManager = AllEnglishDatabaseManager.getInstance(mContext);
        if (mDatabaseManager.isCollectedWord(mBaseWord.word)) {
            mCollection.setSelected(true);
        }
        setUi();
        initRecyclerView();
        if (!NetWorkUtils.getNetworkTypeName(mContext).equals(NetWorkUtils.NETWORK_TYPE_DISCONNECT)) {
            fetchWordInfoFromInternet(mBaseWord.word);
        } else {
            Toast.makeText(this, R.string.bad_internet, Toast.LENGTH_SHORT).show();
        }
    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new WordDetailsAdapter(mDetailedWord, mContext, mPlayAudio, mSentenceHornAnimations);
        mRecyclerView.setAdapter(mAdapter); //  TODO: 还有嵌套recyclerview卡顿的问题待解决
    }

    private void setUi() {
        mWord.setText(mBaseWord.word);
        mPhen.setText(new StringBuilder().append("英 ").append(mBaseWord.ph_en));
        mPham.setText(new StringBuilder().append("美 ").append(mBaseWord.ph_am));
        mWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterPopup(v);
            }
        });
        mCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCollection.isSelected()) {
                    mCollection.setSelected(true);
                    mDatabaseManager.saveCollectedWord(mBaseWord);
                } else {
                    mCollection.setSelected(false);
                    mDatabaseManager.cancelCollectedWord(mBaseWord.word);
                }
            }
        });
        mPhenHorn.setBackgroundResource(R.drawable.animation_horn);
        mPhenHornAnimation = (AnimationDrawable) mPhenHorn.getBackground();
        mSentenceHornAnimations.add(mPhenHornAnimation);
        mPhenHorn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.stopAnimation();
                mPhenHornAnimation.start();
                mPlayAudio.play(mContext, mWord.getText().toString(), 1);
            }
        });
        mPhamHorn.setBackgroundResource(R.drawable.animation_horn);
        mPhamHornAnimation = (AnimationDrawable) mPhamHorn.getBackground();
        mSentenceHornAnimations.add(mPhamHornAnimation);
        mPhamHorn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.stopAnimation();
                mPhamHornAnimation.start();
                mPlayAudio.play(mContext, mWord.getText().toString(), 2);
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SearchWordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showFilterPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.popup_filter, popup.getMenu());
        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.copy:
                        StringUtils.copyToClipboard(mContext, mWord.getText().toString());
                        return true;
//                    case R.id.add_new_word:
//                        if (!mCollection.isSelected()) {
//                            mDatabaseManager.saveCollectedWord(mBaseWord);
//                            mCollection.setSelected(true);
//                        } else {
//                            Toast.makeText(WordDetailsActivity.this, "生词已存在", Toast.LENGTH_SHORT).show();
//                        }
//                        return true;
                    case R.id.open_bookmark:
                        Intent intent = new Intent(mContext, BookmarkActivity.class);
                        startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });
        // Handle dismissal with: popup.setOnDismissListener(...);
        // Show the menu
        popup.show();
    }

    private void fetchWordInfoFromInternet(String word) {
        VolleySingleton.getInstance(mContext).addToRequestQueue(new GsonRequest<>("http://www.iciba.com/index.php?a=getWordMean&c=search&list=1,4,8,9,12,13,14,15&word=" + word + "&_=1479203939913",
                DetailedWord.class,
                null,
                new Response.Listener<DetailedWord>() {
                    @Override
                    public void onResponse(DetailedWord response) {
                        Message msg = mHandler.obtainMessage();
                        msg.what = 1;
                        msg.obj = response;
                        msg.sendToTarget();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "onErrorResponse: " + error.toString());
                Toast.makeText(WordDetailsActivity.this, R.string.error_loading, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPlayAudio.stopMediaPlayer();
        mAdapter.stopAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayAudio.killMediaPlayer();
        mAdapter.stopAnimation();
    }
}
