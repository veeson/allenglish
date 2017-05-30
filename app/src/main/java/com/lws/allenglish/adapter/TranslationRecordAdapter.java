package com.lws.allenglish.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lws.allenglish.R;
import com.lws.allenglish.bean.TranslationRecord;
import com.lws.allenglish.connector.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wilson on 2016/12/16.
 */

public class TranslationRecordAdapter extends RecyclerView.Adapter<TranslationRecordAdapter.ViewHolder> {
    private List<TranslationRecord> mList;
    private OnItemClickListener mOnItemClickListener;

    public TranslationRecordAdapter(List<TranslationRecord> mList) {
        this.mList = mList;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_translation_record, parent, false);
        return new TranslationRecordAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.originalText.setText(mList.get(position).text);
        holder.translationText.setText(mList.get(position).result);
        holder.dateAndSource.setText(new StringBuilder().append(mList.get(position).date).append(" (").append(mList.get(position).source).append(")"));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getLayoutPosition();
                mOnItemClickListener.onItemClick(holder.itemView, pos);
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int pos = holder.getLayoutPosition();
                mOnItemClickListener.onItemLongClick(holder.itemView, pos);
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.original_text)
        TextView originalText;
        @BindView(R.id.translation_text)
        TextView translationText;
        @BindView(R.id.date_and_source)
        TextView dateAndSource;
        @BindView(R.id.card_view)
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
