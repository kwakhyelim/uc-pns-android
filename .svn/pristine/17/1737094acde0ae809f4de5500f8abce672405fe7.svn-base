/**
 * 
 */
package ac.uc.mobile.pns.fsp;

import android.content.Context;
import android.content.Intent;

import com.sz.fspmobile.activity.IntroSimpleActivity;
import com.sz.fspmobile.interfaces.DefaultMyAppConfig;
import ac.uc.mobile.pns.main.PNSMainActivity;
import ac.uc.mobile.pns.user.PNSLoginActivity;

/** 
 * <pre> 
 * ?�행??�� ???�경구성 ?�정?? *
 * 로그?�및 기�? 기본 ?�티비티�??�구?�할 ???�게 ?�다.
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
	 * ?�에?�의 기본로그???�티비티?�의�?�?��
	 * </pre>
	 * 
	 * @param boolean
	 * @return String
	 * @throws 
	 */
	@Override
	public String getLoginPage(boolean arg0) {
		// TODO Auto-generated method stub
		return PNSLoginActivity.class.getName();
	}
	
	/**
	 * getScreenLockPage
	 * <pre>
	 * ?�면?�금?�티비티�??�의
	 * </pre>
	 * 
	 * @param 
	 * @return String
	 * @throws 
	 */
	@Override
	public String getScreenLockPage() {
		// TODO Auto-generated method stub
		return PNSLoginActivity.class.getName();
	}
	
	/**
	 * getPushMessagePage
	 * <pre>
	 * ?�시메시�?? 처리???�티비티 ?�의
	 * </pre>
	 * 
	 * @param 
	 * @return String
	 * @throws 
	 */
	@Override
	public String getPushMessagePage() {
		// TODO Auto-generated method stub
		return PNSLoginActivity.class.getName();
	}

	/**
	 * getIntroPage
	 * <pre>
	 * ?�트로액?�비??�?��
	 * �?��?�권?�을 빼고 ?�작?�수 ?�도�?.
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
	public Intent getFirstMenuPageIntent(Context context) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, PNSMainActivity.class);
		
		return intent;
	}

//	@Override
//	public Intent getNoticePopupPageIntent(Context context) {
//		// TODO Auto-generated method stub
//		Intent intent = new Intent(context, PNSWebViewActivity.class);
//		
//		return intent;
//	}

	
	
	
	
	
//	@Override
//	public String getPushMessageNotificationClassName() {
//		// TODO Auto-generated method stub
//		return KnockPushNotificationManager.class.getName();
//				
//	}

	
	
}
