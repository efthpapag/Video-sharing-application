package gr.aueb.dsapp.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import gr.aueb.dsapp.BackEnd.UserDao;
import gr.aueb.dsapp.BackEnd.VideoFile;
import gr.aueb.dsapp.R;

public class PlayVideo extends AppCompatActivity {

    public static final String vid = "video";
    public static final String cha = "channel";
    Button buttondDownload;
    VideoView playVid;
    VideoFile vidToPlay = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        buttondDownload = findViewById(R.id.buttondDownload);
        playVid = findViewById(R.id.playVid);

        Intent intent = getIntent();
        String videoToPlayName = intent.getStringExtra(VideoRecommendationsAdapter.vid);
        String videoToPlayChannel = intent.getStringExtra(VideoRecommendationsAdapter.cha);


        for (VideoFile v : UserDao.user.videos_to_watch){
            if (v.getVideoName().equals(videoToPlayName) && v.getChannelName().equals(videoToPlayChannel)){
                vidToPlay = v;
                break;
            }
        }

        try {
            FileOutputStream out1 = new FileOutputStream("/sdcard/Download/video.mp4");
            out1.write(vidToPlay.getVideoFileChunk());
            out1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }




        /*FileOutputStream out = null;
        try {
            out = new FileOutputStream("/sdcard/Download/video.mp4");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            out.write(vidToPlay.getVideoFileChunk());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        String path = ("/sdcard/Download/video.mp4");
        Uri uri = Uri.parse(path);
        playVid.setVideoURI(uri);

        MediaController mediaController = new MediaController(this);
        playVid.setMediaController(mediaController);
        mediaController.setAnchorView(playVid);

        buttondDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FileOutputStream out2 = new FileOutputStream("/sdcard/Download/" + vidToPlay.getVideoName() + ".mp4");
                    out2.write(vidToPlay.getVideoFileChunk());
                    out2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        File f = new File("/sdcard/Download/video.mp4");
        f.delete();
    }

}