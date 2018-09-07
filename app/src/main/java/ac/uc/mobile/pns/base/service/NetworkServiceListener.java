/**
 * 
 */
package ac.uc.mobile.pns.base.service;

import org.json.JSONObject;

/**
 * @author sz-jkkoo
 *
 */
public interface NetworkServiceListener {

	/**
	 * 媛꾨왂���댁슜
	 *
	 * �곸꽭�ㅻ챸���꾩슂 ��寃쎌슦 �ш린��湲곗닠
	 * @title getStuSosokInfo
	 * @param  
	 * @return BaseDomain
	 */
	public void onHandleResults(int requestId, JSONObject json);
}
