/**
 * 
 */
package ac.uc.mobile.pns;

import android.content.Context;
import android.content.Intent;

import com.sz.fspmobile.interfaces.DefaultMyAppConfig;


/** 
 * <pre> 
 * @title    TravunivAppConfig.java
 * @project  ESNCloud
 * @date     2014. 04. 03. ?�후 1:16:28
 * @version  ver1.0
 * @author   jkkoo
 * </pre>
 */
/**
 * @author kjksds
 *
 */
public class PNSAppConfig extends DefaultMyAppConfig {

	/**
	 * getLoginPage
	 * <pre>
	 * </pre>
	 * 
	 *  boolean
	 * @return String
	 * @throws 
	 */
	@Override
	public String getLoginPage(boolean arg0) {
		// TODO Auto-generated method stub
		return PnsLoginActivity.class.getName();
	}
	
	/**
	 * getScreenLockPage
	 * <pre>
	 * </pre>
	 * 
	 * @param 
	 * @return String
	 * @throws 
	 */
	@Override
	public String getScreenLockPage() {
		// TODO Auto-generated method stub
		return PnsLoginActivity.class.getName();
	}
	
	/**
	 * getPushMessagePage
	 * <pre>
	 * </pre>
	 * 
	 * @param 
	 * @return String
	 * @throws 
	 */
	@Override
	public String getPushMessagePage() {
		// TODO Auto-generated method stub
		return PnsLoginActivity.class.getName();
	}


	/**
	 * getIntroPage
	 * <pre>
	 * </pre>
	 * 
	 * @param 
	 * @return String
	 * @throws 
	 */
	@Override
	public String getIntroPage() {
		// TODO Auto-generated method stub
		return IntroSimpleActivity.class.getName();
	}
	

	
	
	@Override
	public String getPushMessageNotificationClassName() {
		// TODO Auto-generated method stub
		return PnsPushNotificationManager.class.getName();
				
	}
	
	
	@Override
	public String getWebMainPage() {
		return WebMainActivity.class.getName();
	}
	
	@Override
	public Intent getNoticePopupPageIntent(Context context) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, WebMainActivity.class);
		
		return intent;
	}

	@Override
	public Intent getFirstMenuPageIntent(Context context) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, WebMainActivity.class);
		
		return intent;
	}
	
	
	
}
