package com.lws.allenglish.model.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.IFLYNativeListener;
import com.iflytek.voiceads.NativeADDataRef;
import com.lws.allenglish.Constants;
import com.lws.allenglish.bean.BaseWord;
import com.lws.allenglish.bean.DetailedWord;
import com.lws.allenglish.bean.LeanCloudApiBean;
import com.lws.allenglish.model.BaseLearningModel;
import com.lws.allenglish.model.OnBaseLearningListener;
import com.lws.allenglish.util.GsonRequest;
import com.lws.allenglish.util.StringUtils;
import com.lws.allenglish.util.VolleySingleton;
import com.lws.allenglish.util.common.MD5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseLearningModelImpl implements BaseLearningModel {
    private OnBaseLearningListener listener;
    private IFLYNativeAd iflyNativeAd1;
    private IFLYNativeAd iflyNativeAd2;
    private List<LeanCloudApiBean.ResultsEntity> beanList = new ArrayList<>();
    ;

    private Context context;

    public BaseLearningModelImpl(Context context, OnBaseLearningListener listener) {
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
                null, null,
                new Response.Listener<DetailedWord>() {
                    @Override
                    public void onResponse(DetailedWord response) {
                        if (response == null || response.baesInfo == null) {
                            listener.onGetBaseWordError();
                            return;
                        }
                        BaseWord baseWord = new BaseWord();
                        baseWord.word = response.baesInfo.word_name;
                        if (response.baesInfo.symbols != null
                                && response.baesInfo.symbols.get(0) != null
                                && response.baesInfo.symbols.get(0).parts != null
                                && response.baesInfo.symbols.get(0).parts.get(0) != null) {
                            StringBuilder sb = new StringBuilder();
                            for (DetailedWord.BaesInfoEntity.SymbolsEntity.PartsEntity partsEntity : response.baesInfo.symbols.get(0).parts) {
                                sb.append(partsEntity.part).append(TextUtils.join(", ", partsEntity.means)).append("\n");
                            }
                            sb.setLength(sb.length() - 1);
                            baseWord.means = sb.toString();
                            baseWord.ph_en = "[" + response.baesInfo.symbols.get(0).ph_en + "]";
                            baseWord.ph_am = "[" + response.baesInfo.symbols.get(0).ph_am + "]";
                        }
                        listener.onGetBaseWordSuccess(baseWord);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onGetBaseWordError();
            }
        }));
    }

    @Override
    public void getLeanCloudBean(String tableName, int limit, int skin, String tag, String createdAt) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_VALUE);
        headers.put(Constants.X_LC_Id, Constants.X_LC_ID_VALUE);
        long timestamp = System.currentTimeMillis();
        headers.put(Constants.X_LC_SIGN, MD5.md5(timestamp + Constants.X_LC_KEY_VALUE) + "," + timestamp);
        String url;
        if (tag != null) {
            url = "https://leancloud.cn:443/1.1/classes/" + tableName + "?where={\"createdAt\":{\"$lt\":{\"__type\":\"Date\",\"iso\":\"" + createdAt + "\"}},\"tag\":\"" + StringUtils.encodeText(tag) + "\"}&limit=" + limit + "&order=-createdAt";
        } else {
            url = "https://leancloud.cn:443/1.1/classes/" + tableName + "?where={\"createdAt\":{\"$lt\":{\"__type\":\"Date\",\"iso\":\"" + createdAt + "\"}}}&limit=" + limit + "&order=-createdAt";
        }
        VolleySingleton.getInstance()
                .addToRequestQueue(new GsonRequest<>(url, LeanCloudApiBean.class, headers, null, new Response.Listener<LeanCloudApiBean>() {
                    @Override
                    public void onResponse(LeanCloudApiBean response) {
                        beanList.addAll(response.results);
                        addListAds();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }));
    }

    @Override
    public void getImageAds() {
        addImageAds();
    }

    private void addListAds() {
        if (iflyNativeAd1 == null) {
            //创建原生广告： adId：开发者在广告平台(http://www.voiceads.cn/)申请的广告位 ID
            iflyNativeAd1 = new IFLYNativeAd(context, "B19B87C53A1032812B1598F976D7CE2F", mListener);
        }
        iflyNativeAd1.loadAd(Constants.ADS_COUNT);
    }

    private void addImageAds() {
        //创建原生广告： adId：开发者在广告平台(http://www.voiceads.cn/)申请的广告位 ID
        iflyNativeAd2 = new IFLYNativeAd(context, "A35B350F770FD64B63DE453997A3B4D6", mListener2);
        iflyNativeAd2.loadAd(Constants.ADS_COUNT);
    }

    private IFLYNativeListener mListener = new IFLYNativeListener() {
        @Override
        public void onAdFailed(AdError error) {
            listener.onGetLeanCloudSuccess(beanList);
        }

        @Override
        public void onADLoaded(List<NativeADDataRef> lst) {
            if (lst == null || lst.isEmpty()) {
                listener.onGetLeanCloudSuccess(beanList);
                return;
            }
            LeanCloudApiBean.ResultsEntity leanCloudBean = new LeanCloudApiBean.ResultsEntity();
            leanCloudBean.nativeADDataRef = lst.get(0);
            leanCloudBean.iflyNativeAd = iflyNativeAd1;
            leanCloudBean.type = 1;
            beanList.add(beanList.size() / 2, leanCloudBean);
            listener.onGetLeanCloudSuccess(beanList);
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

    private IFLYNativeListener mListener2 = new IFLYNativeListener() {
        @Override
        public void onAdFailed(AdError error) {
        }

        @Override
        public void onADLoaded(final List<NativeADDataRef> lst) {
            if (lst == null || lst.isEmpty()) {
                return;
            }
//            DisplayMetrics metrics = new DisplayMetrics();
//            ((AppCompatActivity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
//            int width = metrics.widthPixels;
            RequestQueue mQueue = VolleySingleton.getInstance().getRequestQueue();
            ImageRequest imageRequest = new ImageRequest(
                    lst.get(0).getImage(),
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            listener.onGetAdsImageSuccess(response, iflyNativeAd2, lst.get(0));
                        }
                    }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            mQueue.add(imageRequest);
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
