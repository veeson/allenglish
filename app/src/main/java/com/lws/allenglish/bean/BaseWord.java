package com.lws.allenglish.bean;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class BaseWord implements Comparable<BaseWord>, Serializable {
    public String word;
    public String ph_en;
    public String ph_am;
    public String means;

    public BaseWord() {
    }

    @Override
    public String toString() {
        return "BaseWord{" +
                "word='" + word + '\'' +
                ", ph_en='" + ph_en + '\'' +
                ", ph_am='" + ph_am + '\'' +
                ", means='" + means + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull BaseWord o) {
        return word.compareToIgnoreCase(o.word);
    }
}
