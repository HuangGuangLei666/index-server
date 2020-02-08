package com.pl.indexserver.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.pl.indexserver.model.*;
import com.pl.indexserver.service.*;
import com.pl.indexserver.untils.*;
import com.pl.model.*;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static com.pl.indexserver.untils.StampToDates.dateToString;
import static com.pl.indexserver.untils.StampToDates.stringToDate;


@RestController
@RequestMapping(value = "/busiManagement")
public class OutboundController {
    private static final Logger logger = LoggerFactory.getLogger(OutboundController.class);
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm");
    private static SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat simpleDateFormat4 = new SimpleDateFormat("yyyyMM");
    private static SimpleDateFormat simpleDateFormat5 = new SimpleDateFormat("_yyyyMM");

    private static final String NumberRegexp = "(^(0\\d{2,3})(-)?\\d{7,8})$|(1[0-9]{10})|(\\d{7,8})";

    private static String PATTERN_DATE_2 = "yyyy-MM-dd HH:mm:ss";
    @Value("${recordings.ftpPath}")
    private String ftpFilePath;

    @Value("${custom.excel-path}")
    private String excelFilePath;

    @Value("${recordings.address}")
    private String recordAddress;

    @Value("${task.switch}")
    private boolean tswitch;

    @Autowired
    private RedisClient redisClient;

    @Autowired //呼叫坐席表
    private TCallAgentService tCallAgentService;

    @Autowired //外呼任务表
    private CallTaskService callTaskService;

    @Autowired //业务表
    private TBusinessService tBusinessService;

    @Autowired //对话详情
    private TDialogService tDialogService;

    @Autowired // 电话号码
    private TmCustomerService tmCustomerService;
    @Autowired
    private BusinessReportService businessReportService;

    @Autowired
    private UserOperateRecordService userOperateRecordService;

    @Autowired
    private FileTransferService fileTransferService;

    @Autowired
    private SmsMsgService smsMsgService;

    @Autowired
    private SpeechcraftTagService speechcraftTagService;

    @Autowired  //黑名单表
    private BlacklistService blacklistService;

    /**
     * @api {GET} /busiManagement/callTask/agent/queryAvailable   查询坐席
     * @apiName agentQueryAvailable
     * @apiGroup OutboundController
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  坐席信息
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": {
     * "phoneAll": 0,
     * "freeList": [
     * {
     * "id": 7,
     * "companyId": 12,
     * "channelId": 1,
     * "agentNum": "213213",
     * "outNumber": "32131",
     * "numberType": 1,
     * "status": 0,
     * "isUsed": 0,
     * "usedTaskid": 0
     * }
     * ],
     * "allusered": 2,
     * "fixedNotUsered": 1,
     * "fixedAll": 3,
     * "fixedUsered": 2,
     * "phoneNotUsered": 0,
     * "allCount": 3,
     * "allNotusered": 1,
     * "phoneUsered": 0
     * },
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @RequestMapping(value = "/callTask/agent/queryAvailable")
    public ReturnMsg agentQueryAvailable(HttpServletRequest request) {
        TmUser user = GetUid.getUID(request, redisClient);
        ReturnMsg returnMsg = new ReturnMsg();
        Map<String, Object> map = new HashMap<>();
        List<TCallAgent> freeList = new ArrayList<>(); //空闲坐席
        try {
            List<TCallAgent> callAgentList = tCallAgentService.getTCallAgentListByCompany_id(user.getCompanyId());
            //全部坐席总数
            int allCount = callAgentList.size();
            //全部已用
            int allusered = 0;
            //全部空闲
            int allNotusered = 0;
            //固话总数量
            int fixedAll = 0;
            int fixedUsered = 0;
            int fixedNotUsered = 0;

            //手机总数量
            int phoneAll = 0;
            int phoneUsered = 0;
            int phoneNotUsered = 0;
            for (TCallAgent agent : callAgentList) {
                //空闲坐席列表
                if (agent.getStatus() == 0 && agent.getIsUsed() == 0) {
                    freeList.add(agent);
                    allNotusered += 1;
                } else {
                    allusered += 1;
                }
                //固话数量
                if (agent.getNumberType() == 1) {
                    fixedAll += 1;
                    if (agent.getIsUsed() == 0 && agent.getStatus() == 0) {
                        //空闲
                        fixedNotUsered += 1;
                    } else {
                        //已用
                        fixedUsered += 1;
                    }
                } else {  //手机数量
                    phoneAll += 1;
                    if (agent.getIsUsed() == 0 && agent.getStatus() == 0) {
                        //空闲
                        phoneNotUsered += 1;
                    } else {
                        //已用
                        phoneUsered += 1;
                    }
                }
            }
            //手机坐席数量
            //固话坐席数量
            //已用坐席数量
            map.put("allCount", allCount);
            map.put("allusered", allusered);
            map.put("allNotusered", allNotusered);
            map.put("fixedAll", fixedAll);
            map.put("fixedUsered", fixedUsered);
            map.put("fixedNotUsered", fixedNotUsered);
            map.put("phoneAll", phoneAll);
            map.put("phoneUsered", phoneUsered);
            map.put("phoneNotUsered", phoneNotUsered);
            map.put("freeList", freeList);
            returnMsg.setContent(map);
            returnMsg.setCode(0);
        } catch (Exception e) {
            returnMsg.setErrorMsg(e.getMessage());
            returnMsg.setCode(1);
            return returnMsg;
        }
        return returnMsg;
    }


    /**
     * @api {GET} /busiManagement/callTask/task/create   申请任务标识
     * @apiName create
     * @apiGroup OutboundController
     * @apiParam {String} taskName 任务名称
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务ID
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": 29,
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @RequestMapping(value = "/callTask/task/create")
    public ReturnMsg create(HttpServletRequest request,
                            @RequestParam("taskName") String taskName,
                            @RequestParam(value = "taskId", required = false) Long taskId) {
        TmUser user = GetUid.getUID(request, redisClient);
        ReturnMsg returnMsg = new ReturnMsg();
        //修改任务名称
        if (null != taskId && taskId > 0) {
            List<CallTask> callTaskList = callTaskService.getCallTaskListByCompany_id2(user.getCompanyId(), taskName, taskId);
            if (callTaskList.size() > 0) {
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("已存在相同的任务名称！");
                return returnMsg;
            }
            CallTask callTask = new CallTask();
            callTask.setTaskName(taskName);
            callTask.setId(taskId);
            int rest = callTaskService.updateByPrimaryKeySelective(callTask);
            if (rest > 0) {
                returnMsg.setCode(0);
                returnMsg.setContent(taskId);
            } else {
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("任务修改失败！");
            }
            return returnMsg;
        }
        //创建任务。
        List<CallTask> callTaskList = callTaskService.getCallTaskListByCompany_id(user.getCompanyId(), taskName);
        try {
            if (callTaskList.size() > 0) {
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("已存在相同的任务名称！");
                return returnMsg;
            }
            CallTask callTask = new CallTask();
            callTask.setTaskName(taskName);
            callTask.setCreateDate(new Date());
            callTask.setCompanyId(user.getCompanyId());
            callTask.setUid(user.getUserid());
            callTask.setTaskState("未运行");
            callTask.setStatus(4);
            callTask.setTotalNumber(0);
            callTask.setBusinessId(1L);
            callTask.setBeginDate(new Date());
            callTask.setEndDate(new Date());
            callTask.setModifyDate(new Date());
            callTask.setIsTemp(0);
            callTaskService.insert(callTask);
            Long id = callTask.getId();
            returnMsg.setCode(0);
            returnMsg.setContent(id);
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.STATUS_1, id.toString(), "/callTask/task/create",
                    CommonConstant.CREATE_TASK, taskName, OperateType.CREATE, "任务：" + taskName,
                    null, null);
        } catch (Exception e) {
            returnMsg.setErrorMsg("创建任务出错：" + e);
            returnMsg.setCode(-1);
        }
        return returnMsg;
    }


    /**
     * @api {GET} /busiManagement/callTask/business/queryAvailable 查询可选的AI方案
     * @apiName businessQueryAvailable
     * @apiGroup OutboundController
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  可选方案
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": [
     * {
     * "id": 1,
     * "companyId": 12,
     * "name": "测试",
     * "templateName": "测试——模板",
     * "createDate": 1525233932000,
     * "modifyDate": 1525233934000,
     * "enableInterupt": 1,
     * "controllAddr": "xx",
     * "algorithmAddr": "xx",
     * "remark": null,
     * "status": 0
     * }
     * ],
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @RequestMapping(value = "/callTask/business/queryAvailable")
    public ReturnMsg businessQueryAvailable(HttpServletRequest request) {
        TmUser user = GetUid.getUID(request, redisClient);
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            List<TBusiness> tBusinessList = tBusinessService.getTBusinessListByCompany_id(user.getCompanyId());
            returnMsg.setCode(0);
            returnMsg.setContent(tBusinessList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnMsg;
    }

    /**
     * @api {GET} /busiManagement/callTask/cuser/addBatch   添加外呼号码
     * @apiName addBatch
     * @apiGroup OutboundController
     * @apiParam {String} phones 手机号码
     * @apiParam {Long} taskName 任务ID
     * @apiParam {int} priority 是否优先
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回导入时间
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": "luo2018-05-04 03:37:06",
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @RequestMapping(value = "/callTask/cuser/addBatch")
    public ReturnMsg addBatch(HttpServletRequest request, @RequestParam("phones") String phones,
                              @RequestParam("taskid") Long taskid, @RequestParam(value = "priority", required = false) Integer priority) {
        ReturnMsg returnMsg = new ReturnMsg();
        TmUser user = GetUid.getUID(request, redisClient);
        if (null == user) {
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("登录信息丢失！");
            return returnMsg;
        }
        try {
            String[] addNums = phones.split("\\|");
            //添加的号码为空。
            if (addNums.length <= 0) {
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("添加号码为空，请按照提示正确添加号码！");
                return returnMsg;
            }
            //任务是否暂停
            CallTask callTask = callTaskService.selectByPrimaryKey(taskid);
            if (callTask.getStatus() == 1 || callTask.getStatus() == 3) {
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("任务已结束或正在运行，不能添加号码");
                return returnMsg;
            }
            // 记录错误号码
            List<String> errorPhones = new ArrayList<>();
            Set<String> newList = new HashSet<>();
            for (String num : addNums) {
                // 校验手机号的格式
                if (!num.matches(NumberRegexp)) {
                    errorPhones.add(num);
                } else {
                    newList.add(num);
                }
            }
            // 手机号格式有误，提前结束号码处理，把错误手机号返回
            if (errorPhones.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (String tel : errorPhones) {
                    sb.append(tel).append(";");
                }
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("您输入的号码格式有误，请输入正确的号码,错误号码有：" + sb.toString());
                return returnMsg;
            }
            //获取当前任务是否有号码！
            List<String> oldList = tDialogService.getCallTaskPhoeList(user.getCompanyId(), taskid);
            List<String> blackPhoneList = blacklistService.getCompanyBlackPhoneList(user.getCompanyId());
            if (!CollectionUtils.isEmpty(oldList)) {
                //如果数据库中有数据,对比去除相同的数据
                newList.removeAll(oldList);
            }
            if (!CollectionUtils.isEmpty(blackPhoneList)) {
                newList.removeAll(blackPhoneList);
            }
            List<TDialog> tDialogSet = new ArrayList<>();
            boolean addFlag = false;
            for (String num : newList) {
                //还要添加一张表
                TDialog tDialog = new TDialog();
                tDialog.setPriority(priority);
                tDialog.setTelephone(num);
                tDialog.setCreateDate(new Date());
                tDialog.setCompanyId(user.getCompanyId());
                tDialog.setTaskId(taskid);
                tDialog.setStatus(0);
                tDialog.setIsIntention(0);
                tDialogSet.add(tDialog);
                if (tDialogSet.size() == 99) {
                    //99个添加一次
                    tDialogService.addDialog(tDialogSet, taskid);
                    tDialogSet.clear();
                    addFlag = true;
                }
            }
            //添加剩余的
            if (tDialogSet.size() > 0) {
                tDialogService.addDialog(tDialogSet, taskid);
                addFlag = true;
            }
            if (addFlag) {
                addTaskPhone(taskid, user.getCompanyId(), newList, redisClient);
                returnMsg.setCode(0);
                returnMsg.setContent(user.getUsername() + simpleDateFormat.format(new Date()));
            } else {
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("号码保存失败！");
            }
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.STATUS_1, taskid.toString(), "/callTask/cuser/addBatch",
                    CommonConstant.ADD_PHONE, phones, OperateType.CREATE, "号码：" + phones, null, null);
        } catch (Exception e) {
            logger.error("添加号码异常：", e);
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("添加号码失败");
            return returnMsg;
        }
        return returnMsg;
    }


    /**
     * @api {GET} /busiManagement/callTask/index 首页查询
     * @apiName index
     * @apiGroup OutboundController
     * @apiParam {String} task_state 状态
     * @apiParam {String} taskid 任务ID
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": [
     * {
     * "callTaskId": 1,
     * "status": 1,
     * "taskName": "任务名称1",
     * "endDate": "2018-05-02 12:00:00",
     * "taskCount": 11,
     * "finishToday": 8,
     * "intentional": 3,
     * "stillCount": 3,
     * "stillConnect": "72.73%",
     * "sjc": "1524442437000"
     * },
     * {
     * "callTaskId": 2,
     * "status": 1,
     * "taskName": "任务名称2",
     * "endDate": "",
     * "taskCount": 0,
     * "finishToday": 0,
     * "intentional": 0,
     * "stillCount": 0,
     * "stillConnect": "0%",
     * "sjc": "1524534976000"
     * },
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @RequestMapping(value = "/callTask/index")
    public ReturnMsg index(HttpServletRequest request,
                           @RequestParam(value = "task_state", required = false) Integer taskState,
                           @RequestParam(value = "taskid", required = false) String taskName,
                           @RequestParam(value = "pageIndex", defaultValue = "1", required = false) int pageIndex,
                           @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        TmUser user = GetUid.getUID(request, redisClient);
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            if (StringUtils.isEmpty(taskState)) {
                taskState = null;
            }
            if (StringUtils.isEmpty(taskName)) {
                taskName = null;
            }
            returnMsg.setCode(0);
            Page<CallTaskMsg> callTaskMsgs = callTaskService.getIndex(user.getUserid(), user.getCompanyId(), taskState, taskName,
                    pageIndex, pageSize);
            returnMsg.setContent(callTaskMsgs);
            // 登录首页时提示买短信
            boolean flag = smsRemind(user.getCompanyId(), user.getUserid());
            if (flag) {// 在redis中保存已提示的标识
                String time = redisClient.get(2, "tipSMS:" + user.getUserid());
                if (StringUtils.isEmpty(time)) {
                    redisClient.set(2, "tipSMS:" + user.getUserid(), simpleDateFormat.format(new Date()));
                    returnMsg.setErrorMsg("您的可用短信数不足，请尽快充值");
                } else {
                    //当前时间没有提示过
                    if (!simpleDateFormat3.format(new Date()).equals(time.substring(0, 10))) {
                        redisClient.set(2, "tipSMS:" + user.getUserid(), simpleDateFormat.format(new Date()));
                        returnMsg.setErrorMsg("您的可用短信数不足，请尽快充值");
                    }
                }
            }
        } catch (Exception e) {
            logger.info("外呼任务出错：", e);
            returnMsg.setCode(1);
            returnMsg.setErrorMsg(e.getMessage());
            return returnMsg;
        }
        return returnMsg;
    }


    /**
     * @api {GET} /busiManagement/callTask/task/queryBasic 任务基本信息查询
     * @apiName queryBasic
     * @apiGroup OutboundController
     * @apiParam {String} task_state 状态
     * @apiParam {Long} taskid 任务ID
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务信息
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": {
     * "id": 1,
     * "taskName": "任务名称1",
     * "ctType": null,
     * "companyId": 12,
     * "businessName": "测试",
     * "uid": "123456",
     * "taskState": "启动",
     * "status": 1,
     * "beginDate": "2018-05-02",
     * "endDate": "2018-05-02",
     * "totalNumber": 0,
     * "timeQuantum": "18:21-18:22|18:23-18:24|18:25-18:29",
     * "additionalInfo":{
     * "customerLabels":["label1","label2"]
     * }
     * "createDate": "2018-04-23 20:13:57",
     * "modifyDate": "2018-05-04 14:09:43",
     * "taskvalue": null
     * },
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @RequestMapping(value = "/callTask/task/queryBasic")
    public ReturnMsg queryBasic(HttpServletRequest request,
                                @Param("callTaskId") Long callTaskId) {
        TmUser user = GetUid.getUID(request, redisClient);
        ReturnMsg returnMsg = new ReturnMsg();
        CallTaskReturnDto callTaskReturnDto;
        try {
            if (callTaskId == 0) {
                returnMsg.setCode(0);
                returnMsg.setContent(new CallTaskReturnDto());
                return returnMsg;
            }
            CallTask callTask = callTaskService.getCallTaskByid(callTaskId, user.getCompanyId());
            String businessName = null;
            if (0 != callTask.getBusinessId()) {
                TBusiness tBusiness = tBusinessService.selectByPrimaryKey(callTask.getBusinessId());
                if (null != tBusiness) {
                    businessName = tBusiness.getName();
                }
            }
            callTaskReturnDto = new CallTaskReturnDto(callTask.getId(), callTask.getTaskName(), callTask.getCtType(),
                    callTask.getCompanyId(), businessName, callTask.getUid(), callTask.getTaskState(),
                    callTask.getStatus(), (dateToString(callTask.getBeginDate(), "yyyy-MM-dd")),
                    (dateToString(callTask.getEndDate(), "yyyy-MM-dd")), callTask.getTotalNumber(),
                    callTask.getTimeQuantum(), (dateToString(callTask.getCreateDate(), "yyyy-MM-dd HH:mm:ss")),
                    (dateToString(callTask.getModifyDate(), "yyyy-MM-dd HH:mm:ss")));
            if (0 != callTask.getBusinessId()) {
                callTaskReturnDto.setBussinessId(callTask.getBusinessId());
                callTaskReturnDto.setShowExportReport(businessReportService.checkBusinessStatistics(callTask.getBusinessId()));
            }
            callTaskReturnDto.setAutoAgentNum(callTask.getAutoAgentNum());
            callTaskReturnDto.setAutoAgentType(callTask.getAutoAgentType());
            callTaskReturnDto.setSelfAgentIds(callTask.getSelfAgentIds());
            callTaskReturnDto.setAgentType(callTask.getAgentType());
            callTaskReturnDto.setAdditionalInfo(JSONObject.parseObject(callTask.getAdditionalInfo(), CallTaskAdditionalInfo.class));
            returnMsg.setContent(callTaskReturnDto);
            returnMsg.setCode(0);
        } catch (Exception e) {
            logger.error("CallTaskQueryBasic is Error!", e);
            returnMsg.setCode(-1);
            returnMsg.setErrorMsg("查询任务基本信息出错！");
        }

        return returnMsg;
    }


    /**
     * @api {POST} /busiManagement/callTask/task/release 发布任务
     * @apiName release
     * @apiGroup OutboundController
     * @apiParam {String} msgBody {callTaskId:任务ID，businessId:业务标识} 任务信息
     * @apiSuccess {String} code 0:获取成功 1:获取失败***
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": [
     * {
     * "callTaskId": 1,
     * "status": 1,
     * "taskName": "任务名称1",
     * "endDate": "2018-05-02 12:00:00",
     * "taskCount": 11,
     * "finishToday": 8,
     * "intentional": 3,
     * "stillCount": 3,
     * "stillConnect": "72.73%",
     * "sjc": "1524442437000"
     * },
     * {
     * "callTaskId": 2,
     * "status": 1,
     * "taskName": "任务名称2",
     * "endDate": "",
     * "taskCount": 0,
     * "finishToday": 0,
     * "intentional": 0,
     * "stillCount": 0,
     * "stillConnect": "0%",
     * "sjc": "1524534976000"
     * },
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @RequestMapping(value = "/callTask/task/release")
    public ReturnMsg release(HttpServletRequest request, @RequestBody String msgBody) {
        TmUser user = GetUid.getUID(request, redisClient);
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            CallTaskDto callTaskDto = JSONObject.parseObject(msgBody, CallTaskDto.class);
            CallTask callTasks = callTaskService.selectByPrimaryKey(callTaskDto.getCallTaskId());
            // 时间过滤，时间段在早上8点到九点之间可以行
            String pattern = ".*((0[0-7]:)|(2[1-3]:)).*";
            boolean isMatch = Pattern.matches(pattern, callTaskDto.getTimeQuantum());
            if (isMatch && tswitch) {
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("任务可选时间范围：8:00--21:00，请选择正确的呼叫时间！");
                return returnMsg;
            }
            if (!checkTime(callTaskDto.getTimeQuantum())) {
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("请勿选择重复时间段！");
                return returnMsg;
            }
            if (callTasks.getStatus() != 4 && callTasks.getStatus() != 5) {
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("该任务已发布");
                return returnMsg;
            }
            CallTaskAdditionalInfo additionalInfo = callTaskDto.getAdditionalInfo();
            if (null == additionalInfo || null == additionalInfo.getCustomerLabels() || additionalInfo.getCustomerLabels().isEmpty()) {
                List<TDialog> tDialogList = tDialogService.getTDialogListByMap(user.getCompanyId(), callTaskDto.getCallTaskId());
                if (CollectionUtils.isEmpty(tDialogList)) {
                    returnMsg.setCode(1);
                    returnMsg.setErrorMsg("任务中没有号码");
                    return returnMsg;
                }
            }
            CallTask callTask = new CallTask();
            callTask.setId(callTaskDto.getCallTaskId());
            callTask.setTaskName(callTasks.getTaskName());
            callTask.setCompanyId(callTasks.getCompanyId());
            callTask.setBusinessId(callTaskDto.getBusinessId());
            callTask.setBeginDate(stringToDate(callTaskDto.getBeginDate() + " 00:00:00", PATTERN_DATE_2));
            callTask.setAdditionalInfo(null == callTaskDto.additionalInfo ? null : JSONObject.toJSONString(callTaskDto.additionalInfo));
            // 把最大的时间段作为结束时间
            String time = callTaskDto.getTimeQuantum();
            if (!StringUtils.isEmpty(time)) {
                String[] arr = time.split("\\|");
                Date maxDate = stringToDate(callTaskDto.getEndDate() + " " + arr[0].substring(6, 11) + ":00", PATTERN_DATE_2);
                for (String endTime : arr) {
                    Date temp = stringToDate(callTaskDto.getEndDate() + " " + endTime.substring(6, 11) + ":00", PATTERN_DATE_2);
                    if (maxDate.compareTo(temp) < 0) {
                        maxDate = temp;
                    }
                }
                callTask.setEndDate(maxDate);
            } else {
                callTask.setEndDate(stringToDate(callTaskDto.getEndDate() + " 23:59:59", PATTERN_DATE_2));
            }
            callTask.setModifyDate(new Date());
            callTask.setStatus(1);
            callTask.setTaskState("启动");
            callTask.setTimeQuantum(time);
            // 判断当前任务的结束时间，若小于当前时间，则不可发布
            if (callTask.getEndDate().compareTo(new Date()) <= 0) {
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("当前任务时间已过，请选择正确的任务时间！");
                return returnMsg;
            }
            // 当前任务的号码个数
            int totalNumber = tDialogService.countDialogByTaskId(user.getCompanyId(), callTaskDto.getCallTaskId());
            callTask.setTotalNumber(totalNumber);
            // 保存任务发布时的坐席发布情况
            callTask.setAgentType(callTaskDto.getAgentType());
            List<TCallAgent> callAgentList = new ArrayList<>();
            //随机坐席
            if (callTaskDto.getAgentType() == 1) {
                //选的数量
                int autoAgentNum = callTaskDto.getAutoAgentNum();
                // 号码类型
                int autoAgentType = callTaskDto.getAutoAgentType();
                //坐席
                List<TCallAgent> tCallAgentList = tCallAgentService.getTCAllAgentListByupdate(user.getCompanyId(), autoAgentType, autoAgentNum);
                if (CollectionUtils.isEmpty(tCallAgentList)) {
                    returnMsg.setCode(1);
                    returnMsg.setErrorMsg("坐席不足！");
                    return returnMsg;
                }
                for (TCallAgent tc : tCallAgentList) {
                    tc.setIsUsed(1);
                    tc.setUsedTaskid(callTask.getId());
                }
                callAgentList = tCallAgentList;
                callTask.setAutoAgentType(callTaskDto.getAutoAgentType());
                callTask.setAutoAgentNum(callTaskDto.getAutoAgentNum());
                //自选坐席
            } else if (callTaskDto.getAgentType() == 2) {
                String[] arr = callTaskDto.getSelfAgentIds().split("\\|");
                List<TCallAgent> tCallAgents = tCallAgentService.selectByIdArray(arr);
                StringBuilder usedAgent = new StringBuilder("");
                // 检测坐席是够被占用
                for (TCallAgent tc : tCallAgents) {
                    if (tc.getIsUsed() == 1) {
                        usedAgent.append(tc.getOutNumber()).append(",");
                    }
                    tc.setIsUsed(1);
                    tc.setUsedTaskid(callTask.getId());
                }
                if (!StringUtils.isEmpty(usedAgent.toString())) {
                    returnMsg.setCode(1);
                    returnMsg.setErrorMsg("坐席" + usedAgent.toString() + "被占用！");
                    return returnMsg;
                }
                callAgentList = tCallAgents;
                // 给坐席设置给当前任务
                callTask.setSelfAgentIds(callTaskDto.getSelfAgentIds());
            }
            callTaskService.updateCallTaskStatus(callTask, callAgentList);
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.STATUS_1, String.valueOf(callTaskDto.getCallTaskId()), "/callTask/task/release",
                    CommonConstant.PUBLISH_TASK, msgBody, OperateType.CREATE, "任务：" + callTasks.getTaskName(),
                    null, null);
            //返回List
            returnMsg.setContent(callTaskService.getIndex(user.getUserid(), user.getCompanyId(), null, null, 1, 10));
            returnMsg.setCode(0);
            startTask(callTask.getAdditionalInfo(), callTask.getId(), user.getCompanyId(), redisClient, 1);
            String preDateKey = String.format("prehandle:%s:%s:date", user.getCompanyId(), callTaskDto.getCallTaskId());
            redisClient.set(2, preDateKey, simpleDateFormat5.format(callTasks.getCreateDate()));

            //短信不足的提示
            // 是否有发短信的节点
            int falg = tBusinessService.countSmsProbably(callTaskDto.getBusinessId());
            if (falg > 0) {
                // 当前账号剩余短信
                TAccSmsaccount tAccSmsaccount = smsMsgService.getGeneralSituation(user.getCompanyId());
                if (null != tAccSmsaccount) {
                    if (tAccSmsaccount.getBalance() < callTask.getTotalNumber()) {
                        returnMsg.setErrorMsg("您的可用短信数不足，请尽快充值");
                    }
                }
            }
        } catch (Exception e) {
            returnMsg.setCode(1);
            returnMsg.setErrorMsg(e.getMessage());
            logger.error("出错了：", e);
            return returnMsg;
        }
        return returnMsg;
    }

    /**
     * @api {GET} /busiManagement/callTask/task/downloadTemplate 下载号码模板
     * @apiName downloadExcelTemplate
     * @apiGroup OutboundController
     * @apiVersion 1.0.0
     */
    @GetMapping(value = "/callTask/task/downloadTemplate")
    public void downloadExcelTemplate(HttpServletRequest request, HttpServletResponse response) {
        TmUser user = GetUid.getUID(request, redisClient);
        try {
            List<SpeechcraftTagDto> speechcraftTags = speechcraftTagService.getSpeechcraftTagDtoList(user.getCompanyId(), "TTS");
            String titles = "";
            if (!CollectionUtils.isEmpty(speechcraftTags)) {
                for (SpeechcraftTagDto speeTagDto : speechcraftTags) {
                    String tagName = speeTagDto.getTagName();
                    if (!"客户姓名".equals(tagName) && !"号码后四位".equals(tagName)) {
                        titles += tagName + ",";
                    }
                }
            }
            HSSFWorkbook wb;
            if (user.getCompanyId() == 1005) {
                wb = ExcelDataUtil.exportFileModelWithCarnumber();
            } else {
                wb = ExcelDataUtil.exportFileModel(titles);
            }
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment;filename=" + new String("淮安通讯录".getBytes("gbk"), StandardCharsets.ISO_8859_1) + ".xls");
            OutputStream ouputStream = response.getOutputStream();
            wb.write(ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @api {POST} /busiManagement/callTask/cuser/import 导入Excel号码
     * @apiName import
     * @apiGroup OutboundController
     * @apiParam {MultipartFile} file  excel 文件
     * @apiParam {Long} callTaskId  任务ID
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": "2018-05-04 03:37:06xxx.xls",
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @RequestMapping(value = "/callTask/cuser/import")
    public ReturnMsg importExcel(HttpServletRequest request,
                                 @RequestParam("file") MultipartFile file,
                                 @RequestParam("callTaskId") Long callTaskId) {
        ReturnMsg returnMsg = new ReturnMsg();
        TmUser user = GetUid.getUID(request, redisClient);
        if (null == user) {
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("上传号码失败!");
            return returnMsg;
        }
        try {
            List<PersonDto> excelNumslist = ExcelDataUtil.getExcelDatas(file, user.getCompanyId());
            if (CollectionUtils.isEmpty(excelNumslist)) {
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("上传的文件中没有号码,请检查文件！");
                return returnMsg;
            }
            String ct_type = String.format("%s-%s-%s-%s", user.getCompanyId(), user.getUserid(), user.getUsername(), callTaskId.toString());
            String filename = file.getOriginalFilename();
            Set<TmCustomer> tmCustomerSet = new HashSet<>();
            List<TDialog> tDialogSet = new ArrayList<>();
            InputStream is = file.getInputStream();
            String fileName = System.currentTimeMillis() + file.getOriginalFilename();
            Set<PersonDto> setList = new HashSet<>();
            // 记录错误号码
            List<String> errorPhones = new ArrayList<>();
            //检测号码是否是正确格式。
            excelNumslist.forEach(
                    enl -> {
                        if (!StringUtils.isEmpty(enl.getPhone())) {
                            // 校验手机号的格式
                            if (!enl.getPhone().matches(NumberRegexp)) {//错误的手机号
                                errorPhones.add(enl.getPhone());
                            } else { // 添加正确的手机号
                                setList.add(enl);
                            }
                        }
                    }
            );

            // 把客户信息添加到tm_customer表中，不存在就添加，存在就执行修改
            // List<TmCustomer> list1 = tmCustomerService.getTelephonesFromPrivatCustomer(user.getCompanyId());
            //setList.removeAll(list1);
            if (CollectionUtils.isEmpty(setList)) {
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("上传的号码格式全部错误，没有可用的电话号码！");
                return returnMsg;
            }
            //是否有黑名单
            boolean hasBlack = false;
            //是否有老数据
            boolean hasOldData = false;
            List<String> blackPhoneList = blacklistService.getCompanyBlackPhoneList(user.getCompanyId());
            if (!CollectionUtils.isEmpty(blackPhoneList)) {
                hasBlack = true;
            }
            //先获取数据库中是否有数据
            List<String> oldList = tDialogService.getCallTaskPhoeList(user.getCompanyId(), callTaskId);
            if (!CollectionUtils.isEmpty(oldList)) {
                hasOldData = true;
            }
            PersonDto pdo;
            for (Iterator<PersonDto> iterator = setList.iterator(); iterator.hasNext(); ) {
                pdo = iterator.next();
                if (hasBlack) {
                    //去除存在于黑名单中的电话号码
                    if (blackPhoneList.contains(pdo.getPhone())) {
                        iterator.remove();
                        continue;
                    }
                }
                TmCustomer tmCustomer = new TmCustomer(user.getUserid(), pdo.getName(), user.getCompanyId(),
                        "", "", "", pdo.getPhone(), ct_type, 2);
                tmCustomer.setTask_id(callTaskId);
                if (1005 == user.getCompanyId()) {
                    tmCustomer.setCar_numbers(pdo.getCar_numbers());
                }
                tmCustomerSet.add(tmCustomer);
                //500个添加一次
                if (tmCustomerSet.size() >= 500) {
                    tmCustomerService.addCustonerBatch(tmCustomerSet);
                    tmCustomerSet.clear();
                }
                if (hasOldData) {
                    if (oldList.contains(pdo.getPhone())) {
                        continue;
                    }
                }
                TDialog tDialog = new TDialog();
                tDialog.setCompanyId(user.getCompanyId());
                tDialog.setTaskId(callTaskId);
                tDialog.setTelephone(pdo.getPhone());
                tDialog.setStatus(0);
                tDialog.setCreateDate(new Date());
                tDialog.setIsIntention(0);
                if (1005 != user.getCompanyId()) {
                    tDialog.setTtsInfo(pdo.getExtra());
                }
                tDialogSet.add(tDialog);
                if (tDialogSet.size() == 500) {
                    //900个添加一次
                    tDialogService.addDialog(tDialogSet, callTaskId);
                    tDialogSet.clear();
                }
            }
            //添加剩余的用户号码信息
            if (tmCustomerSet.size() > 0) {
                tmCustomerService.addCustonerBatch(tmCustomerSet);
            }
            //添加剩余的电话号码拨打信息
            if (tDialogSet.size() > 0) {
                tDialogService.addDialog(tDialogSet, callTaskId);
            }
            // 上传excel文件
            String filePath = user.getCompanyId() + "/task-" + callTaskId;
            System.out.println(recordAddress + "/" + excelFilePath + "/" + filePath);
            boolean re = fileTransferService.uploadFileToFTP(excelFilePath + "/" + filePath, fileName, is);
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.STATUS_1, callTaskId.toString(), "/callTask/cuser/import",
                    CommonConstant.IMPORT_FILE, filename, OperateType.CREATE, "Ecxel批量导入号码，" + filename + "<a style='color:blue;' href='" + recordAddress + "/" + excelFilePath + "/" + filePath + "/" + fileName + "'>下载</a>",
                    null, null);
            returnMsg.setCode(0);
            // 提示错误的号码
            if (errorPhones.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (String str : errorPhones) {
                    sb.append(str).append("; ");
                }
                returnMsg.setErrorMsg("错误号码未上传，错误号码有：" + sb.toString());
            }
            returnMsg.setContent(simpleDateFormat.format(new Date()) + filename);
            logger.info("导入的号码数量为：" + setList.size());
        } catch (Exception e) {
            logger.info("上传号码出错：", e);
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("上传号码失败");
            return returnMsg;
        }
        return returnMsg;
    }


    /**
     * @api {POST} /busiManagement/callTask/task/start 启动任务
     * @apiName start
     * @apiGroup OutboundController
     * @apiParam {String} msgBody 任务信息
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": [
     * {
     * "callTaskId": 1,
     * "status": 1,
     * "taskName": "任务名称1",
     * "endDate": "2018-05-02 12:00:00",
     * "taskCount": 11,
     * "finishToday": 8,
     * "intentional": 3,
     * "stillCount": 3,
     * "stillConnect": "72.73%",
     * "sjc": "1524442437000"
     * },
     * {
     * "callTaskId": 2,
     * "status": 1,
     * "taskName": "任务名称2",
     * "endDate": "",
     * "taskCount": 0,
     * "finishToday": 0,
     * "intentional": 0,
     * "stillCount": 0,
     * "stillConnect": "0%",
     * "sjc": "1524534976000"
     * },
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @RequestMapping(value = "/callTask/task/start")
    public ReturnMsg start(HttpServletRequest request,
                           @RequestBody String msgBody) {
        TmUser user = GetUid.getUID(request, redisClient);
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            CallTaskDto callTaskDto = JSONObject.parseObject(msgBody, CallTaskDto.class);
            if (!StringUtils.isEmpty(callTaskDto.getTimeQuantum())) {
                String[] arr = callTaskDto.getTimeQuantum().split("\\|");
                Date maxDate = stringToDate(callTaskDto.getEndDate() + " " + arr[0].substring(6, 11) + ":00", PATTERN_DATE_2);
                for (String endTime : arr) {
                    Date temp = stringToDate(callTaskDto.getEndDate() + " " + endTime.substring(6, 11) + ":00", PATTERN_DATE_2);
                    if (maxDate.compareTo(temp) < 0) {
                        maxDate = temp;
                    }
                }
                callTaskDto.setEndDate(simpleDateFormat.format(maxDate));
            } else {
                callTaskDto.setEndDate(simpleDateFormat.format(stringToDate(callTaskDto.getEndDate() + " 23:59:59", PATTERN_DATE_2)));
            }
            // 判断当前任务的结束时间，若小于当前时间，则不可发布
            if (simpleDateFormat.parse(callTaskDto.getEndDate()).compareTo(new Date()) <= 0) {
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("当前任务时间已过，请选择正确的任务时间！");
                return returnMsg;
            }
            // 当前坐席可能全部故障后被暂停，开启前判断坐席时候为可用状态
            int usableNum = tCallAgentService.countByTaskIdAndStatus(callTaskDto.getCallTaskId(), 0L);
            if (usableNum == 0) {
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("当前任务无可用坐席，请联系管理员查看原因！");
                return returnMsg;
            }

            // 修改之前的任务信息
            CallTask old = callTaskService.selectByPrimaryKey(callTaskDto.getCallTaskId());
            CallTaskDto oldInfo = new CallTaskDto();
            oldInfo.setBeginDate(simpleDateFormat3.format(old.getBeginDate()));
            oldInfo.setEndDate(simpleDateFormat3.format(old.getEndDate()));
            if (!StringUtils.isEmpty(callTaskDto.getTimeQuantum())
                    && !StringUtils.isEmpty(old.getTimeQuantum())) {
                oldInfo.setTimeQuantum(old.getTimeQuantum());
            }
            // 当前任务需要拨打的电话数量
            int totalNumber = tDialogService.countDialogByTaskId(user.getCompanyId(), callTaskDto.getCallTaskId());
            callTaskService.updateTask_state(1, callTaskDto.getCallTaskId(), new Date(), simpleDateFormat.parse(callTaskDto.getBeginDate() + " 00:00:00"),
                    simpleDateFormat.parse(callTaskDto.getEndDate()), callTaskDto.getTimeQuantum(), totalNumber, JSON.toJSONString(callTaskDto.getAdditionalInfo()));
            //TODO 设置的坐席数量修改

            returnMsg.setContent("启动成功！");
            callTaskDto.setBeginDate(callTaskDto.getBeginDate().substring(0, 10));
            callTaskDto.setEndDate(callTaskDto.getEndDate().substring(0, 10));
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.RETURN_NOPERMISSION, String.valueOf(callTaskDto.getCallTaskId()), "/callTask/task/start",
                    CommonConstant.START_TASK, "开始任务", OperateType.MODIFY, null, oldInfo, callTaskDto);
            returnMsg.setContent(callTaskService.getIndex(user.getUserid(), user.getCompanyId(), null, null, 1, 10));
            returnMsg.setCode(0);
            startTask(null == callTaskDto.additionalInfo ? null : JSONObject.toJSONString(callTaskDto.additionalInfo), callTaskDto.getCallTaskId(), user.getCompanyId(), redisClient, 4);
        } catch (Exception e) {
            returnMsg.setCode(1);
            returnMsg.setErrorMsg(e.getMessage());
            return returnMsg;
        }
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/callTask/task/sotop 暂停任务
     * @apiName sotop
     * @apiGroup OutboundController
     * @apiParam {Long} callTaskId  任务ID
     * @apiParam {String} type  操作类型 pause 暂停， stop 停止。
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": [
     * {
     * "callTaskId": 1,
     * "status": 1,
     * "taskName": "任务名称1",
     * "endDate": "2018-05-02 12:00:00",
     * "taskCount": 11,
     * "finishToday": 8,
     * "intentional": 3,
     * "stillCount": 3,
     * "stillConnect": "72.73%",
     * "sjc": "1524442437000"
     * },
     * {
     * "callTaskId": 2,
     * "status": 1,
     * "taskName": "任务名称2",
     * "endDate": "",
     * "taskCount": 0,
     * "finishToday": 0,
     * "intentional": 0,
     * "stillCount": 0,
     * "stillConnect": "0%",
     * "sjc": "1524534976000"
     * },
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @RequestMapping(value = "/callTask/task/sotop")
    public ReturnMsg stop(HttpServletRequest request,
                          @RequestParam("callTaskId") Long callTaskId,
                          @RequestParam("type") String type) {
        TmUser user = GetUid.getUID(request, redisClient);
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            if ("stop".equals(type)) {
                stopTask(callTaskId, redisClient);
                returnMsg.setContent("手动停止任务成功");
                userOperateRecordService.saveUserOperateRecord(user, CommonConstant.RETURN_NOPERMISSION, callTaskId.toString(), "/callTask/task/sotop",
                        CommonConstant.FINISH_TASK, "手动停止任务", OperateType.CREATE, "手动停止任务", null, null);
            } else {
                pauseTask(callTaskId, redisClient);
                returnMsg.setContent("暂停成功");
                userOperateRecordService.saveUserOperateRecord(user, CommonConstant.RETURN_NOPERMISSION, callTaskId.toString(), "/callTask/task/sotop",
                        CommonConstant.PAUSE_TASK, "暂停任务", OperateType.CREATE, "暂停任务", null, null);
            }
            returnMsg.setContent(callTaskService.getIndex(user.getUserid(), user.getCompanyId(), null, null, 1, 10));
            returnMsg.setCode(0);
        } catch (Exception e) {
            logger.error("CallTask Sotop is Error!", e);
        }
        return returnMsg;
    }


    /**
     * @param response
     * @param dialogId
     * @Author oyxs
     * 下载录音文件
     */
    @RequestMapping(value = "/callTask/dialog/downRecordFile")
    public void downRecordFile(HttpServletResponse response, @RequestParam(name = "taskId", required = false) Long taskId, @RequestParam("dialogId") Long dialogId) {
        OutputStream os = null;
        try {
            TDialog tDialog = tDialogService.selectByPrimaryKey(taskId, dialogId);
            String temp = tDialog.getFile_path();
            if (temp.endsWith(".wav")) {
                InputStream fis = new BufferedInputStream(new FileInputStream(temp));
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
                File file = new File(temp);
                temp = file.getName();
                // 以流的形式下载文件。
                os = new BufferedOutputStream(response.getOutputStream());
                os.write(buffer);
            } else {
                String path = String.format("%s/TASK-%s/%s", tDialog.getCompanyId(), tDialog.getTaskId(), tDialog.getTelephone());
                os = fileTransferService.downloadFileByFTP(path, temp + ".wav", response.getOutputStream());
            }
            response.setHeader("Content-disposition", "attachment;filename=" + new String(temp.getBytes("GBK"), "ISO-8859-1"));
            response.setContentType("application/x-msdownload;charset=UTF-8");
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != os) {
                    os.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * * @api {POST} /busiManagement/callTask/task/queryStat 外呼任务拨打统计
     * * @apiName 外呼任务拨打统计
     *
     * @apiGroup OutboundController
     * @apiParam {Long} id  任务ID
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {"code": 0,
     * "content": [
     * {
     * "count": 2,
     * "status": 0
     * },
     * {
     * "count": 2,
     * "status": 0
     * }
     * ],
     * "token": null,
     * "errorMsg": null
     * }
     */
    @GetMapping(value = "/callTask/task/queryStat")
    public ReturnMsg count(HttpServletRequest request, @RequestParam("id") Long taskId,
                           @RequestParam(value = "type", defaultValue = "all", required = false) String type) {
        TmUser user = GetUid.getUID(request, redisClient);
        ReturnMsg returnMsg = new ReturnMsg();
        if (!StringUtils.isEmpty(user)) {
            Long company_id = user.getCompanyId();
            List<TDialogCount> tDialogCounts = new ArrayList<>();
            // 19:程序异常；20：有意向；22：设备故障；23：无意向；24：待回拨
            Integer[] stateCode = new Integer[]{2, 10, 11, 13, 14, 15, 16, 17, 18, 19, 20, 22, 23, 24};
            List<Integer> list = Arrays.asList(stateCode);
            for (Integer state : list) {
                TDialogCount tc = new TDialogCount();
                tc.setStatus(state);
                tc.setCount(0);
                tDialogCounts.add(tc);
            }
            try {
                List<TDialogCount> allTDialogList = tDialogService.selectAllTDialogStatusById(company_id, taskId);
                // 待回访
                List<TDialogCount> allTDialogList1 = tDialogService.selectAllTDialogIntentionById(company_id, taskId);

                //处理有意向==> 20:a-c
                TDialogCount tDialogCount = tDialogService.selectAllIsIntentionByCompanyIdAndTaskId(company_id, taskId, type);
                allTDialogList1.add(tDialogCount);
                // 获取无意向的数量
                TDialogCount notDialogCount = tDialogService.selectNoIntentionByCompanyIdAndTaskId(company_id, taskId, type);
                allTDialogList1.add(notDialogCount);
                allTDialogList.addAll(allTDialogList1);
                for (TDialogCount dialogCount : tDialogCounts) {
                    for (TDialogCount dialogCount2 : allTDialogList) {
                        if (dialogCount.getStatus() == dialogCount2.getStatus()) {
                            dialogCount.setCount(dialogCount2.getCount());
                        } else if (dialogCount.getStatus() == 11) {
                            // 把号码错误，程序异常，设备故障的算到呼叫失败里面
                            if (dialogCount.getStatus() == 10 || dialogCount2.getStatus() == 18 || dialogCount2.getStatus() == 19 || dialogCount2.getStatus() == 22) {
                                dialogCount.setCount(dialogCount.getCount() + dialogCount2.getCount());
                            }
                        }
                    }
                }
                // 把号码错误，程序异常，设备故障的数据删除
                tDialogCounts.removeIf(
                        tdlog -> tdlog.getStatus() == 10 || tdlog.getStatus() == 19 || tdlog.getStatus() == 22 || tdlog.getStatus() == 18
                );
                TDialogCount visitCount = new TDialogCount();
                visitCount.setStatus(50);
                visitCount.setCount(businessReportService.getVisitSuccessCount(company_id, taskId));
                tDialogCounts.add(visitCount);
                returnMsg.setCode(0);
                returnMsg.setContent(tDialogCounts);
            } catch (Exception e) {
                logger.error("Task state count is Error!");
                return new ReturnMsg(-1, "", "", e.getMessage());
            }
        }
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/callTask/task/queryDetail 任务明细信息
     * @apiName 任务明细信息
     * @apiGroup OutboundController
     * @apiParam {Long} id  任务ID
     * @apiParam {String} beginDate  开始时间
     * @apiParam {String} endDate  结束时间
     * @apiParam {String} intentionLevel  意向等级
     * @apiParam {String} ctAddress  城市查找
     * @apiParam {String} ctPosition  档位查找
     * @apiParam {String} telephone  联系号码
     * @apiParam {String} status  工作状态
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * <p>
     * "code": 0,
     * "content": {
     * "pageIndex": 1,
     * "totalPage": 0,
     * " tdialogModelDtoList": [
     * "id": 111271,
     * "ctName": "啊啊",
     * "ctPhone": "13392195945",
     * " isIntention": 0,
     * "isIntentionInfo": "未拨打",
     * "beginDate": null,
     * "duration": 0,
     * "agentNum": 0,
     * "intentionLevel": null,
     * "ctAddress": ""
     * }
     * ],
     * "pageSize": 4
     * },
     * "token": null,
     * "errorMsg": null
     * }
     */
    @PostMapping(value = "/callTask/task/queryDetail")
    public ReturnMsg selectAllTDialogTDialogModelDto(HttpServletRequest request,
                                                     @RequestParam Long id,
                                                     @RequestParam(value = "beginDate", required = false) String beginDate,
                                                     @RequestParam(value = "endDate", required = false) String endDate,
                                                     @RequestParam(value = "intentionLevel", required = false) String intentionLevel,
                                                     @RequestParam(value = "ctAddress", required = false) String ctAddress,
                                                     @RequestParam(value = "ctPosition", required = false) String ctPosition,
                                                     @RequestParam(value = "status", required = false) String status,
                                                     @RequestParam(value = "pageIndex") int pageIndex,
                                                     @RequestParam int pageSize,
                                                     @RequestParam(value = "type", required = false, defaultValue = "all") String type,
                                                     @RequestParam(value = "telephone", required = false) String telephone) {
        TmUser user = GetUid.getUID(request, redisClient);
        if (!StringUtils.isEmpty(user)) {
            ReturnMsg returnMsg = new ReturnMsg();
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("type", type);
            if (!StringUtils.isEmpty(beginDate)) {
                map.put("beginDate", beginDate + " 00:00:00");
            }
            if (!StringUtils.isEmpty(endDate)) {
                map.put("endDate", endDate + " 23:59:59");
            }
            if (!StringUtils.isEmpty(intentionLevel)) {
                map.put("intentionLevel", intentionLevel);
            }
            if (!StringUtils.isEmpty(ctAddress)) {
                map.put("ctAddress", ctAddress);
            }
            if (!StringUtils.isEmpty(ctPosition)) {
                map.put("ctPosition", ctPosition);
            }
            if (!StringUtils.isEmpty(telephone)) {
                telephone = telephone.replaceAll("\\D", "");
                map.put("telephone", telephone);
            }
            if (status != null) {
                if (Integer.valueOf(status) == 50) {
                    map.put("tableName", businessReportService.getReportTableName(id));
                } else if (Integer.valueOf(status) < 20) {
                    map.put("status", status);
                } else if (Integer.valueOf(status) > 20) {
                    map.put("isIntention", status);
                } else if (Integer.valueOf(status) == 20) {
                    map.put("allIsIntention", "true");
                }
            }
            Page<TDialogModelDto> tdialogModelDtoList = tDialogService.selectAllTDialogTDialogModelDto(pageIndex, pageSize, map);
            returnMsg.setCode(0);
            if (tdialogModelDtoList != null) {
                returnMsg.setContent(tdialogModelDtoList);
            } else {
                returnMsg.setErrorMsg("查询结果为空！");
                returnMsg.setContent(new ArrayList<>());
            }
            return returnMsg;
        }
        return new ReturnMsg(-1, "", "", "未知错误！");
    }

    /**
     * @api {POST} /busiManagement/callTask/agent/disableOrNot 启用/停用坐席
     * @apiName 坐席信息查询
     * @apiGroup OutboundController
     * @apiParam {Long} id  坐席Id
     * @apiParam {String} agentNum  坐席号码
     * @apiParam {String} agentSatus  坐席工作状态
     * @apiParam {Long} usedtaskid  任务Id
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": [
     * {
     * "id": 1,
     * "agentNum": "8080-8081",
     * "agentSatus": 1,
     * "ctName": ""
     * }
     * ],
     * "token": null,
     * "* errorMsg": null
     * }
     */

    @PostMapping(value = "/callTask/agent/disableOrNot")
    public ReturnMsg isUseTCallAgent(HttpServletRequest request,
                                     @RequestParam("id") String id,
                                     @RequestParam("usedtaskid") String usedtaskid,
                                     @RequestParam("status") String status, String agentNum, String agentSatus) {
        ReturnMsg returnMsg = new ReturnMsg();
        TmUser user = GetUid.getUID(request, redisClient);
        if (StringUtils.isEmpty(user)) {
            returnMsg.setCode(1);
            returnMsg.setContent("登录已过期，请重新登录！");
            return returnMsg;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("usedtaskid", Long.valueOf(usedtaskid));
        if (agentNum != null) {
            map.put("agentNum", agentNum);
        }
        if (agentSatus != null) {
            map.put("agentSatus", agentSatus);
        }
        int statusValue;
        if (Integer.valueOf(status) == 1) {
            statusValue = 2;
        } else {
            statusValue = 0;
        }
        String[] strings = id.split(",");
        if (strings.length > 0) {
            for (String ids : strings) {
                try {
                    Long ls = Long.valueOf(ids);
                    TCallAgent tCallAgent = tCallAgentService.selectByPrimaryKey(ls);
                    if (statusValue != tCallAgent.getStatus()) {
                        tCallAgent.setStatus(statusValue);
                    }
                    tCallAgentService.updateByPrimaryKey(tCallAgent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            List<TCallAgentSelectDto> tCallAgentSelectDtoList = tCallAgentService.selectTCallAgentSelect(map);
            returnMsg.setCode(0);
            returnMsg.setContent(tCallAgentSelectDtoList);
        } else {
            returnMsg.setCode(1);
            returnMsg.setContent("选择的坐席数为0，请重新选择坐席！");
        }
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/callTask/agent/queryDetail 坐席信息查询
     * * @apiName 坐席信息查询
     * @apiGroup OutboundController
     * @apiParam {Long} id  任务ID
     * @apiParam {String} agentNum  坐席号码
     * @apiParam {String} agentSatus  坐席工作状态
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": [
     * {
     * "id": 1,
     * "agentNum": "8080-8081",
     * "agentSatus": 1,
     * "ctName": ""
     * }
     * ],
     * "token": null,
     * "* errorMsg": null
     * }
     */
    @PostMapping(value = "/callTask/agent/queryDetail")
    public ReturnMsg selectTCallAgent(@RequestParam("id") String id,
                                      @RequestParam(value = "agentNum", required = false) String agentNum,
                                      @RequestParam(value = "agentSatus", required = false) String agentSatus) {
        ReturnMsg returnMsg = new ReturnMsg();
        Map<String, Object> map = new HashMap<>();
        map.put("usedtaskid", Long.valueOf(id));
        if (!StringUtils.isEmpty(agentNum)) {
            map.put("agentNum", agentNum);
        }
        if (!StringUtils.isEmpty(agentSatus)) {
            map.put("agentSatus", agentSatus);
        }
        List<TCallAgentSelectDto> tCallAgentSelectDtoList = tCallAgentService.selectTCallAgentSelect(map);
        returnMsg.setCode(0);
        returnMsg.setContent(tCallAgentSelectDtoList);
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/callTask/task/deletetTask 删除未发布任务
     * * @apiName 删除未发布任务
     * @apiGroup OutboundController
     * @apiParam {Long} id  任务ID
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": [
     * {
     * "callTaskId": 456911,
     * "status": 3,
     * "taskName": "ff",
     * "endDate": "2018-06-07 11:59:59",
     * "taskCount": 1,
     * "finishToday": 0,
     * "intentional": 0,
     * "stillCount": 1,
     * "stillConnect": "0%"
     * }
     * ],
     * "token": null,
     * "* errorMsg": null
     * }
     */
    @RequestMapping(value = "/callTask/task/deletetTask")
    public ReturnMsg deletetTask(HttpServletRequest request, @RequestParam("id") Long id) {
        ReturnMsg returnMsg = new ReturnMsg();
        TmUser user = GetUid.getUID(request, redisClient);
        try {
            CallTask callTask = callTaskService.selectByPrimaryKey(id);
            if (null == callTask || (callTask.getStatus() != 4 && callTask.getStatus() != 5)) {
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("删除失败!");
                return returnMsg;
            }
            int i = callTaskService.deleteByPrimaryKey(id);
            if (i > 0) {
                returnMsg.setCode(0);
                returnMsg.setContent(callTaskService.getIndex(user.getUserid(), user.getCompanyId(), null, null, 1, 10));
            } else {
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("删除失败!");
            }
        } catch (Exception e) {
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("删除失败");
        }

        return returnMsg;
    }

    //发布任务（启动任务）
    private void startTask(String additionalInfo, Long taskId, Long companyId, RedisClient redisClient, int operaType) {
        try {
            TaskManageDto taskManageDto = new TaskManageDto();
            taskManageDto.taskId = taskId;
            taskManageDto.companyId = companyId;
            taskManageDto.operateType = operaType;
            taskManageDto.telephoneList = new ArrayList<>();
            taskManageDto.additionalInfo = JSONObject.parseObject(additionalInfo, CallTaskAdditionalInfo.class);
            redisClient.lpush("TaskPreprocessQuene", JSONObject.toJSONString(taskManageDto));
            logger.info("发送启动任务消息【{}】", taskManageDto.toString());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    //给任务添加号码
    private void addTaskPhone(Long taskId, Long companyId, Collection<String> phoneList, RedisClient redisClient) {
        try {
            TaskManageDto taskManageDto = new TaskManageDto();
            taskManageDto.taskId = taskId;
            taskManageDto.companyId = companyId;
            taskManageDto.operateType = 2;
            taskManageDto.telephoneList = phoneList;
            redisClient.lpush("TaskPreprocessQuene", JSONObject.toJSONString(taskManageDto));
            logger.info("发送添加任务号码消息【{}】", taskManageDto.toString());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    //手动暂停任务
    private void pauseTask(Long taskId, RedisClient redisClient) {
        try {
            TaskManageDto taskManageDto = new TaskManageDto();
            taskManageDto.taskId = taskId;
            taskManageDto.operateType = 3;
            taskManageDto.telephoneList = new ArrayList<>();
            redisClient.lpush("TaskManageQueue", JSONObject.toJSONString(taskManageDto));
            logger.info("发送暂停任务消息：【{}】", taskManageDto.toString());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    //手动结束任务
    private void stopTask(Long taskId, RedisClient redisClient) {
        try {
            TaskManageDto taskManageDto = new TaskManageDto();
            taskManageDto.taskId = taskId;
            taskManageDto.operateType = 0;
            taskManageDto.telephoneList = new ArrayList<>();
            redisClient.lpush("TaskManageQueue", JSONObject.toJSONString(taskManageDto));
            logger.info("发送停止任务消息：【{}】", taskManageDto.toString());
        } catch (Exception e) {
            logger.error("StopTask is Error!", e);
        }
    }

    /**
     * @api {POST} /busiManagement/callTask/task/createTestOutcallTask 发布一个测试话语 (已弃用，不用再维护)
     * * @apiName 发布一个测试话语
     * @apiGroup OutboundController
     * @apiParam {Long} id  任务ID
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": [
     * {
     * "callTaskId": 456911,
     * "status": 3,
     * "taskName": "ff",
     * "endDate": "2018-06-07 11:59:59",
     * "taskCount": 1,
     * "finishToday": 0,
     * "intentional": 0,
     * "stillCount": 1,
     * "stillConnect": "0%"
     * }
     * ],
     * "token": null,
     * "* errorMsg": null
     * }
     */
    @RequestMapping(value = "/callTask/task/createTestOutcallTask", method = RequestMethod.POST)
    public ReturnMsg createTestOutcallTask(HttpServletRequest request, @Param(value = "telephone") String telephone,
                                           @Param(value = "businessId") Long businessId) {
        ReturnMsg returnMsg = new ReturnMsg();
        TmUser user = GetUid.getUID(request, redisClient);
        Long taskId = 0L;
        logger.info("创建任务手机参数:" + telephone);
        logger.info("创建任务业务参数:" + businessId);
        if (StringUtils.isEmpty(telephone)) {
            returnMsg.setCode(40002);
            returnMsg.setErrorMsg("号码不能为空");
            return returnMsg;
        }
        //1.先创建任务
        String tackName = "Test" + System.currentTimeMillis() + (int) (Math.random() * 9000) + 1000;
        List<CallTask> callTaskList = callTaskService.getCallTaskListByCompany_id(user.getCompanyId(), tackName);
        try {
            if (callTaskList.size() > 0) {
                returnMsg.setCode(40003);
                returnMsg.setErrorMsg("已存在相同的任务名称！");
                return returnMsg;
            }
            CallTask callTask = new CallTask();
            callTask.setTaskName(tackName);
            callTask.setCreateDate(new Date());
            callTask.setCompanyId(user.getCompanyId());
            callTask.setUid(user.getUserid());
            callTask.setTaskState("未运行");
            callTask.setStatus(4);
            callTask.setTotalNumber(0);
            callTask.setBusinessId(1L);
            callTask.setBeginDate(new Date());
            callTask.setEndDate(new Date());
            callTask.setModifyDate(new Date());
            callTask.setIsTemp(1);
            callTaskService.insert(callTask);
            taskId = callTask.getId();
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.RETURN_NOPERMISSION, taskId.toString(), "/callTask/task/createTestOutcallTask",
                    CommonConstant.CREATION_TASK, tackName, OperateType.CREATE, "测试智库任务",
                    null, null);
        } catch (Exception e) {
            logger.info("创建任务出错：", e);
            returnMsg.setErrorMsg("创建任务出错：" + e);
            returnMsg.setCode(40004);
        }
        //2.添加号码
        try {
            List<String> phoneList = new ArrayList<>();
            Set<TmCustomer> tmCustomerSet = new HashSet<>();
            ArrayList<TDialog> tDialogSet = new ArrayList<>();
            TmCustomer customer;
            TDialog tDialog;
            StringBuffer phones = new StringBuffer();
            //先查询出任务是否是未运行
            CallTask callTask = callTaskService.selectByPrimaryKey(taskId);

            String ct_type = user.getCompanyId() + "-" + user.getUserid() + "-" + user.getUsername() + "-" + taskId;

            // 记录错误号码
            String regEx = "(^(0\\d{2,3})(-)?\\d{7,8})$|(1[0-9]{10})|(\\d{7,8})";
            // 校验手机号的格式
            if (!telephone.matches(regEx)) {
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("您输入的号码格式有误，请输入正确的号码");
                return returnMsg;
            }
            //获取当前任务是否有号码！
            List<String> oldList = tDialogService.getCallTaskPhoeList(user.getCompanyId(), taskId);
            if (oldList.size() > 0) {
                //如果数据库中有数据
                //对比去除相同的数据
                for (String num : oldList) {
                    if (num.equals(telephone)) {
                        telephone = "";
                    }
                }
            }
            if (!StringUtils.isEmpty(telephone)) {
                phoneList.add(telephone);
                customer = new TmCustomer();
                customer.setCompanyId(user.getCompanyId());
                customer.setCtid(user.getUserid());
                customer.setCtPhone(telephone);
                customer.setCtType(ct_type);
                customer.setTask_id(taskId);
                // 根据公司id和电话去出重复数据
                TmCustomer tmCustomerOld = tmCustomerService.selectByTelephoneAndCompanyId(customer.getCtPhone(), String.valueOf(customer.getCompanyId()));
                if (null == tmCustomerOld) {
                    tmCustomerSet.add(customer);
                }
                //还要添加一张表
                tDialog = new TDialog();
                // 不优先
                tDialog.setPriority(0);
                tDialog.setTelephone(telephone);
                tDialog.setCreateDate(new Date());
                tDialog.setCompanyId(user.getCompanyId());
                tDialog.setTaskId(taskId);
                tDialog.setStatus(0);
                tDialog.setIsIntention(0);
                tDialogSet.add(tDialog);
                if (tmCustomerSet.size() > 0) { //添加剩余的
                    tmCustomerService.addCustomer(tmCustomerSet);
                }
                if (tDialogSet.size() > 0) { //添加剩余的
                    tDialogService.addDialog(tDialogSet, taskId);
                }
                addTaskPhone(taskId, user.getCompanyId(), phoneList, redisClient);
            }
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.RETURN_NOPERMISSION, taskId.toString(), "/callTask/task/createTestOutcallTask",
                    CommonConstant.ADD_PHONE, phones.toString(), OperateType.CREATE, "测试智库添加号码：" + phones.toString(),
                    null, null);
        } catch (Exception e) {
            logger.info("添加号码出错：", e);
            returnMsg.setCode(-1);
            returnMsg.setErrorMsg(e.getMessage());
            return returnMsg;
        }
        //3.发布任务
        try {
            CallTask callTasks = callTaskService.selectByPrimaryKey(taskId);
            if (callTasks.getStatus() != 4 && callTasks.getStatus() != 5) {
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("该任务已发布");
                return returnMsg;
            }
            List<TDialog> tDialogList = tDialogService.getTDialogListByMap(user.getCompanyId(), taskId);
            if (tDialogList == null || tDialogList.size() == 0) {
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("任务中没有号码");
                return returnMsg;
            }
            CallTask callTask = new CallTask();
            callTask.setId(taskId);
            callTask.setBusinessId(businessId);
            Date now = new Date();
            callTask.setBeginDate(now);
            callTask.setEndDate(new Date(now.getTime() + 600000));
            callTask.setModifyDate(now);
            callTask.setStatus(1);
            callTask.setTaskState("启动");
            callTask.setTimeQuantum(simpleDateFormat2.format(callTask.getBeginDate()) + "-" + simpleDateFormat2.format(callTask.getEndDate()));
            TCallAgent callAgent;
            // 判断当前任务的结束时间，若小于当前时间，则不可发布
            if (callTask.getEndDate().compareTo(new Date()) <= 0) {
                returnMsg.setCode(40007);
                returnMsg.setErrorMsg("当前任务时间已过，请选择正确的任务时间！");
                return returnMsg;
            }
            //随机
            //选的数量
            int autoAgentNum = 1;
            // 号码类型
            int autoAgentType = 3;
            //坐席
            List<TCallAgent> tCallAgentList = tCallAgentService.getTCAllAgentListByupdate(user.getCompanyId(),
                    autoAgentType, autoAgentNum);
            if (tCallAgentList.size() == 0) {
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("坐席不足！");
                return returnMsg;
            }
            for (TCallAgent tc : tCallAgentList) {
                callAgent = tCallAgentService.selectByPrimaryKey(tc.getId());
                callAgent.setIsUsed(1);
                callAgent.setUsedTaskid(callTask.getId());
                tCallAgentService.updateByPrimaryKeySelective(callAgent);
            }
            callTaskService.updateByPrimaryKeySelective(callTask);
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.RETURN_NOPERMISSION, taskId.toString(), "/callTask/task/createTestOutcallTask",
                    CommonConstant.PUBLISH_TASK, telephone + "-" + businessId, OperateType.CREATE, "测试智库发布任务：" + telephone + "-" + businessId,
                    null, null);
            returnMsg.setCode(0);
            returnMsg.setContent("成功");
            startTask(null, callTask.getId(), user.getCompanyId(), redisClient, 1);
        } catch (Exception e) {
            returnMsg.setCode(-1);
            returnMsg.setErrorMsg(e.getMessage());
            logger.info("发布任务出错：", e);
            return returnMsg;
        }
        return returnMsg;
    }

    private boolean smsRemind(Long companyId, String userId) {
        TAccSmsaccount tAccSmsaccount = smsMsgService.getGeneralSituation(companyId);
        if (null != tAccSmsaccount) {
            // 正在运行中的任务
            List<CallTask> callTasks = callTaskService.getCallTaskBystatus(userId, 1);
            if (null != callTasks && callTasks.size() > 0) {
                List<String> calltasks = new ArrayList<>();
                List<Long> taskIds = new ArrayList<>();
                for (CallTask c : callTasks) {
                    // 判断当前任务是否有配置短信节点，大于0就是有
                    int falg = tBusinessService.countSmsProbably(c.getBusinessId());
                    if (falg > 0) {
                        taskIds.add(c.getId());
                        calltasks.add(simpleDateFormat4.format(c.getCreateDate()));
                    }
                }
                int undoNum = 0;
                // 查看当前商户的在运行任务的未拨打电话个数
                if (taskIds.size() > 0 && calltasks.size() > 0) {
                    undoNum = tDialogService.countUndoTelephone(taskIds, calltasks);
                }
                return tAccSmsaccount.getBalance() < undoNum;
            }
        }
        return false;
    }

    /**
     * @api {POST} /busiManagement/callTask/task/saveTaskInfo 保存任务的信息
     * * @apiName 保存任务的信息
     * @apiGroup OutboundController
     * @apiParam {Long} agentType  任务发布类型：1: 随机分配；2: 自选坐席
     * @apiSuccess {int} autoAgentNum 随机拨打的坐席数量
     * @apiSuccess {int} autoAgentType  1:固话; 2:手机; 3:手机+固话
     * @apiSuccess {String} beginDate 开始时间
     * @apiSuccess {String} endDate 结束时间
     * @apiSuccess {int} businessId 智库id
     * @apiSuccess {int} callTaskId 任务id
     * @apiSuccess {String} callTaskName 任务名称
     * @apiSuccess {String} timeQuantum 任务时间段
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": [
     * {
     * "callTaskId": 1,
     * "status": 1,
     * "taskName": "任务名称1",
     * "endDate": "2018-05-02 12:00:00",
     * "taskCount": 11,
     * "finishToday": 8,
     * "intentional": 3,
     * "stillCount": 3,
     * "stillConnect": "72.73%",
     * "sjc": "1524442437000"
     * },
     * {
     * "callTaskId": 2,
     * "status": 1,
     * "taskName": "任务名称2",
     * "endDate": "",
     * "taskCount": 0,
     * "finishToday": 0,
     * "intentional": 0,
     * "stillCount": 0,
     * "stillConnect": "0%",
     * "sjc": "1524534976000"
     * },
     * "token": null,
     * "errorMsg": null
     * }
     */
    @RequestMapping(value = "/callTask/task/saveTaskInfo", method = RequestMethod.POST)
    public ReturnMsg saveTaskInfo(HttpServletRequest request, @RequestBody String msgBody) {
        ReturnMsg returnMsg = new ReturnMsg();
        TmUser user = GetUid.getUID(request, redisClient);
        if (StringUtils.isEmpty(user)) {
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("登录已过期，请重新登录！");
            return returnMsg;
        }
        try {
            CallTaskDto callTaskDto = JSONObject.parseObject(msgBody, CallTaskDto.class);
            if (!StringUtils.isEmpty(callTaskDto.getCallTaskName())) {
                Long taskid = null;
                if (callTaskDto.getCallTaskId() != 0) {
                    taskid = callTaskDto.getCallTaskId();
                }
                List<CallTask> callTaskList = callTaskService.getCallTaskListByCompany_id2(user.getCompanyId(), callTaskDto.getCallTaskName(), taskid);
                if (callTaskList.size() > 0) {
                    returnMsg.setCode(1);
                    returnMsg.setErrorMsg("已存在相同的任务名称！");
                    return returnMsg;
                }
            }
            CallTask callTasks = callTaskService.selectByPrimaryKey(callTaskDto.getCallTaskId());
            if (null != callTasks && callTasks.getStatus() != 4 && callTasks.getStatus() != 5) {
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("该任务已发布");
                return returnMsg;
            }
            //设置时间
            CallTask callTask = new CallTask();
            callTask.setId(callTaskDto.getCallTaskId());
            callTask.setTaskName(callTaskDto.getCallTaskName() == null ? "" : callTaskDto.getCallTaskName());
            callTask.setBusinessId(callTaskDto.getBusinessId());
            callTask.setBeginDate(stringToDate(callTaskDto.getBeginDate() + " 00:00:00", PATTERN_DATE_2));
            if (callTaskDto.getCallTaskId() > 0L) {
                // 当前任务的号码个数
                int totalNumber = tDialogService.countDialogByTaskId(user.getCompanyId(), callTaskDto.getCallTaskId());
                callTask.setTotalNumber(totalNumber);
            }
            // 把最大的时间段作为结束时间
            String time = callTaskDto.getTimeQuantum();
            if (!StringUtils.isEmpty(time)) {
                String[] arr = time.split("\\|");
                Date maxDate = stringToDate(callTaskDto.getEndDate() + " " + arr[0].substring(6, 11) + ":00", PATTERN_DATE_2);
                for (String endTime : arr) {
                    Date temp = stringToDate(callTaskDto.getEndDate() + " " + endTime.substring(6, 11) + ":00", PATTERN_DATE_2);
                    if (maxDate.compareTo(temp) < 0) {
                        maxDate = temp;
                    }
                }
                callTask.setEndDate(maxDate);
            } else {
                callTask.setEndDate(stringToDate(callTaskDto.getEndDate() + " 23:59:59", PATTERN_DATE_2));
            }
            Date now = new Date();
            callTask.setCreateDate(now);
            callTask.setModifyDate(now);
            callTask.setCompanyId(user.getCompanyId());
            callTask.setUid(user.getUserid());
            callTask.setTaskState("未运行");
            callTask.setStatus(4);
            callTask.setAdditionalInfo(JSONObject.toJSONString(callTaskDto.getAdditionalInfo()));
            callTask.setTimeQuantum(StringUtils.isEmpty(time) ? null : time);
            callTask.setAgentType(callTaskDto.getAgentType());
            if (callTaskDto.getAgentType() == 1) {
                callTask.setAutoAgentType(callTaskDto.getAutoAgentType());
                callTask.setAutoAgentNum(callTaskDto.getAutoAgentNum());
            } else if (callTaskDto.getAgentType() == 2) {
                callTask.setSelfAgentIds(callTaskDto.getSelfAgentIds());
            }
            callTaskService.updateOrInsertByPrimaryKeySelective(callTask);
            userOperateRecordService.saveUserOperateRecord(user, CommonConstant.STATUS_1, String.valueOf(callTask.getId()), "/busiManagement/callTask/task/saveTaskInfo",
                    CommonConstant.CREATE_TASK, msgBody, callTasks == null ? OperateType.CREATE : OperateType.MODIFY, "任务修改", callTasks, callTask);
            //返回List
            returnMsg.setContent(callTaskService.getIndex(user.getUserid(), user.getCompanyId(), null, null, 1, 10));
            returnMsg.setCode(0);
        } catch (Exception e) {
            returnMsg.setCode(1);
            returnMsg.setErrorMsg(e.getMessage());
            logger.info("出错了：", e);
        }
        return returnMsg;
    }

    @RequestMapping(value = "/sendMessage")
    public void testSendWeixinMessage() {
        try {
            String result = RestClientUtil.getData("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx854c0228c7027f99&secret=481aac8e8653666d0a28a5183eb2163d", "");
            System.out.println(result);
            if (result.contains("errcode")) {
                System.out.println(result);
            } else {
                JSONObject json = JSONObject.parseObject(result);
                String acctoken = json.getString("access_token");
                JSONObject para = new JSONObject();
                para.put("touser", "ojSQN1HrmsepHzAEiqD8jWFccHRk");
                para.put("template_id", "YULl9P5QpUfx8Ua7xqRggibcvYcQJ9BX01yDQO2mrkE");
                //data
                JSONObject dataJson = new JSONObject();
                //first
                JSONObject fstJson = new JSONObject();
                fstJson.put("value", "你好，您的账号已登录:");
                //keyword1
                JSONObject k1Json = new JSONObject();
                k1Json.put("value", "2018.8.22 10:04");
                //keyword2
                JSONObject k2Json = new JSONObject();
                k2Json.put("value", "127.0.0.1");
                //remark
                JSONObject rmkJson = new JSONObject();
                rmkJson.put("value", "备注：如果本次登录不是您本人所为，说明您的帐号已经被盗！为减少您的损失，请点击本条消息，立即锁定帐号。");
                dataJson.put("first", fstJson); //模版中对应的字段
                dataJson.put("time", k1Json);
                dataJson.put("ip", k2Json);
                dataJson.put("reason", rmkJson);
                para.put("data", dataJson);
                String re = RestClientUtil.postData("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + acctoken, para.toString());
                System.out.println("发送结果：" + re);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 时间范围比较
     *
     * @param time
     * @return
     */
    private static boolean checkTime(String time) {
        try {
            if (!StringUtils.isEmpty(time)) {
                String[] arrs = time.split("\\|");
                if (arrs.length > 1) {
                    for (int i = 1; i < arrs.length; i++) {
                        String[] tempTime = arrs[i].split("-");
                        Date begin = simpleDateFormat.parse("2018-01-01 " + tempTime[0] + ":00");
                        Date end = simpleDateFormat.parse("2018-01-01 " + tempTime[1] + ":00");
                        for (int j = 0; j < i; j++) {
                            String[] timeTemp = arrs[j].split("-");
                            Date begindata = simpleDateFormat.parse("2018-01-01 " + timeTemp[0] + ":00");
                            Date enddata = simpleDateFormat.parse("2018-01-01 " + timeTemp[1] + ":00");
                            // 时间段不可以有交集
                            if (begin.compareTo(begindata) >= 0 && begin.compareTo(enddata) < 0
                                    || (end.compareTo(begindata) > 0 && end.compareTo(enddata) <= 0)) {
                                return false;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("时间比较出错：", e);
        }
        return true;
    }


    /**
     * @api {POST} /busiManagement/callIn/saveCallInConfig 保存呼入配置的信息
     * @apiName 保存呼入配置的信息
     * @apiGroup OutboundController
     * @apiParam {int} agentNumber 坐席号码
     * @apiParam {int} businessId 智库id
     * @apiParam {String} callInConfigName 呼入配置名称
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0
     * "token": null,
     * "errorMsg": null
     * }
     */
    @RequestMapping(value = "/callIn/saveCallInConfig", method = RequestMethod.POST)
    public ReturnMsg saveCallin(HttpServletRequest request,
                                @RequestParam("agentNumber") String agentNumber,
                                @RequestParam("businessId") Long businessId,
                                @RequestParam("callInConfigName") String callInConfigName) {
        ReturnMsg returnMsg = new ReturnMsg();
        TmUser user = GetUid.getUID(request, redisClient);
        if (StringUtils.isEmpty(user)) {
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("登录已过期，请重新登录！");
            return returnMsg;
        }
        TCallAgent tCallAgent = new TCallAgent();
        tCallAgent.setCompanyId(user.getCompanyId());
        tCallAgent.setChannelId(2281L);
        tCallAgent.setNumberType(2);
        int i = tCallAgentService.selectCountOutnumber(agentNumber);
        if (i != 0) {
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("已有此坐席，不可添加！");
            return returnMsg;
        }

        tCallAgent.setOutNumber(agentNumber);
        tCallAgent.setUsedTaskid(businessId);
        tCallAgent.setAgentNum(callInConfigName);
        tCallAgent.setStatus(0);
        tCallAgent.setIsUsed(0);
        tCallAgent.setExtraInfo("好棒的");

        int count = tCallAgentService.insert(tCallAgent);
        returnMsg.setCode(0);
        returnMsg.setErrorMsg("没有此坐席，可添加！");

        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/callIn/deleteCallInConfig 删除呼入配置的信息
     * @apiName 删除呼入配置的信息
     * @apiGroup OutboundController
     * @apiParam {int} id 呼入配置id
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "token": null,
     * "errorMsg": null
     * }
     */
    @RequestMapping(value = "/callIn/deleteCallInConfig")
    public ReturnMsg deleteCallinConfig(HttpServletRequest request, @RequestParam(value = "id", required = false) Long id) {
        ReturnMsg returnMsg = new ReturnMsg();
        TmUser user = GetUid.getUID(request, redisClient);
        TCallAgent tCallAgent = tCallAgentService.selectByPrimaryKey(id);
        if (null == tCallAgent) {
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("此呼入配置不存在!");
            return returnMsg;
        }
        if (!user.getCompanyId().equals(tCallAgent.getCompanyId())) {
            returnMsg.setCode(2);
            returnMsg.setErrorMsg("此配置id不属于你的!");
            /*logger.warn("");*/
            return returnMsg;
        }

        int i = tCallAgentService.deleteByPrimaryKey(id);
        returnMsg.setCode(0);
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/callIn/queryCallInConfigList 查询呼入配置列表
     * @apiName 查询呼入配置列表
     * @apiGroup OutboundController
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": [
     * {
     * "id": 741,
     * "companyId": 1030,
     * "channelId": 741,
     * "agentNum": "123456",
     * "outNumber": "123456",
     * "numberType": 1,
     * "status": 0,
     * "isUsed": 0,
     * "usedTaskid": 0,
     * "tCallChannel": null,
     * "extraInfo": null
     * },
     * {
     * "id": 2343,
     * "companyId": 1030,
     * "channelId": 2281,
     * "agentNum": "大兵",
     * "outNumber": "18750031121",
     * "numberType": 2,
     * "status": 0,
     * "isUsed": 0,
     * "usedTaskid": 42452,
     * "tCallChannel": null,
     * "extraInfo": "好棒的"
     * }
     * ],
     * "token": null,
     * "errorMsg": null
     * }
     */
    @RequestMapping(value = "/callIn/queryCallInConfigList")
    public ReturnMsg queryCallinConfigList(HttpServletRequest request) {
        ReturnMsg returnMsg = new ReturnMsg();
        TmUser user = GetUid.getUID(request, redisClient);
        List<TCallAgent> tCallAgentList = tCallAgentService.queryCallinList(user.getCompanyId());
        returnMsg.setCode(0);
        returnMsg.setContent(tCallAgentList);

        return returnMsg;
    }


    /**
     * @api {POST} /busiManagement/callIn/queryRecordList 查询呼入列表信息
     * @apiName 查询呼入列表信息
     * @apiGroup OutboundController
     * @apiParam {Long} id  任务ID
     * @apiParam {String} beginDate  开始时间
     * @apiParam {String} endDate  结束时间
     * @apiParam {String} intentionLevel  意向等级
     * @apiParam {String} ctAddress  城市查找
     * @apiParam {String} ctPosition  档位查找
     * @apiParam {String} telephone  联系号码
     * @apiParam {String} status  工作状态
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": {
     * "offset": 0,
     * "limit": 5,
     * "total": 2,
     * "size": 5,
     * "pages": 1,
     * "current": 1,
     * "searchCount": true,
     * "openSort": true,
     * "orderByField": null,
     * "records": [
     * {
     * "id": 1191290,
     * "ctName": "",
     * "ctPhone": "13686494551",
     * "isIntentionInfo": "已接通",
     * "beginDate": "2018-11-15 11:58:02",
     * "duration": 148311,
     * "outNumber": "18750031121",
     * "intentionLevel": "无意向",
     * "focusLevel": null,
     * "intentLevel": null,
     * "finalLevel": null,
     * "ctAddress": "",
     * "isIntention": 2,
     * "taskId": 2343,
     * "fileSize": 0,
     * "errormsg": null,
     * "intentionInfo": {
     * "errormsg": null,
     * "intention": "已接通"
     * }
     * },
     * {
     * "id": 1191296,
     * "ctName": "",
     * "ctPhone": "13686494551",
     * "isIntentionInfo": "已接通",
     * "beginDate": "2018-11-15 11:58:02",
     * "duration": 148311,
     * "outNumber": "18750031121",
     * "intentionLevel": "无意向",
     * "focusLevel": null,
     * "intentLevel": null,
     * "finalLevel": null,
     * "ctAddress": "",
     * "isIntention": 2,
     * "taskId": 2343,
     * "fileSize": 0,
     * "errormsg": null,
     * "intentionInfo": {
     * "errormsg": null,
     * "intention": "已接通"
     * }
     * }
     * ],
     * "condition": null,
     * "asc": true,
     * "offsetCurrent": 0
     * },
     * "token": null,
     * "errorMsg": null
     * }
     */
    @PostMapping(value = "/callIn/queryRecordList")
    public ReturnMsg selectCallInTDialogList(HttpServletRequest request,
                                             @RequestParam Long id,
                                             @RequestParam(value = "beginDate", required = false) String beginDate,
                                             @RequestParam(value = "endDate", required = false) String endDate,
                                             @RequestParam(value = "intentionLevel", required = false) String intentionLevel,
                                             @RequestParam(value = "ctAddress", required = false) String ctAddress,
                                             @RequestParam(value = "ctPosition", required = false) String ctPosition,
                                             @RequestParam(value = "status", required = false) String status,
                                             @RequestParam(value = "pageIndex") int pageIndex,
                                             @RequestParam int pageSize,
                                             @RequestParam(value = "type", required = false, defaultValue = "all") String type,
                                             @RequestParam(value = "telephone", required = false) String telephone) {
        TmUser user = GetUid.getUID(request, redisClient);
        if (!StringUtils.isEmpty(user)) {
            ReturnMsg returnMsg = new ReturnMsg();
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("type", type);
            if (!StringUtils.isEmpty(beginDate)) {
                map.put("beginDate", beginDate + " 00:00:00");
            }
            if (!StringUtils.isEmpty(endDate)) {
                map.put("endDate", endDate + " 23:59:59");
            }
            if (!StringUtils.isEmpty(intentionLevel)) {
                map.put("intentionLevel", intentionLevel);
            }
            if (!StringUtils.isEmpty(ctAddress)) {
                map.put("ctAddress", ctAddress);
            }
            if (!StringUtils.isEmpty(ctPosition)) {
                map.put("ctPosition", ctPosition);
            }
            if (!StringUtils.isEmpty(telephone)) {
                telephone = telephone.replaceAll("\\D", "");
                map.put("telephone", telephone);
            }
            if (status != null && Integer.valueOf(status) == 50) {
                map.put("tableName", businessReportService.getReportTableName(id));
            } else {
                if (status != null && Integer.valueOf(status) < 20) {
                    map.put("status", status);
                } else if (status != null && Integer.valueOf(status) > 20) {
                    map.put("isIntention", status);
                } else if (status != null && Integer.valueOf(status) == 20) {
                    map.put("allIsIntention", "true");
                }
            }
            Page<TDialogModelDto> tdialogModelDtoList = tDialogService.selectCallInTDialogList(pageIndex, pageSize, map);
            returnMsg.setCode(0);
            if (tdialogModelDtoList != null) {
                returnMsg.setContent(tdialogModelDtoList);
            } else {
                returnMsg.setErrorMsg("查询结果为空！");
                returnMsg.setContent(new ArrayList<>());
            }
            return returnMsg;
        }
        return new ReturnMsg(-1, "", "", "未知错误！");
    }

    /**
     * 呼入号码18926450343
     * 更改呼入智库
     */
    @RequestMapping(value = "/changeBusinessCallIn")
    public ReturnMsg changeBusinessCallIn(TCallAgent tCallAgent) {
        ReturnMsg returnMsg = new ReturnMsg();
        TCallAgent callAgent = tCallAgentService.getCallInNumber();
        if (callAgent == null) {
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("没有此呼入坐席~~");
            return returnMsg;
        }
        int i = tCallAgentService.updateByCompanyAndUsedtaskId(tCallAgent);
        if (i < 0) {
            returnMsg.setCode(2);
            returnMsg.setErrorMsg("原呼入坐席修改失败~~");
            return returnMsg;
        }
        returnMsg.setCode(0);
        returnMsg.setErrorMsg("原呼入坐席已被修改~~");
        return returnMsg;
    }

    /**
     * 显示呼入热线
     */
    @RequestMapping(value = "/callInAgentList")
    public String callInAgentList(ModelMap modelMap) {
        TCallAgent callAgent = tCallAgentService.getCallInNumber();
        if (callAgent == null) {
            return "";
        }
        modelMap.put("callAgent",callAgent);
        return "callInAgent.jsp";
    }
}