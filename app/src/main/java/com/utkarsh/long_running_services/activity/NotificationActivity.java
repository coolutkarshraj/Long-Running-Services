package com.utkarsh.long_running_services.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import com.utkarsh.long_running_services.R;

import java.util.Timer;
import java.util.TimerTask;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotificationActivity extends AppCompatActivity {
    private MediaPlayer music;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initView();
        startMusic();

    }

    /*
     * Method for start beeping music
     * for every fix interval of time
     * it will run periodically on every 2 sec
     * */
    private void startMusic() {
        timer = new Timer("MetronomeTimer", true);
        TimerTask tone = new TimerTask() {
            @Override
            public void run() {
                //Play sound
                music = MediaPlayer.create(NotificationActivity.this, R.raw.beep);
                music.start();
            }
        };
        timer.scheduleAtFixedRate(tone, 2000, 2000); // every 2 sec
    }

    /*
     * Method for initialization
     * of view constant
     * */
    private void initView() {
        ButterKnife.bind(this);
    }

    /*
     * Life cycle back pressed method
     * for stoping music or timer
     * want music to play after back pressed comment
     *   music.stop();
     *  timer.cancel();
     * */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        music.stop();
        timer.cancel();
        finish();
    }

    /*
     * All Click Event Handled here
     * */
    @OnClick({R.id.btnStopMusic})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnStopMusic:
                timer.cancel();
                music.stop();
                break;
        }
    }

    /*
     * Life cycle pause method
     * for stoping music or timer
     * want music to play after app is in pause state comment
     *   music.stop();
     *  timer.cancel();
     * */
    @Override
    protected void onPause() {
        super.onPause();
        music.stop();
        timer.cancel();

    }
}