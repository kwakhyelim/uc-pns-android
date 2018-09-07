/**
 *
 */
package ac.uc.mobile.pns.setup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sz.fspmobile.BaseActivity;
import com.sz.fspmobile.config.UserConfig;

import ac.uc.mobile.pns.R;
import ac.uc.mobile.pns.base.service.NetworkService;
import ac.uc.mobile.pns.base.service.NetworkServiceListener;
import ac.uc.mobile.pns.setup.model.AlarmModel;
import ac.uc.mobile.pns.util.Util;


/**
 * @author sz-jkkoo
 *
 */
public class PNSAlarmListActivity extends BaseActivity implements NetworkServiceListener{
	private static final String TAG = PNSAlarmListActivity.class.getSimpleName();
	private final static int REQUEST_SVR_SELECT_ALARM = 1100;

	private String SERVLET_URL ;

	ImageButton btnBack;

	ListView listAlarm;
	ArrayList<AlarmModel> alarmList;
	PNSAlarmListAdapter adapter;


	SharedPreferences prefs;

	NetworkService net;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);

		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
				WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);;
		SERVLET_URL = getGlobal().getServerConfig().getServerRootUrl() + "/WebJSON";

		net = new NetworkService(this, this);

		setContentView(ac.uc.mobile.pns.R.layout.pns_alarm_list);

		btnBack = (ImageButton)findViewById(ac.uc.mobile.pns.R.id.btnBack);

		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(ac.uc.mobile.pns.R.anim.slidedown, ac.uc.mobile.pns.R.anim.slidedownout);
			}
		});

		listAlarm = (ListView)findViewById(ac.uc.mobile.pns.R.id.listAlarm);
		listAlarm.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				// TODO Auto-generated method stub
				setSelectAlarm(alarmList.get(position));
			}
		});

		selectAlarmList();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		finish();
		overridePendingTransition(ac.uc.mobile.pns.R.anim.leftin, ac.uc.mobile.pns.R.anim.rightout);
	}


	@Override
	public void onHandleResults(int requestId, JSONObject json) {
		// TODO Auto-generated method stub

		try{
			if(requestId == REQUEST_SVR_SELECT_ALARM){
				String errorCode = json.getString("ErrorCode");
				if("0".equals(errorCode)){
					JSONArray jsonArray = json.getJSONArray("ds_list");
					alarmList = new ArrayList<AlarmModel>();
					for(int i=0; i<jsonArray.length(); i++){
						JSONObject jobj = jsonArray.getJSONObject(i);
						alarmList.add(new AlarmModel(
								jobj.getString("ALM_NO")
								, jobj.getString("ALM_NM")
								, jobj.getString("ALM_PATH")
								, jobj.getString("ALM_FILE_NM")
								, R.drawable.sound1));
					}
					alarmList.add(new AlarmModel("","지정 알림음 없음","","", R.drawable.sound1));
					alarmList.add(new AlarmModel("999","무음","","", R.drawable.sound2));
					alarmList.add(new AlarmModel("998","진동","","", R.drawable.sound3));
					adapter = new PNSAlarmListAdapter(this, alarmList);
					listAlarm.setAdapter(adapter);
				}
			}
		}catch (JSONException e) {
			// TODO: handle exception
			Log.e(TAG, e.getMessage());
		}
	}
	public void selectAlarmList(){

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("sqlId", "mobile/user:PNS_ALM_L01");
		data.put("user_no", Util.getSharedData("userId",""));

		net.launchRequestFsp(SERVLET_URL, data, REQUEST_SVR_SELECT_ALARM, true);
	}

	public void setSelectAlarm(AlarmModel alarm) {
		Util.setSharedData("userAlmNo", alarm.getAlmNo());
		Util.setSharedData("userAlmNm", alarm.getAlmNm());
		Util.setSharedData("userAlmFileNm", alarm.getAlmFileNm());
		Util.setSharedData("userAlmPath", alarm.getAlmPath()+alarm.getAlmFileNm());
		finish();
		overridePendingTransition(ac.uc.mobile.pns.R.anim.leftin, ac.uc.mobile.pns.R.anim.rightout);
	}

	public class PNSAlarmListAdapter extends BaseAdapter{

		private LayoutInflater inflater = null;
		private ArrayList<AlarmModel> mAlarmList = null;
		private Context mContext;

		/**
		 * @param knockMeAlrimSetupActivity
		 * @param mCateList
		 */
		public PNSAlarmListAdapter(Context ctx, ArrayList<AlarmModel> mAlarmList) {
			// TODO Auto-generated constructor stub
			this.mContext = ctx;
			this.inflater = LayoutInflater.from(ctx);
			this.mAlarmList = mAlarmList;
		}

		public ArrayList<AlarmModel> getCateList(){
			return this.mAlarmList;
		}

		public void setCateList(ArrayList<AlarmModel> mAlarmList){
			this.mAlarmList = mAlarmList;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return this.mAlarmList.size();
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return this.mAlarmList.get(position);
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		class ViewHolder{
			TextView txtAlarmNm;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			View view = convertView;
			final ViewHolder viewHolder;
			final AlarmModel item = this.mAlarmList.get(position);
			if(view == null){
				viewHolder = new ViewHolder();
				view = inflater.inflate(ac.uc.mobile.pns.R.layout.pns_alarm_list_item, null);
				ImageView icon=(ImageView)view.findViewById(R.id.imageview);
                Log.d("","Icon call value :: "+item.getIcon());
				icon.setImageResource(item.getIcon());
				viewHolder.txtAlarmNm = (TextView)view.findViewById(ac.uc.mobile.pns.R.id.txtAlarmNm);
				view.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder)view.getTag();
			}

			viewHolder.txtAlarmNm.setText(item.getAlmNm());

			return view;
		}

	}

}
