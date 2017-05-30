package com.lws.allenglish.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Wilson on 2016/12/19.
 */

public class BaseEnglish {
//    public transient List<ResultsEntity> results;

    public static class ResultsEntity implements Serializable {
        /**
         * updatedAt : 2016-12-18T03:50:02.129Z
         * objectId : 5856076a128fe1006b6a033c
         * createdAt : 2016-12-18T03:50:02.129Z
         * source : 可可英语
         * imageUrl : http://pic.kekenet.com//2016/1218/thumb_110_82_64771482028340.jpg
         * newsUrl : http://www.kekenet.com/read/201612/484208.shtml
         * summary : [环球之旅] 英国评出最冷清火车站 一年仅接待旅客12人次 英国剑桥郡的西皮亚山火车站去年竟仅接待旅客12人次，荣登“全国最冷清车站榜”榜首。 2016-12-18 编辑：max 标签： 双语新闻 英国 火车站 冷清
         * postTime : {"__type":"Date","iso":"2016-12-17T16:00:00.000Z"}
         * tag : 图文阅读
         * newsTitle : 英国评出最冷清火车站 一年仅接待旅客12人次
         * newsContent : <div class="info-qh">
         * <div class="qh_en" id="en1">
         * <p>A table ranking of the quietest stations in the UK is topped by Shippea Hill station in Cambridgeshire, which astonishingly only 12 people used last year.</p>
         * </div>
         * <div class="qh_zg" id="zg1">
         * 英国剑桥郡的西皮亚山火车站去年竟仅接待旅客12人次，荣登“全国最冷清车站榜”榜首。
         * </div>
         * <div class="qh_en" id="en2">
         * Just one passenger a month boarded or exited a train at the 170-year-old station, which serves the hamlet of Shippea Hill, down from 22 in 2014-15, figures from the Office for Rail and Road for 2015-16 show.
         * </div>
         * <div class="qh_zg" id="zg2">
         * 这个服务于西皮亚山村的车站已有170年历史。英国铁路和公路办公室公布的2015-16年度数据显示，每个月只有一位旅客在该火车站上下车，较2014-15年度的22人次有所下降。
         * </div>
         * <div class="qh_en" id="en3">
         * One train to Norwich serves the outpost on weekdays, but there is no return service.
         * </div>
         * <div class="qh_zg" id="zg3">
         * 工作日有一列驶往诺维奇镇的火车经过这个边远村庄，但没有返程车。
         * <p></p>
         * </div>
         * <div class="qh_en" id="en4">
         * <p></p>
         * <p>Shippea Hill is a request stop, so passengers must flag down the driver if they want to board.</p>
         * </div>
         * <div class="qh_zg" id="zg4">
         * 西皮亚山火车站是招呼站，因此如果旅客想在该站上车，须打旗号向司机示意。
         * </div>
         * <div class="qh_en" id="en5">
         * By contrast, London's Waterloo was the country's busiest station with 99.1 million passengers coming in and out during the past 12 months. The figure is equivalent to around three people per second.
         * </div>
         * <div class="qh_zg" id="zg5">
         * 与之相比，英国最繁忙的车站--伦敦滑铁卢火车站过去12个月的进出站旅客总量为9910万人次，相当于每秒约有3名旅客进出该站。
         * </div>
         * <div class="qh_en" id="en6">
         * London Victoria is the second busiest with 81.2 million passengers.
         * </div>
         * <div class="qh_zg" id="zg6">
         * 伦敦维多利亚站是第二繁忙的火车站，其接待旅客为8120万。
         * </div>
         * <div class="qh_en" id="en7">
         * Overall it is estimated that there were over 2.9 billion entries and exits at all rail stations in the UK.
         * </div>
         * <div class="qh_zg" id="zg7">
         * 据估计，英国所有火车站的进出站旅客总量达到了29亿人次。
         * <p></p>
         * </div>
         * </div>
         */

        public String updatedAt;
        public String objectId;
        public String createdAt;
        public String source;
        public String imageUrl;
        public String newsUrl;
        public String summary;
        public PostTimeEntity postTime;
        public String tag;
        public String newsTitle;
        public String newsContent;

        public static class PostTimeEntity implements Serializable {
            /**
             * __type : Date
             * iso : 2016-12-17T16:00:00.000Z
             */

            public String __type;
            public String iso;
        }
    }
}
