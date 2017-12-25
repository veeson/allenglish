package com.lws.allenglish.model;

public interface BaseLearningModel {
    void getDetailedWord(int type, String word);
    void getLeanCloudBean(String tableName, int limit, int skin, String tag, String createdAt);
    void getImageAds();
}
