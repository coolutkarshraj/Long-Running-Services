package com.utkarsh.long_running_services.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.utkarsh.long_running_services.Constant;
import com.utkarsh.long_running_services.services.PeriodicNotifServices;
import com.utkarsh.long_running_services.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.tvServiceStat)
    TextView tvServiceStat;
    private Intent mServiceIntent;
    ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        startServiceOnEveryTwoMin();
    }

    /*
     * Method first check
     * whether service is running or not
     * by calling isMyServiceRunning() Method
     * if service not running it will start service
     * */
    private void startServiceOnEveryTwoMin() {
        mServiceIntent = new Intent(this, PeriodicNotifServices.class);
        if (!isMyServiceRunning(PeriodicNotifServices.class)) {
            startService(mServiceIntent);
            tvServiceStat.setText("Service Started");
        } else {
            tvServiceStat.setText("Service Running");
        }
    }

    /*
     * Method for initialization
     * of view constant
     * */
    private void initView() {
        Constant.forceStoped = false;
        ButterKnife.bind(this);
    }

    /*
     * Medhod for checking wether service is running or not
     * */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /*
     * All Click Event Handled here
     * */
    @OnClick({R.id.btnStopService})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnStopService:
                stopNotificService();
                break;
        }
    }

    /*
     *First check service is running or not
     * if running then this
     * Method for stopping our running services
     * */
    private void stopNotificService() {
        if (!isMyServiceRunning(PeriodicNotifServices.class)) {
            Constant.forceStoped = true;
            Intent intent = new Intent(MainActivity.this, PeriodicNotifServices.class);
            stopService(intent);
            tvServiceStat.setText("Service Stopped");
        } else {
            //Do Nothing
        }

    }

}