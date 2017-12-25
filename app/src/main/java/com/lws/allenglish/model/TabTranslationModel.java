package com.lws.allenglish.model;

public interface TabTranslationModel {
    void getBaiduTranslation(String text);
    void getYoudaoTranslation(String text);
    void getGoogleTranslation(String text);
}
