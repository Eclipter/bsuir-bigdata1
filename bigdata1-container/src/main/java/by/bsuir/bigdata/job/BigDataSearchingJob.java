package by.bsuir.bigdata.job;

import by.bsuir.bigdata.aggregation.service.HadoopAggregationService;
import by.bsuir.bigdata.exception.BigDataServiceException;
import by.bsuir.bigdata.youtube.model.VideoSearchResult;
import by.bsuir.bigdata.youtube.service.YoutubeSearchingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss-SSS");
    private static final String DOWNLOAD_PATH = System.getProperty("user.home") + "/youtube_download/";

    @Value("${country.codes}")
    private String[] regionCodes;

    @Autowired
    private YoutubeSearchingService youtubeSearchingService;

    @Autowired
    private HadoopAggregationService aggregationService;

    @PostConstruct
    public void setupDirectories() {
        Path path = Paths.get(DOWNLOAD_PATH + "input");
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new BigDataServiceException("Failed to setup directories", e);
            }
        }
    }

    public void reportCurrentTime() {
        LOG.info("The time is now {}", ZonedDateTime.now().format(DATE_TIME_FORMAT));
    }

//    @Scheduled(fixedRate = 300000)
    public void writeStatistics() {

        for(String regionCode : regionCodes) {

            LOG.info("Retrieving most popular videos for a region: {}", regionCode);

            List<VideoSearchResult> videoSearchResults = youtubeSearchingService.searchMostPopular(regionCode);
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
    }

    @Scheduled(fixedRate = 300000)
    public void searchHipers() {

        for(String regionCode : regionCodes) {

            LOG.info("Retrieving most popular videos for a region: {}", regionCode);

            final String folderPrefix = ZonedDateTime.now().format(DATE_TIME_FORMAT);

            List<VideoSearchResult> videoSearchResults = youtubeSearchingService.searchMostPopular(regionCode);

            LOG.info("Retrieving hipers for channels");

            videoSearchResults.forEach(videoSearchResult -> {

                List<String> searchResults = retrieveChannelNames(youtubeSearchingService
                        .searchRelated(videoSearchResult.getVideoName(), videoSearchResult.getChannelName(),
                                videoSearchResult.getUploadDate()));

                writeToFile(searchResults, folderPrefix);
            });

            LOG.info("Aggregating channels");

            aggregationService.aggregate(DOWNLOAD_PATH + "input/" + folderPrefix + "/",
                    DOWNLOAD_PATH + "output/" + folderPrefix + "/");

            LOG.info("Operation completed successfully");
        }
    }

    private List<String> retrieveChannelNames(List<VideoSearchResult> videoSearchResults) {
        return videoSearchResults.stream()
                .map(VideoSearchResult::getChannelName)
                .collect(Collectors.toList());
    }

    private void writeToFile(List<String> strings, String folderPrefix) {
        try {

            Path path = Paths.get(DOWNLOAD_PATH + "input/" + folderPrefix + "/input" +
                    ZonedDateTime.now().format(DATE_TIME_FORMAT) + ".txt");
            if(!Files.exists(path.getParent())) {
                Files.createDirectory(path.getParent());
            }
            Files.write(path, strings, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new BigDataServiceException("Error while aggregating results", e);
        }
    }
}
