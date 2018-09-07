/**
 * <pre>
 * 제목등 간단내용 기술.
 *
 * 상세 설명이 필요할 경우 기술함.
 * @title    GalleryAlbunModel.java
 * @project  ESNMobileAndroid
 * @date     2013. 7. 31. 오후 3:51:53
 * @version  ver1.0
 * @author   jkkoo-win
 * </pre>
 */
package ac.uc.mobile.pns.setup.model;


/**
 * @author jkkoo-win
 *
 */
public class AlarmModel {

	private String almNo;
	private String almNm;
	private String almPath;
	private String almFileNm;
	private int icon;


	public AlarmModel(String almNo, String almNm, String almPath,String almFileNm,int icon) {
		super();
		this.almNo = almNo;
		this.almNm = almNm;
		this.almPath = almPath;
		this.almFileNm = almFileNm;
		this.icon=icon;
	}


	public String getAlmNo() {
		return almNo;
	}


	public void setAlmNo(String almNo) {
		this.almNo = almNo;
	}


	public String getAlmNm() {
		return almNm;
	}


	public void setAlmNm(String almNm) {
		this.almNm = almNm;
	}


	public String getAlmPath() {
		return almPath;
	}


	public void setAlmPath(String almPath) {
		this.almPath = almPath;
	}


	public String getAlmFileNm() {
		return almFileNm;
	}


	public void setAlmFileNm(String almFileNm) {
		this.almFileNm = almFileNm;
	}

	public int getIcon(){return icon;}
}