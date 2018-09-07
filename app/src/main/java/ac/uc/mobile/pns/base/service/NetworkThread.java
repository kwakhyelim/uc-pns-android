package ac.uc.mobile.pns.base.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import ac.uc.mobile.pns.util.Util;

public class NetworkThread extends Thread {
	private static final String TAG = NetworkThread.class.getSimpleName();
	private static final String USER_AGENT = "Knock (Android)";
	
	private final String url;
	private final ArrayList<NameValuePair> formData;
	private final Map<String,Object> paraMap;
	private final Handler handler;
	private final int requestId;

	public NetworkThread(String url, ArrayList<NameValuePair> formData, Handler handler, int requestId) {
		this.url = url;
		this.formData = formData;
		this.handler = handler;
		this.paraMap = null;
		
		this.requestId = requestId;
	}

	public NetworkThread(String url, Map<String,Object> paraMap, Handler handler, int requestId) {
		this.url = url;
		this.paraMap = paraMap;
		this.handler = handler;
		this.formData = null;
		this.requestId = requestId;
	}
	
	
	@Override
	public void run() {
		//AndroidHttpClient client = null;
		HttpClient client = null;
		try {
//			Log.d(TAG, "url = " + url);
			HttpPost post = new HttpPost(url);
			if(this.formData != null) {
	            UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(formData, "UTF-8");
	            post.setEntity(entityRequest);
			}else if(this.paraMap != null) {
				JSONObject holder = new JSONObject(this.paraMap);
				String isFile = "";
				try{
					isFile = holder.getString("isFile");
				}catch(JSONException e){
					isFile = "";
				}
				if("Y".equals(isFile)) {
					String [] localFileList = holder.getString("fileList").split(",");		
					JSONObject args = new JSONObject(holder.getString("params"));
					MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
					for(int i=0;i<localFileList.length;i++){
						File file = new File(Uri.parse(localFileList[i]).getPath());
						String filename = localFileList[i].substring(localFileList[i].lastIndexOf("/")+1) ;
						if(localFileList[i].toUpperCase().endsWith(".PNG") || localFileList[i].toUpperCase().endsWith(".JPG") || localFileList[i].toUpperCase().endsWith(".GIF") || localFileList[i].toUpperCase().endsWith(".TIFF")){
							ExifInterface exif = new ExifInterface(file.getPath());  
							int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
							BitmapFactory.Options options = new BitmapFactory.Options();
							options.inJustDecodeBounds = true;
							BitmapFactory.decodeFile(file.getAbsolutePath(), options);
							int photoHeight = options.outHeight;
							int photoWidth = options.outWidth;
//							Log.d(TAG, "Upload before Imgae (src orginal ): w=" + photoWidth + ", h="+photoHeight + " , file size = " + file.length());
							options.inPreferredConfig = Bitmap.Config.RGB_565;
							options.inJustDecodeBounds = false;
//							options.inSampleSize =    Math.max(photoHeight/1280, photoWidth/768);
							options.inSampleSize = photoWidth / 640;
							options.inPurgeable = true;
							Bitmap bmOutput = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
							Bitmap bmRotated = rotateBitmap(bmOutput, orientation);
							
							if(bmRotated.getWidth() > 640) {
								float rto = (float) (640.0f / bmRotated.getWidth() );
								long h = (long) (rto * bmRotated.getHeight());
								bmRotated = Util.getResizedBitmap(bmRotated, (int)h, 640);
							}
							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							bmRotated.compress(Bitmap.CompressFormat.JPEG, 70, bos);
							
							byte[] byteArray = bos.toByteArray() ; 
							ByteArrayBody bab = new ByteArrayBody(byteArray, filename);
							reqEntity.addPart("myImage", bab);
						}else{
							FileBody bin = new FileBody(file);
					        reqEntity.addPart("myFile", bin);
						}
					}
			        for (@SuppressWarnings("unchecked")
					Iterator<String> ir = args.keys(); ir.hasNext();) {
			        	String key = ir.next();
			        	reqEntity.addPart(key, new StringBody(args.getString(key), Charset.forName( "UTF-8" )));
			        }
			        post.setEntity(reqEntity);
				}else{
					String jsonStr = holder.toString();
					
//					Log.d(TAG, "jsonStr = " + jsonStr);
					StringEntity se = new StringEntity(jsonStr, "UTF-8");
					post.setEntity(se);
					post.setHeader("Content-type", "application/json;charset=UTF-8");
				}
			}
//  			client = AndroidHttpClient.newInstance(USER_AGENT);
			
  			client = getNewHttpClient();
  			
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				ByteArrayOutputStream jsonHolder = new ByteArrayOutputStream();
				entity.writeTo(jsonHolder);
				jsonHolder.flush();
				JSONObject json = new JSONObject(jsonHolder
						.toString(getEncoding(entity)));
				jsonHolder.close();

				Message message = Message.obtain(handler, ac.uc.mobile.pns.R.id.request_succeeded);
				message.arg1 = this.requestId;
				message.obj = json;
				message.sendToTarget();
			} else {
				Log.w(TAG, "HTTP returned " + response.getStatusLine().getStatusCode() + " for " + url);
				Message message = Message.obtain(handler, ac.uc.mobile.pns.R.id.request_failed);
				message.sendToTarget();
			}
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "UnsupportedEncodingException :"+ e.getMessage());
			Message message = Message.obtain(handler, ac.uc.mobile.pns.R.id.request_failed);
			message.sendToTarget();
		} catch (ClientProtocolException e) {
			Log.e(TAG, "ClientProtocolException :"+ e.getMessage());
			Message message = Message.obtain(handler, ac.uc.mobile.pns.R.id.request_failed);
			message.sendToTarget();
		} catch (IOException e) {
			Log.e(TAG, "IOException :"+ e.getMessage());
			Message message = Message.obtain(handler, ac.uc.mobile.pns.R.id.request_failed);
			message.sendToTarget();
		} catch (JSONException e) {
			Log.e(TAG, "JSONException :"+ e.getMessage());
			Message message = Message.obtain(handler, ac.uc.mobile.pns.R.id.request_failed);
			message.sendToTarget();
		} finally {
			if (client != null) {
//				client.close();
			}
		}
	}

	private static String getEncoding(HttpEntity entity) {
		HeaderElement[] elements = entity.getContentType().getElements();
		if (elements != null && elements.length > 0) {
			String encoding = elements[0].getParameterByName("charset")
					.getValue();
			if (encoding != null && encoding.length() > 0) {
				return encoding;
			}
		}
		return "UTF-8";
	}
	
	private static JSONObject getJsonObjectFromMap(Map params) throws JSONException {

	    //all the passed parameters from the post request
	    //iterator used to loop through all the parameters
	    //passed in the post request
	    Iterator iter = params.entrySet().iterator();

	    //Stores JSON
	    JSONObject holder = new JSONObject();

	    //using the earlier example your first entry would get email
	    //and the inner while would get the value which would be 'foo@bar.com' 
	    //{ fan: { email : 'foo@bar.com' } }

	    //While there is another entry
	    while (iter.hasNext()) 
	    {
	        //gets an entry in the params
	        Map.Entry pairs = (Map.Entry)iter.next();

	        //creates a key for Map
	        String key = (String)pairs.getKey();

	        //Create a new map
	        Map m = (Map)pairs.getValue();   

	        //object for storing Json
	        JSONObject data = new JSONObject();

	        //gets the value
	        Iterator iter2 = m.entrySet().iterator();
	        while (iter2.hasNext()) 
	        {
	            Map.Entry pairs2 = (Map.Entry)iter2.next();
	            data.put((String)pairs2.getKey(), (String)pairs2.getValue());
	        }

	        //puts email and 'foo@bar.com'  together in map
	        holder.put(key, data);
	    }
	    return holder;
	}	
	
	public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

		Matrix matrix = new Matrix();
		switch (orientation) {
		case ExifInterface.ORIENTATION_NORMAL:
			return bitmap;
		case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
			matrix.setScale(-1, 1);
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			matrix.setRotate(180);
			break;
		case ExifInterface.ORIENTATION_FLIP_VERTICAL:
			matrix.setRotate(180);
			matrix.postScale(-1, 1);
			break;
		case ExifInterface.ORIENTATION_TRANSPOSE:
			matrix.setRotate(90);
			matrix.postScale(-1, 1);
			break;
		case ExifInterface.ORIENTATION_ROTATE_90:
			matrix.setRotate(90);
			break;
		case ExifInterface.ORIENTATION_TRANSVERSE:
			matrix.setRotate(-90);
			matrix.postScale(-1, 1);
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			matrix.setRotate(-90);
			break;
		default:
			return bitmap;
		}
		try {
			Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			bitmap.recycle();
			return bmRotated;
		} catch (OutOfMemoryError e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
	}
	
	
	public HttpClient getNewHttpClient() {
	    try {
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        try {
				trustStore.load(null, null);
			} catch (CertificateException e) {
				Log.e(TAG, e.getMessage());
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
	        SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	        HttpParams params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", sf, 443));

	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
	        return new DefaultHttpClient(ccm, params);
	    } catch (KeyManagementException e) {
	    	return new DefaultHttpClient();
	    } catch (UnrecoverableKeyException e) {
	    	return new DefaultHttpClient();
		} catch (NoSuchAlgorithmException e) {
			return new DefaultHttpClient();
		} catch (KeyStoreException e) {
			return new DefaultHttpClient();
		}
	}
}
