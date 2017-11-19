package by.bsuir.bigdata.job;

import by.bsuir.bigdata.youtube.service.YoutubeSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class BigDataSearchingJob {

    private static final Logger log = LoggerFactory.getLogger(BigDataSearchingJob.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    private YoutubeSearcher youtubeSearcher;

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        log.info("The time is now {}", dateFormat.format(new Date()));
    }

//    @Scheduled(fixedRate = 10000)
    public void search() {
        log.info(youtubeSearcher.simpleSearch());
    }

    @Scheduled(fixedRate = 30000)
    public void downloadMostPopular() {
        youtubeSearcher.searchMostPopular();
        log.info("Successfully downloaded the results");
    }
}
