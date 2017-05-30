package com.lws.allenglish.bean;

import java.util.List;

/**
 * Created by Wilson on 2016/12/28.
 */

public class IdiomsDictionary {

    /**
     * reason : success
     * result : {"bushou":"一","head":"三","pinyin":"sān xīn èr yì","chengyujs":" 又想这样又想那样，犹豫不定。常指不安心，不专一。","from_":" 元·关汉卿《救风尘》第一折：\u201c争奈是匪妓，都三心二意。\u201d","example":" 可是眼下大敌当前，后有追兵，你可千万不要～，迟疑不决，误了大事。 姚雪垠《李自成》第一卷第四章","yufa":" 联合式；作谓语、宾语；含贬义","ciyujs":null,"yinzhengjs":"谓意志不坚定，犹豫不决。 元 关汉卿 《救风尘》第一折：\u201c争奈是匪妓，都三心二意。\u201d 老舍 《女店员》第一幕：\u201c政府招考咱们，咱们就得干出个名堂来，不能三心二意。\u201d亦作\u201c 三心两意 \u201d。《醒世姻缘传》第二七回：\u201c我的主意已定了，你们都别要三心两意，七嘴八舌的乱了我的主意。\u201d 欧阳予倩 《忠王李秀成》第一幕：\u201c你不要三心两意，早点歇息，明天又要行军了。\u201d","tongyi":["见异思迁","朝三暮四"],"fanyi":["一心一意","专心致志"]}
     * error_code : 0
     */

    public String reason;
    public ResultEntity result;
    public int error_code;

    public static class ResultEntity {
        /**
         * bushou : 一
         * head : 三
         * pinyin : sān xīn èr yì
         * chengyujs :  又想这样又想那样，犹豫不定。常指不安心，不专一。
         * from_ :  元·关汉卿《救风尘》第一折：“争奈是匪妓，都三心二意。”
         * example :  可是眼下大敌当前，后有追兵，你可千万不要～，迟疑不决，误了大事。 姚雪垠《李自成》第一卷第四章
         * yufa :  联合式；作谓语、宾语；含贬义
         * ciyujs : null
         * yinzhengjs : 谓意志不坚定，犹豫不决。 元 关汉卿 《救风尘》第一折：“争奈是匪妓，都三心二意。” 老舍 《女店员》第一幕：“政府招考咱们，咱们就得干出个名堂来，不能三心二意。”亦作“ 三心两意 ”。《醒世姻缘传》第二七回：“我的主意已定了，你们都别要三心两意，七嘴八舌的乱了我的主意。” 欧阳予倩 《忠王李秀成》第一幕：“你不要三心两意，早点歇息，明天又要行军了。”
         * tongyi : ["见异思迁","朝三暮四"]
         * fanyi : ["一心一意","专心致志"]
         */

        public String bushou;
        public String head;
        public String pinyin;
        public String chengyujs;
        public String from_;
        public String example;
        public String yufa;
        public Object ciyujs;
        public String yinzhengjs;
        public List<String> tongyi;
        public List<String> fanyi;
    }
}
