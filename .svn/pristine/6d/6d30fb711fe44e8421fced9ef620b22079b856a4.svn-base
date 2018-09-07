/**
 * 
 */
package ac.uc.mobile.pns.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.sz.fspmobile.BaseActivity;
import ac.uc.mobile.pns.base.CommonConfirmDialog;
//import com.sz.pns.dialog.CommonConfirmDialog;
import ac.uc.mobile.pns.util.Util;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouch.OnImageViewTouchSingleTapListener;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;

/**
 * @author jkkoo
 *
 */
@SuppressLint("InlinedApi")
public class ImageViewerActivity extends BaseActivity {
	private static final String TAG = ImageViewerActivity.class.getSimpleName();
	int currIdx = 0;
	public ViewPager mPager;
	public ImageButton btnClose;
	private ImageButton btnDownload;
	public LinearLayout layoutMask;
	
	public ArrayList<String> imageList;
	public ArrayList<String> orgImageNames;
	static Matrix imageMatrix;
	
	private CommonConfirmDialog confirmDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		String [] imageUrlList = getIntent().getStringArrayExtra("imageUrlList");
		String selectedIdx = getIntent().getStringExtra("selectedIdx");
		imageList = new ArrayList<String>();
		orgImageNames = new ArrayList<String>();
		for(int i=0;i<imageUrlList.length;i++){
			String [] fileInfos = imageUrlList[i].split("::");
			if(fileInfos[3].equals("1")){
				imageList.add(getGlobal().getServerConfig().getServerRootUrl()+"/" + fileInfos[1] + fileInfos[2]);
				orgImageNames.add(fileInfos[4]);
			}
		}
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
				WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		
		setContentView(ac.uc.mobile.pns.R.layout.common_image_view);
		layoutMask = (LinearLayout)findViewById(ac.uc.mobile.pns.R.id.layoutMask);
		btnClose = (ImageButton)findViewById(ac.uc.mobile.pns.R.id.btnClose);
		btnDownload = (ImageButton)findViewById(ac.uc.mobile.pns.R.id.btnDownload);
		
		mPager = (ViewPager)findViewById(ac.uc.mobile.pns.R.id.pager);
		mPager.setAdapter(new PagerAdapterClass(this));
//		mPager.setOffscreenPageLimit(imageList.size()-1);
		mPager.setCurrentItem(Integer.parseInt(selectedIdx));
		buttonEventSetting();
	}

	public void buttonEventSetting() {
		btnClose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		btnDownload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				confirmDialog = new CommonConfirmDialog(ImageViewerActivity.this, CommonConfirmDialog.ALERT_TYPE, "확인", "다운로드 하시겠습니까?", new OnClickListener() {
					
					@SuppressLint("NewApi")
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						confirmDialog.dismiss();
						
						int idx = mPager.getCurrentItem();
						String filepath = imageList.get(idx);
						
						
						DownloadManager.Request request = new DownloadManager.Request(Uri.parse(filepath));
						request.setDescription("File Download ");
						request.setTitle(orgImageNames.get(idx));
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						    request.allowScanningByMediaScanner();
						    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
						}
						request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, orgImageNames.get(idx));
						DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
						manager.enqueue(request);
					}
				});
				confirmDialog.show();
			}
		});
		
		layoutMask.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				layoutMask.setVisibility(View.INVISIBLE);
			}
		});
		
		mPager.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				layoutMask.setVisibility(View.VISIBLE);
			}
		});
	}
	
	
	public void imageViewTab() {
		if(layoutMask.getVisibility() == View.VISIBLE) {
			layoutMask.setVisibility(View.INVISIBLE);
		}else{
			layoutMask.setVisibility(View.VISIBLE);
		}
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		finish();
	}
	
	/**
	 * PagerAdapter 
	 */
	private class PagerAdapterClass extends PagerAdapter{
		
		private LayoutInflater mInflater;
		private Context ctx;
		public Map<Long,Bitmap> imageCacheMap = new HashMap<Long,Bitmap>();
		
		
		public PagerAdapterClass(Context c){
			super();
			mInflater = LayoutInflater.from(c);
			ctx = c;
		}
		
		@Override
		public int getCount() {
			return imageList.size();
		}

		@Override
		public void destroyItem(View pager, int position, Object view) {
			((ViewPager)pager).removeView((View)view);
		}
		
		@Override
		public boolean isViewFromObject(View pager, Object obj) {
			return pager == obj; 
		}
		
		@Override 
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}
		
		@Override 
		public Parcelable saveState() { 
			return null; 
		}
		
		@Override 
		public void startUpdate(View arg0) {
		}
		
		@Override 
		public void finishUpdate(View arg0) {
		}

		
		@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
		@Override
		public Object instantiateItem(View pager, int position) {
						
		

			View v = mInflater.inflate(ac.uc.mobile.pns.R.layout.common_image_view_item, null);
			ImageViewTouch imageView = (ImageViewTouch)v.findViewById(ac.uc.mobile.pns.R.id.imgAtt);
			imageView.setDisplayType( DisplayType.FIT_TO_SCREEN );
			
//			WebView webview = (WebView)v.findViewById(R.id.webImageView);
			
//			v.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					imageViewTab();
//				}
//			});
//			imageView.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					imageViewTab();
//				}
//			});
			imageView.setSingleTapListener( new OnImageViewTouchSingleTapListener() {
				
				@Override
				public void onSingleTapConfirmed() {
					imageViewTab();
				}
			} );
//			
			
			new ImageLoadTask(position, imageView, imageCacheMap).execute(imageList.get(position));
			
//			webview.setInitialScale(100);
//			webview.getSettings().setBuiltInZoomControls(true);
//			webview.loadUrl(imageList.get(position));
			
    		((ViewPager)pager).addView(v, 0);
			return v;    	
			
		}


	}

	@SuppressWarnings("rawtypes")
	private static class ImageLoadTask extends AsyncTask {
	    @SuppressWarnings("unused")
		private long mPosition;
	    private ImageViewTouch imageView;
	    private boolean isFileCache;
	    private boolean isMapCache;
	    private String filePath;
	    public File savedFile;
	    private Map<Long,Bitmap> imageMap;
	    private int width = 0;
	    
	    private boolean isCached = false;
	    
	    public ImageLoadTask(long position, ImageViewTouch imageView, Map<Long,Bitmap> list, int width) {
	        this.mPosition = position;
	        this.imageView = imageView;
	        this.imageMap = list;
	        this.width = width;
	    }
	    
		public ImageLoadTask(int position, ImageViewTouch imageView, Map<Long,Bitmap> imageMap) {
	        this.mPosition = position;
	        this.imageView = imageView;
	        this.imageMap = imageMap;
	    }
		
		@Override
		protected Object doInBackground(Object... params) {
			
			// TODO Auto-generated method stub
		        String urldisplay = (String) params[0];
		        isMapCache = false;
		        if("".equals(urldisplay)) return null;
		        
		        Log.d("image", "DownloadImageTask: urldisplay = "+urldisplay);
		        Bitmap bm = null;
		        String fileNm = urldisplay.substring(urldisplay.lastIndexOf("/")+1) ;
		        fileNm = fileNm.replace("PNG", "jpg").replace("png", "jpg");
		        savedFile = new File(Util.getCacheDirectory(imageView.getContext(),"bulletin"),fileNm);
		        isFileCache = savedFile.exists();
		        filePath = savedFile.getPath();
		        InputStream in = null;
		        
		        if(this.imageMap != null &&  this.imageMap.get(mPosition) != null) {
		        	isCached = true;
		        	bm = (Bitmap)this.imageMap.get(mPosition);
		        	Log.d("", "local memory map  loading..." + mPosition);
		        }else{
		        	if(isFileCache) {
			        	BitmapFactory.Options options = new BitmapFactory.Options();
			        	options.inPurgeable = true;
			        	options.inPreferredConfig = Bitmap.Config.RGB_565;
		    			bm = BitmapFactory.decodeFile(filePath,options);
			    		Log.d("", "local file loading..." + filePath);
		        	}else{
			        	try {
			        		Log.d("", "url file loading..." + urldisplay);
			        		BitmapFactory.Options options = new BitmapFactory.Options();
				        	options.inPurgeable = true;
				        	options.inPreferredConfig = Bitmap.Config.RGB_565;
				             in = new java.net.URL(urldisplay).openStream();
				            bm = BitmapFactory.decodeStream(in, null, options);
				            System.gc();
				        } catch (MalformedURLException e) {
				            Log.e(TAG, e.getMessage());
				        } catch (IOException e) {
				        	Log.e(TAG, e.getMessage());
				        } finally {
				        	if(in != null){try {in.close();} catch (IOException e) {Log.e(TAG, e.getMessage());}}
				        }
		        	}
		        }
		        return bm;
		}
		
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result == null) {

			}else{
				Bitmap bm = (Bitmap)result;
				if(!isMapCache) {
					if(imageMap != null) {
						if(width != 0) {
							float h = ((float)width / (float)bm.getWidth()) * (float)bm.getHeight();
							bm = Util.getResizedBitmap(bm, (int)h, width);
						}
						imageMap.put(mPosition, bm);
					}
				}
				if(!isFileCache) {
					if(width != 0) {
						float h = (width / bm.getWidth()) * (float)bm.getHeight();
						bm = Util.getResizedBitmap(bm, (int)h, width);
					}
					Util.SaveBitmapToFileCache(bm, savedFile);
				}

				if( null == imageMatrix ) {
					imageMatrix = new Matrix();
				} else {
					// get the current image matrix, if we want restore the 
					// previous matrix once the bitmap is changed
					// imageMatrix = mImage.getDisplayMatrix();
				}
				imageView.setImageBitmap( bm, imageMatrix.isIdentity() ? null : imageMatrix, ImageViewTouchBase.ZOOM_INVALID, ImageViewTouchBase.ZOOM_INVALID );
				System.gc();
			}
		}
	}
}
