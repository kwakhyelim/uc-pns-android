package ac.uc.mobile.pns;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sz.fspmobile.activity.WebMainFrameActivity;
import com.sz.fspmobile.api.base.ApiManager;
import com.sz.fspmobile.api.base.FSPApiWebChromeClient;
import com.sz.fspmobile.api.base.FSPApiWebViewClient;
import com.sz.fspmobile.api.base.WebViewUtility;
import com.sz.fspmobile.api.spec.FSPApiLogger;
import com.sz.fspmobile.app.FSPGlobalApplication;
import com.sz.fspmobile.config.AppConfig;
import com.sz.fspmobile.config.UserConfig;
import com.sz.fspmobile.log.Logger;
import com.sz.fspmobile.util.AESHelper;
import com.sz.fspmobile.util.ResourceHelper;
import com.sz.fspmobile.view.FSPWebView;
import com.sz.fspmobile.xplatform.DummyActivity;
import ac.uc.mobile.pns.R;
import ac.uc.mobile.pns.base.service.NetworkService;
import ac.uc.mobile.pns.base.service.NetworkServiceListener;
import ac.uc.mobile.pns.setup.PNSSettingActivity;
import ac.uc.mobile.pns.user.PNSLoginActivity;
import ac.uc.mobile.pns.util.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
//import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
//import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class WebMainActivity extends WebMainFrameActivity implements NetworkServiceListener {

    final String TAG = "STECH";
    /**
     * 웹뷰에 대한 이벤트에 대한 메시지 코드.<p>
     */
    private long lastTimeBackPressed = 0; //백버튼 클릭시간

    private FrameLayout layoutMain;
    public ArrayList<WebView> weblist = new ArrayList<WebView>();

    WebView web;
    private ImageButton btnBack;
    public SharedPreferences prefs;
    private ImageButton btnDeleteAll;
    private ImageButton btnDelete;


    private String rsakey;
    /**
     * 설정정보.<p>
     */
    private SharedPreferences sharedPreference;
    public final static String SHARED_DATA_ID = "FSPmobile_SharedData";

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // 웹 페이지 방향 전환 시 페이지 다시 조회 하지 않으려면 해당 메소드를 추가해 놔야 한다.
        super.onConfigurationChanged(newConfig);
        getCurrentWebView().getWebView().computeScroll();
    }

    /**
     * 사용자 정보 업데이트<p>
     */
    private final static int REQUEST_SVR_SELECT_USER = 1200;
    private final static int REQUEST_SVR_SELECT_NOREADMSG_CNT = 1100;
    private String SERVLET_URL = "http://m.uc.ac.kr/WebJSON";

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            //selectNotReadMsgCntAndExit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void login(String userId, String pwd) {
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

    public void selectNotReadMsgCntAndExit() throws Exception {
        NetworkService net = new NetworkService(this, this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("sqlId", "mobile/main:PNS_NO_READ_MSG_S01");
        data.put("user_no", Util.getSharedData("userId", ""));
        net.launchRequestFsp(SERVLET_URL, data, REQUEST_SVR_SELECT_NOREADMSG_CNT, true);
    }


    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        NotificationManager nm = (NotificationManager) getGlobal().getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        nm.cancelAll();

        SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(this);

        if (!"".equals(Util.getSharedData("comWebViewUrl", ""))) {
            String popupUrl = Util.getSharedData("comWebViewUrl", "");
            Util.setSharedData("comWebViewUrl", "");
            try {
                goOpenPage(popupUrl, "slide", true);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!"".equals(Util.getSharedData("userId",""))) {
            login(Util.getSharedData("userId", ""), Util.getSharedData("pwd", ""));
        }
        //getCurrentWebView().getWebView().reload();
    }

    /**
     * 푸시 메시지가 도착하면, XPLATFORM 화면에 푸시 메시지에 대한 알림을 처리한다.<p>
     *
     * @param messageID   메시지 아이디
     * @param message     메시지
     * @param sendUseName 메시지 생성자 명
     */
    public void notificationPushMessage(final String messageID, final String message, final String sendUseName) {
        // ui thread 로 변경
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                StringBuffer buf = new StringBuffer();
                buf.append("fn_fspmob_pushMsg('0', ")
                        .append("{messageID:'")
                        .append(messageID)
                        .append("', message:'")
                        .append(message)
                        .append("', sendUserName:'")
                        .append(sendUseName)
                        .append("'}); ");

                loadJavascript(buf.toString());
            }
        });
    }

    @Override
    public void remove() {
        // 2013/05/22 종료시 웹의 캐시를 삭제한다.
        try {
            FSPWebView web = getCurrentWebView();
            web.getWebView().clearFormData();
            web.getWebView().clearCache(true);
            deleteCacheFiles(getCacheDir());
        } catch (Throwable e) {
            //  무시한다.
//			getLogger().writeException("web cache clear on remove", e);
        }

        // 프로그램을 완전히 종료 한다.
        super.remove();
        FSPGlobalApplication.getGlobalApplicationContext().forceClose();
    }

    public String getRsaKey() {
        return rsakey;
    }

    private LinearLayout introBack = null;
    private int currcnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getLogger().debug("############url: onCreate" );

        super.onCreate(savedInstanceState);
        rsakey = getGlobal().getServerConfig().getCryptoRSAPublicKey();
        sharedPreference = getSharedPreferences(SHARED_DATA_ID, MODE_PRIVATE);
        ResourceHelper resMgr = getGlobal().getResourceHelper();

        layoutMain = (FrameLayout) findViewById(R.id.layoutMain);
        btnDeleteAll = (ImageButton) findViewById(R.id.btnDeleteAll);
        btnDelete = (ImageButton) findViewById(R.id.btnDelete);
        btnBack = (ImageButton) findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                goBackPage("slide");

            }
        });
        btnDeleteAll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                web.loadUrl("javascript:fn_msg_delAll()");
            }
        });

        btnDelete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                web.loadUrl("javascript:fn_msg_del()");
            }
        });

        FSPWebView web = getCurrentWebView();
        initMainWebView(web);
        String bannerUrl = FSPGlobalApplication.getGlobalApplicationContext().getServerConfig().getBannerUrl();
        if (bannerUrl != null && !"".equals(bannerUrl)) {
            web.getWebView().loadUrl(bannerUrl);
        } else {
            web.getWebView().loadUrl(getGlobal().getServerConfig().getNoticeUrl());

        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void initMainWebView(FSPWebView fspWebView) {

        WebView webView = fspWebView.getWebView();
        //webView.clearCache(true);
        apiManager = new ApiManager(webView, this, this, fspLogger);

        WebSettings settings = webView.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setUserAgentString(settings.getUserAgentString() + " Softzam/FspMobile");
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setSupportMultipleWindows(true);

        try {
            WebViewUtility.initWebSettings(settings);
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);


        } catch (Throwable ee) {
//			getLogger().writeException("WebView#Settings", ee);
        }

        // web view client setting
        webView.setWebViewClient(new FSPWebViewClient(this));

        webView.setWebChromeClient(getWebChromeClient(fspWebView));

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype, long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        webView.setInitialScale(0);
    }

    /**
     * FSP Web View Client.<p>
     */
    private class FSPWebViewClient extends FSPApiWebViewClient {
        /**
         * @param activity
         */
        private FSPWebViewClient(Activity activity) {
            super(activity);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.equals("about:blank")) {
                return true;
            }

            if (url.startsWith("pns://")) {
                Uri uri = Uri.parse(url);

                if ("login".equals(uri.getQueryParameter("url"))) {
                    Intent i = new Intent(WebMainActivity.this, PNSLoginActivity.class);
                    i.putExtra("forwardPage", "");
                    startActivity(i);
                } else if ("main".equals(uri.getQueryParameter("url"))) {
                    getCurrentWebView().getWebView().loadUrl(getGlobal().getServerConfig().getNoticeUrl());
                } else {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.getQueryParameter("url")));
                    startActivity(myIntent);
                }
                return true;
            } else if (url.startsWith("pnspopup://")) {
                Uri uri = Uri.parse(url);

                try {
                    goOpenPage(uri.getQueryParameter("url"), "popup", true);
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
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
            } else if (url.startsWith("tel:") || url.startsWith("sms:") || url.startsWith("mailto:")) {
                Uri uri = Uri.parse(url);
                Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(myIntent);
                return true;
            } else {
//				view.loadUrl(url);

            }

            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            FSPWebView fspWebView = getCurrentWebView();
            if (fspWebView != null) {

            }

            if (mProcessBack != null) {
                mProcessBack.setVisibility(View.VISIBLE);
            }

            if (introBack != null) {
                introBack.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (introBack != null) {
                introBack.setVisibility(View.GONE);
            }

            FSPWebView fspWebView = getCurrentWebView();
            if (fspWebView != null) {
                fspWebView.setProgressVisibility(View.GONE);
            }

            if (mProcessBack != null) {
                mProcessBack.setVisibility(View.GONE);
            }

            WebViewUtility.loadJavaScript(view, scriptFiles, loadScriptName);

            finishPageLoad(url);

            if (DummyActivity.ON_MESSAGE) {
                DummyActivity.ON_MESSAGE = false;
                try {
                    notificationPushMessage(DummyActivity.MESSAGE_ID, DummyActivity.MESSAGE
                            , DummyActivity.SEND_USER_ID);
                } catch (Exception e) {

                }
            }
        }

        @Override
        protected String getErrorPageUrl() {
            return null;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onLoadResource(view, url);

            getLogger().write("FMSVC00029"
                , new String[]{"onLoadResource", url} );
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(WebMainActivity.this, android.R.style.Theme_Holo_Light_Dialog));
            builder.setTitle("확인");
            builder.setMessage("요청된 주소의 보안인증서를 신뢰할 없습니다.");
            builder.setCancelable(true);
            builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();    // 알림창 객체 생성
            dialog.show();    // 알림창 띄우기
        }
    }

    private void deleteCacheFiles(File dir) {
        if (dir != null) {
            if (dir.listFiles() != null && dir.listFiles().length > 0) {
                //
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
//				getLogger().debug("############forward:" + forward );
                if ("PNSMAIN".equals(forward)) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    try {
                        goOpenPage(getMainUrl(prefs), "slide", false);
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if ("SETUP".equals(forward)) {
                    Intent intent = new Intent(WebMainActivity.this, PNSSettingActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.leftout, R.anim.rightin);
                } else {
                    introBack = null;
                    FSPWebView fspWebView = getCurrentWebView();
                    WebView web = fspWebView.getWebView();
                    web.reload();
                }
            }
        }
    }

    public String getMainUrl(SharedPreferences prefs) throws UnsupportedEncodingException {
        String returnUrl = getGlobal().getServerConfig().getServerRootUrl() + "/mobile/main_na.html?user_no=" + Util.getSharedData("userId", "") + "&user_gbn=" + Util.getSharedData("userGbn", "") + "&dvic_id=" + URLEncoder.encode(UserConfig.getSharedInstance().getDeviceID(), "UTF-8");
        return returnUrl;
    }

    public String getSubUrl(String url) throws UnsupportedEncodingException {
        String returnUrl = getGlobal().getServerConfig().getServerRootUrl() + url;
        return returnUrl;
    }

    @Override
    protected void pressBack() {

        // TODO Auto-generated method stub
     if (System.currentTimeMillis() <= lastTimeBackPressed + 2000){
         AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(WebMainActivity.this, android.R.style.Theme_Holo_Light_Dialog));

         builder.setTitle("확인");
         builder.setMessage("스마트캠퍼스 앱을 종료하시겠습니까?");
         builder.setCancelable(true);
         builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int which) {
                 try {
                     selectNotReadMsgCntAndExit();
                     //getGlobal().forceClose();
                 } catch (Exception e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                 }
             }
         });
         builder.setNegativeButton("취소",
                 new AlertDialog.OnClickListener() {
                     public void onClick(DialogInterface dialog, int which) {
                         dialog.dismiss();
                     }
                 });

         AlertDialog dialog = builder.create();    // 알림창 객체 생성
         dialog.show();    // 알림창 띄우기*/
        }else{
         if (weblist.size() > 0) {
             WebView wv = weblist.get(weblist.size() - 1);
             if (wv.canGoBack()) wv.goBack();
             else goBackPage("slide");
//			web.reload();
             return;
         }else {
             FSPWebView fspWebView = getCurrentWebView();
             WebView web = fspWebView.getWebView();
             web.reload();
         }
        }
        lastTimeBackPressed = System.currentTimeMillis();
/*
        if(web != null&& web.canGoBack()){
            web.goBack();
            checkRefreshPage();
            return;
        }*/



    }


    void webLoadUrl2(WebView wv, String url) {
        try {
            wv.loadUrl(url);
            wv.invalidate();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void goOpenPage(String url, String type, boolean directYn) throws UnsupportedEncodingException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if ("Y".equals(Util.getSharedData("autoLogin", ""))) {
            if ("SETUP".equals(url)) {
                Intent intent = new Intent(WebMainActivity.this, PNSSettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.rightin, R.anim.leftout);
            } else if ("MAIN".equals(url)) {

            } else {
                int anim = 0;
                if ("slide".equals(type)){
                    anim = R.anim.rightin;
                } else if ("popup".equals(type)){
                    anim = R.anim.slideup;
                }
                Animation slidein = null;
                if (!"".equals(type)){
                    slidein = AnimationUtils.loadAnimation(getApplicationContext(), anim);
                }

                if (layoutMain.getVisibility() == View.VISIBLE) {
                    if(url.indexOf("main_na.html") !=-1){
                        return;
                    }
                    WebView wv = new WebView(WebMainActivity.this);
                    initMainWebView2(wv);
                    layoutMain.addView(wv);
                    webLoadUrl2(wv, getSubUrl(url));
                    if (!"".equals(type)) {
                        wv.startAnimation(slidein);
                    }
                } else {
                    layoutMain.setVisibility(View.VISIBLE);
                    layoutMain.startAnimation(slidein);
                    web = (WebView) findViewById(R.id.webview);
                    initMainWebView2(web);

                    webLoadUrl2(web, getMainUrl(prefs));

                    if (directYn) {
                        WebView wv = new WebView(WebMainActivity.this);
                        initMainWebView2(wv);
                        layoutMain.addView(wv);
                        webLoadUrl2(wv, getSubUrl(url));
                        wv.startAnimation(slidein);
                    }
                }

            }
        } else {
            Intent intent = new Intent(this, PnsLoginActivity.class);
            if ("SETUP".equals(url)) {
                intent.putExtra("forwardPage", "SETUP");
            } else {
                intent.putExtra("forwardPage", "MAIN");
            }

            startActivityForResult(intent, 1000);
            overridePendingTransition(R.anim.rightin, R.anim.leftout);
        }


    }

    public void goBackPage(String type) {
        int anim = 0;
        if ("slide".equals(type)) anim = R.anim.rightout;
        else anim = R.anim.slidedown;
        Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), anim);

        if (weblist.size() > 1) {
            final WebView wv = weblist.get(weblist.size() - 1);
            new Handler().postDelayed(new Runnable() { // new Handler and Runnable
                @Override
                public void run() {
                    wv.removeAllViews();
                    wv.destroy();
                    layoutMain.removeView(wv);
                    weblist.remove(wv);
                    checkRefreshPage();
                }
            }, 100);
            wv.setVisibility(View.INVISIBLE);
            wv.startAnimation(slide);
        } else {
            layoutMain.setVisibility(View.INVISIBLE);
            layoutMain.startAnimation(slide);
            weblist.clear();
        }
    }

    public void checkRefreshPage() {
        String refYn = Util.getSharedData("reloadYn", "N");
        if ("Y".equals(refYn)) {
            if (weblist.size() > 0) {
                WebView wv = weblist.get(weblist.size() - 1);
                wv.reload();
            } else {
                web.reload();
            }
            Editor fspeditor = sharedPreference.edit();
            fspeditor.putString("reloadYn", "N");
            fspeditor.commit();
        }
    }

    @SuppressLint("NewApi")
    public void initMainWebView2(WebView webview) {

        ApiManager apiManager = new ApiManager(webview, this, this, getLogger());

        WebSettings settings = webview.getSettings();
        // java script
        settings.setJavaScriptEnabled(true);
        settings.setSupportMultipleWindows(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAllowContentAccess(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setBuiltInZoomControls(true);
        webview.setWebViewClient(new FSPWebViewClient2());
        webview.setWebChromeClient(new MyWebChromeClient2(apiManager, getLogger(), this));
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        weblist.add(webview);
    }

    /**
     * FSP Web View Client.<p>
     */
    private class FSPWebViewClient2 extends WebViewClient {
        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("pns://")) {
                Uri uri = Uri.parse(url);

                if ("login".equals(uri.getQueryParameter("url"))) {
                    Intent i = new Intent(WebMainActivity.this, PNSLoginActivity.class);
                    i.putExtra("forwardPage", "");
                    startActivity(i);
                } else if ("main".equals(uri.getQueryParameter("url"))) {
                    getCurrentWebView().getWebView().loadUrl(getGlobal().getServerConfig().getNoticeUrl());
                } else {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.getQueryParameter("url")));
                    startActivity(myIntent);
                }
            } else if (url.startsWith("pnspopup://")) {
                Uri uri = Uri.parse(url);

                try {
                    goOpenPage(uri.getQueryParameter("url"), "popup", true);
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
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
            } else if (url.startsWith("tel:") || url.startsWith("sms:") || url.startsWith("mailto:")) {
                Uri uri = Uri.parse(url);
                Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(myIntent);
                return true;
            }


            return true;
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            // TODO Auto-generated method stub
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(WebMainActivity.this, android.R.style.Theme_Holo_Light_Dialog));
            builder.setTitle("확인");
            builder.setMessage("요청된 주소의 보안인증서를 신뢰할 없습니다.");
            builder.setCancelable(true);
            builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();    // 알림창 객체 생성
            dialog.show();    // 알림창 띄우기
        }
    }

    /**
     * 웹 크롬 클라이언트.<p>
     */
    private class MyWebChromeClient2 extends FSPApiWebChromeClient {
        private ResourceHelper resMgr = getGlobal().getResourceHelper();

        private View mCustomView;
        private Activity mActivity;

        /**
         * 생성자.<p>
         *
         * @param apiManager
         * @param logger
         * @param context
         */
        public MyWebChromeClient2(ApiManager apiManager, FSPApiLogger logger,
                                  Activity context) {
            super(apiManager, logger, context);
            mActivity = context;
        }

        @Override
        protected Drawable getTitleIcon() {
            return getResources().getDrawable(resMgr.getDrawableId("icon"));
        }

        @Override
        protected String getAlertTitle() {
            return getResources().getString(resMgr.getStringId("web_title_alert", true));
        }

        @Override
        protected String getConfirmTitle() {
            return getResources().getString(resMgr.getStringId("web_title_confirm", true));
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            // 조회하는 프로그래스 바의 상태가 변경되었을 경우의 처리
            super.onProgressChanged(view, newProgress);
        }


    }


    @Override
    public void onHandleResults(int requestId, JSONObject json) {
        // TODO Auto-generated method stub
        try {
            if (requestId == REQUEST_SVR_SELECT_NOREADMSG_CNT) {
                String errorCode = json.getString("ErrorCode");
                if ("0".equals(errorCode)) {

                    JSONArray jsonArray = json.getJSONArray("ds_output");
                    JSONObject jobj = jsonArray.getJSONObject(0);
                    int badgeCount = Integer.parseInt(jobj.getString("MSGNOTREADCNT"));

                    FSPGlobalApplication context = getGlobal();
                    SharedPreferences prefs = context.getSharedPreferences("setup", Activity.MODE_PRIVATE);
                    Intent badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
                    badgeIntent.putExtra("badge_count", badgeCount);
                    badgeIntent.putExtra("badge_count_package_name", context.getPackageName());
                    badgeIntent.putExtra("badge_count_class_name", "ac.uc.mobile.pns.IntroSimpleActivity");
                    context.sendBroadcast(badgeIntent);

                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            } else if (requestId == REQUEST_SVR_SELECT_USER) {

                String errorCode = json.getString("ErrorCode");
                if("0".equals(errorCode)){
                    JSONObject jobj = json.getJSONObject("result");

                    boolean isAddSession = true;
                    if( isAddSession && jobj != null) {
                        CookieManager cookieManager = CookieManager.getInstance();
                        cookieManager.setAcceptCookie(true);
                        String domain = FSPGlobalApplication.getGlobalApplicationContext().getServerConfig().getServerRootUrl();
                        CookieManager.getInstance().setCookie(domain, "uc_auth=" + getSessionString(jobj));

                        System.out.println("COOKIE----> " + getSessionString(jobj));
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * 세션으로 전달할 문자열을 가져온다.
     * @return
     */
    public String getSessionString(JSONObject json) {
        String str = null;
        try {
            str = getJSonString(json);
            String key = FSPGlobalApplication.getGlobalApplicationContext().getServerConfig().getAESMasterKey();
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
    public String getJSonString(JSONObject json) {
        JSONObject objJson = new JSONObject();
        try {
            objJson.put("USER_ID", json.get("userId"));
            objJson.put("USER_GBNM", json.get("userGbn"));
            objJson.put("USER_NAME", json.get("userNm"));
        }catch (Exception e){e.printStackTrace();}

        return objJson.toString();
    }
}