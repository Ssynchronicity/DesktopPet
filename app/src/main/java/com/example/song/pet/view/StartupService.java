package com.example.song.pet.view;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;

import com.example.song.pet.FloatWindowService;

/**
 * Created by MR on 2018/7/7.
 */
public class StartupService extends JobService {
    private int kJobId = 0;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("MyJobDaemonService", "jobService启动");

        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("MyJobDaemonService", "执行了onStartJob方法");
        this.startService(new Intent(this,FloatWindowService.class));
//            this.startService(new Intent(this,Service2.class));
//            Toast.makeText(this, "进程启动", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("MyJobDaemonService", "执行了onStopJob方法");

        return true;
    }


}
