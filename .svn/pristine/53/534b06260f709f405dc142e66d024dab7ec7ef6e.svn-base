package ac.uc.mobile.pns.base;





import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommonConfirmDialog extends Dialog{
	public static final int ALERT_TYPE = 1;
	public static final int CONFIRM_TYPE = 2;
	
	private Context mContext;
	private ImageButton btnClose;
	
	private LinearLayout layoutConfirm;
	private LinearLayout layoutAlert;
	private Button btnCancel;
	private Button btnConfirm;
	private Button btnOk;
	private TextView txtTit;
	private TextView txtMessage;
	private String mTitle;
	private String mMessage;
	private View.OnClickListener cancelBtnClickListener;
	private View.OnClickListener confirmBtnClickListener;
	private int type;
	private String mBtnLabel1;
	private String mBtnLabel2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();    
		lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		lpWindow.dimAmount = 0.4f;
		getWindow().setAttributes(lpWindow);
		
		setContentView(ac.uc.mobile.pns.R.layout.common_confirm_dialog);
		btnClose = (ImageButton) findViewById(ac.uc.mobile.pns.R.id.btnClose);
		btnCancel =  (Button) findViewById(ac.uc.mobile.pns.R.id.btnCancel);
		btnConfirm = (Button) findViewById(ac.uc.mobile.pns.R.id.btnConfirm);
		btnOk = (Button) findViewById(ac.uc.mobile.pns.R.id.btnOk);
		txtTit = (TextView) findViewById(ac.uc.mobile.pns.R.id.txtTit);
		txtMessage = (TextView) findViewById(ac.uc.mobile.pns.R.id.txtMessage);
		 
		txtTit.setText(mTitle); 
		txtMessage.setText(mMessage); 

		layoutConfirm = (LinearLayout) findViewById(ac.uc.mobile.pns.R.id.layoutConfirm);
		layoutAlert = (LinearLayout) findViewById(ac.uc.mobile.pns.R.id.layoutAlert);
		
		if(mBtnLabel1 != null && !"".equals(mBtnLabel1)) {
			btnConfirm.setText(mBtnLabel1);
		}
		
		if(mBtnLabel2 != null && !"".equals(mBtnLabel2)) {
			btnCancel.setText(mBtnLabel2);
		}

		switch (this.type) {
		case ALERT_TYPE:
			layoutAlert.setVisibility(View.VISIBLE);
			layoutConfirm.setVisibility(View.INVISIBLE);
			break;
		case CONFIRM_TYPE:
			layoutAlert.setVisibility(View.INVISIBLE);
			layoutConfirm.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
		
		btnClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		
		if(this.cancelBtnClickListener == null) {
			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dismiss();
				}
			});
		}else{
			btnCancel.setOnClickListener(this.cancelBtnClickListener);
		}
		
		if(this.confirmBtnClickListener == null) {
			btnConfirm.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dismiss();
				}
			});
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dismiss();
				}
			});			
		}else{
			btnConfirm.setOnClickListener(this.confirmBtnClickListener);
			btnOk.setOnClickListener(this.confirmBtnClickListener);
		}
		
	}

	public CommonConfirmDialog(Context context) {
		// Dialog 배경을 투명 처리 해준다.
		super(context , android.R.style.Theme_Translucent_NoTitleBar);
	}
	
	public CommonConfirmDialog(Context context , String title,  String message, View.OnClickListener confirmListener, View.OnClickListener cancelListener) {
		super(context , android.R.style.Theme_Translucent_NoTitleBar);
		this.cancelBtnClickListener = cancelListener;
		this.confirmBtnClickListener = confirmListener;
		this.mContext = context;
		this.mTitle = title;
		this.mMessage = message;
		this.type = 2;
	}
	
	public CommonConfirmDialog(Context context , String title,  String message, String label1, String label2, View.OnClickListener confirmListener, View.OnClickListener cancelListener) {
		super(context , android.R.style.Theme_Translucent_NoTitleBar);
		this.cancelBtnClickListener = cancelListener;
		this.confirmBtnClickListener = confirmListener;
		this.mContext = context;
		this.mTitle = title;
		this.mMessage = message;
		this.type = 2;
		this.mBtnLabel1 = label1;
		this.mBtnLabel2 = label2;
	}
	
	
	public CommonConfirmDialog(Context context , String title, String message, View.OnClickListener confirmListener) {
		super(context , android.R.style.Theme_Translucent_NoTitleBar);
		this.cancelBtnClickListener = null;
		this.confirmBtnClickListener = confirmListener;
		this.mContext = context;
		this.mTitle = title;
		this.mMessage = message;
		this.type = 2;
	}
	
	public CommonConfirmDialog(Context context , int type, String title, String message, View.OnClickListener confirmListener) {
		super(context , android.R.style.Theme_Translucent_NoTitleBar);
		this.cancelBtnClickListener = null;
		this.confirmBtnClickListener = confirmListener;
		this.mContext = context;
		this.mTitle = title;
		this.mMessage = message;
		this.type = type;
	}
	
	public CommonConfirmDialog(Context context , int type, String title, String message) {
		super(context , android.R.style.Theme_Translucent_NoTitleBar);
		this.cancelBtnClickListener = null;
		this.confirmBtnClickListener = null;
		this.mContext = context;
		this.mTitle = title;
		this.mMessage = message;
		this.type = type;
	}
}
