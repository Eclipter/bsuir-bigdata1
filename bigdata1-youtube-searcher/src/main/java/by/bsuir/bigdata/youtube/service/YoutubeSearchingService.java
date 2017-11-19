package by.bsuir.bigdata.youtube.service;

import com.google.api.services.youtube.model.VideoListResponse;

public interface YoutubeSearchingService {

    public String simpleSearch();

    VideoListResponse searchMostPopular();
}
