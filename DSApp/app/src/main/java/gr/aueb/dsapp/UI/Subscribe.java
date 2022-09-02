package gr.aueb.dsapp.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import gr.aueb.dsapp.BackEnd.UserDao;
import gr.aueb.dsapp.R;

public class Subscribe extends AppCompatActivity {

    EditText editSearch;
    Button buttonSubscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        editSearch = findViewById(R.id.editSearch);
        buttonSubscribe = findViewById(R.id.buttonSubscribe);
    }

    @Override
    protected void onStart() {
        super.onStart();

        buttonSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new SubscribeRunner().execute(editSearch.getText().toString()).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!UserDao.user.videos_to_watch.isEmpty()){
                    Intent intent = new Intent(Subscribe.this, VideoRecommendations.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(Subscribe.this, "Hashtag or Channel not found", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private class SubscribeRunner extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params){
            UserDao.user.subscribe(params[0]);
            return "ok";
        }
    }

}