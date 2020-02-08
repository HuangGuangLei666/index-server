package com.pl.indexserver.untils.jiguang.common;

import cn.jiguang.common.ServiceHelper;
import cn.jiguang.common.connection.HttpProxy;
import cn.jiguang.common.connection.IHttpClient;
import cn.jiguang.common.connection.NativeHttpClient;
import cn.jiguang.common.connection.NettyHttpClient;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jiguang.common.resp.ResponseWrapper;
import cn.jiguang.common.utils.Preconditions;
import cn.jiguang.common.utils.StringUtils;
import com.google.gson.JsonObject;
import com.pl.indexserver.untils.jiguang.SendSMSResult;
import com.pl.indexserver.untils.jiguang.ValidSMSResult;
import com.pl.indexserver.untils.jiguang.account.AccountBalanceResult;
import com.pl.indexserver.untils.jiguang.account.AppBalanceResult;
import com.pl.indexserver.untils.jiguang.common.model.BatchSMSPayload;
import com.pl.indexserver.untils.jiguang.common.model.BatchSMSResult;
import com.pl.indexserver.untils.jiguang.common.model.SMSPayload;
import com.pl.indexserver.untils.jiguang.schedule.model.ScheduleResult;
import com.pl.indexserver.untils.jiguang.schedule.model.ScheduleSMSPayload;
import com.pl.indexserver.untils.jiguang.schedule.model.ScheduleSMSResult;
import com.pl.indexserver.untils.jiguang.template.SendTempSMSResult;
import com.pl.indexserver.untils.jiguang.template.TempSMSResult;
import com.pl.indexserver.untils.jiguang.template.TemplatePayload;

import java.util.regex.Pattern;

public class SMSClient {

	private static String SMS_CODE = "code";
	private static String PHONENUMBER = "phonenumber";

	private String _baseUrl;
	private String _smsCodePath;
	private String _validPath;
	private String _voiceCodePath;
	private String _shortMsgPath;
	private String _schedulePath;
    private String _accountPath;
    private String _tempMsgPath;
	private IHttpClient _httpClient;

	public SMSClient(String masterSecret, String appkey) {
        this(masterSecret, appkey, null, JSMSConfig.getInstance());
	}

	public SMSClient(String masterSecret, String appkey, HttpProxy proxy, JSMSConfig conf) {
		ServiceHelper.checkBasic(appkey, masterSecret);

		_baseUrl = (String) conf.get(JSMSConfig.API_HOST_NAME);
		_smsCodePath = (String) conf.get(JSMSConfig.CODE_PATH);
		_validPath = (String) conf.get(JSMSConfig.VALID_PATH);
		_voiceCodePath = (String) conf.get(JSMSConfig.VOICE_CODE_PATH);
		_shortMsgPath = (String) conf.get(JSMSConfig.SHORT_MESSAGE_PATH);
        _tempMsgPath = (String) conf.get(JSMSConfig.TEMPlATE_MESSAGE_PATH);
		_schedulePath = (String) conf.get(JSMSConfig.SCHEDULE_PATH);
        _accountPath = (String) conf.get(JSMSConfig.ACCOUNT_PATH);
		String authCode = ServiceHelper.getBasicAuthorization(appkey, masterSecret);
        this._httpClient = new NativeHttpClient(authCode, proxy, conf.getClientConfig());
	}

	/**
	 * Send SMS verification code to mobile
	 * @param payload include two parameters: mobile number and templete id. The second parameter is optional.
	 * @return return SendSMSResult which includes msg_id
	 * @throws APIConnectionException connection exception
	 * @throws APIRequestException request exception
	 */
	public SendSMSResult sendSMSCode(SMSPayload payload)
		throws APIConnectionException, APIRequestException {
		Preconditions.checkArgument(null != payload, "SMS payload should not be null");

		ResponseWrapper response = _httpClient.sendPost(_baseUrl + _smsCodePath, payload.toString());
		return SendSMSResult.fromResponse(response, SendSMSResult.class);
	}

	/**
	 * Send SMS verification code to server, to verify if the code valid
	 * @param msgId The message id of the verification code
	 * @param code Verification code
	 * @return return ValidSMSResult includes is_valid
	 * @throws APIConnectionException connection exception
	 * @throws APIRequestException request exception
	 */
	public ValidSMSResult sendValidSMSCode(String msgId, String code)
		throws APIConnectionException, APIRequestException {
		Preconditions.checkArgument(null != msgId, "Message id should not be null");
		Pattern codePattern = Pattern.compile("^[0-9]{6}");
		Preconditions.checkArgument(codePattern.matcher(code).matches(), "The verification code shoude be consist of six number");
		JsonObject json = new JsonObject();
		json.addProperty(SMS_CODE, code);

		ResponseWrapper response = _httpClient.sendPost(_baseUrl + _smsCodePath + "/" + msgId + _validPath, json.toString());
		return ValidSMSResult.fromResponse(response, ValidSMSResult.class);
	}

	/**
	 * Send voice SMS verification code to mobile
	 * @param payload payload includes two parameters: mobile number and ttl(time to live),
	 *                the second one is optional(if miss ttl, will use default value 60 seconds).
	 * @return return SendSMSResult which includes msg_id
	 * @throws APIConnectionException connection exception
	 * @throws APIRequestException request exception
	 */
	public SendSMSResult sendVoiceSMSCode(SMSPayload payload)
		throws APIConnectionException, APIRequestException {
		Preconditions.checkArgument(null != payload, "SMS payload should not be null");

		ResponseWrapper response = _httpClient.sendPost(_baseUrl + _voiceCodePath, payload.toString());
		return SendSMSResult.fromResponse(response, SendSMSResult.class);
	}

	/**
	 * Send template SMS to mobile
	 * @param payload payload includes mobile, temp_id and temp_para, the temp_para is a map,
	 *                which's key is what you had set in jiguang portal
	 * @return return SendSMSResult which includes msg_id
	 * @throws APIConnectionException  connection exception
	 * @throws APIRequestException request exception
	 */
	public SendSMSResult sendTemplateSMS(SMSPayload payload)
			throws APIConnectionException, APIRequestException {
		System.out.println(payload.toString());
		Preconditions.checkArgument(null != payload, "SMS payload should not be null");

//		JsonObject json = new JsonObject();
//		json.addProperty(PHONENUMBER, phone);

		ResponseWrapper response = _httpClient.sendPost(_baseUrl + _shortMsgPath, payload.toString());
		return SendSMSResult.fromResponse(response, SendSMSResult.class);
	}

    /**
     * Send a batch of template SMS
     * @param payload BatchSMSPayload
     * @return BatchSMSResult
     * @throws APIConnectionException connect exception
     * @throws APIRequestException request exception
     */
	public BatchSMSResult sendBatchTemplateSMS(BatchSMSPayload payload)
            throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != payload, "BatchSMSPayload should not be null");
        ResponseWrapper responseWrapper = _httpClient.sendPost(_baseUrl + _shortMsgPath + "/batch", payload.toString());
        return BatchSMSResult.fromResponse(responseWrapper, BatchSMSResult.class);
    }

	public void setHttpClient(IHttpClient client) {
		this._httpClient = client;
	}

	// 如果使用 NettyHttpClient，在发送请求后需要手动调用 close 方法
	public void close() {
		if (_httpClient != null && _httpClient instanceof NettyHttpClient) {
			((NettyHttpClient) _httpClient).close();
		}
	}

    /**
     * Submit a mission that sending a template SMS with pointed schedule
     * @param payload ScheduleSMSPayload
     * @return ScheduleResult which includes schedule_id
     * @throws APIConnectionException connect exception
     * @throws APIRequestException request exception
     */
	public ScheduleResult sendScheduleSMS(ScheduleSMSPayload payload) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != payload, "Schedule SMS payload should not be null");
		Preconditions.checkArgument(null != payload.getMobile(), "Mobile should not be null");
        Preconditions.checkArgument(StringUtils.isMobileNumber(payload.getMobile()), "Invalid mobile number");
        ResponseWrapper responseWrapper = _httpClient.sendPost(_baseUrl + _schedulePath, payload.toString());
        return ScheduleResult.fromResponse(responseWrapper, ScheduleResult.class);
    }

    /**
     * Modify SMS with schedule
     * @param payload ScheduleSMSPayload
     * @param scheduleId id
     * @return ScheduleResult which includes schedule_id
     * @throws APIConnectionException connect exception
     * @throws APIRequestException request exception
     */
    public ScheduleResult updateScheduleSMS(ScheduleSMSPayload payload, String scheduleId)
            throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != payload, "Schedule SMS payload should not be null");
        Preconditions.checkArgument(null != scheduleId, "Schedule id should not be null");
        Preconditions.checkArgument(null != payload.getMobile(), "Mobile should not be null");
        Preconditions.checkArgument(StringUtils.isMobileNumber(payload.getMobile()), "Invalid mobile number");
        ResponseWrapper responseWrapper = _httpClient.sendPut(_baseUrl + _schedulePath + "/" + scheduleId, payload.toString());
        return ScheduleResult.fromResponse(responseWrapper, ScheduleResult.class);
    }

    /**
     * Submit a mission that sending a batch of SMS with schedule
     * @param payload Payload should include sendTime and recipients
     * @return BatchSMSResult
     * @throws APIConnectionException connect exception
     * @throws APIRequestException request exception
     */
    public BatchSMSResult sendBatchScheduleSMS(ScheduleSMSPayload payload)
            throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != payload, "Schedule SMS payload should not be null");
        Preconditions.checkArgument(null != payload.getRecipients(), "Recipients should not be null");
        ResponseWrapper responseWrapper = _httpClient.sendPost(_baseUrl + _schedulePath + "/batch", payload.toString());
        return BatchSMSResult.fromResponse(responseWrapper, BatchSMSResult.class);
    }

    /**
     * Update batch of SMS with schedule
     * @param payload ScheduleSMSPayload
     * @param scheduleId id
     * @return BatchSMSResult
     * @throws APIConnectionException connection exception
     * @throws APIRequestException request exception
     */
    public BatchSMSResult updateBatchScheduleSMS(ScheduleSMSPayload payload, String scheduleId)
            throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != payload, "Schedule SMS payload should not be null");
        Preconditions.checkArgument(null != payload.getRecipients(), "Recipients should not be null");
        Preconditions.checkArgument(null != scheduleId, "Schedule id should not be null");
        ResponseWrapper responseWrapper = _httpClient.sendPut(_baseUrl + _schedulePath + "/batch/" + scheduleId, payload.toString());
        return BatchSMSResult.fromResponse(responseWrapper, BatchSMSResult.class);
    }

    /**
     * Get schedule SMS by scheduleId
     * @param scheduleId id
     * @return ScheduleSMSResult
     * @throws APIConnectionException connection exception
     * @throws APIRequestException request exception
     */
    public ScheduleSMSResult getScheduleSMS(String scheduleId) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != scheduleId, "Schedule id should not be null");
        ResponseWrapper responseWrapper = _httpClient.sendGet(_baseUrl + _schedulePath + "/" + scheduleId);
        return ScheduleSMSResult.fromResponse(responseWrapper, ScheduleSMSResult.class);
    }

    /**
     * Delete schedule SMS by scheduleId
     * @param scheduleId id
     * @return No content
     * @throws APIConnectionException connect exception
     * @throws APIRequestException request exception
     */
    public ResponseWrapper deleteScheduleSMS(String scheduleId) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != scheduleId, "Schedule id should not be null");
        return _httpClient.sendDelete(_baseUrl + _schedulePath + "/" + scheduleId);
    }

    /**
     * Get account's SMS balance
     * @return AccountBalanceResult
     * @throws APIConnectionException connect exception
     * @throws APIRequestException request exception
     */
    public AccountBalanceResult getSMSBalance() throws APIConnectionException, APIRequestException {
        ResponseWrapper responseWrapper = _httpClient.sendGet(_baseUrl + _accountPath + "/dev");
        return AccountBalanceResult.fromResponse(responseWrapper, AccountBalanceResult.class);
    }

    /**
     * Get app's SMS balance of an account
     * @return AppBalanceResult
     * @throws APIConnectionException connect exception
     * @throws APIRequestException request exception
     */
    public AppBalanceResult getAppSMSBalance() throws APIConnectionException, APIRequestException {
        ResponseWrapper responseWrapper = _httpClient.sendGet(_baseUrl + _accountPath + "/app");
        return AppBalanceResult.fromResponse(responseWrapper, AppBalanceResult.class);
    }

    //===============      Template API     =================

    /**
     * Create template sms.
     * @param payload {@link TemplatePayload }
     * @return {@link SendTempSMSResult }, include temp_id
     * @throws APIConnectionException connect exception
     * @throws APIRequestException request exception
     */
	public SendTempSMSResult createTemplate(TemplatePayload payload) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != payload, "Template payload should not be null");
		ResponseWrapper responseWrapper = _httpClient.sendPost(_baseUrl + _tempMsgPath, payload.toString());
        return SendTempSMSResult.fromResponse(responseWrapper, SendTempSMSResult.class);
	}

    /**
     * update template sms. Template can be modified ONLY when status is not approved
     * @param payload {@link TemplatePayload }
     * @param tempId template id
     * @return {@link SendTempSMSResult }, include temp_id
     * @throws APIConnectionException connect exception
     * @throws APIRequestException request exception
     */
	public SendTempSMSResult updateTemplate(TemplatePayload payload, int tempId) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != payload, "Template payload should not be null");
		Preconditions.checkArgument(tempId > 0, "temp id is invalid");
        ResponseWrapper responseWrapper = _httpClient.sendPut(_baseUrl + _tempMsgPath + "/" + tempId, payload.toString());
        return SendTempSMSResult.fromResponse(responseWrapper, SendTempSMSResult.class);
    }

    /**
     * check template by id
     * @param tempId necessary
     * @return {@link TempSMSResult}
     * @throws APIConnectionException connect exception
     * @throws APIRequestException request exception
     */
    public TempSMSResult checkTemplate(int tempId) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(tempId > 0, "temp id is invalid");
        ResponseWrapper responseWrapper = _httpClient.sendGet(_baseUrl + _tempMsgPath + "/" + tempId);
        return TempSMSResult.fromResponse(responseWrapper, TempSMSResult.class);
    }

    /**
     * Delete template by id
     * @param tempId necessary
     * @return No content
     * @throws APIConnectionException connect exception
     * @throws APIRequestException request exception
     */
    public ResponseWrapper deleteTemplate(int tempId) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(tempId > 0, "temp id is invalid");
        return _httpClient.sendDelete(_baseUrl + _tempMsgPath + "/" + tempId);
    }



}
