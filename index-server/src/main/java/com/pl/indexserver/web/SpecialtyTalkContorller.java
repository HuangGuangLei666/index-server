package com.pl.indexserver.web;


import com.pl.indexserver.model.CallTaskStatDto;
import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.query.Page;
import com.pl.indexserver.service.CallTaskStatService;
import com.pl.indexserver.service.SpeechcraftService;
import com.pl.indexserver.service.TBusinessService;
import com.pl.indexserver.service.UserOperateRecordService;
import com.pl.indexserver.untils.CommonConstant;
import com.pl.indexserver.untils.DateUtils;
import com.pl.indexserver.untils.GetUid;
import com.pl.indexserver.untils.RedisClient;
import com.pl.model.TBusiness;
import com.pl.model.TmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/busiManagement")
public class SpecialtyTalkContorller {

    private static final Logger logger = LoggerFactory.getLogger(SpecialtyTalkContorller.class);
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    final public static String PATTERN_DATE_1 = "yyyy-MM-dd";
    final public static String PATTERN_DATE_2 = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    private SpeechcraftService speechcraftService;

    @Value("${recordings.address}")
    private String filePath;

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private UserOperateRecordService userOperateRecordService;
    @Autowired
    private CallTaskStatService callTaskStatService;
    @Autowired
    private TBusinessService tBusinessService;


    /**
     * @api {GET} /busiManagement/flowConfig/knowledgeDB/queryBasic 基础信息-单个知识库基本信息统计;
     * @apiName queryBasicKnowledgeDB
     * @apiGroup 智能配置-单个知识库
     * @apiParam{Long} id 响应方式的id值
     * @apiSuccess {String} code 0:删除成功 1:删除失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": {
     * "1": 632165,
     * "2": 32165,
     * "3": 62165,
     * "4": 2165
     * "name": "智库标题"
     * "createDate": "创建时间"
     * },
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @RequestMapping(value = "/flowConfig/knowledgeDB/queryBasic")
    public ReturnMsg queryBasicKnowledgeDB(HttpServletRequest request,
                                           @RequestParam("id") Long id) {

        ReturnMsg returnMsg = new ReturnMsg();
        try {

            TBusiness tBusiness = tBusinessService.selectByPrimaryKey(id);
            int int1 = speechcraftService.selectSpecialtyTalkCount(id);
            int int2 = speechcraftService.selectSpecialtyTalkCountByIsRecord(id);
            int int3 = speechcraftService.selectTDialogCountByIStatus(id);
            int int4 = speechcraftService.selectTDialogCountByIsIntention(id);

            Map<String, Object> map = new HashMap<>();
            map.put("1", int1);
            map.put("2", int2);
            map.put("3", int3);
            map.put("4", int4);
            map.put("name", tBusiness.getName());
            map.put("createDate", DateUtils.getStringForDate(tBusiness.getCreateDate(), DateUtils.DATEFORMAT_1));
            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            returnMsg.setContent(map);
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            returnMsg.setErrorMsg("请求异常！");
            logger.error("查询知识库基本信息出错；  ", e);
        }
        return returnMsg;
    }




    /**
     * @api {GET} /busiManagement/flowConfig/userLog/queryAll  基础信息-日志信息查询;
     * @apiName queryAllUserLog
     * @apiGroup 智能配置-单个知识库
     * @apiParam{Long} businessId 业务标识
     * @apiSuccess {String} code 0:成功 其他:错误码
     * @apiSuccess {String} content  返回专用话术列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": [
     * {
     * "id": 0,
     * "uid": "abacdk",
     * "userName": "张三",
     * "objectType": 2,
     * "object": 2,
     * "operateName": "添加知识库",
     * "operateTime": "2018-05-09 14:20",
     * "remark": "abcddddd"
     * }
     * ],
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @GetMapping(value = "/flowConfig/userLog/queryAll")
    public ReturnMsg queryAllUserLog(HttpServletRequest request, long businessId,
                                     @RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            TmUser user = GetUid.getUID(request, redisClient);
            Page page = userOperateRecordService.queryUserOperateRecordByPage(user.getUserid() ,CommonConstant.FLOWCONFIG,String.valueOf(businessId), pageNum, pageSize);
            returnMsg.setCode(0);
            returnMsg.setContent(page);
            returnMsg.setErrorMsg("ok.");
        } catch (Exception e) {
            logger.error("查询智库日志失败:", e);
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
        }

        return returnMsg;

    }



    /**
     * @api {GET} /busiManagement/flowConfig/statistics/query 基础信息-查询统计信息;
     * @apiName queryStatistics
     * @apiGroup 智能配置-单个知识库
     * @apiParam{Long} businessId  业务标识
     * @apiSuccess {String} code 0:成功 其他:错误码
     * @apiSuccess {String} content  返回统计列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": {
     * "num":2,
     * "timeData":["时间点","2009/6/12 2:10"],
     * "acceptData":[接听量,200],
     * "intentionData":[意向客户量,80],
     * },
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @GetMapping(value = "/flowConfig/statistics/query")
    public ReturnMsg queryStatistics(HttpServletRequest request, @RequestParam("businessId") Long businessId) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            TmUser user = GetUid.getUID(request, redisClient);
            Long companyId = user.getCompanyId();
            List<CallTaskStatDto> callTaskStatDtos = callTaskStatService.queryStatistics(companyId, businessId);
            Map<String, Object> map = new HashMap<>();
            if (null != callTaskStatDtos) {
                List<String> timeData = new ArrayList<>();
                List<Long> acceptData = new ArrayList<>();
                List<Long> intentionData = new ArrayList<>();
                for (int i = 0; i < callTaskStatDtos.size(); i++) {
                    CallTaskStatDto callTaskStatDto = callTaskStatDtos.get(i);
                    String time = DateUtils.getStringForDateString(callTaskStatDto.getBeginTime(), DateUtils.DATEFORMAT_1, DateUtils.DATEFORMAT_5);
                    Long accept = callTaskStatDto.getItem2Num();
                    Long intention = callTaskStatDto.getItem25Num() + callTaskStatDto.getItem26Num() + callTaskStatDto.getItem27Num();
                    timeData.add(time);
                    acceptData.add(accept);
                    intentionData.add(intention);
                }
                map.put("timeData", timeData);
                map.put("acceptData", acceptData);
                map.put("intentionData", intentionData);
            }
            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            returnMsg.setContent(map);
        } catch (Exception e) {
            logger.error("统计信息查询异常:", e);
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
        }
        return returnMsg;

    }

}
