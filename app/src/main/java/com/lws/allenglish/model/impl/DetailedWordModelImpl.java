package com.lws.allenglish.model.impl;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.IFLYNativeListener;
import com.iflytek.voiceads.NativeADDataRef;
import com.lws.allenglish.Constants;
import com.lws.allenglish.base.BaseApplication;
import com.lws.allenglish.bean.BaseWord;
import com.lws.allenglish.bean.DetailedWord;
import com.lws.allenglish.database.AllEnglishDatabaseManager;
import com.lws.allenglish.model.DetailedWordModel;
import com.lws.allenglish.model.OnDetailedWordListener;
import com.lws.allenglish.util.GsonRequest;
import com.lws.allenglish.util.VolleySingleton;

import java.util.List;

public class DetailedWordModelImpl implements DetailedWordModel {
    private OnDetailedWordListener listener;
    private IFLYNativeAd iflyNativeAd;
    private DetailedWord detailedWord;
    private Context context;

    public DetailedWordModelImpl(OnDetailedWordListener listener, Context context) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    public void getDetailedWord(int type, String word) {
        String list = null;
        switch (type) {
            case 0:
                list = "1,4,8,14,15";
                break;
            case 1:
                list = "1";
                break;
        }
        String url = "http://www.iciba.com/index.php?a=getWordMean&c=search&list=" + list + "&word=" + word + "&_=" + System.currentTimeMillis();
        VolleySingleton.getInstance().addToRequestQueue(new GsonRequest<>(url,
                DetailedWord.class,
                null,null,
                new Response.Listener<DetailedWord>() {
                    @Override
                    public void onResponse(DetailedWord response) {
                        detailedWord = response;
                        addAds();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError();
            }
        }));
    }

    @Override
    public void setCollectedWord(String word) {
        listener.onIsCollectedWord(AllEnglishDatabaseManager.getInstance(BaseApplication.getInstance()).isCollectedWord(word));
    }

    @Override
    public void saveCollectedWord(BaseWord baseWord) {
        AllEnglishDatabaseManager.getInstance(BaseApplication.getInstance()).saveCollectedWord(baseWord);
    }

    @Override
    public void cancelCollectedWord(String word) {
        AllEnglishDatabaseManager.getInstance(BaseApplication.getInstance()).cancelCollectedWord(word);
    }

    private void addAds() {
        //创建原生广告： adId：开发者在广告平台(http://www.voiceads.cn/)申请的广告位 ID
        iflyNativeAd = new IFLYNativeAd(context, "CDC49DBAAC6D74FC2FCF27351BF2DBD2", mListener);
        iflyNativeAd.loadAd(Constants.ADS_COUNT);
    }

    private IFLYNativeListener mListener = new IFLYNativeListener() {
        @Override
        public void onAdFailed(AdError error) {
            listener.onSuccess(detailedWord);
        }

        @Override
        public void onADLoaded(List<NativeADDataRef> lst) {
            if (lst == null || lst.isEmpty()) {
                listener.onSuccess(detailedWord);
                return;
            }
            if (detailedWord == null){
                detailedWord = new DetailedWord();
            }
            detailedWord.nativeADDataRef = lst.get(0);
            detailedWord.iflyNativeAd = iflyNativeAd;
            listener.onSuccess(detailedWord);
        }

        @Override
        public void onCancel() {
            // 下载类广告，下载提示框取消
        }

        @Override
        public void onConfirm() {
            // 下载类广告，下载提示框确认
        }
    };
}
