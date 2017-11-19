package by.bsuir.bigdata.youtube.model;

public class VideoSearchResult {

    private String channelName;
    private String videoName;
    private String uploadDate;

    public VideoSearchResult(String channelName, String videoName, String uploadDate) {

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

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }
}
