package com.lws.allenglish.bean;

import java.util.List;

public class XinhuaDictionary {
    public String reason;
    public ResultEntity result;
    public int error_code;

    public static class ResultEntity {
        public String id;
        public String zi;
        public String py;
        public String wubi;
        public String pinyin;
        public String bushou;
        public String bihua;
        public List<String> jijie;
        public List<String> xiangjie;
    }
}
