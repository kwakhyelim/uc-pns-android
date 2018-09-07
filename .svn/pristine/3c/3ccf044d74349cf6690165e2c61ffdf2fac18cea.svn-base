/**
 * 
 */
package ac.uc.mobile.pns.base.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sz.fspmobile.app.FSPGlobalApplication;

/**
 * @author sz-jkkoo
 *
 */
public class NetworkService  {
	private static final String TAG = NetworkService.class.getSimpleName();
	//서측통신을 위한 쓰레드
	private NetworkThread networkThread; 
	
	//서버통신후 처리할 핸들러
	private Handler handler;
	
	//서버 호출을 구분할 요청아이디.
	private int requestId;
	
	protected ProgressDialog progressDialog;
	
	//컨텍스트
	private Context mContext;
	
	NetworkServiceListener onResult;
	
	public NetworkService(Context mContext, Object obj) {
		super();
		this.mContext = mContext;
		this.onResult = (NetworkServiceListener)obj;
	}


	public void setmListener(NetworkServiceListener listenet) {
		this.onResult = listenet;
	}


	/**
	 * showProgress
	 * <pre>
	 * 
	 * �꾨줈洹몃젅��dialog瑜��쒖떆.
	 * 
	 * </pre>
	 * 
	 * @param 
	 * @return void
	 * @throws 
	 */
	protected void showProgress() {
		progressDialog = ProgressDialog.show(this.mContext,  "", this.mContext.getString(ac.uc.mobile.pns.R.string.esn_progress), true);
	}
	
	/**
	 * hideProgress
	 * <pre>
	 * 
	 * 
	 * 
	 * </pre>
	 * 
	 * @param 
	 * @return void
	 * @throws 
	 */
    protected void hideProgress() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
    
    
	/**
	 * launchRequest
	 * <pre>
	 * 서버측 통신 요청(http콜)
	 * </pre>
	 * 
	 * @param url 서버측경로
	 * @param paramMap 파라미터맵
	 * @param viewList 뷰목록
	 * @param requestId 전송요청아이디
	 * @param showProgress 인디케이터
	 * @return void
	 * @throws 
	 */	
	public void launchRequest(String url, Map<String,Object> paramMap,  int requestId, boolean showProgress) {
		this.requestId = requestId;

		
		if(paramMap.get("APPS_ID") == null || "".equals(paramMap.get("APPS_ID"))) {
			paramMap.put("APPS_ID", this.mContext.getPackageName());
		}
		
		if (networkThread == null) {
			if (handler == null) {
				handler = new DefaultHandler();
			}
	
			
			// 연결상태를 확인한다.
			ConnectivityManager connectivity = (ConnectivityManager)this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo wifiInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo mobileInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

			// 네트웍 접속여부 확인
			if (!(wifiInfo.isConnected() || mobileInfo.isConnected())) {
//				alert("전송할 수 없습니다. 네트워크(WIFI, 3G) 상태를 확인바랍니다.");
				return;
			} 
			if(showProgress)
				showProgress();

			networkThread = new NetworkThread(url, paramMap, handler, requestId);
			networkThread.start();
		}else{

			if (handler == null) {
				handler = new DefaultHandler();
			}
			
			if(showProgress)
				showProgress();
			
			networkThread = new NetworkThread(url, paramMap, handler, requestId);
			networkThread.start();
			
		}
	}
	
	/**
	 * resetForRequest
	 * <pre>
	 * 서버호출 작업 초기화(종료)
	 * </pre>
	 * 
	 * @param 
	 * @return void
	 * @throws 
	 */	
	protected void resetForRequest() {

		networkThread = null;
		hideProgress();
	}
	
	/**
	 * DefaultHandler
	 * <pre>
	 * 서버호출후 핸들러
	 * </pre>
	 * 
	 * @param 
	 * @return void
	 * @throws 
	 */	
	class DefaultHandler extends Handler {
		@Override
		public void handleMessage(Message message) {
			resetForRequest();
			if(message.what == ac.uc.mobile.pns.R.id.request_succeeded){
				//onHandleResults(message.arg1, (JSONObject) message.obj);
				onResult.onHandleResults(message.arg1, (JSONObject) message.obj);
			}else if(message.what == ac.uc.mobile.pns.R.id.request_failed){
//					alert(R.string.dialog_tit_request_failed, R.string.dialog_msg_request_failed);
			}else if(message.what == ac.uc.mobile.pns.R.id.request_canceled){
			}
		}
	}

	
	/**
	 * launchRequestFsp
	 * <pre>
	 * FspServer의 기본포맷에 맞춘요청
	 * </pre>
	 * 
	 * @param url 서버측경로
	 * @param paramMap 파라미터맵
	 * @param viewList 뷰목록
	 * @param requestId 전송요청아이디
	 * @param showProgress 인디케이터
	 * @return void
	 * @throws 
	 */
	public void launchRequestFsp(String url, Map<String,Object> paramMap, int requestId, boolean showProgress) {
		
		Map<String,Object> dic = paramMap;
		Map<String,Object> ds_cmd_dic = new HashMap<String,Object>();
//		Map<String,Object> ds_input_dic = paramMap;
		
		ds_cmd_dic.put("TYPE","N");
		ds_cmd_dic.put("SQL_ID",paramMap.get("sqlId"));
		ds_cmd_dic.put("KEY_SQL_ID","");
		ds_cmd_dic.put("KEY_INCREMENT","");
		ds_cmd_dic.put("CALLBACK_SQL_ID","");
		ds_cmd_dic.put("INSERT_SQL_ID","");
		ds_cmd_dic.put("UPDATE_SQL_ID","");
		ds_cmd_dic.put("DELETE_SQL_ID","");
		ds_cmd_dic.put("SAVE_FLAG_COLUMN","");
		ds_cmd_dic.put("KEY_ZERO_LEN","");
		ds_cmd_dic.put("EXEC_TYPE","");
		ds_cmd_dic.put("USE_INPUT","Y");
		ds_cmd_dic.put("USE_ORDER","N");
		ds_cmd_dic.put("EXEC","");
		ds_cmd_dic.put("FAIL","");
		ds_cmd_dic.put("FAIL_MSG","");
		ds_cmd_dic.put("EXEC_CNT","0");
		ds_cmd_dic.put("MSG","");
		
		
//		List<Object> ds_cmd_arr = new ArrayList<Object>();
//		ds_cmd_arr.add(ds_cmd_dic);
		

		
		JSONArray jsonArray = new JSONArray();
		try {
			jsonArray.put(0, new JSONObject(ds_cmd_dic));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.getMessage());
		}
		
		if(paramMap.get("fsp_action") == null || "".equals(paramMap.get("fsp_action"))) {
			dic.put("fsp_action", "xDefaultAction");
		}else{
			dic.put("fsp_action", paramMap.get("fsp_action"));
		}

		if(paramMap.get("fsp_cmd") == null || "".equals(paramMap.get("fsp_cmd"))) {
			dic.put("fsp_cmd", "execute");
		}else{
			dic.put("fsp_cmd", paramMap.get("fsp_cmd"));
		}
		
		dic.put("fsp_ds_cmd", jsonArray);
//		dic.put("ds_input", new JSONObject(ds_input_dic));
		launchRequest(url, dic, requestId, showProgress);
	}
	

	
	/**
	 * launchRequestFsp
	 * <pre>
	 * FspServer의 기본포맷에 맞춘요청
	 * </pre>
	 * 
	 * @param url 서버측경로
	 * @param paramMap 파라미터맵
	 * @param viewList 뷰목록
	 * @param showProgress 인디케이터
	 * @return void
	 * @throws 
	 */
	public void launchRequestFsp( Map<String,Object> paramMap, int requestId, boolean showProgress) {
		String url = getGlobal().getServerConfig().getServerRootUrl()+"/jsonAdm";
		launchRequestFsp(url, paramMap, requestId, showProgress);

	}
	
	public FSPGlobalApplication getGlobal() {
		return FSPGlobalApplication.getGlobalApplicationContext();
	}
}
