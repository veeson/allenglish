package com.lws.allenglish;

/**
 * Created by Wilson on 2016/12/13.
 */

public class Constants {
    // Ctrl + Shift + U 大小写转换快捷键
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_VALUE = "application/json";
    public static final String X_LC_Id = "X-LC-Id";
    public static final String X_LC_ID_VALUE = "QirfL37ojQOHahLaas6pCUVD-gzGzoHsz";
//    public static final String X_LC_KEY = "X-LC-Key";
    public static final String X_LC_SIGN = "X-LC-Sign";
    public static final String X_LC_KEY_VALUE = "fGaq08HCWfE86b8arqWpOBKz";
    public static final String BASE_INFO = "base_info";
    public static final String TRANSLATION_RESULT = "translation_result";
    public static final String BAIDU_APPID = "20160607000022961";
    public static final String BAIDU_SECRET_KEY = "s52637hqrVrVsZks53Dc";
    public static final String TRANSLATION_RECORD_DATA = "translation_record_data";
    public static final String PAGE_NUMBER = "page_number";
//    public static final String BASE_ENGLISH = "base_english";
//    public static final String DATA_SHEET = "data_sheet";
//    public static final String IDIOMS_DICTIONARY_APPKEY = "49c4ec6b09923b57b6aa6ef64d670149";
//    public static final String XINHUA_DICTIONARY_APPKEY = "63ca50d904e451ad97e42204eb84247d";

    public static final String[] BILINGUAL_READING_TAGS = {"资讯", "时尚", "体育", "娱乐", "科技", "故事", "文化", "校园", "职场"};
    public static final String[] VOA_ENGLISH_TAGS = {"慢速英语", "常速英语", "双语新闻", "视频学习"};

    public static final String READER = "Reader";
    public static final String Video = "Video";
    public static final String VOAENGLISH = "VOAEnglish";

    public static final String DEFAULT_HISTORY_JSON = "{\"sid\":\"2723\",\"tts\":\"http://news.iciba.com/admin/tts/2017-09-17-day\",\"content\":\"I stay up late every night and realize it\\u0027s a bad idea every morning! \",\"note\":\"每天早上都觉得自己晚上真不该熬夜！\",\"love\":\"3430\",\"translation\":\"词霸小编：有一种妥协叫早睡，有一种委屈叫早起。\",\"picture\":\"http://cdn.iciba.com/news/word/20170917.jpg\",\"picture2\":\"http://cdn.iciba.com/news/word/big_20170917b.jpg\",\"caption\":\"词霸每日一句\",\"dateline\":\"2017-09-17\",\"s_pv\":\"0\",\"sp_pv\":\"0\",\"fenxiang_img\":\"http://cdn.iciba.com/web/news/longweibo/imag/2017-09-17.jpg\",\"tags\":[{},{}]}";

    public static final int ADS_COUNT = 1; // 一次拉取的广告条数：范围 1-30（目前仅支持每次请求一条）
}
