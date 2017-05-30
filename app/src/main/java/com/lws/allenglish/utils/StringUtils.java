package com.lws.allenglish.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Wilson on 2016/12/15.
 */

public class StringUtils {
    /**
     * 检测string中是否含有中文
     *
     * @param s
     * @return
     */
    public static boolean hasChinese(String s) {
        boolean ifHaveChinese = false;
        Character.UnicodeBlock ub;
        for (char c : s.toCharArray()) {
            ub = Character.UnicodeBlock.of(c);
            if ((ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                    || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                    || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS)) {
                ifHaveChinese = true;
                break;
            }
            ifHaveChinese = false;
        }
        return ifHaveChinese;
    }

    /**
     * 复制内容到Clipboard
     *
     * @param text
     */
    public static void copyToClipboard(Context context, String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        if (!TextUtils.isEmpty(clipboard.getPrimaryClip().getItemAt(0).getText())) {
            Toast.makeText(context, "已复制到粘贴板", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享文本给其他应用程序
     *
     * @param text
     */
    public static void shareToApps(Context context, String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(sendIntent, "分享到"));
    }

    /**
     * URLEncoder
     *
     * @param text
     * @return
     */
    public static String encodeText(String text) {
        String encoderText = null;
        try {
            encoderText = URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encoderText;
    }
}
