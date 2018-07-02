package com.example.song.pet;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import java.io.File;


/**
 * 启动服务后开始下载文件，并在通知栏添加一个通知，显示下载进度；下载完成后移除，并创建一个下载成功的通知
 */
public class DownloadService extends Service {
    // 下载路径设为了Download文件夹
    // TODO: 将下载路径改为app内的文件夹
    private static final String DOWNLOAD_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
    private DownloadTask downloadTask;
    private String downloadUrl;
    private DownloadListener listener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            //TODO：在界面上加入进度条，在此函数内更新进度条
        }

        @Override
        public void onSuccess() {
            downloadTask = null;
            Toast.makeText(DownloadService.this, "Download Success.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNetworkFailed() {
            downloadTask = null;
            Toast.makeText(DownloadService.this, "Network error, check your network status", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStorageFailed() {
            downloadTask = null;
            Toast.makeText(DownloadService.this, "Storage error", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            downloadTask = null;
            Toast.makeText(DownloadService.this, "Download Paused", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
            downloadTask = null;
            Toast.makeText(DownloadService.this, "Download Canceled", Toast.LENGTH_SHORT).show();
        }
    };

    private DownloadBinder mBinder = new DownloadBinder();

    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
//        throw new UnsupportedOperationException("Not yet implemented");
    }

    class DownloadBinder extends Binder {
        public void startDownload(String url) {
            if (downloadTask == null) {
                downloadUrl = url;
                downloadTask = new DownloadTask(listener);
                downloadTask.execute(downloadUrl);
                Toast.makeText(DownloadService.this, "Downloading...", Toast.LENGTH_SHORT).show();
            }
        }

        public void pauseDownload() {
            if (downloadTask != null) {
                downloadTask.pauseDownload();
            }
        }

        public void cancelDownload() {
            if (downloadTask != null) {
                downloadTask.cancelDownload();
            } else {
                if (downloadUrl != null) {
                    // 取消下载时将文件删除
                    // 使用场景是用户先点击了暂停下载，此时downloadTask == null，然后又进行了取消
                    // TODO：如果只提供取消按钮，不提供暂停按钮的话，这段代码就不需要了
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    File file = new File(DOWNLOAD_PATH + fileName);
                    if (file.exists()) file.delete();
                    Toast.makeText(DownloadService.this, "Canceled", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

}
