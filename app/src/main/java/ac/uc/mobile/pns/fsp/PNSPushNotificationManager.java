/*
 * Copyright (c) 2013-2020 Softzam, Co., Ltd.
 *
 * All Rights Reserved. Unpublished rights reserved under the copyright laws
 * of the South Korea. The software contained on this media is proprietary
 * to and embodies the confidential technology of Softzam, Co., Ltd. 
 */
package ac.uc.mobile.pns.fsp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.sz.fspmobile.app.FSPGlobalApplication;
import com.sz.fspmobile.config.UserConfig;
import com.sz.fspmobile.interfaces.MyPushNotificationManager;
import com.sz.fspmobile.log.Logger;
import com.sz.fspmobile.service.PushMessageNotification;
import ac.uc.mobile.pns.common.PNSToast;
import ac.uc.mobile.pns.user.PNSLoginActivity;
import ac.uc.mobile.pns.util.Util;

/**
 * <pre>
 * 푸시메시지 처리.
 *
 * 앳위의 다양한 푸시 형태에 따라 fspmobile의 푸시메시지 재정의.
 * @title    AtweePushNotificationManager.java
 * @project  ESNCloud
 * @date     2013. 12. 28. 오후 12:16:28
 * @version  ver1.0
 * @author   jkkoo
 * </pre>
 */
public class PNSPushNotificationManager extends PushMessageNotification 
		implements MyPushNotificationManager {
	private static final String TAG = PNSPushNotificationManager.class.getSimpleName();
	//msg 상세정보 요청 아이디 

	SharedPreferences prefs;

	@SuppressWarnings("rawtypes")
	public Class firstActivity = null;
	public Intent forwardIntent = null;
	
	/**
	 * AtweePushNotificationManager
	 * <pre>
	 * 
	 * 생성자
	 * 
	 * </pre>
	 * 
	 * @param 
	 * @return void
	 * @throws 
	 */
	public PNSPushNotificationManager() {
		this(FSPGlobalApplication.getGlobalApplicationContext());
	}
	
	/**
	 * AtweePushNotificationManager
	 * <pre>
	 * 생성자
	 * </pre>
	 * 
	 * @param 
	 * @return void
	 * @throws 
	 */
	public PNSPushNotificationManager(FSPGlobalApplication context) {
		super(context);
	}

	
	
	@Override
	public void onMessage(final String messageID, final String message, final String sendUseName, Bundle arg3) {
          this.onMessage(messageID, message, sendUseName);
	}

      @Override
      public void onMessage(final String messageID, final String message, final String sendUseName, Map arg3) {
        this.onMessage(messageID, message, sendUseName);
      }

	/**
	 * onMessage
	 * <pre>
	 * 
	 * 푸시메시지를 수신했을때 처리 재정의.
	 * 
	 * 해당 앱이 포그라운드에 있을때(실행중)는 toast메시지만 표시하고
	 * 앱이 미 실행 중이거나 백그라운드에 있을때 노티영역(알림영역)에 표시.
	 * 
	 * </pre>
	 * 
	 * @param 
	 * @return void
	 * @throws 
	 */
	public void onMessage(final String messageID, final String message, final String sendUseName) {
		// TODO Auto-generated method stub
		Log.d("","PNSPushNotificationManager onMessage ::::::::::::::::: ");
		if(firstActivity == null)
			firstActivity = PNSLoginActivity.class;
		
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Log.d("","messageID : " + messageID);
		Log.d("","message : " + message);
		Log.d("","sendUseName : " + sendUseName);
		
    	ActivityManager activityManager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        List<RunningTaskInfo> info;
        info = activityManager.getRunningTasks(1);
		String pkgNm = info.get(0).topActivity.getPackageName();


		
		String msgNotReadCnt = "";
		String result = getMsgFromURL(Util.getSharedData("serverRootUrl","")+"/WebJSON", messageID);
		
		Log.d("","result : " + result);
		if(!context.getPackageName().equals(pkgNm)) {
			
			try {
				JSONObject jobj = new JSONObject(result);
				notiMessage(context, jobj);
				
				JSONObject jobj2 = jobj.getJSONObject("result");
				msgNotReadCnt = jobj2.getString("msgNotReadCnt");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, e.getMessage());
			}
		}else{
			showToast(context, result);	
			
		}
		Util.updateIconBadge(context, Integer.parseInt(msgNotReadCnt));
		
		
	}
	
	/**
	 * notiMessage
	 * <pre>
	 * 노티피케이션 메시지를 생성하여 띄움.
	 * 
	 * 안드로이드의 알림영역에 표시할 이미지,내용 및 탭 했을때의 실행할 액티비티등을 정의 하여
	 * 노티를 발생시킨다.
	 * </pre>
	 * 
	 * @param context
	 * @param msgId 메시지 아이디.
	 * @param msg 서버에서 GCM을 통해 수신한 메시지전문(json타입)
	 * @return void
	 * @throws 
	 */
	@SuppressLint("NewApi") 
	public void notiMessage(FSPGlobalApplication context, JSONObject jobj){
		Log.d("","PNSPushNotificationManager notiMessage ::::::::::::::::: ");
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		try {
			JSONObject result = jobj.getJSONObject("result");
			String userNo = Util.getSharedData("userId","");
			String userGbn = Util.getSharedData("userGbn","");
			String msgGbn = "";
			if(result.has("msgGbn")) msgGbn = result.getString("msgGbn");
			
			Intent intent = null;
			Log.d("","forwardIntent1 : " + forwardIntent);
			Log.d("","firstActivity : " + firstActivity.getClass().toString());
			if(forwardIntent != null){
				Log.d("","forwardIntent2 : " + forwardIntent.getClass().getName());
				
				intent = forwardIntent;
			}else {
				intent = new Intent(context, firstActivity);
				if(result.has("msgScrtYn")&& "Y".equals(result.getString("msgScrtYn"))) {
					intent.putExtra("popupUrl", "/mobile/scrt.html?msg_no="+result.getString("msgNo")+"&msg_ctnt="+result.getString("msgCtnt")+"&user_no="+userNo+"&user_gbn="+userGbn+"&msg_gbn="+msgGbn);
					intent.putExtra("page", "scrt");
					if(isForeground(context, context.getPackageName()))
						Util.setSharedData("comWebViewUrl", "/mobile/scrt.html?msg_no=" + result.getString("msgNo") + "&msg_ctnt=" + result.getString("msgCtnt") + "&user_no=" + userNo + "&user_gbn=" + userGbn + "&msg_gbn=" + msgGbn);

				}else{
					intent.putExtra("popupUrl", "/mobile/view.html?msg_no="+result.getString("msgNo")+"&msg_ctnt="+result.getString("msgCtnt")+"&user_no="+userNo+"&user_gbn="+userGbn+"&msg_gbn="+msgGbn);
					intent.putExtra("page", "detail");
					if(isForeground(context, context.getPackageName()))
						Util.setSharedData("comWebViewUrl", "/mobile/view.html?msg_no=" + result.getString("msgNo") + "&msg_ctnt=" + result.getString("msgCtnt") + "&user_no=" + userNo + "&user_gbn=" + userGbn + "&msg_gbn=" + msgGbn);

				}	
			}

			Intent svrIntent = new Intent(context, PNSNotiService.class);
			svrIntent.putExtra("noti", "detail");
		
			PendingIntent pi1 = PendingIntent.getActivity(context, 0, 	intent, Intent.FLAG_ACTIVITY_CLEAR_TOP);
			PendingIntent pi2 = PendingIntent.getService(context, 0, svrIntent, Intent.FLAG_ACTIVITY_CLEAR_TOP);

			String senderImg = "";
			String senderNm = "";
			String senderCls = "";
			
			if("T".equals(result.getString("msgSendGbn"))){
				senderImg = result.getString("pnsImg");
				senderCls = result.getString("pnsLcNm");
				senderNm = result.getString("pnsNm");
			}else{
				senderImg = result.getString("senderImg");
				senderCls = result.getString("mngDept");
				senderNm = result.getString("mngNm");
			}
		      
			Bitmap senderBmp = getBitmapFromURL(senderImg);
			Bitmap messageBmp =  getBitmapFromURL(result.getString("msgThumb"));
			


			Uri soundUri = null;
			String appPath = context.getFilesDir().getAbsolutePath();
			AudioManager mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
			if(mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL){

				if(!"".equals(Util.getSharedData("userAlmFileNm",""))){
					String soundFilepath = getSoundFileFromURL(appPath, Util.getSharedData("userAlmFileNm",""), Util.getSharedData("userAlmPath",""));
					soundUri = Uri.parse("file://"+soundFilepath);
				}else{
					if(result.has("msgAlm") && result.getString("msgAlm") != null && !"".equals(result.getString("msgAlm"))) {
						String [] fileNmSper = result.getString("msgAlm").split("/");
						String soundFilepath = getSoundFileFromURL(appPath, fileNmSper[fileNmSper.length-1], result.getString("msgAlm"));
						if(soundFilepath != null) soundUri = Uri.parse("file://"+soundFilepath);
						else soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);	
					}else{
						soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);	
					}
				}

			}
			
			if(mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE){
				Vibrator v = (Vibrator) context.getSystemService(Activity.VIBRATOR_SERVICE);
				v.vibrate(1000);
			}
			
			
			if(soundUri != null){
				Util.ringtone = RingtoneManager.getRingtone(context, soundUri);
			    Util.ringtone.play();	
			}


			Notification  notification = null;
			if(messageBmp != null){
				notification = new Notification.BigPictureStyle(
						new Notification.Builder(context)
						.setAutoCancel(true)
						.setSound(soundUri, AudioManager.STREAM_NOTIFICATION)
						.setPriority(Notification.PRIORITY_MAX)
//						.setDefaults(Notification.DEFAULT_ALL)
						.setContentTitle(senderNm)
						.setContentText(result.getString("msgTit"))
						.setSmallIcon(ac.uc.mobile.pns.R.drawable.ic_fsp_small_icon)
						.setLargeIcon(senderBmp)
						.setTicker(result.getString("msgTit"))
						.addAction(ac.uc.mobile.pns.R.drawable.icon_msg_normal, "상세보기", pi1)
						.addAction(ac.uc.mobile.pns.R.drawable.btn_check, "나중에확인", pi2))
						.bigPicture(messageBmp)
						.setBigContentTitle(senderNm)
						.setSummaryText(senderCls)
						.build();
			}else{
				notification = new Notification.BigTextStyle(new Notification.Builder(context)
						.setAutoCancel(true)
						.setSound(soundUri, AudioManager.STREAM_NOTIFICATION)
						.setPriority(Notification.PRIORITY_MAX)
//						.setDefaults(Notification.DEFAULT_ALL)
						.setContentTitle(senderNm)
						.setContentText(result.getString("msgTit"))
						.setSmallIcon(ac.uc.mobile.pns.R.drawable.ic_fsp_small_icon)
						.setLargeIcon(senderBmp)
						.setTicker(result.getString("msgTit"))
						.addAction(ac.uc.mobile.pns.R.drawable.icon_msg_normal, "상세보기", pi1)
						.addAction(ac.uc.mobile.pns.R.drawable.btn_check, "나중에확인", pi2))
						.bigText(result.getString("msgDscr"))
						.setSummaryText(senderCls)
						.build();
			}
			manager.notify(0, notification);
		}catch (JSONException e) {
			// TODO: handle exception
			Log.e(TAG, e.getMessage());
		}
	}
	
	
	
	
	
	@Override
	public int cancelNotification(Context arg0) {
		// TODO Auto-generated method stub
		Log.d("","cancelNotification ......................");
		Log.d("","cancelNotification ......................");
		Log.d("","cancelNotification ......................");
		Log.d("","cancelNotification ......................");
		Log.d("","cancelNotification ......................");
		
		if(Util.ringtone != null && Util.ringtone.isPlaying())
			Util.ringtone.stop();
		
		return super.cancelNotification(arg0);
	}

	
	
	/**
	 * showToast
	 * <pre>
	 * 알림 메시지를 Toast로 띄음.
	 * 
	 * 디자인 적용하여 다시 정리할 필요 있음.
	 * </pre>
	 * 
	 * @param context
	 * @param msg 서버에서 GCM을 통해 수신한 메시지전문(json타입)
	 * @return void
	 * @throws 
	 */
	public void showToast(final Context context, final String msgResult){
		

		
		/**
		 * 요 영역(context)에서 Toast를 띄울수 없으므로 별도쓰레드로 띄움.
		 * 그러나 이렇게 하니 미디어플레이할때 에러메시지 발생(플레이는 됨)
		 */

		try {
			JSONObject jobj = new JSONObject(msgResult);
			JSONObject result = jobj.getJSONObject("result");
			
			
			final String msgNo = result.getString("msgNo");
			final String msgCtnt = result.getString("msgCtnt");
			final String msgSendGbn = result.getString("msgSendGbn");
			final String msgScrtYn = result.getString("msgScrtYn");
			new Runnable() {
				@Override
				public void run() {
					Map<String, Object> map= new Hashtable<String, Object>();
					Message msg = new Message();
					msg.what = 0;
					map.put("msgNo", msgNo);
					map.put("msgCtnt", msgCtnt);
					map.put("msgSendGbn", msgSendGbn);
					map.put("msgScrtYn", msgScrtYn);
					map.put("context", context);
					msg.obj = map;
					handler.sendMessage(msg);
				}
			}.run();
			
			Uri soundUri = null;
			String appPath = context.getFilesDir().getAbsolutePath();
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

			AudioManager mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
			if(mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL){
				if("998".equals(Util.getSharedData("userAlmNo",""))) {
					Vibrator v = (Vibrator) context.getSystemService(Activity.VIBRATOR_SERVICE);
					v.vibrate(1000);
				} else if(!"".equals(Util.getSharedData("userAlmFileNm",""))){
					String soundFilepath = getSoundFileFromURL(appPath, Util.getSharedData("userAlmFileNm",""),Util.getSharedData("userAlmPath",""));
					soundUri = Uri.parse("file://"+soundFilepath);
				}else{
					if(result.has("msgAlm") && result.getString("msgAlm") != null && !"".equals(result.getString("msgAlm"))) {
						String [] fileNmSper = result.getString("msgAlm").split("/");
						String soundFilepath = getSoundFileFromURL(appPath, fileNmSper[fileNmSper.length-1], result.getString("msgAlm"));
						if(soundFilepath != null) soundUri = Uri.parse("file://"+soundFilepath);
						else soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);	
					}else{
						soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);	
					}
				}
			}	
			
			if(mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE){
				Vibrator v = (Vibrator) context.getSystemService(Activity.VIBRATOR_SERVICE);
				v.vibrate(1000);
			}
			
			if(soundUri != null){
				Util.ringtone = RingtoneManager.getRingtone(context, soundUri);
			    Util.ringtone.play();	
			}
			
			
			Log.d("", "context : " + context.getClass().toString());
			
			reloadMainFeed(context);
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.getMessage());
		}
	}
	
	/**
	 * Handler
	 * <pre>
	 * 알림 메시지를 Toast로 띄우는 핸들러 클래스.
	 * </pre>
	 * 
	 * @param context
	 * @param msg 서버에서 GCM을 통해 수신한 메시지전문(json타입)
	 * @return void
	 * @throws 
	 */
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {

			Map<String, Object> map = (Hashtable<String, Object>)msg.obj;
			String msgNo = (String)map.get("msgNo");
			String msgCtnt = (String)map.get("msgCtnt");
			String msgSendGbn = (String)map.get("msgSendGbn");
			String msgScrtYn = (String)map.get("msgScrtYn");
			Context context = (Context)map.get("context");
			PNSToast toast = new PNSToast(context);
			toast.showToast(msgNo, msgCtnt, msgSendGbn, msgScrtYn, Toast.LENGTH_LONG);
			
//			Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP, 0, 0);
			toast.show();
			
			
		}
	};

	
	/**
	 * getBitmapFromURL
	 * <pre>
	 * URL경로의 이미지를 Bitmap으로 변환.
	 * 
	 * </pre>
	 * 
	 * @param src 
	 * @return void
	 * @throws 
	 */
	public Bitmap getBitmapFromURL(String src) {
		if(src == null || "".equals(src)) return null;
		
		HttpURLConnection connection = null;
		InputStream input = null;
		try {
			URL url = new URL(src);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
//			connection.connect();
			input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			return null;
		}finally{
			if(input != null){try {input.close();} catch (IOException e) {Log.e(TAG, e.getMessage());}}
			if(connection!=null)connection.disconnect();
		}
	}

	/**
	 * getBitmapFromURL
	 * <pre>
	 * URL경로의 이미지를 Bitmap으로 변환.
	 * 
	 * </pre>
	 * 
	 * @param src 
	 * @return void
	 * @throws 
	 */
	public String getSoundFileFromURL(String path, String src, String svrPath) {
		if(src == null || "".equals(src)) return null;
		
		if(svrPath == null || "".equals(svrPath) || "null".equals(svrPath)) return null;
		
		String soundUrl = svrPath;
		if(!svrPath.startsWith("http://")) {
			soundUrl = Util.getSharedData("serverRootUrl","") + svrPath;
		}
		
		HttpURLConnection connection = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			File file = new File(path+"/"+src);
			if(file.exists()){
				Log.d("","exists file : " + file.getPath());
				return file.getPath();
			}else{
				URL url = new URL(soundUrl);
				connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				
				inputStream = connection.getInputStream();
				outputStream = new FileOutputStream(file);
				byte[] buf = new byte[1024];
				int len = 0;
				// 끝까지 읽어들이면서 File 객체에 내용들을 쓴다
				while ((len = inputStream.read(buf)) > 0){
					outputStream.write(buf, 0, len);
				}
				outputStream.close();
				inputStream.close();

				return file.getPath();
			}

		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			
		}finally{
			if(outputStream != null){try {outputStream.close();} catch (IOException e) {Log.e(TAG, e.getMessage());}}
			if(inputStream != null){try {inputStream.close();} catch (IOException e) {Log.e(TAG, e.getMessage());}}
			if(connection!=null)connection.disconnect();
		}
		return null;
	}
	
	
	/**
	 * getBitmapFromURL
	 * <pre>
	 * URL경로의 이미지를 Bitmap으로 변환.
	 * 
	 * </pre>
	 * 
	 * @param src 
	 * @return void
	 * @throws 
	 */
	public String getMsgFromURL(String serverUrl, String msgNo) {
		if(serverUrl == null || "".equals(serverUrl) || "".equals(msgNo)) return null;
		
		HttpURLConnection conn = null;
		OutputStream output = null;
		BufferedReader in = null;
		JSONObject inJobj = new JSONObject();

		try {
			URL url = new URL(serverUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", "application/json");
			inJobj.put("fsp_action", "PNSMobileMessageAction");
			inJobj.put("fsp_cmd", "select");
			inJobj.put("msg_no", msgNo);
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			inJobj.put("user_no", Util.getSharedData("userId",""));
			
			// Send post request
			conn.setDoOutput(true);
			output = conn.getOutputStream();
			output.write( inJobj.toString().getBytes("utf-8") );
			output.flush();
			output.close();
			
			int responseCode = conn.getResponseCode();
			Log.d("","\nSending 'POST' request to URL : " + url);
			Log.d("","Post parameters : " + inJobj.toString());
			Log.d("","Response Code : " + responseCode);
	 
			in = new BufferedReader(
			        new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			return response.toString();
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage());
			return null;
		} catch (MalformedURLException e) {
			Log.e(TAG, e.getMessage());
			return null;
		} catch (ProtocolException e) {
			Log.e(TAG, e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			return null;
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
			return null;
		}finally{
			if(in != null){try {in.close();} catch (IOException e) {Log.e(TAG, e.getMessage());}}
			if(output != null){try {output.close();} catch (IOException e) {Log.e(TAG, e.getMessage());}}
			if(conn!=null)conn.disconnect();
		}
	}
	
	
	public static boolean isForeground(Context ctx, String pkgNm) {
		boolean returnValue = false;
		ActivityManager manager = (ActivityManager)ctx.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = manager.getRunningAppProcesses();
		
		for(int i=0;i<runningAppProcessInfo.size();i++){
			if(runningAppProcessInfo.get(i).processName.equals(pkgNm)) {
				returnValue = true;
				break;
			}
		}
		
		return returnValue;
	}
	
	
	public void reloadMainFeed(Context context) {
		
	}
	
}