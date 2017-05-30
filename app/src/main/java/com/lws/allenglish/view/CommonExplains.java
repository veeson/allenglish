package com.lws.allenglish.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lws.allenglish.R;

/**
 * Created by Wilson on 2016/12/10.
 */

public class CommonExplains extends LinearLayout {
    private TextView explainHead;
    private LinearLayout baseInfoLinearLayout;
    private TextView symbols;
    private TextView exchange;
    private RecyclerView sentenceRecyclerView;

    public CommonExplains(Context context) {
        super(context);
    }

    public CommonExplains(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.common_explains_layout, this);
        explainHead = (TextView) view.findViewById(R.id.explain_head);
        baseInfoLinearLayout = (LinearLayout) view.findViewById(R.id.base_info);
        symbols = (TextView) view.findViewById(R.id.symbols);
        exchange = (TextView) view.findViewById(R.id.exchange);
        sentenceRecyclerView = (RecyclerView) view.findViewById(R.id.sentence);
    }

    public void setExplainHeadText(CharSequence text) {
        explainHead.setText(text);
    }

    public void setBaseInfoLinearLayoutVisibility(int visibility) {
        baseInfoLinearLayout.setVisibility(visibility);
    }

    public void setSymbolsText(CharSequence text) {
        symbols.setText(text);
    }

    public void setExchangeText(CharSequence text) {
        exchange.setText(text);
    }

    public void setExchangeVisibility(int visibility) {
        exchange.setVisibility(visibility);
    }

    public void setSentenceRecyclerViewVisibility(int visibility) {
        sentenceRecyclerView.setVisibility(visibility);
    }

    public TextView getExplainHead() {
        return explainHead;
    }

    public void setExplainHead(TextView explainHead) {
        this.explainHead = explainHead;
    }

    public LinearLayout getBaseInfoLinearLayout() {
        return baseInfoLinearLayout;
    }

    public void setBaseInfoLinearLayout(LinearLayout baseInfoLinearLayout) {
        this.baseInfoLinearLayout = baseInfoLinearLayout;
    }

    public TextView getSymbols() {
        return symbols;
    }

    public void setSymbols(TextView symbols) {
        this.symbols = symbols;
    }

    public TextView getExchange() {
        return exchange;
    }

    public void setExchange(TextView exchange) {
        this.exchange = exchange;
    }

    public RecyclerView getSentenceRecyclerView() {
        return sentenceRecyclerView;
    }

    public void setSentenceRecyclerView(RecyclerView sentenceRecyclerView) {
        this.sentenceRecyclerView = sentenceRecyclerView;
    }
}
