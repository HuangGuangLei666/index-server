package com.pl.indexserver.untils;

public interface ExceptionContant {
	/** redis 通讯异常 */
	public interface RedisCommunicateException {
		/** 连接redis服务器异常 */
		public static final String CONNECTION_ERROR = "101";
		/** 操作redis服务器异常 */
		public static final String OPERATE_ERROR = "102";
		/** 参数异常 */
		public static final String ARGUMENTS_ERROR = "103";
		/**操作redis错误*/
		public static final String REDISOPERATE_ERROR = "104";
	}

	public interface SysException{
		/**未知错误*/
		public static final String UNKONW_ERROR = "201";
		/**参数不能为空*/
		public static final String PARAMS_ERROR = "202";
		/**无效的版本号*/
		public static final String VERSION_ERROR = "203";
		/**无效的token*/
		public static final String TOKEN_ERROR = "204";
		/**json解析异常*/
		public static final String JSON_ERROR = "205";
		/**OS错误*/
		public static final String OS_ERROR = "206";
		/***数据库错误**/
		public static final String UNKONW_ERROR_SQL = "207";
	}

	public interface CallTaskException{
		/**外呼任务不存在*/
		public static final String CALLTASK_NOTEXIST_ERROR = "1001";
	}


}
