package by.bsuir.bigdata.youtube.service;

import by.bsuir.bigdata.youtube.model.VideoSearchResult;

import java.util.List;

public interface YoutubeSearchingService {

    public String simpleSearch();

    List<VideoSearchResult> searchMostPopular();
}
