/**
 * 
 */
package ac.uc.mobile.pns.common;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import ac.uc.mobile.pns.main.PNSMainActivity;
import ac.uc.mobile.pns.util.Util;

/**
 * @author sz-jkkoo
 *
 */
@SuppressLint("InflateParams")
public class PNSToast extends Toast {

	Context mContext;
	public PNSToast(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	

 
    public void showToast(final String msgNo, final String msgCtnt, final String msgSendGbn, final String msgScrtYn, int duration){
        // http://developer.android.com/guide/topics/ui/notifiers/toasts.html
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(ac.uc.mobile.pns.R.layout.pns_alarm_toast, null);
        ImageView imgMsgType = (ImageView) v.findViewById(ac.uc.mobile.pns.R.id.imgMsgType);
        
        if("Y".equals(msgScrtYn)){
        	imgMsgType.setImageResource(ac.uc.mobile.pns.R.drawable.inapp_push_msg_private);
        }else{
        	if("T".equals(msgSendGbn)){
        		imgMsgType.setImageResource(ac.uc.mobile.pns.R.drawable.inapp_push_feed);
        	}else{
        		imgMsgType.setImageResource(ac.uc.mobile.pns.R.drawable.inapp_push_msg_public);
        	}
        }
        
        
        imgMsgType.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
//				String userNo = prefs.getString("userId","");
//				String userGbn = prefs.getString("userGbn","");
//
//				Intent intent = AppConfig.getSharedInstance().getMyAppConfig().getNoticePopupPageIntent(mContext);
//				
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				if("Y".equals(msgScrtYn)){
//					String popupUrl = "/mobile/scrt.html?msg_no="+msgNo+"&msg_ctnt="+msgCtnt+"&user_no="+userNo+"&user_gbn="+userGbn;
////					String popupUrl = "local://mobile/scrt.html?msg_no="+msgNo+"&msg_ctnt="+msgCtnt+"&user_no="+userNo+"&user_gbn="+userGbn;
//					intent.putExtra("popupUrl", popupUrl);
//					intent.putExtra("page", "secret");
//				}else{
//					String popupUrl = "/mobile/view.html?msg_no="+msgNo+"&msg_ctnt="+msgCtnt+"&user_no="+userNo+"&user_gbn="+userGbn;
////					String popupUrl = "local://mobile/view.html?msg_no="+msgNo+"&msg_ctnt="+msgCtnt+"&user_no="+userNo+"&user_gbn="+userGbn;
//					intent.putExtra("popupUrl", popupUrl);
//					intent.putExtra("page", "detail");
//				}
//				mContext.startActivity(intent);
//				remove();
			}
		});
//        Util.ringtone = RingtoneManager.getRingtone(mContext, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//	    Util.ringtone.play();
	    
        show(this, v, duration);
        
        
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Activity.ACTIVITY_SERVICE);
        List<RunningTaskInfo> info;
        info = activityManager.getRunningTasks(1);  
        String classNm = info.get(0).topActivity.getClassName();
		if("com.sz.pns.wku.main.PNSMainActivity".equals(classNm)) {
			((PNSMainActivity)PNSMainActivity.activity).reloadMainFeed();
		}
        
    }
 
    
    private void remove(){
    	if(Util.ringtone != null)
			Util.ringtone.stop();
    	
    	super.cancel();
    	
    }
    
    private void show(Toast toast, View v, int duration){
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(duration);
        toast.setView(v);
        toast.show();
        
        
    }
}
