package com.zorro.testcode;

public class RetailModeStat {

	/**
	 * 在无法处理ping时使用此类型代替
	 */
	public static final int RTM_PING = 0;

	// *
	// ===================================请求应答信息===============================
	/**
	 * 客户端连上来后发给服务器的第一个消息，用于请求默认的一些配置
	 */
	public static final int RTM_GET_CONFIG = 1;

	/**
	 * 服务器返回给客户端的默认配置
	 */
	public static final int RTM_RES_CONFIG = 2;

	// *
	// =================================推送信息======================================

	/**
	 * 服务器基础推送信息，包括retailMode开关，轮播视频地址，小鱼默认音量三个
	 */
	public static final int RTM_PUSH_BASIC = 3;

	// ===================状态统计信息（单向消息, 但成对出现，一个开始状态，一个结束状态）====================
	// 101 -> 150

	public static final int RTM_STAT_SCENARIO = 100;

	// * ============================数据信息（单向消息）=================================

	/**
	 * WIFI,信号强度判断
	 */
	public static final int RTM_DATA_SIGNAL_INFO = 151;

	/**
	 * 小鱼的实时位置，精确到街道
	 */
	public static final int RTM_DATA_LOCATION = 152;

	/**
	 * 家庭圈
	 */
	public static final int RTM_DATA_FAMILY_GROUP = 153;

	/**
	 * 驻留时长， 男女，年龄识别
	 */
	public static final int RTM_DATA_USER_STAY_INFO = 154;

	/**
	 * 上报客户端音量信息
	 */
	public static final int RTM_REPORT_VOLUMN	= 155;

	public static class RTMStatScenarioValue {
		/**
		 * 视频开始
		 */
		public static final int RTM_VALUE_VIDEO_BEGIN = 101;

		/**
		 * 视频结束
		 */
		public static final int RTM_VALUE_VIDEO_END = 102;

		/**
		 * 通话开始
		 */
		public static final int RTM_VALUE_CALL_BEGIN = 103;

		/**
		 * 通话结束
		 */
		public static final int RTM_VALUE_CALL_END = 104;

		/**
		 * 点击home
		 */
		public static final int RTM_VALUE_HOME_KEY_SINGLE = 105;

		/**
		 * 双击 home
		 */
		public static final int RTM_VALUE_HOME_KEY_DOUBLE = 117;

		/**
		 * 预设联系人拨打开始
		 */
		public static final int RTM_VALUE_PRE_CONTACT_CALL_BEGIN = 109;

		/**
		 * 预设联系人拨打开始
		 */
		public static final int RTM_VALUE_PRE_CONTACT_CALL_END = 110;

		/**
		 * 活动点击次数 116
		 */
		public static final int RTM_VALUE_MARK_CLICK = 116;

		/**
		 * 视频文件夹点击次数
		 */
		public static final int RTM_VALUE_VIDEO_FOLDER = 118;

		/**
		 * 视频点击次数
		 */
		public static final int RTM_VALUE_VIDEO_CLICK = 119;

		/**
		 * 视频下载失败
		 */
		public static final int RTM_VALUE_VIDEO_FAIL_DOWNLOAD = 120;

		/**
		 * 视频下载成功
		 */
		public static final int RTM_VALUE_VIDEO_SUCCESS_DOWNLOAD = 121;
	}
}
