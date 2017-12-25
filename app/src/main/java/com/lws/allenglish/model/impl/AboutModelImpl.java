package com.lws.allenglish.model.impl;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lws.allenglish.base.BaseApplication;
import com.lws.allenglish.bean.pgyer.ViewBean;
import com.lws.allenglish.model.AboutModel;
import com.lws.allenglish.model.OnAboutListener;
import com.lws.allenglish.util.CommonUtils;
import com.lws.allenglish.util.GsonRequest;
import com.lws.allenglish.util.VolleySingleton;

import java.util.HashMap;
import java.util.Map;

public class AboutModelImpl implements AboutModel {
    private OnAboutListener listener;

    public AboutModelImpl(OnAboutListener listener) {
        this.listener = listener;
    }

    @Override
    public void checkNewVersion() {
        Map<String, String> map = new HashMap<>();
        map.put("appKey", "56bd51ddb76877188a1836d791ed8436");
        map.put("_api_key", "a08ef5ee127a27bd4210f7e1f9e7c84e");
        VolleySingleton.getInstance().addToRequestQueue(new GsonRequest<>(Request.Method.POST, "https://www.pgyer.com/apiv2/app/view",
                ViewBean.class,
                null, map,
                new Response.Listener<ViewBean>() {
                    @Override
                    public void onResponse(ViewBean response) {
                        if (response.data.buildVersion.equals(CommonUtils.getVersionName(BaseApplication.getInstance()))) {
                            listener.onNoNewVersion();
                        } else {
                            listener.onGetANewVersion(response.data.buildUpdateDescription);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onCheckFailed();
            }
        }));
    }
}
