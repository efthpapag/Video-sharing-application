package gr.aueb.dsapp.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import gr.aueb.dsapp.BackEnd.VideoFile;
import gr.aueb.dsapp.R;

public class VideoRecommendationsAdapter extends BaseAdapter {

    public static final String vid = "video";
    public static final String cha = "channel";
    private Context context;
    public static ArrayList<VideoFile> NameArrayList;
    public Button editButton;

    public VideoRecommendationsAdapter(Context context, ArrayList<VideoFile> NameArrayList) {
        this.context = context;
        this.NameArrayList = NameArrayList;
    }

    @Override
    public int getCount() {
        return NameArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return NameArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.one_button_list_item, null, true);
        editButton = convertView.findViewById(R.id.editButton);
        TextView tv = (TextView) convertView.findViewById(R.id.itemName);
        tv.setText(NameArrayList.get(position).getVideoName());

        editButton.setText("Play");

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoToPlayName = NameArrayList.get(position).getVideoName();
                String videoToPlayChannel = NameArrayList.get(position).getChannelName();
                Intent intent = new Intent(context, PlayVideo.class);
                intent.putExtra(vid, videoToPlayName);
                intent.putExtra(cha, videoToPlayChannel);
                context.startActivity(intent);
            }
        });
        return convertView;
    }
}
