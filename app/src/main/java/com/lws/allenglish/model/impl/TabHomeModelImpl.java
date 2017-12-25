package com.lws.allenglish.model.impl;

import android.text.TextUtils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.lws.allenglish.Constants;
import com.lws.allenglish.base.BaseApplication;
import com.lws.allenglish.bean.BaseWord;
import com.lws.allenglish.bean.IcibaSentence;
import com.lws.allenglish.database.DictionaryDatabaseManager;
import com.lws.allenglish.model.OnTabHomeModelListener;
import com.lws.allenglish.model.TabHomeModel;
import com.lws.allenglish.util.GsonRequest;
import com.lws.allenglish.util.VolleySingleton;
import com.lws.allenglish.util.common.PreferencesUtils;
import com.lws.allenglish.util.common.TimeUtils;

public class TabHomeModelImpl implements TabHomeModel {
    private OnTabHomeModelListener listener;

    public TabHomeModelImpl(OnTabHomeModelListener listener) {
        this.listener = listener;
    }

    @Override
    public void getRandomWord() {
        BaseWord baseWord = DictionaryDatabaseManager.randomWord();
        if (TextUtils.isEmpty(baseWord.word)) {
            baseWord = DictionaryDatabaseManager.randomWord();
        }
        listener.onShowRandomWord(baseWord);
    }

    @Override
    public void getIcibaSentence() {
        String historyJson = PreferencesUtils.getString(BaseApplication.getInstance(), "sentence");
        if (historyJson != null) {
            IcibaSentence icibaSentence = new Gson().fromJson(historyJson, IcibaSentence.class);
            if (icibaSentence.dateline.equals(TimeUtils.getCurrentTimeInString(TimeUtils.DATE_FORMAT_DATE))) {
                listener.onGetIcibaSentence(icibaSentence);
                return;
            }
        }
        String url = "http://open.iciba.com/dsapi/?date=" + TimeUtils.getCurrentTimeInString(TimeUtils.DATE_FORMAT_DATE);
        VolleySingleton.getInstance().addToRequestQueue(new GsonRequest<>(url, IcibaSentence.class, null, null, new Response.Listener<IcibaSentence>() {
            @Override
            public void onResponse(IcibaSentence response) {
                PreferencesUtils.putString(BaseApplication.getInstance(), new Gson().toJson(response), "");
                listener.onGetIcibaSentence(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String historyJson = PreferencesUtils.getString(BaseApplication.getInstance(), "sentence", Constants.DEFAULT_HISTORY_JSON);
                listener.onGetIcibaSentence(new Gson().fromJson(historyJson, IcibaSentence.class));
            }
        }));
    }
}
