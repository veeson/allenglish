package com.lws.allenglish.adapter;

import android.content.Context;
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
import com.lws.allenglish.bean.BaseEnglish;
import com.lws.allenglish.bean.VOA;
import com.lws.allenglish.connector.OnItemClickListener;
import com.lws.allenglish.utils.BitmapCache;
import com.lws.allenglish.utils.VolleySingleton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wilson on 2016/12/18.
 */

public class ReaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //上拉加载更多
    public static final int PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int LOADING_MORE = 1;
    //上拉加载更多状态-默认为0
    private int load_more_status = 0;
    private static final int TYPE_ITEM = 0;  //普通Item View
    private static final int TYPE_FOOTER = 1;  //顶部FootView
    // 数据来自学英语页面还是文章详情页面的标志
    private boolean mFlag = false;
    // 表示学英语页面的各个fragment
    private int mPageNumber = 0;
    private Context mContext;
    private ImageLoader mImageLoader;
    private List<? extends BaseEnglish.ResultsEntity> mList;
    private OnItemClickListener mOnItemClickListener;

    public ReaderAdapter(Context mContext, List<? extends BaseEnglish.ResultsEntity> mList, int mPageNumber, boolean mFlag) {
        this.mContext = mContext;
        this.mList = mList;
        this.mPageNumber = mPageNumber;
        this.mFlag = mFlag;
        mImageLoader = new ImageLoader(VolleySingleton.getInstance().getRequestQueue(), new BitmapCache());
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
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            BaseEnglish.ResultsEntity entity = mList.get(position);
            itemViewHolder.title.setText(entity.newsTitle);
            StringBuilder sb = new StringBuilder();
            sb.append(entity.tag).append("   ");
            try {
                VOA.ResultsEntity entity2 = (VOA.ResultsEntity) mList.get(position);
                if (entity2.subtitleType.contains("中英字幕")) {
                    sb.append(entity2.subtitleType);
                }
            } catch (Exception ignored) {
            }
            itemViewHolder.sourceAndTag.setText(sb);
            ((ItemViewHolder) holder).imageView.setDefaultImageResId(0);
            ((ItemViewHolder) holder).imageView.setErrorImageResId(0);
            ((ItemViewHolder) holder).imageView.setImageUrl(entity.imageUrl, mImageLoader);
            itemViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            });
        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            footViewHolder.progress.getIndeterminateDrawable().setColorFilter(mContext.getResources().getColor(R.color.app_color), PorterDuff.Mode.MULTIPLY);
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
        if (!mFlag) {
            return mList.size();
        }
        return mList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (!mFlag || mList.isEmpty() || position + 1 != getItemCount()) {
            return TYPE_ITEM;
        } else {
            return TYPE_FOOTER;
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_view)
        CardView cardView;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.source_and_tag)
        TextView sourceAndTag;
        @BindView(R.id.picture)
        NetworkImageView imageView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (mPageNumber == 2) {
                imageView.setVisibility(View.GONE);
            }
        }
    }

    public static class FootViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.progress)
        ProgressBar progress;
        @BindView(R.id.loading_tip)
        TextView loadingTip;

        public FootViewHolder(View view) {
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
     * @param status
     */
    public void changeMoreStatus(int status) {
        load_more_status = status;
        notifyDataSetChanged();
    }
}
