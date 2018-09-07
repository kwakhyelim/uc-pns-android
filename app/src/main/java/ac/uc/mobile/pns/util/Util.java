package ac.uc.mobile.pns.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.sz.fspmobile.config.UserConfig;
import com.sz.fspmobile.log.Logger;
import com.sz.fspmobile.util.AESHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.Ringtone;
import android.util.DisplayMetrics;
import android.util.Log;

public class Util {
	private static final String TAG = Util.class.getSimpleName();
	public static Ringtone ringtone;
	
	public static String nvl(String src, String tar) {
		String returnStr = "";
		if(src == null) returnStr = tar; 
		else if("".equals(src)) returnStr = tar;
		else if("null".equals(src)) returnStr = tar;
		else returnStr = src;
		return returnStr;
	}
	
	public static Bitmap getAssetImage(Context ctx, String assetFolder, String filename, boolean isRounded) {
		
		Bitmap bitmap = null;
    	AssetManager asset = ctx.getAssets();
    	InputStream is;
		try {
			is = asset.open(assetFolder+"/"+filename);
			bitmap = BitmapFactory.decodeStream(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.getMessage());
		}
		 
    	if(isRounded) 
    		return roundCorner(bitmap, 11f);
    	else 
    		return bitmap;
	}
	
	
	public static Bitmap roundCorner(Bitmap src, float round) {
		
		if(src == null) return null;
		
		int width = src.getWidth();
		int height = src.getHeight();
		
		Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		
		Canvas canvas = new Canvas(result);
		canvas.drawARGB(0, 0, 0, 0);
		
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		
		final Rect rect = new Rect(0,0,width,height);
		final RectF rectF = new RectF(rect);
		
		canvas.drawRoundRect(rectF, round, round, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(src, rect,rect,paint);

		return result;
	}

	public static Bitmap roundCorner(Bitmap src, int w, int h, float round) {

		if(src == null) return null;

		int width = w;
		int height = h;

		//Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Bitmap result = Bitmap.createScaledBitmap(src, w, h, true) ;
		Canvas canvas = new Canvas(result);
		canvas.drawARGB(0, 0, 0, 0);

		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);

		final Rect rect = new Rect(0,0,width,height);
		final RectF rectF = new RectF(rect);

		canvas.drawRoundRect(rectF, round, round, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(src, rect,rect,paint);

		return result;
	}


	public static void SaveBitmapToFileCache(Bitmap bitmap, File savedFile) {
		OutputStream out = null;
		try {
			out = new FileOutputStream(savedFile);
			bitmap.compress(CompressFormat.PNG, 80, out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.getMessage());
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}
	
	public static File getCacheDirectory(Context context, String forderNm){
		String sdState = android.os.Environment.getExternalStorageState();
		File cacheDir;

		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File sdDir = android.os.Environment.getExternalStorageDirectory();  

			//TODO : Change your diretcory here
			cacheDir = new File(sdDir,"data/tac/images/cache/" + forderNm);
		}
		else
			cacheDir = context.getCacheDir();

		if(!cacheDir.exists())
			cacheDir.mkdirs();
		return cacheDir;
	}
	
	
	public static float getCurrentDisplayDpi() {
		float densidy = 0;
		DisplayMetrics metrics = new DisplayMetrics();

		densidy = metrics.density;
		
		return densidy;
	}
	
	
	public static int convertPixtoDip(Context ctx, int pixel)
	
	{
			float scale = ctx.getResources().getDisplayMetrics().density;
	        return (int) (pixel * scale + 0.5f);
	}
	
	public static int convertDiptoPix(Context ctx, float dip)
	{
		float scale = ctx.getResources().getDisplayMetrics().density;
	        return (int)(dip * scale);
	 }
	
	
	
	/** Get Bitmap's Width **/

	public static int getBitmapOfWidth( String fileName ){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(fileName, options);
		return options.outWidth;
	}

	/** Get Bitmap's height **/

	public static int getBitmapOfHeight( String fileName ){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(fileName, options);
		return options.outHeight;
	}
	
	public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

		int width = bm.getWidth();

		int height = bm.getHeight();

		float scaleWidth = ((float) newWidth) / width;

		float scaleHeight = ((float) newHeight) / height;

		// create a matrix for the manipulation

		Matrix matrix = new Matrix();

		// resize the bit map

		matrix.postScale(scaleWidth, scaleHeight);

		// recreate the new Bitmap

		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

		return resizedBitmap;

		}	
	
	public static Bitmap getCenterCropBitmap(Bitmap srcBmp) {
		Bitmap dstBmp = null;
		
		if (srcBmp.getWidth() >= srcBmp.getHeight()){

			  dstBmp = Bitmap.createBitmap(
			     srcBmp, 
			     srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
			     0,
			     srcBmp.getHeight(), 
			     srcBmp.getHeight()
			     );

			}else{

			  dstBmp = Bitmap.createBitmap(
			     srcBmp,
			     0, 
			     srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
			     srcBmp.getWidth(),
			     srcBmp.getWidth() 
			     );
			}
		
		return dstBmp;
	}
	
	public static Bitmap roundCornerRect(Bitmap src, float round) {
		
		if(src == null) return null;
		
		int width = src.getWidth();
		int height = src.getHeight();
		
		Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		
		Canvas canvas = new Canvas(result);
		canvas.drawARGB(0, 0, 0, 0);
		
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		
		final Rect rect = new Rect(0,0,width,height);
		final RectF rectF = new RectF(rect);
		
		canvas.drawRoundRect(rectF, round, round, paint);
		
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(src, rect,rect,paint);
		
		return result;
	}
	
	public static Bitmap getCenterCroppedBitmap(Bitmap bitmap) {
		Bitmap output;

	    if (bitmap.getWidth() > bitmap.getHeight()) {
	        output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Config.ARGB_8888);
	    } else {
	        output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Config.ARGB_8888);
	    }

	    Canvas canvas = new Canvas(output);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

	    float r = 0;

	    if (bitmap.getWidth() > bitmap.getHeight()) {
	        r = bitmap.getHeight() / 2;
	    } else {
	        r = bitmap.getWidth() / 2;
	    }

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawCircle(r, r, r, paint);
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	    return output;
	}
	
	
	/**
	 * 날자형 String을 포맷에 맞게 재정리하여 리턴
	 * 
	 * @param 날짜( 14자리 또는 8자리 문자열 )
	 * @param infmt (입력 포맷)
	 * @param outFmt (출력 포맷)
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	static public String getFormatedDate(String dateStr, String inFmt, String outFmt) {
		String returnStr = "";
		SimpleDateFormat inFormat = new SimpleDateFormat(inFmt);
		SimpleDateFormat outFormat = new SimpleDateFormat(outFmt);
		
		if(dateStr == null) return dateStr;
		try {
			Date date = inFormat.parse(dateStr);
			returnStr = outFormat.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.getMessage());
			return dateStr;
		}
		
		return returnStr;
	}
	
	static public Bitmap getCircleCroppedBitmap(Bitmap bitmap) {
		Bitmap output;

	    if (bitmap.getWidth() > bitmap.getHeight()) {
	        output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Config.ARGB_8888);
	    } else {
	        output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Config.ARGB_8888);
	    }

	    Canvas canvas = new Canvas(output);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

	    float r = 0;

	    if (bitmap.getWidth() > bitmap.getHeight()) {
	        r = bitmap.getHeight() / 2;
	    } else {
	        r = bitmap.getWidth() / 2;
	    }

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawCircle(r, r, r, paint);
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	    return output;
	}
	
	
	public static String getDate(String fmt) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fmt);
		String dateAsString = simpleDateFormat.format(new Date());
		
		return dateAsString;
	}
	
	public static String getLauncherClassName(Context context) {
	    PackageManager pm = context.getPackageManager();
	 
	    Intent intent = new Intent(Intent.ACTION_MAIN);
	    intent.addCategory(Intent.CATEGORY_LAUNCHER);
	 
	    List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
	    for (ResolveInfo resolveInfo : resolveInfos) {
	        String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
	        if (pkgName.equalsIgnoreCase(context.getPackageName())) {
	            String className = resolveInfo.activityInfo.name;
	            return className;
	        }
	    }
	    return null;
	}
	
	/**
	 * 
	 * <pre>
	 * 배지카운트를 업데이트
	 * 
	 * </pre>
	 * @title updateIconBadge
	 * @param context 
	 * @param int 카운트
	 * @return String
	 * @throws 
	 */
	public static void updateIconBadge(Context context, int notiCnt) {
		SharedPreferences prefs = context.getSharedPreferences("setup", Activity.MODE_PRIVATE);
		int badgeCount = prefs.getInt("badgeCount", 0);
		Log.d("package name : %@",context.getPackageName());
		Log.d("launcher name : %@",getLauncherClassName(context));
		badgeCount = badgeCount + notiCnt;
	    Intent badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
	    badgeIntent.putExtra("badge_count", badgeCount);
	    badgeIntent.putExtra("badge_count_package_name", context.getPackageName());
	    badgeIntent.putExtra("badge_count_class_name", getLauncherClassName(context));
	    context.sendBroadcast(badgeIntent);
	    
	 
	    Editor editor = prefs.edit();
	    editor.putInt("badgeCount", badgeCount);
	    editor.commit();
	}	
	
	
	public static String getSharedData( String key, String defaultVal) {
		String encKey = UserConfig.getSharedInstance().getDeviceID();
		String value = "";
		if(UserConfig.getSharedInstance().getString(key, defaultVal) != null && !"".equals(UserConfig.getSharedInstance().getString(key, defaultVal))) {
			try {
				value = AESHelper.decrypt(encKey,UserConfig.getSharedInstance().getString(key, defaultVal));
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		}
		return value;
	}
	
	public static void setSharedData( String key, String value) {
		
		String encKey = UserConfig.getSharedInstance().getDeviceID();
		
		try {
			String encValue = AESHelper.encrypt(encKey, value);
			UserConfig.getSharedInstance().setString(key, encValue);
		} catch (Exception e) {
		  Logger.getLogger().writeException("#### error.... ", e);
			Log.e(TAG, e.getMessage());
		}
	
	}
}
