package com.lws.allenglish.bean;

import java.util.List;

/**
 * Created by Wilson on 2016/12/28.
 */

public class Comprehension extends BaseEnglish {
    public List<Comprehension.ResultsEntity> results;

    public static class ResultsEntity extends BaseEnglish.ResultsEntity {
        public String audioUrl;
    }
}
