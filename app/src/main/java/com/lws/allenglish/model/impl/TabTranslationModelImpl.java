package com.lws.allenglish.model.impl;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lws.allenglish.Constants;
import com.lws.allenglish.base.BaseApplication;
import com.lws.allenglish.bean.BaiduTranslation;
import com.lws.allenglish.bean.GoogleTranslation;
import com.lws.allenglish.bean.TranslationRecord;
import com.lws.allenglish.bean.YoudaoTranslation;
import com.lws.allenglish.database.AllEnglishDatabaseManager;
import com.lws.allenglish.model.OnTabTranslationListener;
import com.lws.allenglish.model.TabTranslationModel;
import com.lws.allenglish.util.GsonRequest;
import com.lws.allenglish.util.StringUtils;
import com.lws.allenglish.util.VolleySingleton;
import com.lws.allenglish.util.common.MD5;

import java.util.HashMap;
import java.util.Map;

public class TabTranslationModelImpl implements TabTranslationModel {
    private OnTabTranslationListener listener;

    public TabTranslationModelImpl(OnTabTranslationListener listener) {
        this.listener = listener;
    }

    @Override
    public void getBaiduTranslation(String text) {
        fetchBaiduTranslation(text);
    }

    @Override
    public void getYoudaoTranslation(String text) {
        fetchYoudaoTranslation(text);
    }

    @Override
    public void getGoogleTranslation(String text) {
        fetchGoogleTranslation(text);
    }

    private void fetchBaiduTranslation(String text) {
        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        String sign = MD5.md5(Constants.BAIDU_APPID + text + salt + Constants.BAIDU_SECRET_KEY);
        String url;
        if (StringUtils.hasChinese(text)) {
            url = "http://api.fanyi.baidu.com/api/trans/vip/translate?salt=" + salt + "&appid=" +
                    Constants.BAIDU_APPID + "&sign=" + sign + "&from=auto&to=en&q=" +
                    StringUtils.encodeText(text);
        } else {
            url = "http://api.fanyi.baidu.com/api/trans/vip/translate?salt=" + salt + "&appid=" +
                    Constants.BAIDU_APPID + "&sign=" + sign + "&from=auto&to=zh&q=" +
                    StringUtils.encodeText(text);
        }
        VolleySingleton.getInstance().addToRequestQueue(new GsonRequest<>(url, BaiduTranslation.class, null, null, new Response.Listener<BaiduTranslation>() {
            @Override
            public void onResponse(BaiduTranslation response) {
                listener.onBaiduTranslationSuccess(response);
                TranslationRecord record = new TranslationRecord();
                record.text = response.trans_result.get(0).src;
                record.result = response.trans_result.get(0).dst;
                record.source = "百度翻译";
                AllEnglishDatabaseManager.getInstance(BaseApplication.getInstance()).saveTranslationRecord(record);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError();
            }
        }));
    }

    private void fetchYoudaoTranslation(String text) {
        VolleySingleton.getInstance().addToRequestQueue(new GsonRequest<>("http://fanyi.youdao.com/openapi.do?keyfrom=allenglish&key=1877329489&type=data&doctype=json&version=1.1&only=translate&q=" +
                StringUtils.encodeText(text), YoudaoTranslation.class, null, null, new Response.Listener<YoudaoTranslation>() {
            @Override
            public void onResponse(YoudaoTranslation response) {
                listener.onYoudaoTranslationSuccess(response);
                TranslationRecord record2 = new TranslationRecord();
                record2.text = response.query;
                record2.result = response.translation.get(0);
                record2.source = "有道翻译";
                AllEnglishDatabaseManager.getInstance(BaseApplication.getInstance()).saveTranslationRecord(record2);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError();
            }
        }));
    }

    private void fetchGoogleTranslation(String text) {
        String url;
        if (StringUtils.hasChinese(text)) {
            url = "https://translate.google.cn/translate_a/single?client=gtx&sl=auto&tl=auto&hl=en&dt=t&dt=tl&dj=1&source=icon&q=" +
                    StringUtils.encodeText(text);
        } else {
            url = "https://translate.google.cn/translate_a/single?client=gtx&sl=auto&tl=auto&hl=zh-CN&dt=t&dt=tl&dj=1&source=icon&q=" +
                    StringUtils.encodeText(text);
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:50.0) Gecko/20100101 Firefox/50.0");
        VolleySingleton.getInstance().addToRequestQueue(new GsonRequest<>(url, GoogleTranslation.class, headers, null, new Response.Listener<GoogleTranslation>() {
            @Override
            public void onResponse(GoogleTranslation response) {
                listener.onGoogleTranslationSuccess(response);
                TranslationRecord record3 = new TranslationRecord();
                record3.text = response.sentences.get(0).orig;
                record3.result = response.sentences.get(0).trans;
                record3.source = "谷歌翻译";
                AllEnglishDatabaseManager.getInstance(BaseApplication.getInstance()).saveTranslationRecord(record3);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError();
            }
        }));
    }
}
