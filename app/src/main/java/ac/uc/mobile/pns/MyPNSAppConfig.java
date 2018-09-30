/**
 *
 */
package ac.uc.mobile.pns;

import android.content.Context;
import android.content.Intent;

import com.sz.pns.fsp.PNSAppConfig;
import com.sz.pns.main.PNSMainActivity;
import com.sz.pns.user.PNSLoginActivity;

/**
 * 앱 환경설정 정보.
 */
public class MyPNSAppConfig extends PNSAppConfig {

  @Override
  public String getLoginPage(boolean arg0) {
    return MyPNSLoginActivity.class.getName();
  }

  @Override
  public Intent getNoticePopupPageIntent(Context context) {
    Intent intent = new Intent(context, MyWebMainFrameActivity.class);
    return intent;
  }

  /**
   * Intro -> 웹 페이지로 이동
   * @return
   */
  @Override
  public String getFirstMenuPage() {
    return MyWebMainFrameActivity.class.getName();
  }

  @Override
  public String getWebMainPage() {
    return getFirstMenuPage();
  }

  public Intent getWebMainPageIntent(Context context, String menuID, String menuUrl, String title, String attrs) {
    Intent i = super.getWebMainPageIntent(context, menuID, menuUrl, title, attrs);
    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
    return i;
  }

  @Override
  public Intent getFirstMenuPageIntent(Context context) {
    Intent intent = new Intent(context, MyWebMainFrameActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
    return intent;
  }

  @Override
  public String getPushMessageNotificationClassName() {
    return MyPNSPushNotificationManager.class.getName();
  }
}
