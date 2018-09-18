package com.zorro.testcode;

import java.util.List;

public class Msg {
	private Msg() {
	}

//	public static final class Service {
//		private Service() {
//		}
//
//		public final static int REGISTER_CLIENT = 0;
//		public final static int UNREGISTER_CLIENT = 1;
//
//		public final static String NETWORK_SERVICE = "nemo.intent.networkservice.START";
//		public final static String KEY_MESSAGE = "message";
//		public final static String KEY_NW_STATE = "nw_state";
//		public final static String KEY_URI = "uri";
//		public final static String KEY_INTERNET_STATE = "internet_state";
////		public final static String KEY_ETHERNET_STATE = "ethernet_state";
//		public final static String KEY_EXTERNALDEVICE_STATE = "external_devicestate";
//		public final static String KEY_HTTP_REDIRECT_RESULT = "http_redirect";
//		public final static String KEY_WS_DISCONNECT_CODE = "disconnect_code";
//		public final static String KEY_WS_DISCONNECT_REASON = "disconnect_reason";
//		public final static String WIFI_CB_DISABLERESONE = "disablereasone";
//		public final static String WIFI_CB_WIFISTATUS = "wifistatus";
//		public final static String WIFI_CB_ACCESSPOINT = "accesspoint";
//	}
//
//	// message sent by PushModule
//	public static final class Push {
//		private Push() {
//		};
//
//		public static final int WS_ACTIVE = 1000;
//		public static final int WS_INACTIVE = 1001;
//		public static final int WS_BUSINESS = 1002;
//		public static final int WS_REAL_NOTIFICATION = 1003;
//		public static final int WS_BIND = 1004;
//		public static final int WS_SIGNALING = 1005;
//		public static final int WS_KICKED_OUT = 1006;
//		public static final int WS_CHECK_VERSION = 1007;
//		public static final int WS_PING = 1008;
//		public static final int WS_DUMMY = 1099;
//		public static final int WS_AUTOTEST = 2001;
//		public static final int WS_NONE = 1999;
//	}

//	// message sent by NetworkModule
//	public static final class Network {
//		private Network() {
//		};
//
//		public static final int NW_CHANGED = 2010;
//		public static final int NW_STRENGTH_CHANGED = 2011;
//		
//		public static final int NW_WIFI_STATUS_CHANGED = 2012;
//		public static final int NW_WIFI_AP_CHANGED = 2013;
//		
//		// detect too many disconnect from WEB SOCKET
//		public static final int NW_WS_DISCONNECT_TOOMANY = 2014;
//		
//		public static final int NW_INTERNET_CHECK_RESULT = 2015;
//		
//		public static final int NW_EXTERNAL_DEVICE_STATUS_CHANGED = 2016;
//		
//		public static final int NW_HTTP_REDIRECT_CHECK_RESULT = 2017;
//		
//		public static final int NW_PLAY_MUSIC_REQUEST = 2018;
//	}
//
//	public static final class MusicRes{
//		private MusicRes() {
//		};
//		
//		public static final int BIND_ID = 0;
//		public static final int NW_CONNECTED_ID = 1;
//		public static final int WRONG_PW_ID = 2;
//	}
//	
//	// message sent by Client
//	public static final class Client {
//		private Client() {
//		};
//
//		public static final int CLIENT_WS_REGISTER = 2100;
//		public static final int CLIENT_START_PUSH = 2101;
//		public static final int CLIENT_STOP_PUSH = 2102;
//		public static final int CLIENT_WS_RE_CONNECT = 2103;
//		public static final int CLIENT_CALL_MSG_SEND_REQUEST = 2104;
//		public static final int CLIENT_IN_CALL = 2105;
//		public static final int CLIENT_OUT_CALL = 2106;		
//		public static final int CLIENT_NOTIFICATION_ACK = 2107;
//		public static final int CLIENT_ECHO = 2199;
//	}
	
	// for retail mode
	public static final class RetailMode {
		private RetailMode() {}
		public static final int CLIENT_RETAIL_MODE_STATUS_UPDATE = 2200;
		public static final int CLIENT_RETAIL_MODE_USER_STAY_INFO = 2201;
		public static final int CLIENT_RETAIL_MODE_USER_STAY_INFO_RSP = 2202;
		public static final int CLIENT_RETAIL_MODE_CONFIG = 2203;
		public static final int CLIENT_RETAIL_MODE_NOISE_LEVEL = 2204;
		public static final int CLIENT_RETAIL_MODE_UPDATE_SMART_RECORD = 2205;
		public static final int CLIENT_RETAIL_MODE_REPORT_STAT = 2206;


		public static final String KEY_RETAIL_MODE_STATUS = "keyRetailModeStatus";
	    public static final String KEY_RETAIL_STATUS_DEVICE_SN = "keyDeviceSN";
	    public static final String KEY_RETAIL_MODE_LONGITUDE = "keyLongitude";
	    public static final String KEY_RETAIL_MODE_LATITUDE = "keyLatitude";
	    public static final String KEY_RETAIL_MODE_LOCALITY = "keyLocality";
	    public static final String KEY_RETAIL_MODE_USER_STAY_INFO = "keyUserStayInfo";
	    public static final String KEY_RETAIL_MODE_USER_STAY_INFO_RESULT = "keyUserStayInfoResult";

		public static class RTMMsgBase {
			public int type = 0;
		}
		
		public static class RetailModeReportData extends RTMMsgBase{
			public int status;
		}

		public static class HeartBeat extends RTMMsgBase{
			public int netType;
			public int strength;
		}
		
		public static class UserStayInfoMsg extends RTMMsgBase {
			public List<Msg.RetailMode.UserStayInfo> list;
		}
		
		public static class RetailModeResponse {
			public int status;
			public int type;
			public String ack;
		}
		
		public static class RetailModeLocationMsg extends RTMMsgBase {
			public double longitude;
			public double latitude;
			public String locality;
		}

	    public static class UserStayInfo {
			public long beginTime;
			public long endTime;
			public int maleNum;
			public int femaleNum;
			public int genderNotSure;
			public int oldGuyNum;
			public int youngNum;
			public int ageNotSure;
			public long stayTimeInSecond;  // in second
			public int totalNum;		
		}
	    
	    public static class SystemVolume extends RTMMsgBase{
	    	public String deviceSn;
	    	public int 	voiceVolume;
			public int seq;

			@Override
			public String toString() {
				return "SystemVolume{" +
						"deviceSn='" + deviceSn + '\'' +
						", voiceVolume=" + voiceVolume +
						", seq=" + seq +
						'}';
			}
		}
	}
	
	public static interface VultureService {
		public static final int MSG_CAMERA_TILT_EVENT		= 2300;
		public static final int MSG_FECC_EVENT				= 2301;
	}
}
