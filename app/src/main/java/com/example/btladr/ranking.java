package com.example.btladr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ranking extends AppCompatActivity {
    Connection connection;
    ResultSet resultSet;
    Statement statement;
    String queryToGetPoints="SELECT TOP 5 userName, point, loginDay FROM userRecords ORDER BY point DESC, loginDay DESC;";

    ListView listView;
    private List<User> userList;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ranking);
        listView=findViewById(R.id.listViewRanking);
        resultSet=startConnectt();
        setUserToRanking();

        ImageView imgView=findViewById(R.id.back_to_main);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ranking.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    //set d liệu lấy được từ resultSet vào 1 userList và thể hiện nó lên ListView
    private void setUserToRanking(){
        userList=new ArrayList<>();
        try {
            resultSet.last();
            int rows=resultSet.getRow();
            resultSet.beforeFirst();
            if(resultSet.next()){
                while(userList.size()<rows) {
                    int i=0;
                    userList.add(new User(resultSet.getString("userName"),resultSet.getInt("point"),resultSet.getDate("loginDay").toString()));
                    resultSet.next();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        userAdapter = new UserAdapter(this, userList);
        listView.setAdapter(userAdapter);
    }

    //kết nối đến CSDL
    private ResultSet startConnectt(){
        ConnectionSQL c=new ConnectionSQL();
        connection= c.conclass();
        if (connection != null) {
            try {
                statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                resultSet = statement.executeQuery(queryToGetPoints);
            } catch (Exception e) {
                Log.e("Error:", e.getMessage());
            }
        }
        return resultSet;

    }
}