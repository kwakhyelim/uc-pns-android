package ac.uc.mobile.pns.setup;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sz.fspmobile.BaseActivity;
import com.sz.fspmobile.config.AppConfig;
import com.sz.fspmobile.config.UserConfig;
import ac.uc.mobile.pns.R;
import ac.uc.mobile.pns.base.CommonConfirmDialog;
import ac.uc.mobile.pns.base.service.NetworkService;
import ac.uc.mobile.pns.base.service.NetworkServiceListener;

import ac.uc.mobile.pns.util.Util;


public class PNSSettingActivity extends BaseActivity implements NetworkServiceListener {
	private static final String TAG = PNSSettingActivity.class.getSimpleName();
	private final static int REQUEST_SVR_SELECT_USER = 1100;
	private final static int REQUEST_SVR_UPDATE_USER = 2100;
	private final static int REQUEST_SVR_UPLOAD_USER_IMAGE = 2200;
	private final static int REQUEST_SVR_DELETE_MESSAGE = 2300;
	private final static int REQUEST_ACTIVITY_SECRET_SETUP = 9001;
	private final static int REQUEST_ACTIVITY_ALARM_LIST = 9002;
	private final static int REQUEST_ACTIVITY_ALBUM = 9003;
	//사용자사진 크롭 액티비티 호출 아이디
	private static final int REQUEST_ACTIVITY_CROP = 9011;
	
	public String SERVLET_URL ;
	

	ImageButton btnBack;
	ImageView imgProf;
	ImageButton btnImgProf;
	
	EditText editName;
	EditText editTel;
	
	public ImageButton btnPrivOnOff;
	public ImageButton btnMyFavOnOff;
	


	//	ImageButton btnReplyOnOff;
	ImageButton btnSecurityOnOff;
	RelativeLayout layoutAlarmSetup;
	
	TextView txtAlmNm;
	TextView txtVersion;
	LinearLayout layoutAllDelete;
	
	private CommonConfirmDialog dialog;
	
	SharedPreferences prefs;
	NetworkService net;
	
	/**
	 * 설정정보.<p>
	 */
	private SharedPreferences sharedPreference;
	public final static String SHARED_DATA_ID = "FSPmobile_SharedData";
	
	
	



	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		sharedPreference = getSharedPreferences(SHARED_DATA_ID, MODE_PRIVATE);
		SERVLET_URL = getGlobal().getServerConfig().getServerRootUrl() + "/WebJSON";
		net = new NetworkService(this, this);
		getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		
		setContentView(R.layout.ntong_setting);
		
		btnBack = (ImageButton)findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveUserSetting();

			}
		});
		
		imgProf = (ImageView)findViewById(R.id.imgProf);
		

		
		btnImgProf = (ImageButton)findViewById(R.id.btnImgProf);
		btnImgProf.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		
		
		editName = (EditText)findViewById(R.id.editName);
		editTel = (EditText)findViewById(R.id.editTel);
		editName.setFocusableInTouchMode(false); 
		editTel.setFocusableInTouchMode(false);
		
		editName.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override 
            public void onGlobalLayout() {
                editName.setSelection(editName.getText().toString().length());
            }
        });
		editTel.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override 
            public void onGlobalLayout() {
            	editTel.setSelection(editTel.getText().toString().length());
            }
        });
		
		
		btnPrivOnOff = (ImageButton)findViewById(R.id.btnPrivOnOff);
		btnMyFavOnOff = (ImageButton)findViewById(R.id.btnMyFavOnOff);
//		btnReplyOnOff = (ImageButton)findViewById(R.id.btnReplyOnOff);
		btnSecurityOnOff = (ImageButton)findViewById(R.id.btnSecurityOnOff);
		layoutAlarmSetup = (RelativeLayout)findViewById(R.id.layoutAlarmSetup);
		layoutAlarmSetup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(PNSSettingActivity.this, PNSAlarmListActivity.class);
				startActivityForResult(intent, REQUEST_ACTIVITY_ALARM_LIST);
				overridePendingTransition(R.anim.rightin, R.anim.leftout);
			}
			
		});
		
		txtAlmNm = (TextView)findViewById(R.id.txtAlmNm);
		txtVersion = (TextView)findViewById(R.id.txtVersion);
		
		String userMsgPasswd = Util.getSharedData("userMsgPasswd","");
		//Log.d("","userMsgPasswd : "+userMsgPasswd);
		if(!"".equals(userMsgPasswd)){
			btnSecurityOnOff.setSelected(true);
		}else{
			btnSecurityOnOff.setSelected(false);
		}
		
		if(!"".equals(Util.getSharedData("userAlmNm",""))){
			txtAlmNm.setText(Util.getSharedData("userAlmNm",""));
		}else{
			txtAlmNm.setText("지정 알림음 없음");
		}

		selectUserInfo();
		txtVersion.setText("v"+UserConfig.getSharedInstance().getCurrentAppVersion());
		
		layoutAllDelete = (LinearLayout)findViewById(R.id.layoutAllDelete);
		layoutAllDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog = new CommonConfirmDialog(PNSSettingActivity.this, "확인", "수신한 모든 메시지를 삭제하시겠습니까?", 
						new OnClickListener() {
					
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								deleteAllMessage();
								dialog.dismiss();
							}
						}, 
						new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
				dialog.show();
			}
		});
	}
	
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		
		
		finish();
	}


	
	
	public void clickToggleToSetting(View v){
		
		if(v.getId() == R.id.btnPrivOnOff ){
			if(btnPrivOnOff.isSelected()){				
				btnPrivOnOff.setSelected(false);
			}else{
				btnPrivOnOff.setSelected(true);
			}
		}else if(v.getId() == R.id.btnMyFavOnOff ){
			if(btnMyFavOnOff.isSelected()){				
				btnMyFavOnOff.setSelected(false);
			}else{
				btnMyFavOnOff.setSelected(true);
			}
//		}else if(v.getId() == R.id.btnReplyOnOff ){
//			if(btnReplyOnOff.isSelected()){				
//				btnReplyOnOff.setSelected(false);
//			}else{
//				btnReplyOnOff.setSelected(true);
//			}
		}else if(v.getId() == R.id.btnSecurityOnOff ){
			if(btnSecurityOnOff.isSelected()){				
				msgSecretSetup(false);
			}else{
				msgSecretSetup(true);
			}
		}
		
	}

	
	public void msgSecretSetup(boolean onOff) {
		if(onOff) {
			btnSecurityOnOff.setSelected(true);
//			Intent intent = new Intent(PNSSettingActivity.this, PNSWebViewActivity.class);
			String popupUrl = "/mobile/setup_secret.html?user_no="+Util.getSharedData("userId", "");
			
			Intent intent = AppConfig.getSharedInstance().getMyAppConfig().getNoticePopupPageIntent(this);
			intent.putExtra("popupUrl", popupUrl);
			intent.putExtra("page", "setup");
			startActivityForResult(intent, REQUEST_ACTIVITY_SECRET_SETUP);
			overridePendingTransition(R.anim.rightin, R.anim.leftout);
		}else {
			btnSecurityOnOff.setSelected(false);
			Editor editor = sharedPreference.edit();
			editor.remove("userMsgPasswd");
			editor.commit();
		}
	}
	
	public void selectUserInfo(){
		
		//Log.d("","user_no : "+prefs.getString("userId", ""));
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("sqlId", "mobile/user:PNS_USER_S01");
		data.put("user_no", Util.getSharedData("userId",""));
		data.put("user_id", Util.getSharedData("userId",""));
		data.put("dvic_id", UserConfig.getSharedInstance().getDeviceID());	
		
		net.launchRequestFsp(SERVLET_URL, data, REQUEST_SVR_SELECT_USER, true);
	}

	public void saveUserSetting(){
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("sqlId", "mobile/user:PNS_USER_U01");
		data.put("user_no",Util.getSharedData("userId",""));
		
		data.put("user_nm", editName.getText().toString());
		data.put("user_telno", editTel.getText().toString());

		if(btnPrivOnOff.isSelected()){
			data.put("tong_user_msg_push_yn", "Y");
		}else{
			data.put("tong_user_msg_push_yn", "N");
		}
		if(btnMyFavOnOff.isSelected()){		
			data.put("tong_push_set_yn", "Y");
		}else{
			data.put("tong_push_set_yn", "N");
		}
		net.launchRequestFsp(SERVLET_URL, data, REQUEST_SVR_UPDATE_USER, true);
	}

	public void deleteAllMessage(){
		
		//Log.d("","user_no : "+prefs.getString("userId", ""));
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("sqlId", "mobile/message:PNS_MSG_RECVUSER_LST_D02");
		data.put("user_no", Util.getSharedData("userId",""));
		data.put("msg_del_yn", "Y");	
		net.launchRequestFsp(SERVLET_URL, data, REQUEST_SVR_DELETE_MESSAGE, true);
	}
	
	@Override
	public void onHandleResults(int requestId, JSONObject json) {
		// TODO Auto-generated method stub
		Log.d("PNSSettingActivity", "onHandleResults json: " + json.toString());	
		try{
			if(requestId == REQUEST_SVR_SELECT_USER){
				String errorCode = json.getString("ErrorCode");
				if("0".equals(errorCode)){
					JSONArray jarray = json.getJSONArray("ds_list");
					if(jarray.length()>0){
						JSONObject jobj = jarray.getJSONObject(0); 
						if(jobj.has("user_nm")) {
							editName.setText(jobj.getString("user_nm"));
							editTel.setText(jobj.getString("user_telno"));
							
							if("Y".equals(jobj.getString("tong_user_msg_push_yn"))){
								btnPrivOnOff.setSelected(true);
							}else{
								btnPrivOnOff.setSelected(false);
							}
							if("Y".equals(jobj.getString("tong_push_set_yn"))){
								btnMyFavOnOff.setSelected(true);			
							}else{
								btnMyFavOnOff.setSelected(false);
							}
						}else{
							editName.setText(jobj.getString("USER_NM"));
							if(jobj.has("USER_TELNO")) {
								editTel.setText(jobj.getString("USER_TELNO"));
							}else if(jobj.has("MOBL")) {
								editTel.setText(jobj.getString("MOBL"));
							}
							
							if(jobj.has("TONG_USER_MSG_PUSH_YN")) {
								if("Y".equals(jobj.getString("TONG_USER_MSG_PUSH_YN"))){
									btnPrivOnOff.setSelected(true);
								}else{
									btnPrivOnOff.setSelected(false);
								}
							}else if(jobj.has("MSG_ALM_YN")) {
								if("Y".equals(jobj.getString("MSG_ALM_YN"))){
									btnPrivOnOff.setSelected(true);
								}else{
									btnPrivOnOff.setSelected(false);
								}
							}
							
							if(jobj.has("TONG_PUSH_SET_YN")) {
								if("Y".equals(jobj.getString("TONG_PUSH_SET_YN"))){
									btnMyFavOnOff.setSelected(true);			
								}else{
									btnMyFavOnOff.setSelected(false);
								}
							}else if(jobj.has("NEW_FEED_ALM_YN")) {
								if("Y".equals(jobj.getString("NEW_FEED_ALM_YN"))){
									btnMyFavOnOff.setSelected(true);			
								}else{
									btnMyFavOnOff.setSelected(false);
								}
							}
						}
					}
				}
			}else if(requestId == REQUEST_SVR_UPDATE_USER){
				String errorCode = json.getString("ErrorCode");
				if("0".equals(errorCode)){
					prefs = PreferenceManager.getDefaultSharedPreferences(this);
					Util.setSharedData("userNm", editName.getText().toString());
					Util.setSharedData("userTelNo", editTel.getText().toString());
					setResult(9001);
					finish();
					overridePendingTransition(R.anim.leftin, R.anim.rightout);
				}else{
					dialog = new CommonConfirmDialog(this, CommonConfirmDialog.ALERT_TYPE, "안내", json.getString("ErrorMsg"));
					dialog.show();
				}
			}else if(requestId == REQUEST_SVR_UPLOAD_USER_IMAGE){
				File f1 = new File(savedFile.getPath());
				File f2 = new File(savedFile.getParent()+"/"+json.getString("RTN_DATA"));
				f1.renameTo(f2);
				Util.setSharedData("userImgChange", "Y");
				Util.setSharedData("userImgPath", f2.getPath());
				Util.setSharedData("userImgServerPath", getGlobal().getServerConfig().getServerRootUrl() + "/content/userImage/" + f2.getName());
			}else if(requestId == REQUEST_SVR_DELETE_MESSAGE) {
				Util.setSharedData("refreshYN", "Y");
			}
		}catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
	}
	
	public File savedFile = new File(Util.getCacheDirectory(this, "user"), String.valueOf(System.currentTimeMillis()));
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == REQUEST_ACTIVITY_SECRET_SETUP){
			
			String userMsgPasswd = Util.getSharedData("userMsgPasswd", "");
			//Log.d("","onActivityResult userMsgPasswd : "+userMsgPasswd);
			if(!"".equals(userMsgPasswd)){
				btnSecurityOnOff.setSelected(true);
			}else{
				btnSecurityOnOff.setSelected(false);
			}
			
			
		}else if(requestCode == REQUEST_ACTIVITY_ALARM_LIST){
			
			if(!"".equals(Util.getSharedData("userAlmNm", ""))){
				txtAlmNm.setText(Util.getSharedData("userAlmNm", ""));
			}else{
				txtAlmNm.setText("지정 알림음 없음");
			}
			
		}else if(requestCode == REQUEST_ACTIVITY_ALBUM){
			
			switch(resultCode){
			case Activity.RESULT_OK:
				String[] imgPath = data.getStringArrayExtra("imgPath");
				if(imgPath.length > 0) {
					
					
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
					startActivityForResult(intent, REQUEST_ACTIVITY_CROP);
				}
				break;
			case Activity.RESULT_CANCELED:
				break;
			default:
				break;
			}
			
		}else if(requestCode == REQUEST_ACTIVITY_CROP) {
			switch(resultCode){
			case Activity.RESULT_OK:
				

	        	BitmapFactory.Options options = new BitmapFactory.Options();
	        	options.inPurgeable = true;
	        	options.inPreferredConfig = Bitmap.Config.RGB_565;
				Bitmap photo = BitmapFactory.decodeFile(savedFile.getPath(), options);
				imgProf.setImageBitmap(Util.getCircleCroppedBitmap(photo));
				
				System.gc();

				Util.getSharedData("userImagePath", savedFile.getPath());
				uploadUserImage();
				
				break;
			case Activity.RESULT_CANCELED:
				break;
			default:
				break;
			}		
		}
	}
	
	public void uploadUserImage() {
		
		Map<String, Object> reqdata = new HashMap<String, Object>();
		Map<String, Object> fileMap = new HashMap<String, Object>();
		reqdata.put("user_no", Util.getSharedData("userId", ""));
		reqdata.put("fsp_action", "MobileUserAction");
		reqdata.put("fsp_cmd", "uploadUserImage");
		fileMap.put("fileList", savedFile.getPath());
		fileMap.put("isFile", "Y");
		fileMap.put("params", new JSONObject(reqdata));

		net.launchRequest(getGlobal().getServerConfig().getServerRootUrl() +"/WebFileUpload", fileMap, REQUEST_SVR_UPLOAD_USER_IMAGE, false);
		
	}
}