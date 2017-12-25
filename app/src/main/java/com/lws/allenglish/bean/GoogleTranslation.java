package com.lws.allenglish.bean;

import java.util.List;

public class GoogleTranslation {

    /**
     * sentences : [{"trans":"Hello Google","orig":"谷歌你好","backend":3}]
     * src : zh-CN
     * confidence : 0.73800677
     * ld_result : {"srclangs":["zh-CN"],"srclangs_confidences":[0.73800677],"extended_srclangs":["zh-CN"]}
     */

    public String src;
    public double confidence;
    public LdResultEntity ld_result;
    public List<SentencesEntity> sentences;

    public static class LdResultEntity {
        public List<String> srclangs;
        public List<Double> srclangs_confidences;
        public List<String> extended_srclangs;
    }

    public static class SentencesEntity {
        /**
         * trans : Hello Google
         * orig : 谷歌你好
         * backend : 3
         */

        public String trans;
        public String orig;
        public int backend;
    }
}
