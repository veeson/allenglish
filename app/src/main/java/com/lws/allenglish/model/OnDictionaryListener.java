package com.lws.allenglish.model;

import com.lws.allenglish.bean.IdiomsDictionary;
import com.lws.allenglish.bean.XinhuaDictionary;

public interface OnDictionaryListener {
    void onXinhuaDictionarySuccess(XinhuaDictionary xinhuaDictionary);

    void onIdiomsDictionarySuccess(IdiomsDictionary idiomsDictionary);

    void onError();
}
