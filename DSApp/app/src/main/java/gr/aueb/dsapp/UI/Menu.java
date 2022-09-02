package gr.aueb.dsapp.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.ExecutionException;

import gr.aueb.dsapp.BackEnd.User;
import gr.aueb.dsapp.BackEnd.UserDao;
import gr.aueb.dsapp.R;

public class Menu extends AppCompatActivity {

    public Button buttonSubscribe;
    public Button buttonPublish;
    public Button buttonRemove;
    public Button buttonClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        buttonSubscribe = findViewById(R.id.buttonSubscribe);
        buttonPublish = findViewById(R.id.buttonPublish);
        buttonRemove = findViewById(R.id.buttonRemove);
        buttonClose = findViewById(R.id.buttonClose);
    }

    @Override
    protected void onStart() {
        super.onStart();

        UserDao.user.init("10.0.2.2", UserDao.user.port);
        //UserDao.user.connect("10.0.2.2", 3001);
        try {
            new ConnectionRunner().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //new UpdateRunner().execute();

        Thread t = UserDao.user.new Passive();
        t.start();

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, Remove.class);
                startActivity(intent);
            }
        });

        buttonSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, Subscribe.class);
                startActivity(intent);
            }
        });


        buttonPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, PublishVideo.class);
                startActivity(intent);
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //UserDao.user.disconnect();
                try {
                    new DisconnectRunner().execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
                System.exit(0);
            }
        });
    }

    private class ConnectionRunner extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids){
            UserDao.user.connect("10.0.2.2", 3001);
            return null;
        }
    }

    private class DisconnectRunner extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids){
            UserDao.user.disconnect();
            return null;
        }
    }

    /*private class UpdateRunner extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids){
            Thread t = UserDao.user.new Passive();
            t.start();
            return null;
        }
    }*/

}