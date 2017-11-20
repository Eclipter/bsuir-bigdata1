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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BigDataSearchingJob {

    private static final Logger LOG = LoggerFactory.getLogger(BigDataSearchingJob.class);
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss.SSS");
    private static final String DOWNLOAD_PATH = System.getProperty("user.home") + "/youtube_download/";
    private static final String TARGET_REGION_CODE = "RU";

    @Autowired
    private YoutubeSearchingService youtubeSearchingService;

    @Autowired
    private HadoopAggregationService aggregationService;


    public void reportCurrentTime() {
        LOG.info("The time is now {}", ZonedDateTime.now().format(DATE_TIME_FORMAT));
    }


    public void writeStatistics() {
        List<VideoSearchResult> videoSearchResults = youtubeSearchingService.searchMostPopular(TARGET_REGION_CODE);
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

        List<VideoSearchResult> videoSearchResults = youtubeSearchingService.searchMostPopular(TARGET_REGION_CODE);

        LOG.info("Retrieving hipers for channels");

        videoSearchResults.forEach(videoSearchResult -> {

            List<String> searchResults = retrieveChannelNames(youtubeSearchingService
                    .searchRelated(videoSearchResult.getVideoName(), videoSearchResult.getChannelName(),
                            videoSearchResult.getUploadDate()));

            writeToFile(searchResults);
        });

        LOG.info("Aggregating channels");

        aggregationService.aggregate(DOWNLOAD_PATH + "input/", DOWNLOAD_PATH + "output/");

        LOG.info("Operation completed successfully");
    }

    private List<String> retrieveChannelNames(List<VideoSearchResult> videoSearchResults) {
        return videoSearchResults.stream()
                .map(VideoSearchResult::getChannelName)
                .collect(Collectors.toList());
    }

    private void writeToFile(List<String> strings) {
        try {
            Path path = Paths.get(DOWNLOAD_PATH + "input/input" +
                    ZonedDateTime.now().format(DATE_TIME_FORMAT) + ".txt");
            Files.write(path, strings, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new BigDataServiceException("Error while aggregating results", e);
        }
    }

    private void cleanup() {

    }
}
