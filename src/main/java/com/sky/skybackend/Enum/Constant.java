package com.sky.skybackend.Enum;

public class Constant {
    public static final int DEFAULT_TAGS_COUNT = 5;
    public static final String DEFAULT_VIDEOS_COUNT = "10";
    public static final int REVIEW_VIDEOS_STATUS = 0;
    public static final int SEARCH_VIDEOS_LIMIT = 1000;
    public static final int SEARCH_DEFAULT_VIDEO_ID = 1;
    public static final int BATCH_SIZE = 1000;
    public static final String REDIS_HOT_RANK_KEY = "video:hot_rank";
    public static final String FEED_INBOX_PREFIX = "feed:inbox:";
    public static final String HOT_VIDEO = "hot:video:";
    public static final double DEFAULT_HOT_VIDEO_SCORE = 5000.0;
}
