package com.pl.indexserver.web;

import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.model.BusinessFocusDto;
import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.model.TBusinessModelDto;
import com.pl.indexserver.service.*;
import com.pl.indexserver.untils.CommonConstant;
import com.pl.indexserver.untils.GetUid;
import com.pl.indexserver.untils.RedisClient;
import com.pl.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/busiManagement/flowConfig")
public class BusinessContorller {

	private static final Logger logger = LoggerFactory.getLogger(BusinessContorller.class);

	@Autowired
	private TBusinessService tBusinessService;
	@Autowired
	private UserOperateRecordService userOperateRecordService;
	@Autowired
	private QCommonCraftService qCommonCraftService;
	@Autowired
	private TBusinessFocusService tBusinessFocusService;

	@Autowired
	private RedisClient redisClient;

	@Value("${t_business.default.controll_addr}")
	private String controllAddr;

	@Autowired
	private WorkFlowService workFlowService;

	@Autowired
	private TWechatService tWechatService;

	/**
	 * @api {GET} /busiManagement/flowConfig/index 获取当前客户的智库方案列表
	 * @apiName index
	 * @apiGroup BusinessContorller
	 * @apiSuccess {String} code 0:获取成功 1:获取失败
	 * @apiSuccess {String} content 返回任务列表
	 * @apiSuccess {String} errorMsg 返回错误说明
	 * @apiSuccessExample {json} 成功返回示例 
	 * { "code": 0, "content": { "id":
	 *                    "主键id", "name": "智库名称", "status": "状态码", "remark":
	 *                    "备注", "modifyDate": "最近更新时间", "speechcraftNum": "话术量",
	 *                    "recordNum": "已录音量" }, "token": null, "errorMsg": null }
	 * @apiVersion 1.0.0
	 */
	@RequestMapping("/index")
	public ReturnMsg index(HttpServletRequest request) {
		ReturnMsg returnMsg = new ReturnMsg();
		try {
			TmUser user = GetUid.getUID(request, redisClient);
			Long companyId = user.getCompanyId();
			List<TBusinessModelDto> list = tBusinessService.getTBusinessDescribeListByCompanyId(companyId.toString());
			returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
			returnMsg.setContent(list == null ? new Object[0] : list);
		} catch (Exception e) {
			returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
			logger.error("获取智库方案列表异常:", e);
		}
		return returnMsg;
	}

	/**
	 * @api {GET} /busiManagement/flowConfig/detail 获取智库明细信息
	 * @apiName index
	 * @apiGroup BusinessContorller
	 * @apiParam {Long} businessId 智库标识
	 * @apiSuccess {String} code 0:获取成功 1:获取失败
	 * @apiSuccess {String} content 返回任务列表
	 * @apiSuccess {String} errorMsg 返回错误说明
	 * @apiSuccessExample {json} 成功返回示例
	 * {
	 *   "code": 0,
	 *   "content": {
	 *     "id": 456795,
	 *     "name": "测试方案",
	 *     "status": "",
	 *     "remark": "测试临时方案",
	 *     "companyId": 18,
	 *     "modifyDate": "",
	 *     "createDate": "",
	 *     "speechcraftNum": 0,
	 *     "recordNum": 0,
	 *     "craftConfig": {
	 *       "onceUnidentified": 0,
	 *       "multiUnidentified": 0,
	 *       "onceUnresponsive": 0,
	 *       "multiUnresponsive": 0,
	 *       "commonCraftConfigList": [
	 *         {
	 *           "id": 87,
	 *           "name": "公司名称",
	 *           "question": "0",
	 *           "enabledStatus": 0
	 *         }
	 *       ]
	 *     }
	 *   },
	 *   "token": null,
	 *   "errorMsg": null
	 * }
	 * @apiVersion 1.0.0
	 */
	@RequestMapping("/detail")
	public ReturnMsg detail(Long businessId, HttpServletRequest request) {
		ReturnMsg returnMsg = new ReturnMsg();
		try {
			TmUser user = GetUid.getUID(request, redisClient);
			TBusinessModelDto tBusinessModelDto =tBusinessService.selectById(businessId);
			tBusinessModelDto.setCraftConfig(qCommonCraftService.selectCommonCraftConfig(user.getCompanyId(), tBusinessModelDto.getId()));
			returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
			returnMsg.setContent(tBusinessModelDto);
		} catch (Exception e) {
			returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
			logger.error("获取智库方案列表异常:", e);
		}
		return returnMsg;
	}

	/**
	 * @api {Post} /busiManagement/flowConfig/business/save
	 *      保存(新增/修改)一条当前客户的智库方案数据
	 * @apiName create
	 * @apiGroup BusinessContorller
	 * @apiParam {Long} id 主键
	 * @apiParam {String} name 智库名称
	 * @apiParam {String} remark 智库介绍
	 * @apiSuccess {String} code 0:获取成功 1:获取失败
	 * @apiSuccess {String} content 返回任务列表
	 * @apiSuccess {String} errorMsg 返回错误说明
	 * @apiSuccessExample {json} 成功返回示例 
	 * { "code": 0, "content": "", "token":  null, "errorMsg": null }
	 * @apiVersion 1.0.0
	 */
	@PostMapping("/business/save")
	public ReturnMsg createBusiness(HttpServletRequest request, @RequestParam("id") String id, @RequestParam("name") String name,
									@RequestParam("remark") String remark,@RequestParam("templateType")String templateType) {
		ReturnMsg returnMsg = new ReturnMsg();
		String operation = null;
		try {
			TmUser user = GetUid.getUID(request, redisClient);
			if ( null == user){
				returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
				returnMsg.setErrorMsg("登录检查错误！");
				return returnMsg;
			}
			int i = 0;
			UserOperateRecord userOperateRecord = new UserOperateRecord();
			Long companyId = user.getCompanyId();
			TBusiness tBusiness = new TBusiness();
			tBusiness.setName(name);
			tBusiness.setRemark(remark);
			tBusiness.setCompanyId(companyId);
			tBusiness.setTemplateType(templateType);
			tBusiness.setId(Long.valueOf(id));
			if (id.equals("0")) {
				tBusiness.setTemplateName("");
				tBusiness.setCreateDate(new Date());
				tBusiness.setStatus(CommonConstant.STATUS_0);
				tBusiness.setAlgorithmAddr("");
				tBusiness.setControllAddr(controllAddr);
				i = tBusinessService.insertSelective(tBusiness);
				userOperateRecord.setOperateName(CommonConstant.CREATE_BUSINESS);
				operation = CommonConstant.CREATE_BUSINESS;
				workFlowService.workFlowCreate(user, user.getUserid(), null, 0L,"{\"title\":\"newFlow\"}", companyId, tBusiness.getId(), "主流程", null);
			} else {
				tBusinessService.updateByPrimaryKeySelective(tBusiness);
				userOperateRecord.setOperateName(CommonConstant.MODIFY_BUSINESS);
				operation = CommonConstant.MODIFY_BUSINESS;
			}
			List<TBusinessModelDto> list = tBusinessService.getTBusinessDescribeListByCompanyId(companyId.toString());
			// 插入操作日志
			userOperateRecord.setUserId(user.getUserid());
			userOperateRecord.setCompanyId(companyId);
			userOperateRecord.setObjectType(CommonConstant.FLOWCONFIG);
			userOperateRecord.setObject(CommonConstant.INITIAL_BUSINESSID);
			userOperateRecord.setOperateId("");
			StringBuilder uRemark = new StringBuilder(JSONObject.toJSONString(tBusiness));
			if (i > 0) {
				uRemark.append(CommonConstant.OPERATION_SUCCEED);
			} else {
				uRemark.append(CommonConstant.OPERATION_DEFEATED);
			}
			userOperateRecord.setRemark(uRemark.toString());
			userOperateRecord.setCreateDate(new Date());
			userOperateRecordService.insert(userOperateRecord);

			returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
			returnMsg.setContent(null == list ? new Object[0] : list);
		} catch (Exception e) {
			returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
			logger.error(operation + "异常:", e);
		}
		return returnMsg;
	}

	/**
	 * @api {GET} /busiManagement/flowConfig//knowledgeDB/release 更新问答库到算法
	 * @apiName release
	 * @apiGroup BusinessContorller
	 * @apiParam {Long} id id值
	 * @apiSuccess {String} code 0:获取成功 1:获取失败
	 * @apiSuccess {String} content 返回任务列表
	 * @apiSuccess {String} errorMsg 返回错误说明
	 * @apiSuccessExample {json} 成功返回示例
	 * {"code": 0, "content": "", "token":null, "errorMsg": null}
	 * @apiVersion 1.0.0
	 */
	@RequestMapping("/knowledgeDB/release")
	public ReturnMsg release(HttpServletRequest request, Long businessId) {
		ReturnMsg returnMsg = new ReturnMsg();
		try {
			TmUser user = GetUid.getUID(request, redisClient);
			Long companyId = user.getCompanyId();
			Map<String, Long> map = new HashMap<>();
			map.put("companyId", companyId);
			map.put("businessId", businessId);
			String s = JSONObject.toJSONString(map);
			redisClient.lpush("RecordUpdateQuene", s);
			returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
		} catch (Exception e) {
			returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
			e.printStackTrace();
		}
		return returnMsg;
	}

	@RequestMapping("/business/clone")
	public ReturnMsg clone(HttpServletRequest request, Long companyId, Long businessId) {
		ReturnMsg returnMsg = new ReturnMsg();
		Long t = System.currentTimeMillis();
		try {
			TmUser user = GetUid.getUID(request, redisClient);
			tBusinessService.clone(user, companyId, businessId, user.getCompanyId());
			returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
		} catch (Exception e) {
			returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
			logger.info("复制智库出现异常: ",e);
		}
		logger.info("====costime=====777===" + (System.currentTimeMillis() - t) );
		return returnMsg;
	}

	/**
	 * @api {GET} /busiManagement/flowConfig/business/focus_list 智库关注点列表
	 * @apiName focus_list
	 * @apiGroup BusinessContorller
	 * @apiParam {Long} businessId 智库id
	 * @apiParam {String} name 关注点名称
	 * @apiSuccess {String} code 0:获取成功 1:获取失败
	 * @apiSuccess {String} content 返回任务列表
	 * @apiSuccess {String} errorMsg 返回错误说明
	 * @apiSuccessExample {json} 成功返回示例
	 * {
	 *   "code": 0,
	 *   "content":
	 *     {
	 *       "id": 1,
	 *       "name": "测试哈哈哈"
	 *     }  ,
	 *   "token": null,
	 *   "errorMsg": null
	 * }
	 * @apiVersion 1.0.0
	 */
	@RequestMapping("/business/focus_list")
	public ReturnMsg focusList(HttpServletRequest request, Long businessId, String name) {
		ReturnMsg returnMsg = new ReturnMsg();
		try {
			TmUser user = GetUid.getUID(request, redisClient);
			if (StringUtils.isEmpty(name)) {
				returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
				returnMsg.setContent(new ArrayList<>());
			} else {
				List<BusinessFocusDto> businessFocusDtos = tBusinessFocusService.getBusinessFocusList(user.getCompanyId(), businessId, name);
				returnMsg.setContent(businessFocusDtos);
				returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
			}
		} catch (Exception e) {
			returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
			e.printStackTrace();
		}
		return returnMsg;
	}


	/**
	 * hgl
	 * 微信机器人问答带流程
	 * @param wechatRecord
	 * @return
	 * {
	 *	"code": 0,
	 *	"contentResp": "喂您好（停顿1秒），诶您好!我这边是专业办理银行贷款的，现在推出一款低利息的信用贷和抵押贷，请问您有资金需求吗？"
	 * }
	 */
	@RequestMapping("/robot")
	public WechatRespon robot(@RequestBody TWechatRecord wechatRecord){
		logger.info("%%%%%%%%%%%%%businessId={}",wechatRecord.getBusinessId());
		//查当前节点是否为空
		return tWechatService.selectWechatRecordByWechatIds(wechatRecord);
	}

}
