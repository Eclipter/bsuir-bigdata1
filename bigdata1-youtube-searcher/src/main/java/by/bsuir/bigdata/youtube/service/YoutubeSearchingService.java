package by.bsuir.bigdata.youtube.service;

import by.bsuir.bigdata.youtube.model.VideoSearchResult;
import com.google.api.client.util.DateTime;

import java.util.List;

public interface YoutubeSearchingService {

    public String simpleSearch();

    List<VideoSearchResult> searchMostPopular();

    List<VideoSearchResult> searchRelated(String videoTitle, String uploaderName, DateTime effectiveDate);
}
