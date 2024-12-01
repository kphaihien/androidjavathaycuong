package com.example.btladr;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class BackGroundMusic extends Service {
    private MediaPlayer mediaPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.background_sound);
        mediaPlayer.setLooping(true); // Lặp lại nhạc khi hết
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer.start(); // Bắt đầu phát nhạc
        return START_STICKY; // Giữ service tiếp tục chạy ngay cả khi activity bị hủy
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release(); // Giải phóng tài nguyên của MediaPlayer
        }
    }
}
