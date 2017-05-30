package com.lws.allenglish.bean;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Wilson on 2016/12/16.
 */

public class TranslationRecord implements Comparable<TranslationRecord>, Serializable {
    public String text;
    public String result;
    public String date;
    public String source;

    @Override
    public int compareTo(@NonNull TranslationRecord o) {
        return date.compareTo(o.date);
    }

    @Override
    public String toString() {
        return "TranslationRecord{" +
                "text='" + text + '\'' +
                ", result='" + result + '\'' +
                ", date='" + date + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
