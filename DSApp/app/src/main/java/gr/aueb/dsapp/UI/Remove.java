package gr.aueb.dsapp.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Vector;

import gr.aueb.dsapp.BackEnd.UserDao;
import gr.aueb.dsapp.BackEnd.Value;
import gr.aueb.dsapp.R;

public class Remove extends AppCompatActivity {

    private ListView listViewRemove;
    private RemoveAdapter customeAdapter;
    ArrayList<String> NameArrayList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove);

        listViewRemove = (ListView) findViewById(R.id.listViewRemove);

    }



    @Override
    protected void onStart() {

        super.onStart();

        NameArrayList = populateList();
        customeAdapter = new RemoveAdapter(Remove.this, NameArrayList);
        listViewRemove.setAdapter(customeAdapter);
        customeAdapter.notifyDataSetChanged();

    }
    private ArrayList<String> populateList(){
        ArrayList<String> list = new ArrayList<>();
        for(String s : UserDao.user.userVideoFilesMap.keySet()){
            for (Value v : UserDao.user.userVideoFilesMap.get(s)){
                list.add(v.getVideoFile().getVideoName());
            }
        }
        return list;
    }

}