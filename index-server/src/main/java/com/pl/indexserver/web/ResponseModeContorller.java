package com.pl.indexserver.web;

import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.model.ResponseModeDto;
import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.model.redisdto.AlgorithmDataSynDto;
import com.pl.indexserver.service.ResponseModeService;
import com.pl.indexserver.service.UserOperateRecordService;
import com.pl.indexserver.untils.CommonConstant;
import com.pl.indexserver.untils.GetUid;
import com.pl.indexserver.untils.RedisClient;
import com.pl.model.TmUser;
import com.pl.model.UserOperateRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/busiManagement/flowConfig/responseMode")
public class ResponseModeContorller {

    private static final Logger logger = LoggerFactory.getLogger(ResponseModeContorller.class);

    @Autowired
    private ResponseModeService responseModeService;
    @Autowired
    private UserOperateRecordService userOperateRecordService;
    @Autowired
    private RedisClient redisClient;

    /**
     * @api {GET} /busiManagement/flowConfig/responseMode/query 获取当前客户的响应方式列表
     * @apiName responseMode/query
     * @apiGroup ResponseModeContorller
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": [
     * {
     * "id": "主键id",
     * "name": "响应类型",
     * "ruleType": "触发条件",
     * "keyNum": "关键字数量",
     * "modifyDate": "更新时间",
     * "flag": "系统还是用户定义"
     * }
     * ],
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @RequestMapping("/query")
    public ReturnMsg query(HttpServletRequest request) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            TmUser user = GetUid.getUID(request, redisClient);
            Long companyId = user.getCompanyId();
            List<ResponseModeDto> list = responseModeService.selectByCompanyId(companyId.toString());
            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            returnMsg.setContent(list == null ? new ArrayList<>() : list);
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            e.printStackTrace();
            logger.error("查询响应方式列表数据出错:", e);
        }
        return returnMsg;
    }

    /**
     * @api {GET} /busiManagement/flowConfig/responseMode/queryDetail 获取指定的响应方式
     * @apiName responseMode/queryDetail
     * @apiGroup ResponseModeContorller
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": [
     * {
     * "id": "主键id",
     * "name": "类型名称",
     * "keyWord": "关键词",
     * "content": "触发话术"
     * }
     * ],
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @RequestMapping("/queryDetail")
    public ReturnMsg queryDetail(@RequestParam("id") String id) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            ResponseModeDto responseModeDto = responseModeService.selectByPrimaryKey(id);
            if (null == responseModeDto) {
                throw new NullPointerException("查无数据！");
            }
            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            returnMsg.setContent(responseModeDto);
        } catch (NullPointerException e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            returnMsg.setErrorMsg("数据异常，请刷新页面！");
            logger.error("查询指定响应方式列表数据出错:", e.getMessage());
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            logger.error("查询指定响应方式列表数据出错:", e);
        }
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/flowConfig/responseMode/delete 删除当前客户指定的响应方式
     * @apiName responseMode/delete
     * @apiGroup ResponseModeContorller
     * @apiParam{Long} id 响应方式的id值
     * @apiSuccess {String} code 0:删除成功 1:删除失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": "",
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @PostMapping("/delete")
    public ReturnMsg deleteById(HttpServletRequest request, @RequestParam("id") String id) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
        	TmUser user = GetUid.getUID(request, redisClient);
            Boolean flag = responseModeService.selectWorkFlowByPrimaryKey(id);
            if (flag) {
            	//重新获取列表数据以返回
                List<ResponseModeDto> list = responseModeService.selectByCompanyId(user.getCompanyId().toString());
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("该数据被流程绑定，无法删除！");
                returnMsg.setContent(null == list ? new Object[0] : list);
                return returnMsg;
            }
            ResponseModeDto oldObj = responseModeService.selectByPrimaryKey(id);
            int i = responseModeService.deleteByPrimaryKey(id);
            if (i > 0) {
                responseModeService.pushSynAlgorithmDataToRedis(AlgorithmDataSynDto.OperationEnum.DELETE, oldObj.getId(), oldObj.getCompanyId(), oldObj.getBusinessId());
            }
            //插入操作日志
            UserOperateRecord userOperateRecord = new UserOperateRecord();
            userOperateRecord.setUserId(user.getUserid());
            userOperateRecord.setCompanyId(user.getCompanyId());
            userOperateRecord.setObjectType(CommonConstant.FLOWCONFIG);
            userOperateRecord.setObject(CommonConstant.INITIAL_BUSINESSID);
            userOperateRecord.setOperateId("");
            userOperateRecord.setOperateName(CommonConstant.DELETE_RESP);
            userOperateRecord.setCreateDate(new Date());
            StringBuilder remark = new StringBuilder(id);
            if (i > 0) {
                remark.append(CommonConstant.OPERATION_SUCCEED);
            } else {
                remark.append(CommonConstant.OPERATION_DEFEATED);
            }
            userOperateRecord.setRemark(remark.toString());
            userOperateRecordService.insert(userOperateRecord);
            //重新获取列表数据以返回
            List<ResponseModeDto> list = responseModeService.selectByCompanyId(user.getCompanyId().toString());
            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            returnMsg.setContent(null == list ? new ArrayList<>() : list);
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            logger.error("删除指定响应方式数据出错:", e);
        }
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/flowConfig/responseMode/create 新增一条响应方式
     * @apiName responseMode/save
     * @apiGroup ResponseModeContorller
     * @apiParam {ResponseModeDto} responseModeDto 响应方式模型数据
     * @apiParamExample {json} 响应方式新增输入
     * {
     * "id":"主键id"
     * "name":"类型名称",
     * "content":"触发话术",
     * "keyWord":"关键词"
     * }
     * @apiSuccess {String} code 0:保存成功 1:保存失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": "",
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @PostMapping("/create")
    public ReturnMsg create(HttpServletRequest request, ResponseModeDto responseModeDto) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            TmUser user = GetUid.getUID(request, redisClient);
            responseModeDto.setUid(user.getUserid());
            //新增操作
            responseModeDto.setCompanyId(user.getCompanyId());
            int flag = responseModeService.insert(responseModeDto);
            if (flag > 0) {
                responseModeService.pushSynAlgorithmDataToRedis(AlgorithmDataSynDto.OperationEnum.INSERT, responseModeDto.getId(), responseModeDto.getCompanyId(), responseModeDto.getBusinessId());
            }
            //插入操作日志
            UserOperateRecord userOperateRecord = new UserOperateRecord();
            userOperateRecord.setOperateName(CommonConstant.CREATE_RESP);
            userOperateRecord.setUserId(user.getUserid());
            userOperateRecord.setCompanyId(user.getCompanyId());
            userOperateRecord.setObjectType(CommonConstant.FLOWCONFIG);
            userOperateRecord.setObject(CommonConstant.INITIAL_BUSINESSID);
            userOperateRecord.setOperateId("");
            StringBuilder remark = new StringBuilder(JSONObject.toJSONString(responseModeDto));
            if (flag > 0) {
                remark.append(CommonConstant.OPERATION_SUCCEED);
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            } else {
                remark.append(CommonConstant.OPERATION_DEFEATED);
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            }
            userOperateRecord.setRemark(remark.toString());
            userOperateRecord.setCreateDate(new Date());
            userOperateRecordService.insert(userOperateRecord);
            //重新获取列表数据以返回
            List<ResponseModeDto> list = responseModeService.selectByCompanyId(user.getCompanyId().toString());
            returnMsg.setContent(null == list ? new ArrayList<>() : list);
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            logger.error("新增响应方式数据出错:", e);
        }
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/flowConfig/responseMode/modify 修改一条响应方式
     * @apiName responseMode/save
     * @apiGroup ResponseModeContorller
     * @apiParam {ResponseModeDto} responseModeDto 响应方式模型数据
     * @apiParamExample {json} 响应方式新增输入
     * {
     * "id":"主键id"
     * "name":"类型名称",
     * "content":"触发话术",
     * "keyWord":"关键词"
     * }
     * @apiSuccess {String} code 0:保存成功 1:保存失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": "",
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @PostMapping("/modify")
    public ReturnMsg modify(HttpServletRequest request, ResponseModeDto responseModeDto) {
    	ReturnMsg returnMsg = new ReturnMsg();
    	try {
    		TmUser user = GetUid.getUID(request, redisClient);
    		responseModeDto.setUid(user.getUserid());
			//修改操作
    		int flag = responseModeService.updateByPrimaryKeySelective(responseModeDto);
            if (flag > 0) {
                responseModeService.pushSynAlgorithmDataToRedis(AlgorithmDataSynDto.OperationEnum.UPDATE, responseModeDto.getId(), responseModeDto.getCompanyId(), responseModeDto.getBusinessId());
            }
            //插入操作日志
			UserOperateRecord userOperateRecord = new UserOperateRecord();
			userOperateRecord.setOperateName(CommonConstant.MODIFY_RESP);
    		userOperateRecord.setUserId(user.getUserid());
    		userOperateRecord.setCompanyId(user.getCompanyId());
    		userOperateRecord.setObjectType(CommonConstant.FLOWCONFIG);
    		userOperateRecord.setObject(CommonConstant.INITIAL_BUSINESSID);
    		userOperateRecord.setOperateId("");
    		StringBuilder remark = new StringBuilder(JSONObject.toJSONString(responseModeDto));
    		if (flag > 0) {
    			remark.append(CommonConstant.OPERATION_SUCCEED);
    			returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
    		} else {
    			remark.append(CommonConstant.OPERATION_DEFEATED);
    			returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
    		}
    		userOperateRecord.setRemark(remark.toString());
    		userOperateRecord.setCreateDate(new Date());
    		userOperateRecordService.insert(userOperateRecord);
    		//重新获取列表数据以返回
    		List<ResponseModeDto> list = responseModeService.selectByCompanyId(user.getCompanyId().toString());
    		returnMsg.setContent(null == list ? new ArrayList<>() : list);
    	} catch (Exception e) {
    		returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
    		logger.error("修改响应方式数据出错:", e);
    	}
    	return returnMsg;
    }

}
