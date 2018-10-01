package ac.uc.mobile.pns;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;

import com.sz.fspmobile.config.AppConfig;
import com.sz.fspmobile.config.ServerConfig;
import com.sz.fspmobile.config.UserConfig;
import com.sz.fspmobile.log.Logger;
import com.sz.fspmobile.net.HttpMessageHelper;
import com.sz.fspmobile.util.AESHelper;
import com.sz.fspmobile.util.AppDataUtility;
import com.sz.fspmobile.util.AppHelper;
import com.sz.fspmobile.util.DialogHelper;
import com.sz.pns.base.service.NetworkService;
import com.sz.pns.user.PNSLoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import ac.uc.mobile.pns.util.Util;

/**
 * 로그인 페이지.
 */
public class MyPNSLoginActivity extends PNSLoginActivity  {

  protected String forwardPage; // 로그인 페이지 호출자 -> 완료 후 이동할 ㅔㅍ이지
  protected  int loginChkCnt = 0; // 로그인 시도 횟수
  protected String loginErrorMsg; // 로그인 에러 메시지

  @Override
  protected void onCreate(Bundle bundle) {
    //  false로 변경
    UserConfig.getSharedInstance().setUseSaveUserId(false);
    UserConfig.getSharedInstance().setUseSavePassword(false);

    super.onCreate(bundle);
    forwardPage = getIntent().getStringExtra("forwardPage");
  }

  @Override
  protected void pressBack() {
    Intent intent = new Intent();
    intent.putExtra("forward", "");
    finish();
    overridePendingTransition(R.anim.leftin, R.anim.rightout);
  }

  @Override
  public void onHandleResults(int requestId, JSONObject json) {
    Log.d("MyPNSLoginActivity", "onHandleResults json: " + json.toString());
    try {
      if (requestId == REQUEST_SVR_SELECT_USER) {
        if (HttpMessageHelper.isSuccessResult(json)) {
          loginChkCnt = 0;

          Util.setSharedData("autoLogin", "Y");
          JSONObject jobj = json.getJSONObject("result");
          Util.setSharedData("serverRootUrl", getFsp().getServerConfig().getServerRootUrl());
          Util.setSharedData("userId", jobj.getString("userId"));
          Util.setSharedData("userGbn", jobj.getString("userGbn"));
          Util.setSharedData("userNm", jobj.getString("userNm"));
          Util.setSharedData("pwd", editUserPwd.getText().toString());
          Util.setSharedData("userPwd", editUserPwd.getText().toString());

          if (jobj.has("pwdChgYn")) {
            Util.setSharedData("pwdChgYn", jobj.getString("pwdChgYn"));
          }

          if (jobj.has("logo_img_path") && jobj.getString("logo_img_path") != null && jobj.has("logo_img_nm") && jobj.getString("logo_img_nm") != null) {
            Util.setSharedData("logoUrl", jobj.getString("logo_img_path") + jobj.getString("logo_img_nm"));
          }

          if (jobj.has("hm_lnk") && jobj.getString("hm_lnk") != null) {
            Util.setSharedData("homeUrl", jobj.getString("hm_lnk"));
          }

          if (jobj.has("orgId") && jobj.getString("orgId") != null) {
            Util.setSharedData("deptCd", jobj.getString("orgId"));
          }

          if (jobj.has("orgNm") && jobj.getString("orgNm") != null) {
            Util.setSharedData("deptNm", jobj.getString("orgNm"));
          }

          if (jobj.has("moblNo") && jobj.getString("moblNo") != null) {
            Util.setSharedData("userTelno", jobj.getString("moblNo"));
          }

          if (jobj.has("msg_gbn_cd") && jobj.getString("msg_gbn_cd") != null) {
            Util.setSharedData("msg_gbn_cd", jobj.getString("msg_gbn_cd"));
          }

          if (jobj.has("empNo")) {
            Util.setSharedData("empNo", jobj.getString("empNo"));
          }
          gotoMainActivity(jobj);

        } else {
          loginChkCnt++;
          if (loginChkCnt > 3) {
            /***************************로그인 시도 3회이상 실패시 실행**************************************/
            DialogHelper.alert2(this, "로그인 인증 3회 이상 오류로 \n앱을 종료 합니다.", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                finish();
              }
            });
          } else {
            if (loginErrorMsg == null || "".equals(loginErrorMsg)) {
              loginErrorMsg = json.getString("ErrorMsg");
            }
            DialogHelper.alert(this, loginErrorMsg);
          }
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }


  @Override
  protected void login(String userId, String pwd) {
    if (AppDataUtility.isNull(userId)) {
      DialogHelper.alert(this, getLogger().getMessage("FMACT00003"));
      editUserId.requestFocus();
      return;
    } else if (AppDataUtility.isNull(pwd)) {
      DialogHelper.alert(this, getLogger().getMessage("FMACT00001"));
      editUserPwd.requestFocus();
      return;
    }

    // 키패드는 내린다..
    View view = this.getCurrentFocus();
    if (view != null) {
      InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    NetworkService net = new NetworkService(this, this);
    Map<String,Object> data = new HashMap<String,Object>();
    data.put("fsp_action", "MobileUserAction");
    data.put("fsp_cmd", "login");
    data.put("DVIC_ID", UserConfig.getSharedInstance().getDeviceID());
    data.put("OS_TP", AppConfig.getSharedInstance().getDeviceOSType());
    data.put("USER_ID", userId);
    data.put("PWD", 	pwd);
    if(isGuest) {
      data.put("LOGIN_GUBUN", "GUEST");
    }

    // super와 틀린점은 loginServiceUrl 사용 여부
    net.launchRequest(getFsp().getServerConfig().getUrl("WebJSON"), data, REQUEST_SVR_SELECT_USER, true);
  }

  /**
   * 메인 페이지로 이동한다.
   * @param json
   */
  private void gotoMainActivity(JSONObject json) throws Exception {
    boolean isAddSession = true;
    if( isAddSession && json != null) {
      CookieManager cookieManager = CookieManager.getInstance();
      cookieManager.setAcceptCookie(true);
      String domain = getFsp().getServerConfig().getServerRootUrl();
      CookieManager.getInstance().setCookie(domain, "uc_auth=" + getSessionString(json));
    }

    if("PNSMAIN".equals(forwardPage) || "SETUP".equals(forwardPage) ||  "MAIN".equals(forwardPage)) {
      Intent intent = new Intent();
      intent.putExtra("forward", forwardPage);
      setResult(Activity.RESULT_OK, intent);
    } else {
      // 일반 웹 페이지로 이동한다.
      Intent intent = AppConfig.getSharedInstance().getMyAppConfig().getFirstMenuPageIntent(MyPNSLoginActivity.this);
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
      startActivity(intent);
    }

    finish();
    overridePendingTransition(R.anim.leftin, R.anim.rightout);
  }

  /**
   * 세션으로 전달할 문자열을 가져온다.
   * @return
   */
  public String getSessionString(JSONObject json) {
    String str = null;
    try {
      str = getJSonString(json);
      String key = getFsp().getServerConfig().getAESMasterKey();
      str = AESHelper.encrypt(key, str);
      String appId = AppConfig.getSharedInstance().getSiteKey();
      return appId + "|" + str;
    } catch( Exception e){
      Logger.getLogger().writeException("#make failed session string#", e);
    }
    return "";
  }

  /**
   * 세션으로 전달할 문자열을 가져온다.
   * @return
   */
  private String getJSonString(JSONObject json) {
    JSONObject objJson = new JSONObject();
    try {
      objJson.put("USER_ID", json.get("userId"));
      objJson.put("USER_GBNM", json.get("userGbn"));
      objJson.put("USER_NAME", json.get("userNm"));
    }catch (Exception e){e.printStackTrace();}

    return objJson.toString();
  }
}
