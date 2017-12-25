package com.lws.allenglish.model.impl;

import android.view.View;

import com.lws.allenglish.base.BaseApplication;
import com.lws.allenglish.bean.BaseWord;
import com.lws.allenglish.database.AllEnglishDatabaseManager;
import com.lws.allenglish.database.DictionaryDatabaseManager;
import com.lws.allenglish.model.OnSearchWordListener;
import com.lws.allenglish.model.SearchWordModel;

import java.util.Collections;
import java.util.List;

public class SearchWordModelImpl implements SearchWordModel {
    private OnSearchWordListener listener;

    public SearchWordModelImpl(OnSearchWordListener listener) {
        this.listener = listener;
    }

    @Override
    public void loadQueriedWords() {
        List<BaseWord> list = AllEnglishDatabaseManager.getInstance(BaseApplication.getInstance()).queryQueriedWords();
        listener.onGetSearchWords(list);
    }

    @Override
    public void saveQueriedWord(BaseWord baseWord) {
        if (!AllEnglishDatabaseManager.getInstance(BaseApplication.getInstance()).existSearchWordHistory(baseWord.word)) {
            AllEnglishDatabaseManager.getInstance(BaseApplication.getInstance()).saveQueriedWord(baseWord);
        }
    }

    @Override
    public void matchingWord(String keyWord) {
        if (keyWord.isEmpty()) {
            listener.onSetClearIconVisibility(View.INVISIBLE);
            List<BaseWord> list = AllEnglishDatabaseManager.getInstance(BaseApplication.getInstance()).queryQueriedWords();
            if (list.isEmpty()) {
                return;
            }
            Collections.reverse(list);
            listener.onGetSearchWords(list);
            return;
        } else {
            listener.onSetClearIconVisibility(View.VISIBLE);
        }
        if (keyWord.contains("%")) {
            ignoreKeyChar(keyWord, "%");
            return;
        }
        if (keyWord.contains("'")) {
            ignoreKeyChar(keyWord, "'");
            return;
        }
        List<BaseWord> list = DictionaryDatabaseManager.matchingWord(keyWord);
        if (!list.isEmpty()) {
            listener.onGetSearchWords(list);
        }
    }

    private void ignoreKeyChar(String keyWord, String c) {
        String subKeyWord = null;
        try {
            subKeyWord = keyWord.split(c)[0];
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        if (subKeyWord != null) {
            listener.onGetSearchWords(DictionaryDatabaseManager.matchingWord(subKeyWord));
        }
    }
}
