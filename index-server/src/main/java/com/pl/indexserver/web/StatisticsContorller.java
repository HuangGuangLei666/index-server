package com.pl.indexserver.web;

import com.pl.indexserver.model.CallTaskStatDto;
import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.model.StatisticeModelDto;
import com.pl.indexserver.model.StatisticsExportDto;
import com.pl.indexserver.query.Page;
import com.pl.indexserver.query.TCustmIntentionQuery;
import com.pl.indexserver.service.CallTaskStatService;
import com.pl.indexserver.service.TCustmIntentionService;
import com.pl.indexserver.untils.CommonConstant;
import com.pl.indexserver.untils.GetUid;
import com.pl.indexserver.untils.RedisClient;
import com.pl.model.TmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/busiManagement")
public class StatisticsContorller {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsContorller.class);

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private CallTaskStatService callTaskStatService;
    @Autowired
    private TCustmIntentionService tCustmIntentionService;


    /**
     * @api {GET} /busiManagement/statistics/basicStat/query   查询基础统计信息
     * @apiName queryBasicStat
     * @apiGroup StatisticsContorller
     * @apiParam {String} beginDate 起始时间 yyyy-mm-dd
     * @apiParam {String} endDate 终止时间 yyyy-mm-dd
     * @apiSuccess {String} code 0:获取成功 其他:获取失败,对应的错误码
     * @apiSuccess {String} content  基础统计信息
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": {
     * "2": 503457,
     * "18": 854800,
     * "20": 854800,
     * "23": 83752512,
     * "25": 869200,
     * "26": 8548048,
     * "27": 86912,
     * "28": 86912,
     * "40": 1620209,
     * "41": 1116752
     * "42": 1116752
     * "43": 1116752
     * },
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @GetMapping(value = "/statistics/basicStat/query")
    public ReturnMsg queryBasicStat(HttpServletRequest request,
                                    @RequestParam(value = "beginDate") String beginDate,
                                    @RequestParam(value = "endDate") String endDate,
                                    @RequestParam(value = "type", required = false, defaultValue = "all") String type
    ) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            Map<String, Long> map = new HashMap<String, Long>();
            TmUser user = GetUid.getUID(request, redisClient);
            Long companyId = user.getCompanyId();
            beginDate = beginDate.replace("\"", "");
            endDate = endDate.replace("\"", "");
            endDate = endDate + " 23:59:59";
            CallTaskStatDto callTaskStatDto = callTaskStatService.queryBasicStat(beginDate, endDate, companyId.toString(), type);
            if (null == callTaskStatDto) {
                callTaskStatDto = new CallTaskStatDto();
            }
            //未接通
            Long num41 = callTaskStatDto.getItem13Num() + callTaskStatDto.getItem15Num() + callTaskStatDto.getItem17Num() + callTaskStatDto.getItem18Num();
            map.put("41", num41);//未接通
            //外呼总量
            Long num40 = callTaskStatDto.getItem2Num() + num41;
            map.put("40", num40);//外呼总量
            //C级以上
            Long num42 = 0L;
            //已接通（D+无意向）
            Long num43 = 0L;
            //无意向
            Long num23 = 0L;
            //意向等级A
            Long num25 = 0L;
            //意向等级B
            Long num26 = 0L;
            //意向等级C
            Long num27 = 0L;
            //意向等级D
            Long num28 = 0L;
            //接通并成功数量
            Long num45 = callTaskStatDto.getSuccessNum();

            if ("intention".equals(type)) {
                num42 = callTaskStatDto.getItem20Num();
                num43 = callTaskStatDto.getItem23Num() + callTaskStatDto.getItem28Num();
                num23 = callTaskStatDto.getItem23Num();
                num25 = callTaskStatDto.getItem25Num();
                num26 = callTaskStatDto.getItem26Num();
                num27 = callTaskStatDto.getItem27Num();
                num28 = callTaskStatDto.getItem28Num();
            } else if ("focus".equals(type)) {
                num42 = callTaskStatDto.getItem37Num();
                num43 = callTaskStatDto.getItem38Num() + callTaskStatDto.getItem36Num();
                num23 = callTaskStatDto.getItem38Num();
                num25 = callTaskStatDto.getItem29Num();
                num26 = callTaskStatDto.getItem30Num();
                num27 = callTaskStatDto.getItem31Num();
                num28 = callTaskStatDto.getItem32Num();
            } else if ("intent".equals(type)) {
                num42 = callTaskStatDto.getItem39Num();
                num43 = callTaskStatDto.getItem37Num() + callTaskStatDto.getItem40Num();
                num23 = callTaskStatDto.getItem40Num();
                num25 = callTaskStatDto.getItem33Num();
                num26 = callTaskStatDto.getItem34Num();
                num27 = callTaskStatDto.getItem35Num();
                num28 = callTaskStatDto.getItem36Num();
            } else if ("all".equals(type)) {
                num42 = callTaskStatDto.getItem41Num();
                num43 = callTaskStatDto.getItem42Num() + callTaskStatDto.getItem46Num();
                num23 = callTaskStatDto.getItem42Num();
                num25 = callTaskStatDto.getItem43Num();
                num26 = callTaskStatDto.getItem44Num();
                num27 = callTaskStatDto.getItem45Num();
                num28 = callTaskStatDto.getItem46Num();
            }
            //接通未成功数量
            Long num46 = num43 - num45;

            map.put("42", num42);//C级以上意向
            map.put("43", num43);//已接通 d+无
            //主叫失败
            Long num18 = callTaskStatDto.getItem18Num();
            map.put("18", num18);//主叫失败
            //接听总量
            Long num2 = callTaskStatDto.getItem2Num();
            map.put("2", num2);//接听量
            //有意向
            //Long num20 = callTaskStatDto.getItem20Num();
            map.put("20", num42);//有意向
            map.put("23", num23);//无意向
            map.put("25", num25);//意向等级A
            map.put("26", num26);//意向等级B
            map.put("27", num27);//意向等级C
            map.put("28", num28);//意向等级D
            map.put("45", num45);//已接通且成功数
            map.put("46", num46);//已接通未成功数
            map.put("isSpecial", 1046 == companyId ? 1 : 0L);//页面控制开关
            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            returnMsg.setContent(map);
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            e.printStackTrace();
        }
        return returnMsg;
    }

    /**
     * @api {GET} /busiManagement/statistics/taskStat/query   查询任务统计信息
     * @apiName queryTaskStat
     * @apiGroup StatisticsContorller
     * @apiParam {String} beginDate 起始时间 yyyy-mm-dd
     * @apiParam {String} endDate 终止时间 yyyy-mm-dd
     * @apiSuccess {String} code 0:获取成功 其他:获取失败,对应的错误码
     * @apiSuccess {String} content  任务统计信息
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": [
     * {
     * "taskId": 0,
     * "taskName": "删选总量",
     * "countAccept": 632165,
     * "countIntention": 632165
     * },
     * {
     * "taskId": 21,
     * "taskName": "任务1",
     * "countAccept": 32165,
     * "countIntention": 632165
     * },
     * {
     * "taskId": 32,
     * "taskName": "任务2",
     * "countAccept": 32165,
     * "countIntention": 632165
     * }
     * ],
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @GetMapping(value = "/statistics/taskStat/query")
    public ReturnMsg queryTaskStat(HttpServletRequest request,
                                   @RequestParam(value = "beginDate") String beginDate,
                                   @RequestParam(value = "endDate") String endDate,
                                   @RequestParam(value = "type", defaultValue = "all", required = false) String type
    ) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            List<Map<String, Object>> resultList = new ArrayList<>();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("taskId", "");
            map.put("taskName", "筛选总量");
            TmUser user = GetUid.getUID(request, redisClient);
            Long companyId = user.getCompanyId();
            beginDate = beginDate.replace("\"", "");
            endDate = endDate.replace("\"", "");
            endDate = endDate + " 23:59:59";
            List<CallTaskStatDto> callTaskStatDtos = callTaskStatService.queryTaskStat(beginDate, endDate, companyId.toString(), type);
            Long item2Gross = 0L;
            Long item20Gross = 0L;
            for (int i = 0; i < callTaskStatDtos.size(); i++) {
                Map<String, Object> temp = new HashMap<String, Object>();
                CallTaskStatDto callTaskStatDto = callTaskStatDtos.get(i);
                item2Gross += callTaskStatDto.getItem2Num();
                item20Gross += callTaskStatDto.getItem20Num();
                temp.put("taskId", callTaskStatDto.getTaskId());
                temp.put("taskName", callTaskStatDto.getTaskName());
                temp.put("countIntention", callTaskStatDto.getItem2Num());
                temp.put("countAccept", callTaskStatDto.getItem20Num());
                resultList.add(temp);
            }
            map.put("countIntention", item2Gross);
            map.put("countAccept", item20Gross);
            resultList.add(map);
            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            returnMsg.setContent(resultList);
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            e.printStackTrace();
        }
        return returnMsg;
    }

    /**
     * @api {GET} /busiManagement/statistics/task/queryAll   查询所有任务
     * @apiName queryAllTask
     * @apiGroup StatisticsContorller
     * @apiSuccess {String} code 0:获取成功 其他:获取失败,对应的错误码
     * @apiSuccess {String} content  任务信息
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": [
     * {
     * "taskId": 20,
     * "taskName": "任务1",
     * },
     * {
     * "taskId": 21,
     * "taskName": "任务2"
     * },
     * {
     * "taskId": 32,
     * "taskName": "任务3"
     * }
     * ],
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @GetMapping(value = "/statistics/task/queryAll")
    public ReturnMsg queryAllTask(HttpServletRequest request) {
        TmUser user = GetUid.getUID(request, redisClient);
        ReturnMsg returnMsg = new ReturnMsg();

        List<StatisticeModelDto> dtoList = new ArrayList<StatisticeModelDto>();

        dtoList = callTaskStatService.selectTaskByCompanyId(user.getCompanyId());
        returnMsg.setCode(0);

        returnMsg.setContent(dtoList);

        return returnMsg;
    }

    /**
     * @api {GET} /busiManagement/statistics/intentionCustomer/query   查询意向客户
     * @apiName queryIntentionCustomer
     * @apiGroup StatisticsContorller
     * @apiParam {Long} taskId 任务ID,0表示所有
     * @apiParam {String} intention 意向等级,0表示所有 ,A-D
     * @apiParam {String} beginDate 起始时间 yyyy-mm-dd
     * @apiParam {String} endDate 终止时间 yyyy-mm-dd
     * @apiParam {Int} pageIndex 第几页，从1开始
     * @apiParam {Int} recordNum 每页的记录数
     * @apiParam {String} telephone 联系号码
     * @apiSuccess {String} code 0:获取成功 其他:获取失败,对应的错误码
     * @apiSuccess {String} content  日志信息
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": {
     * "totalNum":360,
     * "recordList":[
     * {
     * "dialogId": 1,
     * "taskId": 20,
     * "taskName": "任务1",
     * "customerName": "顾客名称1",
     * "telephone": "86110011",
     * "status": "有意向",
     * "callTime": "2018-05-07 16:01",
     * "duration": "60",
     * "outCallNumber": "18888888888",
     * "intention": "A级"
     * },
     * {
     * "dialogId": 2,
     * "taskId": 20,
     * "taskName": "任务1",
     * "customerName": "顾客名称2",
     * "telephone": "86110012",
     * "status": "有意向",
     * "callTime": "2018-05-07 16:01",
     * "duration": "60",
     * "outCallNumber": "18888888888",
     * "intention": "A级"
     * },
     * {
     * "dialogId": 3,
     * "taskId": 20,
     * "taskName": "任务1",
     * "customerName": "顾客名称3",
     * "telephone": "86110013",
     * "status": "有意向",
     * "callTime": "2018-05-07 16:01",
     * "duration": "60",
     * "outCallNumber": "18888888888",
     * "intention": "A级"
     * }
     * ]
     * },
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @GetMapping(value = "/statistics/intentionCustomer/query")
    public ReturnMsg queryIntentionCustomer(HttpServletRequest request,
                                            @RequestParam(value = "taskId", required = false) Long taskId,
                                            @RequestParam(value = "intention", required = false) String intention,
                                            @RequestParam(value = "beginDate", required = false) String beginDate,
                                            @RequestParam(value = "endDate", required = false) String endDate,
                                            @RequestParam(value = "pageIndex") int pageIndex,
                                            @RequestParam(value = "pageSize") int pageSize,
                                            @RequestParam(value = "type", required = false, defaultValue = "all") String type,
                                            @RequestParam(value = "telephone", required = false) String telephone) {
        TmUser user = GetUid.getUID(request, redisClient);
        if (!StringUtils.isEmpty(user)) {
            TCustmIntentionQuery tCustmIntentionQuery = new TCustmIntentionQuery();
            tCustmIntentionQuery.setPageIndex(pageIndex);
            tCustmIntentionQuery.setPageNum(pageSize);
            tCustmIntentionQuery.setCompanyId(user.getCompanyId());
            tCustmIntentionQuery.setType(type);
            tCustmIntentionQuery.setTelephone(telephone);
//            if (!StringUtils.isEmpty(taskId) && taskId != 0) {
//                tCustmIntentionQuery.setTaskId(taskId);
//            }
            if (!StringUtils.isEmpty(intention)) {
                tCustmIntentionQuery.setIntention(intention);
            }
            if (!StringUtils.isEmpty(beginDate)) {
                tCustmIntentionQuery.setBeginDate(beginDate + " 00:00:00");
            }
            if (!StringUtils.isEmpty(endDate)) {
                tCustmIntentionQuery.setEndDate(endDate + " 23:59:59");
            }
            Page<StatisticeModelDto> dtoList = callTaskStatService.selectMapStatisticeForIntention(tCustmIntentionQuery);
            if (dtoList != null) {
                return new ReturnMsg(0, dtoList, "", "");
            } else {
                return new ReturnMsg(0, new Page<StatisticeModelDto>(), "", "查询数据结果为空！");
            }
        }
        return new ReturnMsg(-1, new Page<StatisticeModelDto>(), "", "未知错误");
    }

    /**
     * @api {GET} /busiManagement/statistics/intentionCustomer/export   导出意向客户
     * @apiName exportIntentionCustomer
     * @apiGroup StatisticsContorller
     * @apiParam {Long} taskId 任务ID,0表示所有
     * @apiParam {String} intention 意向等级,0表示所有 ,A-D
     * @apiParam {String} beginDate 起始时间 yyyy-mm-dd
     * @apiParam {String} endDate 终止时间 yyyy-mm-dd
     * @apiSuccess {String} code 0:获取成功 其他:获取失败,对应的错误码
     * @apiSuccess {String} content  任务信息
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": [
     * ],
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @GetMapping(value = "/statistics/intentionCustomer/export")
    public ReturnMsg exportIntentionCustomer(HttpServletRequest request, HttpServletResponse response,
                                             @RequestParam(value = "taskId", required = false) Long taskId,
                                             @RequestParam(value = "intention", required = false) String intention,
                                             @RequestParam(value = "beginDate", required = false) String beginDate,
                                             @RequestParam(value = "endDate", required = false) String endDate,
                                             @RequestParam(value = "type", required = false, defaultValue = "all") String type,
                                             @RequestParam(value = "telephone", required = false) String telephone) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            TmUser user = GetUid.getUID(request, redisClient);
            TCustmIntentionQuery tCustmIntentionQuery = new TCustmIntentionQuery();
            tCustmIntentionQuery.setCompanyId(user.getCompanyId());
            tCustmIntentionQuery.setType(type);
            tCustmIntentionQuery.setTelephone(telephone);
            if (!StringUtils.isEmpty(taskId) && taskId != 0) {
                tCustmIntentionQuery.setTaskId(taskId);
            }
            if (!StringUtils.isEmpty(intention)) {
                tCustmIntentionQuery.setIntention(intention);
            }
            if (!StringUtils.isEmpty(beginDate)) {
                tCustmIntentionQuery.setBeginDate(beginDate + " 00:00:00");
            }
            if (!StringUtils.isEmpty(endDate)) {
                tCustmIntentionQuery.setEndDate(endDate + " 23:59:59");
            }
//        List<StatisticsExportDto> dtoList = new ArrayList<>();
            callTaskStatService.exportStatisticeForIntention(tCustmIntentionQuery, response);
            returnMsg.setCode(0);
        } catch (Exception e) {
            logger.error("导出报表(呼出记录)出现异常:  ",e);
        }
        return returnMsg;
    }

}
