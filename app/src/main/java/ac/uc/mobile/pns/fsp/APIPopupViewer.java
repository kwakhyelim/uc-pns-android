/**
 * <pre>
 * 제목등 간단내용 기술.
 *
 * 상세 설명이 필요할 경우 기술함.
 * @title    APIPopupViewer.java
 * @project  ESNMobileAndroid
 * @date     2013. 6. 20. 오후 2:32:23
 * @version  ver1.0
 * @author   kjksds
 * </pre>
 */
package ac.uc.mobile.pns.fsp;

import java.io.File;
import java.net.URISyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sz.fspmobile.api.base.BaseFSPApi;
import com.sz.fspmobile.api.spec.FSPResult;
import com.sz.fspmobile.api.spec.FSPResult.ErrorCode;

import ac.uc.mobile.pns.WebMainActivity;
import ac.uc.mobile.pns.main.PNSMainActivity;
import ac.uc.mobile.pns.setup.PNSSettingActivity;
import ac.uc.mobile.pns.util.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * @author kjksds
 *
 */
public class APIPopupViewer extends BaseFSPApi{
	private static final String TAG = APIPopupViewer.class.getSimpleName();
	public static final int REQUEST_SVR_UPLOAD_USER_IMAGE = 200;
	public static final int FSP_API_OPENPOPUP = 100;
	public static final int FSP_API_ALBUM = 101;
	public static final int FSP_API_ALBUM_CROP = 102;
	public static final int REQUEST_ACTIVITY_CROP = 9011;
	public static final int REQUEST_ID_ACTIVITY_SETTING = 9001;
	public String callbackName = null;
	

	public final static String SHARED_DATA_ID = "FSPmobile_SharedData";
	/* (non-Javadoc)
	 * @see com.sz.fspmobile.api.spec.FSPApi#execute(java.lang.String, org.json.JSONArray, java.lang.String)
	 */
	@Override
	public FSPResult execute(String method, JSONArray jsonArr, String callback) {
		// TODO Auto-generated method stub
		Log.d(TAG, "APIPopupViewer start....");
		Log.d(TAG, "method = "+ method);
		Log.d(TAG, "jsonArr = "+ jsonArr);
		Log.d(TAG, "callback = "+ callback);
		this.callbackName = callbackName;
		FSPResult result = null;
		try {
			if( method.equals("openPopup")) {
				result = doPopup(jsonArr.getJSONObject(0));
			} else if( method.equals("closePopup")) {
				result = closePopup(jsonArr);
			} else if( method.equals("openSetup")){
				result = doSetup(jsonArr.getJSONObject(0));
			} else if( method.equals("download")){
				result = download(jsonArr.getJSONObject(0));	
			} else if( method.equals("openUrl")){
				result = openUrl(jsonArr.getJSONObject(0));	
			} else if( method.equals("callApp")){
				result = callApp(jsonArr.getJSONObject(0));			
			} else {
				result = new FSPResult(FSPResult.ErrorCode.INVALID_METHOD);
			}
		} catch( JSONException e){
			result = new FSPResult(ErrorCode.ERROR, e.getMessage());
		}
		return result;
	}
	
	/**
	 * 간략한 내용
	 *
	 * 상세설명이 필요 할 경우 여기에 기술
	 * @title doPopup
	 * @param  
	 * @return FSPResult
	 */
	public FSPResult doPopup(JSONObject jsonObj) throws JSONException {
		Activity activity = getActivity();
		FSPResult result = new FSPResult(ErrorCode.NO_RESULT);
		
		((PNSMainActivity)activity).goOpenPage(jsonObj.getString("popupUrl"), jsonObj.getString("anim"));
		
		
		result.setKeepCallback(true);
		
		return result;
	}
	
	/**
	 * 간략한 내용
	 *
	 * 상세설명이 필요 할 경우 여기에 기술
	 * @title closePopup
	 * @param  
	 * @return FSPResult
	 */
	public FSPResult closePopup(JSONArray jsonArr) throws JSONException {
		JSONObject jsonObj = null;
		//Log.d(TAG,"jsonArr.length() = "+ jsonArr.get(0).toString());
		//Log.d(TAG,"jsonArr = "+ jsonArr);
		if("null".equals(jsonArr.get(0).toString())) {
			jsonObj = new JSONObject();
		}else{
			jsonObj = jsonArr.getJSONObject(0);
		}
		
		Activity activity = getActivity();
		
		
		
		FSPResult result = null;

		((PNSMainActivity)activity).goBackPage(jsonObj.getString("anim"));
		
		if(jsonObj.has("refresh") &&  "yes".equals(jsonObj.getString("refresh"))){
			((PNSMainActivity)activity).goBackPage(jsonObj.getString("anim"),"yes");
		}else{
			((PNSMainActivity)activity).goBackPage(jsonObj.getString("anim"));
		}

		

		return result;
	}
	
	
	
	
	/**
	 * 간략한 내용
	 *
	 * 상세설명이 필요 할 경우 여기에 기술
	 * @title doPopup
	 * @param  
	 * @return FSPResult
	 */
	public FSPResult doSetup(JSONObject jsonObj) throws JSONException {
		
		Activity activity = getActivity();
		((PNSMainActivity)activity).setAlive(true);		
		//Log.d("","doSetup : " + activity.getClass().getSimpleName());
		if("PNSMainActivity".equals(activity.getClass().getSimpleName())) {
			((PNSMainActivity)activity).setAlive(true);
		}
		
		FSPResult result = null;
		
		Intent intent = new Intent (activity, PNSSettingActivity.class);
		
		//데이터를 저장하여 보낸다. (urlType, url, width, height) 
		//activity.startActivity(intent);
		activity.startActivityForResult(intent, REQUEST_ID_ACTIVITY_SETTING);
		activity.overridePendingTransition(ac.uc.mobile.pns.R.anim.slideup, ac.uc.mobile.pns.R.anim.slidedown);

		
		
		return result;
	}
	
	/**
	 * 간략한 내용
	 *
	 * 상세설명이 필요 할 경우 여기에 기술
	 * @title doPopup
	 * @param  
	 * @return FSPResult
	 */
	@SuppressLint("NewApi") 
	public FSPResult download(JSONObject jsonObj) throws JSONException {
		FSPResult result = null;
		String filepath = jsonObj.getString("path");
		String orgfilenm = jsonObj.getString("fileNm");
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(filepath));
		request.setDescription("PNS Download ");
		request.setTitle(orgfilenm);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		    request.allowScanningByMediaScanner();
		    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		}
		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, orgfilenm);
		DownloadManager manager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
		manager.enqueue(request);
		
		Toast toast = Toast.makeText(getActivity(),	"파일을 다운로드 합니다.", Toast.LENGTH_SHORT);
		toast.show();
		
		return result;
	}
	
	/**
	 * 간략한 내용
	 *
	 * 상세설명이 필요 할 경우 여기에 기술
	 * @title doPopup
	 * @param  
	 * @return FSPResult
	 */
	public FSPResult openUrl(JSONObject jsonObj) throws JSONException {
		Activity activity = getActivity();
	//	((WebMainActivity)activity).setAlive(true);

		FSPResult result = null;
		String url = "";
		if(jsonObj.has("url") && jsonObj.getString("url") != null) {
			url = jsonObj.getString("url");
		}
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri data = Uri.parse(url);
		intent.setData(data);
		activity.startActivity(intent);
		
		return result;
	}
	
	
	/**
	 * 간략한 내용
	 *
	 * 상세설명이 필요 할 경우 여기에 기술
	 * @title doPopup
	 * @param  
	 * @return FSPResult
	 */
	private FSPResult callApp(JSONObject jsonObj)  {
		Activity activity = getActivity();
		
		FSPResult result = null;
		Log.d("", "callApp : params = " + jsonObj.toString());
		try { 
			if(jsonObj.has("customURL") && jsonObj.getString("customURL") != null && !"".equals(jsonObj.getString("customURL"))) {
				Intent intent = Intent.parseUri(jsonObj.getString("customURL"), Intent.URI_INTENT_SCHEME);
				Intent existPackage = activity.getPackageManager().getLaunchIntentForPackage(intent.getPackage());
				if (existPackage != null) {
					Log.d("", "start activity  .....  customUrl : "+ jsonObj.getString("customURL"));
					try{
						activity.startActivity(intent);
					} catch(NullPointerException ae){
						try{
							Log.d("", "start activity  .....  packageID : "+ jsonObj.getString("packageID")); 
		            		Intent packageIntent = new Intent(); 
		        		    PackageManager pm = activity.getPackageManager(); 
		        		    packageIntent = pm.getLaunchIntentForPackage(jsonObj.getString("packageID"));
		        		    
		        		    activity.startActivity(packageIntent);
	            		}catch (NullPointerException e) { 
	            			    Log.e("","not install App, storeURL=" + jsonObj.getString("storeURL")); 
	            			    if(jsonObj.has("storeURL") && jsonObj.getString("storeURL") != null && !"".equals(jsonObj.getString("storeURL"))) {
	            			    	Uri uri = Uri.parse(jsonObj.getString("storeURL")); 
	                			    Intent i = new Intent(Intent.ACTION_VIEW, uri); 
	                			    activity.startActivity(i); 
	            			    }else{
	            			    	Uri uri = Uri.parse("market://details?id="+jsonObj.getString("packageID")); 
	                			    Intent i = new Intent(Intent.ACTION_VIEW, uri); 
	                			    activity.startActivity(i); 
	            			    }
	            		}
					}
	            } else if(jsonObj.has("storeURL") && jsonObj.getString("storeURL") != null && !"".equals(jsonObj.getString("storeURL"))) {
	            		Intent marketIntent = new Intent(Intent.ACTION_VIEW);
	            		Log.d("", "start activity  .....  storeURL : "+ jsonObj.getString("storeURL"));
	            		marketIntent.setData(Uri.parse(jsonObj.getString("storeURL")));
	            		activity.startActivity(marketIntent);
	            } else if(jsonObj.has("packageID") && jsonObj.getString("packageID") != null && !"".equals(jsonObj.getString("packageID"))) {
	            	Log.d("", "start activity  .....  packageID : "+ jsonObj.getString("packageID"));
	            		try{
		            		Intent packageIntent = new Intent(); 
		        		    PackageManager pm = activity.getPackageManager(); 
		        		    packageIntent = pm.getLaunchIntentForPackage(jsonObj.getString("packageID"));
		        		    activity.startActivity(packageIntent);
	            		}catch (NullPointerException e) { 
	            			    Log.e("","not install App, e=" + e.toString()); 
	            			    Uri uri = Uri.parse("market://details?id="+jsonObj.getString("packageID")); 
	            			    Intent i = new Intent(Intent.ACTION_VIEW, uri); 
	            			    activity.startActivity(i); 
	            		}
            	}
			}else if(jsonObj.has("packageID") && jsonObj.getString("packageID") != null && !"".equals(jsonObj.getString("packageID"))) {
				try{
            		Intent packageIntent = new Intent(); 
        		    PackageManager pm = activity.getPackageManager(); 
        		    packageIntent = pm.getLaunchIntentForPackage(jsonObj.getString("packageID"));
        		    activity.startActivity(packageIntent);
        		}catch (NullPointerException e) { 
        			    Log.e("","not install App, e=" + e.toString()); 
        			    if(jsonObj.has("storeURL") && jsonObj.getString("storeURL") != null && !"".equals(jsonObj.getString("storeURL"))) {
        			    	Uri uri = Uri.parse(jsonObj.getString("storeURL")); 
            			    Intent i = new Intent(Intent.ACTION_VIEW, uri); 
            			    activity.startActivity(i); 
        			    }else{
        			    	Uri uri = Uri.parse("market://details?id="+jsonObj.getString("packageID")); 
            			    Intent i = new Intent(Intent.ACTION_VIEW, uri); 
            			    activity.startActivity(i); 
        			    }
        		}
			}
		} catch(JSONException e){
			Log.e("",e.getMessage());
		} catch(URISyntaxException e){
			Log.e("",e.getMessage()); 
		}
		return result;
	}

	
	/**
	 * 처리 결과 발생 시 호출된다.<p>
	 * 
	 * @Override
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		//Log.d("","requestCode = "+requestCode+"   , resultCode = "+resultCode);
		if( requestCode == FSP_API_OPENPOPUP ) {
			switch( resultCode ) {
			case Activity.RESULT_OK:
				FSPResult result = new FSPResult(ErrorCode.OK);
//					sendJavaScript(result.getCallbackString(callbackName));
				break;
			case Activity.RESULT_CANCELED:
				sendErrorToWeb("cancelled.");
				break;
			default:
				sendErrorToWeb("not complete.");
				break;
			}
		}else if( requestCode == FSP_API_ALBUM ) {
			switch( resultCode ) {
			case Activity.RESULT_OK:
				FSPResult result = new FSPResult(ErrorCode.OK);
//					sendJavaScript(result.getCallbackString(callbackName));
				break;
			case Activity.RESULT_CANCELED:
				sendErrorToWeb("cancelled.");
				break;
			default:
				sendErrorToWeb("not complete.");
				break;
			}
		}else if( requestCode == FSP_API_ALBUM_CROP ) {
			switch( resultCode ) {
			case Activity.RESULT_OK:
				String[] imgPath = data.getStringArrayExtra("imgPath");
				if(imgPath.length > 0) {
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
					File savedFile = new File(Util.getCacheDirectory(getActivity(), "user"), prefs.getString("userId", ""));
					
					File imgFile = new  File(imgPath[0]);
					Uri uri1 = Uri.fromFile(imgFile);
					Intent intent = new Intent("com.android.camera.action.CROP");
					intent.setDataAndType(uri1, "image/*");
					intent.putExtra("outputX", 300);
					intent.putExtra("outputY", 300);
					intent.putExtra("aspectX", 10);
					intent.putExtra("aspectY", 10);
					intent.putExtra("scale", true);
					intent.putExtra("noFaceDetection", true);
					intent.putExtra("output", Uri.fromFile(savedFile));
					getActivity().startActivityForResult(intent, REQUEST_ACTIVITY_CROP);
					
					FSPResult result = new FSPResult(ErrorCode.OK);
//					sendJavaScript(result.getCallbackString(callbackName));
					
				}
				break;
			case Activity.RESULT_CANCELED:
				sendErrorToWeb("cancelled.");
				break;
			default:
				sendErrorToWeb("not complete.");
				break;
			}
		}
		
	}
	
	public void sendErrorToWeb(String errmsg) {
		FSPResult result = new FSPResult(ErrorCode.ERROR, errmsg);
//		sendJavaScript(result.getCallbackString(callbackName));
	}

	
	
}
