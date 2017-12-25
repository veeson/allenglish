package com.lws.allenglish.model.impl;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lws.allenglish.Constants;
import com.lws.allenglish.bean.LeanCloudApiBean;
import com.lws.allenglish.model.LeanCloudModel;
import com.lws.allenglish.model.OnLeanCloudListener;
import com.lws.allenglish.util.GsonRequest;
import com.lws.allenglish.util.StringUtils;
import com.lws.allenglish.util.VolleySingleton;
import com.lws.allenglish.util.common.MD5;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LeanCloudModelImpl implements LeanCloudModel {
    private OnLeanCloudListener leanCloudListener;

    @Override
    public void getLeanCloudBean(String tableName, final int limit, int skin, String tag, OnLeanCloudListener listener) {
        this.leanCloudListener = listener;
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_VALUE);
        headers.put(Constants.X_LC_Id, Constants.X_LC_ID_VALUE);
        long timestamp = System.currentTimeMillis();
        headers.put(Constants.X_LC_SIGN, MD5.md5(timestamp + Constants.X_LC_KEY_VALUE) + "," + timestamp);
        String dateString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.CHINA).format(new Date());
        String url;
        if (tag != null) {
            url = "https://leancloud.cn:443/1.1/classes/" + tableName + "?where={\"postTime\":{\"$lte\":{\"__type\":\"Date\",\"iso\":\"" + dateString + "\"}},\"tag\":\"" + StringUtils.encodeText(tag) + "\"}&limit=" + limit + "&skip=" + skin + "&order=-createdAt";
        } else {
            url = "https://leancloud.cn:443/1.1/classes/" + tableName + "?where={\"postTime\":{\"$lte\":{\"__type\":\"Date\",\"iso\":\"" + dateString + "\"}}}&limit=" + limit + "&skip=" + skin + "&order=-createdAt";
        }
        VolleySingleton.getInstance()
                .addToRequestQueue(new GsonRequest<>(url, LeanCloudApiBean.class, headers, null, new Response.Listener<LeanCloudApiBean>() {
                    @Override
                    public void onResponse(LeanCloudApiBean response) {
                        leanCloudListener.onSuccess(response.results);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        leanCloudListener.onError();
                    }
                }));
    }
}

