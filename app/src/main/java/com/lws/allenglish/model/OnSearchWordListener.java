package com.lws.allenglish.model;

import com.lws.allenglish.bean.BaseWord;

import java.util.List;

public interface OnSearchWordListener {
    void onGetSearchWords(List<BaseWord> list);

    void onSetClearIconVisibility(int visibility);
}
