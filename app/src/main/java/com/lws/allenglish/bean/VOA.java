package com.lws.allenglish.bean;

import java.util.List;

/**
 * Created by Wilson on 2016/12/18.
 */

public class VOA extends BaseEnglish {

    public List<ResultsEntity> results;

    public static class ResultsEntity extends BaseEnglish.ResultsEntity {
        public String audioUrl;
        public String subtitleType;
    }
}
