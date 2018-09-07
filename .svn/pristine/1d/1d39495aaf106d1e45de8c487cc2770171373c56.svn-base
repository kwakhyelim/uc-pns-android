/**
 *
 */
package ac.uc.mobile.pns.user;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.sz.fspmobile.BaseActivity;
import com.sz.fspmobile.app.FSPGlobalApplication;
import com.sz.fspmobile.config.AppConfig;
import com.sz.fspmobile.config.ServerConfig;
import com.sz.fspmobile.config.UserConfig;
import com.sz.fspmobile.log.Logger;
import com.sz.fspmobile.util.AESHelper;
import com.sz.fspmobile.util.AppHelper;
import ac.uc.mobile.pns.base.CommonConfirmDialog;
import ac.uc.mobile.pns.base.service.NetworkService;
import ac.uc.mobile.pns.base.service.NetworkServiceListener;
import ac.uc.mobile.pns.util.Util;


/**
 * @author jkkoo
 *
 */
public class PNSLoginActivity extends BaseActivity implements NetworkServiceListener {
	private static final String TAG = PNSLoginActivity.class.getSimpleName();
	private final static int REQUEST_SVR_SELECT_USER = 1100;

	private String SERVLET_URL ;
	private ImageButton btnLogin;
	private ImageButton btnFindIdPwd;
	private ImageButton btnRegister;

	private EditText editUserId;
	private EditText editUserPwd;

	private LinearLayout layoutLogin;
	private LinearLayout layoutBack;

	private CommonConfirmDialog dialog;
	private String joinUrl;
	private String pwdResetUrl;

	private String loginErrorMsg;
	private boolean guestYn;


	public boolean isGuestYn() {
		return guestYn;
	}

	public void setGuestYn(boolean guestYn) {
		this.guestYn = guestYn;
	}

	/**
	 * 설정정보.<p>
	 */
	//private SharedPreferences sharedPreference;
	public final static String SHARED_DATA_ID = "FSPmobile_SharedData";


	public String getJoinUrl() {
		return joinUrl;
	}

	public void setJoinUrl(String joinUrl) {
		this.joinUrl = joinUrl;
	}



	public String getPwdResetUrl() {
		return pwdResetUrl;
	}

	public void setPwdResetUrl(String pwdResetUrl) {
		this.pwdResetUrl = pwdResetUrl;
	}



	public String getLoginErrorMsg() {
		return loginErrorMsg;
	}

	public void setLoginErrorMsg(String loginErrorMsg) {
		this.loginErrorMsg = loginErrorMsg;
	}

	@Override
	protected void onCreate(Bundle arg0) {

		// TODO Auto-generated method stub
		super.onCreate(arg0);

		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(0);
		setContentView(ac.uc.mobile.pns.R.layout.ntong_login);
		if( !getGlobal().isCompletedInitApp() ) {

			if(Util.ringtone != null) {
				Util.ringtone.stop();
			}

			AppHelper.moveToIntroPage(this);
			finish();
		}else{

			SERVLET_URL = getServerRootUrl();

			setContentView(ac.uc.mobile.pns.R.layout.ntong_login);

			layoutLogin = (LinearLayout)findViewById(ac.uc.mobile.pns.R.id.layoutLogin);
			layoutBack = (LinearLayout)findViewById(ac.uc.mobile.pns.R.id.layoutBack);

			editUserId = (EditText)findViewById(ac.uc.mobile.pns.R.id.editUserId);
			editUserPwd = (EditText)findViewById(ac.uc.mobile.pns.R.id.editUserPwd);

			btnLogin = (ImageButton)findViewById(ac.uc.mobile.pns.R.id.btnLogin);
			btnLogin.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					login(editUserId.getText().toString(), editUserPwd.getText().toString());
				}
			});

			btnFindIdPwd = (ImageButton)findViewById(ac.uc.mobile.pns.R.id.btnFindIdPwd);
			btnFindIdPwd.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					clickFindIdPwdBtn();
				}
			});

			btnRegister = (ImageButton)findViewById(ac.uc.mobile.pns.R.id.btnRegister);
			btnRegister.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					clickRegisterBtn();
				}
			});

			if("Y".equals(Util.getSharedData("autoLogin", "N"))){
				layoutLogin.setVisibility(View.INVISIBLE);
				layoutBack.setVisibility(View.VISIBLE);

				editUserId.setText(Util.getSharedData("userId",""));
				editUserPwd.setText(Util.getSharedData("pwd",""));
				login(Util.getSharedData("userId",""), Util.getSharedData("pwd",""));
			}else{
				layoutLogin.setVisibility(View.VISIBLE);
				layoutBack.setVisibility(View.INVISIBLE);
			}
		}
	}

	public void clickRegisterBtn() {
		if(getJoinUrl()!=null && !"".equals(getJoinUrl())) {
			String url = getGlobal().getServerConfig().getServerRootUrl() + getJoinUrl();
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(intent);

		}
	}

	public void clickFindIdPwdBtn() {
//		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://auth.wonkwang.ac.kr/NCheck/nice/nice.jsp"));
//		startActivity(intent);
	}


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	int loginChkCnt = 0;
	@Override
	public void onHandleResults(int requestId, JSONObject json) {
		// TODO Auto-generated method stub
		Log.d("PNSLoginActivity", "onHandleResults json: " + json.toString());
		try{
			if(requestId == REQUEST_SVR_SELECT_USER){
				String errorCode = json.getString("ErrorCode");
				if("0".equals(errorCode)){
					loginChkCnt = 0;
					JSONObject jobj = json.getJSONObject("result");
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

					Util.setSharedData("serverRootUrl", getGlobal().getServerConfig().getServerRootUrl());
					Util.setSharedData("autoLogin", "Y");
					Util.setSharedData("userId", jobj.getString("userId"));
					Util.setSharedData("userGbn", jobj.getString("userGbn"));
					Util.setSharedData("userNm", jobj.getString("userNm"));
					Util.setSharedData("pwd", editUserPwd.getText().toString());
					Util.setSharedData("userPwd", editUserPwd.getText().toString());

					if(jobj.has("pwdChgYn")) {
						Util.setSharedData("pwdChgYn", jobj.getString("pwdChgYn"));
					}

					if(jobj.has("logo_img_path") && jobj.getString("logo_img_path") != null && jobj.has("logo_img_nm") && jobj.getString("logo_img_nm") != null) {
						Util.setSharedData("logoUrl", jobj.getString("logo_img_path") + jobj.getString("logo_img_nm"));
					}

					if(jobj.has("hm_lnk") && jobj.getString("hm_lnk") != null ) {
						Util.setSharedData("homeUrl", jobj.getString("hm_lnk"));
					}

					if(jobj.has("orgId") && jobj.getString("orgId") != null ) {
						Util.setSharedData("deptCd", jobj.getString("orgId"));
					}

					if(jobj.has("orgNm") && jobj.getString("orgNm") != null ) {
						Util.setSharedData("deptNm", jobj.getString("orgNm"));
					}

					if(jobj.has("moblNo") && jobj.getString("moblNo") != null ) {
						Util.setSharedData("userTelno", jobj.getString("moblNo"));
					}

					if(jobj.has("msg_gbn_cd") && jobj.getString("msg_gbn_cd") != null ) {
						Util.setSharedData("msg_gbn_cd", jobj.getString("msg_gbn_cd"));
					}

					if(jobj.has("empNo")) {
						Util.setSharedData("empNo", jobj.getString("empNo"));
					}
					String mainUrl = getMainUrl(prefs);
					gotoMainActivity(mainUrl,jobj);

				}else{
					loginChkCnt++;
					if(loginChkCnt > 3){
						/***************************로그인 시도 3회이상 실패시 실행**************************************/
						dialog = new CommonConfirmDialog(this, CommonConfirmDialog.ALERT_TYPE, "안내", "로그인 인증 3회 이상 오류로 \n앱을 종료 합니다.", new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								finish();
							}
						});
						dialog.show();
					}  else {
						if(loginErrorMsg == null || "".equals(loginErrorMsg)) {
							setLoginErrorMsg(json.getString("ErrorMsg"));
						}

						dialog = new CommonConfirmDialog(this, CommonConfirmDialog.ALERT_TYPE, "안내", getLoginErrorMsg());
						dialog.show();
						layoutLogin.setVisibility(View.VISIBLE);
						layoutBack.setVisibility(View.INVISIBLE);
					}
				}
			}
		}catch (JSONException e) {
			// TODO: handle exception
			Log.e(TAG, e.getMessage());
		} catch(UnsupportedEncodingException ex) {
			Log.e(TAG, ex.getMessage());
		}
	}



	public void login(String userId, String pwd) {

		NetworkService net = new NetworkService(this, this);

		Map<String,Object> data = new HashMap<String,Object>();

		data.put("fsp_action", "MobileUserAction");
		data.put("fsp_cmd", "login");
		data.put("DVIC_ID", UserConfig.getSharedInstance().getDeviceID());
		data.put("OS_TP", AppConfig.getSharedInstance().getDeviceOSType());
		data.put("USER_ID", userId);
		data.put("PWD", 	pwd);
		if(isGuestYn()) {
			data.put("LOGIN_GUBUN", "GUEST");
		}

		Log.d("", "login server url : "+SERVLET_URL);
		net.launchRequest(SERVLET_URL, data, REQUEST_SVR_SELECT_USER, true);
	}

	public void gotoMainActivity(String initUrl, JSONObject json) {

		Intent intent = AppConfig.getSharedInstance().getMyAppConfig().getFirstMenuPageIntent(PNSLoginActivity.this);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra("initUrl", ServerConfig.getUrl(initUrl, getGlobal().getServerConfig().getServerRootUrl()));
		startActivity(intent);
		finish();
	}


	public String getMainUrl(SharedPreferences prefs) throws UnsupportedEncodingException {

		String returnUrl = "/mobile/main_na.html?user_no="+Util.getSharedData("userId", "")+"&user_gbn="+Util.getSharedData("userGbn","")+" & dvic_id = "+URLEncoder.encode(UserConfig.getSharedInstance().getDeviceID(), "UTF-8") ;

		return returnUrl;
	}

	public String getServerRootUrl() {
		return getGlobal().getServerConfig().getServerRootUrl() + "/WebJSON";
	}


}
