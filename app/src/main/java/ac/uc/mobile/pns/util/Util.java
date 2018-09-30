package ac.uc.mobile.pns.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;

import com.sz.fspmobile.config.UserConfig;
import com.sz.fspmobile.log.Logger;
import com.sz.fspmobile.push.PushUtility;
import com.sz.fspmobile.util.AESHelper;
import com.sz.fspmobile.util.FSPEnvironment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
  private static final String TAG = Util.class.getSimpleName();

  public static String nvl(String src, String defaultValue) {
    if (src == null || "".equals(src) || "null".equals(src)) {
      return defaultValue;
    }
    return src;
  }

  static public Bitmap getCircleCroppedBitmap(Bitmap bitmap) {
    Bitmap output;

    if (bitmap.getWidth() > bitmap.getHeight()) {
      output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Config.ARGB_8888);
    } else {
      output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Config.ARGB_8888);
    }

    Canvas canvas = new Canvas(output);

    final int color = 0xff424242;
    final Paint paint = new Paint();
    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

    float r = 0;

    if (bitmap.getWidth() > bitmap.getHeight()) {
      r = bitmap.getHeight() / 2;
    } else {
      r = bitmap.getWidth() / 2;
    }

    paint.setAntiAlias(true);
    canvas.drawARGB(0, 0, 0, 0);
    paint.setColor(color);
    canvas.drawCircle(r, r, r, paint);
    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
    canvas.drawBitmap(bitmap, rect, rect, paint);
    return output;
  }


  public static String getDate(String fmt) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fmt);
    String dateAsString = simpleDateFormat.format(new Date());

    return dateAsString;
  }

  /**
   * <pre>
   * 배지카운트를 업데이트
   * </pre>
   * @return String
   * @title updateIconBadge
   */
  public static void updateIconBadge(Context context, int notiCnt) {
    SharedPreferences prefs = context.getSharedPreferences("setup", Activity.MODE_PRIVATE);
    int badgeCount = prefs.getInt("badgeCount", 0);
    badgeCount = badgeCount + notiCnt;

    PushUtility.showBadgeCountInAppIcon(context, notiCnt);

    Editor editor = prefs.edit();
    editor.putInt("badgeCount", badgeCount);
    editor.commit();
  }


  public static String getSharedData(String key, String defaultVal) {
    String encKey = UserConfig.getSharedInstance().getDeviceID();
    String value = UserConfig.getSharedInstance().getString(key, defaultVal);
    if ( value != null && !"".equals(value)) {
      try {
        return AESHelper.decrypt(encKey, value);
      } catch (Exception e) {
        Log.e(TAG, e.getMessage());
      }
    }

    return value;
  }

  public static void setSharedData(String key, String value) {
    String encKey = UserConfig.getSharedInstance().getDeviceID();

    try {
      String encValue = AESHelper.encrypt(encKey, value);
      UserConfig.getSharedInstance().setString(key, encValue);
    } catch (Exception e) {
      Logger.getLogger().writeException("#### error.... ", e);
      Log.e(TAG, e.getMessage());
    }

  }
}
