package com.pl.indexserver.web;

import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.model.TCustmIntentionDto;
import com.pl.indexserver.query.Page;
import com.pl.indexserver.query.TCustmIntentionQuery;
import com.pl.indexserver.query.TUnknownRecordQuery;
import com.pl.indexserver.service.TCustmIntentionService;
import com.pl.indexserver.service.UnknownRecordService;
import com.pl.indexserver.untils.CommonConstant;
import com.pl.indexserver.untils.GetUid;
import com.pl.indexserver.untils.RedisClient;
import com.pl.model.TUnknownRecord;
import com.pl.model.TmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/busiManagement/flowConfig/unknownRecord")
public class TUnknownRecordContorller {

    private static final Logger logger = LoggerFactory.getLogger(TUnknownRecordContorller.class);

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private UnknownRecordService unknownRecordService;
    @Autowired
    private TCustmIntentionService tCustmIntentionService;

    /**
     * @api {GET} /busiManagement/flowConfig/unknownRecord/query 获取未识别语句列表
     * @apiName query
     * @apiGroup TUnknownRecordContorller
     * @apiParam {Integer} pageIndex 页码
     * @apiParam {Integer} pageNum 显示条数
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": [
     * {
     * total;10',
     * records:[
     * {
     * content:"文本内容",
     * frequency:频率,
     * businessId:智库id,
     * businessName:智库名称
     * }
     * ]
     * }
     * ],
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @RequestMapping("/query")
    public ReturnMsg query(HttpServletRequest request, TUnknownRecordQuery query) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            TmUser user = GetUid.getUID(request, redisClient);
            Long companyId = user.getCompanyId();
            query.setCompanyId(companyId);
            Page<TUnknownRecord> tUnknownRecordPage = unknownRecordService.selectByQuery(query);
            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            returnMsg.setContent(tUnknownRecordPage);
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            logger.error("获取未识别语句列表异常:", e);
        }
        return returnMsg;
    }

    /**
     * @api {GET} /busiManagement/flowConfig/unknownRecord/queryDetail 获取当前未识别语句详情
     * @apiName queryDetail
     * @apiGroup TUnknownRecordContorller
     * @apiParam {Integer} pageIndex 页码
     * @apiParam {Integer} pageNum 显示条数
     * @apiParam {String} content 文本内容
     * @apiParam {Long} businessId 智库id
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": [
     *  {
     *      id:主键id;
     *      custmName:"客户姓名";
     *      telephone:"客户号码";
     *      outNumber:"主叫号码";
     *      beginDate:"通话时间";
     *      totalSeconds:通话时长;
     *      intentionLevel:意向等级;
     *      taskName:"任务名称";
     *  }
     * ],
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @RequestMapping("/queryDetail")
    public ReturnMsg queryDetail(TCustmIntentionQuery tCustmIntentionQuery) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            Page<TCustmIntentionDto> tCustmIntentionDtoPage = tCustmIntentionService.selectByBusinessIdAndContent(tCustmIntentionQuery);
            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            returnMsg.setContent(tCustmIntentionDtoPage);
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            returnMsg.setErrorMsg("请求异常！");
            logger.error("获取未识别语句详情异常:", e);
        }
        return returnMsg;
    }
    /**
     * @api {POST} /busiManagement/flowConfig/unknownRecord/neglect 忽略当前未识别语句组
     * @apiName neglect
     * @apiGroup TUnknownRecordContorller
     * @apiParam {Long} businessId 智库id
     * @apiParam {String} content 未识别语句
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  返回任务列表
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
    @RequestMapping("/neglect")
    public ReturnMsg neglect(HttpServletRequest request, Long businessId,String content) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            int i = unknownRecordService.neglectByBusinessIdAndContent(businessId, content);
            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            returnMsg.setContent(i>0?"忽略成功":"忽略失败");
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            returnMsg.setErrorMsg("请求异常！");
            logger.error("获取未识别语句详情异常:", e);
        }
        return returnMsg;
    }

}
