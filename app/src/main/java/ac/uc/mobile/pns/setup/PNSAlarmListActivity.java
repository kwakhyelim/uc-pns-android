/**
 *
 */
package ac.uc.mobile.pns.setup;

import com.sz.pns.common.PNSUser;
import com.sz.pns.setup.model.AlarmModel;

import java.util.HashMap;
import java.util.Map;

import ac.uc.mobile.pns.util.Util;

/**
 * 알림음 설정 페이지.
 */
public class PNSAlarmListActivity extends com.sz.pns.setup.PNSAlarmListActivity {
  public void selectAlarmList() {
    Map<String, Object> data = new HashMap();
    data.put("fsp_action", "xDefaultAction");
    data.put("sqlId", "mobile/user:PNS_ALM_L01");
    data.put("user_no", Util.getSharedData("userId", ""));
    net.launchRequestFsp( getFsp().getServerConfig().getUrl("WebJSON"), data, 1100, true);
  }

  /**
   * 알림 선택 시 저장 부분이 틀림.
   * PNS는 일반 Text로 저장, 여기서는 암호화해서 저장
   * @param alarm
   */
  @Override
  public void setSelectAlarm(AlarmModel alarm) {
    Util.setSharedData("userAlmNo", alarm.getAlmNo());
    Util.setSharedData("userAlmNm", alarm.getAlmNm());
    Util.setSharedData("userAlmFileNm", alarm.getAlmFileNm());
    Util.setSharedData("userAlmPath", alarm.getAlmPath() + alarm.getAlmFileNm());

    finish();
    overridePendingTransition(ac.uc.mobile.pns.R.anim.leftin, ac.uc.mobile.pns.R.anim.rightout);
  }
}
