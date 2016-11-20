package com.each.www.each.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;


import com.each.www.each.R;
import com.each.www.each.utils.Constants;

import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import java.io.File;



public class DownloadService extends Service {

    private String mDownloadUrl;//APK的下载路径
    private NotificationManager mNotificationManager;
    private Notification mNotification;


    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager =
                (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null){
            notifyMsg("温馨提醒", "文件下载失败", 0);
            stopSelf();
        }
        mDownloadUrl = intent.getStringExtra("apkUrl");//获取下载APK的链接
        downloadFile(mDownloadUrl);//下载APK
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        throw null;
    }

    private void notifyMsg(String title, String content, int progress) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);//为了向下兼容，这里采用了v7包下的NotificationCompat来构造
        builder.setSmallIcon(R.drawable.login_point)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)).setContentTitle(title);
        if (progress > 0 && progress < 100) {
            //下载进行中
            builder.setProgress(100, progress, false);
        } else {
            builder.setProgress(0, 0, false);
        }
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentText(content);
        if (progress >= 100) {
            //下载完成
            builder.setContentIntent(getInstallIntent());
            Log.e("TAG","nima");
        }
        mNotification = builder.build();
        mNotificationManager.notify(0, mNotification);
    }
    /**
     * 安装apk文件
     *
     * @return
     */
    private PendingIntent getInstallIntent() {
        Log.e("TAG","PendingIntent");
        File file = new File(Constants.DOWNLOAD_DIR + "test.apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + file.getAbsolutePath()),
                "application/vnd.android.package-archive");
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
    /**
     * 下载apk文件
     *
     * @param url
     */
    private void downloadFile(String url) {

        OkHttpUtils.get().url(url).build()
                .execute(new FileCallBack(Constants.DOWNLOAD_DIR, "test.apk") {
                    @Override
                    public void inProgress(float progress) {
                        //progress*100为当前文件下载进度，total为文件大小
                        if ((int) (progress * 100) % 10 == 0) {
                            //避免频繁刷新View，这里设置每下载10%提醒更新一次进度
                            notifyMsg("温馨提醒", "文件正在下载..", (int) (progress * 100));
                        }
                    }


                    @Override
                    public void onError(Request request, Exception e) {
                        notifyMsg("温馨提醒", "文件下载失败", 0);
                        stopSelf();
                    }

                    @Override
                    public void onResponse(File response) {
                        //当文件下载完成后回调
                        notifyMsg("温馨提醒", "文件下载已完成", 100);
                        stopSelf();
                    }
                });
    }
}
