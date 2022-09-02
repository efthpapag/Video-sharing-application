package gr.aueb.dsapp.BackEnd;

import java.io.Serializable;

public class Value implements Serializable{
    private VideoFile videoFile;

    Value(VideoFile videoFile){
        this.videoFile = videoFile;
    }

    public VideoFile getVideoFile() {
        return videoFile;
    }
}