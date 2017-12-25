package com.lws.allenglish.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lws.allenglish.R;
import com.lws.allenglish.bean.BaseWord;
import com.lws.allenglish.connector.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseWordAdapter extends RecyclerView.Adapter<BaseWordAdapter.ViewHolder> {
    private List<BaseWord> mList;
    private OnItemClickListener mOnItemClickListener;

    public BaseWordAdapter(List<BaseWord> mList) {
        this.mList = mList;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public BaseWordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_word_search, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.word.setText(mList.get(position).word);
        holder.baseExplain.setText(mList.get(position).means.replace("\n", ""));
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getLayoutPosition();
                mOnItemClickListener.onItemClick(holder.itemView, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.voa_english)
        TextView word;
        @BindView(R.id.base_explain)
        TextView baseExplain;
        @BindView(R.id.item)
        LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
