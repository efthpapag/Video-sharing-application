package gr.aueb.dsapp.BackEnd;

import java.util.ArrayList;
import java.io.Serializable;

public class VideoFile implements Serializable{
    private String videoName;
    private String channelName;
    private String dateCreated;
    private String length;
    private String framerate;
    private String frameWidth;
    private String frameHeight;
    private ArrayList<String> associatedHashtags;
    private byte[] videoFileChunk;

    VideoFile(String videoname, String channelName, String dateCreated, String length, String framerate, String frameWidth, String frameHeight, ArrayList<String> associatedHashtags, byte[] videoFileChunk){
        this.videoName = videoname;
        this.channelName = channelName;
        this.dateCreated = dateCreated;
        this.length = length;
        this.framerate = framerate;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.associatedHashtags = associatedHashtags;
        this.videoFileChunk = videoFileChunk;
    }

    VideoFile(VideoFile vid){
        this.videoName = vid.getVideoName();
        this.channelName = vid.getChannelName();
        this.dateCreated = vid.getDateCreated();
        this.length = vid.getLength();
        this.framerate = vid.getFramerate();
        this.frameWidth = vid.getFrameWidth();
        this.frameHeight = vid.getFrameHeight();
        this.associatedHashtags = vid.getAssociatedHashtags();
        this.videoFileChunk = vid.getVideoFileChunk().clone();
    }

    public String getVideoName() {
        return videoName;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getFramerate() {
        return framerate;
    }

    public String getLength() {
        return length;
    }

    public String getFrameWidth() {
        return frameWidth;
    }

    public String getFrameHeight() {
        return frameHeight;
    }

    public ArrayList<String> getAssociatedHashtags() {
        return associatedHashtags;
    }

    public byte[] getVideoFileChunk() {
        return videoFileChunk;
    }

    public void setVideoFileChunk(byte[] videoFileChunk) {
        this.videoFileChunk = videoFileChunk;
    }
}