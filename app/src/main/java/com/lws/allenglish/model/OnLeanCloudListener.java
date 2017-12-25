package com.lws.allenglish.model;

import com.lws.allenglish.bean.LeanCloudApiBean;

import java.util.List;

public interface OnLeanCloudListener {
    void onSuccess(List<LeanCloudApiBean.ResultsEntity> list);
    void onError();
}
