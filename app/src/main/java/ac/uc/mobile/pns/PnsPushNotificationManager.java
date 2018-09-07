/**
 *
 */
package ac.uc.mobile.pns;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.sz.fspmobile.app.FSPGlobalApplication;
import com.sz.fspmobile.config.UserConfig;

import ac.uc.mobile.pns.R;
import ac.uc.mobile.pns.fsp.PNSNotiService;
import ac.uc.mobile.pns.fsp.PNSPushNotificationManager;
import ac.uc.mobile.pns.util.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

/**
 * @author sz-jkkoo
 */
public class PnsPushNotificationManager extends PNSPushNotificationManager {

    @Override
    public void onMessage(String messageID, String message, String sendUseName) {
        // TODO Auto-generated method stub

        firstActivity = PnsLoginActivity.class;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        List<RunningTaskInfo> info;
        info = activityManager.getRunningTasks(1); // 해당 앱이 실행중인지 아닌지 체크하기 위해서 액티비티 매니저를 이용하여 실행중인 태스크의 1번 값을 가져와본다
        String pkgNm = info.get(0).topActivity.getPackageName();

        for (int i = 0; i < info.size(); i++) {

        }

        String result = getMsgFromURL("http://m.uc.ac.kr/WebJSON", messageID);

        String msgNotReadCnt = "";
        try {
            JSONObject jobj = new JSONObject(result);
            String jobjS = jobj.getString("result");
            JSONObject jobj2 = new JSONObject(jobjS);
            msgNotReadCnt = jobj2.getString("msgNotReadCnt");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SharedPreferences prefs2 = context.getSharedPreferences("setup", Activity.MODE_PRIVATE);
        int badgeCount = prefs2.getInt("badgeCount", 0);

        badgeCount = Integer.parseInt(msgNotReadCnt);
        Intent badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        badgeIntent.putExtra("badge_count", badgeCount);
        badgeIntent.putExtra("badge_count_package_name", context.getPackageName());
        badgeIntent.putExtra("badge_count_class_name", "ac.uc.mobile.pns.IntroSimpleActivity");
        context.sendBroadcast(badgeIntent);

        if (!context.getPackageName().equals(pkgNm)) {

            try {
                JSONObject jobj = new JSONObject(result);
                notiMessage(context, jobj);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            showToast(context, result);

        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    @Override
    public void notiMessage(FSPGlobalApplication context, JSONObject jobj) {
        // TODO Auto-generated method stub

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            JSONObject result = jobj.getJSONObject("result");

            String userNo = Util.getSharedData("userId","");
            String userGbn = Util.getSharedData("userGbn","");

            Intent intent = null;

            if (forwardIntent != null) {
                intent = forwardIntent;
            } else {
                intent = new Intent(context, firstActivity);
                if (result.has("msgScrtYn") && "Y".equals(result.getString("msgScrtYn"))) {
                    intent.putExtra("popupUrl", "/mobile/scrt.html?msg_no=" + result.getString("msgNo") + "&msg_ctnt=" + result.getString("msgCtnt") + "&user_no=" + userNo + "&user_gbn=" + userGbn);
                    intent.putExtra("page", "scrt");
                } else {
                    intent.putExtra("popupUrl", "/mobile/view.html?msg_no=" + result.getString("msgNo") + "&msg_ctnt=" + result.getString("msgCtnt") + "&user_no=" + userNo + "&user_gbn=" + userGbn);
                    intent.putExtra("page", "detail");
                }
            }

            if (isForeground(context, context.getPackageName())) {
                Util.setSharedData("comWebViewUrl", "/mobile/view.html?msg_no=" + result.getString("msgNo") + "&msg_ctnt=" + result.getString("msgCtnt") + "&user_no=" + userNo + "&user_gbn=" + userGbn);
            }

            Intent svrIntent = new Intent(context, PNSNotiService.class);
            svrIntent.putExtra("noti", "detail");

            PendingIntent pi1 = PendingIntent.getActivity(context, 0, intent, Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pi2 = PendingIntent.getService(context, 0, svrIntent, Intent.FLAG_ACTIVITY_CLEAR_TOP);

            String senderImg = "";
            String senderNm = "";
            String senderCls = "";

            if ("T".equals(result.getString("msgSendGbn"))) {
                senderImg = result.getString("pnsImg");
                senderCls = result.getString("pnsLcNm");
                senderNm = result.getString("pnsNm");
            } else {
                senderImg = result.getString("senderImg");
                senderCls = result.getString("mngDept");
                senderNm = result.getString("mngNm");
            }

            Bitmap senderBmp = getBitmapFromURL(senderImg);

            Uri soundUri = null;
            String appPath = context.getFilesDir().getAbsolutePath();

            if (!"999".equals(Util.getSharedData("userAlmNo",""))) {
                AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                    if("998".equals(Util.getSharedData("userAlmNo",""))) {
                        Vibrator v = (Vibrator) context.getSystemService(Activity.VIBRATOR_SERVICE);
                        v.vibrate(1000);
                    } else if (!"".equals(Util.getSharedData("userAlmFileNm",""))) {
                        String soundFilepath = getSoundFileFromURL(appPath, Util.getSharedData("userAlmFileNm",""), Util.getSharedData("userAlmPath",""));
                        soundUri = Uri.parse("file://" + soundFilepath);
                    } else {
                        if (result.has("msgAlm") && result.getString("msgAlm") != null && !"".equals(result.getString("msgAlm"))) {
                            String[] fileNmSper = result.getString("msgAlm").split("/");
                            String soundFilepath = getSoundFileFromURL(appPath, fileNmSper[fileNmSper.length - 1], result.getString("msgAlm"));
                            if (soundFilepath != null)
                                soundUri = Uri.parse("file://" + soundFilepath);
                            else
                                soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        } else {
                            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        }
                    }
                }


                if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                    Vibrator v = (Vibrator) context.getSystemService(Activity.VIBRATOR_SERVICE);
                    v.vibrate(1000);
                }

                if (soundUri != null) {
                    Util.ringtone = RingtoneManager.getRingtone(context, soundUri);
                    Util.ringtone.play();
                }
            }

            Notification  notification = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel notificationChannel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setDescription("channel description");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            notification = new NotificationCompat.BigTextStyle(new NotificationCompat.Builder(context, "channel_id")
                    .setAutoCancel(true)
                    .setSound(soundUri, AudioManager.STREAM_NOTIFICATION)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle(senderNm)
                    .setContentText(result.getString("msgTit"))
                    .setContentIntent(pi1)
                    .setSmallIcon(R.drawable.ic_fsp_small_icon)
                    .setLargeIcon(senderBmp)
                    .setTicker(result.getString("msgTit"))
                    .addAction(R.drawable.icon_msg_normal, "상세보기", pi1)
                    .addAction(R.drawable.btn_check, "나중에 보기", pi2))
                    .bigText(result.getString("msgTit"))
                    .setSummaryText(senderCls)
                    .build();

            manager.notify(0, notification);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

}
