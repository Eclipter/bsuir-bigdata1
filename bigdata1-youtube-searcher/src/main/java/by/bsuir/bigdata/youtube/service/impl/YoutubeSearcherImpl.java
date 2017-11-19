package by.bsuir.bigdata.youtube.service.impl;

import by.bsuir.bigdata.youtube.exception.YoutubeException;
import by.bsuir.bigdata.youtube.service.YoutubeSearcher;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class YoutubeSearcherImpl implements YoutubeSearcher {

    @Autowired
    private YouTube youTubeAccessor;

    @Override
    public String search() {
        try {
            YouTube.Channels.List channelsListByUsernameRequest =
                    youTubeAccessor.channels().list("snippet,contentDetails,statistics");
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
}
