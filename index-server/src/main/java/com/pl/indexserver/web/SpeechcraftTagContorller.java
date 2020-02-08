package com.pl.indexserver.web;

import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.model.SpeechcraftTagDto;
import com.pl.indexserver.service.SpeechcraftTagService;
import com.pl.indexserver.untils.CommonConstant;
import com.pl.indexserver.untils.GetUid;
import com.pl.indexserver.untils.RedisClient;
import com.pl.model.TmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/busiManagement/flowConfig")
public class SpeechcraftTagContorller {

    private static final Logger logger = LoggerFactory.getLogger(SpeechcraftTagContorller.class);

    @Autowired
    private SpeechcraftTagService speechcraftTagService;
    @Autowired
    private RedisClient redisClient;

    /**
     * @api {GET} /busiManagement/flowConfig/speechcraftTag/list  获取话术标签列表
     * @apiName list
     * @apiGroup SpeechcraftTagContorller
     * @apiParam {String} type 标签类型{TTS:TTS语音合成,REAL:真人录音,PRE:预处理整段录音}
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content 返回任务列表
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json} 成功返回示例
     * {
     *   "code": 0,
     *   "content": {
     *     "ttsTaglist": [
     *       {
     *         "id": 3,
     *         "tagName": "贷款金额",
     *         "tagKey": "amount"
     *       }
     *     ],
     *     "sysTaglist": [
     *       {
     *         "id": 1,
     *         "tagName": "敬畏称呼",
     *         "tagKey": "ctname"
     *       }
     *     ]
     *   },
     *   "token": null,
     *   "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @RequestMapping("/speechcraftTag/list")
    public ReturnMsg list(HttpServletRequest request) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            TmUser user = GetUid.getUID(request, redisClient);
            Map<String, Object> result = new HashMap<>();
            List<SpeechcraftTagDto> sysTaglist = speechcraftTagService.getSpeechcraftTagDtoList(user.getCompanyId(), "REAL");
            List<SpeechcraftTagDto> ttsTaglist = speechcraftTagService.getSpeechcraftTagDtoList(user.getCompanyId(), "TTS");
            result.put("sysTaglist", sysTaglist);
            result.put("ttsTaglist", ttsTaglist);
            returnMsg.setCode(CommonConstant.RETURN_SUCCEED);
            returnMsg.setContent(result);
        } catch (Exception e) {
            returnMsg.setCode(CommonConstant.RETURN_DEFEATED_1);
            logger.error("获取话术标签列表异常:", e);
        }
        return returnMsg;
    }

}
