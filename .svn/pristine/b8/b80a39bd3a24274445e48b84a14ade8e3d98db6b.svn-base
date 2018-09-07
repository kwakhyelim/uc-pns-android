/**
 * 
 */
package ac.uc.mobile.pns;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.CookieManager;

import com.sz.fspmobile.app.FSPGlobalApplication;
import com.sz.fspmobile.config.AppConfig;
import com.sz.fspmobile.config.ServerConfig;
import com.sz.fspmobile.log.Logger;
import com.sz.fspmobile.util.AESHelper;
import ac.uc.mobile.pns.R;
import ac.uc.mobile.pns.user.PNSLoginActivity;
import ac.uc.mobile.pns.util.Util;

import org.json.JSONObject;

/**
 * @author sz-jkkoo
 *
 */
public class PnsLoginActivity extends PNSLoginActivity {
	private String forwardPage;
	//private SharedPreferences sharedPreference;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		forwardPage = getIntent().getStringExtra("forwardPage");
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.putExtra("forward", "");
		finish();
		overridePendingTransition(R.anim.leftin, R.anim.rightout);
	}

	@Override
	public void gotoMainActivity(String initUrl, JSONObject json) {
		// TODO Auto-generated method stub

		boolean isAddSession = true;
		if( isAddSession && json != null) {
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.setAcceptCookie(true);
			String domain = FSPGlobalApplication.getGlobalApplicationContext().getServerConfig().getServerRootUrl();
			CookieManager.getInstance().setCookie(domain, "uc_auth=" + getSessionString(json));
		}

		if("PNSMAIN".equals(forwardPage)) {
			Intent intent = new Intent();
			intent.putExtra("forward", "PNSMAIN");
			setResult(Activity.RESULT_OK, intent);
		}else if("SETUP".equals(forwardPage)) {
			Intent intent = new Intent();
			intent.putExtra("forward", "SETUP");
			setResult(Activity.RESULT_OK, intent);
		}else if("MAIN".equals(forwardPage)) {
			Intent intent = new Intent();
			intent.putExtra("forward", "MAIN");
			setResult(Activity.RESULT_OK, intent);

		}else{
			Intent intent = AppConfig.getSharedInstance().getMyAppConfig().getFirstMenuPageIntent(PnsLoginActivity.this);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.putExtra("initUrl", ServerConfig.getUrl(initUrl ,getGlobal().getServerConfig().getServerRootUrl()));
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


	@Override
	public String getServerRootUrl() {
		// TODO Auto-generated method stub
		return "http://m.uc.ac.kr/WebJSON";
	}

	
}
