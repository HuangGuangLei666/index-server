package com.pl.indexserver.web;

import com.pl.indexserver.model.CallTaskReturnDto;
import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.query.Page;
import com.pl.indexserver.service.UserOperateRecordService;
import com.pl.indexserver.untils.GetUid;
import com.pl.indexserver.untils.RedisClient;
import com.pl.model.TmUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/busiManagement")
public class LoggerContorller {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private UserOperateRecordService userOperateRecordService;


    /**
     * @api {GET} /busiManagement/callTask/userLog/queryAll   查询任务日志信息
     * @apiName queryAll
     * @apiGroup LoggerContorller
     * @apiParam {Long} callTaskId 任务ID
     * @apiSuccess {String} code 0:获取成功 1:获取失败
     * @apiSuccess {String} content  日志信息
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": [
     * {
     * "id": 1,
     * "userId": "123456",
     * "companyId": 12,
     * "objectType": 1,
     * "object": "任务",
     * "operateId": "1",
     * "operateName": "创建任务",
     * "remark": "测试用户",
     * "createDate": 1524734219000
     * },
     * {
     * "id": 2,
     * "userId": "123456",
     * "companyId": 12,
     * "objectType": 2,
     * "object": "ew",
     * "operateId": "1",
     * "operateName": "删除任务",
     * "remark": "12",
     * "createDate": 1524739613000
     * }
     * ],
     * "token": null,
     * "errorMsg": null
     * }
     * @apiVersion 1.0.0
     */
    @GetMapping(value = "/callTask/userLog/queryAll")
    public ReturnMsg queryAll(HttpServletRequest request,
                              @RequestParam(value = "callTaskId") Long callTaskId,
                              @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                              @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        TmUser user = GetUid.getUID(request, redisClient);
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            returnMsg.setCode(0);
            CallTaskReturnDto callTask = new CallTaskReturnDto();
            callTask.setUid(user.getUserid());
            callTask.setCompanyId(user.getCompanyId());
            callTask.setTaskName(String.valueOf(callTaskId));
            callTask.setPageIndex(pageNum);
            callTask.setPageNum(pageSize);
            Page userOperateRecordList = userOperateRecordService.getLogger(callTask,1);
            returnMsg.setContent(userOperateRecordList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnMsg;
    }




}
