package by.bsuir.bigdata.youtube.authentication;

import by.bsuir.bigdata.youtube.exception.YoutubeException;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtubeAnalytics.YouTubeAnalytics;
import com.google.api.services.youtubeAnalytics.YouTubeAnalyticsScopes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

@Configuration
public class YoutubeSecurityConfiguration {

    //TODO: deal with application.properties injection
//    @Value("${youtube.datastore.credentials.filename}")
    private String keysFileName = "client_secret.json";

//    @Value("${youtube.datastore.userdir}")
    private String userDir = "user.home";

//    @Value("${youtube.datastore.credentials.dir}")
    private String credentialsDir = ".credentials/youtube-java-quickstart";

//    @Value("${youtube.application.name}")
    private String appName = "API Sample";

    @Autowired
    private ResourceLoader resourceLoader;

    private List<String> youtubeScopes = Arrays.asList(YouTubeScopes.YOUTUBE_READONLY,
            YouTubeAnalyticsScopes.YOUTUBE_READONLY);

    @Bean
    public YouTube youTubeAccessor() {
        Credential credential = authorize();
        return new YouTube.Builder(trustedTransport(), JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName(appName)
                .build();
    }

    @Bean
    public YouTubeAnalytics youTubeAnalyticsAccessor() {
        Credential credential = authorize();
        return new YouTubeAnalytics.Builder(trustedTransport(), JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName("Analytics API App")
                .build();
    }

    private Credential authorize() {
        try {
            JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            // Load client secrets.
            InputStream in = resourceLoader.getResource("classpath:" + keysFileName).getInputStream();
            GoogleClientSecrets clientSecrets =
                    GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));

            // Build flow and trigger user authorization request.
            GoogleAuthorizationCodeFlow flow =
                    new GoogleAuthorizationCodeFlow.Builder(trustedTransport(), jsonFactory, clientSecrets, youtubeScopes)
                            .setDataStoreFactory(credentialsFile())
                            .setAccessType("offline")
                            .build();
            return new AuthorizationCodeInstalledApp(
                    flow, new LocalServerReceiver()).authorize("user");
        } catch (IOException e) {
            throw new YoutubeException("Error while authenticating", e);
        }
    }

    @Bean
    public FileDataStoreFactory credentialsFile() {
        try {
            return new FileDataStoreFactory(new File(System.getProperty(userDir), credentialsDir));
        } catch (IOException e) {
            throw new YoutubeException("Error while authenticating", e);
        }
    }

    @Bean
    public NetHttpTransport trustedTransport() {
        try {
            return GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException e) {
            throw new YoutubeException("Security error occurred", e);
        } catch (IOException e) {
            throw new YoutubeException("Error while authenticating", e);
        }
    }
}
