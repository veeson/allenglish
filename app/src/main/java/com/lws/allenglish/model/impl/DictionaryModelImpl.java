package com.lws.allenglish.model.impl;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lws.allenglish.bean.IdiomsDictionary;
import com.lws.allenglish.bean.XinhuaDictionary;
import com.lws.allenglish.model.DictionaryModel;
import com.lws.allenglish.model.OnDictionaryListener;
import com.lws.allenglish.util.GsonRequest;
import com.lws.allenglish.util.StringUtils;
import com.lws.allenglish.util.VolleySingleton;

public class DictionaryModelImpl implements DictionaryModel {
    private OnDictionaryListener listener;

    public DictionaryModelImpl(OnDictionaryListener listener) {
        this.listener = listener;
    }

    @Override
    public void getXinhuaDictionary(String text) {
        fetchXinhuaDictionary(text);
    }

    @Override
    public void getIdiomsDictionary(String text) {
        fetchIdiomsDictionary(text);
    }

    private void fetchXinhuaDictionary(String text) {
        VolleySingleton.getInstance().addToRequestQueue(new GsonRequest<>("http://v.juhe.cn/xhzd/query?key=63ca50d904e451ad97e42204eb84247d&word=" +
                StringUtils.encodeText(text), XinhuaDictionary.class, null, null, new Response.Listener<XinhuaDictionary>() {
            @Override
            public void onResponse(XinhuaDictionary response) {
                listener.onXinhuaDictionarySuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError();
            }
        }));
    }

    private void fetchIdiomsDictionary(final String text) {
        VolleySingleton.getInstance().addToRequestQueue(new GsonRequest<>("http://v.juhe.cn/chengyu/query?key=49c4ec6b09923b57b6aa6ef64d670149&word=" +
                StringUtils.encodeText(text), IdiomsDictionary.class, null, null, new Response.Listener<IdiomsDictionary>() {
            @Override
            public void onResponse(IdiomsDictionary response) {
                listener.onIdiomsDictionarySuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError();
            }
        }));
    }
}
