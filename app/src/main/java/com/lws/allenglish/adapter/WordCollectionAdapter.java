package com.lws.allenglish.adapter;

import android.support.v7.widget.CardView;
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

/**
 * Created by Wilson on 2016/12/16.
 */

public class WordCollectionAdapter extends RecyclerView.Adapter<WordCollectionAdapter.ViewHolder> {
    private List<BaseWord> list;
    private OnItemClickListener mOnItemClickListener;

    public WordCollectionAdapter(List<BaseWord> list) {
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_word_collection, parent, false);
        return new WordCollectionAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.word.setText(list.get(position).word);
        holder.means.setText(list.get(position).means);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getLayoutPosition();
                mOnItemClickListener.onItemClick(holder.itemView, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.word)
        TextView word;
        @BindView(R.id.means)
        TextView means;
        @BindView(R.id.card_view)
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
