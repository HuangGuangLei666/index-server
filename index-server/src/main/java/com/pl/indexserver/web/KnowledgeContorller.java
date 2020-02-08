package com.pl.indexserver.web;

import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.model.KnowledgeAnswerDto;
import com.pl.indexserver.model.KnowledgeQuestionDto;
import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.model.redisdto.AlgorithmDataSynDto;
import com.pl.indexserver.service.*;
import com.pl.indexserver.untils.CommonConstant;
import com.pl.indexserver.untils.GetUid;
import com.pl.indexserver.untils.OperateType;
import com.pl.indexserver.untils.RedisClient;
import com.pl.model.KnowledgeAnswer;
import com.pl.model.KnowledgeQuestion;
import com.pl.model.TmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/busiManagement")
public class KnowledgeContorller {

    private static final Logger logger = LoggerFactory.getLogger(KnowledgeContorller.class);

    @Value("${recordings.address}")
    private String filePath;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private UserOperateRecordService userOperateRecordService;
    @Autowired
    private FileTransferService fileTransferService;
    @Autowired
    private KnowledgeQuestionService knowledgeQuestionService;
    @Autowired
    private KnowledgeAnswerService knowledgeAnswerService;
    @Autowired
    private TBusinessFocusService tBusinessFocusService;


    /**
     * @api {POST} /busiManagement/flowConfig/knowledge/createToBasic 【20181106】问答管理-新增问答知识库(问题相关数据)
     * @apiName createToBasic
     * @apiGroup KnowledgeContorller
     * @apiParam {KnowledgeDto} questionDto  知识库问答数据对象：
     * @apiParamExample {json} 问答知识库输入
     * {
     * "id":"主键id,新建时填空",
     * "knowledgeId":"知识库标识,新建时填空",
     * "name":"名称",
     * "businessId":"智库id",
     * "workflowId":"流程id",
     * "question":"触发话术，多个用&分开",
     * "keyWord":"关键字，多个用&分开",
     * "score":"得分",
     * "type":"问题类型 1：单轮,2：多轮",
     * "action":"回答后处理方式",
     * "jump":"跳转到指定流程标识",
     * "foucs":"关注点",
     * "msgId":"短信模板id，当且仅当为0时表示不发送短信"
     * }
     * @apiSuccess {String} code 0:新增成功 1:新增失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例(返回一个知识库标识，用于上传录音文件)
     * {
     * "code": 0,
     * "content": {
     *     "id":主键id,
     *     "knowledgeId":"知识标识"
     * },
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @PostMapping(value = "/flowConfig/knowledge/createToBasic")
    public ReturnMsg createToBasic(HttpServletRequest request,
                                     @RequestBody KnowledgeQuestionDto knowledgeQuestionDto) {
        ReturnMsg returnMsg = new ReturnMsg();
        TmUser user = GetUid.getUID(request, redisClient);
        if (StringUtils.isEmpty(user)){
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("登录已过期，请重新登录！");
            return returnMsg;
        }
        boolean flag;
        try {
            //填充部分数据
            Long companyId = user.getCompanyId();
            String userid = user.getUserid();
            String knowledgeId = UUID.randomUUID().toString().replace("-", "").toLowerCase();
            Long businessId = knowledgeQuestionDto.getBusinessId();
            knowledgeQuestionDto.setCompanyId(companyId);
            knowledgeQuestionDto.setUid(userid);
            knowledgeQuestionDto.setKnowledgeId(knowledgeId);
            KnowledgeQuestionDto operateObj = new KnowledgeQuestionDto();
            BeanUtils.copyProperties(knowledgeQuestionDto, operateObj);
            operateObj.setFoucs(tBusinessFocusService.convertFocusNamesToFocusIds(user, businessId, operateObj.getFoucs()));
            flag = knowledgeQuestionService.insert(operateObj);
            if (flag) {
                knowledgeQuestionService.pushSynAlgorithmDataToRedis(AlgorithmDataSynDto.OperationEnum.INSERT, operateObj.getId(), operateObj.getCompanyId(), operateObj.getBusinessId());
            }
            //插入操作日志
            Map<String, Object> map = new HashMap<>();
            map.put("name", knowledgeQuestionDto.getName());
            map.put("knowledgeId", knowledgeId);
            map.put("businessId", businessId);
            map.put("userid", userid);
            StringBuilder remark = new StringBuilder(JSONObject.toJSONString(map));
            if (flag) {
                remark.append(CommonConstant.OPERATION_SUCCEED);
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            } else {
                remark.append(CommonConstant.OPERATION_DEFEATED);
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            }
            // 操作日志处理
            String opInfo = String.format("话术标题:%s", knowledgeQuestionDto.getName());
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, businessId.toString(), "knowledge/createToBasic",
                    CommonConstant.CREATE_KNOWLEDGE, remark.toString(), OperateType.CREATE, opInfo, null, null);
            Map<String,Object> temp = new HashMap<>();
            temp.put("id",operateObj.getId());
            temp.put("knowledgeId",knowledgeId);
            temp.put("businessId",businessId);
            returnMsg.setContent(temp);
        } catch (Exception e) {
            logger.error("新增知识库问答(问题相关)失败:", e);
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
        }
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/flowConfig/knowledge/createToContentRelevant 问答管理-新增问答知识库(回答部分)
     * @apiName createToContentRelevant
     * @apiGroup KnowledgeContorller
     * @apiParam {KnowledgeDto} questionDto  知识库问答数据对象：
     * @apiParamExample {json} 问答知识库输入
     * {
     * "id":"主键id",
     * "knowledgeId":"知识库标识",
     * "businessId":"智库id",
     * "recordDetail": [
     * {
     *     "content":"文本内容",
     *     "recordFile":"录音文件名",
     *     "recordDescribe":"录音描述(0:tts,1:手动上传)"
     * }
     * ]
     * }
     * @apiSuccess {String} code 0:新增成功 1:新增失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例(返回一个知识库标识，用于上传录音文件)
     * {
     * "code": 0,
     * "content": "123456",
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @PostMapping(value = "/flowConfig/knowledge/createToContentRelevant")
    public ReturnMsg createToContentRelevant(HttpServletRequest request, @RequestBody KnowledgeAnswerDto knowledgeAnswerDto) {
        ReturnMsg returnMsg = new ReturnMsg();
        TmUser user = GetUid.getUID(request, redisClient);
        if (StringUtils.isEmpty(user)){
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("登录已过期，请重新登录！");
            return returnMsg;
        }
        boolean flag;
        try {
            //填充部分数据
            Long companyId = user.getCompanyId();
            String userid = user.getUserid();
            knowledgeAnswerDto.setCompanyId(companyId);
            knowledgeAnswerDto.setUid(userid);
            knowledgeAnswerDto.setKnowledgeId(knowledgeAnswerDto.getCraftId());
            flag = knowledgeAnswerService.insertAndRecardLog(knowledgeAnswerDto,user);
            if (flag){
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            } else {
                returnMsg.setCode(-1);
                returnMsg.setErrorMsg("操作失败！");
            }
        } catch (Exception e) {
            logger.error("新增知识库问答(回答相关)异常:  ", e);
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            returnMsg.setErrorMsg("操作失败！");
        }
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/flowConfig/knowledge/modifyToBasic 【20181106】问答管理-修改问答知识库(问题相关)
     * @apiName modifyToBasic
     * @apiGroup KnowledgeContorller
     * @apiParam {KnowledgeDto} knowledgeDto  知识库：
     * @apiParamExample {json} 问答知识库输入
     * {
     * "id":"主键i"，
     * "knowledgeId":"知识库标识"，
     * "businessId":"智库id",
     * "workflowId":"流程id",
     * "name":"名称"，
     * "question":"触发话术，多个用&分开"，
     * "score":"得分",
     * "keyWord":"关键字，多个用&分开"，
     * "type":"问题类型 1：单轮,2：多轮"，
     * "action":"回答后处理方式",
     * "jump":"跳转到指定流程标识",
     * "msgId":"短信模板id，当且仅当为0时表示不发送短信"
     * }
     * @apiSuccess {String} code 0:新增成功 1:新增失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例(返回一个知识库标识，用于上传录音文件)
     * {
     * "code": 0,
     * "content": "123456",
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @PostMapping(value = "/flowConfig/knowledge/modifyToBasic")
    public ReturnMsg modifyToBasic(HttpServletRequest request,
                                     @RequestBody KnowledgeQuestionDto knowledgeQuestionDto) {
        ReturnMsg returnMsg = new ReturnMsg();
        TmUser user = GetUid.getUID(request, redisClient);
        if (StringUtils.isEmpty(user)){
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("登录已过期，请重新登录！");
            return returnMsg;
        }
        boolean flag;
        try {
            Long companyId = user.getCompanyId();
            String userid = user.getUserid();
            knowledgeQuestionDto.setUid(userid);
            knowledgeQuestionDto.setCompanyId(companyId);
            KnowledgeQuestion oldObj = knowledgeQuestionService.selectByPrimaryKey(knowledgeQuestionDto.getId());
            KnowledgeQuestionDto operateObj = new KnowledgeQuestionDto();
            BeanUtils.copyProperties(knowledgeQuestionDto, operateObj);
            operateObj.setFoucs(tBusinessFocusService.convertFocusNamesToFocusIds(user, knowledgeQuestionDto.getBusinessId(), operateObj.getFoucs()));
            flag = knowledgeQuestionService.update(operateObj);
            if (flag) {
                knowledgeQuestionService.pushSynAlgorithmDataToRedis(AlgorithmDataSynDto.OperationEnum.UPDATE, operateObj.getId(), operateObj.getCompanyId(), operateObj.getBusinessId());
            }
            //插入操作日志
            Map<String, Object> map = new HashMap<>();
            map.put("name", knowledgeQuestionDto.getName());
            map.put("knowledgeId", knowledgeQuestionDto.getKnowledgeId());
            map.put("businessId", knowledgeQuestionDto.getBusinessId());
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
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, knowledgeQuestionDto.getBusinessId().toString(), "knowledge/modifyToBasic",
                    CommonConstant.MODIFY_KNOWLEDGE, remark.toString(), OperateType.MODIFY, opInfo, oldObj, knowledgeQuestionDto);
        } catch (Exception e) {
            logger.error("修改指定知识库问答(问题相关)异常:  ", e);
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            returnMsg.setErrorMsg("请求异常");
        }
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/flowConfig/knowledge/modifyToContentRelevant 问答管理-修改问答知识库(回答相关)
     * @apiName modifyToContentRelevant
     * @apiGroup KnowledgeContorller
     * @apiParam {KnowledgeDto} knowledgeDto  知识库：
     * @apiParamExample {json} 问答知识库输入
     * {
     * "id":"主键i"，
     * "knowledgeId":"知识库标识",
     * "businessId":"智库id",
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
     * @apiSuccessExample {json}  成功返回示例(返回一个知识库标识，用于上传录音文件)
     * {
     * "code": 0,
     * "content": 无,,
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @PostMapping(value = "/flowConfig/knowledge/modifyToContentRelevant")
    public ReturnMsg modifyToContentRelevant(HttpServletRequest request, @RequestBody KnowledgeAnswerDto knowledgeAnswerDto) {
        ReturnMsg returnMsg = new ReturnMsg();
        TmUser user = GetUid.getUID(request, redisClient);
        if (StringUtils.isEmpty(user)){
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("登录已过期，请重新登录！");
            return returnMsg;
        }
        boolean flag;
        try {
            Long companyId = user.getCompanyId();
            String userid = user.getUserid();
            knowledgeAnswerDto.setUid(userid);
            knowledgeAnswerDto.setCompanyId(companyId);
            knowledgeAnswerDto.setKnowledgeId(knowledgeAnswerDto.getCraftId());
            KnowledgeAnswer oldObj = knowledgeAnswerService.selectByPrimaryKey(knowledgeAnswerDto.getId());
            KnowledgeQuestion oldQuestion = knowledgeQuestionService.selectQuestionByKnowledgeId(knowledgeAnswerDto.getKnowledgeId());
            flag = knowledgeAnswerService.updateAndRecardLogByPrimaryKey(knowledgeAnswerDto,user);
            //插入操作日志
            Map<String, Object> map = new HashMap<>();
            map.put("knowledgeId", knowledgeAnswerDto.getKnowledgeId());
            map.put("businessId", knowledgeAnswerDto.getBusinessId());
            map.put("userid", userid);
            StringBuilder remark = new StringBuilder(JSONObject.toJSONString(map));
            if (flag) {
                remark.append(CommonConstant.OPERATION_SUCCEED);
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            } else {
                remark.append(CommonConstant.OPERATION_DEFEATED);
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            }
            String opInfo = String.format("话术标题:%s", oldQuestion.getName());
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, knowledgeAnswerDto.getBusinessId().toString(), "knowledge/modifyToContentRelevant",
                    CommonConstant.MODIFY_KNOWLEDGE_ANSWEr, remark.toString(), OperateType.MODIFY, opInfo, oldObj, knowledgeAnswerDto);
        } catch (Exception e) {
            logger.error("修改指定知识库问答(回答相关)异常 :", e);
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
        }
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/flowConfig/knowledge/queryDetail 问答管理-查询问答知识库详情
     * @apiName queryDetailKnowledge
     * @apiGroup KnowledgeContorller
     * @apiParam {Long} id  知识库标识
     * @apiSuccess {String} code 0:新增成功 1:新增失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例(返回一个知识库标识的详情)
     * {
     * "code": 0,
     * "content":
     * {
     * "knowledgeId":"知识库标识，新建时填空"，
     * "name":"问答标题"，
     * "question":"触发话术，多个用&分开"，
     * "keyWord":"关键字，多个用&分开"，
     * "answerList":[
     * {
     * "id":"主键id，新建时填0",
     * "answer":"回答内容",
     * "recordName":"录音文件名称"
     * }
     * ]，
     * "type":"问题类型 1：单轮,2：多轮"，
     * "action":"回答后处理方式",
     * "jump":"跳转到指定流程标识",
     * "msgtemplId":"短信模板id，当且仅当为0时表示不发送短信"
     * },
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @RequestMapping(value = "/flowConfig/knowledge/queryDetail")
    public ReturnMsg queryDetailKnowledge(@RequestParam("id") Long id) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            if (0 == id) {
                returnMsg.setContent(new KnowledgeQuestionDto());
                return returnMsg;
            }
            KnowledgeQuestionDto knowledgeQuestionDto = knowledgeQuestionService.selectDetailByPrimaryKey(id);
            knowledgeQuestionDto.setFoucs(tBusinessFocusService.convertFocusIdsToFocusNames(knowledgeQuestionDto.getCompanyId(), knowledgeQuestionDto.getBusinessId(), knowledgeQuestionDto.getFoucs()));
            returnMsg.setContent(knowledgeQuestionDto);
        } catch (Exception e) {
            logger.error("获取知识库问答详情失败:", e);
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
        }
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/flowConfig/knowledge/delete 问答管理-删除问答知识库
     * @apiName deleteKnowledge
     * @apiGroup KnowledgeContorller
     * @apiParam {String} id  id值
     * @apiParam {String} businessId  智库方案id值
     * @apiSuccess {String} code 0:新增成功 1:新增失败
     * @apiSuccess {String} content  无
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
    @PostMapping(value = "/flowConfig/knowledge/delete")
    public ReturnMsg delete(HttpServletRequest request, @RequestParam("id") Long id, @RequestParam("businessId") String businessId) {
        ReturnMsg returnMsg = new ReturnMsg();
        boolean flag;
        try {
            Map<String, Object> fileMap = knowledgeQuestionService.selectFileDetailByPrimaryKey(id);
            KnowledgeQuestion oldObj = knowledgeQuestionService.selectByPrimaryKey(id);
            flag = knowledgeQuestionService.deleteKnowledgeDetail(id);
            if (flag) {
                knowledgeQuestionService.pushSynAlgorithmDataToRedis(AlgorithmDataSynDto.OperationEnum.DELETE, oldObj.getId(), oldObj.getCompanyId(), oldObj.getBusinessId());
            }
            if (null != fileMap) {
                String filePath = (String) fileMap.get("filePath");
                String[] fileNames = (String[]) fileMap.get("fileNames");
                fileTransferService.deleteFilesByFTP(filePath, fileNames);
            }
            //插入操作日志
            TmUser user = GetUid.getUID(request, redisClient);
            StringBuilder remark = new StringBuilder("id=" + id);
            if (flag) {
                remark.append(CommonConstant.OPERATION_SUCCEED);
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            } else {
                remark.append(CommonConstant.OPERATION_DEFEATED);
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            }
            // 操作日志处理
            String opInfo = String.format("话术标题:%s", oldObj.getName());
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, businessId.toString(), "knowledge/delete",
                    CommonConstant.DELETE_KNOWLEDGE, remark.toString(), OperateType.DELETE, opInfo, null, null);
        } catch (Exception e) {
            logger.error("删除指定知识库问答失败", e);
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("删除指定知识库问答失败！");
        }
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/flowConfig/knowledge/deleteContent 问答管理-删除问答知识库(单个回答)
     * @apiName deleteContent
     * @apiGroup KnowledgeContorller
     * @apiParam {String} id  id值
     * @apiParam {String} businessId  智库方案id值
     * @apiSuccess {String} code 0:新增成功 1:新增失败
     * @apiSuccess {String} content  无
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
    @PostMapping(value = "/flowConfig/knowledge/deleteContent")
    public ReturnMsg deleteContent(HttpServletRequest request, @RequestParam("id") Long id, @RequestParam("businessId") String businessId) {
        ReturnMsg returnMsg = new ReturnMsg();
        boolean flag;
        try {
            KnowledgeAnswer oldObj = knowledgeAnswerService.selectByPrimaryKey(id);
            KnowledgeQuestion oldQuestion = knowledgeQuestionService.selectQuestionByKnowledgeId(oldObj.getKnowledgeId());
            flag = knowledgeAnswerService.delete(id);
            //插入操作日志
            TmUser user = GetUid.getUID(request, redisClient);
            StringBuilder remark = new StringBuilder("id=" + id);
            if (flag) {
                remark.append(CommonConstant.OPERATION_SUCCEED);
                returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            } else {
                remark.append(CommonConstant.OPERATION_DEFEATED);
                returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            }
            // 操作日志处理
            String opInfo = String.format("话术标题:%s", oldQuestion.getName());
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.FLOWCONFIG, businessId, "knowledge/deleteContent",
                    CommonConstant.DELETE_KNOWLEDGE_ANSWER, remark.toString(), OperateType.DELETE, opInfo, null, null);
        } catch (Exception e) {
            logger.error("删除指定知识库问答(单个回答)异常:  ", e);
        }
        return returnMsg;
    }

    /**
     * @api {GET} /busiManagement/flowConfig/knowledge/query 【20181106】问答管理-查询知识库话术列表信息;
     * @apiName queryKnowledge
     * @apiGroup KnowledgeContorller
     * @apiParam{Long} businessId 业务id
     * @apiParam{Long} workflowId 流程id
     * @apiParam{String} name 问答名称
     * @apiSuccess {String} code 0:成功 其他:错误码
     * @apiSuccess {String} content  返回知识库列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": [
     * {
     * "id": "主键id",
     * "name": "话术标题",
     * "keyNum": "关键词数量",
     * "type": "问题类型",
     * "action": "回答后处理",
     * "modifyDate": "更新时间",
     * },
     * ],
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @GetMapping(value = "/flowConfig/knowledge/query")
    public ReturnMsg queryKnowledge(HttpServletRequest request,
                                    @RequestParam("businessId") Long businessId,
                                    @RequestParam(value = "workflowId", required = false) Long workflowId,
                                    @RequestParam(value = "name", required = false) String name) {
        ReturnMsg returnMsg = new ReturnMsg();
        TmUser user = GetUid.getUID(request, redisClient);
        if (StringUtils.isEmpty(user)){
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("登录过期，请重新登录！");
            return returnMsg;
        }
        try {
            Long companyId = user.getCompanyId();
            List<KnowledgeQuestionDto> knowledgeQuestionDtos = knowledgeQuestionService.selectByCompanyIdAndBusinessId(companyId, businessId, workflowId, name);
            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            returnMsg.setContent(null == knowledgeQuestionDtos ? new Object[0] : knowledgeQuestionDtos);
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            logger.error("获取知识库问答列表页异常:", e);
        }
        return returnMsg;
    }
}
