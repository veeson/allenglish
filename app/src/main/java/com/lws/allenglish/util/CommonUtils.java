package com.lws.allenglish.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.NativeADDataRef;
import com.lws.allenglish.R;
import com.lws.allenglish.util.common.ToastUtils;

import java.util.Formatter;
import java.util.Locale;

public class CommonUtils {
    private static long lastClickTime;
    private final static int SPACE_TIME = 1000;

    public static boolean isFastDoubleClick() {
        return isFastDoubleClick(SPACE_TIME);
    }

    public static boolean isFastDoubleClick(long spaceTime) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < spaceTime) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static void setTextView(TextView tv, CharSequence charSequence) {
        tv.setMovementMethod(new ScrollingMovementMethod()); // 设置TextView可滚动
        tv.setTextIsSelectable(true); // 设置文本可选
        tv.setText(charSequence);
    }

    public static String replaceImgTag(String text) {
        return text.replaceAll("<\\s*?img[^>]*?[\\s\\S]*?\\s*?\\s*?\\s*?>", "");
    }

    public static void setUrlImageToImageView(ImageView imageView, String url) {
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView,
                R.drawable.ic_default, R.drawable.ic_default);
        VolleySingleton.getInstance().getImageLoader().get(url,
                listener, imageView.getMeasuredWidth(), imageView.getMeasuredHeight(), ImageView.ScaleType.FIT_XY);
    }

    /**
     * 将毫秒数格式化为"##:##"的时间
     *
     * @param milliseconds 毫秒数
     * @return ##:##
     */
    public static String formatTime(long milliseconds) {
        if (milliseconds <= 0 || milliseconds >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        long totalSeconds = milliseconds / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public static void setAds(View view, final IFLYNativeAd iflyNativeAd, final NativeADDataRef nativeADDataRef) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nativeADDataRef.onClicked(v);
            }
        });
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        iflyNativeAd.setParameter(AdKeys.CLICK_POS_DX, event.getX() + "");
                        iflyNativeAd.setParameter(AdKeys.CLICK_POS_DY, event.getY() + "");
                        break;
                    case MotionEvent.ACTION_UP:
                        iflyNativeAd.setParameter(AdKeys.CLICK_POS_UX, event.getX() + "");
                        iflyNativeAd.setParameter(AdKeys.CLICK_POS_UY, event.getY() + "");
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 发送邮件
     *
     * @param context
     */
    public static void sendEmail(Context context) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"weishengliu@foxmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "全能词典意见反馈");
        i.putExtra(Intent.EXTRA_TEXT, "");
        try {
            context.startActivity(Intent.createChooser(i, "选择发送邮件的应用"));
        } catch (android.content.ActivityNotFoundException ex) {
            ToastUtils.show(context, "没有找到邮件客户端");
        }
    }

    public static String getVersionName(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert packInfo != null;
        return packInfo.versionName;
    }

    public static void sendTo(Context context) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "哎哟，这个软件不错哦，小伙伴快来！\nhttps://www.pgyer.com/apiv2/app/install?appKey=56bd51ddb76877188a1836d791ed8436&_api_key=a08ef5ee127a27bd4210f7e1f9e7c84e");
        sendIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(sendIntent, "分享到"));
    }
}
