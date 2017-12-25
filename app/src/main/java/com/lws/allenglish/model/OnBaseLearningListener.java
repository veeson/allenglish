package com.lws.allenglish.model;

import android.graphics.Bitmap;

import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.NativeADDataRef;
import com.lws.allenglish.bean.BaseWord;
import com.lws.allenglish.bean.LeanCloudApiBean;

import java.util.List;

public interface OnBaseLearningListener {
    void onGetBaseWordSuccess(BaseWord baseWord);
    void onGetBaseWordError();
    void onGetLeanCloudSuccess(List<LeanCloudApiBean.ResultsEntity> list);
    void onGetAdsImageSuccess(Bitmap bitmap, IFLYNativeAd iflyNativeAd, NativeADDataRef nativeADDataRef);
}
