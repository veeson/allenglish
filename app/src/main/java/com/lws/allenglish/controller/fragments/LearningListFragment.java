package com.lws.allenglish.controller.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.IFLYNativeListener;
import com.iflytek.voiceads.NativeADDataRef;
import com.lws.allenglish.R;
import com.lws.allenglish.adapter.LeanCloudBeanAdapter;
import com.lws.allenglish.base.BaseApplication;
import com.lws.allenglish.base.LazyLoadFragment;
import com.lws.allenglish.bean.LeanCloudApiBean;
import com.lws.allenglish.connector.OnItemClickListener;
import com.lws.allenglish.controller.activities.AudioActivity;
import com.lws.allenglish.controller.activities.ReaderActivity;
import com.lws.allenglish.controller.activities.VideoActivity;
import com.lws.allenglish.model.LeanCloudModel;
import com.lws.allenglish.model.OnLeanCloudListener;
import com.lws.allenglish.model.impl.LeanCloudModelImpl;
import com.lws.allenglish.util.common.NetWorkUtils;
import com.lws.allenglish.util.common.ToastUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LearningListFragment extends LazyLoadFragment implements OnLeanCloudListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private LeanCloudModel model;
    private LeanCloudBeanAdapter adapter;
    private List<Object> mList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    // 表示最后一个可视条目
    private int lastVisibleItem;
    // 数据刷新方式，1表示初次进入fragment更新,2表示下拉刷新,3表示上拉刷新
    private int mRefreshDataMode;
    // true表示显示图片，false表示不显示图片
    private boolean isShowPicture;
    private String bilingualReadingTag;
    private String tableName;

    /*voiceads*/
    // 记录某position是否请求过广告，防止重复请求
    public SparseBooleanArray requested = new SparseBooleanArray();
    // 利用queue记录发送的广告请求
    // loadAd()中enqueue，onADLoaded()和onAdFailed()中dequeue并处理
    public Queue<IFLYAd> iflyAds = new LinkedList<>();

    private IFLYNativeAd nativeAd;

    private IFLYNativeListener listener = new IFLYNativeListener() {
        @Override
        public void onADLoaded(List<NativeADDataRef> list) {
            if (list.size() > 0) {
                final IFLYAd iflyAd = iflyAds.remove();
                iflyAd.aditem = list.get(0);
                // 添加
                mList.add(iflyAd.position, iflyAd);
                // 更新
                adapter.notifyDataSetChanged();
                // listview刷新完毕检查一次曝光
                recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                               int oldRight, int oldBottom) {
                        checkExposure(iflyAd.position);
                    }
                });

            }
        }

        @Override
        public void onAdFailed(AdError adError) {
            // 获取广告失败，remove请求并将已请求标记重新设为false
            requested.put(iflyAds.remove().position, false);
        }

        @Override
        public void onConfirm() {

        }

        @Override
        public void onCancel() {

        }
    };

    // 开发者维护讯飞广告类
    public class IFLYAd {
        public NativeADDataRef aditem;
        public boolean isExposured = false;
        public int position;
        public View adContainer;

        public IFLYAd(int position) {
            this.position = position;
            nativeAd.loadAd(1);
        }
    }

    public static LearningListFragment newInstance(String tableName, String tag, boolean isShowPicture) {
        LearningListFragment fragment = new LearningListFragment();
        Bundle args = new Bundle();
        args.putString("TABLE_NAME", tableName);
        args.putString("BILINGUAL_READING_TAG", tag);
        args.putBoolean("SHOW_PICTURE", isShowPicture);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tableName = getArguments().getString("TABLE_NAME");
        bilingualReadingTag = getArguments().getString("BILINGUAL_READING_TAG");
        isShowPicture = getArguments().getBoolean("SHOW_PICTURE");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learning_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (bilingualReadingTag == null) {
            setUserVisibleHint(true);
        }
        init();
        return view;
    }

    protected void init() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.app_color));
        model = new LeanCloudModelImpl();
        initRecyclerView();
        mIsPrepared = true;
        lazyLoad();
        initNativeAd();
    }

    private void initRecyclerView() {
        adapter = new LeanCloudBeanAdapter(this, mList, true, isShowPicture);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final LeanCloudApiBean.ResultsEntity leanCloudBean = (LeanCloudApiBean.ResultsEntity) mList.get(position);
                if (leanCloudBean.type == 0) {
                    if (leanCloudBean.mediaUrl != null) {
                        if (leanCloudBean.mediaUrl.endsWith(".mp3")) {
                            start(leanCloudBean, AudioActivity.class);
                        } else {
                            start(leanCloudBean, VideoActivity.class);
                        }
                    } else {
                        start(leanCloudBean, ReaderActivity.class);
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 停止滑动
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {
                    adapter.changeMoreStatus(LeanCloudBeanAdapter.LOADING_MORE);
                    mRefreshDataMode = 2;
                    model.getLeanCloudBean(tableName, 8, mList.size(), bilingualReadingTag, LearningListFragment.this);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                // 若firstVisibleItem和lastvisibleItem是广告位置，则检查曝光
                if (adapter.isAdPosition(firstVisibleItem))
                    checkExposure(firstVisibleItem);
                if (adapter.isAdPosition(lastVisibleItem))
                    checkExposure(lastVisibleItem);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    // 检查曝光
    public void checkExposure(int lastvisibleItem) {
        if (lastvisibleItem > mList.size() - 1 || lastvisibleItem < 0) {
            return;
        }
        if (mList.get(lastvisibleItem) instanceof IFLYAd) {
            IFLYAd curAd = (IFLYAd) mList.get(lastvisibleItem);
            if (!curAd.isExposured && curAd.adContainer != null) {
                curAd.isExposured = curAd.aditem.onExposured(curAd.adContainer);
            }
        }
    }

    private void start(LeanCloudApiBean.ResultsEntity bean, Class cls) {
        Intent intent = new Intent(mContext, cls);
        intent.putExtra("BEAN", bean);
        intent.putExtra("TABLE_NAME", tableName);
        intent.putExtra("SHOW_PICTURE", isShowPicture);
        intent.putExtra("BILINGUAL_READING_TAG", bilingualReadingTag);
        startActivity(intent);
    }

    public void initNativeAd() {
        if (nativeAd == null) {
            nativeAd = new IFLYNativeAd(mContext, "B19B87C53A1032812B1598F976D7CE2F", listener);
        }
        nativeAd.setParameter(AdKeys.DEBUG_MODE, "true");
    }

    @Override
    protected void lazyLoad() {
        if (!mIsPrepared || !mIsVisible || mHasLoadedOnce) {
            return;
        }
        if (!NetWorkUtils.getNetworkTypeName(BaseApplication.getInstance()).equals(NetWorkUtils.NETWORK_TYPE_DISCONNECT)) {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(true);
            }
            model.getLeanCloudBean(tableName, 8, 0, bilingualReadingTag, this);
        } else {
            ToastUtils.show(mContext, R.string.bad_internet);
        }
    }

    @Override
    public void onSuccess(List<LeanCloudApiBean.ResultsEntity> list) {
        mHasLoadedOnce = true;
        switch (mRefreshDataMode) {
            case 0:
                mList.addAll(list);
                adapter.notifyDataSetChanged();
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                break;
            case 1:
                if (!mList.isEmpty()) {
                    mList.clear();
                }
                mList.addAll(list);
                adapter.notifyDataSetChanged();
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                break;
            case 2:
                mList.addAll(list);
                adapter.changeMoreStatus(LeanCloudBeanAdapter.PULLUP_LOAD_MORE);
                break;
        }
    }

    @Override
    public void onError() {
        mHasLoadedOnce = true;
        ToastUtils.show(mContext, R.string.bad_internet);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        mRefreshDataMode = 1;
        model.getLeanCloudBean(tableName, 8, 0, bilingualReadingTag, this);
    }
}
