package com.lws.allenglish.bean;

import java.util.List;

public class IcibaSentence {

    /**
     * sid : 2724
     * tts : http://news.iciba.com/admin/tts/2017-09-18-day
     * content : Don't let your pride leave you all alone.
     * note : 别让你的骄傲使你孤独一人。——《吸血鬼日记》
     * love : 2462
     * translation : 词霸小编：一个骄傲的人，结果总是在骄傲里毁灭了自己。——莎士比亚
     * picture : http://cdn.iciba.com/news/word/20170918.jpg
     * picture2 : http://cdn.iciba.com/news/word/big_20170918b.jpg
     * caption : 词霸每日一句
     * dateline : 2017-09-18
     * s_pv : 0
     * sp_pv : 0
     * tags : [{"id":null,"name":null},{"id":null,"name":null}]
     * fenxiang_img : http://cdn.iciba.com/web/news/longweibo/imag/2017-09-18.jpg
     */

    public String sid;
    public String tts;
    public String content;
    public String note;
    public String love;
    public String translation;
    public String picture;
    public String picture2;
    public String caption;
    public String dateline;
    public String s_pv;
    public String sp_pv;
    public String fenxiang_img;
    public List<TagsEntity> tags;

    public static class TagsEntity {
        /**
         * id : null
         * name : null
         */

        public Object id;
        public Object name;
    }
}
