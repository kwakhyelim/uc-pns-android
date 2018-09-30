package ac.uc.mobile.pns;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;

import com.sz.fspmobile.base.FSPConfig;
import com.sz.pns.fsp.PNSPushNotificationManager;

import ac.uc.mobile.pns.util.Util;

public class MyPNSPushNotificationManager extends PNSPushNotificationManager {
  public MyPNSPushNotificationManager() {
    this(FSPConfig.getInstance().getApplication());
  }

  public MyPNSPushNotificationManager(Application context) {
    super(context);
  }

  protected Uri getSoundUri(String sound) {
    // 사용자 설정 알림음
    String userAlmNo = Util.getSharedData("userAlmNo", "");
    String userAlmPath = Util.getSharedData("userAlmPath", "");
    String userAlmFileNm = Util.getSharedData("userAlmFileNm", "");

    // 알림음 확인.
    Uri soundUri = null;
    String appPath = context.getFilesDir().getAbsolutePath();

    AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    if (!"9999".equals(userAlmNo) && mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL ) { // 무음, 진동이 아닐 경우만 처리 한다.
      if (!"".equals(userAlmFileNm)) {
        // 앱에 설정된 알림음
        String soundFilepath = getSoundFileFromURL(appPath, userAlmFileNm, userAlmPath);
        soundUri = Uri.parse("file://" + soundFilepath);
      } else {
        // 메시지에 포함된 정보
        if (sound != null && sound.length() > 0 ) {
          String[] fileNmSper = sound.split("/");
          String soundFilepath = getSoundFileFromURL(appPath, fileNmSper[fileNmSper.length - 1], sound);
          if (soundFilepath != null) {
            soundUri = Uri.parse("file://" + soundFilepath);
          } else {
            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
          }
        } else {
          soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
      }
    }

    return soundUri;
  }

}
