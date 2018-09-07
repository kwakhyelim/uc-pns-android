/**
 * 
 */
package ac.uc.mobile.pns.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import ac.uc.mobile.pns.util.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageButton;


/**
 * @author jkkoo
 *
 */
public class ImageTask extends AsyncTask {

	private ImageButton  imageButton;
	private Context	ctx;
	public ImageTask(Context ctx, ImageButton imageButton) {
		super();
		this.imageButton = imageButton;
		this.ctx = ctx;
	}


	@Override
	protected Object doInBackground(Object... params) {
		// TODO Auto-generated method stub
		String urldisplay = (String) params[0];
        
        Log.d("", "UserImageThumgnailTask: urldisplay = "+urldisplay);
        if(urldisplay == null || "".equals(urldisplay) || "null".equals(urldisplay))
        	return null;
		Bitmap userImage = null;
		InputStream in = null;
        try {
            in = new java.net.URL(urldisplay).openStream();
            userImage = BitmapFactory.decodeStream(in);
            System.gc();
        } catch (MalformedURLException e) {
            Log.e("Error", e.getMessage());
        } catch (IOException e) {
        	Log.e("Error", e.getMessage());
        } finally {
        	if(in != null){try {in.close();} catch (IOException e) {Log.e("", e.getMessage());}}
        }
        return userImage;
	}


	@Override
	protected void onPostExecute(Object result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(result == null) {

		}else{
			Bitmap bm = (Bitmap)result;
			
			int w = bm.getWidth() / 2;
			int h = bm.getHeight() / 2;
			imageButton.setImageBitmap(Util.getResizedBitmap(bm, Util.convertDiptoPix(this.ctx, h), Util.convertDiptoPix(this.ctx, w)));
			
			System.gc();
		}
		
	}

	public Bitmap getCroppedBitmap(Bitmap bitmap) {
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
	
}
