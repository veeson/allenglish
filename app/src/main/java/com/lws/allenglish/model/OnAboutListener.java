package com.lws.allenglish.model;

public interface OnAboutListener {
    void onGetANewVersion(String buildUpdateDescription);
    void onNoNewVersion();
    void onCheckFailed();
}
