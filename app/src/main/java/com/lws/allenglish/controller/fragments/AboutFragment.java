package com.lws.allenglish.controller.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;

import com.lws.allenglish.R;
import com.lws.allenglish.model.AboutModel;
import com.lws.allenglish.model.OnAboutListener;
import com.lws.allenglish.model.impl.AboutModelImpl;
import com.lws.allenglish.util.CommonUtils;
import com.lws.allenglish.util.common.ToastUtils;

public class AboutFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    private Preference versionUpdate;
    private Preference contactAuthor;
    private Preference sendTo;
    private Context mContext;

    private AboutModel aboutModel;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.about);
        mContext = getActivity();
        versionUpdate = findPreference("version_update");
        contactAuthor = findPreference("contact_author");
        sendTo = findPreference("send_to");
        versionUpdate.setSummary("VERSION-" + CommonUtils.getVersionName(mContext));
        contactAuthor.setOnPreferenceClickListener(this);
        versionUpdate.setOnPreferenceClickListener(this);
        sendTo.setOnPreferenceClickListener(this);

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("正在检测新版本...");
        progressDialog.setCanceledOnTouchOutside(false);

        aboutModel = new AboutModelImpl(new CustomOnAboutListener());
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (contactAuthor == preference) {
            CommonUtils.sendEmail(mContext);
        } else if (versionUpdate == preference) {
            aboutModel.checkNewVersion();
            progressDialog.show();
        } else if (sendTo == preference) {
            CommonUtils.sendTo(mContext);
        }
        return false;
    }

    private class CustomOnAboutListener implements OnAboutListener {

        @Override
        public void onGetANewVersion(String buildUpdateDescription) {
            progressDialog.dismiss();
            new AlertDialog.Builder(mContext).setTitle("软件版本更新").setMessage(buildUpdateDescription)
                    .setPositiveButton("去更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            Uri content_url = Uri.parse("https://www.pgyer.com/apiv2/app/install?appKey=56bd51ddb76877188a1836d791ed8436&_api_key=a08ef5ee127a27bd4210f7e1f9e7c84e");
                            intent.setData(content_url);
                            startActivity(intent);
                        }
                    }).setNegativeButton("先等等", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
        }

        @Override
        public void onNoNewVersion() {
            progressDialog.dismiss();
            ToastUtils.show(mContext, "没有新版本");
        }

        @Override
        public void onCheckFailed() {
            progressDialog.dismiss();
            ToastUtils.show(mContext, R.string.bad_internet);
        }
    }
}
