package com.example.song.pet;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

interface DownloadListener {
    void onProgress(int progress);

    void onSuccess();

    void onNetworkFailed();

    void onStorageFailed();

    void onPaused();

    void onCanceled();
}

public class DownloadTask extends AsyncTask<String, Integer, Integer> {
    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;
    public static final int TYPE_STORAGE_ERROR = 4;
    public static final int TYPE_NETWORK_ERROR = 5;

    // 下载路径设为了Download文件夹
    // TODO: 将下载路径改为app内的文件夹
    private static final String DOWNLOAD_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();


    private DownloadListener listener;
    private boolean isCanceled = false;
    private boolean isPaused = false;
    private int lastProgress;

    public DownloadTask(DownloadListener listener) {
        this.listener = listener;
    }

    @Override
    protected Integer doInBackground(String... params) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File file = null;
        long downloadedLength = 0;
        String downloadUrl = params[0];
        String fileName = downloadUrl.substring((downloadUrl.lastIndexOf("/")));
        file = new File(DOWNLOAD_PATH + fileName);
        if (file.exists()) {
            // 如果文件已下载，获取已下载的长度
            downloadedLength = file.length();
        }
        long contentLength = getContentLength(downloadUrl);
        if (contentLength == -1) {
            return TYPE_NETWORK_ERROR;
        } else if (contentLength == downloadedLength) {
            // 如果服务器上的文件长度和已下载的文件长度相同，则不用重新下载了
            //TODO: unzip
            return TYPE_SUCCESS;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                .url(downloadUrl)
                .build();
        Response response = null;
        boolean getResponseSuccess = false;
        try {
            response = client.newCall(request).execute();
            if (response != null && response.isSuccessful())
                getResponseSuccess = true;
        } catch (IOException e) {
            e.printStackTrace();
            return TYPE_NETWORK_ERROR;
        }
        if (!getResponseSuccess) return TYPE_NETWORK_ERROR;

        is = response.body().byteStream();
        try {
            savedFile = new RandomAccessFile(file, "rw");
            savedFile.seek(downloadedLength);  // 跳过已下载的字节
            byte[] b = new byte[1024];
            int total = 0;
            int len;
            while ((len = is.read(b)) != -1) {
                if (isCanceled) {
                    return TYPE_CANCELED;
                } else if (isPaused) {
                    return TYPE_PAUSED;
                } else {
                    total += len;
                    savedFile.write(b, 0, len);
                    int progress = (int) ((total + downloadedLength) * 100 / contentLength); // 已下载的百分比
                    publishProgress(progress);
                }
            }
            response.body().close();

            // 将下载之后的zip解压至同级目录下的同名文件夹内
            savedFile.close();
            unzip(file);
            return TYPE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
                if (savedFile != null) savedFile.close();
                if (isCanceled && file != null) file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return TYPE_STORAGE_ERROR;
    }

    private void unzip(File zipFile) throws IOException {
        String parentPath = zipFile.getPath().substring(0, zipFile.getPath().lastIndexOf("/") + 1);
        InputStream is = new FileInputStream(zipFile);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
        ZipEntry ze;
        String filename;
        byte[] buffer = new byte[1024];
        int count;
        while ((ze = zis.getNextEntry()) != null) {
            filename = ze.getName();
            if (ze.isDirectory()) {
                File fmd = new File(parentPath + filename);
                fmd.mkdirs();
                continue;
            }
            FileOutputStream outs = new FileOutputStream(parentPath + filename);
            while ((count = zis.read(buffer)) != -1)
                outs.write(buffer, 0, count);
            outs.close();
            zis.closeEntry();
        }
        zis.close();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress > lastProgress) {
            listener.onProgress(progress);
            lastProgress = progress;
        }
    }

    @Override
    protected void onPostExecute(Integer status) {
        switch (status) {
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_NETWORK_ERROR:
                listener.onNetworkFailed();
                break;
            case TYPE_STORAGE_ERROR:
                listener.onStorageFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
            default:
                break;
        }
    }

    public void pauseDownload() {
        isPaused = true;
    }

    public void cancelDownload() {
        isCanceled = true;
    }

    /**
     * 获取要下载的文件的长度
     *
     * @param downloadUrl 要下载文件的url
     * @return 正常则返回文件长度，如果网络访问/服务器存在问题则返回-1
     */
    private long getContentLength(String downloadUrl) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(downloadUrl).build();
            Response response = client.newCall(request).execute();
            // 返回的HTTP状态在[200, 300)之间则为successful
            if (response != null && response.isSuccessful()) {
                long contentLength = response.body().contentLength();
                response.close();
                return contentLength;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
