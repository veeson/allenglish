package com.lws.allenglish.bean.pgyer;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by Wilson on 2017/9/26.
 */

public class ViewBean {

    public int code;
    public String message;
    public DataEntity data;

    public static class DataEntity {
        public String buildKey;
        public String buildType;
        public String buildIsLastest;
        public String buildFileKey;
        public String buildFileName;
        public String buildFileSize;
        public String buildName;
        public String buildVersion;
        public String buildVersionNo;
        public String buildBuildVersion;
        public String buildIdentifier;
        public String buildIcon;
        public String buildPassword;
        public String buildDescription;
        public String buildUpdateDescription;
        public String buildScreenshots;
        public String buildShortcutUrl;
        public String buildLauncherActivity;
        public String buildCreated;
        public String buildUpdated;
        public String buildQRCodeURL;
        public String buildFollowed;
        public String otherAppsCount;
        @Expose
        public List<?> otherapps;
    }
}
