package com.zorro.testcode;

import java.io.File;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;


public class RetailPushModule {
//	private static int COMPONENT_RETAIL_MODE_MESSAGE = 100;
//
//	private static String RETAIL_MODE_URI = "ws://" + RetailConfig.HOST + "/console/api/nemo/ws";
////	private static String RETAIL_MODE_URI = "ws://retail.zaijia.cn:9080/console/api/nemo/ws";
////	private static String RETAIL_MODE_URI = "ws://192.168.0.106:8080/console/api/nemo/ws";
//
//
//	private IModuleContainer mContainer = null;
//	private WebSocketClient mSocketClient = null;
//	private Timer mRetryTimer = null;
//	private boolean mSocketConnected = false;
//	private Context mContext = null;
//
//	private static int NETTYPE_WIFI = 1;
//	private static int NETTYPE_4G = 2;
//
//	private static int NET_STRENGTH_STRONGEST = 3;
//	private static int NET_STRENGTH_STRONG = 2;
//	private static int NET_STRENGTH_WEAK = 1;
//	private static int NET_STRENGTH_UNDEFINED = 0;
//
//	private long mChangeVolumeTime = 0;
//
//	private static Msg.RetailMode.RetailModeLocationMsg mLocationMsg = null;
//
//	private boolean mRetrying = false;
//
//	String mSn 			= null;
//	String mVersionName	= null;
//	private DownloadManager mDownloadManager;
//	private RetailDownloadManager mRetailDownloadManager;
//
//	private AudioManager mAudioManager = null;
//
//	private Handler mHandler;
//
//	private static final int UPDATE_VOLUME_TAG = 200;
//
//	public RetailPushModule(Context ctx) {
//		this.mContext = ctx;
//
//		mSn 			= GenerateSN.generateDeviceSn(mContext);
//		mVersionName	= VersionUtil.getVersionName(ctx);
//		mDownloadManager = new DownloadManager(null);
//		mRetailDownloadManager = new RetailDownloadManager(this);
//		mAudioManager =  (AudioManager) this.mContext.getSystemService(Service.AUDIO_SERVICE);
//		mHandler = new Handler();
//		registerReceiver();
//
//		mHandler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//
//				checkRetailModeRes();
//				checkRTVideoRes();
//			}
//		}, 2000);
//	}
//
//	@Override
//	public ModuleTag getModuleTag() {
//		return ModuleTag.RETAIL_PUSH;
//	}
//
//	@Override
//	public void destroy() {
//		mDownloadManager.stop();
//		mContext.unregisterReceiver(mVolumeReceiver);
//	}
//
//
//	@Override
//	public void onMessage(ModuleTag from, Message message) {
//		if (message.what == Msg.RetailMode.CLIENT_RETAIL_MODE_STATUS_UPDATE) {
//			if (mSocketConnected) {
//				NetworkLog.i("weekend I got a msg, " + message);
//				Msg.RetailMode.RetailModeReportData reportData = new Msg.RetailMode.RetailModeReportData();
//				reportData.type = RetailModeStat.RTM_STAT_SCENARIO;
//				reportData.status = message.getData().getInt(Msg.RetailMode.KEY_RETAIL_MODE_STATUS);
//				String msgStr = JsonUtil.toJson(reportData);
//				mSocketClient.sendMessage(COMPONENT_RETAIL_MODE_MESSAGE, msgStr);
//			} else {
//				NetworkLog.i("weekend I got a msg, but socket not connected.");
//			}
//			// also record location.
//			if (mLocationMsg == null) {
//				mLocationMsg = new Msg.RetailMode.RetailModeLocationMsg();
//				mLocationMsg.type = RetailModeStat.RTM_DATA_LOCATION;
//			}
//			mLocationMsg.longitude = message.getData().getDouble(Msg.RetailMode.KEY_RETAIL_MODE_LONGITUDE);
//			mLocationMsg.latitude = message.getData().getDouble(Msg.RetailMode.KEY_RETAIL_MODE_LATITUDE);
//			mLocationMsg.locality = message.getData().getString(Msg.RetailMode.KEY_RETAIL_MODE_LOCALITY);
//
//		} else if (message.what == Msg.RetailMode.CLIENT_RETAIL_MODE_USER_STAY_INFO) {
//			if (mSocketConnected) {
//				String userStayInfoStr = message.getData().getString(Msg.RetailMode.KEY_RETAIL_MODE_USER_STAY_INFO);
//				NetworkLog.i("updatet user stay info: " + userStayInfoStr);
//				if (userStayInfoStr != null) {
//					Msg.RetailMode.UserStayInfoMsg info = JsonUtil.toObject(userStayInfoStr, Msg.RetailMode.UserStayInfoMsg.class);
//					if (null != info) {
//						info.type = RetailModeStat.RTM_DATA_USER_STAY_INFO;
//						String msgString = JsonUtil.toJson(info);
//						NetworkLog.i("weekend reporting user stay info. = " + msgString);
//						mSocketClient.sendMessage(COMPONENT_RETAIL_MODE_MESSAGE, msgString);
//					}
//				}
//			}
//		}
//	}
//
//	@Override
//	public void setContainer(IModuleContainer container) {
//		if(TextUtils.isEmpty(mSn)){
//			return;
//		}
//		this.mContainer = container;
//
//		// init socket information.
//		mSocketClient = new WebSocketClient();
//		mSocketClient.addListener(this);
//
//		tryConnect(0, 1000 * 15);
//
//		startPingTimer();
//		startLocationReportTimer();
//	}
//
//	private VolumeReceiver mVolumeReceiver = new VolumeReceiver() ;
//
//	private void registerReceiver() {
//		IntentFilter filter = new IntentFilter() ;
//		filter.addAction("android.media.VOLUME_CHANGED_ACTION") ;
//		mContext.registerReceiver(mVolumeReceiver, filter) ;
//	}
//
//	/**
//	 * 处理音量变化时的界面显示
//	 * @author long
//	 */
//	private class VolumeReceiver extends BroadcastReceiver {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			//如果音量发生变化则更改seekbar的位置
//			if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
//				int currVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);// 当前的媒体音量
//				NetworkLog.i("weekend volume change volume = " + currVolume);
//				mChangeVolumeTime = System.currentTimeMillis();
//				mHandler.removeCallbacks(mUpdateVolumeRunnable);
//				mHandler.postDelayed(mUpdateVolumeRunnable, 2000);
//			}
//		}
//	}
//
//
//	private Runnable mUpdateVolumeRunnable = new Runnable() {
//		@Override
//		public void run() {
//			int seq = getLocalVolumeSeq();
//			if (seq >= 0) {
//				seq++;
//				setLocalVolumeSeq(seq);
//				compareVolume();
//			}
//		}
//	};
//
//	private void setLocalVolumeSeq(int seq) {
//		ApplicationUtil.getRetailConfig().updateValue("localSeq", seq + "");
//	}
//	private int getLocalVolumeSeq() {
//		return ApplicationUtil.getRetailConfig().getInt("localSeq", -1);
//	}
//
//	private void startPingTimer() {
//		Timer timer = new Timer();
//		timer.schedule(new TimerTask() {
//
//			@Override
//			public void run() {
//				try {
//					sendHeartBeat();
//				}catch(Exception e) {}
//			}
//		}, 0, 10*1000);
//	}
//
//	private void startLocationReportTimer(){
//		Timer timer = new Timer();
//		timer.schedule(new TimerTask() {
//
//			@Override
//			public void run() {
//				try {
//					sendLocation();
//				}catch(Exception e) {}
//			}
//		}, 15*60*1000, 60*60*1000); // update every hour
//	}
//
//
//	private void checkRetailModeRes() {
//		NetworkLog.i("checkRetailModeRes");
//
//		SplashData data = ApplicationUtil.getRetailConfig().getSplashData();
//		NetworkLog.i("hzhenx splashData = " + (data == null ? " null" : data));
//		mRetailDownloadManager.downloadSplashData(data);
//
//		FloatIcon icon = ApplicationUtil.getRetailConfig().getFloatICON();
//		NetworkLog.i("hzhenx FloatIcon = " + (icon == null ? " null" : icon));
//
//		mRetailDownloadManager.downloadFloatIcon(icon);
//
//	}
//
//	private void checkRTVideoRes() {
//		NetworkLog.i("rtVideo checkRTVideoRes");
//		RTVideo rtVideo = ApplicationUtil.getRetailConfig().getRTVideo();
//		NetworkLog.i("rtVideo checkRTVideoRes rtVideo = " + rtVideo);
//
//		if (rtVideo != null) {
//			if (!mRetailDownloadManager.isRTVideoDownloaded(rtVideo)) {
//				NetworkLog.i("rtVideo checkRTVideoRes not downloaded");
//				ApplicationUtil.getRetailConfig().updateValue(vulture.util.RetailConfig.KEY_RETAIL_MODE_VIDEO, "");
//				mRetailDownloadManager.downloadRTVide(rtVideo);
//			} else {
//				NetworkLog.i("rtVideo checkRTVideoRes downloaded");
//			}
//		} else {
//			ApplicationUtil.getRetailConfig().updateValue(vulture.util.RetailConfig.KEY_RETAIL_MODE_VIDEO, "");
//		}
//		RxBus.get().post(RxBusEventType.Retailmode.RT_CONFIG_UPDATE, RxNullArgs.Instance);
//	}
//	private static final Msg.RetailMode.HeartBeat heartbeatMsg = new Msg.RetailMode.HeartBeat();
//	private void sendHeartBeat() {
//		if(!mSocketConnected){
//			return;
//		}
//		NetworkLog.i("weekend sending ping.");
//		int wifiStr = getWifiStrength();
//		heartbeatMsg.netType = NETTYPE_WIFI;// hardcode to wifi.
//		heartbeatMsg.strength = wifiStr;
//		mSocketClient.sendMessage(COMPONENT_RETAIL_MODE_MESSAGE, JsonUtil.toJson(heartbeatMsg));
//	}
//
//	private void sendLocation() {
//		if (!mSocketConnected || mLocationMsg == null) {
//			return;
//		}
//		NetworkLog.i("weekend sending location.");
//		mSocketClient.sendMessage(COMPONENT_RETAIL_MODE_MESSAGE, JsonUtil.toJson(mLocationMsg));
//	}
//
//	private int oldVolume = 0;
//	private Msg.RetailMode.SystemVolume mSystemVolume;
//
//	private void compareVolume() {
//		NetworkLog.i("volume compareVolume ");
//
//		int newVolume = mAudioManager
//				.getStreamVolume(AudioManager.STREAM_MUSIC);
//
//		int maxVolume = mAudioManager
//				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//
//		if (newVolume != oldVolume) {
//			double dVolume = newVolume*1.0 / maxVolume;
//			oldVolume = newVolume;
//
//			mSystemVolume		= new Msg.RetailMode.SystemVolume();
//			mSystemVolume.type = RetailModeStat.RTM_REPORT_VOLUMN;
//			mSystemVolume.deviceSn = mSn;
//			mSystemVolume.seq = getLocalVolumeSeq();
//			mSystemVolume.voiceVolume = (int)(dVolume * 100);
//		}
//
//		if (null != mSystemVolume) {
//			String message		= JsonUtil.toJson(mSystemVolume);
//			NetworkLog.i("sendVolumeChangeMessage " + message);
//			mSocketClient.sendMessage(COMPONENT_RETAIL_MODE_MESSAGE, message);
//		}
//	}
//
//
//	private long configTimestamp = 0;
//	private final long TIME_MILLIS_PER_DAY = 2 * 60 * 60 * 1000;
//	private void sendConfigRequest() {
//		long currentTime = System.currentTimeMillis();
//		long timeDiff = currentTime - configTimestamp;
//
//		NetworkLog.i("sendConfigRequest " + configTimestamp + " " + currentTime);
//
//		if (timeDiff >= TIME_MILLIS_PER_DAY) {
//			configTimestamp = currentTime;
//
//
//			Msg.RetailMode.RTMMsgBase configMsg = new Msg.RetailMode.RTMMsgBase();
//			configMsg.type = RetailModeStat.RTM_GET_CONFIG;
//			String request	= JsonUtil.toJson(configMsg);
//			NetworkLog.i("sendConfigRequest " + request);
//			mSocketClient.sendMessage(COMPONENT_RETAIL_MODE_MESSAGE, JsonUtil.toJson(configMsg));
//		}
//	}
//
//	@Override
//	public synchronized void onConnect() {
//		NetworkLog.i("weekend socket connected ");
//		mSocketConnected = true;
//		mRetrying = false;
//		if (mRetryTimer != null) {
//			mRetryTimer.cancel();
//		}
//		mRetryTimer = null;
//
//		sendConfigRequest();
//	}
//
//	@Override
//	public void onMessage(int component, String message) {
//		NetworkLog.i("weekend got component = " + component + ", msg = " + message);
//
//		if (component == COMPONENT_RETAIL_MODE_MESSAGE) {
//			Msg.RetailMode.RetailModeResponse response = JsonUtil.toObject(message, Msg.RetailMode.RetailModeResponse.class);
//
//			NetworkLog.i("weekend got status update result status = " + response.status + ", type = " + response.type);
//			if (!TextUtils.isEmpty(response.ack)) {
//				mSocketClient.sendMessage(COMPONENT_RETAIL_MODE_MESSAGE, response.ack);
//			}
//
//			if (null != response && response.status == 200) {
//				if (response.type == RetailModeStat.RTM_DATA_USER_STAY_INFO) {
//					NetworkLog.i("weekend got result, sending it back.");
//					// server responds, notify retail mode service.
//					Message msg = Message.obtain();
//					msg.what = Msg.RetailMode.CLIENT_RETAIL_MODE_USER_STAY_INFO_RSP;
//					msg.getData().putBoolean(Msg.RetailMode.KEY_RETAIL_MODE_USER_STAY_INFO_RESULT, true);
//					mContainer.sendMessage(ModuleTag.RETAIL_PUSH, ModuleTag.RETAIL_MODE_MODULE, msg);
//				} else if (response.type == RetailModeStat.RTM_RES_CONFIG) {
//					didReceiveConfigMessage(message);
//				} else if (response.type == RetailModeStat.RTM_REPORT_VOLUMN) {
//					mSystemVolume	= null;
//				}
//			}
//
//
//		}
//	}
//
//	@Override
//	public void onText(String text) {
//	}
//
//	@Override
//	public synchronized void onDisconnect(int code, String reason) {
//		NetworkLog.i("weekend ondisconnect ");
//		mSocketConnected = false;
//
//		if (code == 4004) { // 4004 invalid sn.
//			// retry in the next 3 hours.
//			tryConnect(1000*60*60*3, 1000*60*60*3);
//		} else {
//			// otherwise we try to reconnect in the next 30 seconds.
//			tryConnect(1000*30, 1000*30);
//		}
//	}
//
//	@Override
//	public void onError(int code, String message) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onPong() {
//		NetworkLog.i("onPong ");
//	}
//
//
//	/**
//	 * 尝试重新建立websokect
//	 *
//	 * @param delay
//	 * @param period
//	 */
//	private synchronized void tryConnect(long delay, long period) {
//		if (mSocketConnected || mRetrying) {
//			return;
//		}
//
//		if (mRetryTimer == null) {
//			NetworkLog.i("weekend retry connect");
//			mRetrying = true;
//			mRetryTimer = new Timer();;
//			mRetryTimer.schedule(new TimerTask() {
//
//				@Override
//				public void run() {
//					boolean isRt = ApplicationUtil.getRetailConfig().isRetailModeEnabled();
//					String uri = RETAIL_MODE_URI + "?deviceSn=" + mSn + "&ver=" +  mVersionName + "&rt=" + (isRt ? "1" : "0");
//					NetworkLog.i("trying to connect sn = " + uri);
//					mSocketClient.connect(new AndroidSocketWriter(), URI.create(uri), null);
//				}
//			}, delay, period);
//		}
//	}
//
//	private int getWifiStrength() {
//		int strength = NET_STRENGTH_UNDEFINED;
//		try {
//			WifiManager wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
//			if (wifiManager != null) {
//				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//				if (wifiInfo != null) {
//					int rssi = wifiInfo.getRssi();
//					if (rssi > -50) {
//						strength = NET_STRENGTH_STRONGEST; // strongest
//					} else if (rssi <= -50 && rssi >= -70) {
//						strength = NET_STRENGTH_STRONG;
//					} else {
//						strength = NET_STRENGTH_WEAK;
//					}
//				}
//			}
//		} catch (Exception e) {}
//		return strength;
//	}
//
//	private void deleteOldVideoData(RTVideo video) {
//		if (video == null || video.filelist == null && video.filelist.size() == 0) {
//			return;
//		}
//		String path = RetailDownloadManager.getRtVideoResDir();
//		File dir = new File(path);
//		if (dir != null || dir.isDirectory()) {
//
//			File[] files = dir.listFiles();
//			if (files.length < 5) {
//				return;
//			}
//			for (File file : files) {
//				if (file.isDirectory()) {
//					NetworkLog.i("delete file " + file.getName());
//					File[] childFiles = file.listFiles();
//					for (File afile : childFiles) {
//						afile.delete();
//					}
//					continue;
//				}
//				boolean isFind = false;
//				String name = file.getName();
//				for (RTVideo.RTVideoItem item : video.filelist) {
//					String md5Name = Md5Util.MD5(item.url);
//					if (md5Name != null && name.equals(md5Name)) {
//						isFind = true;
//						break;
//					}
//
//					md5Name = Md5Util.MD5(item.coverUrl);
//					if (md5Name != null && name.equals(md5Name)) {
//						isFind = true;
//						break;
//					}
//				}
//				if (!isFind) {
//					NetworkLog.i("delete file " + file.getName());
//					file.delete();
//				}
//			}
//		}
//	}
//
//	@Override
//	public void rtVideoDidFinishDownload(RTVideo video) {
//		NetworkLog.i("rtVideoDidFinishDownload 1 ");
//
//		ApplicationUtil.getRetailConfig().updateValue(vulture.util.RetailConfig.KEY_RETAIL_MODE_VIDEO, JsonUtil.toJson(video));
//		//TODO: hzhenx 通知
//		RxBus.get().post(RxBusEventType.Retailmode.RT_RES_DOWNLOADED, RxNullArgs.Instance);
//
//		NetworkLog.i("rtVideoDidFinishDownload rtvide = " + video);
//
//		deleteOldVideoData(video);
//
//		if (mSocketConnected) {
//			NetworkLog.i("rtVideoDidFinishDownload 2 ");
//			Msg.RetailMode.RetailModeReportData reportData = new Msg.RetailMode.RetailModeReportData();
//			reportData.type = RetailModeStat.RTM_STAT_SCENARIO;
//			reportData.status = RTM_VALUE_VIDEO_SUCCESS_DOWNLOAD;
//			String msgStr = JsonUtil.toJson(reportData);
//			mSocketClient.sendMessage(COMPONENT_RETAIL_MODE_MESSAGE, msgStr);
//		} else {
//			NetworkLog.i("rtVideoDidFinishDownload 3 ");
//			NetworkLog.i("weekend I got a msg, but socket not connected.");
//		}
//	}
//
//
//	@Override
//	public void rtResDidFinishDownload() {
//		NetworkLog.i("rtResDidFinishDownload");
//
//		RxBus.get().post(RxBusEventType.Retailmode.RT_CONFIG_UPDATE, RxNullArgs.Instance);
//	}
//
//	@Override
//	public void rtVideoDidFailedDownload(RTVideo video) {
//		if (mSocketConnected) {
//			Msg.RetailMode.RetailModeReportData reportData = new Msg.RetailMode.RetailModeReportData();
//			reportData.type = RetailModeStat.RTM_STAT_SCENARIO;
//			reportData.status = RTM_VALUE_VIDEO_FAIL_DOWNLOAD;
//			String msgStr = JsonUtil.toJson(reportData);
//			mSocketClient.sendMessage(COMPONENT_RETAIL_MODE_MESSAGE, msgStr);
//		} else {
//			NetworkLog.i("weekend I got a msg, but socket not connected.");
//		}
//	}
//
//	private void didReceiveConfigMessage(String message) {
//		ApplicationUtil.getRetailConfig().update(message);
//		NetworkLog.i("didReceiveConfigMessage 1");
//
//		try {
//
//			RTContact rtContact = JsonUtil.toObject(message, RTContact.class);
//			if (null != rtContact && rtContact.memberlist != null) {
//				NetworkLog.i("didReceiveConfigMessage 2");
//				rtContact.filtData();
//				if (!mRetailDownloadManager.isRTContactDownloaded(rtContact)) {
//					NetworkLog.i("didReceiveConfigMessage 3");
//					mRetailDownloadManager.downloadRTContact(rtContact);
//				}
//
//				ApplicationUtil.getRetailConfig().updateValue(vulture.util.RetailConfig.KEY_RETAIL_MODE_CONTACT, JsonUtil.toJson(rtContact));
//				NetworkLog.i("rtVideo Downloaded rtcontact = " + rtContact);
//			}
//
//			RTVideo rtVideo = JsonUtil.toObject(message, RTVideo.class);
//			if (null != rtVideo && rtVideo.filelist != null) {
//				rtVideo.filterData();
//				NetworkLog.i("didReceiveConfigMessage 4");
//				if (rtVideo.isValid()) {
//					if (mRetailDownloadManager.isRTVideoDownloaded(rtVideo)) {
//						NetworkLog.i("didReceiveConfigMessage 5");
//						rtVideoDidFinishDownload(rtVideo);
//
//					} else {
//						NetworkLog.i("didReceiveConfigMessage 6");
//						mRetailDownloadManager.downloadRTVide(rtVideo);
//
//					}
//				} else {
//					rtVideoDidFailedDownload(rtVideo);
//				}
//
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		checkRetailModeRes();
//
//		updateVolume();
//
//		Message msg = Message.obtain();
//		msg.what = vulture.api.Msg.ActivityProxyModule.RETAIL_MODE_CONFIG_UPDATE;
//		msg.arg1 = ApplicationUtil.getRetailConfig().getVoiceEnhance();
//		mContainer.sendMessage(getModuleTag(), ModuleTag.ACTIVITY_PROXY_MODULE, msg);
//		mContainer.sendMessage(getModuleTag(), ModuleTag.AUDIO_MODULE, msg);
//
//		RxBus.get().post(RxBusEventType.Retailmode.RT_CONFIG_UPDATE, RxNullArgs.Instance);
//	}
//
//	private void updateVolume() {
//
//		int localSeq = getLocalVolumeSeq();
//		int seq = ApplicationUtil.getRetailConfig().getInt("seq", -1);
//
//		NetworkLog.i("volume updateVolume  seq = " + seq + " localSeq = " + localSeq);
//
//		if (seq >= 0) {
//			int volume = ApplicationUtil.getRetailConfig().getInt("volume", -1);
//
//			if (volume == -1) {
//				return;
//			}
//			int maxMusicVolume = mAudioManager
//					.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//			int musicVolume = (int) ((volume * 1.0 / 100) * maxMusicVolume);
//			if (seq >= localSeq) {
//				setLocalVolumeSeq(seq);
//				mAudioManager
//						.setStreamVolume(AudioManager.STREAM_MUSIC, musicVolume, 0);
//			} else {
//
//				oldVolume = musicVolume;
//				compareVolume();
//			}
//		}
//	}
}
