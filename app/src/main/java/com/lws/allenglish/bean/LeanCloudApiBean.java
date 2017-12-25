package com.lws.allenglish.bean;

import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.NativeADDataRef;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Wilson on 2017/10/18.
 */

public class LeanCloudApiBean {
    public List<ResultsEntity> results;

    public static class ResultsEntity implements Serializable {
        public String content;
        public String createdAt;
        public String source;
        public String title;
        public String tag;
        public String imageUrl;
        public PostTimeEntity postTime;
        public String pageUrl;
        public String mediaUrl;

        public int type; // 0：内容 1：广告
        public NativeADDataRef nativeADDataRef;
        public IFLYNativeAd iflyNativeAd;

        public static class PostTimeEntity implements Serializable {
            /**
             * __type : Date
             * iso : 2017-10-12T16:00:00.000Z
             */

            public String __type;
            public String iso;
        }
    }
}
