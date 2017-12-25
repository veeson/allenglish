package com.lws.allenglish.model;

public interface LeanCloudModel {
    void getLeanCloudBean(String tableName, int limit, int skin, String tag, OnLeanCloudListener listener);
}
