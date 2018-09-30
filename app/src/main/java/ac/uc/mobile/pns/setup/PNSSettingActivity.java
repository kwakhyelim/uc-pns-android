package ac.uc.mobile.pns.setup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.sz.fspmobile.net.HttpMessageHelper;
import com.sz.fspmobile.util.DialogHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ac.uc.mobile.pns.R;
import ac.uc.mobile.pns.util.Util;

/**
 * 환경설정 페이지.
 */
public class PNSSettingActivity extends com.sz.pns.setup.PNSSettingActivity {

  @SuppressLint("InlinedApi")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // 암호화 되어 있어서 변경
    String userAlmNm = Util.getSharedData("userAlmNm", "");
    if (!"".equals(userAlmNm)) {
      this.txtAlmNm.setText(userAlmNm);
    }

    // 재정의
    layoutAlarmSetup.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent intent = new Intent(PNSSettingActivity.this, PNSAlarmListActivity.class);
        startActivityForResult(intent, REQUEST_ACTIVITY_ALARM_LIST);
        overridePendingTransition(R.anim.rightin, R.anim.leftout);
      }

    });
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    finish();
  }

  public void saveUserSetting() {
    Map<String, Object> data = new HashMap();
    data.put("fsp_action", "xDefaultAction");
    data.put("sqlId", "mobile/user:PNS_USER_APP_PUSH_YN_U01");
    data.put("user_id", Util.getSharedData("userId", ""));
    if (this.btnPrivOnOff.isSelected()) {
      data.put("MSG_ALM_YN", "Y");
    } else {
      data.put("MSG_ALM_YN", "N");
    }

    if (this.btnMyFavOnOff.isSelected()) {
      data.put("NEW_FEED_ALM_YN", "Y");
    } else {
      data.put("NEW_FEED_ALM_YN", "N");
    }
    net.launchRequestFsp(getFsp().getServerConfig().getUrl("WebJSON"), data, 2100, true);
  }

  @Override
  public void onHandleResults(int requestId, JSONObject json) {
    // TODO Auto-generated method stub
    Log.d("PNSSettingActivity", "onHandleResults json: " + json.toString());
    try {
      if (requestId == REQUEST_SVR_UPDATE_USER) {
        String errorCode = json.getString("ErrorCode");
        if ("0".equals(errorCode)) {
          Util.setSharedData("userNm", editName.getText().toString());
          Util.setSharedData("userTelNo", editTel.getText().toString());
          setResult(9001);
          finish();
          overridePendingTransition(R.anim.leftin, R.anim.rightout);
        } else {
          DialogHelper.alert(this, HttpMessageHelper.getErrorMsg(json));
        }
      } else {
        super.onHandleResults(requestId, json);
      }
    } catch (JSONException e) {
      getLogger().writeException("## setting activity#onHandleResults", e);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_ACTIVITY_ALARM_LIST) {
      if (!"".equals(Util.getSharedData("userAlmNm", ""))) {
        txtAlmNm.setText(Util.getSharedData("userAlmNm", ""));
      } else {
        txtAlmNm.setText("지정 알림음 없음");
      }
    } else {  // 그외는 모두  super
      super.onActivityResult(requestCode, requestCode, data);
    }
  }
}
