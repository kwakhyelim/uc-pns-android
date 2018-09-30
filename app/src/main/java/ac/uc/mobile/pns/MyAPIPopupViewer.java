package ac.uc.mobile.pns;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.webkit.CookieManager;

import com.sz.fspmobile.api.spec.FSPResult;
import com.sz.fspmobile.api.spec.FSPResult.ErrorCode;
import com.sz.fspmobile.base.FSPConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import ac.uc.mobile.pns.util.Util;

public class MyAPIPopupViewer extends com.sz.pns.fsp.APIPopupViewer {

  @Override
  public FSPResult doPopup(JSONObject jsonObj) throws JSONException {

    Activity activity = getActivity();
    FSPResult result = new FSPResult(ErrorCode.NO_RESULT);

    if (activity.getLocalClassName().indexOf("MyWebMainFrameActivity") != -1) {
      String page = jsonObj.getString("page");
      if ("LOGOUT".equals(page)) {
        Util.setSharedData("autoLogin", "N");
        Util.setSharedData("userId", "");
        Util.setSharedData("pwd", "");
        Util.setSharedData("userPwd", "");

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        String domain = FSPConfig.getInstance().getServerConfig().getServerRootUrl();
        CookieManager.getInstance().setCookie(domain, "uc_auth=");
      } else {
        if ("SETUP".equals(page)) {
          ((MyWebMainFrameActivity) activity).goOpenPage(page, jsonObj.getString("anim"), null);
        } else {
          ((MyWebMainFrameActivity) activity).goOpenPage(jsonObj.getString("popupUrl"), jsonObj.getString("anim"), jsonObj.getString("page"));
        }
      }
    }

    result.setKeepCallback(true);
    return result;
  }

  @Override
  public FSPResult closePopup(JSONArray jsonArr) throws JSONException {
    JSONObject jsonObj = null;

    if ("null".equals(jsonArr.get(0).toString())) {
      jsonObj = new JSONObject();
    } else {
      jsonObj = jsonArr.getJSONObject(0);
    }

    final Activity activity = getActivity();
    FSPResult result = null;

    final String anim = jsonObj.getString("anim");
    ((MyWebMainFrameActivity) activity).goBackPage(anim);

    return result;
  }
}
