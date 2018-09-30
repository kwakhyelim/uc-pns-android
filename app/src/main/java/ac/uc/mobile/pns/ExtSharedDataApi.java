package ac.uc.mobile.pns;

import com.sz.fspmobile.api.SharedDataApi;
import com.sz.fspmobile.api.spec.FSPResult;
import com.sz.fspmobile.api.spec.FSPResult.ErrorCode;
import com.sz.fspmobile.config.UserConfig;
import com.sz.fspmobile.util.AESHelper;

import org.json.JSONObject;

import ac.uc.mobile.pns.util.Util;

public class ExtSharedDataApi extends SharedDataApi {
  @Override
  public FSPResult getValue(String key) throws Exception {

    if(key == null || key.equals("")){
      return new FSPResult(ErrorCode.OK, "");
    }

    String encKey = UserConfig.getSharedInstance().getDeviceID();
    String str = Util.getSharedData(key, "");
    return new FSPResult(ErrorCode.OK, str);
  }

  @Override
  public FSPResult getValues(String key) throws Exception {
    String encKey = UserConfig.getSharedInstance().getDeviceID();

    String[] str = key.split(",");
    JSONObject result = new JSONObject();
    for(int i = 0; i < str.length; i++){
      if(str[i].equals("encUserId")) {
        result.put(str[i], UserConfig.getSharedInstance().getString(str[i],""));
      }else{
        if(UserConfig.getSharedInstance().getString(str[i], "") != null && !"".equals(UserConfig.getSharedInstance().getString(str[i], ""))) {
          result.put(str[i], AESHelper.decrypt(encKey, UserConfig.getSharedInstance().getString(str[i], "")));
        }else{
          result.put(str[i], "");
        }
      }
    }
    JSONObject resultObj = new JSONObject();
    resultObj.put("result", result);

    return new FSPResult(ErrorCode.OK, resultObj);
  }

  @Override
  public FSPResult setValue(String key, String value) throws Exception {
    String encKey = UserConfig.getSharedInstance().getDeviceID();
    UserConfig.getSharedInstance().setString(key, AESHelper.encrypt(encKey, value));
    return new FSPResult(ErrorCode.OK);
  }

  @Override
  public FSPResult setValues(JSONObject jsonObj) throws Exception {
    String encKey = UserConfig.getSharedInstance().getDeviceID();
    for(int i = 0 ; i< jsonObj.length(); i++){
      UserConfig.getSharedInstance().setString(jsonObj.names().getString(i),  AESHelper.encrypt(encKey,jsonObj.getString(jsonObj.names().getString(i))));
    }

    return new FSPResult(ErrorCode.OK);
  }
}
