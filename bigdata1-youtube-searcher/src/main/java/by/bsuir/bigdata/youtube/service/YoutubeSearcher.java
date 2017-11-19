package by.bsuir.bigdata.youtube.service;

import com.google.api.services.youtube.model.VideoListResponse;

public interface YoutubeSearcher {

    public String simpleSearch();

    VideoListResponse searchMostPopular();
}
