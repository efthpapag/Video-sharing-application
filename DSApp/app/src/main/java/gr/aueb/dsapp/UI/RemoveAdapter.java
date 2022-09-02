package gr.aueb.dsapp.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import gr.aueb.dsapp.BackEnd.UserDao;
import gr.aueb.dsapp.R;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class RemoveAdapter extends BaseAdapter {

    private Context context;
    public static ArrayList<String> NameArrayList;

    public RemoveAdapter(Context context, ArrayList<String> NameArrayList) {
        super();
        this.context = context;
        this.NameArrayList = NameArrayList;
    }

    @Override
    public int getCount() {
        return NameArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.one_button_list_item, parent, false);

        TextView itemName = convertView.findViewById(R.id.itemName);
        itemName.setText(NameArrayList.get(position));
        Button editButton = convertView.findViewById(R.id.editButton);
        editButton.setText("Delete");

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new RemoveRunner().execute(NameArrayList.get(position)).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(context, Remove.class);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
        return convertView;
    }

    private class RemoveRunner extends AsyncTask<String, String, String>
    {

        @Override
        protected String doInBackground(String... params){
            UserDao.user.removeVideo(params[0]);
            return "ok";
        }
    }

}
