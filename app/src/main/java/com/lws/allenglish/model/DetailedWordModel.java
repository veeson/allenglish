package com.lws.allenglish.model;

import com.lws.allenglish.bean.BaseWord;

public interface DetailedWordModel {
    void getDetailedWord(int type, String word);

    void setCollectedWord(String word);

    void saveCollectedWord(BaseWord baseWord);

    void cancelCollectedWord(String word);
}
