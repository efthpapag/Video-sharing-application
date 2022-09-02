package gr.aueb.dsapp.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

import gr.aueb.dsapp.BackEnd.UserDao;
import gr.aueb.dsapp.R;

public class VideoRecommendations extends AppCompatActivity {

    private ListView VideoRecommendations;
    private VideoRecommendationsAdapter customeAdapter;
    //ArrayList<String> NameArrayList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_recommendations);

        VideoRecommendations = (ListView) findViewById(R.id.VideoRecommendations);

    }



    @Override
    protected void onStart() {

        super.onStart();

        //NameArrayList = populateList();
        customeAdapter = new VideoRecommendationsAdapter(VideoRecommendations.this, UserDao.user.videos_to_watch);
        VideoRecommendations.setAdapter(customeAdapter);

    }
    /*private ArrayList<String> populateList(){
        ArrayList<String> list = new ArrayList<>();
        for(int i = 0; i < UserDao.user.videos_to_watch.size(); i++){
            list.add(UserDao.user.videos_to_watch.get(i).getVideoName());
        }
        return list;
    }*/

}