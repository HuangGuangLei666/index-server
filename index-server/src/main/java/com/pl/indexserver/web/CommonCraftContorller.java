package com.pl.indexserver.web;


import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.model.ACommonCraftDto;
import com.pl.indexserver.model.CraftConfigDto;
import com.pl.indexserver.model.QCommonCraftDto;
import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.model.redisdto.AlgorithmDataSynDto;
import com.pl.indexserver.service.*;
import com.pl.indexserver.untils.CommonConstant;
import com.pl.indexserver.untils.GetUid;
import com.pl.indexserver.untils.OperateType;
import com.pl.indexserver.untils.RedisClient;
import com.pl.model.ACommonCraft;
import com.pl.model.QCommonCraft;
import com.pl.model.TBusinessConfig;
import com.pl.model.TmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/busiManagement/flowConfig/commonCraft")
public class CommonCraftContorller {

    private static final Logger logger = LoggerFactory.getLogger(CommonCraftContorller.class);

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private QCommonCraftService qCommonCraftService;
    @Autowired
    private ACommonCraftService aCommonCraftService;
    @Autowired
    private FileTransferService fileTransferService;
    @Autowired
    private UserOperateRecordService userOperateRecordService;
    @Autowired
    private TBusinessFocusService tBusinessFocusService;

    /**
     * @api {GET} /busiManagement/flowConfig/commonCraft/queryConfig 获取智库或通用话术配置
     * @apiName queryConfig
     * @apiGroup CommonCraftContorller
     * @apiParam {Long} businessId 智库id
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": {
     * "businessId": 456829,
     * "companyId": 2000,
     * "name": "测试2",
     * "remark": "",
     * "unidentifiedStatus": 2,
     * "unresponsiveStatus": 2,
     * "commonCraftConfigList": [
     * {
     * "id": 1,
     * "name": "公司名称",
     * "question": "22",
     * "enabledStatus": 1
     * }
     * ],
     * "focusConfig": {
     * "configType": "FOCUS",
     * "intentA": "",
     * "intentB": "",
     * "intentC": "",
     * "intentD": "",
     * "status": "USED"
     * },
     * "scoreConfig": {
     * "configType": "SCORE",
     * "intentA": "9",
     * "intentB": "5",
     * "intentC": "2",
     * "intentD": "1",
     * "status": "UNUSED"
     * },
     * "intentConfig": {
     * "configType": "INTENT",
     * "intentA": "{\"contition_config\":\"or\",\"call_time\":0,\"call_wheel_num\":0,\"intent_num\":0}",
     * "intentB": "{\"contition_config\":\"or\",\"call_time\":0,\"call_wheel_num\":0,\"intent_num\":0}",
     * "intentC": "{\"contition_config\":\"or\",\"call_time\":0,\"call_wheel_num\":0,\"intent_num\":0}",
     * "intentD": "{\"contition_config\":\"or\",\"call_time\":0,\"call_wheel_num\":0,\"intent_num\":0}",
     * "status": "UNUSED"
     * }
     * },
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @GetMapping("/queryConfig")
    public ReturnMsg queryConfig(Long businessId, HttpServletRequest request) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            TmUser user = GetUid.getUID(request, redisClient);
            CraftConfigDto configInfo = qCommonCraftService.selectCommonCraftConfig(user.getCompanyId(), businessId);
            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            returnMsg.setContent(configInfo);
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            logger.error("获取通用话术配置信息:", e);
        }
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/flowConfig/commonCraft/modifyConfig 修改通用话术配置
     * @apiName modifyConfig
     * @apiGroup CommonCraftContorller
     * @apiParam {CraftConfigDto} commonCraftConfigList 话术配置模型
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": null,
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @PostMapping("/modifyConfig")
    public ReturnMsg modifyConfig(@RequestBody CraftConfigDto craftConfig, HttpServletRequest request) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            TmUser user = GetUid.getUID(request, redisClient);
            if (TBusinessConfig.Status.UNUSED.getCode().equals(craftConfig.getFocusConfig().getStatus())
                    && TBusinessConfig.Status.UNUSED.getCode().equals(craftConfig.getScoreConfig().getStatus())
                    && TBusinessConfig.Status.UNUSED.getCode().equals(craftConfig.getIntentConfig().getStatus())) {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("必须启用一个意向条件");
            } else {
                qCommonCraftService.updateCommonCraftConfig(user, craftConfig);
                Long businessId = craftConfig.getBusinessId() == null ? 0L : craftConfig.getBusinessId();
                qCommonCraftService.pushSynAlgorithmDataToRedis(AlgorithmDataSynDto.OperationEnum.INSERT, null, craftConfig.getCompanyId(), businessId);
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            }
//            returnMsg.setContent();
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            logger.error("设置通用话术相关配置:", e);
        }
        return returnMsg;
    }


    /**
     * @api {GET} /busiManagement/flowConfig/commonCraft/query 获取当前客户的通用话术列表
     * @apiName query
     * @apiGroup CommonCraftContorller
     * @apiParam {Long} businessId 智库id
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": [
     * {
     * "id": "主键id",
     * "name": "话术标题",
     * "ruleType": "触发条件",
     * "keyNum": "关键词数量",
     * "action": "回答后处理",
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
    public ReturnMsg query(Long businessId, HttpServletRequest request) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            TmUser user = GetUid.getUID(request, redisClient);
            Long companyId = user.getCompanyId();
            List<QCommonCraftDto> list = qCommonCraftService.selectByCompanyIdAndBusinessId(companyId, businessId);
            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            returnMsg.setContent(list == null ? new Object[0] : list);
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            logger.error("获取通用话术列表异常:", e);
        }
        return returnMsg;
    }

    /**
     * @api {GET} /busiManagement/flowConfig/commonCraft/queryDetail 获取指定的通用话术详情
     * @apiName /commonCraft/queryDetail
     * @apiGroup CommonCraftContorller
     * @apiParam {String} id 指定查询的通用话术对象id
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": {
     * "id": "主键id",
     * "craftId": "话术标识",
     * "name": "话术标题",
     * "keyWord": "关键词",
     * "question": "触发话术",
     * "jump": "跳转流程",
     * "action": "回答后处理",
     * "msgtemplId": "短信模板（当为0时表示不发送）",
     * "score":"得分",
     * "aCommonCraftDtos": [
     * {
     * "id": "主键id",
     * "content": "回答内容",
     * "recordName": "录音文件名",
     * "recordState": "录音状态",
     * "recordDescribe": "录音描述",
     * "recordDetail": [
     * {
     * "content":"文本内容",
     * "recordFile":"录音文件名",
     * "recordDescribe":"录音描述(0:tts,1:手动上传)"
     * }
     * ]
     * }
     * ]
     * },
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @RequestMapping("/queryDetail")
    public ReturnMsg queryDetail(HttpServletRequest request, @RequestParam("id") Long id) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            //配合前端验证
            if (0 == id) {
                returnMsg.setContent(new QCommonCraftDto());
                return returnMsg;
            }
            QCommonCraftDto qCommonCraftDto = qCommonCraftService.selectDetailByPrimaryKey(id);
            qCommonCraftDto.setFoucs(tBusinessFocusService.convertFocusIdsToFocusNames(qCommonCraftDto.getCompanyId(), qCommonCraftDto.getBusinessId(), qCommonCraftDto.getFoucs()));
            if (qCommonCraftDto == null) {
                throw new NullPointerException();
            }
            returnMsg.setContent(qCommonCraftDto);
        } catch (NullPointerException e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            returnMsg.setContent(new QCommonCraftDto());
            logger.error("获取指定通用话术异常(查无数据):", e);
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            logger.error("获取指定通用话术异常:", e);
        }
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/flowConfig/commonCraft/delete 删除当前客户指定的通用话术
     * @apiName delete
     * @apiGroup CommonCraftContorller
     * @apiParam {Long} id 通用话术的id值
     * @apiSuccess {String} code 0:新增成功 1:新增失败
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
    public ReturnMsg deleteById(HttpServletRequest request, @RequestParam("id") Long id) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            QCommonCraft oldObj = qCommonCraftService.selectByPrimaryKey(id);
            Boolean flag = qCommonCraftService.deleteDetailByPrimaryKey(id);
            if (flag) {
                qCommonCraftService.pushSynAlgorithmDataToRedis(AlgorithmDataSynDto.OperationEnum.DELETE, oldObj.getId(), oldObj.getCompanyId(), oldObj.getBusinessId());
            }
            TmUser user = GetUid.getUID(request, redisClient);
            StringBuilder remark = new StringBuilder(id.toString());
            if (flag) {
                remark.append(CommonConstant.OPERATION_SUCCEED);
            } else {
                remark.append(CommonConstant.OPERATION_DEFEATED);
            }

            // 操作日志处理
            String opInfo = String.format("话术标题:%s", oldObj.getName());
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, CommonConstant.INITIAL_BUSINESSID, "commonCraft/delete",
                    CommonConstant.DELETE_COMMONCRAFT, remark.toString(), OperateType.DELETE, opInfo, null, null);
            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            logger.error("删除指定通用问答异常:", e);
        }
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/flowConfig/commonCraft/deleteContent 删除通用话术的一条回答数据
     * @apiName deleteContent
     * @apiGroup CommonCraftContorller
     * @apiParam {Long} id 通用话术的id值
     * @apiSuccess {String} code 0:新增成功 1:新增失败
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
    @PostMapping("/deleteContent")
    public ReturnMsg deleteContent(HttpServletRequest request, @RequestParam("id") Long id) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            TmUser user = GetUid.getUID(request, redisClient);
            Boolean flag = aCommonCraftService.deleteByPrimaryKey(id, user.getCompanyId());
            //插入操作日志
//            TmUser user = GetUid.getUID(request, redisClient);
//            UserOperateRecord userOperateRecord = new UserOperateRecord();
//            userOperateRecord.setUserId(user.getUserid());
//            userOperateRecord.setCompanyId(user.getCompanyId());
//            userOperateRecord.setObjectType(CommonConstant.FLOWCONFIG);
//            userOperateRecord.setObject(CommonConstant.INITIAL_BUSINESSID);
//            userOperateRecord.setOperateId("");
//            userOperateRecord.setOperateName(CommonConstant.DELETE_COMMONCRAFT);
//            StringBuilder remark = new StringBuilder(id.toString());
//            if (flag) {
//                remark.append(CommonConstant.OPERATION_SUCCEED);
//            } else {
//                remark.append(CommonConstant.OPERATION_DEFEATED);
//            }
//            userOperateRecord.setRemark(remark.toString());
//            userOperateRecord.setCreateDate(new Date());
//            userOperateRecordService.insert(userOperateRecord);
            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            returnMsg.setErrorMsg("请求异常");
            logger.error("删除指定通用问答(回答数据)异常:", e);
        }
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/flowConfig/commonCraft/createToBasic 新增一条的通用话术数据(问题相关数据)
     * @apiName createToBasic
     * @apiGroup CommonCraftContorller
     * @apiParam {QCommonCraftDto} qCommonCraftDto  通用话术问答数据，示例如下：
     * {
     * "name":"话术标题",
     * "question":"触发话术",
     * "keyWord":"关键词",
     * "action":"回答后处理",
     * "jump":"跳转到指定流程",
     * "score":"得分",
     * "foucs":"关注点",
     * "msgtemplId":"短信模板id，当为0时表示不发送短信"
     * }
     * @apiSuccess {String} code 0:新增成功 1:新增失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例(返回一个任务表示id，用于上传录音文件)
     * {
     * "code": 0,
     * "content": {
     * "id":主键id,
     * "craftId":"话术标识"
     * },
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @PostMapping("/createToBasic")
    public ReturnMsg createToBasic(HttpServletRequest request, @RequestBody QCommonCraftDto qCommonCraftDto) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            TmUser user = GetUid.getUID(request, redisClient);
            Long companyId = user.getCompanyId();
            String userid = user.getUserid();
            //新增操作
            String craftId = UUID.randomUUID().toString().replace("-", "").toLowerCase();
            qCommonCraftDto.setCompanyId(companyId);
            if (qCommonCraftDto.getBusinessId() == null) {
                qCommonCraftDto.setBusinessId(Long.valueOf(CommonConstant.INITIAL_BUSINESSID));
            }
            qCommonCraftDto.setUid(userid);
            qCommonCraftDto.setCraftId(craftId);
            QCommonCraftDto operateObj = new QCommonCraftDto();
            BeanUtils.copyProperties(qCommonCraftDto, operateObj);
            operateObj.setFoucs(tBusinessFocusService.convertFocusNamesToFocusIds(user, qCommonCraftDto.getBusinessId(), operateObj.getFoucs()));
            boolean flag = qCommonCraftService.insert(operateObj);
            if (flag) {
                qCommonCraftService.pushSynAlgorithmDataToRedis(AlgorithmDataSynDto.OperationEnum.INSERT, operateObj.getId(), operateObj.getCompanyId(), operateObj.getBusinessId());
            }
            //插入操作日志
            Map<String, Object> map = new HashMap<>();
            map.put("name", qCommonCraftDto.getName());
            map.put("craftd", craftId);
            map.put("userid", userid);
            StringBuilder remark = new StringBuilder(JSONObject.toJSONString(map));
            if (flag) {
                remark.append(CommonConstant.OPERATION_SUCCEED);
            } else {
                remark.append(CommonConstant.OPERATION_DEFEATED);
            }

            // 操作日志处理
            String opInfo = String.format("话术标题:%s", qCommonCraftDto.getName());
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, CommonConstant.INITIAL_BUSINESSID, "commonCraft/createToBasic",
                    CommonConstant.CREATE_COMMONCRAFT, remark.toString(), OperateType.CREATE, opInfo, null, null);

            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            Map<String, Object> temp = new HashMap<>();
            temp.put("id", qCommonCraftDto.getId());
            temp.put("craftId", craftId);
            returnMsg.setContent(temp);
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            logger.error("新增通用话术(问题相关数据)异常:", e);
        }
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/flowConfig/commonCraft/createToContentRelevant 新增一条的通用话术数据(回答相关数据)
     * @apiName createToContentRelevant
     * @apiGroup CommonCraftContorller
     * @apiParam {QCommonCraftDto} qCommonCraftDto  通用话术问答数据，示例如下：
     * {
     * "craftId":"话术标识",
     * "recordDetail": [
     * {
     * "content":"文本内容",
     * "recordFile":"录音文件名",
     * "recordDescribe":"录音描述(0:tts,1:手动上传)"
     * }
     * ]
     * }
     * @apiSuccess {String} code 0:新增成功 1:新增失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例(返回一个任务表示id，用于上传录音文件)
     * {
     * "code": 0,
     * "content": 主键id,
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @PostMapping("/createToContentRelevant")
    public ReturnMsg createToContentRelevant(HttpServletRequest request, @RequestBody ACommonCraftDto aCommonCraftDto) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            TmUser user = GetUid.getUID(request, redisClient);
            Long companyId = user.getCompanyId();
            String userid = user.getUserid();
            //新增操作
            String craftId = aCommonCraftDto.getCraftId();
            if (StringUtils.isEmpty(craftId)) {
                throw new NullPointerException("话术标识不能为null!");
            }
            aCommonCraftDto.setCompanyId(companyId);
            if (aCommonCraftDto.getBusinessId() == null) {
                aCommonCraftDto.setBusinessId(0L);
            }
            aCommonCraftDto.setUid(userid);
            boolean flag = aCommonCraftService.insertAndRecardLog(aCommonCraftDto, user);
            if (flag){
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
                returnMsg.setContent(aCommonCraftDto.getId());
            } else {
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
                returnMsg.setErrorMsg("操作失败！");
            }
        } catch (NullPointerException e) {
            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            returnMsg.setErrorMsg("请检查参数");
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            returnMsg.setErrorMsg("请求异常");
            logger.error("新增通用话术异常:", e);
        }
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/flowConfig/commonCraft/modifyToBasic 修改一条当前客户的通用话术数据(问题相关数据)
     * @apiName modifyToBasic
     * @apiGroup CommonCraftContorller
     * @apiParam {QCommonCraftDto} qCommonCraftDto  通用话术问答数据，示例如下：
     * {
     * "id":"主键id",
     * "craftId":"话术标识",
     * "name":"话术标题",
     * "question":"触发话术",
     * "keyWord":"关键词",
     * "score":"得分",
     * "action":"回答后处理",
     * "jump":"跳转到指定流程",
     * "msgtemplId":"短信模板id，当为0时表示不发送短信"
     * }
     * @apiSuccess {String} code 0:新增成功 1:新增失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例(返回一个任务表示id，用于上传录音文件)
     * {
     * "code": 0,
     * "content": 无,
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @PostMapping("/modifyToBasic")
    public ReturnMsg modifyToBasic(HttpServletRequest request, @RequestBody QCommonCraftDto qCommonCraftDto) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            Boolean flag = false;
            TmUser user = GetUid.getUID(request, redisClient);
            Long companyId = user.getCompanyId();
            String userid = user.getUserid();
            qCommonCraftDto.setCompanyId(companyId);
            qCommonCraftDto.setUid(userid);
            QCommonCraft oldObj = qCommonCraftService.selectByPrimaryKey(qCommonCraftDto.getId());
            QCommonCraftDto operateObj = new QCommonCraftDto();
            BeanUtils.copyProperties(qCommonCraftDto, operateObj);
            operateObj.setFoucs(tBusinessFocusService.convertFocusNamesToFocusIds(user, qCommonCraftDto.getBusinessId(), operateObj.getFoucs()));
            flag = qCommonCraftService.updateByPrimaryKey(operateObj);
            if (flag) {
                qCommonCraftService.pushSynAlgorithmDataToRedis(AlgorithmDataSynDto.OperationEnum.UPDATE, qCommonCraftDto.getId(), qCommonCraftDto.getCompanyId(), qCommonCraftDto.getBusinessId());
            }
            String craftId = qCommonCraftDto.getCraftId();
            //插入操作日志
            Map<String, Object> map = new HashMap<>();
            map.put("name", qCommonCraftDto.getName());
            map.put("craftd", craftId);
            map.put("userid", userid);
            StringBuilder remark = new StringBuilder(JSONObject.toJSONString(map));
            if (flag) {
                remark.append(CommonConstant.OPERATION_SUCCEED);
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            } else {
                remark.append(CommonConstant.OPERATION_DEFEATED);
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            }

            String opInfo = String.format("话术标题:%s", oldObj.getName());
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, CommonConstant.INITIAL_BUSINESSID, "commonCraft/modifyToBasic",
                    CommonConstant.MODIFY_COMMONCRAFT, remark.toString(), OperateType.MODIFY, opInfo, oldObj, qCommonCraftDto);
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            logger.error("修改通用话术数据异常:", e);
        }
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/flowConfig/commonCraft/modifyToContentRelevant 修改一条当前客户的通用话术数据(回答相关数据)
     * @apiName modifyToContentRelevant
     * @apiGroup CommonCraftContorller
     * @apiParam {QCommonCraftDto} qCommonCraftDto  通用话术问答数据，示例如下：
     * {
     * "id":"主键id",
     * "craftId":"话术标识",
     * "recordDetail":[
     * {
     * "content":"文本内容",
     * "recordFile":"录音文件",
     * "recordDescribe":"录音描述(0:tts,1:手动上传)"
     * }
     * ]
     * }
     * @apiSuccess {String} code 0:新增成功 1:新增失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例(返回一个任务表示id，用于上传录音文件)
     * {
     * "code": 0,
     * "content": 无,
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @PostMapping("/modifyToContentRelevant")
    public ReturnMsg modifyToContentRelevant(HttpServletRequest request, @RequestBody ACommonCraftDto aCommonCraftDto) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            boolean flag;
            TmUser user = GetUid.getUID(request, redisClient);
            Long companyId = user.getCompanyId();
            String userid = user.getUserid();
            aCommonCraftDto.setCompanyId(companyId);
            aCommonCraftDto.setUid(userid);
            ACommonCraft oldObj = aCommonCraftService.selectByPrimaryKey(aCommonCraftDto.getId());
            QCommonCraft oldQCraft = qCommonCraftService.selectQCommonCraftByCraftIdAndCompanyIdAndBusinessId(oldObj.getCraftId(), oldObj.getCompanyId(), oldObj.getBusinessId());
            flag = aCommonCraftService.updateAndRecardLogByPrimaryKey(aCommonCraftDto, user);
            String craftId = aCommonCraftDto.getCraftId();
            //插入操作日志
            Map<String, Object> map = new HashMap<>();
            map.put("name", aCommonCraftDto.getContent());
            map.put("craftd", craftId);
            map.put("userid", userid);
            StringBuilder remark = new StringBuilder(JSONObject.toJSONString(map));
            if (flag) {
                remark.append(CommonConstant.OPERATION_SUCCEED);
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            } else {
                remark.append(CommonConstant.OPERATION_DEFEATED);
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            }

            String opInfo = String.format("话术标题:%s", oldQCraft.getName());
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, CommonConstant.INITIAL_BUSINESSID, "commonCraft/modifyToContentRelevant",
                    CommonConstant.MODIFY_COMMONCRAFT, remark.toString(), OperateType.MODIFY, opInfo, oldObj, aCommonCraftDto);
            //获取列表信息返回
            List<QCommonCraftDto> qCommonCraftDtos = qCommonCraftService.selectByCompanyIdAndBusinessId(companyId, aCommonCraftDto.getBusinessId());
            returnMsg.setContent(null == qCommonCraftDtos ? new Object[0] : qCommonCraftDtos);
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            logger.error("修改通用话术数据异常:", e);
        }
        return returnMsg;
    }

}
