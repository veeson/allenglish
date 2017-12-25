package com.lws.allenglish.model;

import com.lws.allenglish.bean.BaseWord;
import com.lws.allenglish.bean.IcibaSentence;

public interface OnTabHomeModelListener {
    void onShowRandomWord(BaseWord baseWord);

    void onGetIcibaSentence(IcibaSentence sentence);
}
