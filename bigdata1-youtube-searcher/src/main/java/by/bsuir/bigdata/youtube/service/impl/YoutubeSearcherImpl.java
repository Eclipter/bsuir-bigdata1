package by.bsuir.bigdata.youtube.service.impl;

import by.bsuir.bigdata.youtube.exception.YoutubeException;
import by.bsuir.bigdata.youtube.service.YoutubeSearcher;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtubeAnalytics.YouTubeAnalytics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class YoutubeSearcherImpl implements YoutubeSearcher {

    private static final Long MAX_SEARCH_RESULTS = 50L;
    private static final String TARGET_REGION_CODE = "RU";
    public static final String DOWNLOAD_PATH = System.getProperty("user.home") + "/youtube_download/input.txt";

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
    public VideoListResponse searchMostPopular() {
        try {
            YouTube.Videos.List request = youTubeDataAccessor.videos().list("snippet,contentDetails,statistics");
            request.setChart("mostPopular");
            request.setMaxResults(MAX_SEARCH_RESULTS);
            request.setRegionCode(TARGET_REGION_CODE);

            return request.execute();
        } catch (IOException e) {
            throw new YoutubeException("Error while retrieving most popular videos", e);
        }
    }
}
