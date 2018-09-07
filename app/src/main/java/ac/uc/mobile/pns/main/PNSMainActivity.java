/**
 * 
 */
package ac.uc.mobile.pns.main;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sz.fspmobile.activity.HomeWebMainActivity;
import com.sz.fspmobile.api.base.ApiManager;
import com.sz.fspmobile.api.base.FSPApiWebChromeClient;
import com.sz.fspmobile.api.spec.FSPApiLogger;
import com.sz.fspmobile.app.FSPGlobalApplication;
import com.sz.fspmobile.config.ServerConfig;
import com.sz.fspmobile.config.UserConfig;
import com.sz.fspmobile.util.ResourceHelper;

import ac.uc.mobile.pns.WebMainActivity;
import ac.uc.mobile.pns.base.CommonConfirmDialog;
import ac.uc.mobile.pns.base.service.NetworkService;
import ac.uc.mobile.pns.base.service.NetworkServiceListener;
import ac.uc.mobile.pns.setup.PNSSettingActivity;
import ac.uc.mobile.pns.util.CloseAnimation;
import ac.uc.mobile.pns.util.OpenAnimation;
import ac.uc.mobile.pns.util.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
//import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author jkkoo
 *
 */
@SuppressLint("SetJavaScriptEnabled") 
public class PNSMainActivity extends HomeWebMainActivity implements NetworkServiceListener {
	private static final String TAG = PNSMainActivity.class.getSimpleName();
	private int REQUEST_ID_ACTIVITY_SETTING = 9001;
	private final static int REQUEST_SVR_SELECT_NOREADMSG_CNT = 1100;
	private final static int REQUEST_SVR_SELECT_NOREADMSG_CNT2 = 1200;
	
	
	public static Activity activity ;
	private boolean isAlive = true;;
	
	
	/* slide menu */
	private DisplayMetrics metrics;
	public FrameLayout layoutMain;
	private LinearLayout layoutLeft;
	private FrameLayout.LayoutParams leftMenuLayoutPrams;
	private int leftMenuWidth;
	private boolean isLeftExpanded;
	public boolean isLeftExpanded() {
		return isLeftExpanded;
	}



	private ImageButton btnLeftMenu;
//	private ImageButton btnProfile;
	private TextView txtMypage;
	
	public EditText searchEdit;
	private ImageButton btnSearch;
	private ImageButton btnMainTitle;
	private ImageButton btnDeviceInfo;
	private ImageButton btnQna;
	private ImageButton btnFaq; 
	private ImageButton btnGuide;
	private ImageButton btnSetup;
	private ImageButton btnLogout;
	private ImageButton btnHomepage;
	private ImageButton btnFacebook;
	private ImageButton btnKakao;
	private ProgressBar progressBar;
	
	public SharedPreferences prefs;
	public String initUrl;
	
	public void setInitUrl(String initUrl) {
		this.initUrl = initUrl;
	}



	private String userImgPath;
	int pxWidth;
	
	public String logoUrl;
	private String homeUrl;

	public ArrayList<WebView> weblist = new ArrayList<WebView>();
	
	public boolean isFrameVisible = false;
	float lf = 0.0f;

	/**
	 * 설정정보.<p>
	 */
	private SharedPreferences sharedPreference;

	CommonConfirmDialog dialog;

	public final static String SHARED_DATA_ID = "FSPmobile_SharedData";
	
	private String SERVLET_URL = getGlobal().getServerConfig().getServerRootUrl() + "/WebJSON";
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		if(isFrameVisible){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}
	
	@SuppressLint({ "InlinedApi", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {


		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		activity = this;
		
		if(isTablet(this)) {
			lf = 0.40f;
		}else{
			lf = 0.86f;
		}
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
				WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		pxWidth  = displayMetrics.widthPixels;
		
		initUrl = getIntent().getStringExtra("initUrl");
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		sharedPreference = getSharedPreferences(SHARED_DATA_ID, MODE_PRIVATE);

		logoUrl = Util.getSharedData("logoUrl", "");
		homeUrl = Util.getSharedData("homeUrl","");

		Log.d("", "homeUrl : "+homeUrl);

		userImgPath = Util.getSharedData("userImgPath","");

		setContentView(ac.uc.mobile.pns.R.layout.pns_main);
		web = (WebView) findViewById(ac.uc.mobile.pns.R.id.webview);
		web.setBackgroundColor(Color.WHITE);
		progressBar = (ProgressBar) findViewById(ac.uc.mobile.pns.R.id.progressBar);
		initMainWebView();
		
		
		if("APP3".equals(Util.getSharedData("msg_gbn_cd",""))) {
			Log.d("getHomeUrl", getHomeUrl());
			webLoadUrl(getUrl("homepage")); 
		}else{
			Log.d("initUrl", initUrl);
			webLoadUrl(initUrl);
		}
		
		
		initLeftMenu();
		buttonEventSetting();	
		
		if(!"".equals(Util.getSharedData("comWebViewUrl",""))){
			
			String popupUrl = Util.getSharedData("comWebViewUrl","");
			Editor editor = prefs.edit();
			editor.remove("comWebViewUrl");
			editor.commit();

			goOpenPage(popupUrl, "slide");
		}
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.d("PNS.", "on destory.....");
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		web.onPause();
		super.onPause();
			if(!isAlive){
				selectNotReadMsgCntAndExit(false);
			}
	}
	@Override
	protected void onStop() {
		super.onStop();
	}


	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		web.onResume();
		
		super.onResume();
		
		NotificationManager nm = 
                (NotificationManager)getGlobal().getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
		nm.cancelAll();

		
		isAlive = false;
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String comWebViewUrl = Util.getSharedData("comWebViewUrl","");
		if(comWebViewUrl != null && !"".equals(comWebViewUrl)) {
			Editor editor = prefs.edit();
			editor.remove("comWebViewUrl");
			editor.commit();
			goOpenPage(comWebViewUrl, "slide");
			reloadMainFeed();
		}

	}

	@SuppressLint("InlinedApi")
	@Override
	protected void pressBack() {
		// TODO Auto-generated method stub
		if(isLeftExpanded) {
			menuLeftSlideAnimationToggle();
			return;
		}
		
		if(weblist.size() > 0){
			WebView wv = weblist.get(weblist.size()-1);
			if(wv.canGoBack()) wv.goBack();
			else goBackPage("");
			return;
		}
		
		if(web != null&& web.canGoBack()){
    		web.goBack();
    		checkRefreshPage();
    		return;
    	} 
		

		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(PNSMainActivity.this, android.R.style.Theme_Holo_Light_Dialog));
		
		builder.setTitle("확인");
		builder.setMessage(getString(ac.uc.mobile.pns.R.string.app_name) + "를(을) 종료 하시겠습니까?");
		builder.setCancelable(true);
		builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				selectNotReadMsgCntAndExit(true);
			}
		});
		builder.setNegativeButton("취소",
	            new AlertDialog.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
            	dialog.dismiss();
            }
		}) ;
	
		AlertDialog dialog = builder.create();    // 알림창 객체 생성
		dialog.show();    // 알림창 띄우기
		
	}

	
	public void selectNotReadMsgCntAndExit(boolean exitYn)  {
		NetworkService net = new NetworkService(this, this);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("sqlId", "mobile/main:PNS_NO_READ_MSG_S01");
		data.put("user_no", Util.getSharedData("userId",""));
		if(exitYn) {
			net.launchRequestFsp(SERVLET_URL, data, REQUEST_SVR_SELECT_NOREADMSG_CNT, true);
		}else {
			net.launchRequestFsp(SERVLET_URL, data, REQUEST_SVR_SELECT_NOREADMSG_CNT2, false);
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, "Back");
        menu.add(0, 2, 0, "Foward");
        menu.add(0, 3, 0, "Refresh");
        return true;
 
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 1:
        	web.goBack();
            return true;
        case 2:
        	web.goForward();
            return true;
        case 3:
        	if(weblist.size() > 0){
    			WebView wv = weblist.get(weblist.size()-1);
    			wv.reload();
    		}else
        		web.reload();
            return true;
        case 4:
        	finish();
        	return true;
        }
        return false;
    }
	
	
	private void buttonEventSetting() {
		btnMainTitle = (ImageButton) findViewById(ac.uc.mobile.pns.R.id.btnMainTitle);
		btnMainTitle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				webLoadUrl(initUrl);
				web.clearHistory();
			}
		});
		//Log.d("","logoUrl : " + logoUrl);
		//xml 에 해당 내용 없음
		/*txtMypage = (TextView) findViewById(R.id.txtMypage);
		txtMypage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String popupUrl = getUrl("mypage");
				Animation slidein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rightin);
				WebView wv = new WebView(PNSMainActivity.this);
				initMainWebView2(wv);
				layoutMain.addView(wv);
				webLoadUrl2(wv, popupUrl);
				wv.startAnimation(slidein);
			}
		});*/

		
		btnDeviceInfo = (ImageButton) findViewById(ac.uc.mobile.pns.R.id.btnDeviceInfo);
		btnDeviceInfo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				menuLeftSlideAnimationToggle();
				
//				Animation slidein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rightin);
				WebView wv = new WebView(PNSMainActivity.this);
				initMainWebView2(wv);
				layoutMain.addView(wv);
				webLoadUrl2(wv, getUrl("version"));
//				wv.startAnimation(slidein);
			}
		});
		
		btnQna = (ImageButton) findViewById(ac.uc.mobile.pns.R.id.btnQna);
		btnQna.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				menuLeftSlideAnimationToggle();
				
//				Animation slidein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rightin);
				WebView wv = new WebView(PNSMainActivity.this);
				initMainWebView2(wv);
				layoutMain.addView(wv);
				webLoadUrl2(wv, getUrl("qna"));
//				wv.startAnimation(slidein);
			}
		});
		
		btnGuide = (ImageButton) findViewById(ac.uc.mobile.pns.R.id.btnGuide);
		btnGuide.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				menuLeftSlideAnimationToggle();

//				Animation slidein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rightin);
				WebView wv = new WebView(PNSMainActivity.this);
				initMainWebView2(wv);
				layoutMain.addView(wv);
				webLoadUrl2(wv, getUrl("guide"));
//				wv.startAnimation(slideCin);
			}
			
		});
		
		btnFaq = (ImageButton) findViewById(ac.uc.mobile.pns.R.id.btnFaq);
		btnFaq.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				menuLeftSlideAnimationToggle();

				
//				Animation slidein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rightin);
				WebView wv = new WebView(PNSMainActivity.this);
				initMainWebView2(wv);
				layoutMain.addView(wv);
				webLoadUrl2(wv, getUrl("faq"));
//				wv.startAnimation(slidein);
			}
		});
		
		btnSetup = (ImageButton) findViewById(ac.uc.mobile.pns.R.id.btnSetup);
		btnSetup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isAlive = true;
				menuLeftSlideAnimationToggle();
				Intent intent = new Intent(PNSMainActivity.this, PNSSettingActivity.class);
				startActivityForResult(intent, REQUEST_ID_ACTIVITY_SETTING);
				overridePendingTransition(ac.uc.mobile.pns.R.anim.rightin, ac.uc.mobile.pns.R.anim.leftout);
			}
		});
		
		
		btnLogout = (ImageButton) findViewById(ac.uc.mobile.pns.R.id.btnLogout);
		btnLogout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				logout();
			}
		});
		
		btnHomepage = (ImageButton) findViewById(ac.uc.mobile.pns.R.id.btnHomepage);
		btnHomepage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String returnUrl = getUrl("homepage");
				Log.d("", "homepage : " + returnUrl);
				webLoadUrl(returnUrl);
				menuLeftSlideAnimationToggle();
			}
		});
		
		btnFacebook = (ImageButton) findViewById(ac.uc.mobile.pns.R.id.btnFacebook);
		btnFacebook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try { 
				    Intent intent = new Intent(); 
				    PackageManager pm = getPackageManager(); 
				    intent = pm.getLaunchIntentForPackage("com.facebook.katana");
				    Log.d(TAG, "fasebookcall");
				    
				    startActivity(intent); 
				} catch (NullPointerException  e) { 
				    Uri uri = Uri.parse("market://details?id=com.facebook.katana"); 
				    Intent i = new Intent(Intent.ACTION_VIEW, uri); 
				    startActivity(i); 
				    Log.e(TAG, "fasebookcall error"+ e.getMessage());
				}
			}
		});
		
		btnKakao = (ImageButton) findViewById(ac.uc.mobile.pns.R.id.btnKakao);
		btnKakao.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try { 
				    Intent intent = new Intent(); 
				    PackageManager pm = getPackageManager(); 
				    intent = pm.getLaunchIntentForPackage("com.kakao.talk");
				    startActivity(intent); 
				} catch (NullPointerException e) { 
				    Log.e("","kakao talk execute failed, e=" + e.toString()); 
				    Uri uri = Uri.parse("market://details?id=com.kakao.talk"); 
				    Intent i = new Intent(Intent.ACTION_VIEW, uri); 
				    startActivity(i); 
				}
			}
		});
		
		btnSearch = (ImageButton) findViewById(ac.uc.mobile.pns.R.id.btnSearch);
		btnSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				searchEdit.setText("");
				searchEdit.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(searchEdit, InputMethodManager.SHOW_IMPLICIT);
			}
		});
		
		searchEdit = (EditText) findViewById(ac.uc.mobile.pns.R.id.searchEdit);
		searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					menuLeftSlideAnimationToggle();
					
					WebView wv = new WebView(PNSMainActivity.this);
					initMainWebView2(wv);
					layoutMain.addView(wv);
					webLoadUrl2(wv, getUrl("search"));

		            return true;
		        }
		        return false;
		    }
		});
	}
	
	public void reloadMainFeed() {
		//Log.d("","reload page : " + initUrl);
		webLoadUrl(initUrl);
	}
	
	public void logout() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//		Editor editor = prefs.edit();
		Util.setSharedData("autoLogin", "N");
		Util.setSharedData("userId", "");
		Util.setSharedData("pwd", "");
//		editor.commit();	
		android.os.Process.killProcess(android.os.Process.myPid());

	}
	
	private void initLeftMenu() {

		// init left menu width
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		leftMenuWidth = (int) ((metrics.widthPixels) * lf);

		// init main view
		layoutMain = (FrameLayout) findViewById(ac.uc.mobile.pns.R.id.layoutMain);

		// init left menu
		layoutLeft = (LinearLayout) findViewById(ac.uc.mobile.pns.R.id.layoutLeft);
		leftMenuLayoutPrams = (FrameLayout.LayoutParams) layoutLeft.getLayoutParams();
		leftMenuLayoutPrams.width = leftMenuWidth;
		layoutLeft.setLayoutParams(leftMenuLayoutPrams);

		// init ui
		btnLeftMenu = (ImageButton) findViewById(ac.uc.mobile.pns.R.id.btnLeftMenu);
		btnLeftMenu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				menuLeftSlideAnimationToggle();
			}
		});

	}
	
	static boolean isTablet (Context context) { 
        // TODO: This hacky stuff goes away when we allow users to target devices 
        int xlargeBit = 4; // Configuration.SCREENLAYOUT_SIZE_XLARGE;  // upgrade to HC SDK to get this 
        Configuration config = context.getResources().getConfiguration(); 
        return (config.screenLayout & xlargeBit) == xlargeBit; 
	} 
	
	public void menuLeftSlideAnimationToggle() {
	
		if (!isLeftExpanded) {

			isLeftExpanded = true;
			// Expand
			new OpenAnimation(layoutMain, leftMenuWidth, pxWidth,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, lf, 0, 0.0f, 0, 0.0f);

			// disable all of main view
			FrameLayout viewGroup = (FrameLayout) findViewById(ac.uc.mobile.pns.R.id.ll_fragment).getParent();
			enableDisableViewGroup(viewGroup, false);

			// enable empty view
			((LinearLayout) findViewById(ac.uc.mobile.pns.R.id.ll_empty)).setVisibility(View.VISIBLE);

			findViewById(ac.uc.mobile.pns.R.id.ll_empty).setEnabled(true);
			findViewById(ac.uc.mobile.pns.R.id.ll_empty).setOnTouchListener(
					new OnTouchListener() {

						@Override
						public boolean onTouch(View arg0, MotionEvent arg1) {
							menuLeftSlideAnimationToggle();
							return true;
						}
					});

			
		} else {
			isLeftExpanded = false;

			// close
			new CloseAnimation(layoutMain, leftMenuWidth,
					TranslateAnimation.RELATIVE_TO_SELF, lf,
					TranslateAnimation.RELATIVE_TO_SELF, 0.0f, 0, 0.0f, 0, 0.0f);

			// enable all of main view
			FrameLayout viewGroup = (FrameLayout) findViewById(ac.uc.mobile.pns.R.id.ll_fragment)
					.getParent();
			enableDisableViewGroup(viewGroup, true);

			// disable empty view
			((LinearLayout) findViewById(ac.uc.mobile.pns.R.id.ll_empty)).setVisibility(View.GONE);
			findViewById(ac.uc.mobile.pns.R.id.ll_empty).setEnabled(false);

		}
		
		
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
		
	}
	
	public static void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
		int childCount = viewGroup.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View view = viewGroup.getChildAt(i);
			if (view.getId() != ac.uc.mobile.pns.R.id.btnLeftMenu) {
				view.setEnabled(enabled);
				if (view instanceof ViewGroup) {
					enableDisableViewGroup((ViewGroup) view, enabled);
				}
			}
		}
	}
	
	public void webLoadUrl( String url) {	
		//Log.d("","PnsMainActivity webLoadUrl : " + url);
		web.loadUrl(url);
//			web.invalidate();
	}
	
	public void webLoadUrl2(WebView wv, String url) {	
		//Log.d("","PnsMainActivity webLoadUrl2 : " + url);
		wv.loadUrl(url);
		wv.invalidate();
	}	
	/**
	 * 웹 브라우저 초기화.<p>
	 * 
	 * @param mainView
	 */
	@SuppressLint("NewApi") 
	private void initMainWebView() {
		ApiManager apiManager = new ApiManager(web, this, this, getLogger());
		
		WebSettings settings = web.getSettings(); 
		settings.setUserAgentString(settings.getUserAgentString() + " Softzam/PNS");
		// java script 
		settings.setJavaScriptEnabled(false);	//2016-11-04 일 보완취약점 관련 변경 true -> false
		settings.setSupportMultipleWindows(true); 
		settings.setUseWideViewPort(true);
		settings.setCacheMode(WebSettings.LOAD_NORMAL);
		settings.setAllowContentAccess(true);
		settings.setAllowUniversalAccessFromFileURLs(true);
		settings.setLoadWithOverviewMode(true);
		settings.setSupportZoom(true);
		settings.setUseWideViewPort(true);
		settings.setBuiltInZoomControls(true);
		settings.setSupportMultipleWindows(false);
		web.setWebViewClient(new FSPWebViewClient());
		web.setWebChromeClient(new MyWebChromeClient(apiManager, getLogger(), this));
		web.setDownloadListener(new DownloadListener() {
		    public void onDownloadStart(String url, String userAgent,
		                String contentDisposition, String mimetype,
		                long contentLength) {
		    	DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

	            request.allowScanningByMediaScanner();
	            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
	            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, url);
	            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
	            dm.enqueue(request);
	            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); //This is important!
	            intent.addCategory(Intent.CATEGORY_OPENABLE); //CATEGORY.OPENABLE
	            intent.setType("*/*");//any application,any extension
	            Toast.makeText(getApplicationContext(), "Downloading File", //To notify the Client that the file is being downloaded
	                    Toast.LENGTH_LONG).show();
		    }
		});
	}
	
	
	@SuppressLint("NewApi") 
	public void initMainWebView2(WebView webview) {
		ApiManager apiManager = new ApiManager(webview, this, this, getLogger());
		
		WebSettings settings = webview.getSettings(); 
		settings.setUserAgentString(settings.getUserAgentString() + " Softzam/PNS");
		// java script 
		settings.setJavaScriptEnabled(false); //2016-11-04 일 보완취약점 관련 변경 true -> false
		settings.setSupportMultipleWindows(true); 
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
		settings.setSupportZoom(true);
		settings.setCacheMode(WebSettings.LOAD_NORMAL);
		settings.setAllowContentAccess(true);
		settings.setAllowUniversalAccessFromFileURLs(true);
		settings.setBuiltInZoomControls(true);
		settings.setSupportMultipleWindows(false);
		webview.setWebViewClient(new FSPWebViewClient());
		
		webview.setWebChromeClient(new MyWebChromeClient(apiManager, getLogger(), this));
		webview.setDownloadListener(new DownloadListener() {
		    public void onDownloadStart(String url, String userAgent,
		                String contentDisposition, String mimetype,
		                long contentLength) {
		    	DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

	            request.allowScanningByMediaScanner();
	            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
	            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, url);
	            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
	            dm.enqueue(request);
	            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); //This is important!
	            intent.addCategory(Intent.CATEGORY_OPENABLE); //CATEGORY.OPENABLE
	            intent.setType("*/*");//any application,any extension
	            Toast.makeText(getApplicationContext(), "Downloading File", //To notify the Client that the file is being downloaded
	                    Toast.LENGTH_LONG).show();
		    }
		});
		
		weblist.add(webview);
	}
	
	
	public void setUserImage() {
		BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inPurgeable = true;
    	options.inPreferredConfig = Bitmap.Config.RGB_565;
    	Bitmap photo = null;
    	photo = BitmapFactory.decodeFile(userImgPath, options);
		if(photo == null)
			photo = BitmapFactory.decodeResource(getResources(), ac.uc.mobile.pns.R.drawable.profile_default);
	
		int w = Util.convertDiptoPix(this, 31);
		int h = Util.convertDiptoPix(this, 31);
//		btnProfile.setImageBitmap(Util.getCircleCroppedBitmap(Util.getResizedBitmap(photo, w, h)));
//		btnProfile.setImageBitmap(Util.getCircleCroppedBitmap(photo));
	}
	
	/**
	 * 웹 크롬 클라이언트.<p>
	 */
	private class MyWebChromeClient extends FSPApiWebChromeClient {
		private ResourceHelper resMgr = getGlobal().getResourceHelper();
		
		private View mCustomView;
		private Activity mActivity;
		
		/**
		 * 생성자.<p>
		 * @param apiManager
		 * @param logger
		 * @param context
		 */
		public MyWebChromeClient(ApiManager apiManager, FSPApiLogger logger,
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
			if( progressBar != null ) {
				progressBar.setProgress(newProgress);
			}
		}
		
		
		 private FullscreenHolder mFullscreenContainer;
		    private CustomViewCallback mCustomViewCollback;
		     
			@Override
			public void onShowCustomView(View view, CustomViewCallback callback) {
				// TODO Auto-generated method stub
				
				//Log.d("","onShowCustomView ....");
			     if (mCustomView != null) {
		             callback.onCustomViewHidden();
		             return;
		         }

			     isFrameVisible = true;
		         FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();

		         mFullscreenContainer = new FullscreenHolder(mActivity);
		         mFullscreenContainer.addView(view, ViewGroup.LayoutParams.MATCH_PARENT);
		         decor.addView(mFullscreenContainer, ViewGroup.LayoutParams.MATCH_PARENT);
		         mCustomView = view;
		         mCustomViewCollback = callback;
		         mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		         
		         
			}
			
			@Override
		     public void onHideCustomView() {
		         if (mCustomView == null) {
		             return;
		         }

		         FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
		         decor.removeView(mFullscreenContainer);
		         mFullscreenContainer = null;
		         mCustomView = null;
		         mCustomViewCollback.onCustomViewHidden();
		         isFrameVisible = false;
		         mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		     }
		
	}
	
	static class FullscreenHolder extends FrameLayout {

	     public FullscreenHolder(Context ctx) {
	         super(ctx);
	         setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
	     }
	
	     @Override
	     public boolean onTouchEvent(MotionEvent evt) {
	         return true;
	     }
	}
	
	/**
	 * FSP Web View Client.<p>
	 */
	private class FSPWebViewClient extends WebViewClient {
		@Override
		public void onReceivedError(WebView view, int errorCode, 
				String description, String failingUrl) {
			 super.onReceivedError(view, errorCode, description, failingUrl);
		}
		
		@Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){	
			
			//Log.d("shouldOverrideUrlLoading : ",url);
			if(url.startsWith("pns://")){
				Uri uri = Uri.parse(url);
				//Log.d("","uri param name  url = " + uri.getQueryParameter("url"));
				Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.getQueryParameter("url")));
			    startActivity(myIntent);
				isAlive = true;
			}else if (url.startsWith("pnspopup://")){
				Uri uri = Uri.parse(url);
				//Log.d("","uri param name  url = " + uri.getQueryParameter("url"));
				goOpenPage(uri.getQueryParameter("url"), "slide");
			}else if (url.startsWith("app://package=")) {
				if(url == null || "".equals(url)) return false;
				try { 
				    Intent intent = new Intent(); 
				    PackageManager pm = getPackageManager(); 
				    intent = pm.getLaunchIntentForPackage(url.replace("app://package=", ""));
				    startActivity(intent); 
				} catch (NullPointerException e) { 
				    Log.e("","app execute failed, e=" + e.toString()); 
				    Uri uri = Uri.parse("market://details?id="+url.replace("app://package=", "")); 
				    Intent i = new Intent(Intent.ACTION_VIEW, uri); 
				    startActivity(i); 
				}
				isAlive = true;
				return true;
			}else if (url.startsWith("tel:") || url.startsWith("sms:") || url.startsWith("mailto:")) {
				Uri uri = Uri.parse(url);
				Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
			    startActivity(myIntent);
			}else{
				view.loadUrl(url);	
			}
			return true;
        }
		
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			if( progressBar != null ) {
				progressBar.setVisibility(View.VISIBLE);
			}
		}
		
		@Override
        public void onPageFinished(WebView view, String url){
			super.onPageFinished(view, url);
			if( progressBar != null ) {
				progressBar.setVisibility(View.GONE);
			}
		}

		@Override
		public void onReceivedSslError(WebView view, final SslErrorHandler handler,	SslError error) {

			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(PNSMainActivity.this, android.R.style.Theme_Holo_Light_Dialog));
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
	@Override
	protected void onActivityResult(int requestId, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestId, arg1, arg2);
		
		//Log.d("Main onActivityResult","request id = "+requestId);
		
		
		
		if(requestId == REQUEST_ID_ACTIVITY_SETTING) {
			if("Y".equals(sharedPreference.getString("userImgChange", "N"))) {
				WebView wv = weblist.get(weblist.size()-1);
				wv.reload();
			}
			
			checkRefreshPage();
		}

	}
	
	public void goOpenPage(String url, String type) {
		int anim = 0;
		if("slide".equals(type)) {
			anim = ac.uc.mobile.pns.R.anim.rightin;
		}else if("popup".equals(type)){
			anim = ac.uc.mobile.pns.R.anim.slideup;
		}
		Animation slidein = null;
		if(!"".equals(type)) {
			slidein = AnimationUtils.loadAnimation(getApplicationContext(), anim);
		}
		WebView wv = new WebView(PNSMainActivity.this);
		initMainWebView2(wv);
		layoutMain.addView(wv);
		webLoadUrl2(wv, getUrl(url).replace("https://", "http://"));
		if(!"".equals(type)) {
			wv.startAnimation(slidein);
		}
	}
	public void goBackPage(String type, String refresh) {
		if("yes".equals(refresh)) {
			Util.setSharedData("refreshYN", "Y");
		}
		goBackPage(type);
	}
	public void goBackPage(String type) {
		int anim = 0;
		if("slide".equals(type)) anim = ac.uc.mobile.pns.R.anim.rightout;
		else anim = ac.uc.mobile.pns.R.anim.slidedown;
		Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), anim);
		final WebView wv = weblist.get(weblist.size()-1);
		new Handler().postDelayed(new Runnable() { // new Handler and Runnable
            @Override
            public void run() {
            	wv.removeAllViews();
            	wv.destroy();
            	layoutMain.removeView(wv);
            	weblist.remove(wv);
            	checkRefreshPage();
            }
        },200);
		wv.setVisibility(View.INVISIBLE);
		wv.startAnimation(slide);
		slide.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub

			}
		});
	}
	
	
	public void checkRefreshPage() {
		//String refYn = sharedPreference.getString("refreshYN", "N");
		String refYn = Util.getSharedData( "refreshYN","");
		if("Y".equals(refYn)) {
			if(weblist.size() > 0){
				WebView wv = weblist.get(weblist.size()-1);
				wv.reload();
			}else{
				web.reload();
			}
			Util.getSharedData("refreshYN", "N");
		}
	}
	
	public String getUrl(String gubun) {
		String returnUrl = "";
		
		if("mypage".equals(gubun)){
			returnUrl = "/mobile/my_page.html?gubun=clip&user_no="+Util.getSharedData("userId","")+"&user_nm="+Util.getSharedData("userNm","");
		}else if("version".equals(gubun)){
			returnUrl = "/mobile/ver.html";
		}else if("faq".equals(gubun)){
			returnUrl = "/mobile/faq.html";
		}else if("qna".equals(gubun)){
			if("W01".equals(Util.getSharedData("userGbn",""))) {
				returnUrl = "/mobile/qnaList.html?user_no="+Util.getSharedData("userId","")+"&user_gbn="+Util.getSharedData("userGbn","");
			}else{
				returnUrl = "/mobile/qna.html?user_no="+Util.getSharedData("userId","")+"&user_gbn="+Util.getSharedData("userGbn","");
			}
			
		}else if("guide".equals(gubun)){
			returnUrl = "/mobile/guide.html";
		}else if("search".equals(gubun)){
			returnUrl = "/mobile/search.html?gubun=M&searchWord="+searchEdit.getText().toString()+"&user_no="+Util.getSharedData("userId","")+"&user_gbn="+Util.getSharedData("userGbn","");
		}else if("homepage".equals(gubun)){
			returnUrl = getHomeUrl();
		}else if("alimtomg".equals(gubun)) {
			returnUrl = "/mobile/select_tong.html?userId="+Util.getSharedData("userId","")+"&navGbn=popup&userGbn="+Util.getSharedData("userNm","");
		}else if("feed".equals(gubun)) {
			returnUrl = "/mobile/main_na.html?user_no="+Util.getSharedData("userId","")
						+"&user_gbn="+Util.getSharedData("userNm","")
						+"&dvic_id="+UserConfig.getSharedInstance().getDeviceID()
						+"&msg_gbn_cd=APP1";
		}else if("message".equals(gubun)) {
			returnUrl = "/mobile/main_na.html?user_no="+Util.getSharedData("userId","")
						+"&user_gbn=" + Util.getSharedData("userNm","")
						+"&dvic_id=" + UserConfig.getSharedInstance().getDeviceID()
						+"&msg_gbn_cd=APP2";		
		}else if("clip".equals(gubun)) {
			returnUrl = "/mobile/my_page.html?user_no="+Util.getSharedData("userId","")
						+"&user_gbn=" + Util.getSharedData("userNm","")
						+"&user_nm=" + Util.getSharedData("userNm","")
						+"&gubun=clip";				
		}else{
			returnUrl = gubun;
		}

		Log.d("", "getUrl : " + returnUrl);
		return ServerConfig.getUrl(returnUrl, getGlobal().getServerConfig().getServerRootUrl());
	}


	public boolean isAlive() {
		return isAlive;
	}


	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	
	
	public String getHomeUrl() {
		return homeUrl;
	}


	public void setHomeUrl(String homeUrl) {
		this.homeUrl = homeUrl;
	}
	
	

	@Override
	public void onHandleResults(int requestId, JSONObject json) {
		// TODO Auto-generated method stub
		//Log.d("", "onHandleResults");
		Log.d("", "onHandleResults json: " + json.toString());
		try {
			if(requestId == REQUEST_SVR_SELECT_NOREADMSG_CNT){
				//Log.d("", "REQUEST_SVR_SELECT_NOREADMSG_CNT");
				String errorCode = json.getString("ErrorCode");
				if("0".equals(errorCode)){

					JSONArray jsonArray = json.getJSONArray("ds_output");
					JSONObject jobj = jsonArray.getJSONObject(0);
					int badgeCount = 0;
					if(jobj.has("MSG_NOT_READ_CNT")){
						badgeCount = Integer.parseInt(jobj.getString("MSG_NOT_READ_CNT"));
					} else if(jobj.has("MSGNOTREADCNT")){
						badgeCount = Integer.parseInt(jobj.getString("MSGNOTREADCNT"));
					}
					FSPGlobalApplication context = getGlobal();
		
					SharedPreferences prefs = context.getSharedPreferences("setup", Activity.MODE_PRIVATE);
				    Intent badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
				    badgeIntent.putExtra("badge_count", badgeCount);
				    badgeIntent.putExtra("badge_count_package_name", context.getPackageName());
				    badgeIntent.putExtra("badge_count_class_name", Util.getLauncherClassName(context));
				    context.sendBroadcast(badgeIntent);

					finish();
		    		android.os.Process.killProcess(android.os.Process.myPid()); 
				}
			}else if(requestId == REQUEST_SVR_SELECT_NOREADMSG_CNT2){
				JSONArray jsonArray = json.getJSONArray("ds_output");
				JSONObject jobj = jsonArray.getJSONObject(0);
				int badgeCount = 0;
				if(jobj.has("MSG_NOT_READ_CNT")){
					badgeCount = Integer.parseInt(jobj.getString("MSG_NOT_READ_CNT"));
				} else if(jobj.has("MSGNOTREADCNT")){
					badgeCount = Integer.parseInt(jobj.getString("MSGNOTREADCNT"));
				}

				FSPGlobalApplication context = getGlobal();
			    Intent badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
			    badgeIntent.putExtra("badge_count", badgeCount);
			    badgeIntent.putExtra("badge_count_package_name", context.getPackageName());
			    badgeIntent.putExtra("badge_count_class_name", Util.getLauncherClassName(context));
			    context.sendBroadcast(badgeIntent);
			}
		} catch (JSONException e) {
			// TODO: handle exception
			Log.e(TAG, e.getMessage());
		}
	}
	/**
	 * 디바이스 루팅 검사 루팅 일시 true 
	* <pre>
	* isRooting 
	* 간단요약(한줄설명)
	*
	* 상세내역 설명
	* </pre>
	* @title 메소드명
	* @return
	 */
	public boolean isRooting(){
        boolean isRooting = false;
        try {
            Runtime.getRuntime().exec("su");
            isRooting = true;
        } catch ( Exception e) {
            isRooting = false;
        }
        return isRooting;
    }
	/**
	 * 앱 위변조 검사 위변조 일시 true
	* <pre>
	* isSign 
	* 간단요약(한줄설명)
	*
	* 상세내역 설명
	* </pre>
	* @title 메소드명
	* @return
	 */
    public boolean isSign(){
        boolean isSign = false;
        String noticeUrl =  getGlobal().getServerConfig().getNoticeUrl();
        noticeUrl = noticeUrl.substring(getGlobal().getServerConfig().getServerRootUrl().length());
        Context context = getApplicationContext();
        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
        String cert = "";
        try {
            PackageInfo packageInfo  = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            Signature certSignature =  packageInfo.signatures[0];
            MessageDigest msgDigest = MessageDigest.getInstance("SHA1");
            msgDigest.update(certSignature.toByteArray());
            cert = Base64.encodeToString(msgDigest.digest(), Base64.NO_WRAP);
            //Log.d(TAG,"cert :" +cert);
            if(cert.equals(noticeUrl)){
                isSign = false;
            } else {
                isSign = true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("signCheck", "예외발생");
            isSign = true;
        } catch (NoSuchAlgorithmException e) {
            Log.e("signCheck", "예외");
            isSign = true;
        }
        return isSign;
    }

}
