package com.lws.allenglish.bean;

import java.util.List;

/**
 * Created by Wilson on 2016/12/22.
 */

public class IcibaDailySentence extends BaseIcibaDailySentence {

    /**
     * sid : 2453
     * tts : http://news.iciba.com/admin/tts/2016-12-22-day.mp3
     * content : And ever has it been that love knows not its own depth until the hour of separation.
     * note : 除非临到了别离的时候，爱永远不会知道自己的深浅。
     * love : 1104
     * translation : 词霸小编：下周二（12月27日）在北京或者是要去北京的小伙伴们注意啦！下周二下午2点半金山词霸要在北京举行粉丝交流会啦！届时会有各种好礼拿到手软，还有机会跟金山词霸的开发人员一对一交流哦！把你想吐槽的想说的都畅所欲言~最后还有大抽奖！有手机电脑等超级大礼，抽到现场就能拿走！参与方式在首页轮播图第三张，点进去就是啦！
     * picture : http://cdn.iciba.com/news/word/20161222.jpg
     * picture2 : http://cdn.iciba.com/news/word/big_20161222b.jpg
     * caption : 词霸每日一句
     * dateline : 2016-12-22
     * s_pv : 0
     * sp_pv : 0
     * tags : [{"id":null,"name":null}]
     * fenxiang_img : http://cdn.iciba.com/web/news/longweibo/imag/2016-12-22.jpg
     */

    public String sid;
    //    public String tts;
//    public String content;
//    public String note;
    public String love;
    public String translation;
    public String picture;
    public String picture2;
    public String caption;
    //    public String dateline;
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
