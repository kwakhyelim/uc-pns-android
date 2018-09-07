/**
 * 
 */
package ac.uc.mobile.pns.fsp;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import ac.uc.mobile.pns.util.Util;

/**
 * @author sz-jkkoo
 *
 */
public class PNSNotiService extends Service {

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(0);
		
		
		if(Util.ringtone != null && Util.ringtone.isPlaying())
			Util.ringtone.stop();
		

		
	
		return super.onStartCommand(intent, flags, startId);
	}

	
}
