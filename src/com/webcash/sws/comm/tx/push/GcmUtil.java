package com.webcash.sws.comm.tx.push;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.webcash.sws.comm.debug.PrintLog;
import com.webcash.sws.comm.tx.push.GcmListener.onRegistrationListener;

public class GcmUtil {
	

	private static final String LOG_TAG = "WEBCASH";
	
    public static final String PROPERTY_REG_ID = "registration_id";
    
    private static final String PROPERTY_APP_VERSION = "appVersion";
    
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    
    /**
	 * http connection timeout 시간
	 */	
	private static final int KEY_HTTP_TIMEOUT = 30000;
    	
	private Activity mActivity;
	
	private String mSenderID;
	
	private String mServerAddress;
	
	private GoogleCloudMessaging mGcm;
	
    //private String mRegid;
    
    private boolean isDebug = false;
    
    private String ErrorMsg;
	private String ErrorCd;
	
    public GcmUtil(Activity atvt) {
    	mActivity = atvt;
    }
    
    public void setDebugYN(boolean value) {
    	isDebug = value;
    }
    
    public void setServerAddress(String serverAddress) {
    	mServerAddress = serverAddress;
    }
    
    public void setSenderId(String senderId) {
    	mSenderID = senderId;
    }
    
	public GcmUtil(Activity atvt, String serverAddress, String senderId) {
		mActivity = atvt;
		mServerAddress = serverAddress;
		mSenderID = senderId;
	}
	
	/**
	 * 푸시서비스에서 앱의 현재 푸시아이디를 가져온다. <br>
     * regid값이 빈값이면 등록과정을 거쳐야 한다. <br>
     * 앱 버전이 변경되면 빈값을 반환한다.
	 * @param context
	 * @return : 푸시아이디
	 */
    public String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (TextUtils.isEmpty(registrationId)) {
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            PrintLog.printSingleLog(LOG_TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

	
	
    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
	
    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return mActivity.getSharedPreferences(mActivity.getClass().getSimpleName(), Context.MODE_PRIVATE);
    }
    
//    /**
//     * 푸시 서버에 등록된 푸시아이디를 삭제한다.
//     */
//    public void unRegisterInBackground(String pushServerUrl, final TX_PUSH_PS0002_REQ reqMsg, onPushTranListener listener) {
//    	
//    	
//    	mListener = listener;
//    	
//    	PushTran pushTran = new PushTran(pushServerUrl, listener);
//
//    	pushTran.makeJsonData(TX_PUSH_PS0002_REQ.TXNO, reqMsg.getSendMessage());	
//    	pushTran.execute(TX_PUSH_PS0002_REQ.TXNO);
//    	
//    	new AsyncTask<Object, Object, Void>() {
//
//			@Override
//			protected Void doInBackground(Object... params) {
//				
//				try {
//				
//					if (TextUtils.isEmpty(mServerAddress)) {
//						mActivity.runOnUiThread(new Runnable() {
//							@Override
//							public void run() {
//			                    if (mOnRegistrationListener != null)
//			                    	mOnRegistrationListener.onRegistrationError("I9001", "Server Address 가 설정되지 않았습니다.");
//							}
//						});
//			    	}
//			    	
//					// 농협 Format
//			    	String sendData =   
//							  "{ \"_tran_cd\":\"ps0005\" ,\"_tran_req_data\": [{"
//				            + "    \"PUSHSERVERKIND\":\"GCM\","
//				            + "    \"APPID\":\"" + mActivity.getPackageName() + "\","
//				            + "    \"PUSHID\":\"" + regId + "\","
//				            + "    \"REVOKEREASON\":\"\""
//				            + "}]}";
//			    	
////					// SWS Format
////			    	String sendData =   
////							  "{ \"_tran_cd\":\"PS0005\" ,\"_tran_req_data\": [{"
////				            + "    \"_pushserver_kind\":\"GCM\","
////				            + "    \"_app_id\":\"com.webcash.bizplay.collabo\","
////				            + "	   \"_company_id\":\"BIZPLAY\","
////				            + "    \"_device_id\":\"" + regId + "\","
////				            + "    \"_remark\":\"\""
////				            + "}]}";
//					
//			    	
//			    	HttpURLConnection conn	= null;
//			 		OutputStreamWriter wr	= null;
//			 		BufferedReader rd 		= null;
//			 		
//			
//					///////////////////////////////////////////////////////////////////////
//					// HTTPS 추가
//					URL url = new URL(mServerAddress);
//					
//					printLog("Nebmoa PushServer SEND ::: " + sendData);
//					
//					String data = "JSONData=" +  URLEncoder.encode(sendData);
//					
//					 if (url.getProtocol().toLowerCase().equals("https")) { 
//					     trustAllHosts(); 
//					     HttpsURLConnection https = (HttpsURLConnection) url.openConnection(); 
//					     https.setHostnameVerifier(DO_NOT_VERIFY); 
//					     conn = https; 
//					 } else { 
//					 	conn = (HttpURLConnection) url.openConnection(); 
//					 }
//					
//					conn.setConnectTimeout(KEY_HTTP_TIMEOUT);
//					conn.setUseCaches(false);
//					conn.setDoOutput(true);
//					conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
//					 
//					wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
//					wr.write(data);
//					wr.flush();
//					
//					rd = new BufferedReader(new InputStreamReader(conn.getInputStream(),"EUC-KR"));
//					String line = null;
//					String aResponseBody = "";
//					while ((line = rd.readLine()) != null) {
//					 	aResponseBody += line;
//					}
//					 
//					printLog("Nebmoa PushServer RECV ::: " + aResponseBody);					
//					
//					mActivity.runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//		                    if (mOnUnRegistrationListener != null)
//		                    	storeRegistrationId(mActivity, "");
//		                    	mOnUnRegistrationListener.onUnRegistrationCompleted();
//						}
//					});
//					
//					// HTTPS 추가            
//					///////////////////////////////////////////////////////////////////////
//				} catch (Exception e) {
//					e.printStackTrace();
//        			
//        			mActivity.runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//		                    if (mOnUnRegistrationListener != null)
//		                    	mOnUnRegistrationListener.onUnRegistrationError("2002", "서버와의 연결에서 오류가 발생하였습니다.");
//						}
//					});
//				}
//				return null;
//			}
//		}.execute(null, null);
//    	
//    }
    
    
    
    /**
     * Google 서버에서 푸시아이디를 가져와 반환한다.
     */
    public void registerInBackground(final String senderId, final onRegistrationListener listener) throws Exception {
    	
    	try {
    		
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {

					try {

						if (mGcm == null) {
							mGcm = GoogleCloudMessaging.getInstance(mActivity);
						}

						String regId = mGcm.register(senderId);
						storeRegistrationId(mActivity, regId);

						if (listener != null)
							listener.onRegistrationCompleted(regId);
					} catch (IOException ex) {
						if (listener != null)
							listener.onRegistrationError("I9999", ex.getMessage());
						return null;
					}
					return null;
				}

			}.execute(null, null, null);       
    	
    	} catch (Exception e) {
    		if (listener != null)
    			listener.onRegistrationError("I9999", e.getMessage());
    	}
    	
//    	if (TextUtils.isEmpty(mSenderID)) {
//			mActivity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//                    if (mOnRegistrationListener != null)
//                    	mOnRegistrationListener.onRegistrationError("I9000", "Sender ID 가 설정되지 않았습니다.");
//				}
//			});
//			return;
//    	}
//    	
//    	if (TextUtils.isEmpty(mServerAddress)) {
//			mActivity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//                    if (mOnRegistrationListener != null)
//                    	mOnRegistrationListener.onRegistrationError("I9001", "Server Address 가 설정되지 않았습니다.");
//				}
//			});
//			return;
//    	}
//    	
//    	
//        if (mGcm == null) {
//        	mGcm = GoogleCloudMessaging.getInstance(mActivity);
//        }
//        mRegid = mGcm.register(senderId);
//    	
//    	PushTran pushTran = new PushTran(pushServerUrl, listener);
//
//    	pushTran.makeJsonData(TX_PUSH_PS0002_REQ.TXNO, reqMsg.getSendMessage());	
//    	pushTran.execute(TX_PUSH_PS0002_REQ.TXNO);
    	
    }
    
	
    
//    public void sendMessageByXmpp(final int msgId, final Bundle msgData) {
//    	sendMessageByXmpp(msgId, msgData, null);
//    }
//    
//    public void sendMessageByXmpp(final int msgId, final Bundle msgData, final onSendMessageListener i) {
//    	
//    	
//    	
//    	
//    	if (mGcm == null) {
//        	mGcm = GoogleCloudMessaging.getInstance(mActivity);
//        }
//    	
//    	new AsyncTask<Void, Void, String>() {
//    		/**
//    		 * @return : 정상인경우 공백을, 오류인 경우 오류 메세지를 반환한다.
//    		 */
//            @Override
//            protected String doInBackground(Void... params) {
//            	
//                try {                    
//                    mGcm.send(mSenderID + "@gcm.googleapis.com", String.valueOf(msgId), msgData);
//                } catch (IOException ex) {
//                	return "Error :" + ex.getMessage();
//                }
//                return "";
//            }
//
//            /**
//             * @Parameters resultMsg : 정상인경우 공백을, 오류인 경우 오류 메세지를 가져온다.
//             */
//            @Override
//            protected void onPostExecute(String resultMsg) {
//        		mActivity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//		            	if (i != null) {
//		            		i.onSendMessageCompleted(msgId);
//		            	}
//					}
//				});
//            }
//        }.execute(null, null, null);
//    	
//    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(LOG_TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
    
    
	
	
	/**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public static boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {           
            return false;
        }
        return true;
    }
	
}
