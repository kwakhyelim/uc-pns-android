package ac.uc.mobile.pns;

import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sz.fspmobile.api.spec.FSPResult;
import com.sz.fspmobile.api.spec.FSPResult.ErrorCode;
import com.sz.fspmobile.app.FSPGlobalApplication;
import com.sz.fspmobile.config.UserConfig;

import ac.uc.mobile.pns.fsp.APIPopupViewer;
import ac.uc.mobile.pns.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.webkit.CookieManager;

public class APIPopupViewerPNS extends APIPopupViewer {


	@Override
	public FSPResult doPopup(JSONObject jsonObj) throws JSONException {

		// TODO Auto-generated method stub
		Activity activity = getActivity();
		FSPResult result = new FSPResult(ErrorCode.NO_RESULT);

		if(activity.getLocalClassName().indexOf("WebMainActivity") != -1) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
			String page = jsonObj.getString("page");
			if("LOGOUT".equals(page)) {
				Util.setSharedData("autoLogin", "N");
				Util.setSharedData("userId", "");
				Util.setSharedData("pwd", "");
				Util.setSharedData("userPwd", "");

				CookieManager cookieManager = CookieManager.getInstance();
				cookieManager.setAcceptCookie(true);
				String domain = FSPGlobalApplication.getGlobalApplicationContext().getServerConfig().getServerRootUrl();
				CookieManager.getInstance().setCookie(domain, "uc_auth=");
			}else{
				try {
					if("SETUP".equals(page)){
						((WebMainActivity)activity).goOpenPage(page, jsonObj.getString("anim"), false);
					}else{
						((WebMainActivity)activity).goOpenPage(jsonObj.getString("popupUrl"), jsonObj.getString("anim"), false);
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}else{
			try {
				((WebMainActivity)activity).goOpenPage(jsonObj.getString("popupUrl"), jsonObj.getString("anim"), false);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		result.setKeepCallback(true);
		return result;
	}

	@Override
	public FSPResult closePopup(JSONArray jsonArr) throws JSONException {
		// TODO Auto-generated method stub
		JSONObject jsonObj = null;

		if("null".equals(jsonArr.get(0).toString())) {
			jsonObj = new JSONObject();
		}else{
			jsonObj = jsonArr.getJSONObject(0);
		}
		
		Activity activity = getActivity();
		FSPResult result = null;

		((WebMainActivity)activity).goBackPage(jsonObj.getString("anim"));
		return result;
	}
}
