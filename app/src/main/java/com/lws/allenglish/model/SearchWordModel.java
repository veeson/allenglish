package com.lws.allenglish.model;

import com.lws.allenglish.bean.BaseWord;

public interface SearchWordModel {
    void loadQueriedWords();

    void saveQueriedWord(BaseWord baseWord);

    void matchingWord(String keyWord);
}
