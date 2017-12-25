package com.lws.allenglish.model;

import com.lws.allenglish.bean.BaiduTranslation;
import com.lws.allenglish.bean.GoogleTranslation;
import com.lws.allenglish.bean.YoudaoTranslation;

public interface OnTabTranslationListener {
    void onBaiduTranslationSuccess(BaiduTranslation baiduTranslation);

    void onYoudaoTranslationSuccess(YoudaoTranslation youdaoTranslation);

    void onGoogleTranslationSuccess(GoogleTranslation googleTranslation);

    void onError();
}
