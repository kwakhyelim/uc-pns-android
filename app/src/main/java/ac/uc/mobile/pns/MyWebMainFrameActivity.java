package ac.uc.mobile.pns;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.sz.fspmobile.activity.WebMainFrameActivity;
import com.sz.fspmobile.api.base.ApiManager;
import com.sz.fspmobile.api.base.FSPWebChromeClient;
import com.sz.fspmobile.api.base.FSPWebViewClient;
import com.sz.fspmobile.api.base.WebAttributes;
import com.sz.fspmobile.api.base.WebViewUtility;
import com.sz.fspmobile.base.FSPConfig;
import com.sz.fspmobile.config.AppConfig;
import com.sz.fspmobile.config.UserConfig;
import com.sz.fspmobile.log.Logger;
import com.sz.fspmobile.net.HttpMessageHelper;
import com.sz.fspmobile.push.PushUtility;
import com.sz.fspmobile.util.AESHelper;
import com.sz.fspmobile.util.AppDataUtility;
import com.sz.fspmobile.util.DialogHelper;
import com.sz.fspmobile.view.FSPWebView;
import com.sz.pns.base.service.NetworkService;
import com.sz.pns.base.service.NetworkServiceListener;
import com.sz.pns.common.PNSConfigUtil;
import com.sz.pns.common.PNSUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import ac.uc.mobile.pns.setup.PNSSettingActivity;
import ac.uc.mobile.pns.util.Util;

/**
 * 울산 과학대학교 메인
 * 웹 페이지 표시.
 * 울산과학대학교는 로그인 없이 사용가능
 * 1. 메인 -> 웹페이지
 * 2. 타이틀에서 UC알림 버튼 선택 시 -> (로그인이 안되어 있는 경우) -> 로그인 -> PNS 메인
 *                                (로그인되어 있는 경우 ) -> PNS 메인
 * 3. 로그아웃 시 푸시 메시지 수신 되지 않음.
 */
public class MyWebMainFrameActivity extends WebMainFrameActivity implements NetworkServiceListener {

  /**
   * 사용자 정보 업데이트<p>
   */
  private final static int REQUEST_SVR_SELECT_USER = 1200;
  private final static int REQUEST_SVR_SELECT_NOREADMSG_CNT = 1100;
  private String SERVLET_URL = "http://m.uc.ac.kr/WebJSON";

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    // 웹 페이지 방향 전환 시 페이지 다시 조회 하지 않으려면 해당 메소드를 추가해 놔야 한다.
    super.onConfigurationChanged(newConfig);
    getCurrentWebView().getWebView().computeScroll();
  }

  @SuppressLint("NewApi")
  @Override
  protected void onResume() {
    super.onResume();
    NotificationManager nm = (NotificationManager)
        getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    nm.cancelAll();

    if (!"".equals(Util.getSharedData("comWebViewUrl", ""))) {
      String popupUrl = Util.getSharedData("comWebViewUrl", "");
      Util.setSharedData("comWebViewUrl", "");
      goOpenPage(popupUrl, "slide", null);
    }

    // 아이디와 비밀번호가 저장되어 있는 경우 로그인 처리를 무조건 한다. -> 로그아웃 처리시 없어짐.
    if (!"".equals(Util.getSharedData("userId", ""))
        && !"".equals(Util.getSharedData("pwd", "")) ) {
      Util.setSharedData("autoLogin", "N");
      login(Util.getSharedData("userId", ""), Util.getSharedData("pwd", ""));
    }
  }

  private boolean loadMain = false;
  /**
   * 메인 페이지 표시
   */
  protected void loadMainPage() {
    if( loadMain ) {
      return;
    }

    loadMain = true;
    String menuUrl = getFsp().getServerConfig().getBannerUrl();
      if (AppDataUtility.isNull(menuUrl)) {
      menuUrl =  getFsp().getServerConfig().getNoticeUrl();
    }

    String attrs = "agent=Softzam/FspMobile;initialScale=0;cookie=y;";
    displayMenu(null, null, menuUrl, attrs);
}
  /**
   * 로그인 처리.
   *
   * @param userId 사용자 아이디
   * @param pwd 비밀번호
   */
  protected void login(String userId, String pwd) {
    NetworkService net = new NetworkService(this, this);
    Map<String, Object> data = new HashMap<String, Object>();

    data.put("fsp_action", "MobileUserAction");
    data.put("fsp_cmd", "login");
    data.put("DVIC_ID", UserConfig.getSharedInstance().getDeviceID());
    data.put("OS_TP", AppConfig.getSharedInstance().getDeviceOSType());
    data.put("USER_ID", userId);
    data.put("PWD", pwd);

    net.launchRequest(SERVLET_URL, data, REQUEST_SVR_SELECT_USER, true);
  }

  /**
   * 앱 종료 선택 시 푸시 메시지 건수 조회.
   * @throws Exception
   */
  protected void selectNotReadMsgCntAndExit() throws Exception {
    NetworkService net = new NetworkService(this, this);
    Map<String, Object> data = new HashMap<String, Object>();
    data.put("sqlId", "mobile/main:PNS_NO_READ_MSG_S01");
    data.put("user_no", Util.getSharedData("userId", ""));
    net.launchRequestFsp(SERVLET_URL, data, REQUEST_SVR_SELECT_NOREADMSG_CNT, true);
  }

  @Override
  public void remove() {
    if (this.getFsp().getActivityCount() == 1) {
      DialogHelper.confirm2(this, this.getLogger().getMessage("FMSVC00013"), new android.content.DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          dialog.dismiss();
          try {
            FSPWebView web = getCurrentWebView();
            web.getWebView().clearFormData();
            web.getWebView().clearCache(true);

            deleteCacheFiles(getCacheDir());
            removeWebView();
          } catch (Throwable e) {
            //  무시한다.
//			getLogger().writeException("web cache clear on remove", e);
          }
          FSPConfig.getInstance().forceClose();
        }
      });
    } else {
      this.removeWebView();
      this.finish();
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadMainPage();
  }

  /**
   * 앱 종료시 캐시 삭제
   * @param dir
   */
  private void deleteCacheFiles(File dir) {
    if (dir != null) {
      if (dir.listFiles() != null && dir.listFiles().length > 0) {
        for (File file : dir.listFiles()) {
          deleteCacheFiles(file);
        }
      } else {
        //
        dir.delete();
      }
    }
  }

  /**
   * 액티비티의 처리 결과를 리턴 받아야 할 경우에 호출되는 함수.<p>
   *
   * @Override
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    getLogger().debug("############onActivityResult2:" + requestCode + "," + resultCode);
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 1000) { // 메인.
      if (resultCode == Activity.RESULT_OK) {
        String forward = data.getStringExtra("forward");
        if ("PNSMAIN".equals(forward)) {  // pns 페이지 표시
          goOpenPage(null, "slide", forward);
        } else if ("SETUP".equals(forward)) { // 설정 페이지 이동
          Intent intent = new Intent(MyWebMainFrameActivity.this, PNSSettingActivity.class);
          startActivity(intent);
          overridePendingTransition(R.anim.leftout, R.anim.rightin);
        } else {
          FSPWebView fspWebView = getCurrentWebView();
          WebView web = fspWebView.getWebView();
          web.reload();
        }
      }
    }
  }

  public void goOpenPage(String url, String type, String page) {
    if (Util.getSharedData("autoLogin", "N").equals("Y")) {  // autoLogin Y에서 변경 -> 로그인과 동일 조건으로
      if ("SETUP".equals(url)) {
        Intent intent = new Intent(MyWebMainFrameActivity.this, PNSSettingActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.rightin, R.anim.leftout);
      } else if ("MAIN".equals(page)) { // main 에서 호출... 로그인 요청이다. -> 없어야 함.

      } else {
        int anim = 0;
        if ("slide".equals(type)) {
          anim = R.anim.rightin;
        } else if ("popup".equals(type)) {
          anim = R.anim.slideup;
        }
        Animation slidein = null;
        if (!"".equals(type)) {
          slidein = AnimationUtils.loadAnimation(getApplicationContext(), anim);
        }
        createWebView(false);

        if(page != null && page.equals("PNSMAIN")) {  // 메인 페이지 이동
          // 페이지를 표시한다.
          loadUrl(getPnsMainUrl());
        } else {
          // 페이지를 표시한다.
          loadUrl(FSPConfig.getInstance().getServerConfig().getUrl(url));
        }

        if (!"".equals(type)) {
          getCurrentWebView().getWebView().startAnimation(slidein);
        }
      }
    } else {  // 로그인 으로 이동
      Intent intent = new Intent(this, MyPNSLoginActivity.class);
      if ("SETUP".equals(url)) {
        intent.putExtra("forwardPage", "SETUP");
      } else if( page != null && !page.equals("")){
        intent.putExtra("forwardPage", page);
      } else {
        intent.putExtra("forwardPage", "MAIN");
      }

      startActivityForResult(intent, 1000);
      overridePendingTransition(R.anim.rightin, R.anim.leftout);
    }
  }

  public void goBackPage(String type) {
    int anim = 0;
    if ("slide".equals(type)) {
      anim = R.anim.rightout;
    } else {
      anim = R.anim.slidedown;
    }
    final Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), anim);

    final FSPWebView wv = getCurrentWebView();
    wv.setVisibility(View.INVISIBLE);
    wv.startAnimation(slide);

    removeWebView();

    // 페이지 새로고침 여부 확인하기
    checkRefreshPage();
  }

  /**
   * 페이지 새로고침 여부 확인하기.
   */
  protected void checkRefreshPage() {
    String refYn = Util.getSharedData("reloadYn", "N");
    getLogger().debug("### reload page " + refYn);
    if ("Y".equals(refYn)) {
      Util.setSharedData("reloadYn", "N");
      getCurrentWebView().getWebView().reload();
    }
  }


  @Override
  public void onHandleResults(int requestId, JSONObject json) {
    try {
      if (requestId == REQUEST_SVR_SELECT_NOREADMSG_CNT) {
        String errorCode = json.getString("ErrorCode");
        if ("0".equals(errorCode)) {

          JSONArray jsonArray = json.getJSONArray("ds_output");
          JSONObject jobj = jsonArray.getJSONObject(0);
          int badgeCount = Integer.parseInt(jobj.getString("MSGNOTREADCNT"));

          PushUtility.showBadgeCountInAppIcon(MyWebMainFrameActivity.this, badgeCount);

          getFsp().forceClose();
        }
      } else if (requestId == REQUEST_SVR_SELECT_USER) {
        if (HttpMessageHelper.isSuccessResult(json)) {
          JSONObject jobj = json.getJSONObject("result");
          Util.setSharedData("compLogin", "Y");

          boolean isAddSession = true;
          if (isAddSession && jobj != null) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            String domain = getFsp().getServerConfig().getServerRootUrl();
            CookieManager.getInstance().setCookie(domain, "uc_auth=" + getSessionString(jobj));

            System.out.println("COOKIE----> " + getSessionString(jobj));

            // 로그인 완료 후 신규 푸시 메시지가 존재하는지 확인
            // 푸시 데이터 확인
            Bundle pushData = FSPConfig.getPushData();

            FSPConfig.removePushData();
            if( pushData != null ) {
              showPushMessage(pushData);
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 세션으로 전달할 문자열을 가져온다.
   */
  public String getSessionString(JSONObject json) {
    String str = null;
    try {
      str = getJSonString(json);
      String key = getFsp().getServerConfig().getAESMasterKey();
      str = AESHelper.encrypt(key, str);
      String appId = AppConfig.getSharedInstance().getSiteKey();
      return appId + "|" + str;
    } catch (Exception e) {
      Logger.getLogger().writeException("#make failed session string#", e);
    }
    return "";
  }

  /**
   * 세션으로 전달할 문자열을 가져온다.
   */
  public String getJSonString(JSONObject json) {
    JSONObject objJson = new JSONObject();
    try {
      objJson.put("USER_ID", json.get("userId"));
      objJson.put("USER_GBNM", json.get("userGbn"));
      objJson.put("USER_NAME", json.get("userNm"));
    } catch (Exception e) {
      e.printStackTrace();
    }

    return objJson.toString();
  }

  protected void showPushMessage(Bundle data)  {
    Log.d("showPushMessage", "data : " + data);

    try {
      //  toast 메시지
      //Toast.makeText(this, R.string.pns_push_new_msg_toast, Toast.LENGTH_LONG ).show();

      // 페이지 오픈
      String msgType = PushUtility.getMessageType(data);
      String msgNo = PushUtility.getMessageID(data);
      String msg = data.getString("m");

      JSONObject json = new JSONObject(msg);
      String msgCtnt = json.getString("msgUrl");
      String securYn = json.getString("securYn");

      String url = null;

      if( "Y".equals(securYn)) {  //  비밀
        url = "/mobile/scrt.html?msg_no='${msg_no}'&msg_ctnt='${msg_ctnt}'&msg_recv_yn='${msg_recv_yn}'&user_no='${user_no}'&user_gbn='${user_gbn}'&msg_gbn='${msg_gbn}'";
      } else {
        url = "/mobile/view.html?msg_no='${msg_no}'&msg_ctnt='${msg_ctnt}'&msg_recv_yn='${msg_recv_yn}'&user_no='${user_no}'&user_gbn='${user_gbn}'&msg_gbn='${msg_gbn}'";
      }

      url = url.replace("${user_no}", Util.getSharedData("userId", ""));
      url = url.replace("${user_gbn}", Util.getSharedData("userGbn", ""));
      url = url.replace("${msg_recv_yn}", "Y"); // 전송여부
      url = url.replace("${msg_no}", msgNo);
      url = url.replace("${msg_ctnt}", msgCtnt);
      url = url.replace("${msg_gbn}", "A".equals(msgType) ? "APP1" : "APP2");

      Log.d("showPushMessage", "url :::::::::::::::: " + url);

      goOpenPage(getFsp().getServerConfig().getUrl(url), "popup", null);
    }catch(JSONException e) {
      Log.e("showPushMessage", e.getMessage());
    }
  }

  /**
   * PNS 화면 표시시 최초로 표시하는 경로를 가져온다.
   *
   * @return PNS 화면 경로.
   */
  protected String getPnsMainUrl() {
    try {
      String returnUrl = getFsp().getServerConfig().getServerRootUrl()
          + "/mobile/msglist.html?user_no=" // main_na.html
          + Util.getSharedData("userId", "")
          + "&user_gbn=" + Util.getSharedData("userGbn", "")
          + "&dvic_id=" + URLEncoder.encode(UserConfig.getSharedInstance().getDeviceID(), "UTF-8");

      return returnUrl;
    } catch (UnsupportedEncodingException e) {
      return "";
    }
  }

  @Override
  protected FSPWebViewClient getFSPWebViewClient() {
    MyWebViewClient fspWebViewClient = new MyWebViewClient(this);
    return fspWebViewClient;
  }

  @Override
  protected FSPWebChromeClient getFSPWebChromeClient(ApiManager apiManger) {
    FSPWebChromeClient chromeClient = new FSPWebChromeClient(apiManger, this);
    chromeClient.setFSPChromeClient(new FSPWebChromeClient.FSPChromeClient() {
      public void onReceivedTitle(String title) {
      }
      public void onCloseWindow(WebView window) {
        MyWebMainFrameActivity.this.removeWebView();
      }

      public FSPWebView createWindow() {
        return MyWebMainFrameActivity.this.createWebView(true);
      }
    });
    return chromeClient;
  }

  /**
   * FSP Web View Client.<p>
   */
  private class MyWebViewClient extends FSPWebViewClient {
    private MyWebViewClient(Activity activity) {
      super(activity);
    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      if (url.equals("about:blank")) {
        return true;
      }

      if (url.startsWith("pns://")) {
        Uri uri = Uri.parse(url);
        if ("login".equals(uri.getQueryParameter("url"))) {
          Intent i = new Intent(MyWebMainFrameActivity.this, MyPNSLoginActivity.class);
          i.putExtra("forwardPage", "");
          startActivity(i);
        } else if ("main".equals(uri.getQueryParameter("url"))) {
          getCurrentWebView().getWebView().loadUrl(getFsp().getServerConfig().getNoticeUrl());
        } else {
          Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.getQueryParameter("url")));
          startActivity(myIntent);
        }
        return true;
      } else if (url.startsWith("pnspopup://")) {
        Uri uri = Uri.parse(url);
        goOpenPage(uri.getQueryParameter("url"), "popup", null);
        return true;
      } else if (url.startsWith("app://package=")) {
        if (url == null || "".equals(url)) return false;
        try {
          Intent intent = new Intent();
          PackageManager pm = getPackageManager();
          intent = pm.getLaunchIntentForPackage(url.replace("app://package=", ""));
          startActivity(intent);
        } catch (Exception e) {
          Uri uri = Uri.parse("market://details?id=" + url.replace("app://package=", ""));
          Intent i = new Intent(Intent.ACTION_VIEW, uri);
          startActivity(i);
        }
        return true;
      }

      return super.shouldOverrideUrlLoading(view, url);
    }
  }
}