package gr.aueb.dsapp.UI;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import gr.aueb.dsapp.BackEnd.UserDao;
import gr.aueb.dsapp.BackEnd.VideoFile;
import gr.aueb.dsapp.R;

public class PublishVideo extends AppCompatActivity {

    public Button buttonCapture;
    public Button buttonAddHashtag;
    public Button buttonPublishVideo;
    public EditText editVideoName;
    public EditText editTextHashtag;
    VideoFile playVid = null;
    public int CAPTURE_VIDEO_REQUEST_CODE = 1;
    public static final int TAKE_VIDEO=0;
    private static final int ACTION_TAKE_VIDEO = 3;

    ArrayList<String> hashtags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_video);

        buttonCapture = findViewById(R.id.buttonCapture);
        buttonAddHashtag = findViewById(R.id.buttonAddHashtag);
        buttonPublishVideo = findViewById(R.id.buttonPublishVideo);
        editTextHashtag = findViewById(R.id.editTextHashtag);
        editVideoName = findViewById(R.id.editVideoName);

    }

    @Override
    protected void onStart() {
        super.onStart();

        hashtags = new ArrayList<String>();

        buttonCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(PublishVideo.this, CaptureVideo.class);
                //Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                //startActivityForResult(intent, 1);
                //startActivity(intent);
                //finish();
                //Intent photoPickerIntent= new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                //startActivityForResult(Intent.createChooser(photoPickerIntent,"Take Video"),TAKE_VIDEO);



                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);

            }
        });


        buttonAddHashtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hashtags.add(editTextHashtag.getText().toString());
                editTextHashtag.setText("");
            }
        });

        buttonPublishVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editVideoName.getText().toString();
                String path = "/sdcard/Download/" + name + ".mp4";
                try {
                    new PublishRunner().execute(path, name).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });

    }


    //@Override
    //public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      //  super.onActivityResult(requestCode, resultCode, data);



        //if (resultCode == RESULT_OK && requestCode == 1){
            //AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //VideoView videoView = new VideoView(this);
            //videoView.setVideoURI(data.getData());
            //videoView.start();
            //builder.setView(videoView).show();

            //Uri uri = data.getData();
            //File videoFile = getFilePath();
          //  Uri videoUri = Uri.fromFile(videoFile);


        //}
    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == TAKE_VIDEO) {


                try {
                    Log.e("videopath", "videopath");
                    AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(data.getData(), "r");
                    FileInputStream fis = videoAsset.createInputStream();
                    File root = new File(Environment.getExternalStorageDirectory(), "Directory Name");

                    if (!root.exists()) {
                        root.mkdirs();
                    }

                    File file;
                    file = new File(root, "android_" + System.currentTimeMillis() + ".mp4");

                    FileOutputStream fos = new FileOutputStream(file);

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = fis.read(buf)) > 0) {
                        fos.write(buf, 0, len);
                    }
                    fis.close();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        String name = editVideoName.getText().toString();

        if (resultCode == RESULT_OK) {
            try {
                AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(data.getData(), "r");
                FileInputStream fis = videoAsset.createInputStream();
                File root = new File(Environment.getExternalStorageDirectory(), "/Download/" /*+ name + ".mp4"*/);
                if (!root.exists()) {
                    System.out.println("No directory");
                    root.mkdirs();
                }

                File file;
                file = new File(root, name + ".mp4");

                FileOutputStream fos = new FileOutputStream(file);

                byte[] buf = new byte[1024];
                int len;
                while ((len = fis.read(buf)) > 0) {
                    fos.write(buf, 0, len);
                }
                fis.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String path = "/sdcard/Download/" + name + ".mp4";

            try {
                new PublishRunner().execute(path, name).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finish();
        }
    }

    //public File getFilePath(){
      //  File folder = new File( "/sdcard/Download/");
    //}


    private class PublishRunner extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params){
            UserDao.user.publishVideo(params[0], params[1], hashtags);
            return "ok";
        }

    }

}