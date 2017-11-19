package by.bsuir.bigdata.youtube.model;

import com.google.api.client.util.DateTime;

public class VideoSearchResult {

    private String channelName;
    private String videoName;
    private DateTime uploadDate;

    public VideoSearchResult(String channelName, String videoName, DateTime uploadDate) {

        this.channelName = channelName;
        this.videoName = videoName;
        this.uploadDate = uploadDate;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public DateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(DateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    @Override
    public String toString() {
        return "VideoSearchResult{" +
                "channelName='" + channelName + '\'' +
                ", videoName='" + videoName + '\'' +
                ", uploadDate='" + uploadDate + '\'' +
                '}';
    }
}
