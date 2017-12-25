package com.lws.allenglish.model;

import com.lws.allenglish.bean.DetailedWord;

public interface OnDetailedWordListener {
    void onSuccess(DetailedWord detailedWord);
    void onError();
    void onIsCollectedWord(boolean isCollected);
}
