package by.bsuir.bigdata.job;

import by.bsuir.bigdata.aggregation.service.HadoopAggregationService;
import by.bsuir.bigdata.exception.BigDataServiceException;
import by.bsuir.bigdata.youtube.model.VideoSearchResult;
import by.bsuir.bigdata.youtube.service.YoutubeSearchingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BigDataSearchingJob {

    private static final Logger LOG = LoggerFactory.getLogger(BigDataSearchingJob.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss.SSS");
    private static final DateTimeFormatter ENAHANCED_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss.SSS");
    private static final String DOWNLOAD_PATH = System.getProperty("user.home") + "/youtube_download/";

    @Autowired
    private YoutubeSearchingService youtubeSearchingService;

    @Autowired
    private HadoopAggregationService aggregationService;


    public void reportCurrentTime() {
        LOG.info("The time is now {}", DATE_FORMAT.format(new Date()));
    }


    public void writeStatistics() {
        List<VideoSearchResult> videoSearchResults = youtubeSearchingService.searchMostPopular();
        LOG.info("Successfully downloaded the results");

        try {
            final PrintWriter fileWriter =
                    new PrintWriter(new BufferedOutputStream(
                            new FileOutputStream(new File(DOWNLOAD_PATH + "input.txt"))));

            videoSearchResults.stream().forEach(videoSearchResult -> {
                List<VideoSearchResult> searchResults = youtubeSearchingService
                        .searchRelated(videoSearchResult.getVideoName(), videoSearchResult.getChannelName(),
                                videoSearchResult.getUploadDate());

                fileWriter.println("Channel: " + videoSearchResult.getChannelName() + ", Video: " +
                        videoSearchResult.getVideoName());

                searchResults.forEach(result -> {
                    fileWriter.println(result.getChannelName() + "; " + result.getVideoName());
                });
                fileWriter.println();
            });

            fileWriter.close();

            LOG.info("Successfully aggregated the results");
        } catch (FileNotFoundException e) {
            throw new BigDataServiceException("Error while aggregating search results", e);
        }
    }

    @Scheduled(fixedRate = 300000)
    public void searchHipers() {
        LOG.info("Retrieving most popular videos");
        List<VideoSearchResult> videoSearchResults = youtubeSearchingService.searchMostPopular();

        LOG.info("Retrieving hipers for channels");
        videoSearchResults.stream().forEach(videoSearchResult -> {
            List<String> searchResults = youtubeSearchingService
                    .searchRelated(videoSearchResult.getVideoName(), videoSearchResult.getChannelName(),
                            videoSearchResult.getUploadDate())
                    .stream()
                    .map(VideoSearchResult::getChannelName)
                    .collect(Collectors.toList());

            try {
                Path path = Paths.get(DOWNLOAD_PATH + "input/input" +
                        ZonedDateTime.now().format(ENAHANCED_DATE_TIME_FORMAT) + ".txt");
                Files.write(path, searchResults, StandardOpenOption.CREATE);
            } catch (IOException e) {
                throw new BigDataServiceException("Error while aggregating results", e);
            }
        });

        aggregationService.aggregate(DOWNLOAD_PATH + "input/", DOWNLOAD_PATH + "output/");

        LOG.info("Operation completed successfully");
    }
}
