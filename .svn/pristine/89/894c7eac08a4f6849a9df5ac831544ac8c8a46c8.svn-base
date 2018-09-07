package ac.uc.mobile.pns;

import org.json.JSONArray;

import android.content.Intent;

import com.sz.fspmobile.config.AppConfig;
import com.sz.fspmobile.config.ServerConfig;
import com.sz.fspmobile.interfaces.MyAppConfig;
import com.sz.fspmobile.util.AppHelper;
import com.sz.fspmobile.util.DialogHelper;


public class IntroSimpleActivity extends com.sz.fspmobile.activity.IntroSimpleActivity {

    @Override
    protected void checkStartPage() {
        checkStartPageAfterDownMenu();
    }


    protected void checkStartPageForXPlatform() {

        ServerConfig svcConfig = getGlobal().getServerConfig();
        String menuUrl = svcConfig.getMenuAllUrl();
        MyAppConfig config = AppConfig.getSharedInstance().getMyAppConfig();

        System.setProperty("http.keepAlive", "true");
        Intent i = config.getWebMainPageIntent(this
                , "MAIN"
                , menuUrl
                , ""
                , "cache=y;viewport=y;zoom=n;");
        startActivity(i);
        finish();
    }
    /*
	@Override
	protected void onUnactivateDevicePolicy() {
		checkDeviceStatus();
	}
	*/
    /**

     @Override protected void checkDevicePolicyReceiverActive() {
     checkDeviceStatus();
     }
     */
    /**
     */
    @Override
    protected void checkStartPageAfterDownMenu() {
        ServerConfig config = getGlobal().getServerConfig();

        JSONArray files = config.getFileLists();

        if (files != null && files.length() > 0) {
            handler.sendEmptyMessage(AppHelper.MSG_START_ALL_FILE);
        } else {
            checkStartPageForXPlatform();
        }
    }

    @Override
    public void callback(int what, String msg) {
        switch (what) {
            case AppHelper.MSG_END_ALL_IMAGE:
                checkStartPageAfterDownMenu();
                break;
            case AppHelper.MSG_END_ALL_FILE:
                checkStartPageForXPlatform();
                break;
        }
    }

}
