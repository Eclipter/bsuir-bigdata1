package by.bsuir.bigdata.youtube.service.impl;

import by.bsuir.bigdata.youtube.exception.YoutubeException;
import by.bsuir.bigdata.youtube.model.VideoSearchResult;
import by.bsuir.bigdata.youtube.service.YoutubeSearchingService;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtubeAnalytics.YouTubeAnalytics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class YoutubeSearchingServiceImpl implements YoutubeSearchingService {

    private static final Long MAX_SEARCH_RESULTS = 50L;

    @Autowired
    private YouTube youTubeDataAccessor;

    @Autowired
    private YouTubeAnalytics youTubeAnalyticsAccessor;

    @Override
    public String simpleSearch() {
        try {
            YouTube.Channels.List channelsListByUsernameRequest =
                    youTubeDataAccessor.channels().list("snippet,contentDetails,statistics");
            channelsListByUsernameRequest.setForUsername("GoogleDevelopers");

            ChannelListResponse response = channelsListByUsernameRequest.execute();
            Channel channel = response.getItems().get(0);

            return String.format("This channel's ID is %s. Its title is '%s', and it has %s views.\n",
                    channel.getId(),
                    channel.getSnippet().getTitle(),
                    channel.getStatistics().getViewCount());
        } catch (IOException e) {
            throw new YoutubeException("Error while retrieving the data", e);
        }
    }

    @Override
    public List<VideoSearchResult> searchMostPopular(String regionCode) {
        try {
            YouTube.Videos.List request = youTubeDataAccessor.videos().list("snippet");
            request.setChart("mostPopular");
            request.setMaxResults(MAX_SEARCH_RESULTS);
            request.setRegionCode(regionCode);

            return request.execute().getItems().stream().map(video ->
                    new VideoSearchResult(video.getSnippet().getChannelTitle(),
                            video.getSnippet().getTitle(),
                            video.getSnippet().getPublishedAt()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new YoutubeException("Error while retrieving most popular videos", e);
        }
    }

    @Override
    public List<VideoSearchResult> searchRelated(String videoTitle, String uploaderName, DateTime effectiveDate) {
        try {
            YouTube.Search.List request = youTubeDataAccessor.search().list("snippet");
            request.setQ(videoTitle + " -" + uploaderName);
            request.setType("video");
            request.setOrder("date");
            request.setPublishedAfter(effectiveDate);
            request.setMaxResults(MAX_SEARCH_RESULTS);

            return request.execute().getItems().stream().map(video ->
                    new VideoSearchResult(video.getSnippet().getChannelTitle(),
                            video.getSnippet().getTitle(),
                            video.getSnippet().getPublishedAt()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new YoutubeException("Error while searching videos", e);
        }
    }
}
