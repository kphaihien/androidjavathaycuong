package com.example.btladr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class UserAdapter extends BaseAdapter {
    private Context context;
    private final List<User> userList;
    private final LayoutInflater inflater;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_list_view, parent, false);
        }

        // Get the current user
        User user = userList.get(position);

        // Find views by id
        TextView numberRanking = convertView.findViewById(R.id.numberRanking);
        TextView userName = convertView.findViewById(R.id.userName);
        TextView userPoints = convertView.findViewById(R.id.userPoints);
        TextView playDate = convertView.findViewById(R.id.playDate);

        // Set data to views
        numberRanking.setText((position+1)+"."); // Ranking number
        userName.setText(user.getName());// User's name
        userPoints.setText(String.valueOf(user.getPoint())); // User's points
        playDate.setText(user.getPlayDate()); // User's play date

        return convertView;
    }
}
