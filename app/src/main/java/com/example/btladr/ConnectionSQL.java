package com.example.btladr;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionSQL {
    Connection con;
    //192.168.2.159
//    172.16.13.198
    @SuppressLint("NewApi")
    public Connection conclass(){
        String ip="172.16.13.198",port="1433",db="hien",username="sa",password="1";
        StrictMode.ThreadPolicy a=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(a);
        String ConnectURL=null;

        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectURL="jdbc:jtds:sqlserver://"+ip+":"+port+";"+"databasename="+db+";user="+username+";"+"password="+password+";";
            con= DriverManager.getConnection(ConnectURL);
        }catch (Exception e){
            Log.e("Error is:",e.getMessage());
        }
        return con;
    }
}
