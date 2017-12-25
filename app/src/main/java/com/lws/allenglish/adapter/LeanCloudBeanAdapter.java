package com.lws.allenglish.adapter;

import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.lws.allenglish.R;
import com.lws.allenglish.base.BaseApplication;
import com.lws.allenglish.bean.LeanCloudApiBean;
import com.lws.allenglish.connector.OnItemClickListener;
import com.lws.allenglish.controller.fragments.LearningListFragment;
import com.lws.allenglish.util.VolleySingleton;
import com.lws.allenglish.util.common.TimeUtils;

import java.text.ParseException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LeanCloudBeanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //上拉加载更多
    public static final int PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int LOADING_MORE = 1;
    //上拉加载更多状态-默认为0
    private int load_more_status = 0;
    private static final int TYPE_ITEM = 0;  //普通Item View
    private static final int TYPE_FOOTER = 1;  //顶部FootView
    // false表示详情页，true表示列表
    private boolean isList = false;
    // true表示显示图片，false表示不显示图片
    private boolean isShowPicture;
    // 表示学英语页面的各个fragment
    private List<Object> mList;
    private ImageLoader mImageLoader;
    private OnItemClickListener mOnItemClickListener;

    private LearningListFragment fragment2;

    public LeanCloudBeanAdapter(LearningListFragment fragment2, List<Object> mList, boolean mFlag, boolean isShowPicture) {
        this.fragment2 = fragment2;
        this.mList = mList;
        this.isList = mFlag;
        this.isShowPicture = isShowPicture;
        mImageLoader = new ImageLoader(VolleySingleton.getInstance().getRequestQueue(), BaseApplication.getInstance().getBitmapCache());
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //进行判断显示类型，来创建返回不同的View
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_article_list, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View foot_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more_layout, parent, false);
            return new FootViewHolder(foot_view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            //若是广告位置，则请求广告
            if (isAdPosition(position)) {
                loadAd(position);
            }
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            final Object data = mList.get(position);
            if (data instanceof LearningListFragment.IFLYAd) {
                LearningListFragment.IFLYAd iflyAd = (LearningListFragment.IFLYAd) data;
                iflyAd.adContainer = itemViewHolder.cardView;
                itemViewHolder.title.setText(iflyAd.aditem.getTitle());
                itemViewHolder.source.setText("广告");
                if (isShowPicture) {
                    itemViewHolder.picture.setVisibility(View.VISIBLE);
                    itemViewHolder.picture.setDefaultImageResId(R.drawable.ic_default);
                    itemViewHolder.picture.setErrorImageResId(R.drawable.ic_default);
                    itemViewHolder.picture.setImageUrl(iflyAd.aditem.getImage(), mImageLoader);
                }
                itemViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((LearningListFragment.IFLYAd) data).aditem.onClicked(itemViewHolder.cardView);
                    }
                });
            } else {
                LeanCloudApiBean.ResultsEntity leanCloudBean = (LeanCloudApiBean.ResultsEntity) data;
                itemViewHolder.title.setText(leanCloudBean.title);
                try {
                    itemViewHolder.source.setText(leanCloudBean.source + "   " + TimeUtils.DATE_FORMAT_DATE.format(TimeUtils.FULL_DATE_FORMAT_DATE.parse(leanCloudBean.postTime.iso)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (isShowPicture) {
                    if (leanCloudBean.imageUrl.isEmpty()) {
                        itemViewHolder.picture.setVisibility(View.GONE);
                    } else {
                        itemViewHolder.picture.setVisibility(View.VISIBLE);
                        itemViewHolder.picture.setDefaultImageResId(R.drawable.ic_default);
                        itemViewHolder.picture.setErrorImageResId(R.drawable.ic_default);
                        itemViewHolder.picture.setImageUrl(leanCloudBean.imageUrl, mImageLoader);
                    }
                }
                itemViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickListener.onItemClick(view, pos);
                    }
                });
            }
        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            footViewHolder.progress.getIndeterminateDrawable().setColorFilter(fragment2.getResources().getColor(R.color.app_color), PorterDuff.Mode.MULTIPLY);
            switch (load_more_status) {
                case PULLUP_LOAD_MORE:
                    footViewHolder.loadingTip.setText("上拉加载更多...");
                    footViewHolder.progress.setVisibility(View.VISIBLE);
                    break;
                case LOADING_MORE:
                    footViewHolder.loadingTip.setText("正在加载更多数据...");
                    footViewHolder.progress.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mList.isEmpty()) {
            return 0;
        }
        if (!isList) {
            return mList.size();
        }
        return mList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (!isList || mList.isEmpty() || position + 1 != getItemCount()) {
            return TYPE_ITEM;
        } else {
            return TYPE_FOOTER;
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.source)
        TextView source;
        @BindView(R.id.picture)
        NetworkImageView picture;
        @BindView(R.id.card_view)
        CardView cardView;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (!isShowPicture) {
                picture.setVisibility(View.GONE);
            }
        }
    }

    static class FootViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.progress)
        ProgressBar progress;
        @BindView(R.id.loading_tip)
        TextView loadingTip;

        FootViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * //上拉加载更多
     * PULLUP_LOAD_MORE=0;
     * //正在加载中
     * LOADING_MORE=1;
     * //加载完成已经没有更多数据了
     * NO_MORE_DATA=2;
     *
     * @param status 状态
     */
    public void changeMoreStatus(int status) {
        load_more_status = status;
        notifyDataSetChanged();
    }

    // 广告位置：9,18,27...
    public boolean isAdPosition(int position) {
        return position != 0 && position % 8 == 0;
    }

    // 加载广告
    private void loadAd(int position) {
        if (!fragment2.requested.get(position, false)) {
            fragment2.requested.put(position, true);
            fragment2.iflyAds.add(fragment2.new IFLYAd(position));
        }
    }
}
